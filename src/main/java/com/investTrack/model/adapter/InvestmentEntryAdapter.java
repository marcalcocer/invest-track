package com.investTrack.model.adapter;

import com.investTrack.model.Investment;
import com.investTrack.model.InvestmentEntry;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

// Adapts investment entry data from the Google Sheets to the application

@Slf4j
@RequiredArgsConstructor
public class InvestmentEntryAdapter {
  private final AdapterUtils adapterUtils;

  public InvestmentEntry fromSheetValueRange(List<Object> valueRange, Investment investment) {
    log.trace("Parsing investment entry object from value range: {}", valueRange);
    if (valueRange.isEmpty()) {
      log.trace("Empty investment entry object from value range: {}", valueRange);
      return null;
    }

    var id = adapterUtils.parseLong(valueRange.get(0));
    var dateTime = adapterUtils.parseDateTime(valueRange.get(1));
    var initialInvestedAmount = adapterUtils.parseCurrencyDouble(valueRange.get(2));
    var reinvestedAmount = adapterUtils.parseCurrencyDouble(valueRange.get(3));
    var profitability = adapterUtils.parsePercentageDouble(valueRange.get(4));
    var comments = adaptComments(valueRange);

    return new InvestmentEntry(
        (Long) adapterUtils.mandatoryField(id),
        (LocalDateTime) adapterUtils.mandatoryField(dateTime),
        (Double) adapterUtils.mandatoryField(initialInvestedAmount),
        (Double) adapterUtils.mandatoryField(reinvestedAmount),
        (Double) adapterUtils.mandatoryField(profitability),
        investment,
        comments);
  }

  private String adaptComments(List<Object> valueRange) {
    try {
      return adapterUtils.parseString(valueRange.get(5));
    } catch (IndexOutOfBoundsException e) {
      log.trace("No comments found for investment entry");
      return "";
    }
  }
}
