package com.invest.track.api.google;

import com.invest.track.model.Forecast;
import com.invest.track.model.adapter.ForecastAdapter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class GoogleSheetsForecastService {
  private final String spreadSheetId;
  private final GoogleSheetsClient client;
  private final ForecastAdapter forecastAdapter;

  private static final String FORECASTS_SHEET_NAME = "Forecasts";
  private static final String READ_SHEET_RANGE = "A2:H";
  private static final String WRITE_SHEET_RANGE = "A1:H";
  private static final List<Object> FORECASTS_HEADERS =
      List.of(
          "Forecast ID",
          "Investment ID",
          "Name",
          "Start Date",
          "End Date",
          "Scenario Rates",
          "Created At",
          "Updated At");

  public synchronized List<Forecast> readForecastsData() throws IOException {
    log.info("Started reading forecasts data from Google Sheets");
    if (!client.sheetExists(spreadSheetId, FORECASTS_SHEET_NAME)) {
      client.createSheet(spreadSheetId, FORECASTS_SHEET_NAME);
      return new ArrayList<>();
    }
    var rows = client.readSheet(spreadSheetId, FORECASTS_SHEET_NAME, READ_SHEET_RANGE);
    if (rows == null) {
      log.debug("Forecast data not found");
      return new ArrayList<>();
    }
    var forecasts = new ArrayList<Forecast>();
    for (var row : rows) {
      var forecast = forecastAdapter.fromSheetValueRange(row);
      if (forecast != null) {
        forecasts.add(forecast);
      }
    }
    log.debug("Finished reading forecasts data");
    return forecasts;
  }

  public synchronized void writeForecastsData(List<Forecast> forecasts) throws IOException {
    log.info("Started writing forecasts data to Google Sheets");
    var values = new ArrayList<List<Object>>();
    values.add(FORECASTS_HEADERS);
    for (var forecast : forecasts) {
      var row = forecastAdapter.toSheetValueRange(forecast);
      values.add(row);
    }
    client.writeToSheet(spreadSheetId, FORECASTS_SHEET_NAME, WRITE_SHEET_RANGE, values);
  }
}
