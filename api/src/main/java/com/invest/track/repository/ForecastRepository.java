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

  public void save(Forecast forecast) {
    if (forecast.getId() == null) {
      long nextId = storage.keySet().stream().mapToLong(Long::longValue).max().orElse(0L) + 1;

      idGenerator.set(nextId);
      long newId = idGenerator.getAndIncrement();
      forecast.setId(newId);
      log.debug("Assigned new ID {} to forecast", newId);
    }
    storage.put(forecast.getId(), forecast);
    log.debug("Saved forecast with ID {}", forecast.getId());
  }

  public void saveAll(List<Forecast> forecasts) {
    log.debug("Saving {} forecasts", forecasts.size());
    for (Forecast f : forecasts) {
      save(f);
    }
  }

  public void update(Forecast forecast) {
    if (forecast.getId() == null) return;
    storage.put(forecast.getId(), forecast);
    log.debug("Updated forecast with ID {}", forecast.getId());
  }

  public void delete(Forecast forecast) {
    log.debug("Deleting forecast with ID {}", forecast.getId());
    storage.remove(forecast.getId());
  }
}
