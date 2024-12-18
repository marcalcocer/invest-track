package com.investTrack.model;

import java.time.DateTimeException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

// Adapts investment data between Google Sheets and the application

@Slf4j
public class InvestmentAdapter {

  // Google Sheets date time format
  // TODO (Marc. A): Extract to a common class with the GoogleSheetsAdapter
  private static final DateTimeFormatter DATE_FORMATTER =
      DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

  public Investment fromSheetValueRange(List<Object> investmentValueRange) {
    var id = parseLong(investmentValueRange.get(0));
    var name = parseString(investmentValueRange.get(1));
    var description = parseString(investmentValueRange.get(2));
    var currency = parseString(investmentValueRange.get(3));
    var startDateTime = parseDateTime(investmentValueRange.get(4));
    var endDateTime = parseDateTime(investmentValueRange.get(5));
    var isActive = parseBoolean(investmentValueRange.get(6));
    var isReinvested = parseBoolean(investmentValueRange.get(7));
    var initialInvestedAmount = parseDouble(investmentValueRange.get(8));
    var reinvestedAmount = parseDouble(investmentValueRange.get(9));
    var totalInvestedAmount = parseDouble(investmentValueRange.get(10));
    var profitability = parseDouble(investmentValueRange.get(11));
    var totalObtained = parseDouble(investmentValueRange.get(12));
    var totalBenefit = parseDouble(investmentValueRange.get(13));
    var benefitFromInitialAmount = parseDouble(investmentValueRange.get(14));
    var profitabilityFromInitialAmount = parseDouble(investmentValueRange.get(15));

    return Investment.builder()
        .id(id)
        .name(name)
        .description(description)
        .currency(currency)
        .startDateTime(startDateTime)
        .endDateTime(endDateTime)
        .isActive(isActive)
        .isReinvested(isReinvested)
        .initialInvestedAmount(initialInvestedAmount)
        .reinvestedAmount(reinvestedAmount)
        .totalInvestedAmount(totalInvestedAmount)
        .profitability(profitability)
        .totalObtained(totalObtained)
        .totalBenefit(totalBenefit)
        .benefitFromInitialAmount(benefitFromInitialAmount)
        .profitabilityFromInitialAmount(profitabilityFromInitialAmount)
        .build();
  }

  private Long parseLong(Object value) {
    var str = parseString(value);
    try {
      return Long.parseLong(str);
    } catch (NumberFormatException e) {
      log.error("Failed to parse long value \"{}\"", str);
      throw e;
    }
  }

  private Double parseDouble(Object value) {
    var str = parseString(value);

    // E.g. 3.147,21 will become 3147.21
    var sanitizedStr = str.replace(".", "").replace(",", ".");

    try {
      return Double.parseDouble(sanitizedStr);
    } catch (NumberFormatException e) {
      log.error("Failed to parse double value \"{}\"", sanitizedStr);
      throw e;
    }
  }

  private LocalDateTime parseDateTime(Object value) {
    var str = parseString(value);
    if (str.isEmpty()) {
      return null;
    }
    try {
      return LocalDateTime.parse(str, DATE_FORMATTER);
    } catch (DateTimeException e) {
      log.error("Failed to parse date time value \"{}\"", str);
      throw e;
    }
  }

  private Boolean parseBoolean(Object value) {
    var str = parseString(value);
    return Boolean.parseBoolean(str);
  }

  private String parseString(Object value) {
    return value.toString();
  }
}
