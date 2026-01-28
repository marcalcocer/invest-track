package com.invest.track.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.invest.track.model.Forecast;
import com.invest.track.model.Forecast.ForecastScenario;
import java.time.LocalDate;
import java.util.EnumMap;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class ForecastRepositoryTest {

  @InjectMocks private ForecastRepository repository;

  @Test
  public void testFindAll_ShouldReturnEmptyList_WhenNoForecasts() {
    List<Forecast> result = repository.findAll();
    assertEquals(0, result.size());
  }

  @Test
  public void testSaveAndFindAll_ShouldReturnForecast() {
    var forecast = buildForecast();

    repository.save(forecast);

    List<Forecast> result = repository.findAll();

    assertEquals(1, result.size());
    assertEquals(forecast.getName(), result.get(0).getName());
  }

  @Test
  public void testFindByInvestmentId_ShouldReturnCorrectForecasts() {
    var forecast1 = buildForecast();
    var forecast2 = buildForecast();

    forecast2.setInvestmentId("inv2");

    var saved1 = repository.save(forecast1);

    assertEquals(forecast1.getName(), saved1.getName());
    assertEquals(forecast1.getInvestmentId(), saved1.getInvestmentId());

    repository.save(forecast2);

    List<Forecast> result = repository.findByInvestmentId("inv1");

    assertEquals(1, result.size());
    assertEquals("inv1", result.get(0).getInvestmentId());
  }

  @Test
  public void testFindById_ShouldReturnForecast() {
    Forecast forecast = buildForecast();
    repository.save(forecast);
    var found = repository.findById(Long.valueOf(forecast.getId()));
    assertNotNull(found);
    assertEquals(forecast.getName(), found.getName());
  }

  @Test
  public void testUpdate_ShouldModifyForecast() {
    Forecast forecast = buildForecast();
    repository.save(forecast);
    forecast.setName("Updated Name");

    repository.update(forecast);

    var updated = repository.findById(Long.valueOf(forecast.getId()));
    assertEquals("Updated Name", updated.getName());
  }

  @Test
  public void testDelete_ShouldRemoveForecast() {
    Forecast forecast = buildForecast();
    repository.save(forecast);

    repository.delete(Long.valueOf(forecast.getId()));

    var deleted = repository.findById(Long.valueOf(forecast.getId()));
    assertNull(deleted);
  }

  private Forecast buildForecast() {
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
