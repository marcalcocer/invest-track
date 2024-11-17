package com.investTrack.repository;

import com.investTrack.model.InvestmentEntry;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InvestmentEntryRepository extends JpaRepository<InvestmentEntry, Integer> {
  // Custom query methods (if needed) can be added here
}
