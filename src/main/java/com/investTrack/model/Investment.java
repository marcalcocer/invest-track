package com.investTrack.model;

import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.ColumnDefault;

@Entity
@Table(name = "investments")
@Data
@NoArgsConstructor // Default constructor required by JPA
@ToString(exclude = "entries") // Prevents infinite recursion when serializing to JSON
public class Investment {

  public Investment(
      Long id,
      String name,
      String description,
      String currency,
      LocalDateTime startDateTime,
      LocalDateTime endDateTime,
      boolean isReinvested,
      double initialInvestedAmount,
      double reinvestedAmount,
      double profitability) {
    this.id = id;
    this.name = name;
    this.description = description;
    this.currency = currency;
    this.startDateTime = startDateTime;
    this.endDateTime = endDateTime;
    this.isReinvested = isReinvested;

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

  @Column(nullable = false, unique = true)
  private String name;

  @Column(length = 500)
  private String description;

  @OneToMany(mappedBy = "investment", cascade = CascadeType.ALL, orphanRemoval = true, fetch = LAZY)
  @JsonManagedReference
  private List<InvestmentEntry> entries;

  @Column(length = 3, nullable = false)
  private String currency; // TODO (Marc. A): Create currency enum

  @Column(name = "start_datetime", nullable = false)
  private LocalDateTime startDateTime;

  @Column(name = "end_datetime")
  private LocalDateTime endDateTime;

  @Column(name = "is_reinvested", nullable = false)
  @ColumnDefault("false")
  private boolean isReinvested;

  @Column(name = "initial_investment_amount", nullable = false)
  private double initialInvestedAmount;

  @Column(name = "reinvested_amount", nullable = false)
  private double reinvestedAmount;

  @Column(name = "total_invested_amount", nullable = false)
  private double totalInvestedAmount;

  @Column(nullable = false)
  private double profitability;

  @Column(name = "obtained", nullable = false)
  private double obtained;

  @Column(name = "benefit", nullable = false)
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
