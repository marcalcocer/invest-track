package com.investTrack.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.HttpStatus.OK;

import com.investTrack.model.Investment;
import com.investTrack.service.InvestmentService;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

@ExtendWith(MockitoExtension.class)
public class InvestmentControllerTest {
  @Mock private InvestmentService investmentService;

  @InjectMocks private InvestmentController controller;

  private static Stream<Arguments> parametersForInvestments() {
    return Stream.of(
        Arguments.of(null, INTERNAL_SERVER_ERROR), Arguments.of(List.of(), NO_CONTENT));
  }

  @ParameterizedTest(name = "ShouldReturn{1}When{0}Investments")
  @MethodSource("parametersForInvestments")
  public void testLoadInvestments(List<Investment> investments, HttpStatus status) {
    doReturn(investments).when(investmentService).loadInvestments();

    var response = controller.loadInvestments();

    assertEquals(status, response.getStatusCode());

    verify(investmentService).loadInvestments();
    verifyNoMoreInteractions(investmentService);
  }

  @Test
  public void testLoadInvestments_ShouldReturnInvestments_WhenLoadedInvestments() {
    var investments = List.of(newInvestmentWithId(1L), newInvestmentWithId(2L));
    doReturn(investments).when(investmentService).loadInvestments();

    var response = controller.loadInvestments();

    assertEquals(OK, response.getStatusCode());
    assertEquals(investments, response.getBody());

    verify(investmentService).loadInvestments();
    verifyNoMoreInteractions(investmentService);
  }

  private Investment newInvestmentWithId(Long id) {
    return Investment.builder().id(id).build();
  }
}
