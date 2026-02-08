package com.invest.track.api.google;

import static java.util.Collections.emptyList;

import com.invest.track.model.Forecast;
import com.invest.track.model.Investment;
import com.invest.track.model.adapter.InvestmentAdapter;
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
public class GoogleSheetsInvestmentService {
  private final String spreadSheetId;
  private final GoogleSheetsClient client;
  private final GoogleSheetsInvestmentAdapter googleSheetsInvestmentAdapter;
  private final InvestmentAdapter investmentAdapter;
  private final String allowlistSheetsConfig;
  private final GoogleSheetsInvestmentEntriesService investmentEntriesService;
  private final GoogleSheetsForecastService forecastService;

  // Map to store the sheet names and their corresponding IDs, so we are able to clean up them based
  // on the sheet names that we haven't modified when writing investment data
  private Map<String, Integer> sheetsByName;

  private List<String> getAllowlistSheets() {
    return List.of(allowlistSheetsConfig.split(",\s*"));
  }

  private static final String READ_SHEET_RANGE = "A2:P";
  private static final String WRITE_SHEET_RANGE = "A1:P";

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

  public synchronized List<Investment> readInvestmentsData() throws IOException {
    log.info("Started reading investments data from Google Sheets");
    sheetsByName = client.getSheets(spreadSheetId);
    if (!existInvestmentsListSheet()) {
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

      var investmentEntries = investmentEntriesService.readInvestmentEntries(investment);
      investment.setEntries(investmentEntries);

      investments.add(investment);
    }

    mergeForecasts(investments);
    return investments;
  }

  private void mergeForecasts(List<Investment> investments) throws IOException {
    var forecasts = forecastService.readForecastsData(investments);
    for (var forecast : forecasts) {
      var inv =
          investments.stream()
              .filter(i -> i.getId().equals(forecast.getInvestment().getId()))
              .findFirst();
      inv.ifPresent(
          i -> {
            log.debug("Merging forecast {} into investment {}", forecast.getId(), i.getId());
            i.addForecast(forecast);
          });
    }
  }

  public synchronized void writeInvestmentsData(List<Investment> investments) throws IOException {
    log.info("Started writing investments data to Google Sheets");

    // We want to store all the sheet names that are not written to, so we can clean them up later
    sheetsByName = client.getSheets(spreadSheetId);
    var nonWrittenSheets = new HashMap<>(Map.copyOf(sheetsByName));

    // Remove allowlist sheets from cleanup
    for (String allowSheet : getAllowlistSheets()) {
      nonWrittenSheets.remove(allowSheet);
    }

    writeInvestmentsList(investments, nonWrittenSheets);

    investmentEntriesService.writeInvestmentEntries(investments, nonWrittenSheets);

    writeAllForecasts(investments);

    if (!nonWrittenSheets.isEmpty()) {
      cleanUpSheets(nonWrittenSheets);
    }
  }

  private void writeAllForecasts(List<Investment> investments) throws IOException {
    var allForecasts = new ArrayList<Forecast>();
    for (var investment : investments) {
      if (investment.getForecasts() != null) {
        allForecasts.addAll(investment.getForecasts());
      }
    }
    forecastService.writeForecastsData(allForecasts);
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

  private void cleanUpSheets(HashMap<String, Integer> nonWrittenSheets) throws IOException {
    log.info("Deprecated sheets found, cleaning them up: {}", nonWrittenSheets);
    client.deleteSheets(spreadSheetId, nonWrittenSheets.values().stream().toList());
  }

  private boolean existInvestmentsListSheet() {
    if (sheetsByName == null) {
      log.error("Sheet names are not loaded");
      throw new IllegalStateException("Sheet names are not loaded");
    }
    return sheetsByName.containsKey("Investments List");
  }
}
