package com.invest.track.api.google;

import com.invest.track.model.Investment;
import com.invest.track.model.InvestmentEntry;
import com.invest.track.model.adapter.InvestmentEntryAdapter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class GoogleSheetsInvestmentEntriesService {
  private final String spreadSheetId;
  private final GoogleSheetsClient client;
  private final InvestmentEntryAdapter investmentEntryAdapter;
  private final GoogleSheetsInvestmentEntryAdapter googleSheetsInvestmentEntryAdapter;

  private final String INVESTMENT_SHEET_NAME_PATTERN = "Investment entries - ";
  private static final String READ_SHEET_RANGE = "A2:P";
  private static final String WRITE_SHEET_RANGE = "A1:P";
  private static final List<Object> INVESTMENT_ENTRIES_HEADERS =
      List.of("Date", "Initial Invested Amount", "Reinvested Amount", "Profitability", "Comments");

  public List<InvestmentEntry> readInvestmentEntries(Investment investment) throws IOException {
    var sheetName = INVESTMENT_SHEET_NAME_PATTERN + investment.getName();
    var existSheet = existSheet(sheetName);
    if (!existSheet) {
      client.createSheet(spreadSheetId, sheetName);
      return new ArrayList<>();
    }
    var rows = client.readSheet(spreadSheetId, sheetName, READ_SHEET_RANGE);
    if (rows == null) {
      log.debug("Investment entries data not found for sheet name \"{}\"", sheetName);
      return new ArrayList<>();
    }
    var investmentEntries = new ArrayList<InvestmentEntry>();
    for (var row : rows) {
      var investmentEntry = investmentEntryAdapter.fromSheetValueRange(row, investment);
      investmentEntries.add(investmentEntry);
    }
    return investmentEntries;
  }

  public void writeInvestmentEntries(
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

  private boolean existSheet(String sheetName) throws IOException {
    return client.sheetExists(spreadSheetId, sheetName);
  }

  public void writeInvestmentEntriesData(List<InvestmentEntry> entries, String sheetName)
      throws IOException {
    var values = new ArrayList<List<Object>>();
    values.add(INVESTMENT_ENTRIES_HEADERS);
    for (var entry : entries) {
      var row = googleSheetsInvestmentEntryAdapter.toSheetValueRange(entry);
      values.add(row);
    }
    client.writeToSheet(spreadSheetId, sheetName, WRITE_SHEET_RANGE, values);
  }
}
