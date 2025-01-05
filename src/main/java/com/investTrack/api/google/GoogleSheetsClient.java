package com.investTrack.api.google;

import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.AddSheetRequest;
import com.google.api.services.sheets.v4.model.BatchUpdateSpreadsheetRequest;
import com.google.api.services.sheets.v4.model.ClearValuesRequest;
import com.google.api.services.sheets.v4.model.Request;
import com.google.api.services.sheets.v4.model.SheetProperties;
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

  private static final String INTERVAL_SEPARATOR = "!";

  public List<List<Object>> readSheet(String spreadSheetId, String sheetName, String range)
      throws IOException {
    var msg = "Getting data for spread sheet id \"{}\", name \"{}\", and range \"{}\"";
    log.debug(msg, spreadSheetId, sheetName, range);

    var rangeFormat = getRangeFormat(sheetName, range);
    var response = sheets.spreadsheets().values().get(spreadSheetId, rangeFormat).execute();

    var values = response.getValues();
    log.debug(
        "Obtained values for spread sheet id \"{}\", name \"{}\", and range \"{}\": {}",
        spreadSheetId,
        sheetName,
        range,
        values);

    return values;
  }

  /*
   * When we write to a sheet, we assume that the sheet exists, as it must be checked previously in the reading operation.
   */
  public void writeToSheet(
      String spreadSheetId, String name, String range, List<List<Object>> values)
      throws IOException {
    log.debug("Cleaning up the sheet before writing data");
    sheets
        .spreadsheets()
        .values()
        .clear(spreadSheetId, getRangeFormat(name, range), new ClearValuesRequest())
        .execute();

    var rangeFormat = getRangeFormat(name, range);

    var msg = "Writing data to spread sheet id \"{}\", sheet name \"{}\", and range \"{}\"";
    log.debug(msg, spreadSheetId, name, range);

    var body = new ValueRange().setValues(values);
    sheets
        .spreadsheets()
        .values()
        .update(spreadSheetId, rangeFormat, body)
        .setValueInputOption("RAW") // TODO (Marc. A): Check if this is the best option
        .execute();
  }

  /*
   * When we check if a sheet exists, we assume that its spreadsheet exists. If it does not, an exception
   * will be cached.
   */
  public boolean existSheet(String spreadSheetId, String sheetName) {
    try {
      var spreadsheet = sheets.spreadsheets().get(spreadSheetId).execute();
      if (spreadsheet == null) {
        log.debug("Sheet \"{}\" does not exist if the spreadsheet does not exist", sheetName);
        return false;
      }

      var exists =
          spreadsheet.getSheets().stream()
              .anyMatch(sheet -> sheetName.equals(sheet.getProperties().getTitle()));
      log.debug("Sheet \"{}\" existing verification returned {}", sheetName, exists);
      return exists;
    } catch (Exception e) {
      log.warn("Exception while checking if sheet \"{}\" exists:", sheetName, e);
      return false;
    }
  }

  public void createSheet(String spreadSheetId, String title) throws IOException {
    var addSheetRequest =
        new AddSheetRequest().setProperties(new SheetProperties().setTitle(title));

    var batchUpdateRequest =
        new BatchUpdateSpreadsheetRequest()
            .setRequests(List.of(new Request().setAddSheet(addSheetRequest)));

    sheets.spreadsheets().batchUpdate(spreadSheetId, batchUpdateRequest).execute();
    log.info("Sheet \"{}\" created successfully.", title);
  }

  private String getRangeFormat(String name, String range) {
    return name + INTERVAL_SEPARATOR + range;
  }
}
