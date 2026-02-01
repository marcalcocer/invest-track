package com.invest.track.service;

import com.invest.track.api.google.GoogleSheetsForecastService;
import com.invest.track.model.Forecast;
import com.invest.track.repository.ForecastRepository;
import jakarta.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ForecastService {
  private final ForecastRepository repository;
  private final GoogleSheetsForecastService googleSheetsService;

  @PostConstruct
  public void init() {
    loadForecasts();
  }

  private void loadForecasts() {
    log.info("Loading forecasts...");
    List<Forecast> forecasts = new ArrayList<>();
    try {
      var loadedForecasts = googleSheetsService.readForecastsData();
      log.debug("Loaded {} forecasts from Google Sheets", loadedForecasts.size());
      forecasts.addAll(loadedForecasts);

      repository.saveAll(forecasts);
      log.info("Loaded forecasts successfully!");
    } catch (Exception e) {
      throw new RuntimeException("Failed to load forecasts", e);
    }
  }

  public List<Forecast> getForecasts() {
    return repository.findAll();
  }

  public Forecast createForecast(Forecast forecast) {
    List<Forecast> forecasts = getForecasts();
    if (forecasts == null) {
      log.error("Failed to load forecasts list while creating a new one: forecasts list is null (possible Google Sheets error)");
      forecasts = new ArrayList<>();
    }

    try {
      forecasts.add(forecast);
      repository.save(forecast);
    } catch (Exception e) {
      log.error("Failed to save forecast", e);
      return null;
    }

    try {
      googleSheetsService.writeForecastsData(forecasts);
    } catch (Exception e) {
      log.error("Failed to write forecasts into Google Sheets while creating a forecast due to", e);
      return null;
    }

    return forecast;
  }

  public Forecast deleteForecast(Long id) {
    List<Forecast> forecasts = getForecasts();
    if (forecasts.isEmpty()) {
      log.error("Failed to load forecasts list while deleting one");
      return null;
    }

    Forecast forecastToDelete;
    try {
      forecastToDelete = getForecast(forecasts, id);

      log.debug("Deleting forecast with id {}", id);
      repository.delete(forecastToDelete);
      forecasts.remove(forecastToDelete);

    } catch (Exception e) {
      log.error("Failed to delete forecast", e);
      return null;
    }

    try {
      googleSheetsService.writeForecastsData(forecasts);
    } catch (Exception e) {
      log.error("Failed to write forecasts into Google Sheets while deleting a forecast due to", e);
      return null;
    }
    return forecastToDelete;
  }

  private Forecast getForecast(List<Forecast> forecasts, Long id) {
    return forecasts.stream()
        .filter(forecast -> forecast.getId().equals(id))
        .findFirst()
        .orElse(null);
  }
}
