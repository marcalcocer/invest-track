package com.investTrack.api.google;

import static java.util.Collections.emptyList;
import static java.util.Collections.emptyMap;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import com.investTrack.model.Investment;
import com.investTrack.model.InvestmentEntry;
import com.investTrack.model.adapter.InvestmentAdapter;
import com.investTrack.model.adapter.InvestmentEntryAdapter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class GoogleSheetsServiceTest {
  @Mock private GoogleSheetsClient mockClient;
  @Mock private GoogleSheetsInvestmentAdapter mockSheetsInvestmentAdapter;
  @Mock private GoogleSheetsInvestmentEntryAdapter mockSheetsInvestmentEntryAdapter;
  @Mock private InvestmentAdapter mockInvestmentAdapter;
  @Mock private InvestmentEntryAdapter mockInvestmentEntryAdapter;

  private GoogleSheetsService service;

  @BeforeEach
  public void setUp() {
    service =
        new GoogleSheetsService(
            "1",
            mockClient,
            mockSheetsInvestmentAdapter,
            mockSheetsInvestmentEntryAdapter,
            mockInvestmentAdapter,
            mockInvestmentEntryAdapter);
  }

  private Object[] allMocks() {
    return new Object[] {
      mockClient,
      mockSheetsInvestmentAdapter,
      mockSheetsInvestmentEntryAdapter,
      mockInvestmentAdapter
    };
  }

  @Test
  public void testReadInvestmentsData_ShouldThrowExceptionsWhenNullSheetsByName()
      throws IOException {
    doReturn(null).when(mockClient).getSheets(any());

    var exception = assertThrows(IllegalStateException.class, () -> service.readInvestmentsData());

    assertEquals("Sheet names are not loaded", exception.getMessage());

    verify(mockClient).getSheets(eq("1"));

    verifyNoMoreInteractions(allMocks());
  }

  @Test
  public void testReadInvestmentsData_ShouldReturnAnEmptyList_WhenSheetDoesNotExist()
      throws IOException {
    doReturn(emptyMap()).when(mockClient).getSheets(any());

    var investments = service.readInvestmentsData();

    assertEquals(emptyList(), investments);

    verify(mockClient).getSheets(eq("1"));
    verify(mockClient).createSheet(eq("1"), eq("Investments List"));

    verifyNoMoreInteractions(allMocks());
  }

  @Test
  public void testReadInvestmentsData_ShouldThrowIOException_WhenErrorsReadingSheet()
      throws IOException {
    doReturn(Map.of("Investments List", 1)).when(mockClient).getSheets(any());
    doThrow(new IOException("test")).when(mockClient).readSheet(any(), any(), any());

    var exception = assertThrows(IOException.class, () -> service.readInvestmentsData());

    assertEquals("test", exception.getMessage());

    verify(mockClient).getSheets(eq("1"));
    verify(mockClient).readSheet(eq("1"), eq("Investments List"), eq("A2:P"));

    verifyNoMoreInteractions(allMocks());
  }

  @Test
  public void testReadInvestmentsData_ShouldReturnEmptyList_WhenSheetIsEmpty() throws IOException {
    doReturn(Map.of("Investments List", 1)).when(mockClient).getSheets(any());
    doReturn(null).when(mockClient).readSheet(any(), any(), any());

    var investments = service.readInvestmentsData();

    assertEquals(new ArrayList<>(), investments);

    verify(mockClient).getSheets(eq("1"));
    verify(mockClient).readSheet(eq("1"), eq("Investments List"), eq("A2:P"));

    verifyNoMoreInteractions(allMocks());
  }

  @Test
  public void testReadInvestmentsData_ShouldStopReading_WhenAnInvestmentIsNull()
      throws IOException {
    var listRows = getSampleInvestmentListRows();

    doReturn(Map.of("Investments List", 1)).when(mockClient).getSheets(any());
    doReturn(listRows).when(mockClient).readSheet(any(), any(), any());
    doReturn(null).when(mockInvestmentAdapter).fromSheetValueRange(any());

    var investments = service.readInvestmentsData();
    assertEquals(emptyList(), investments);

    verify(mockClient).getSheets(eq("1"));
    verify(mockClient).readSheet(eq("1"), eq("Investments List"), eq("A2:P"));
    verify(mockInvestmentAdapter).fromSheetValueRange(eq(listRows.get(0)));

    verifyNoMoreInteractions(allMocks());
  }

  @Test
  public void testReadInvestmentsData_ShouldCreateInvestmentEntriesSheet_WhenItDoesNotExist()
      throws IOException {
    var listRows = getSampleInvestmentListRows();
    var investment = newInvestment();

    doReturn(Map.of("Investments List", 1)).when(mockClient).getSheets(any());
    doReturn(listRows).when(mockClient).readSheet(any(), any(), any());
    doReturn(investment).when(mockInvestmentAdapter).fromSheetValueRange(any());

    var investments = service.readInvestmentsData();
    assertEquals(List.of(investment), investments);

    verify(mockClient).getSheets(eq("1"));
    verify(mockClient).readSheet(eq("1"), eq("Investments List"), eq("A2:P"));
    verify(mockInvestmentAdapter).fromSheetValueRange(eq(listRows.get(0)));

    verify(mockClient).createSheet(eq("1"), eq("Investment entries - test"));

    verifyNoMoreInteractions(allMocks());
  }

  @Test
  public void testReadInvestmentsData_ShouldReturnEmptyInvestmentEntries_WhenNoDataReadingThem()
      throws IOException {
    var listRows = getSampleInvestmentListRows();
    var investment = newInvestment();

    doReturn(Map.of("Investments List", 1, "Investment entries - test", 2))
        .when(mockClient)
        .getSheets(any());
    doReturn(listRows).when(mockClient).readSheet(any(), any(), any());
    doReturn(investment).when(mockInvestmentAdapter).fromSheetValueRange(any());

    doReturn(null).when(mockClient).readSheet(any(), eq("Investment entries - test"), any());

    var investments = service.readInvestmentsData();
    assertEquals(List.of(investment), investments);

    verify(mockClient).getSheets(eq("1"));
    verify(mockClient).readSheet(eq("1"), eq("Investments List"), eq("A2:P"));
    verify(mockInvestmentAdapter).fromSheetValueRange(eq(listRows.get(0)));
    verify(mockClient).readSheet(eq("1"), eq("Investment entries - test"), eq("A2:P"));

    verifyNoMoreInteractions(allMocks());
  }

  @Test
  public void testReadInvestmentsData_ShouldReturnInvestmentEntries_WhenData() throws IOException {
    var investmentListRows = getSampleInvestmentListRows();
    List<List<Object>> investmentEntriesListRows = List.of(List.of("test1 investment entry"));
    var investment = newInvestment();
    var investmentEntry = new InvestmentEntry(null, 0.0, 0.0, 0.0, "", null);

    doReturn(Map.of("Investments List", 1, "Investment entries - test", 2))
        .when(mockClient)
        .getSheets(any());
    doReturn(investmentListRows).when(mockClient).readSheet(any(), any(), any());
    doReturn(investment).when(mockInvestmentAdapter).fromSheetValueRange(any());

    doReturn(investmentEntriesListRows)
        .when(mockClient)
        .readSheet(any(), eq("Investment entries - test"), any());
    doReturn(investmentEntry).when(mockInvestmentEntryAdapter).fromSheetValueRange(any(), any());

    var investments = service.readInvestmentsData();
    assertEquals(List.of(investment), investments);

    verify(mockClient).getSheets(eq("1"));
    verify(mockClient).readSheet(eq("1"), eq("Investments List"), eq("A2:P"));
    verify(mockInvestmentAdapter).fromSheetValueRange(eq(investmentListRows.get(0)));

    verify(mockClient).readSheet(eq("1"), eq("Investment entries - test"), eq("A2:P"));
    verify(mockInvestmentEntryAdapter)
        .fromSheetValueRange(eq(investmentEntriesListRows.get(0)), eq(investment));

    verifyNoMoreInteractions(allMocks());
  }

  @Test
  public void testWriteInvestmentsData_ShouldThrowIOException_WhenErrorsWritingToSheet()
      throws IOException {
    List<Object> headers = getInvestmentsListHeaders();

    doReturn(emptyMap()).when(mockClient).getSheets(any());
    doThrow(new IOException("test")).when(mockClient).writeToSheet(any(), any(), any(), any());

    service.readInvestmentsData(); // We simulate to have sheets by name

    var exception =
        assertThrows(IOException.class, () -> service.writeInvestmentsData(new ArrayList<>()));

    assertEquals("test", exception.getMessage());

    verify(mockClient, times(2)).getSheets(eq("1"));
    verify(mockClient).createSheet(eq("1"), eq("Investments List"));

    verify(mockClient)
        .writeToSheet(eq("1"), eq("Investments List"), eq("A1:P"), eq(List.of(headers)));

    verifyNoMoreInteractions(allMocks());
  }

  @Test
  public void testWriteInvestmentsData_ShouldContinue_WhenNullEntriesInAnInvestment()
      throws IOException {
    var investment = newInvestment();
    List<List<Object>> values = List.of(getInvestmentsListHeaders(), List.of());

    doReturn(Map.of("Investments List", 123, "Investment entries - test", 456))
        .when(mockClient)
        .getSheets(any());
    doReturn(List.of()).when(mockSheetsInvestmentAdapter).toSheetValueRange(eq(investment));

    service.readInvestmentsData(); // We simulate to have sheets by name

    service.writeInvestmentsData(List.of(investment));

    verify(mockClient, times(2)).getSheets(eq("1"));
    verify(mockClient).readSheet(eq("1"), eq("Investments List"), eq("A2:P"));

    verify(mockSheetsInvestmentAdapter).toSheetValueRange(eq(investment));
    verify(mockClient).writeToSheet(eq("1"), eq("Investments List"), eq("A1:P"), eq(values));

    verifyNoMoreInteractions(allMocks());
  }

  @Test
  public void testWriteInvestmentsData_ShouldContinue_WhenEmptyEntriesInAnInvestment()
      throws IOException {
    var investment = newInvestment();
    investment.setEntries(new ArrayList<>());
    List<List<Object>> values = List.of(getInvestmentsListHeaders(), List.of());

    doReturn(Map.of("Investments List", 123, "Investment entries - test", 456))
        .when(mockClient)
        .getSheets(any());
    doReturn(List.of()).when(mockSheetsInvestmentAdapter).toSheetValueRange(eq(investment));

    service.readInvestmentsData(); // We simulate to have sheets by name

    service.writeInvestmentsData(List.of(investment));

    verify(mockClient, times(2)).getSheets(eq("1"));
    verify(mockClient).readSheet(eq("1"), eq("Investments List"), eq("A2:P"));

    verify(mockSheetsInvestmentAdapter).toSheetValueRange(eq(investment));
    verify(mockClient).writeToSheet(eq("1"), eq("Investments List"), eq("A1:P"), eq(values));

    verifyNoMoreInteractions(allMocks());
  }

  @Test
  public void testWriteInvestmentsData_ShouldWriteInvestmentEntries_WhenData() throws IOException {
    List<Object> valueList = List.of("test1 investment");
    List<Object> valueEntry = List.of("test1 investment entry");
    var investment = newInvestment();
    var investmentEntry = newInvestmentEntry();
    investment.setEntries(List.of(investmentEntry));

    List<List<Object>> invListValues = List.of(getInvestmentsListHeaders(), valueList);
    List<List<Object>> entriesValues = List.of(getInvestmentEntriesHeaders(), valueEntry);

    doReturn(Map.of("Investments List", 123)).when(mockClient).getSheets(any());
    doReturn(valueList).when(mockSheetsInvestmentAdapter).toSheetValueRange(eq(investment));
    doReturn(valueEntry)
        .when(mockSheetsInvestmentEntryAdapter)
        .toSheetValueRange(eq(investmentEntry));

    service.readInvestmentsData(); // We simulate to have sheets by name

    service.writeInvestmentsData(List.of(investment));

    verify(mockClient, times(2)).getSheets(eq("1"));
    verify(mockClient).readSheet(eq("1"), eq("Investments List"), eq("A2:P"));

    verify(mockSheetsInvestmentAdapter).toSheetValueRange(eq(investment));
    verify(mockClient).writeToSheet(eq("1"), eq("Investments List"), eq("A1:P"), eq(invListValues));
    verify(mockClient).createSheet(eq("1"), eq("Investment entries - test"));
    verify(mockSheetsInvestmentEntryAdapter).toSheetValueRange(eq(investmentEntry));
    verify(mockClient)
        .writeToSheet(eq("1"), eq("Investment entries - test"), eq("A1:P"), eq(entriesValues));

    verifyNoMoreInteractions(allMocks());
  }

  @Test
  public void testWriteInvestmentsData_ShouldCleanUpDeprecatedSheets() throws IOException {
    var investment = newInvestment();
    var values = List.of(getInvestmentsListHeaders(), List.of());

    doReturn(getSampleSheetMapping()).when(mockClient).getSheets(any());

    service.readInvestmentsData(); // We simulate to have sheets by name

    service.writeInvestmentsData(List.of(investment));

    verify(mockClient, times(2)).getSheets(eq("1"));
    verify(mockClient).readSheet(eq("1"), eq("Investments List"), eq("A2:P"));

    verify(mockSheetsInvestmentAdapter).toSheetValueRange(eq(investment));
    verify(mockClient).writeToSheet(eq("1"), eq("Investments List"), eq("A1:P"), eq(values));

    verify(mockClient).deleteSheets(eq("1"), eq(List.of(34, 12)));

    verifyNoMoreInteractions(allMocks());
  }

  private Map<String, Integer> getSampleSheetMapping() {
    return Map.of(
        "unk-sheet-1",
        12,
        "unk-sheet-2",
        34,
        "Investments List",
        56,
        "Investment entries - test",
        78);
  }

  private List<List<Object>> getSampleInvestmentListRows() {
    return List.of(List.of("test1 investment"));
  }

  private Investment newInvestment() {
    return new Investment(1L, "test", "desc", "USD", null, null, false, 0.0, 0.0, 0.0);
  }

  private InvestmentEntry newInvestmentEntry() {
    return new InvestmentEntry(null, 0.0, 0.0, 0.0, "", null);
  }

  private List<Object> getInvestmentsListHeaders() {
    return List.of(
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
  }

  private List<Object> getInvestmentEntriesHeaders() {
    return List.of(
        "Date", "Initial Invested Amount", "Reinvested Amount", "Profitability", "Comments");
  }
}
