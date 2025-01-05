package com.investTrack.api.google;

import static java.util.Collections.emptyList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import com.investTrack.model.Investment;
import com.investTrack.model.InvestmentEntry;
import com.investTrack.model.adapter.InvestmentAdapter;
import com.investTrack.model.adapter.InvestmentEntryAdapter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
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
  public void testReadInvestmentsData_ShouldReturnAnEmptyList_WhenSheetDoesNotExist()
      throws IOException {
    doReturn(false).when(mockClient).existSheet(any(), any());

    var investments = service.readInvestmentsData();

    assertEquals(emptyList(), investments);

    verify(mockClient).existSheet(eq("1"), eq("Investments List"));
    verify(mockClient).createSheet(eq("1"), eq("Investments List"));

    verifyNoMoreInteractions(allMocks());
  }

  @Test
  public void testReadInvestmentsData_ShouldThrowIOException_WhenErrorsReadingSheet()
      throws IOException {
    doReturn(true).when(mockClient).existSheet(any(), any());
    doThrow(new IOException("test")).when(mockClient).readSheet(any(), any(), any());

    var exception = assertThrows(IOException.class, () -> service.readInvestmentsData());

    assertEquals("test", exception.getMessage());

    verify(mockClient).existSheet(eq("1"), eq("Investments List"));
    verify(mockClient).readSheet(eq("1"), eq("Investments List"), eq("A2:P"));

    verifyNoMoreInteractions(allMocks());
  }

  @Test
  public void testReadInvestmentsData_ShouldReturnEmptyList_WhenSheetIsEmpty() throws IOException {
    doReturn(true).when(mockClient).existSheet(any(), any());
    doReturn(null).when(mockClient).readSheet(any(), any(), any());

    var investments = service.readInvestmentsData();

    assertEquals(new ArrayList<>(), investments);

    verify(mockClient).existSheet(eq("1"), eq("Investments List"));
    verify(mockClient).readSheet(eq("1"), eq("Investments List"), eq("A2:P"));

    verifyNoMoreInteractions(allMocks());
  }

  @Test
  public void testReadInvestmentsData_ShouldStopReading_WhenAnInvestmentIsNull()
      throws IOException {
    var listRows = getSampleInvestmentListRows();

    doReturn(true).when(mockClient).existSheet(any(), any());
    doReturn(listRows).when(mockClient).readSheet(any(), any(), any());
    doReturn(null).when(mockInvestmentAdapter).fromSheetValueRange(any());

    var investments = service.readInvestmentsData();
    assertEquals(emptyList(), investments);

    verify(mockClient).existSheet(eq("1"), eq("Investments List"));
    verify(mockClient).readSheet(eq("1"), eq("Investments List"), eq("A2:P"));
    verify(mockInvestmentAdapter).fromSheetValueRange(eq(listRows.get(0)));

    verifyNoMoreInteractions(allMocks());
  }

  @Test
  public void testReadInvestmentsData_ShouldCreateInvestmentEntriesSheet_WhenItDoesNotExist()
      throws IOException {
    var listRows = getSampleInvestmentListRows();
    var investment = newInvestment();

    doReturn(true).when(mockClient).existSheet(any(), eq("Investments List"));
    doReturn(listRows).when(mockClient).readSheet(any(), any(), any());
    doReturn(investment).when(mockInvestmentAdapter).fromSheetValueRange(any());

    doReturn(false).when(mockClient).existSheet(any(), eq("Investment entries - test"));

    var investments = service.readInvestmentsData();
    assertEquals(List.of(investment), investments);

    verify(mockClient).existSheet(eq("1"), eq("Investments List"));
    verify(mockClient).readSheet(eq("1"), eq("Investments List"), eq("A2:P"));
    verify(mockInvestmentAdapter).fromSheetValueRange(eq(listRows.get(0)));

    verify(mockClient).existSheet(eq("1"), eq("Investment entries - test"));
    verify(mockClient).createSheet(eq("1"), eq("Investment entries - test"));

    verifyNoMoreInteractions(allMocks());
  }

  @Test
  public void testReadInvestmentsData_ShouldReturnEmptyInvestmentEntries_WhenNoDataReadingThem()
      throws IOException {
    var listRows = getSampleInvestmentListRows();
    var investment = newInvestment();

    doReturn(true).when(mockClient).existSheet(any(), eq("Investments List"));
    doReturn(listRows).when(mockClient).readSheet(any(), any(), any());
    doReturn(investment).when(mockInvestmentAdapter).fromSheetValueRange(any());

    doReturn(true).when(mockClient).existSheet(any(), eq("Investment entries - test"));
    doReturn(null).when(mockClient).readSheet(any(), eq("Investment entries - test"), any());

    var investments = service.readInvestmentsData();
    assertEquals(List.of(investment), investments);

    verify(mockClient).existSheet(eq("1"), eq("Investments List"));
    verify(mockClient).readSheet(eq("1"), eq("Investments List"), eq("A2:P"));
    verify(mockInvestmentAdapter).fromSheetValueRange(eq(listRows.get(0)));

    verify(mockClient).existSheet(eq("1"), eq("Investment entries - test"));
    verify(mockClient).readSheet(eq("1"), eq("Investment entries - test"), eq("A2:P"));

    verifyNoMoreInteractions(allMocks());
  }

  @Test
  public void testReadInvestmentsData_ShouldReturnInvestmentEntries_WhenData() throws IOException {
    var investmentListRows = getSampleInvestmentListRows();
    List<List<Object>> investmentEntriesListRows = List.of(List.of("test1 investment entry"));
    var investment = newInvestment();
    var investmentEntry = new InvestmentEntry(1L, null, 0.0, 0.0, 0.0, null, "");

    doReturn(true).when(mockClient).existSheet(any(), eq("Investments List"));
    doReturn(investmentListRows).when(mockClient).readSheet(any(), any(), any());
    doReturn(investment).when(mockInvestmentAdapter).fromSheetValueRange(any());

    doReturn(true).when(mockClient).existSheet(any(), eq("Investment entries - test"));
    doReturn(investmentEntriesListRows)
        .when(mockClient)
        .readSheet(any(), eq("Investment entries - test"), any());
    doReturn(investmentEntry).when(mockInvestmentEntryAdapter).fromSheetValueRange(any(), any());

    var investments = service.readInvestmentsData();
    assertEquals(List.of(investment), investments);

    verify(mockClient).existSheet(eq("1"), eq("Investments List"));
    verify(mockClient).readSheet(eq("1"), eq("Investments List"), eq("A2:P"));
    verify(mockInvestmentAdapter).fromSheetValueRange(eq(investmentListRows.get(0)));

    verify(mockClient).existSheet(eq("1"), eq("Investment entries - test"));
    verify(mockClient).readSheet(eq("1"), eq("Investment entries - test"), eq("A2:P"));
    verify(mockInvestmentEntryAdapter)
        .fromSheetValueRange(eq(investmentEntriesListRows.get(0)), eq(investment));

    verifyNoMoreInteractions(allMocks());
  }

  @Test
  public void testWriteInvestmentsData_ShouldThrowIOException_WhenErrorsWritingToSheet()
      throws IOException {
    doThrow(new IOException("test")).when(mockClient).writeToSheet(any(), any(), any(), any());

    var exception =
        assertThrows(IOException.class, () -> service.writeInvestmentsData(new ArrayList<>()));

    assertEquals("test", exception.getMessage());

    verify(mockClient).writeToSheet(eq("1"), eq("Investments List"), eq("A2:P"), any());

    verifyNoMoreInteractions(allMocks());
  }

  @Test
  public void testWriteInvestmentsData_ShouldContinue_WhenNullEntriesInAnInvestment()
      throws IOException {
    var investment = newInvestment();

    doReturn(List.of()).when(mockSheetsInvestmentAdapter).toSheetValueRange(eq(investment));

    service.writeInvestmentsData(List.of(investment));

    verify(mockSheetsInvestmentAdapter).toSheetValueRange(eq(investment));
    verify(mockClient).writeToSheet(eq("1"), eq("Investments List"), eq("A2:P"), any());

    verifyNoMoreInteractions(allMocks());
  }

  @Test
  public void testWriteInvestmentsData_ShouldContinue_WhenEmptyEntriesInAnInvestment()
      throws IOException {
    var investment = newInvestment();
    investment.setEntries(new ArrayList<>());

    doReturn(List.of()).when(mockSheetsInvestmentAdapter).toSheetValueRange(eq(investment));

    service.writeInvestmentsData(List.of(investment));

    verify(mockSheetsInvestmentAdapter).toSheetValueRange(eq(investment));
    verify(mockClient).writeToSheet(eq("1"), eq("Investments List"), eq("A2:P"), any());

    verifyNoMoreInteractions(allMocks());
  }

  @Test
  public void testWriteInvestmentsData_ShouldWriteInvestmentEntries_WhenData() throws IOException {
    var investment = newInvestment();
    var investmentEntry = newInvestmentEntry();
    investment.setEntries(List.of(investmentEntry));

    doReturn(List.of(List.of("test1 investment")))
        .when(mockSheetsInvestmentAdapter)
        .toSheetValueRange(eq(investment));
    doReturn(List.of(List.of("test1 investment entry")))
        .when(mockSheetsInvestmentEntryAdapter)
        .toSheetValueRange(eq(investmentEntry));

    service.writeInvestmentsData(List.of(investment));

    verify(mockSheetsInvestmentAdapter).toSheetValueRange(eq(investment));
    verify(mockClient).writeToSheet(eq("1"), eq("Investments List"), eq("A2:P"), any());

    verify(mockSheetsInvestmentEntryAdapter).toSheetValueRange(eq(investmentEntry));
    verify(mockClient).writeToSheet(eq("1"), eq("Investment entries - test"), eq("A2:P"), any());

    verifyNoMoreInteractions(allMocks());
  }

  private List<List<Object>> getSampleInvestmentListRows() {
    return List.of(List.of("test1 investment"));
  }

  private Investment newInvestment() {
    return new Investment(1L, "test", "desc", "USD", null, null, false, 0.0, 0.0, 0.0);
  }

  private InvestmentEntry newInvestmentEntry() {
    return new InvestmentEntry(1L, null, 0.0, 0.0, 0.0, null, "");
  }
}
