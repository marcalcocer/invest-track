package com.invest.track.service;

import com.invest.track.model.Forecast;
import com.invest.track.repository.ForecastRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ForecastService {
  private final ForecastRepository forecastRepository;

  public List<Forecast> getForecasts(String investmentId) {
    return forecastRepository.findByInvestmentId(investmentId);
  }

  public Forecast createForecast(String investmentId, Forecast forecast) {
    forecast.setInvestmentId(investmentId);
    // TODO: Add validation and scenario calculations here
    return forecastRepository.save(forecast);
  }

  public Forecast updateForecast(String forecastId, Forecast forecast) {
    forecast.setId(forecastId);
    // TODO: Add validation and scenario calculations here
    return forecastRepository.update(forecast);
  }

  public Forecast deleteForecast(String forecastId) {
    var deleted = forecastRepository.findById(Long.valueOf(forecastId));
    if (deleted != null) {
      forecastRepository.delete(Long.valueOf(forecastId));
    }
    return deleted;
  }
}
