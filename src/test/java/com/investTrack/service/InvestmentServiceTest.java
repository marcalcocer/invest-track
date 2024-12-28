package com.investTrack.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import com.investTrack.api.google.GoogleSheetsService;
import com.investTrack.model.Investment;
import com.investTrack.repository.InvestmentRepository;
import java.io.IOException;
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
  public void testLoadInvestments_ShouldReturnNullWhenExceptionThrown() throws IOException {
    doThrow(new IOException("test")).when(mockSheetsService).getInvestmentData();

    assertNull(service.loadInvestments());

    verify(mockSheetsService).getInvestmentData();
  }

  @Test
  public void testLoadInvestments_ShouldReturnInvestmentsList() throws IOException {
    var investmentsList = List.of(Investment.builder().id(1L).build());
    doReturn(investmentsList).when(mockSheetsService).getInvestmentData();

    assertEquals(investmentsList, service.loadInvestments());

    verify(mockSheetsService).getInvestmentData();
    verify(mockRepository).saveAll(eq(investmentsList));
  }
}
