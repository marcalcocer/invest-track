package com.investTrack.repository;

import com.investTrack.model.Investment;
import org.springframework.data.jpa.repository.JpaRepository;

// @Slf4j
public interface InvestmentRepository extends JpaRepository<Investment, Integer> {
  // Custom query methods (if needed) can be added here
}
