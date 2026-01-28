package com.invest.track.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import com.invest.track.model.Forecast;
import com.invest.track.model.Forecast.ForecastScenario;
import com.invest.track.repository.ForecastRepository;
import java.time.LocalDate;
import java.util.EnumMap;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class ForecastServiceTest {

  @Mock private ForecastRepository forecastRepository;

  @InjectMocks private ForecastService forecastService;

  @Test
  public void testGetForecasts_ShouldReturnForecasts() {
    var forecasts = List.of(sampleForecast());
    doReturn(forecasts).when(forecastRepository).findByInvestmentId(eq("inv1"));

    var result = forecastService.getForecasts("inv1");

    assertEquals(forecasts, result);
    verify(forecastRepository).findByInvestmentId(eq("inv1"));
    verifyNoMoreInteractions(forecastRepository);
  }

  @Test
  public void testCreateForecast_ShouldSaveAndReturnForecast() {
    var forecast = sampleForecast();
    doReturn(forecast).when(forecastRepository).save(any());

    var result = forecastService.createForecast("inv1", forecast);

    assertEquals(forecast, result);
    verify(forecastRepository).save(any());
    verifyNoMoreInteractions(forecastRepository);
  }

  @Test
  public void testUpdateForecast_ShouldUpdateAndReturnForecast() {
    var forecast = sampleForecast();
    doReturn(forecast).when(forecastRepository).update(any());

    var result = forecastService.updateForecast("1", forecast);

    assertEquals(forecast, result);
    verify(forecastRepository).update(any());
    verifyNoMoreInteractions(forecastRepository);
  }

  @Test
  public void testDeleteForecast_ShouldDeleteAndReturnForecast() {
    var forecast = sampleForecast();
    doReturn(forecast).when(forecastRepository).findById(eq(1L));

    var result = forecastService.deleteForecast("1");

    assertEquals(forecast, result);
    verify(forecastRepository).findById(eq(1L));
    verify(forecastRepository).delete(eq(1L));
    verifyNoMoreInteractions(forecastRepository);
  }

  @Test
  public void testDeleteForecast_ShouldReturnNull_WhenNotFound() {
    doReturn(null).when(forecastRepository).findById(eq(1L));

    var result = forecastService.deleteForecast("1");

    assertNull(result);
    verify(forecastRepository).findById(eq(1L));
    verifyNoMoreInteractions(forecastRepository);
  }

  private Forecast sampleForecast() {
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
