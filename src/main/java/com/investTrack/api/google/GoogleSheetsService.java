package com.investTrack.api.google;

import static java.util.Collections.emptyList;

import com.investTrack.model.Investment;
import com.investTrack.model.InvestmentEntry;
import com.investTrack.model.adapter.InvestmentAdapter;
import com.investTrack.model.adapter.InvestmentEntryAdapter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/*
 * Provides access to Google Sheets API for investment data, orchestrates and adapts client operations
 */

@Slf4j
@RequiredArgsConstructor
public class GoogleSheetsService {
  private final String spreadSheetId;

  // Map to store the sheet names and their corresponding IDs, so we are able to clean up them based
  // on the sheet names that we haven't modified when writing investment data
  private Map<String, Integer> sheetsByName;

  private final String READ_SHEET_RANGE = "A2:P";
  private final String WRITE_SHEET_RANGE = "A1:P";

  private final String INVESTMENTS_LIST_SHEET_NAME = "Investments List";
  private final List<Object> INVESTMENTS_LIST_HEADERS =
      List.of(
          "Investment ID",
          "Name",
          "Description",
          "Currency",
          "Start Date",
          "End Date",
          "Reinvested",
          "Initial Invested Amount",
          "Reinvested Amount",
          "Profitability");

  private final String INVESTMENT_SHEET_NAME_PATTERN = "Investment entries - ";
  private final List<Object> INVESTMENT_ENTRIES_HEADERS =
      List.of("Date", "Initial Invested Amount", "Reinvested Amount", "Profitability", "Comments");

  private final GoogleSheetsClient client;
  private final GoogleSheetsInvestmentAdapter googleSheetsInvestmentAdapter;
  private final GoogleSheetsInvestmentEntryAdapter googleSheetsInvestmentEntryAdapter;
  private final InvestmentAdapter investmentAdapter;
  private final InvestmentEntryAdapter investmentEntryAdapter;

  public synchronized List<Investment> readInvestmentsData() throws IOException {
    log.info("Started reading investments data from Google Sheets");
    sheetsByName = client.getSheets(spreadSheetId);
    var existSheet = existSheet(INVESTMENTS_LIST_SHEET_NAME);
    if (!existSheet) {
      client.createSheet(spreadSheetId, INVESTMENTS_LIST_SHEET_NAME);
      return emptyList();
    }

    var rows = client.readSheet(spreadSheetId, INVESTMENTS_LIST_SHEET_NAME, READ_SHEET_RANGE);
    if (rows == null) {
      log.debug("Investment data not found");
      return emptyList();
    }

    var investments = readInvestmentsListFromRows(rows);
    log.debug("Finished reading investments data");
    return investments;
  }

  private List<Investment> readInvestmentsListFromRows(List<List<Object>> rows) throws IOException {
    var investments = new ArrayList<Investment>();
    log.info("Found {} investment rows in sheet \"{}\"", rows.size(), INVESTMENTS_LIST_SHEET_NAME);
    for (var row : rows) {
      var investment = investmentAdapter.fromSheetValueRange(row);
      if (investment == null) {
        log.debug("Actual investment object is null, we assume we have no more investments");
        break;
      }

      var investmentEntries = readInvestmentEntries(investment);
      investment.setEntries(investmentEntries);

      investments.add(investment);
    }
    return investments;
  }

  private List<InvestmentEntry> readInvestmentEntries(Investment investment) throws IOException {
    var sheetName = INVESTMENT_SHEET_NAME_PATTERN + investment.getName();
    var existSheet = existSheet(sheetName);
    if (!existSheet) {
      client.createSheet(spreadSheetId, sheetName);
      return emptyList();
    }

    var rows = client.readSheet(spreadSheetId, sheetName, READ_SHEET_RANGE);
    if (rows == null) {
      log.debug("Investment entries data not found for sheet name \"{}\"", sheetName);
      return emptyList();
    }

    var investmentEntries = new ArrayList<InvestmentEntry>();
    for (var row : rows) {
      var investmentEntry = investmentEntryAdapter.fromSheetValueRange(row, investment);
      investmentEntries.add(investmentEntry);
    }
    return investmentEntries;
  }

  public synchronized void writeInvestmentsData(List<Investment> investments) throws IOException {
    log.info("Started writing investments data to Google Sheets");

    // We want to store all the sheet names that are not written to, so we can clean them up later
    sheetsByName = client.getSheets(spreadSheetId);
    var nonWrittenSheets = new HashMap<>(Map.copyOf(sheetsByName));

    writeInvestmentsList(investments, nonWrittenSheets);

    writeInvestmentEntries(investments, nonWrittenSheets);

    if (!nonWrittenSheets.isEmpty()) {
      cleanUpSheets(nonWrittenSheets);
    }
  }

  private void writeInvestmentsList(
      List<Investment> investments, HashMap<String, Integer> nonWrittenSheets) throws IOException {
    var investmentValues = new ArrayList<List<Object>>();
    investmentValues.add(INVESTMENTS_LIST_HEADERS);

    for (var investment : investments) {
      var row = googleSheetsInvestmentAdapter.toSheetValueRange(investment);
      investmentValues.add(row);
    }

    client.writeToSheet(
        spreadSheetId, INVESTMENTS_LIST_SHEET_NAME, WRITE_SHEET_RANGE, investmentValues);
    nonWrittenSheets.remove(INVESTMENTS_LIST_SHEET_NAME);
  }

  private void writeInvestmentEntries(
      List<Investment> investments, HashMap<String, Integer> nonWrittenSheets) throws IOException {
    for (var investment : investments) {
      var investmentEntriesSheetName = INVESTMENT_SHEET_NAME_PATTERN + investment.getName();
      if (!existSheet(investmentEntriesSheetName)) {
        client.createSheet(spreadSheetId, investmentEntriesSheetName);
      }
      nonWrittenSheets.remove(investmentEntriesSheetName);

      var entries = investment.getEntries();
      if (entries == null || entries.isEmpty()) {
        log.info(
            "No investment entries found for investment {}, cleaning up sheet",
            investment.getName());
        client.clearSheet(spreadSheetId, investmentEntriesSheetName, READ_SHEET_RANGE);
        continue;
      }

      writeInvestmentEntriesData(entries, investmentEntriesSheetName);
    }
  }

  private void writeInvestmentEntriesData(List<InvestmentEntry> entries, String sheetName)
      throws IOException {
    var values = new ArrayList<List<Object>>();
    values.add(INVESTMENT_ENTRIES_HEADERS);

    for (var entry : entries) {
      var row = googleSheetsInvestmentEntryAdapter.toSheetValueRange(entry);
      values.add(row);
    }

    client.writeToSheet(spreadSheetId, sheetName, WRITE_SHEET_RANGE, values);
  }

  private void cleanUpSheets(HashMap<String, Integer> nonWrittenSheets) throws IOException {
    log.info("Deprecated sheets found, cleaning them up: {}", nonWrittenSheets);
    client.deleteSheets(spreadSheetId, nonWrittenSheets.values().stream().toList());
  }

  private boolean existSheet(String sheetName) {
    if (sheetsByName == null) {
      log.error("Sheet names are not loaded");
      throw new IllegalStateException("Sheet names are not loaded");
    }
    return sheetsByName.containsKey(sheetName);
  }
}
