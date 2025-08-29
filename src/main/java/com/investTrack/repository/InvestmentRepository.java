package com.investTrack.repository;

import com.investTrack.model.Investment;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

@Repository
@Slf4j
public class InvestmentRepository {
  private final Map<Long, Investment> storage = new HashMap<>();
  private final AtomicLong idGenerator = new AtomicLong(1);

  public List<Investment> findAll() {
    log.debug("Retrieving all investments, total count: {}", storage.size());
    return new ArrayList<>(storage.values());
  }

  public void save(Investment investment) {
    if (investment.getId() == null) {
      long newId = idGenerator.getAndIncrement();
      investment.setId(newId);
      log.debug("Assigned new ID {} to investment", newId);
    }
    storage.put(investment.getId(), investment);
    log.debug("Saved investment with ID {}", investment.getId());
  }

  public void saveAll(List<Investment> investments) {
    log.debug("Saving {} investments", investments.size());
    for (Investment inv : investments) {
      save(inv);
    }
  }

  public void delete(Investment investment) {
    log.debug("Deleting investment with ID {}", investment.getId());
    storage.remove(investment.getId());
  }
}
