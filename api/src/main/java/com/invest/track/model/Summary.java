package com.invest.track.model;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class Summary {
  private final double investedAmount;
  private final double obtained;
  private final double benefit;
  private final double profitability;

  private final double initialInvestedAmount;
  private final double initialObtained;
  private final double initialBenefit;
  private final double initialProfitability;
}
