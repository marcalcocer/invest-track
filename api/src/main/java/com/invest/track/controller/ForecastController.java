package com.invest.track.controller;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

import com.invest.track.model.Forecast;
import com.invest.track.service.InvestmentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/investments/forecast")
public class ForecastController {
  private final InvestmentService investmentService;

  @PostMapping("/{id}")
  public ResponseEntity<Forecast> createForecast(
      @PathVariable Long id, @RequestBody(required = true) Forecast forecast) {
    log.info("Received createForecast request: id={}, forecast={}", id, forecast);

    log.info("Create forecast endpoint called");
    var savedForecast = investmentService.createForecast(forecast, id);
    if (savedForecast == null) {
      log.debug("Answering with internal server error to create forecast call");
      return ResponseEntity.internalServerError().build();
    }
    return new ResponseEntity<>(savedForecast, CREATED);
  }

  @PutMapping("/{investmentId}/{forecastId}")
  public ResponseEntity<Forecast> updateForecast(
      @PathVariable Long investmentId,
      @PathVariable Long forecastId,
      @RequestBody Forecast forecast) {
    log.info("Update forecast endpoint called");
    forecast.setId(forecastId);
    var updatedForecast = investmentService.updateForecast(investmentId, forecast);
    if (updatedForecast == null) {
      return ResponseEntity.internalServerError().build();
    }
    return new ResponseEntity<>(updatedForecast, OK);
  }

  @DeleteMapping("/{investmentId}/{forecastId}")
  public ResponseEntity<Forecast> deleteForecast(
      @PathVariable Long investmentId, @PathVariable Long forecastId) {
    log.info("Delete forecast endpoint called");
    var deletedForecast = investmentService.deleteForecast(investmentId, forecastId);
    if (deletedForecast == null) {
      log.debug("Answering with no content to delete forecast call");
      return ResponseEntity.noContent().build();
    }
    return new ResponseEntity<>(deletedForecast, OK);
  }
}
