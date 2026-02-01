package com.invest.track.controller;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

import com.invest.track.model.Forecast;
import com.invest.track.service.ForecastService;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
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

@Slf4j
@RestController
@RequestMapping("/forecasts")
public class ForecastController {
  @Autowired private ForecastService service;

  @GetMapping
  public ResponseEntity<List<Forecast>> getForecasts() {
    log.info("Get forecasts endpoint called");
    List<Forecast> forecasts = service.getForecasts();

    if (forecasts == null) {
      log.debug("Answering with internal server error to load forecast call");
      return ResponseEntity.internalServerError().build();
    }

    if (forecasts.isEmpty()) {
      log.debug("Answering with no content to load forecast call");
      return ResponseEntity.noContent().build();
    }

    return new ResponseEntity<>(forecasts, OK);
  }

  @PostMapping
  public ResponseEntity<Forecast> createForecast(@RequestBody Forecast forecast) {
    log.info("Create forecast endpoint called");
    var saved = service.createForecast(forecast);

    if (saved == null) {
      return ResponseEntity.internalServerError().build();
    }
    return new ResponseEntity<>(saved, CREATED);
  }

  @PutMapping("/{id}")
  public ResponseEntity<Forecast> updateForecast(
      @PathVariable Long id, @RequestBody Forecast forecast) {
    log.info("Update forecast endpoint called");
    forecast.setId(id);
    var updated = service.updateForecast(forecast);
    if (updated == null) {
      return ResponseEntity.internalServerError().build();
    }
    return new ResponseEntity<>(updated, OK);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Forecast> deleteForecast(@PathVariable Long id) {
    log.info("Delete investment endpoint called");

    var deleted = service.deleteForecast(id);
    if (deleted == null) {
      log.debug("Answering with no content to delete forecast call");
      return ResponseEntity.noContent().build();
    }
    return new ResponseEntity<>(deleted, OK);
  }
}
