package com.investTrack.model;

import static jakarta.persistence.GenerationType.IDENTITY;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "investment_entries")
public class InvestmentEntry {

  @Id
  @GeneratedValue(strategy = IDENTITY)
  private Integer id;

  @Column(length = 500)
  private String comments;

  @ManyToOne
  @JoinColumn(name = "investment_id", nullable = false)
  private Investment investment;

  @Column(name = "entry_datetime")
  private LocalDateTime entryDatetime;

  @Column(name = "initial_investment_amount", precision = 10, scale = 2)
  private BigDecimal initialInvestmentAmount;

  @Column(name = "reinvested_amount", precision = 10, scale = 2)
  private BigDecimal reinvestedAmount;

  @Column(name = "return_rate", precision = 5, scale = 2)
  private BigDecimal returnRate;

  @Column(name = "total_value", precision = 10, scale = 2)
  private BigDecimal totalValue;
}
