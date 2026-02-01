package com.invest.track.model;

import static lombok.AccessLevel.PRIVATE;

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
  private String investmentId;
  private String name;
  private LocalDate startDate;
  private LocalDate endDate;
  private Map<ForecastScenario, Double> scenarioRates;


  public enum ForecastScenario {
    PESSIMIST,
    NEUTRAL,
    OPTIMIST
  }
}
