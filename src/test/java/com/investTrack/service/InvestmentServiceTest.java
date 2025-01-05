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
import com.investTrack.repository.InvestmentRepository;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class InvestmentServiceTest {
  @Mock private GoogleSheetsService mockSheetsService;
  @Mock private InvestmentRepository mockRepository;

  @InjectMocks private InvestmentService service;

  private Object[] allMocks() {
    return new Object[] {mockSheetsService, mockRepository};
  }

  @Test
  public void testGetInvestments_ShouldReturnInvestments_WhenInvestmentsAlreadyLoaded()
      throws IOException {
    service.getInvestments(); // First call to load investments

    var investmentsList = List.of(newInvestmentWithId());
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
    var investmentsList = List.of(newInvestmentWithId());
    doReturn(investmentsList).when(mockSheetsService).readInvestmentsData();

    assertEquals(investmentsList, service.getInvestments());

    verify(mockSheetsService).readInvestmentsData();
    verify(mockRepository).saveAll(eq(investmentsList));

    verifyNoMoreInteractions(allMocks());
  }

  @Test
  public void testCreateInvestment_ShouldReturnNull_WhenNoInvestmentsFound() throws IOException {
    doThrow(new IOException("test")).when(mockSheetsService).readInvestmentsData();

    assertNull(service.createInvestment(newInvestmentWithId()));

    verify(mockSheetsService).readInvestmentsData();
    verify(mockRepository, times(0)).save(any());

    verifyNoMoreInteractions(allMocks());
  }

  @Test
  public void testCreateInvestment_ShouldReturnNull_WhenSaveFails() throws IOException {
    var newInvestment = newInvestmentWithId();

    List<Investment> investments = new ArrayList<>();
    investments.add(newInvestmentWithId());

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
    var newInvestment = newInvestmentWithId();

    List<Investment> investments = new ArrayList<>();
    investments.add(newInvestmentWithId());

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
    var newInvestment = newInvestmentWithId();

    List<Investment> investments = new ArrayList<>();
    investments.add(newInvestmentWithId());
    investments.add(newInvestmentWithId());

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
    var investments = List.of(newInvestmentWithId());
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
    var investmentToDelete = newInvestmentWithId();
    var investments = List.of(investmentToDelete, newInvestmentWithId());
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
    var investmentToDelete = newInvestmentWithId();
    var allInvestments = List.of(investmentToDelete, newInvestmentWithId(), newInvestmentWithId());
    var writtenInvestments = List.of(newInvestmentWithId(), newInvestmentWithId());

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
    var investmentToDelete = newInvestmentWithId();
    var allInvestments = List.of(investmentToDelete, newInvestmentWithId(), newInvestmentWithId());
    var writtenInvestments = List.of(newInvestmentWithId(), newInvestmentWithId());

    doReturn(allInvestments).when(mockSheetsService).readInvestmentsData();

    assertEquals(investmentToDelete, service.deleteInvestment(1L));

    verify(mockSheetsService).readInvestmentsData();
    verify(mockRepository).saveAll(eq(writtenInvestments));
    verify(mockRepository).delete(eq(investmentToDelete));
    verify(mockSheetsService).writeInvestmentsData(eq(writtenInvestments));

    verifyNoMoreInteractions(allMocks());
  }

  private Investment newInvestmentWithId() {
    return new Investment(1L, "test", "desc", "EUR", null, null, false, 0.0, 0.0, 0.0);
  }
}
