package com.investTrack.model;

import static jakarta.persistence.GenerationType.IDENTITY;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Table(name = "investment_entries")
@NoArgsConstructor(force = true) // Default constructor required by JPA
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

  @Id
  @GeneratedValue(strategy = IDENTITY)
  // We explicitly need to declare it here and not in the superclass because JPA required each
  // @Entity to have an @Id
  private Long id;

  @ManyToOne
  @JoinColumn(name = "investment_id", nullable = false)
  @JsonBackReference
  private Investment investment;

  @Column(name = "datetime", nullable = false)
  private LocalDateTime datetime;

  @Column(length = 500)
  private final String comments;

  @Column(name = "initial_investment_amount", nullable = false)
  private double initialInvestedAmount;

  @Column(name = "reinvested_amount", nullable = false)
  private double reinvestedAmount;

  @Column(name = "total_invested_amount")
  private double totalInvestedAmount;

  @Column(nullable = false)
  private double profitability;

  @Column(name = "obtained")
  private double obtained;

  @Column(name = "benefit")
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
