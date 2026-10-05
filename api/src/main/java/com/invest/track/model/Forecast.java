package com.invest.track.model;

import static lombok.AccessLevel.PRIVATE;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonProperty;
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

  @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
  private Long investmentId;

  @JsonBackReference private Investment investment;

  private LocalDate startDate;
  private LocalDate endDate;

  /** Monthly scenario growth rates expressed as percentages (e.g. 0.5 means 0.5% per month). */
  private Map<ForecastScenario, Double> scenarioRates;

  /** Amount expected to be added to the investment every month during the forecast. */
  private Double monthlyContribution;

  public enum ForecastScenario {
    PESSIMIST,
    NEUTRAL,
    OPTIMIST
  }
}
