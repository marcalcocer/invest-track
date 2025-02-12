package com.investTrack.service;

import static java.util.Collections.emptyList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import com.investTrack.api.google.GoogleSheetsService;
import com.investTrack.model.Investment;
import com.investTrack.model.InvestmentEntry;
import com.investTrack.model.Summary;
import com.investTrack.repository.InvestmentRepository;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class InvestmentServiceTest {
  @Mock private GoogleSheetsService mockSheetsService;
  @Mock private InvestmentRepository mockRepository;
  @Mock private SummaryService mockSummaryService;

  @InjectMocks private InvestmentService service;

  private Object[] allMocks() {
    return new Object[] {mockSheetsService, mockRepository, mockSummaryService};
  }

  @Test
  public void testGetInvestments_ShouldReturnInvestments_WhenInvestmentsAlreadyLoaded()
      throws IOException {
    service.getInvestments(); // First call to load investments

    var investmentsList = List.of(newInvestment());
    doReturn(investmentsList).when(mockRepository).findAll();

    assertEquals(investmentsList, service.getInvestments());

    verify(mockSheetsService).readInvestmentsData();
    verify(mockRepository).saveAll(eq(emptyList()));

    verify(mockRepository).findAll();

    verifyNoMoreInteractions(allMocks());
  }

  @Test
  public void testGetInvestments_ShouldReturnNullWhenExceptionThrown() throws IOException {
    doThrow(new IOException("test")).when(mockSheetsService).readInvestmentsData();

    assertNull(service.getInvestments());

    verify(mockSheetsService).readInvestmentsData();

    verifyNoMoreInteractions(allMocks());
  }

  @Test
  public void testGetInvestments_ShouldReturnInvestmentsList() throws IOException {
    var investmentsList = List.of(newInvestment());
    doReturn(investmentsList).when(mockSheetsService).readInvestmentsData();

    assertEquals(investmentsList, service.getInvestments());

    verify(mockSheetsService).readInvestmentsData();
    verify(mockRepository).saveAll(eq(investmentsList));

    verifyNoMoreInteractions(allMocks());
  }

  @Test
  public void testCreateInvestment_ShouldReturnNull_WhenNoInvestmentsFound() throws IOException {
    doThrow(new IOException("test")).when(mockSheetsService).readInvestmentsData();

    assertNull(service.createInvestment(newInvestment()));

    verify(mockSheetsService).readInvestmentsData();
    verify(mockRepository, times(0)).save(any());

    verifyNoMoreInteractions(allMocks());
  }

  @Test
  public void testCreateInvestment_ShouldReturnNull_WhenSaveFails() throws IOException {
    var newInvestment = newInvestment();

    List<Investment> investments = new ArrayList<>();
    investments.add(newInvestment());

    var allInvestments = new ArrayList<>(investments);
    allInvestments.add(newInvestment);

    doReturn(investments).when(mockSheetsService).readInvestmentsData();
    doThrow(new RuntimeException("test")).when(mockRepository).save(any());

    assertNull(service.createInvestment(newInvestment));

    verify(mockSheetsService).readInvestmentsData();
    verify(mockRepository).saveAll(eq(allInvestments));

    verify(mockRepository).save(eq(newInvestment));

    verifyNoMoreInteractions(allMocks());
  }

  @Test
  public void testCreateInvestment_ShouldReturnNull_WhenWriteFails() throws IOException {
    var newInvestment = newInvestment();

    List<Investment> investments = new ArrayList<>();
    investments.add(newInvestment());

    var allInvestments = new ArrayList<>(investments);
    allInvestments.add(newInvestment);

    doReturn(investments).when(mockSheetsService).readInvestmentsData();
    doThrow(new IOException("test")).when(mockSheetsService).writeInvestmentsData(any());

    assertNull(service.createInvestment(newInvestment));

    verify(mockSheetsService).readInvestmentsData();
    verify(mockRepository).saveAll(eq(allInvestments));

    verify(mockRepository).save(eq(newInvestment));
    verify(mockSheetsService).writeInvestmentsData(eq(allInvestments));

    verifyNoMoreInteractions(allMocks());
  }

  @Test
  public void testCreateInvestment_ShouldReturnInvestment() throws IOException {
    var newInvestment = newInvestment();

    List<Investment> investments = new ArrayList<>();
    investments.add(newInvestment());
    investments.add(newInvestment());

    var allInvestments = new ArrayList<>(investments);
    allInvestments.add(newInvestment);

    doReturn(investments).when(mockSheetsService).readInvestmentsData();

    assertEquals(newInvestment, service.createInvestment(newInvestment));

    verify(mockSheetsService).readInvestmentsData();
    verify(mockRepository).saveAll(eq(allInvestments));

    verify(mockRepository).save(eq(newInvestment));
    verify(mockSheetsService).writeInvestmentsData(argThat(list -> list.size() == 3));

    verifyNoMoreInteractions(allMocks());
  }

  @Test
  public void testDeleteInvestment_ShouldReturnNull_WhenNoInvestmentsLoaded() throws IOException {
    doReturn(null).when(mockSheetsService).readInvestmentsData();

    assertNull(service.deleteInvestment(1L));

    verify(mockSheetsService).readInvestmentsData();
    verify(mockRepository, times(0)).saveAll(any());

    verifyNoMoreInteractions(allMocks());
  }

  @Test
  public void testDeleteInvestment_ShouldReturnNull_WheNoInvestmentToDeleteFound()
      throws IOException {
    var investments = List.of(newInvestment());
    doReturn(investments).when(mockSheetsService).readInvestmentsData();

    assertNull(service.deleteInvestment(2L));

    verify(mockSheetsService).readInvestmentsData();
    verify(mockRepository).saveAll(eq(investments));
    verify(mockRepository, times(0)).delete(any());

    verifyNoMoreInteractions(allMocks());
  }

  @Test
  public void testDeleteInvestment_ShouldReturnNull_WhenExceptionDeletingInvestment()
      throws IOException {
    var investmentToDelete = newInvestment();
    var investments = List.of(investmentToDelete, newInvestment());
    doReturn(investments).when(mockSheetsService).readInvestmentsData();
    doThrow(new RuntimeException("test")).when(mockRepository).delete(any());

    assertNull(service.deleteInvestment(1L));

    verify(mockSheetsService).readInvestmentsData();
    verify(mockRepository).saveAll(eq(investments));
    verify(mockRepository).delete(eq(investmentToDelete));

    verifyNoMoreInteractions(allMocks());
  }

  @Test
  public void testDeleteInvestment_ShouldReturnNull_WhenExceptionWritingInvestments()
      throws IOException {
    var investmentToDelete = newInvestment();
    var allInvestments = List.of(investmentToDelete, newInvestment(), newInvestment());
    var writtenInvestments = List.of(newInvestment(), newInvestment());

    doReturn(allInvestments).when(mockSheetsService).readInvestmentsData();
    doThrow(new IOException("test")).when(mockSheetsService).writeInvestmentsData(any());

    assertNull(service.deleteInvestment(1L));

    verify(mockSheetsService).readInvestmentsData();
    verify(mockRepository).saveAll(eq(writtenInvestments));
    verify(mockRepository).delete(any());
    verify(mockSheetsService).writeInvestmentsData(eq(writtenInvestments));

    verifyNoMoreInteractions(allMocks());
  }

  @Test
  public void testDeleteInvestment_ShouldReturnInvestment() throws IOException {
    var investmentToDelete = newInvestment();
    var allInvestments = List.of(investmentToDelete, newInvestment(), newInvestment());
    var writtenInvestments = List.of(newInvestment(), newInvestment());

    doReturn(allInvestments).when(mockSheetsService).readInvestmentsData();

    assertEquals(investmentToDelete, service.deleteInvestment(1L));

    verify(mockSheetsService).readInvestmentsData();
    verify(mockRepository).saveAll(eq(writtenInvestments));
    verify(mockRepository).delete(eq(investmentToDelete));
    verify(mockSheetsService).writeInvestmentsData(eq(writtenInvestments));

    verifyNoMoreInteractions(allMocks());
  }

  @Test
  public void testCreateInvestmentEntry_ShouldReturnNull_WhenNoInvestmentsLoaded()
      throws IOException {
    doReturn(null).when(mockSheetsService).readInvestmentsData();

    assertNull(service.createInvestmentEntry(null, 1L));

    verify(mockSheetsService).readInvestmentsData();
    verify(mockRepository, times(0)).save(any());

    verifyNoMoreInteractions(allMocks());
  }

  @Test
  public void testCreateInvestmentEntry_ShouldReturnNull_WhenNoInvestmentFound()
      throws IOException {
    var investments = List.of(newInvestment());
    doReturn(investments).when(mockSheetsService).readInvestmentsData();

    assertNull(service.createInvestmentEntry(null, 2L));

    verify(mockSheetsService).readInvestmentsData();
    verify(mockRepository).saveAll(eq(investments));
    verify(mockRepository, times(0)).save(any());

    verifyNoMoreInteractions(allMocks());
  }

  @Test
  public void testCreateInvestmentEntry_ShouldReturnNull_WhenSaveFails() throws IOException {
    var investment = newInvestment();
    var entry = newInvestmentEntry();
    var investments = List.of(investment);

    doReturn(investments).when(mockSheetsService).readInvestmentsData();
    doThrow(new RuntimeException("test")).when(mockRepository).save(any());

    assertNull(service.createInvestmentEntry(entry, 1L));

    verify(mockSheetsService).readInvestmentsData();
    verify(mockRepository).saveAll(eq(investments));
    verify(mockRepository).save(eq(investment));

    verifyNoMoreInteractions(allMocks());
  }

  @Test
  public void testCreateInvestmentEntry_ShouldReturnNull_WhenWriteFails() throws IOException {
    var investment = newInvestment();
    var entry = newInvestmentEntry();
    var investments = List.of(investment);

    doReturn(investments).when(mockSheetsService).readInvestmentsData();
    doThrow(new IOException("test")).when(mockSheetsService).writeInvestmentsData(any());

    assertNull(service.createInvestmentEntry(entry, 1L));

    verify(mockSheetsService).readInvestmentsData();
    verify(mockRepository).saveAll(eq(investments));
    verify(mockRepository).save(eq(investment));
    verify(mockSheetsService).writeInvestmentsData(eq(investments));

    verifyNoMoreInteractions(allMocks());
  }

  @ParameterizedTest
  @NullSource
  @CsvSource({"2025-01-01T00:00:00"})
  public void testCreateInvestmentEntry_ShouldReturnEntry_WhenEntrySaved(String datetime)
      throws IOException {
    var investment = newInvestment();
    var investments = List.of(investment);
    var entry = newInvestmentEntry();
    entry.setDatetime(datetime != null ? LocalDateTime.parse(datetime) : null);

    doReturn(investments).when(mockSheetsService).readInvestmentsData();

    assertEquals(entry, service.createInvestmentEntry(entry, 1L));

    verify(mockSheetsService).readInvestmentsData();
    verify(mockRepository).saveAll(eq(investments));
    verify(mockRepository).save(eq(investment));
    verify(mockSheetsService).writeInvestmentsData(eq(investments));

    verifyNoMoreInteractions(allMocks());
  }

  @Test
  public void testDeleteInvestmentEntry_ShouldReturnNull_WhenNoInvestmentsLoaded()
      throws IOException {
    doReturn(null).when(mockSheetsService).readInvestmentsData();

    assertNull(service.deleteInvestmentEntry(1L, 1L));

    verify(mockSheetsService).readInvestmentsData();
    verify(mockRepository, times(0)).save(any());

    verifyNoMoreInteractions(allMocks());
  }

  @Test
  public void testDeleteInvestmentEntry_ShouldReturnNull_WhenNoInvestmentFound()
      throws IOException {
    var investments = List.of(newInvestment());
    doReturn(investments).when(mockSheetsService).readInvestmentsData();

    assertNull(service.deleteInvestmentEntry(2L, 1L));

    verify(mockSheetsService).readInvestmentsData();
    verify(mockRepository).saveAll(eq(investments));
    verify(mockRepository, times(0)).save(any());

    verifyNoMoreInteractions(allMocks());
  }

  @Test
  public void testDeleteInvestmentEntry_ShouldReturnNull_WhenNoEntryFound() throws IOException {
    var investment = newInvestment();
    var investments = List.of(investment);
    doReturn(investments).when(mockSheetsService).readInvestmentsData();

    assertNull(service.deleteInvestmentEntry(1L, 2L));

    verify(mockSheetsService).readInvestmentsData();
    verify(mockRepository).saveAll(eq(investments));
    verify(mockRepository, times(0)).save(any());

    verifyNoMoreInteractions(allMocks());
  }

  @Test
  public void testDeleteInvestmentEntry_ShouldReturnNull_WhenExceptionSavingInvestment()
      throws IOException {
    var investment = newInvestment();
    var entryToDelete = newInvestmentEntry();
    investment.getEntries().add(entryToDelete);
    var investments = List.of(investment);

    doReturn(investments).when(mockSheetsService).readInvestmentsData();
    doThrow(new RuntimeException("test")).when(mockRepository).save(any());

    assertNull(service.deleteInvestmentEntry(1L, 1L));

    verify(mockSheetsService).readInvestmentsData();
    verify(mockRepository).saveAll(eq(investments));
    verify(mockRepository).save(eq(investment));

    verifyNoMoreInteractions(allMocks());
  }

  @Test
  public void testDeleteInvestmentEntry_ShouldReturnNull_WhenExceptionWritingInvestments()
      throws IOException {
    var investment = newInvestment();
    var entryToDelete = newInvestmentEntry();
    investment.getEntries().add(entryToDelete);
    var investments = List.of(investment);

    doReturn(investments).when(mockSheetsService).readInvestmentsData();
    doThrow(new IOException("test")).when(mockSheetsService).writeInvestmentsData(any());

    assertNull(service.deleteInvestmentEntry(1L, 1L));

    verify(mockSheetsService).readInvestmentsData();
    verify(mockRepository).saveAll(eq(investments));
    verify(mockRepository).save(eq(investment));
    verify(mockSheetsService).writeInvestmentsData(eq(investments));

    verifyNoMoreInteractions(allMocks());
  }

  @Test
  public void testDeleteInvestmentEntry_ShouldReturnEntry() throws IOException {
    var investment = newInvestment();
    var entryToDelete = newInvestmentEntry();
    investment.getEntries().add(entryToDelete);
    var investments = List.of(investment);

    doReturn(investments).when(mockSheetsService).readInvestmentsData();

    assertEquals(entryToDelete, service.deleteInvestmentEntry(1L, 1L));

    verify(mockSheetsService).readInvestmentsData();
    verify(mockRepository).saveAll(eq(investments));
    verify(mockRepository).save(eq(investment));
    verify(mockSheetsService).writeInvestmentsData(eq(investments));

    verifyNoMoreInteractions(allMocks());
  }

  @Test
  public void testGetSummary_ShouldReturnNull_WhenNoInvestmentsLoaded() throws IOException {
    doReturn(null).when(mockSheetsService).readInvestmentsData();

    assertNull(service.getSummary());

    verify(mockSheetsService).readInvestmentsData();
    verify(mockRepository, times(0)).saveAll(any());

    verifyNoMoreInteractions(allMocks());
  }

  @Test
  public void testGetSummary_ShouldReturnSummary() throws IOException {
    var investments = List.of(newInvestment());
    doReturn(investments).when(mockSheetsService).readInvestmentsData();

    var summary = Summary.builder().build();
    doReturn(summary).when(mockSummaryService).calculateSummary(investments);

    assertEquals(summary, service.getSummary());

    verify(mockSheetsService).readInvestmentsData();
    verify(mockRepository).saveAll(eq(investments));
    verify(mockSummaryService).calculateSummary(eq(investments));

    verifyNoMoreInteractions(allMocks());
  }

  private Investment newInvestment() {
    var investment = new Investment(1L, "test", "desc", "EUR", null, null, false, 0.0, 0.0, 0.0);
    investment.setEntries(new ArrayList<>());
    return investment;
  }

  private InvestmentEntry newInvestmentEntry() {
    var entry = new InvestmentEntry();
    entry.setId(1L);
    return entry;
  }
}
