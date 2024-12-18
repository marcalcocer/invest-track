package com.investTrack.api.google;

import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.ValueRange;
import java.io.IOException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

// Performs operations on Google Sheets API

@Slf4j
@RequiredArgsConstructor
public class GoogleSheetsClient {
  private final Sheets sheets;
  private final String spreadSheetId;

  private static final String INTERVAL_SEPARATOR = "!";

  public List<List<Object>> readSheet(String name, String range) throws IOException {
    var msg = "Getting data for spread sheet id \"{}\", sheet name \"{}\", and range \"{}\"";
    log.debug(msg, spreadSheetId, name, range);

    var rangeFormat = getRangeFormat(name, range);
    var response = sheets.spreadsheets().values().get(spreadSheetId, rangeFormat).execute();

    var values = response.getValues();
    log.debug("Obtained values: {}", values);

    return values;
  }

  public void writeToSheet(String name, String range, List<List<Object>> values)
      throws IOException {
    var msg = "Writing data to spread sheet id \"{}\", sheet name \"{}\", and range \"{}\"";
    log.debug(msg, spreadSheetId, name, range);

    var rangeFormat = getRangeFormat(name, range);
    var body = new ValueRange().setValues(values);

    sheets
        .spreadsheets()
        .values()
        .update(spreadSheetId, rangeFormat, body)
        .setValueInputOption("RAW") // TODO (Marc. A): Check if this is the best option
        .execute();
  }

  public void createSheet(String title) {}

  private String getRangeFormat(String name, String range) {
    return name + INTERVAL_SEPARATOR + range;
  }
}
