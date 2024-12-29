package com.investTrack.service;

import com.investTrack.api.google.GoogleSheetsService;
import com.investTrack.model.Investment;
import com.investTrack.repository.InvestmentRepository;
import jakarta.transaction.Transactional;
import java.util.ArrayList;
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
  private boolean areInvestmentsLoaded = false;

  public List<Investment> getInvestments() {
    if (areInvestmentsLoaded) {
      log.debug("Investments already loaded, returning from cache");
      return repository.findAll();
    }
    List<Investment> investments = new ArrayList<>();
    try {
      var loadedInvestments = googleSheetsService.getInvestmentData();
      investments.addAll(loadedInvestments);

      saveInvestments(investments);
      areInvestmentsLoaded = true;
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

  public Investment createInvestment(Investment investment) {
    List<Investment> investments = getInvestments();
    if (investments == null) {
      log.error("Failed to load investments list");
      return null;
    }

    try {
      investments.add(investment);
      repository.save(investment);
    } catch (Exception e) {
      log.error("Failed to save investment", e);
      return null;
    }

    try {
      googleSheetsService.writeInvestmentData(investments);
    } catch (Exception e) {
      log.error("Failed to write investments into Google Sheets due to", e);
      return null;
    }

    return investment;
  }
}
