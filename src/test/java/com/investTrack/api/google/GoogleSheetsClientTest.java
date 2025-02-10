package com.investTrack.api.google;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.Sheets.Spreadsheets;
import com.google.api.services.sheets.v4.Sheets.Spreadsheets.Values;
import com.google.api.services.sheets.v4.model.BatchUpdateSpreadsheetRequest;
import com.google.api.services.sheets.v4.model.ClearValuesRequest;
import com.google.api.services.sheets.v4.model.Sheet;
import com.google.api.services.sheets.v4.model.SheetProperties;
import com.google.api.services.sheets.v4.model.Spreadsheet;
import com.google.api.services.sheets.v4.model.ValueRange;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class GoogleSheetsClientTest {
  @Mock private ValueRange mockValueRange;
  @Mock private Values.Get mockValuesGet;
  @Mock private Spreadsheets.Get mockSpreadSheetsGet;
  @Mock private Values.Update mockValuesUpdate;
  @Mock private Spreadsheets mockSpreadSheets;
  @Mock private Values mockValues;
  @Mock private Spreadsheets.BatchUpdate mockBatchUpdate;
  @Mock private Values.Clear mockValuesClear;

  @Mock private Sheets mockSheets;

  @InjectMocks private GoogleSheetsClient client;

  private Object[] allMocks() {
    return new Object[] {
      mockValueRange,
      mockValuesGet,
      mockSpreadSheetsGet,
      mockValuesUpdate,
      mockSpreadSheets,
      mockValues,
      mockBatchUpdate,
      mockValuesClear,
      mockSheets
    };
  }

  @Test
  public void testReadSheet_ShouldThrowAnIOException_ErrorsGettingData() throws IOException {
    doThrow(new IOException("test")).when(mockValues).get(any(), any());
    doReturn(mockValues).when(mockSpreadSheets).values();
    doReturn(mockSpreadSheets).when(mockSheets).spreadsheets();

    var exception =
        assertThrows(IOException.class, () -> client.readSheet("1", "sheet", "A1:Z100"));

    assertEquals("test", exception.getMessage());

    verify(mockSpreadSheets).values();
    verify(mockValues).get(eq("1"), eq("sheet!A1:Z100"));

    verifyNoMoreInteractions(allMocks());
  }

  @Test
  public void testReadSheet_ShouldReturnAValidList() throws IOException {
    var values = getSampleValuesRange();
    var valueRange = new ValueRange().setValues(values);

    doReturn(valueRange).when(mockValuesGet).execute();
    doReturn(mockValuesGet).when(mockValues).get(any(), any());
    doReturn(mockValues).when(mockSpreadSheets).values();
    doReturn(mockSpreadSheets).when(mockSheets).spreadsheets();

    var result = client.readSheet("1", "sheet", "A1:Z100");
    assertEquals(values, result);

    verify(mockSheets).spreadsheets();
    verify(mockSpreadSheets).values();
    verify(mockValues).get(eq("1"), eq("sheet!A1:Z100"));
    verify(mockValuesGet).execute();

    verifyNoMoreInteractions(allMocks());
  }

  @Test
  public void testWriteToSheet_ShouldThrowAnIOException_WhenErrorsClearingData()
      throws IOException {
    doThrow(new IOException("test")).when(mockValues).clear(any(), any(), any());
    doReturn(mockValues).when(mockSpreadSheets).values();
    doReturn(mockSpreadSheets).when(mockSheets).spreadsheets();

    var exception =
        assertThrows(
            IOException.class,
            () -> client.writeToSheet("1", "sheet", "A1:Z100", getSampleValuesRange()));

    assertEquals("test", exception.getMessage());

    verify(mockSheets).spreadsheets();
    verify(mockSpreadSheets).values();
    verify(mockValues).clear(eq("1"), eq("sheet!A1:Z100"), any());
    verify(mockValues, times(0)).update(any(), any(), any());

    verifyNoMoreInteractions(allMocks());
  }

  @Test
  public void testWriteToSheet_ShouldThrowAnIOException_WhenErrorsWritingData() throws IOException {
    var sampleValueRange = getSampleValuesRange();

    doReturn(mockValuesClear).when(mockValues).clear(any(), any(), any());
    doReturn(mockValues).when(mockSpreadSheets).values();
    doReturn(mockSpreadSheets).when(mockSheets).spreadsheets();
    doThrow(new IOException("test")).when(mockValues).update(any(), any(), any());

    var exception =
        assertThrows(
            IOException.class,
            () -> client.writeToSheet("1", "sheet", "A1:Z100", sampleValueRange));

    assertEquals("test", exception.getMessage());

    verify(mockSheets, times(2)).spreadsheets();
    verify(mockSpreadSheets, times(2)).values();
    verify(mockValues).clear(eq("1"), eq("sheet!A1:Z100"), any(ClearValuesRequest.class));
    verify(mockValuesClear).execute();
    verify(mockValues)
        .update(eq("1"), eq("sheet!A1:Z100"), assertValueRangeContent(sampleValueRange));

    verifyNoMoreInteractions(allMocks());
  }

  @Test
  public void testWriteToSheet_ShouldWriteDataToSheet() throws IOException {
    var values = getSampleValuesRange();
    var valueRange = new ValueRange().setValues(values);

    doReturn(mockValuesClear).when(mockValues).clear(any(), any(), any());
    doReturn(mockValuesUpdate).when(mockValuesUpdate).setValueInputOption(any());
    doReturn(mockValuesUpdate).when(mockValues).update(any(), any(), any());
    doReturn(mockValues).when(mockSpreadSheets).values();
    doReturn(mockSpreadSheets).when(mockSheets).spreadsheets();

    client.writeToSheet("1", "sheet", "A1:Z100", values);

    verify(mockSheets, times(2)).spreadsheets();
    verify(mockSpreadSheets, times(2)).values();
    verify(mockValues).clear(eq("1"), eq("sheet!A1:Z100"), any(ClearValuesRequest.class));
    verify(mockValuesClear).execute();

    verify(mockValues).update(eq("1"), eq("sheet!A1:Z100"), eq(valueRange));
    verify(mockValuesUpdate).setValueInputOption("RAW");
    verify(mockValuesUpdate).execute();

    verifyNoMoreInteractions(allMocks());
  }

  @Test
  public void testGetSheets_ShouldThrowIOException_WhenErrorsGettingSheets() throws IOException {
    doThrow(new IOException("test")).when(mockSpreadSheets).get(any());
    doReturn(mockSpreadSheets).when(mockSheets).spreadsheets();

    var exception = assertThrows(IOException.class, () -> client.getSheets("1"));
    assertEquals("test", exception.getMessage());

    verify(mockSheets).spreadsheets();
    verify(mockSpreadSheets).get(eq("1"));

    verifyNoMoreInteractions(allMocks());
  }

  @Test
  public void testGetSheets_ShouldReturnSheets() throws IOException {
    var sheet1 = newSheetWithTitleAndId("sheet1", 1);
    var sheet2 = newSheetWithTitleAndId("sheet2", 2);

    var spreadsheet = new Spreadsheet();
    spreadsheet.setSheets(List.of(sheet1, sheet2));

    doReturn(spreadsheet).when(mockSpreadSheetsGet).execute();
    doReturn(mockSpreadSheetsGet).when(mockSpreadSheets).get(any());
    doReturn(mockSpreadSheets).when(mockSheets).spreadsheets();

    var result = client.getSheets("1");
    assertEquals(Map.of("sheet1", 1, "sheet2", 2), result);

    verify(mockSheets).spreadsheets();
    verify(mockSpreadSheets).get(eq("1"));
    verify(mockSpreadSheetsGet).execute();

    verifyNoMoreInteractions(allMocks());
  }

  @Test
  public void testCreateSheet_ShouldThrowAnIOException_WhenErrorsCreatingSheet()
      throws IOException {
    doThrow(new IOException("test")).when(mockSpreadSheets).batchUpdate(any(), any());
    doReturn(mockSpreadSheets).when(mockSheets).spreadsheets();

    var exception = assertThrows(IOException.class, () -> client.createSheet("1", "sheet"));
    assertEquals("test", exception.getMessage());

    verify(mockSheets).spreadsheets();
    verify(mockSpreadSheets).batchUpdate(eq("1"), assertBatchUpdateRequest());

    verifyNoMoreInteractions(allMocks());
  }

  @Test
  public void testCreateSheet_ShouldCreateSheet() throws IOException {
    doReturn(mockBatchUpdate).when(mockSpreadSheets).batchUpdate(any(), any());
    doReturn(mockSpreadSheets).when(mockSheets).spreadsheets();

    client.createSheet("1", "sheet");

    verify(mockSheets).spreadsheets();
    verify(mockSpreadSheets).batchUpdate(eq("1"), assertBatchUpdateRequest());
    verify(mockBatchUpdate).execute();

    verifyNoMoreInteractions(allMocks());
  }

  @ParameterizedTest
  @NullAndEmptySource
  public void testDeleteSheets_ShouldReturn_WhenNoSheetsToDelete(List<Integer> list)
      throws IOException {
    client.deleteSheets("1", list);
    verifyNoMoreInteractions(allMocks());
  }

  @Test
  public void testDeleteSheets_ShouldThrowIOException_WhenErrorsDeletingSheets()
      throws IOException {
    doThrow(new IOException("test")).when(mockSpreadSheets).batchUpdate(any(), any());
    doReturn(mockSpreadSheets).when(mockSheets).spreadsheets();

    var exception = assertThrows(IOException.class, () -> client.deleteSheets("1", List.of(1, 2)));
    assertEquals("test", exception.getMessage());

    verify(mockSheets).spreadsheets();
    verify(mockSpreadSheets).batchUpdate(eq("1"), any());
    verifyNoMoreInteractions(allMocks());
  }

  @Test
  public void testDeleteSheets_ShouldDeleteSheets() throws IOException {
    doReturn(mockBatchUpdate).when(mockSpreadSheets).batchUpdate(any(), any());
    doReturn(mockSpreadSheets).when(mockSheets).spreadsheets();

    client.deleteSheets("1", List.of(1, 2));

    verify(mockSheets).spreadsheets();
    verify(mockSpreadSheets).batchUpdate(eq("1"), argThat(arg -> arg.getRequests().size() == 2));
    verify(mockBatchUpdate).execute();

    verifyNoMoreInteractions(allMocks());
  }

  private List<List<Object>> getSampleValuesRange() {
    List<List<Object>> valueRange = new ArrayList<>(new ArrayList<>());
    valueRange.add(List.of(("test")));
    return valueRange;
  }

  private BatchUpdateSpreadsheetRequest assertBatchUpdateRequest() {
    return argThat(
        request ->
            request.getRequests().size() == 1
                && request
                    .getRequests()
                    .get(0)
                    .getAddSheet()
                    .getProperties()
                    .getTitle()
                    .equals("sheet"));
  }

  private ValueRange assertValueRangeContent(List<List<Object>> values) {
    return argThat(arg -> arg.getValues().equals(values));
  }

  private Sheet newSheetWithTitleAndId(String sheet, int id) {
    return new Sheet().setProperties(new SheetProperties().setTitle(sheet).setSheetId(id));
  }
}
