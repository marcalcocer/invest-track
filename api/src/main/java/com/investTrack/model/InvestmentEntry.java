package com.investTrack.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class InvestmentEntry {
  public InvestmentEntry(
      LocalDateTime datetime,
      double initialInvestedAmount,
      double reinvestedAmount,
      double profitability,
      String comments,
      Investment investment) {
    this.datetime = datetime;
    this.investment = investment;
    this.comments = comments;

    // TODO: Treat common behavior in a separated class
    this.initialInvestedAmount = initialInvestedAmount;
    this.reinvestedAmount = reinvestedAmount;
    this.profitability = profitability;

    calculateTotalInvestedAmount();
    calculateObtained();
    calculateBenefit();
  }

  private Long id;
  @JsonBackReference private Investment investment;
  private LocalDateTime datetime;
  private final String comments;
  private double initialInvestedAmount;
  private double reinvestedAmount;
  private double totalInvestedAmount;
  private double profitability;
  private double obtained;
  private double benefit;

  private void calculateTotalInvestedAmount() {
    totalInvestedAmount = initialInvestedAmount + reinvestedAmount;
  }

  private void calculateObtained() {
    obtained = totalInvestedAmount + totalInvestedAmount * profitability;
  }

  private void calculateBenefit() {
    benefit = totalInvestedAmount * profitability;
  }
}
