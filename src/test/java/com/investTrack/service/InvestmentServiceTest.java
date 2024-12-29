package com.investTrack.service;

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
import org.junit.jupiter.api.AfterEach;
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

  @AfterEach
  public void afterTests() {
    verifyNoMoreInteractions(mockSheetsService, mockRepository);
  }

  @Test
  public void testGetInvestments_ShouldReturnInvestments_WhenInvestmentsAlreadyLoaded()
      throws IOException {
    service.getInvestments(); // First call to load investments

    var investmentsList = List.of(newInvestmentWithId(1L));
    doReturn(investmentsList).when(mockRepository).findAll();

    assertEquals(investmentsList, service.getInvestments());

    verify(mockSheetsService).getInvestmentData();
    verify(mockRepository).saveAll(any());

    verify(mockRepository).findAll();
  }

  @Test
  public void testGetInvestments_ShouldReturnNullWhenExceptionThrown() throws IOException {
    doThrow(new IOException("test")).when(mockSheetsService).getInvestmentData();

    assertNull(service.getInvestments());

    verify(mockSheetsService).getInvestmentData();
  }

  @Test
  public void testGetInvestments_ShouldReturnInvestmentsList() throws IOException {
    var investmentsList = List.of(newInvestmentWithId(1L));
    doReturn(investmentsList).when(mockSheetsService).getInvestmentData();

    assertEquals(investmentsList, service.getInvestments());

    verify(mockSheetsService).getInvestmentData();
    verify(mockRepository).saveAll(eq(investmentsList));
  }

  @Test
  public void testCreateInvestment_ShouldReturnNull_WhenNoInvestmentsFound() throws IOException {
    doThrow(new IOException("test")).when(mockSheetsService).getInvestmentData();

    assertNull(service.createInvestment(newInvestmentWithId(1L)));

    verify(mockSheetsService).getInvestmentData();
    verify(mockRepository, times(0)).save(any());
  }

  @Test
  public void testCreateInvestment_ShouldReturnNull_WhenSaveFails() throws IOException {
    var newInvestment = newInvestmentWithId(1L);

    List<Investment> investments = new ArrayList<>();
    investments.add(newInvestmentWithId(2L));

    var allInvestments = new ArrayList<>(investments);
    allInvestments.add(newInvestment);

    doReturn(investments).when(mockSheetsService).getInvestmentData();
    doThrow(new RuntimeException("test")).when(mockRepository).save(any());

    assertNull(service.createInvestment(newInvestment));

    verify(mockSheetsService).getInvestmentData();
    verify(mockRepository).saveAll(eq(allInvestments));

    verify(mockRepository).save(eq(newInvestment));
  }

  @Test
  public void testCreateInvestment_ShouldReturnNull_WhenWriteFails() throws IOException {
    var newInvestment = newInvestmentWithId(1L);

    List<Investment> investments = new ArrayList<>();
    investments.add(newInvestmentWithId(2L));

    var allInvestments = new ArrayList<>(investments);
    allInvestments.add(newInvestment);

    doReturn(investments).when(mockSheetsService).getInvestmentData();
    doThrow(new IOException("test")).when(mockSheetsService).writeInvestmentData(any());

    assertNull(service.createInvestment(newInvestment));

    verify(mockSheetsService).getInvestmentData();
    verify(mockRepository).saveAll(eq(allInvestments));

    verify(mockRepository).save(eq(newInvestment));
    verify(mockSheetsService).writeInvestmentData(eq(allInvestments));
  }

  @Test
  public void testCreateInvestment_ShouldReturnInvestment() throws IOException {
    var newInvestment = newInvestmentWithId(1L);

    List<Investment> investments = new ArrayList<>();
    investments.add(newInvestmentWithId(2L));
    investments.add(newInvestmentWithId(3L));

    var allInvestments = new ArrayList<>(investments);
    allInvestments.add(newInvestment);

    doReturn(investments).when(mockSheetsService).getInvestmentData();

    assertEquals(newInvestment, service.createInvestment(newInvestment));

    verify(mockSheetsService).getInvestmentData();
    verify(mockRepository).saveAll(eq(allInvestments));

    verify(mockRepository).save(eq(newInvestment));
    verify(mockSheetsService).writeInvestmentData(argThat(list -> list.size() == 3));
  }

  private Investment newInvestmentWithId(Long id) {
    return Investment.builder().id(id).build();
  }
}
