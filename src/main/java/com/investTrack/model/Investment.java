package com.investTrack.model;

import static jakarta.persistence.GenerationType.IDENTITY;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.hibernate.annotations.ColumnDefault;

@Entity(name = "investments")
@Table(name = "investments")
@Data
@Builder
@AllArgsConstructor
public class Investment {

  public Investment() {
    // Default constructor required by JPA
  }

  @Id
  @GeneratedValue(strategy = IDENTITY)
  private Long id;

  @Column(nullable = false, unique = true)
  private String name;

  @Column(length = 500)
  private String description;

  @Column(name = "currency", length = 3, nullable = false)
  private String currency; // TODO (Marc. A): Create currency enum

  @Column(name = "start_datetime", nullable = false)
  private LocalDateTime startDateTime;

  @Column(name = "end_datetime")
  private LocalDateTime endDateTime;

  @Column(name = "is_active", nullable = false)
  @ColumnDefault("true")
  private boolean isActive;

  @Column(name = "is_reinvested", nullable = false)
  @ColumnDefault("false")
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
