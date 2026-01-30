package com.invest.track.api.google;

import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.AddSheetRequest;
import com.google.api.services.sheets.v4.model.BatchUpdateSpreadsheetRequest;
import com.google.api.services.sheets.v4.model.ClearValuesRequest;
import com.google.api.services.sheets.v4.model.DeleteSheetRequest;
import com.google.api.services.sheets.v4.model.Request;
import com.google.api.services.sheets.v4.model.SheetProperties;
import com.google.api.services.sheets.v4.model.ValueRange;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

// Performs operations on Google Sheets API

@Slf4j
@RequiredArgsConstructor
public class GoogleSheetsClient {
  private final Sheets sheets;

  private static final String INTERVAL_SEPARATOR = "!";

  public boolean sheetExists(String spreadSheetId, String sheetName) throws IOException {
    var sheetsList = sheets.spreadsheets().get(spreadSheetId).execute().getSheets();
    if (sheetsList == null) return false;
    for (var sheet : sheetsList) {
      var props = sheet.getProperties();
      if (props != null && sheetName.equals(props.getTitle())) {
        return true;
      }
    }
    return false;
  }

  public List<List<Object>> readSheet(String spreadSheetId, String sheetName, String range)
      throws IOException {
    var msg = "Getting data for spread sheet id \"{}\", name \"{}\", and range \"{}\"";
    log.info(msg, spreadSheetId, sheetName, range);

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
    clearSheet(spreadSheetId, name, range);

    var rangeFormat = getRangeFormat(name, range);

    var msg = "Writing data to spread sheet id \"{}\", sheet name \"{}\", and range \"{}\"";
    log.info(msg, spreadSheetId, name, range);

    var body = new ValueRange().setValues(values);
    sheets
        .spreadsheets()
        .values()
        .update(spreadSheetId, rangeFormat, body)
        .setValueInputOption("RAW") // TODO (Marc. A): Check if this is the best option
        .execute();
  }

  public void clearSheet(String spreadSheetId, String name, String range) throws IOException {
    log.debug(
        "Clearing data for spread sheet id \"{}\", name \"{}\", and range \"{}\"",
        spreadSheetId,
        name,
        range);
    sheets
        .spreadsheets()
        .values()
        .clear(spreadSheetId, getRangeFormat(name, range), new ClearValuesRequest())
        .execute();
  }

  public Map<String, Integer> getSheets(String spreadSheetId) throws IOException {
    log.debug("Getting sheets for spread sheet id \"{}\"", spreadSheetId);
    var sheets = this.sheets.spreadsheets().get(spreadSheetId).execute().getSheets();
    var sheetsMap =
        sheets.stream()
            .collect(
                Collectors.toMap(
                    sheet -> sheet.getProperties().getTitle(),
                    sheet -> sheet.getProperties().getSheetId()));

    log.info("Obtained sheets for spread sheet id \"{}\": {}", spreadSheetId, sheetsMap);
    return sheetsMap;
  }

  public void createSheet(String spreadSheetId, String title) throws IOException {
    log.info("Investment entries sheet \"{}\" does not exist, creating it", title);
    var addSheetRequest =
        new AddSheetRequest().setProperties(new SheetProperties().setTitle(title));

    var batchUpdateRequest =
        new BatchUpdateSpreadsheetRequest()
            .setRequests(List.of(new Request().setAddSheet(addSheetRequest)));

    sheets.spreadsheets().batchUpdate(spreadSheetId, batchUpdateRequest).execute();
    log.info("Sheet \"{}\" created successfully.", title);
  }

  public void deleteSheets(String spreadSheetId, List<Integer> sheets) throws IOException {
    if (sheets == null || sheets.isEmpty()) {
      log.warn("No sheets to delete.");
      return;
    }

    var requests = new ArrayList<Request>();
    for (var sheetId : sheets) {
      var deleteSheetRequest = new DeleteSheetRequest().setSheetId(sheetId);
      requests.add(new Request().setDeleteSheet(deleteSheetRequest));
    }

    var batchUpdateRequest = new BatchUpdateSpreadsheetRequest().setRequests(requests);

    this.sheets.spreadsheets().batchUpdate(spreadSheetId, batchUpdateRequest).execute();
    log.info("Sheet \"{}\" deleted successfully.", sheets);
  }

  private String getRangeFormat(String name, String range) {
    return name + INTERVAL_SEPARATOR + range;
  }
}
