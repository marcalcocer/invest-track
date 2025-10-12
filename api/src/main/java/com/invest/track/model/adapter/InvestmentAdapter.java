package com.invest.track.model.adapter;

import com.invest.track.model.Investment;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

// Adapts investment data from Google Sheets to the application

@Slf4j
@RequiredArgsConstructor
public class InvestmentAdapter {
  private final AdapterUtils adapterUtils;

  public Investment fromSheetValueRange(List<Object> valueRange) {
    log.trace("Parsing investment object from value range: {}", valueRange);
    if (valueRange.isEmpty()) {
      log.trace("Empty investment object from value range: {}", valueRange);
      return null;
    }

    var id = adapterUtils.parseLong(valueRange.get(0));
    var name = adapterUtils.parseString(valueRange.get(1));
    var description = adapterUtils.parseString(valueRange.get(2));
    var currency = adapterUtils.parseString(valueRange.get(3));
    var startDateTime = adapterUtils.parseDateTime(valueRange.get(4));
    var endDateTime = adapterUtils.parseDateTime(valueRange.get(5));
    var isReinvested = adapterUtils.parseBoolean(valueRange.get(6));

    return new Investment(
        id, name, description, currency, startDateTime, endDateTime, isReinvested);
  }
}
