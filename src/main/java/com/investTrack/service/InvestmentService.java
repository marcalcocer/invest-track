package com.investTrack.service;

import com.investTrack.api.google.GoogleSheetsService;
import com.investTrack.model.Investment;
import com.investTrack.model.InvestmentEntry;
import com.investTrack.model.Summary;
import com.investTrack.repository.InvestmentRepository;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

// Provides application logic for managing investments.

@Slf4j
@Service
public class InvestmentService {
  private final GoogleSheetsService googleSheetsService;
  private final InvestmentRepository repository;
  private final SummaryService summaryService;

  public InvestmentService(
      GoogleSheetsService googleSheetsService,
      InvestmentRepository repository,
      SummaryService summaryService) {
    this.googleSheetsService = googleSheetsService;
    this.repository = repository;
    this.summaryService = summaryService;

    loadInvestments();
  }

  private void loadInvestments() {
    log.info("Loading investments...");
    List<Investment> investments = new ArrayList<>();
    try {
      var loadedInvestments = googleSheetsService.readInvestmentsData();
      log.debug("Loaded {} investments from Google Sheets", loadedInvestments.size());
      investments.addAll(loadedInvestments);

      repository.saveAll(investments);
      log.info("Loaded investments successfully!");
    } catch (Exception e) {
      throw new RuntimeException("Failed to load investments", e);
    }
  }

  public List<Investment> getInvestments() {
    return repository.findAll();
  }

  public Investment createInvestment(Investment investment) {
    List<Investment> investments = getInvestments();
    if (investments.isEmpty()) {
      log.error("Failed to load investments list while creating a new one");
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
      googleSheetsService.writeInvestmentsData(investments);
    } catch (Exception e) {
      log.error(
          "Failed to write investments into Google Sheets while creating an investment due to", e);
      return null;
    }

    return investment;
  }

  public Investment deleteInvestment(Long id) {
    List<Investment> investments = getInvestments();
    if (investments.isEmpty()) {
      log.error("Failed to load investments list while deleting one");
      return null;
    }

    Investment investmentToDelete;
    try {
      investmentToDelete = getInvestment(investments, id);

      if (investmentToDelete == null) {
        log.error("Could not be found investment to be deleted with id {}", id);
        return null;
      }

      log.debug("Deleting investment {} from the database", investmentToDelete);
      repository.delete(investmentToDelete);
      investments.remove(investmentToDelete);

    } catch (Exception e) {
      log.error("Failed to delete investment", e);
      return null;
    }

    try {
      googleSheetsService.writeInvestmentsData(investments);
    } catch (Exception e) {
      log.error(
          "Failed to write investments into Google Sheets while deleting an investment due to", e);
      return null;
    }
    return investmentToDelete;
  }

  public InvestmentEntry createInvestmentEntry(InvestmentEntry entry, Long id) {
    List<Investment> investments = getInvestments();
    if (investments.isEmpty()) {
      log.error("Failed to load investments list while creating an investment entry");
      return null;
    }

    Investment investment = getInvestment(investments, id);
    if (investment == null) {
      log.error("Could not be found investment with id {} when creating an investment entry", id);
      return null;
    }

    log.debug("Creating investment entry {} for investment {}", entry, investment);
    try {
      if (entry.getDatetime() == null) {
        var now = LocalDateTime.now();
        log.debug("Setting current datetime to the entry {}", now);
        entry.setDatetime(now);
      }
      entry.setInvestment(investment);

      investment.getEntries().add(entry);
      saveInvestment(investment);
    } catch (Exception e) {
      log.error("Failed to create investment entry {} due to:", entry, e);
      return null;
    }

    try {
      googleSheetsService.writeInvestmentsData(investments);
    } catch (Exception e) {
      log.error("Failed to write investments into Google Sheets while creating an entry due to", e);
      return null;
    }

    return entry;
  }

  public InvestmentEntry deleteInvestmentEntry(Long investmentId, Long entryId) {
    List<Investment> investments = getInvestments();
    if (investments.isEmpty()) {
      log.error("Failed to load investments list while deleting an investment entry");
      return null;
    }

    Investment investment = getInvestment(investments, investmentId);
    if (investment == null) {
      log.error(
          "Could not be found investment with id {} when deleting an investment entry",
          investmentId);
      return null;
    }

    InvestmentEntry entryToDelete = getInvestmentEntry(investment, entryId);
    if (entryToDelete == null) {
      log.error("Could not be found entry with id {} in investment {}", entryId, investment);
      return null;
    }

    log.info("Deleting investment entry {} from investment {}", entryToDelete, investment);
    try {
      investment.getEntries().remove(entryToDelete);
      saveInvestment(investment);
    } catch (Exception e) {
      log.error("Failed to delete investment entry {} due to:", entryToDelete, e);
      return null;
    }

    try {
      googleSheetsService.writeInvestmentsData(investments);
    } catch (Exception e) {
      log.error("Failed to write investments into Google Sheets while deleting an entry due to", e);
      return null;
    }

    return entryToDelete;
  }

  public Summary getSummary() {
    List<Investment> investments = getInvestments();
    if (investments.isEmpty()) {
      log.error("Failed to load investments list while calculating the summary");
      return null;
    }

    var summary = summaryService.calculateSummary(investments);
    log.info("Calculated summary: {}", summary);
    return summary;
  }

  private void saveInvestment(Investment investment) {
    log.debug("Saving investment into the database: {}", investment);
    repository.save(investment);
    log.trace("Saved investment into the database");
  }

  private Investment getInvestment(List<Investment> investments, Long id) {
    return investments.stream().filter(i -> i.getId().equals(id)).findFirst().orElse(null);
  }

  private InvestmentEntry getInvestmentEntry(Investment investment, Long id) {
    return investment.getEntries().stream()
        .filter(e -> e.getId().equals(id))
        .findFirst()
        .orElse(null);
  }
}
