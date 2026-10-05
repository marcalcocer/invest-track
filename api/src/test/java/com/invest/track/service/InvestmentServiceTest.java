package com.invest.track.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.invest.track.api.google.GoogleSheetsForecastService;
import com.invest.track.api.google.GoogleSheetsInvestmentService;
import com.invest.track.model.Forecast;
import com.invest.track.model.Forecast.ForecastScenario;
import com.invest.track.model.Investment;
import com.invest.track.repository.InvestmentRepository;
import java.io.IOException;
import java.time.LocalDate;
import java.util.EnumMap;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class InvestmentServiceTest {

  @Mock private GoogleSheetsInvestmentService googleSheetsService;
  @Mock private GoogleSheetsForecastService googleSheetsForecastService;
  @Mock private SummaryService summaryService;

  @Spy private InvestmentRepository repository = new InvestmentRepository();

  @InjectMocks private InvestmentService investmentService;

  private Investment investment;

  @BeforeEach
  void setUp() {
    investment = Investment.builder().id(1L).name("Nordic Shield").currency("EUR").build();
    repository.save(investment);
  }

  @Test
  void shouldCreateAForecastKeepingTheMonthlyContribution() throws IOException {
    var created = investmentService.createForecast(sampleForecast().build(), 1L);

    assertNotNull(created);
    assertNotNull(created.getId());
    assertEquals("Nordic Shield", created.getName());
    assertEquals(250.0, created.getMonthlyContribution());
    assertEquals(investment, created.getInvestment());
    assertEquals(1, investment.getForecasts().size());
    verify(googleSheetsForecastService).writeForecastsData(any());
  }

  @Test
  void shouldReturnNullWhenCreatingAForecastForAnUnknownInvestment() throws IOException {
    var created = investmentService.createForecast(sampleForecast().build(), 99L);

    assertNull(created);
    verify(googleSheetsForecastService, never()).writeForecastsData(any());
  }

  @Test
  void shouldReturnNullWhenCreatingAForecastCannotBePersisted() throws IOException {
    doThrow(new IOException("boom")).when(googleSheetsForecastService).writeForecastsData(any());

    assertNull(investmentService.createForecast(sampleForecast().build(), 1L));
  }

  @Test
  void shouldUpdateTheMonthlyContributionOfAnExistingForecast() throws IOException {
    var created = investmentService.createForecast(sampleForecast().build(), 1L);
    var update =
        sampleForecast()
            .id(created.getId())
            .name("Nordic Shield v3")
            .monthlyContribution(500.0)
            .build();

    var updated = investmentService.updateForecast(update);

    assertNotNull(updated);
    assertEquals("Nordic Shield v3", updated.getName());
    assertEquals(500.0, updated.getMonthlyContribution());
    assertEquals(0.287, updated.getScenarioRates().get(ForecastScenario.PESSIMIST));
    assertEquals(0.471, updated.getScenarioRates().get(ForecastScenario.NEUTRAL));
    assertEquals(0.604, updated.getScenarioRates().get(ForecastScenario.OPTIMIST));
    assertEquals(investmentService.getForecasts().get(0).getMonthlyContribution(), 500.0);
  }

  @Test
  void shouldKeepANullMonthlyContributionWhenItIsNotProvidedOnUpdate() throws IOException {
    var created = investmentService.createForecast(sampleForecast().build(), 1L);
    var update = sampleForecast().id(created.getId()).monthlyContribution(null).build();

    var updated = investmentService.updateForecast(update);

    assertNull(updated.getMonthlyContribution());
  }

  @Test
  void shouldReturnNullWhenUpdatingAnUnknownForecast() throws IOException {
    var update = sampleForecast().id(99L).build();

    assertNull(investmentService.updateForecast(update));
  }

  @Test
  void shouldListTheForecastsOfEveryInvestment() throws IOException {
    var other = Investment.builder().id(2L).name("Conservative Plus").build();
    repository.save(other);
    investmentService.createForecast(sampleForecast().build(), 1L);
    var otherForecast = sampleForecast().id(null).name("Other").build();
    investmentService.createForecast(otherForecast, 2L);

    var forecasts = investmentService.getForecasts();

    assertEquals(2, forecasts.size());
    assertTrue(forecasts.stream().allMatch(f -> f.getMonthlyContribution() != null));
  }

  @Test
  void shouldDeleteAForecast() throws IOException {
    var created = investmentService.createForecast(sampleForecast().build(), 1L);

    var deleted = investmentService.deleteForecast(created.getId());

    assertNotNull(deleted);
    assertTrue(investment.getForecasts().isEmpty());
    assertTrue(investmentService.getForecasts().isEmpty());
    verify(googleSheetsForecastService, times(2)).writeForecastsData(any());
  }

  @Test
  void shouldReturnNullWhenDeletingAnUnknownForecast() throws IOException {
    assertNull(investmentService.deleteForecast(99L));
  }

  @Test
  void shouldWrapSheetFailuresOnLoad() throws IOException {
    doThrow(new IOException("boom")).when(googleSheetsService).readInvestmentsData();

    assertThrows(RuntimeException.class, () -> investmentService.init());
  }

  private Forecast.ForecastBuilder sampleForecast() {
    Map<ForecastScenario, Double> rates = new EnumMap<>(ForecastScenario.class);
    rates.put(ForecastScenario.PESSIMIST, 0.287);
    rates.put(ForecastScenario.NEUTRAL, 0.471);
    rates.put(ForecastScenario.OPTIMIST, 0.604);
    return Forecast.builder()
        .name("Nordic Shield")
        .startDate(LocalDate.of(2026, 2, 23))
        .endDate(LocalDate.of(2027, 2, 23))
        .scenarioRates(rates)
        .monthlyContribution(250.0);
  }
}
