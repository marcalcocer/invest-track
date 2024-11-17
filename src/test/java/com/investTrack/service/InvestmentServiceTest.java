package com.investTrack.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import com.investTrack.model.Investment;
import com.investTrack.repository.InvestmentRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class InvestmentServiceTest {

  @Mock private InvestmentRepository mockRepository;

  @InjectMocks private InvestmentService service;

  @AfterEach
  public void afterTests() {
    verifyNoMoreInteractions(mockRepository);
  }

  @Test
  public void testCreateInvestment_ShouldSaveAndReturnNewInvestment() {
    Investment investment = Investment.builder().name("name").build();
    doReturn(investment).when(mockRepository).save(any());

    assertEquals(investment, service.createInvestment(investment));

    verify(mockRepository).save(eq(investment));
  }
}
