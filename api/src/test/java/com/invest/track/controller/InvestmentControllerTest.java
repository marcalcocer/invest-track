package com.invest.track.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.HttpStatus.OK;

import com.invest.track.model.Investment;
import com.invest.track.model.InvestmentEntry;
import com.invest.track.model.Summary;
import com.invest.track.service.InvestmentService;
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
  public void testGetInvestments(List<Investment> investments, HttpStatus status) {
    doReturn(investments).when(investmentService).getInvestments();

    var response = controller.getInvestments();

    assertEquals(status, response.getStatusCode());

    verify(investmentService).getInvestments();
    verifyNoMoreInteractions(investmentService);
  }

  @Test
  public void testGetInvestments_ShouldReturnInvestments_WhenLoadedInvestments() {
    var investments = List.of(newInvestment(), newInvestment());
    doReturn(investments).when(investmentService).getInvestments();

    var response = controller.getInvestments();

    assertEquals(OK, response.getStatusCode());
    assertEquals(investments, response.getBody());

    verify(investmentService).getInvestments();
    verifyNoMoreInteractions(investmentService);
  }

  @Test
  public void testCreateInvestment_ShouldReturnInternalServerError_WhenNullInvestment() {
    var investment = newInvestment();
    doReturn(null).when(investmentService).createInvestment(any());

    var response = controller.createInvestment(investment);

    assertEquals(INTERNAL_SERVER_ERROR, response.getStatusCode());

    verify(investmentService).createInvestment(eq(investment));
    verifyNoMoreInteractions(investmentService);
  }

  @Test
  public void testCreateInvestment_ShouldReturnCreatedInvestment_WhenInvestmentCreated() {
    var investment = newInvestment();
    doReturn(investment).when(investmentService).createInvestment(any());

    var response = controller.createInvestment(investment);

    assertEquals(HttpStatus.CREATED, response.getStatusCode());
    assertEquals(investment, response.getBody());

    verify(investmentService).createInvestment(eq(investment));
    verifyNoMoreInteractions(investmentService);
  }

  @Test
  public void testDeleteInvestment_ShouldAnswerNoContent_WhenInvestmentNotFound() {
    doReturn(null).when(investmentService).deleteInvestment(eq(1L));

    var response = controller.deleteInvestment(1L);

    assertEquals(NO_CONTENT, response.getStatusCode());

    verify(investmentService).deleteInvestment(eq(1L));
    verifyNoMoreInteractions(investmentService);
  }

  @Test
  public void testDeleteInvestment_ShouldReturnDeletedInvestment_WhenInvestmentDeleted() {
    var investment = newInvestment();
    doReturn(investment).when(investmentService).deleteInvestment(eq(1L));

    var response = controller.deleteInvestment(1L);

    assertEquals(OK, response.getStatusCode());
    assertEquals(investment, response.getBody());

    verify(investmentService).deleteInvestment(eq(1L));
    verifyNoMoreInteractions(investmentService);
  }

  @Test
  public void testCreateInvestmentEntry_ShouldReturnInternalServerError_WhenNullInvestmentEntry() {
    doReturn(null).when(investmentService).createInvestmentEntry(any(), any());

    var response = controller.createInvestmentEntry(null, 1L);

    assertEquals(INTERNAL_SERVER_ERROR, response.getStatusCode());

    verify(investmentService).createInvestmentEntry(eq(null), eq(1L));
    verifyNoMoreInteractions(investmentService);
  }

  @Test
  public void testCreateInvestmentEntry_ShouldReturnCreatedInvestmentEntry_WhenEntryCreated() {
    var entry = newInvestmentEntry();
    doReturn(entry).when(investmentService).createInvestmentEntry(any(), any());

    var response = controller.createInvestmentEntry(entry, 1L);

    assertEquals(HttpStatus.CREATED, response.getStatusCode());
    assertEquals(entry, response.getBody());

    verify(investmentService).createInvestmentEntry(eq(entry), eq(1L));
    verifyNoMoreInteractions(investmentService);
  }

  @Test
  public void testDeleteInvestmentEntry_ShouldAnswerNoContent_WhenInvestmentEntryNotFound() {
    doReturn(null).when(investmentService).deleteInvestmentEntry(eq(1L), eq(2L));

    var response = controller.deleteInvestmentEntry(1L, 2L);

    assertEquals(NO_CONTENT, response.getStatusCode());

    verify(investmentService).deleteInvestmentEntry(eq(1L), eq(2L));
    verifyNoMoreInteractions(investmentService);
  }

  @Test
  public void testDeleteInvestmentEntry_ShouldReturnDeletedInvestmentEntry_WhenEntryDeleted() {
    var entry = newInvestmentEntry();
    doReturn(entry).when(investmentService).deleteInvestmentEntry(eq(1L), eq(2L));

    var response = controller.deleteInvestmentEntry(1L, 2L);

    assertEquals(OK, response.getStatusCode());
    assertEquals(entry, response.getBody());

    verify(investmentService).deleteInvestmentEntry(eq(1L), eq(2L));
    verifyNoMoreInteractions(investmentService);
  }

  @Test
  public void testGetSummary_ShouldReturnInternalServerError_WhenNullSummary() {
    doReturn(null).when(investmentService).getSummary();

    var response = controller.getSummary();

    assertEquals(INTERNAL_SERVER_ERROR, response.getStatusCode());

    verify(investmentService).getSummary();
    verifyNoMoreInteractions(investmentService);
  }

  @Test
  public void testGetSummary_ShouldReturnSummary() {
    var summary = Summary.builder().build();
    doReturn(summary).when(investmentService).getSummary();

    var response = controller.getSummary();

    assertEquals(OK, response.getStatusCode());
    assertEquals(summary, response.getBody());

    verify(investmentService).getSummary();
    verifyNoMoreInteractions(investmentService);
  }

  private Investment newInvestment() {
    return Investment.builder().build();
  }

  private InvestmentEntry newInvestmentEntry() {
    return InvestmentEntry.builder().build();
  }
}
