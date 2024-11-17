package com.investTrack.service;

import com.investTrack.model.Investment;
import com.investTrack.repository.InvestmentRepository;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
@Builder
public class InvestmentService {
  private final InvestmentRepository repository;

  public Investment createInvestment(Investment investment) {
    return repository.save(investment);
  }
}
