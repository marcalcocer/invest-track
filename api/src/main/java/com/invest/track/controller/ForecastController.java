package com.invest.track.controller;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

import com.invest.track.model.Forecast;
import com.invest.track.service.ForecastService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/forecasts")
public class ForecastController {
  @Autowired private ForecastService forecastService;

  @GetMapping("/{investmentId}")
  public ResponseEntity<List<Forecast>> getForecasts(@PathVariable String investmentId) {
    var forecasts = forecastService.getForecasts(investmentId);
    if (forecasts == null) {
      return ResponseEntity.internalServerError().build();
    }
    if (forecasts.isEmpty()) {
      return ResponseEntity.noContent().build();
    }
    return new ResponseEntity<>(forecasts, OK);
  }

  @PostMapping("/{investmentId}")
  public ResponseEntity<Forecast> createForecast(
      @PathVariable String investmentId, @RequestBody Forecast forecast) {
    var saved = forecastService.createForecast(investmentId, forecast);
    if (saved == null) {
      return ResponseEntity.internalServerError().build();
    }
    return new ResponseEntity<>(saved, CREATED);
  }

  @PutMapping("/{forecastId}")
  public ResponseEntity<Forecast> updateForecast(
      @PathVariable String forecastId, @RequestBody Forecast forecast) {
    var updated = forecastService.updateForecast(forecastId, forecast);
    if (updated == null) {
      return ResponseEntity.internalServerError().build();
    }
    return new ResponseEntity<>(updated, OK);
  }

  @DeleteMapping("/{forecastId}")
  public ResponseEntity<Forecast> deleteForecast(@PathVariable String forecastId) {
    var deleted = forecastService.deleteForecast(forecastId);
    if (deleted == null) {
      return ResponseEntity.noContent().build();
    }
    return new ResponseEntity<>(deleted, OK);
  }
}
