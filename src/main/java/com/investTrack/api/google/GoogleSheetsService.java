package com.investTrack.api.google;

import com.investTrack.model.Investment;
import com.investTrack.model.InvestmentAdapter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

// Provides access to Google Sheets API for investment data and orchestrates client operations

@Slf4j
@RequiredArgsConstructor
public class GoogleSheetsService {
  private static final String INVESTMENTS_LIST = "Investments List";
  private static final String INVESTMENTS_LIST_RANGE = "A2:P";

  private final GoogleSheetsClient client;
  private final InvestmentAdapter investmentAdapter;
  private final GoogleSheetsAdapter googleSheetsAdapter;

  public List<Investment> getInvestmentData() throws IOException {
    var rows = client.readSheet(INVESTMENTS_LIST, INVESTMENTS_LIST_RANGE);

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

    client.writeToSheet(INVESTMENTS_LIST, INVESTMENTS_LIST_RANGE, values);
  }
}
