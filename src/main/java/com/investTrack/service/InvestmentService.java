package com.investTrack.service;

import com.investTrack.api.google.GoogleSheetsService;
import com.investTrack.model.Investment;
import com.investTrack.repository.InvestmentRepository;
import jakarta.transaction.Transactional;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

// Provides application logic for managing investments.

@Slf4j
@RequiredArgsConstructor
@Service
public class InvestmentService {
  private final GoogleSheetsService googleSheetsService;
  private final InvestmentRepository repository;

  public List<Investment> loadInvestments() {
    List<Investment> investments;
    try {
      investments = googleSheetsService.getInvestmentData();
      saveInvestments(investments);
    } catch (Exception e) {
      log.error("Failed to load investments list", e);
      return null;
    }
    return investments;
  }

  @Transactional
  private void saveInvestments(List<Investment> investments) {
    repository.saveAll(investments);
  }
}
