package com.investTrack.model;

import static jakarta.persistence.GenerationType.IDENTITY;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Data;

@Entity
@Table(name = "investments")
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

  @Column(name = "platform_name", nullable = false)
  private String platformName;

  @Column(name = "start_datetime", nullable = false)
  private LocalDateTime startDateTime;

  @Column(name = "end_datetime")
  private LocalDateTime endDateTime;

  @Column(name = "is_active", nullable = false)
  private boolean isActive;

  @Column(name = "is_reinvested")
  private boolean isReinvested;

  private String currency;
}
