package com.investTrack.service;

import com.investTrack.model.InvestmentEntry;
import com.investTrack.repository.InvestmentEntryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class InvestmentEntryService {
  private final InvestmentEntryRepository repository;

  public InvestmentEntry createEntry(InvestmentEntry investment) {
    return repository.save(investment);
  }
}
