package com.invest.track.model.adapter;

import static com.fasterxml.jackson.databind.util.ClassUtil.name;

import com.invest.track.model.Forecast;
import com.invest.track.model.Investment;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class ForecastAdapter {
  private final AdapterUtils adapterUtils;

  public Forecast fromSheetValueRange(List<Object> valueRange, List<Investment> investments) {
    if (valueRange.isEmpty()) return null;

    var id = adapterUtils.parseLong(valueRange.get(0));
    var investmentId = adapterUtils.parseLong(valueRange.get(1));
    var name = adapterUtils.parseString(valueRange.get(2));
    var startDate = adapterUtils.parseLocalDate(valueRange.get(3));
    var endDate = adapterUtils.parseLocalDate(valueRange.get(4));
    var scenarioRates = parseScenarioRates(adapterUtils.parseString(valueRange.get(5)));
    return Forecast.builder()
        .id(id)
        .investment(
            investments.stream()
                .filter(inv -> inv.getId() != null && inv.getId().equals(investmentId))
                .findFirst()
                .orElse(null))
        .name(name)
        .startDate(startDate)
        .endDate(endDate)
        .scenarioRates(scenarioRates)
        .build();
  }

  public List<Object> toSheetValueRange(Forecast forecast) {
    return List.of(
        forecast.getId() != null ? forecast.getId() : "",
        forecast.getInvestment() != null ? forecast.getInvestment().getId() : "",
        forecast.getName() != null ? forecast.getName() : "",
        adapterUtils.formatLocalDate(forecast.getStartDate()),
        adapterUtils.formatLocalDate(forecast.getEndDate()),
        formatScenarioRates(forecast.getScenarioRates()));
  }

  private Map<Forecast.ForecastScenario, Double> parseScenarioRates(String str) {
    var map = new HashMap<Forecast.ForecastScenario, Double>();
    if (str != null && !str.isEmpty()) {
      var pairs = str.split(",");
      for (var pair : pairs) {
        var kv = pair.split(":");
        if (kv.length == 2) {
          try {
            var scenario = Forecast.ForecastScenario.valueOf(kv[0].trim());
            var rate = Double.parseDouble(kv[1].trim());
            map.put(scenario, rate);
          } catch (Exception e) {
            log.warn("Failed to parse scenario rate: {}", pair);
          }
        }
      }
    }
    // Ensure all scenarios are present with default 0
    for (var scenario : Forecast.ForecastScenario.values()) {
      map.putIfAbsent(scenario, 0.0);
    }
    return map;
  }

  private String formatScenarioRates(Map<Forecast.ForecastScenario, Double> map) {
    if (map == null || map.isEmpty()) return "";
    var sb = new StringBuilder();
    for (var entry : map.entrySet()) {
      if (!sb.isEmpty()) sb.append(",");
      sb.append(entry.getKey().name()).append(":").append(entry.getValue());
    }
    return sb.toString();
  }
}
