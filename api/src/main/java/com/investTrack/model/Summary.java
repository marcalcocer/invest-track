package com.investTrack.model;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class Summary {
  private final double investedAmount;
  private final double obtained;
  private final double benefit;
  private final double profitability;
}
