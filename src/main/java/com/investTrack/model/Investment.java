package com.investTrack.model;

import static jakarta.persistence.GenerationType.IDENTITY;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Data;

@Entity
@Data
@Builder
public class Investment {
  @Id
  @GeneratedValue(strategy = IDENTITY)
  private Long id;

  @Column(nullable = false)
  private String name;

  @Column(length = 500)
  private String description;

  @Column(length = 3)
  private String currency; // TODO (Marc. A): Create currency enum

  @Column(name = "start_datetime", nullable = false)
  private LocalDateTime startDateTime;

  @Column(name = "end_datetime")
  private LocalDateTime endDateTime;

  @Column(name = "is_active", nullable = false)
  private boolean isActive;

  @Column(name = "is_reinvested", nullable = false)
  private boolean isReinvested;

  @Column private double initialInvestedAmount;

  @Column private double reinvestedAmount;

  @Column private double totalInvestedAmount;

  @Column private double profitability;

  @Column private double totalObtained;

  @Column private double totalBenefit;

  @Column private double benefitFromInitialAmount;

  @Column private double profitabilityFromInitialAmount;
}
