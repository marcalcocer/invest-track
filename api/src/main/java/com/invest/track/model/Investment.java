package com.invest.track.model;

import static lombok.AccessLevel.PRIVATE;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString(exclude = "entries") // Prevents infinite recursion when serializing to JSON
@Builder
@NoArgsConstructor(access = PRIVATE)
@AllArgsConstructor
public class Investment {

  public Investment(
      Long id,
      String name,
      String description,
      String currency,
      LocalDateTime startDateTime,
      LocalDateTime endDateTime,
      boolean isReinvested) {
    this.id = id;
    this.name = name;
    this.description = description;
    this.currency = currency;
    this.startDateTime = startDateTime;
    this.endDateTime = endDateTime;
    this.isReinvested = isReinvested;
  }

  private Long id;
  private String name;
  private String description;
  private String currency; // TODO (Marc. A): Create currency enum
  private LocalDateTime startDateTime;
  private LocalDateTime endDateTime;
  private boolean isReinvested;

  @JsonManagedReference private List<InvestmentEntry> entries;

  public boolean isActive() {
    return endDateTime == null || endDateTime.isAfter(LocalDateTime.now());
  }

  public InvestmentEntry getLastEntry() {
    if (entries == null || entries.isEmpty()) {
      return null;
    }
    return entries.get(entries.size() - 1);
  }
}
