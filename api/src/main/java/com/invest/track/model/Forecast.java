package com.invest.track.model;

import static lombok.AccessLevel.PRIVATE;

import com.fasterxml.jackson.annotation.JsonBackReference;
import java.time.LocalDate;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor(access = PRIVATE)
@AllArgsConstructor
@Builder
public class Forecast {
  private Long id;
  private String name;
  private LocalDate startDate;
  private LocalDate endDate;
  private Map<ForecastScenario, Double> scenarioRates;

  @JsonBackReference private Investment investment;

  public enum ForecastScenario {
    PESSIMIST,
    NEUTRAL,
    OPTIMIST
  }
}
