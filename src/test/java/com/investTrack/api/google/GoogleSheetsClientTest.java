package com.investTrack.api.google;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.Sheets.Spreadsheets;
import com.google.api.services.sheets.v4.Sheets.Spreadsheets.Values;
import com.google.api.services.sheets.v4.model.ValueRange;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class GoogleSheetsClientTest {
  @Mock private ValueRange mockValueRange;
  @Mock private Values.Get mockValuesGet;
  @Mock private Values.Update mockValuesUpdate;
  @Mock private Spreadsheets mockSpreadSheets;
  @Mock private Values mockValues;

  @Mock private Sheets mocksheets;

  private GoogleSheetsClient client;

  @BeforeEach
  public void setUp() {
    client = new GoogleSheetsClient(mocksheets, "1");
  }

  private Object[] allMocks() {
    return new Object[] {
      mockSpreadSheets, mockValues, mocksheets, mockValuesGet, mockValuesUpdate, mockValueRange
    };
  }

  @Test
  public void testReadSheet_ShouldThrowAnIOException_ErrorsGettingData() throws IOException {
    doThrow(new IOException("test")).when(mockValues).get(any(), any());
    doReturn(mockValues).when(mockSpreadSheets).values();
    doReturn(mockSpreadSheets).when(mocksheets).spreadsheets();

    var exception = assertThrows(IOException.class, () -> client.readSheet("sheet", "A1:Z100"));

    assertEquals("test", exception.getMessage());

    verify(mocksheets).spreadsheets();
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
    doReturn(mockSpreadSheets).when(mocksheets).spreadsheets();

    var result = client.readSheet("sheet", "A1:Z100");
    assertEquals(values, result);

    verify(mocksheets).spreadsheets();
    verify(mockSpreadSheets).values();
    verify(mockValues).get(eq("1"), eq("sheet!A1:Z100"));
    verify(mockValuesGet).execute();

    verifyNoMoreInteractions(allMocks());
  }

  @Test
  public void testWriteToSheet_ShouldThrowAnIOException_ErrorsWritingData() throws IOException {
    doThrow(new IOException("test")).when(mockValues).update(any(), any(), any());
    doReturn(mockValues).when(mockSpreadSheets).values();
    doReturn(mockSpreadSheets).when(mocksheets).spreadsheets();

    var exception =
        assertThrows(
            IOException.class,
            () -> client.writeToSheet("sheet", "A1:Z100", getSampleValuesRange()));

    assertEquals("test", exception.getMessage());

    verify(mocksheets).spreadsheets();
    verify(mockSpreadSheets).values();
    verify(mockValues).update(eq("1"), eq("sheet!A1:Z100"), any());

    verifyNoMoreInteractions(allMocks());
  }

  @Test
  public void testWriteToSheet_ShouldWriteDataToSheet() throws IOException {
    var values = getSampleValuesRange();
    var valueRange = new ValueRange().setValues(values);

    doReturn(mockValuesUpdate).when(mockValuesUpdate).setValueInputOption(any());
    doReturn(mockValuesUpdate).when(mockValues).update(any(), any(), any());
    doReturn(mockValues).when(mockSpreadSheets).values();
    doReturn(mockSpreadSheets).when(mocksheets).spreadsheets();

    client.writeToSheet("sheet", "A1:Z100", values);

    verify(mocksheets).spreadsheets();
    verify(mockSpreadSheets).values();
    verify(mockValues).update(eq("1"), eq("sheet!A1:Z100"), eq(valueRange));
    verify(mockValuesUpdate).setValueInputOption("RAW");
    verify(mockValuesUpdate).execute();

    verifyNoMoreInteractions(allMocks());
  }

  private List<List<Object>> getSampleValuesRange() {
    List<List<Object>> valueRange = new ArrayList<>(new ArrayList<>());
    valueRange.add(List.of(("test")));
    return valueRange;
  }
}
