package com.investTrack.api.google;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import com.investTrack.model.Investment;
import com.investTrack.model.InvestmentAdapter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class GoogleSheetsServiceTest {
  @Mock private GoogleSheetsClient mockClient;
  @Mock private InvestmentAdapter mockInvestmentAdapter;
  @Mock private GoogleSheetsAdapter mockSheetsAdapter;

  @InjectMocks private GoogleSheetsService service;

  private Object[] allMocks() {
    return new Object[] {mockClient, mockInvestmentAdapter, mockSheetsAdapter};
  }

  @Test
  public void testGetInvestmentData_ShouldThrowIOException_WhenErrorsReadingSheet()
      throws IOException {
    doThrow(new IOException("test")).when(mockClient).readSheet(any(), any());

    var exception = assertThrows(IOException.class, () -> service.getInvestmentData());

    assertEquals("test", exception.getMessage());

    verify(mockClient).readSheet(eq("Investments List"), eq("A2:P"));

    verifyNoMoreInteractions(allMocks());
  }

  @Test
  public void testGetInvestmentData_ShouldReturnEmptyList_WhenSheetIsEmpty() throws IOException {
    doReturn(null).when(mockClient).readSheet(any(), any());

    var investments = service.getInvestmentData();

    assertEquals(new ArrayList<>(), investments);

    verify(mockClient).readSheet(eq("Investments List"), eq("A2:P"));

    verifyNoMoreInteractions(allMocks());
  }

  @Test
  public void testGetInvestmentData_ShouldReturnAValidList() throws IOException {
    var sampleInvestments = getSampleInvestments();
    var sampleInvestment1 = sampleInvestments.get(0);
    var sampleInvestment2 = sampleInvestments.get(1);

    var sampleValuesRange = getSampleValuesRange();
    var sampleValueRange1 = sampleValuesRange.get(0);
    var sampleValueRange2 = sampleValuesRange.get(1);

    doReturn(sampleValuesRange).when(mockClient).readSheet(any(), any());
    doReturn(sampleInvestment1)
        .when(mockInvestmentAdapter)
        .fromSheetValueRange(eq(sampleValueRange1));
    doReturn(sampleInvestment2)
        .when(mockInvestmentAdapter)
        .fromSheetValueRange(eq(sampleValueRange2));

    var investments = service.getInvestmentData();

    assertEquals(sampleInvestments, investments);

    verify(mockClient).readSheet(eq("Investments List"), eq("A2:P"));
    verify(mockInvestmentAdapter).fromSheetValueRange(eq(sampleValueRange1));
    verify(mockInvestmentAdapter).fromSheetValueRange(eq(sampleValueRange2));

    verifyNoMoreInteractions(allMocks());
  }

  @Test
  public void testWriteInvestmentData_ShouldThrowIOException_WhenErrorsWritingToSheet()
      throws IOException {
    doThrow(new IOException("test")).when(mockClient).writeToSheet(any(), any(), any());

    var exception =
        assertThrows(IOException.class, () -> service.writeInvestmentData(new ArrayList<>()));

    assertEquals("test", exception.getMessage());

    verify(mockClient).writeToSheet(eq("Investments List"), eq("A2:P"), any());

    verifyNoMoreInteractions(allMocks());
  }

  @Test
  public void testWriteInvestmentData_ShouldWriteToSheet() throws IOException {
    var sampleInvestments = getSampleInvestments();
    var sampleInvestment1 = sampleInvestments.get(0);
    var sampleInvestment2 = sampleInvestments.get(1);

    var sampleValuesRange = getSampleValuesRange();

    doReturn(sampleValuesRange.get(0))
        .when(mockSheetsAdapter)
        .toSheetValueRange(eq(sampleInvestment1));
    doReturn(sampleValuesRange.get(1))
        .when(mockSheetsAdapter)
        .toSheetValueRange(eq(sampleInvestment2));

    service.writeInvestmentData(sampleInvestments);

    verify(mockSheetsAdapter).toSheetValueRange(eq(sampleInvestment1));
    verify(mockSheetsAdapter).toSheetValueRange(eq(sampleInvestment2));

    verify(mockClient).writeToSheet(eq("Investments List"), eq("A2:P"), eq(sampleValuesRange));

    verifyNoMoreInteractions(allMocks());
  }

  private List<List<Object>> getSampleValuesRange() {
    List<List<Object>> valueRange = new ArrayList<>(new ArrayList<>());
    valueRange.add(List.of(("test1")));
    valueRange.add(List.of(("test2")));
    return valueRange;
  }

  private List<Investment> getSampleInvestments() {
    List<Investment> investments = new ArrayList<>();
    investments.add(Investment.builder().id(1L).build());
    investments.add(Investment.builder().id(2L).build());
    return investments;
  }
}
