package com.invest.track.model.adapter;

import static java.time.format.DateTimeFormatter.ofPattern;

import java.time.DateTimeException;
import java.time.LocalDateTime;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AdapterUtils {
  // Google Sheets date time format
  // TODO (Marc. A): Extract to a common class with the GoogleSheetsAdapter
  private static final List<String> DATE_FORMATTERS =
      List.of(
          "dd/MM/yyyy HH:mm:ss", "dd/MM/yyyy H:mm:ss", "d/MM/yyyy HH:mm:ss", "d/MM/yyyy H:mm:ss");

  public Long parseLong(Object value) {
    var str = parseString(value);
    try {
      return Long.parseLong(str);
    } catch (NumberFormatException e) {
      log.error("Failed to parse long value \"{}\"", str);
      throw e;
    }
  }

  public Double parseCurrencyDouble(Object value) {
    var str = parseString(value);

    // E.g. "3.147,21 €" will become 3147.21
    var sanitizedCurrency = str.replace(".", "").replace("€", "");
    var sanitizedDouble = sanitizeDouble(sanitizedCurrency);

    try {
      return Double.parseDouble(sanitizedDouble);
    } catch (NumberFormatException e) {
      log.error("Failed to parse currency double value \"{}\"", sanitizedDouble);
      throw e;
    }
  }

  public Double parsePercentageDouble(Object value) {
    var str = parseString(value);
    var absolutePercentage = true;

    if (str.contains("%")) {
      str = str.replace("%", "");
      absolutePercentage = false;
    }

    var sanitizedDouble = sanitizeDouble(str);

    try {
      var percentage = Double.parseDouble(sanitizedDouble);
      return absolutePercentage ? percentage : percentage / 100;
    } catch (NumberFormatException e) {
      log.error("Failed to parse percentage double value \"{}\"", sanitizedDouble);
      throw e;
    }
  }

  private String sanitizeDouble(String str) {
    return str.replace(",", ".");
  }

  public LocalDateTime parseDateTime(Object value) {
    var str = parseString(value);
    if (str.isEmpty()) {
      return null;
    }
    for (var formatter : DATE_FORMATTERS) {
      try {
        return LocalDateTime.parse(str, ofPattern(formatter));
      } catch (DateTimeException e) {
        log.trace("Failed to parse date time value \"{}\" with formatter \"{}\"", str, formatter);
      }
    }
    throw new DateTimeException(
        "Failed to parse date time value \"" + str + "\" with any formatter");
  }

  public Boolean parseBoolean(Object value) {
    var str = parseString(value);
    return Boolean.parseBoolean(str);
  }

  public String parseString(Object value) {
    return value == null ? "" : value.toString();
  }

  public Object mandatoryField(Object value) {
    if (value == null || value.toString().isEmpty()) {
      throw new IllegalArgumentException("Mandatory field is missing");
    }
    return value;
  }
}
