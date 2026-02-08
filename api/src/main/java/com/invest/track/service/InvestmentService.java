package com.invest.track.service;

import com.invest.track.api.google.GoogleSheetsInvestmentService;
import com.invest.track.model.Forecast;
import com.invest.track.model.Investment;
import com.invest.track.model.InvestmentEntry;
import com.invest.track.model.Summary;
import com.invest.track.repository.InvestmentRepository;
import jakarta.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

// Provides application logic for managing investments.

@Slf4j
@Service
@RequiredArgsConstructor
public class InvestmentService {
  private final GoogleSheetsInvestmentService googleSheetsService;
  private final InvestmentRepository repository;
  private final SummaryService summaryService;
  private final AtomicLong entryIdGenerator = new AtomicLong(1);

  @PostConstruct
  public void init() {
    loadInvestments();
  }

  private void loadInvestments() {
    log.info("Loading investments...");
    List<Investment> investments = new ArrayList<>();
    try {
      var loadedInvestments = googleSheetsService.readInvestmentsData();
      log.debug("Loaded {} investments from Google Sheets", loadedInvestments.size());

      // Assign IDs to entries if missing
      for (var inv : loadedInvestments) {
        if (inv.getEntries() != null) {
          for (var entry : inv.getEntries()) {
            if (entry.getId() == null) {
              entry.setId(entryIdGenerator.getAndIncrement());
            }
          }
        }
      }
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

  public Investment updateInvestment(Long id, Investment investment) {
    investment.setId(id);
    repository.save(investment);
    List<Investment> investments = getInvestments();
    try {
      googleSheetsService.writeInvestmentsData(investments);
    } catch (Exception e) {
      log.error(
          "Failed to write investments into Google Sheets while updating an investment due to", e);
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
    log.debug("Creating investment entry {} for investment {}", entry, investment);

    try {
      if (entry.getId() == null) {
        entry.setId(entryIdGenerator.getAndIncrement());
      }
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

  public InvestmentEntry updateInvestmentEntry(Long investmentId, InvestmentEntry entry) {
    List<Investment> investments = getInvestments();
    if (investments.isEmpty()) {
      log.error("Failed to load investments list while updating an investment entry");
      return null;
    }
    Investment investment = getInvestment(investments, investmentId);
    InvestmentEntry existingEntry = getInvestmentEntry(investment, entry.getId());

    existingEntry.setDatetime(entry.getDatetime());
    existingEntry.setComments(entry.getComments());
    existingEntry.setInitialInvestedAmount(entry.getInitialInvestedAmount());
    existingEntry.setReinvestedAmount(entry.getReinvestedAmount());
    existingEntry.setProfitability(entry.getProfitability());

    // Recalculate derived fields
    saveInvestment(investment);

    try {
      googleSheetsService.writeInvestmentsData(investments);
    } catch (Exception e) {
      log.error("Failed to write investments into Google Sheets while updating an entry due to", e);
      return null;
    }
    return existingEntry;
  }

  public InvestmentEntry deleteInvestmentEntry(Long investmentId, Long entryId) {
    List<Investment> investments = getInvestments();
    if (investments.isEmpty()) {
      log.error("Failed to load investments list while deleting an investment entry");
      return null;
    }

    Investment investment = getInvestment(investments, investmentId);
    InvestmentEntry entryToDelete = getInvestmentEntry(investment, entryId);
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

  public Forecast createForecast(Forecast forecast, Long id) {
    List<Investment> investments = getInvestments();
    if (investments.isEmpty()) {
      log.error("Failed to load investments list while creating a forecast");
      return null;
    }
    Investment investment = getInvestment(investments, id);
    log.debug("Creating forecast {} for investment {}", forecast, investment);
    try {
      if (forecast.getId() == null) {
        forecast.setId(System.currentTimeMillis());
      }
      forecast.setInvestment(investment);
      investment.getForecasts().add(forecast);
      saveInvestment(investment);
    } catch (Exception e) {
      log.error("Failed to create forecast {} due to:", forecast, e);
      return null;
    }
    try {
      googleSheetsService.writeInvestmentsData(investments);
    } catch (Exception e) {
      log.error(
          "Failed to write investments into Google Sheets while creating a forecast due to", e);
      return null;
    }
    return forecast;
  }

  public Forecast updateForecast(Long investmentId, Forecast forecast) {
    List<Investment> investments = getInvestments();
    if (investments.isEmpty()) {
      log.error("Failed to load investments list while updating a forecast");
      return null;
    }
    Investment investment = getInvestment(investments, investmentId);
    Forecast existingForecast = getForecast(investment, forecast.getId());

    existingForecast.setName(forecast.getName());
    existingForecast.setStartDate(forecast.getStartDate());
    existingForecast.setEndDate(forecast.getEndDate());
    existingForecast.setScenarioRates(forecast.getScenarioRates());

    // Recalculate derived fields
    saveInvestment(investment);

    try {
      googleSheetsService.writeInvestmentsData(investments);
    } catch (Exception e) {
      log.error(
          "Failed to write investments into Google Sheets while updating a forecast due to", e);
      return null;
    }
    return existingForecast;
  }

  public Forecast deleteForecast(Long investmentId, Long forecastId) {
    List<Investment> investments = getInvestments();
    if (investments.isEmpty()) {
      log.error("Failed to load investments list while deleting a forecast");
      return null;
    }
    Investment investment = getInvestment(investments, investmentId);
    Forecast forecastToDelete = getForecast(investment, forecastId);
    log.info("Deleting forecast {} from investment {}", forecastToDelete, investment);
    try {
      investment.getForecasts().remove(forecastToDelete);
      saveInvestment(investment);
    } catch (Exception e) {
      log.error("Failed to delete forecast {} due to:", forecastToDelete, e);
      return null;
    }
    try {
      googleSheetsService.writeInvestmentsData(investments);
    } catch (Exception e) {
      log.error(
          "Failed to write investments into Google Sheets while deleting a forecast due to", e);
      return null;
    }
    return forecastToDelete;
  }

  private Forecast getForecast(Investment investment, Long id) {
    return investment.getForecasts().stream()
        .filter(f -> f.getId().equals(id))
        .findFirst()
        .orElse(null);
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
