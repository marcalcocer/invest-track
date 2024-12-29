package com.investTrack.api.google;

import static java.util.Collections.emptyList;

import com.investTrack.model.Investment;
import com.investTrack.model.InvestmentAdapter;
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
  private static final String INVESTMENTS_LIST_RANGE = "A2:P";

  private final GoogleSheetsClient client;
  private final InvestmentAdapter investmentAdapter;
  private final GoogleSheetsAdapter googleSheetsAdapter;

  public List<Investment> getInvestmentData() throws IOException {
    var existSheet = client.existSheet(spreadSheetId, INVESTMENTS_LIST_SHEET_NAME);
    if (!existSheet) {
      log.info("Sheet \"{}\" does not exist, creating it", INVESTMENTS_LIST_SHEET_NAME);
      client.createSheet(spreadSheetId, INVESTMENTS_LIST_SHEET_NAME);
      return emptyList();
    }

    var rows = client.readSheet(spreadSheetId, INVESTMENTS_LIST_SHEET_NAME, INVESTMENTS_LIST_RANGE);
    if (rows == null) {
      log.debug("Investment data not found");
      return emptyList();
    }

    var investments = new ArrayList<Investment>();
    for (var row : rows) {
      var investment = investmentAdapter.fromSheetValueRange(row);
      investments.add(investment);
    }
    return investments;
  }

  public void writeInvestmentData(List<Investment> investments) throws IOException {
    var values = new ArrayList<List<Object>>();
    for (var investment : investments) {
      var row = googleSheetsAdapter.toSheetValueRange(investment);
      values.add(row);
    }

    client.writeToSheet(spreadSheetId, INVESTMENTS_LIST_SHEET_NAME, INVESTMENTS_LIST_RANGE, values);
  }
}
