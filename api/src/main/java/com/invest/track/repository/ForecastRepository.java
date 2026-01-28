package com.invest.track.repository;

import com.invest.track.model.Forecast;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

@Repository
@Slf4j
public class ForecastRepository {
  private final Map<Long, Forecast> storage = new HashMap<>();
  private final AtomicLong idGenerator = new AtomicLong(1);

  public List<Forecast> findAll() {
    log.debug("Retrieving all forecasts, total count: {}", storage.size());
    return new ArrayList<>(storage.values());
  }

  public List<Forecast> findByInvestmentId(String investmentId) {
    List<Forecast> result = new ArrayList<>();
    for (Forecast forecast : storage.values()) {
      if (forecast.getInvestmentId().equals(investmentId)) {
        result.add(forecast);
      }
    }
    return result;
  }

  public Forecast findById(Long id) {
    return storage.get(id);
  }

  public Forecast save(Forecast forecast) {
    if (forecast.getId() == null) {
      var newId = idGenerator.getAndIncrement();
      forecast.setId(String.valueOf(newId));
      log.debug("Assigned new ID {} to forecast", newId);
    }
    storage.put(Long.valueOf(forecast.getId()), forecast);
    log.debug("Saved forecast with ID {}", forecast.getId());
    return forecast;
  }

  public Forecast update(Forecast forecast) {
    if (forecast.getId() != null && storage.containsKey(Long.valueOf(forecast.getId()))) {
      storage.put(Long.valueOf(forecast.getId()), forecast);
      log.debug("Updated forecast with ID {}", forecast.getId());
      return forecast;
    }
    return null;
  }

  public void delete(Long id) {
    storage.remove(id);
    log.debug("Deleted forecast with ID {}", id);
  }
}
