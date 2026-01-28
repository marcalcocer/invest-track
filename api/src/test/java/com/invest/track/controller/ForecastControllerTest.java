package com.invest.track.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.HttpStatus.OK;

import com.invest.track.model.Forecast;
import com.invest.track.model.Forecast.ForecastScenario;
import com.invest.track.service.ForecastService;
import java.time.LocalDate;
import java.util.EnumMap;
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
public class ForecastControllerTest {
  @Mock private ForecastService forecastService;

  @InjectMocks private ForecastController controller;

  private static Stream<Arguments> parametersForForecasts() {
    return Stream.of(
        Arguments.of(null, INTERNAL_SERVER_ERROR), Arguments.of(List.of(), NO_CONTENT));
  }

  @ParameterizedTest(name = "ShouldReturn{1}When{0}Forecasts")
  @MethodSource("parametersForForecasts")
  public void testGetForecasts(List<Forecast> forecasts, HttpStatus status) {
    doReturn(forecasts).when(forecastService).getForecasts(any());

    var response = controller.getForecasts("inv1");

    assertEquals(status, response.getStatusCode());

    verify(forecastService).getForecasts(eq("inv1"));
    verifyNoMoreInteractions(forecastService);
  }

  @Test
  public void testGetForecasts_ShouldReturnForecasts_WhenLoadedForecasts() {
    var forecasts = List.of(newForecast(), newForecast());

    doReturn(forecasts).when(forecastService).getForecasts(any());

    var response = controller.getForecasts("inv1");

    assertEquals(OK, response.getStatusCode());
    assertEquals(forecasts, response.getBody());

    verify(forecastService).getForecasts(eq("inv1"));
    verifyNoMoreInteractions(forecastService);
  }

  @Test
  public void testCreateForecast_ShouldReturnInternalServerError_WhenNullForecast() {
    var forecast = newForecast();

    doReturn(null).when(forecastService).createForecast(any(), any());

    var response = controller.createForecast("inv1", forecast);

    assertEquals(INTERNAL_SERVER_ERROR, response.getStatusCode());

    verify(forecastService).createForecast(eq("inv1"), eq(forecast));
    verifyNoMoreInteractions(forecastService);
  }

  @Test
  public void testCreateForecast_ShouldReturnCreatedForecast_WhenForecastCreated() {
    var forecast = newForecast();

    doReturn(forecast).when(forecastService).createForecast(any(), any());

    var response = controller.createForecast("inv1", forecast);

    assertEquals(CREATED, response.getStatusCode());
    assertEquals(forecast, response.getBody());

    verify(forecastService).createForecast(eq("inv1"), eq(forecast));
    verifyNoMoreInteractions(forecastService);
  }

  @Test
  public void testUpdateForecast_ShouldReturnInternalServerError_WhenNullForecast() {
    var forecast = newForecast();

    doReturn(null).when(forecastService).updateForecast(any(), any());

    var response = controller.updateForecast("1", forecast);

    assertEquals(INTERNAL_SERVER_ERROR, response.getStatusCode());

    verify(forecastService).updateForecast(eq("1"), eq(forecast));
    verifyNoMoreInteractions(forecastService);
  }

  @Test
  public void testUpdateForecast_ShouldReturnUpdatedForecast_WhenForecastUpdated() {
    var forecast = newForecast();

    doReturn(forecast).when(forecastService).updateForecast(any(), any());

    var response = controller.updateForecast("1", forecast);

    assertEquals(OK, response.getStatusCode());
    assertEquals(forecast, response.getBody());

    verify(forecastService).updateForecast(eq("1"), eq(forecast));
    verifyNoMoreInteractions(forecastService);
  }

  @Test
  public void testDeleteForecast_ShouldAnswerNoContent_WhenForecastNotFound() {
    doReturn(null).when(forecastService).deleteForecast(any());

    var response = controller.deleteForecast("1");

    assertEquals(NO_CONTENT, response.getStatusCode());

    verify(forecastService).deleteForecast(eq("1"));
    verifyNoMoreInteractions(forecastService);
  }

  @Test
  public void testDeleteForecast_ShouldReturnDeletedForecast_WhenForecastDeleted() {
    var forecast = newForecast();

    doReturn(forecast).when(forecastService).deleteForecast(any());

    var response = controller.deleteForecast("1");

    assertEquals(OK, response.getStatusCode());
    assertEquals(forecast, response.getBody());

    verify(forecastService).deleteForecast(eq("1"));
    verifyNoMoreInteractions(forecastService);
  }

  private Forecast newForecast() {
    EnumMap<ForecastScenario, Double> rates = new EnumMap<>(ForecastScenario.class);
    rates.put(ForecastScenario.PESSIMIST, 0.01);
    rates.put(ForecastScenario.NEUTRAL, 0.03);
    rates.put(ForecastScenario.OPTIMIST, 0.05);
    return Forecast.builder()
        .investmentId("inv1")
        .name("Test Forecast")
        .startDate(LocalDate.of(2024, 1, 1))
        .endDate(LocalDate.of(2024, 12, 31))
        .scenarioRates(rates)
        .createdAt(LocalDate.now())
        .updatedAt(LocalDate.now())
        .build();
  }
}
