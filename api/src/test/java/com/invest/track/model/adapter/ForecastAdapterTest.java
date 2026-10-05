package com.invest.track.model.adapter;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.invest.track.model.Forecast;
import com.invest.track.model.Forecast.ForecastScenario;
import com.invest.track.model.Investment;
import java.time.LocalDate;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ForecastAdapterTest {

  private ForecastAdapter forecastAdapter;
  private Investment investment;

  @BeforeEach
  void setUp() {
    forecastAdapter = new ForecastAdapter(new AdapterUtils());
    investment = Investment.builder().id(1L).name("Nordic Shield").build();
  }

  @Test
  void shouldReturnNullWhenTheRowIsEmpty() {
    assertNull(forecastAdapter.fromSheetValueRange(List.of(), List.of(investment)));
  }

  @Test
  void shouldParseAForecastRowFromTheSheet() {
    var row =
        List.<Object>of(
            "3",
            "1",
            "Estrategia 2026 v2: Nordic Shield",
            "2026-02-23",
            "2027-02-23",
            "PESSIMIST:0.287,NEUTRAL:0.471,OPTIMIST:0.604",
            "250");

    var forecast = forecastAdapter.fromSheetValueRange(row, List.of(investment));

    assertThat(forecast.getId(), is(3L));
    assertThat(forecast.getName(), is("Estrategia 2026 v2: Nordic Shield"));
    assertThat(forecast.getStartDate(), is(LocalDate.of(2026, 2, 23)));
    assertThat(forecast.getEndDate(), is(LocalDate.of(2027, 2, 23)));
    assertThat(forecast.getInvestment(), is(investment));
    assertThat(forecast.getScenarioRates().get(ForecastScenario.PESSIMIST), is(0.287));
    assertThat(forecast.getScenarioRates().get(ForecastScenario.NEUTRAL), is(0.471));
    assertThat(forecast.getScenarioRates().get(ForecastScenario.OPTIMIST), is(0.604));
    assertThat(forecast.getMonthlyContribution(), is(250.0));
  }

  @Test
  void shouldDefaultTheMonthlyContributionToZeroWhenTheColumnIsMissing() {
    var row =
        List.<Object>of(
            "3",
            "1",
            "Nordic Shield",
            "2026-02-23",
            "2027-02-23",
            "PESSIMIST:0.287,NEUTRAL:0.471,OPTIMIST:0.604");

    var forecast = forecastAdapter.fromSheetValueRange(row, List.of(investment));

    assertThat(forecast.getMonthlyContribution(), is(0.0));
  }

  @Test
  void shouldDefaultTheMonthlyContributionToZeroWhenTheCellIsBlank() {
    var forecast =
        forecastAdapter.fromSheetValueRange(rowWithContribution(""), List.of(investment));

    assertThat(forecast.getMonthlyContribution(), is(0.0));
  }

  @Test
  void shouldDefaultTheMonthlyContributionToZeroWhenTheCellIsNotANumber() {
    var forecast =
        forecastAdapter.fromSheetValueRange(rowWithContribution("n/a"), List.of(investment));

    assertThat(forecast.getMonthlyContribution(), is(0.0));
  }

  @Test
  void shouldParseTheMonthlyContributionUsingACommaAsDecimalSeparator() {
    var forecast =
        forecastAdapter.fromSheetValueRange(rowWithContribution("1234,56"), List.of(investment));

    assertThat(forecast.getMonthlyContribution(), is(1234.56));
  }

  @Test
  void shouldFillEveryMissingScenarioRateWithZero() {
    var row =
        List.<Object>of(
            "3", "1", "Nordic Shield", "2026-02-23", "2027-02-23", "NEUTRAL:0.471", "0");

    var rates = forecastAdapter.fromSheetValueRange(row, List.of(investment)).getScenarioRates();

    assertThat(rates.size(), is(3));
    assertThat(rates.keySet(), containsInAnyOrder(ForecastScenario.values()));
    assertThat(rates.get(ForecastScenario.PESSIMIST), is(0.0));
    assertThat(rates.get(ForecastScenario.OPTIMIST), is(0.0));
  }

  @Test
  void shouldIgnoreUnparseableScenarioRates() {
    var row =
        List.<Object>of(
            "3", "1", "Nordic Shield", "2026-02-23", "2027-02-23", "WRONG:1,NEUTRAL:0.471", "0");

    var rates = forecastAdapter.fromSheetValueRange(row, List.of(investment)).getScenarioRates();

    assertThat(rates.get(ForecastScenario.NEUTRAL), is(0.471));
    assertThat(rates.get(ForecastScenario.PESSIMIST), is(0.0));
  }

  @Test
  void shouldSerializeAForecastToTheSheet() {
    var forecast = sampleForecast().build();

    var row = forecastAdapter.toSheetValueRange(forecast);

    assertThat(row.size(), is(7));
    assertEquals(3L, row.get(0));
    assertEquals(1L, row.get(1));
    assertEquals("Nordic Shield", row.get(2));
    assertEquals("2026-02-23", row.get(3));
    assertEquals("2027-02-23", row.get(4));
    assertEquals(250.0, row.get(6));
  }

  @Test
  void shouldSerializeAMissingMonthlyContributionAsZero() {
    var forecast = sampleForecast().monthlyContribution(null).build();

    var row = forecastAdapter.toSheetValueRange(forecast);

    assertEquals(0.0, row.get(6));
  }

  @Test
  void shouldRoundTripAForecastThroughTheSheetFormat() {
    var original = sampleForecast().build();

    var roundTripped =
        forecastAdapter.fromSheetValueRange(
            forecastAdapter.toSheetValueRange(original), List.of(investment));

    assertEquals(original.getName(), roundTripped.getName());
    assertEquals(original.getStartDate(), roundTripped.getStartDate());
    assertEquals(original.getEndDate(), roundTripped.getEndDate());
    assertEquals(original.getMonthlyContribution(), roundTripped.getMonthlyContribution());
    assertEquals(original.getScenarioRates(), roundTripped.getScenarioRates());
  }

  private List<Object> rowWithContribution(String contribution) {
    return List.<Object>of(
        "3",
        "1",
        "Nordic Shield",
        "2026-02-23",
        "2027-02-23",
        "PESSIMIST:0.287,NEUTRAL:0.471,OPTIMIST:0.604",
        contribution);
  }

  private Forecast.ForecastBuilder sampleForecast() {
    Map<ForecastScenario, Double> rates = new EnumMap<>(ForecastScenario.class);
    rates.put(ForecastScenario.PESSIMIST, 0.287);
    rates.put(ForecastScenario.NEUTRAL, 0.471);
    rates.put(ForecastScenario.OPTIMIST, 0.604);
    return Forecast.builder()
        .id(3L)
        .investment(investment)
        .name("Nordic Shield")
        .startDate(LocalDate.of(2026, 2, 23))
        .endDate(LocalDate.of(2027, 2, 23))
        .scenarioRates(rates)
        .monthlyContribution(250.0);
  }
}
