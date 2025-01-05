package com.investTrack.api.google;

import com.investTrack.model.InvestmentEntry;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class GoogleSheetsInvestmentEntryAdapter {

  // Google Sheets date time format
  // TODO (Marc. A): Extract to a common class
  private static final DateTimeFormatter DATE_FORMATTER =
      DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

  public List<Object> toSheetValueRange(InvestmentEntry entry) {
    log.trace("Converting to sheet value range investment entry {}", entry);
    var dateTime = parseDateTime(entry.getDatetime());

    return List.of(
        entry.getId(),
        dateTime,
        entry.getInitialInvestedAmount(),
        entry.getReinvestedAmount(),
        entry.getProfitability());
  }

  private String parseDateTime(LocalDateTime dateTime) {
    return dateTime.format(DATE_FORMATTER);
  }
}
