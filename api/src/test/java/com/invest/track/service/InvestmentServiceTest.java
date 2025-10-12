package com.invest.track.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import com.invest.track.api.google.GoogleSheetsService;
import com.invest.track.model.Investment;
import com.invest.track.model.InvestmentEntry;
import com.invest.track.model.Summary;
import com.invest.track.repository.InvestmentRepository;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
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

  @BeforeEach
  public void setUp() throws IOException {
    verify(mockSheetsService).readInvestmentsData();
    verify(mockRepository).saveAll(any());
  }

  private Object[] allMocks() {
    return new Object[] {mockSheetsService, mockRepository, mockSummaryService};
  }

  @Test
  public void testConstructor_ShouldThrowException_WhenLoadingInvestmentsFails()
      throws IOException {
    doThrow(new IOException("test")).when(mockSheetsService).readInvestmentsData();

    var exc =
        assertThrows(
            RuntimeException.class,
            () -> new InvestmentService(mockSheetsService, mockRepository, mockSummaryService));
    assertEquals("Failed to load investments", exc.getMessage());

    verify(mockSheetsService, times(2)).readInvestmentsData();

    verifyNoMoreInteractions(allMocks());
  }

  @Test
  public void testCreateInvestment_ShouldReturnNull_WhenNoInvestmentsFound() {
    doReturn(List.of()).when(mockRepository).findAll();

    assertNull(service.createInvestment(newInvestment()));

    verify(mockRepository).findAll();
    verify(mockRepository, times(0)).save(any());

    verifyNoMoreInteractions(allMocks());
  }

  @Test
  public void testCreateInvestment_ShouldReturnNull_WhenSaveFails() {
    var newInvestment = newInvestment();

    List<Investment> investments = sampleInvestmentsList();

    doReturn(investments).when(mockRepository).findAll();
    doThrow(new RuntimeException("test")).when(mockRepository).save(any());

    assertNull(service.createInvestment(newInvestment));

    verify(mockRepository).findAll();
    verify(mockRepository).save(eq(newInvestment));

    verifyNoMoreInteractions(allMocks());
  }

  @Test
  public void testCreateInvestment_ShouldReturnNull_WhenWriteFails() throws IOException {
    var newInvestment = newInvestment();

    List<Investment> investments = sampleInvestmentsList();

    var allInvestments = new ArrayList<>(investments);
    allInvestments.add(newInvestment);

    doReturn(investments).when(mockRepository).findAll();
    doThrow(new IOException("test")).when(mockSheetsService).writeInvestmentsData(any());

    assertNull(service.createInvestment(newInvestment));

    verify(mockRepository).findAll();

    verify(mockRepository).save(eq(newInvestment));
    verify(mockSheetsService).writeInvestmentsData(eq(allInvestments));

    verifyNoMoreInteractions(allMocks());
  }

  @Test
  public void testCreateInvestment_ShouldReturnInvestment() throws IOException {
    var newInvestment = newInvestment();

    List<Investment> investments = sampleInvestmentsList();

    var allInvestments = new ArrayList<>(investments);
    allInvestments.add(newInvestment);

    doReturn(investments).when(mockRepository).findAll();

    assertEquals(newInvestment, service.createInvestment(newInvestment));

    verify(mockRepository).findAll();

    verify(mockRepository).save(eq(newInvestment));
    verify(mockSheetsService).writeInvestmentsData(eq(allInvestments));

    verifyNoMoreInteractions(allMocks());
  }

  @Test
  public void testDeleteInvestment_ShouldReturnNull_WhenNoInvestmentsLoaded() {
    doReturn(List.of()).when(mockRepository).findAll();

    assertNull(service.deleteInvestment(1L));

    verify(mockRepository).findAll();

    verify(mockRepository, times(0)).delete(any());

    verifyNoMoreInteractions(allMocks());
  }

  @Test
  public void testDeleteInvestment_ShouldReturnNull_WheNoInvestmentToDeleteFound() {
    var investments = sampleInvestmentsList();

    doReturn(investments).when(mockRepository).findAll();

    assertNull(service.deleteInvestment(2L));

    verify(mockRepository).findAll();

    verify(mockRepository, times(0)).delete(any());

    verifyNoMoreInteractions(allMocks());
  }

  @Test
  public void testDeleteInvestment_ShouldReturnNull_WhenExceptionDeletingInvestment() {
    var investmentToDelete = newInvestment();
    var investments = List.of(investmentToDelete, newInvestment());

    doReturn(investments).when(mockRepository).findAll();
    doThrow(new RuntimeException("test")).when(mockRepository).delete(any());

    assertNull(service.deleteInvestment(1L));

    verify(mockRepository).findAll();
    verify(mockRepository).delete(eq(investmentToDelete));

    verifyNoMoreInteractions(allMocks());
  }

  @Test
  public void testDeleteInvestment_ShouldReturnNull_WhenExceptionWritingInvestments()
      throws IOException {
    var investmentToDelete = newInvestmentWithId(1L);
    var writtenInvestments = List.of(newInvestmentWithId(2L), newInvestmentWithId(3L));

    var allInvestments = new ArrayList<>(writtenInvestments);
    allInvestments.add(investmentToDelete);

    doReturn(allInvestments).when(mockRepository).findAll();
    doThrow(new IOException("test")).when(mockSheetsService).writeInvestmentsData(any());

    assertNull(service.deleteInvestment(1L));

    verify(mockRepository).findAll();
    verify(mockRepository).delete(any());
    verify(mockSheetsService).writeInvestmentsData(eq(writtenInvestments));

    verifyNoMoreInteractions(allMocks());
  }

  @Test
  public void testDeleteInvestment_ShouldReturnInvestment() throws IOException {
    var investmentToDelete = newInvestmentWithId(1L);
    var writtenInvestments = List.of(newInvestmentWithId(2L), newInvestmentWithId(3L));

    var allInvestments = new ArrayList<>(writtenInvestments);
    allInvestments.add(investmentToDelete);

    doReturn(allInvestments).when(mockRepository).findAll();

    assertEquals(investmentToDelete, service.deleteInvestment(1L));

    verify(mockRepository).findAll();
    verify(mockRepository).delete(eq(investmentToDelete));
    verify(mockSheetsService).writeInvestmentsData(eq(writtenInvestments));

    verifyNoMoreInteractions(allMocks());
  }

  @Test
  public void testCreateInvestmentEntry_ShouldReturnNull_WhenNoInvestmentsLoaded() {
    doReturn(List.of()).when(mockRepository).findAll();

    assertNull(service.createInvestmentEntry(null, 1L));

    verify(mockRepository).findAll();
    verify(mockRepository, times(0)).save(any());

    verifyNoMoreInteractions(allMocks());
  }

  @Test
  public void testCreateInvestmentEntry_ShouldReturnNull_WhenNoInvestmentFound() {
    var investments = List.of(newInvestment());
    doReturn(investments).when(mockRepository).findAll();

    assertNull(service.createInvestmentEntry(null, 2L));

    verify(mockRepository).findAll();
    verify(mockRepository, times(0)).save(any());

    verifyNoMoreInteractions(allMocks());
  }

  @Test
  public void testCreateInvestmentEntry_ShouldReturnNull_WhenSaveFails() {
    var investment = newInvestmentWithId(1L);
    var entry = newInvestmentEntry();
    var investments = List.of(investment);

    doReturn(investments).when(mockRepository).findAll();
    doThrow(new RuntimeException("test")).when(mockRepository).save(any());

    assertNull(service.createInvestmentEntry(entry, 1L));

    verify(mockRepository).findAll();
    verify(mockRepository).save(eq(investment));

    verifyNoMoreInteractions(allMocks());
  }

  @Test
  public void testCreateInvestmentEntry_ShouldReturnNull_WhenWriteFails() throws IOException {
    var investment = newInvestment();
    var entry = newInvestmentEntry();
    var investments = List.of(investment);

    doReturn(investments).when(mockRepository).findAll();
    doThrow(new IOException("test")).when(mockSheetsService).writeInvestmentsData(any());

    assertNull(service.createInvestmentEntry(entry, 1L));

    verify(mockRepository).findAll();
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

    doReturn(investments).when(mockRepository).findAll();

    assertEquals(entry, service.createInvestmentEntry(entry, 1L));

    verify(mockRepository).findAll();
    verify(mockRepository).save(eq(investment));
    verify(mockSheetsService).writeInvestmentsData(eq(investments));

    verifyNoMoreInteractions(allMocks());
  }

  @Test
  public void testDeleteInvestmentEntry_ShouldReturnNull_WhenNoInvestmentsLoaded() {
    doReturn(List.of()).when(mockRepository).findAll();

    assertNull(service.deleteInvestmentEntry(1L, 1L));

    verify(mockRepository).findAll();
    verify(mockRepository, times(0)).save(any());

    verifyNoMoreInteractions(allMocks());
  }

  @Test
  public void testDeleteInvestmentEntry_ShouldReturnNull_WhenNoInvestmentFound() {
    var investments = List.of(newInvestment());
    doReturn(investments).when(mockRepository).findAll();

    assertNull(service.deleteInvestmentEntry(2L, 1L));

    verify(mockRepository).findAll();
    verify(mockRepository, times(0)).save(any());

    verifyNoMoreInteractions(allMocks());
  }

  @Test
  public void testDeleteInvestmentEntry_ShouldReturnNull_WhenNoEntryFound() {
    var investment = newInvestment();
    var investments = List.of(investment);
    doReturn(investments).when(mockRepository).findAll();

    assertNull(service.deleteInvestmentEntry(1L, 2L));

    verify(mockRepository).findAll();
    verify(mockRepository, times(0)).save(any());

    verifyNoMoreInteractions(allMocks());
  }

  @Test
  public void testDeleteInvestmentEntry_ShouldReturnNull_WhenExceptionSavingInvestment() {
    var investment = newInvestment();
    var entryToDelete = newInvestmentEntry();
    investment.getEntries().add(entryToDelete);
    var investments = List.of(investment);

    doReturn(investments).when(mockRepository).findAll();
    doThrow(new RuntimeException("test")).when(mockRepository).save(any());

    assertNull(service.deleteInvestmentEntry(1L, 1L));

    verify(mockRepository).findAll();
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

    doReturn(investments).when(mockRepository).findAll();
    doThrow(new IOException("test")).when(mockSheetsService).writeInvestmentsData(any());

    assertNull(service.deleteInvestmentEntry(1L, 1L));

    verify(mockRepository).findAll();
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

    doReturn(investments).when(mockRepository).findAll();

    assertEquals(entryToDelete, service.deleteInvestmentEntry(1L, 1L));

    verify(mockRepository).findAll();
    verify(mockRepository).save(eq(investment));
    verify(mockSheetsService).writeInvestmentsData(eq(investments));

    verifyNoMoreInteractions(allMocks());
  }

  @Test
  public void testGetSummary_ShouldReturnNull_WhenNoInvestmentsLoaded() {
    doReturn(List.of()).when(mockRepository).findAll();

    assertNull(service.getSummary());

    verify(mockRepository).findAll();
    verify(mockSummaryService, times(0)).calculateSummary(any());

    verifyNoMoreInteractions(allMocks());
  }

  @Test
  public void testGetSummary_ShouldReturnSummary() {
    var investments = List.of(newInvestment());
    doReturn(investments).when(mockRepository).findAll();

    var summary = Summary.builder().build();
    doReturn(summary).when(mockSummaryService).calculateSummary(investments);

    assertEquals(summary, service.getSummary());

    verify(mockRepository).findAll();
    verify(mockSummaryService).calculateSummary(eq(investments));

    verifyNoMoreInteractions(allMocks());
  }

  private List<Investment> sampleInvestmentsList() {
    var investments = new ArrayList<Investment>();
    investments.add(newInvestment());
    return investments;
  }

  private Investment newInvestment() {
    return newInvestmentWithId(1L);
  }

  private Investment newInvestmentWithId(Long id) {
    var investment = new Investment(1L, "test", "desc", "EUR", null, null, false);
    investment.setId(id);
    investment.setEntries(new ArrayList<>());
    return investment;
  }

  private InvestmentEntry newInvestmentEntry() {
    var entry = InvestmentEntry.builder().build();
    entry.setId(1L);
    return entry;
  }
}
