package com.investTrack.api.google;

import static java.util.Collections.emptyList;

import com.investTrack.model.Investment;
import com.investTrack.model.InvestmentEntry;
import com.investTrack.model.adapter.InvestmentAdapter;
import com.investTrack.model.adapter.InvestmentEntryAdapter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/*
 * Provides access to Google Sheets API for investment data,  orchestrates and adapts client operations
 */

@Slf4j
@RequiredArgsConstructor
public class GoogleSheetsService {
  private final String spreadSheetId;
  private static final String INVESTMENTS_LIST_SHEET_NAME = "Investments List";
  private static final String SHEET_RANGE = "A2:P";
  private static final String INVESTMENT_SHEET_NAME_PATTERN = "Investment entries - ";

  private final GoogleSheetsClient client;
  private final GoogleSheetsInvestmentAdapter googleSheetsInvestmentAdapter;
  private final GoogleSheetsInvestmentEntryAdapter googleSheetsInvestmentEntryAdapter;
  private final InvestmentAdapter investmentAdapter;
  private final InvestmentEntryAdapter investmentEntryAdapter;

  public List<Investment> readInvestmentsData() throws IOException {
    var existSheet = client.existSheet(spreadSheetId, INVESTMENTS_LIST_SHEET_NAME);
    if (!existSheet) {
      log.info(
          "Investments list sheet \"{}\" does not exist, creating it", INVESTMENTS_LIST_SHEET_NAME);
      client.createSheet(spreadSheetId, INVESTMENTS_LIST_SHEET_NAME);
      return emptyList();
    }

    var rows = client.readSheet(spreadSheetId, INVESTMENTS_LIST_SHEET_NAME, SHEET_RANGE);
    if (rows == null) {
      log.debug("Investment data not found");
      return emptyList();
    }

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
    log.debug("Finished reading investments data");
    return investments;
  }

  private List<InvestmentEntry> readInvestmentEntries(Investment investment) throws IOException {
    var sheetName = INVESTMENT_SHEET_NAME_PATTERN + investment.getName();
    var existSheet = client.existSheet(spreadSheetId, sheetName);
    if (!existSheet) {
      log.info("Investment entries sheet \"{}\" does not exist, creating it", sheetName);
      client.createSheet(spreadSheetId, sheetName);
      return emptyList();
    }

    var rows = client.readSheet(spreadSheetId, sheetName, SHEET_RANGE);
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

  public void writeInvestmentsData(List<Investment> investments) throws IOException {
    var values = new ArrayList<List<Object>>();
    for (var investment : investments) {
      var row = googleSheetsInvestmentAdapter.toSheetValueRange(investment);
      values.add(row);

      var entries = investment.getEntries();
      if (entries == null || entries.isEmpty()) {
        log.debug("No investment entries found for investment {}", investment.getName());
        continue;
      }
      writeInvestmentEntriesData(entries, INVESTMENT_SHEET_NAME_PATTERN + investment.getName());
    }

    log.debug("Writing investments data to Google Sheets");
    client.writeToSheet(spreadSheetId, INVESTMENTS_LIST_SHEET_NAME, SHEET_RANGE, values);
  }

  private void writeInvestmentEntriesData(List<InvestmentEntry> entries, String sheetName)
      throws IOException {
    var values = new ArrayList<List<Object>>();
    for (var entry : entries) {
      var row = googleSheetsInvestmentEntryAdapter.toSheetValueRange(entry);
      values.add(row);
    }

    client.writeToSheet(spreadSheetId, sheetName, SHEET_RANGE, values);
  }
}
