package com.investTrack.api.google;

import com.investTrack.model.Investment;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class GoogleSheetsAdapter {

  // Google Sheets date time format
  // TODO (Marc. A): Extract to a common class with the InvestmentAdapter
  private static final DateTimeFormatter DATE_FORMATTER =
      DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

  public List<Object> toSheetValueRange(Investment investment) {
    log.debug("Converting to sheet value range investment {}", investment);
    var startDateTime = parseDateTime(investment.getStartDateTime());
    var endDateTime = parseDateTime(investment.getEndDateTime());

    return List.of(
        investment.getId(),
        investment.getName(),
        investment.getDescription(),
        investment.getCurrency(),
        startDateTime,
        endDateTime,
        investment.isActive(),
        investment.isReinvested(),
        investment.getInitialInvestedAmount(),
        investment.getReinvestedAmount(),
        investment.getTotalInvestedAmount(),
        investment.getProfitability(),
        investment.getTotalObtained(),
        investment.getTotalBenefit(),
        investment.getBenefitFromInitialAmount(),
        investment.getProfitabilityFromInitialAmount());
  }

  private String parseDateTime(LocalDateTime dateTime) {
    if (dateTime == null) {
      return "";
    }
    return dateTime.format(DATE_FORMATTER);
  }
}
