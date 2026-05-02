package com.invest.track.service;

import com.invest.track.api.google.GoogleSheetsForecastService;
import com.invest.track.api.google.GoogleSheetsInvestmentService;
import com.invest.track.model.Forecast;
import com.invest.track.model.Investment;
import com.invest.track.model.InvestmentEntry;
import com.invest.track.model.Summary;
import com.invest.track.repository.InvestmentRepository;
import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

// Provides application logic for managing investments.

@Slf4j
@Service
@RequiredArgsConstructor
public class InvestmentService {
  private final GoogleSheetsInvestmentService googleSheetsService;
  private final GoogleSheetsForecastService googleSheetsForecastService;
  private final InvestmentRepository repository;
  private final SummaryService summaryService;
  private final AtomicLong entryIdGenerator = new AtomicLong(1);
  private final AtomicLong forecastIdGenerator = new AtomicLong(1);

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

      var loadedForecasts = googleSheetsForecastService.readForecastsData(investments);
      log.debug("Loaded {} forecasts from Google Sheets", loadedForecasts.size());
      for (var forecast : loadedForecasts) {
        if (forecast.getId() == null) {
          forecast.setId(forecastIdGenerator.getAndIncrement());
        } else {
          forecastIdGenerator.set(Math.max(forecastIdGenerator.get(), forecast.getId() + 1));
        }
        if (forecast.getInvestment() != null) {
          var inv = forecast.getInvestment();
          if (inv.getForecasts() == null) {
            inv.setForecasts(new ArrayList<>());
          }
          inv.getForecasts().add(forecast);
        }
      }

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
    try {
      repository.save(investment);
      googleSheetsService.writeInvestmentsData(getInvestments());
    } catch (Exception e) {
      log.error("Failed to save investment", e);
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
      writeForecasts();
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
    if (existingEntry == null) {
      log.error("Entry to update not found");
      return null;
    }

    // Update fields
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

  public List<Forecast> getForecasts() {
    return repository.findAll().stream()
        .flatMap(inv -> inv.getForecasts() != null ? inv.getForecasts().stream() : Stream.empty())
        .toList();
  }

  public Forecast createForecast(Forecast forecast, Long investmentId) {
    var investment = repository.findById(investmentId);
    if (investment == null) {
      log.error("Investment with ID {} not found while creating forecast", investmentId);
      return null;
    }

    if (forecast.getId() == null) {
      forecast.setId(forecastIdGenerator.getAndIncrement());
    }
    forecast.setInvestment(investment);

    if (investment.getForecasts() == null) {
      investment.setForecasts(new ArrayList<>());
    }
    investment.getForecasts().add(forecast);
    repository.save(investment);

    try {
      writeForecasts();
    } catch (IOException e) {
      log.error("Failed to write forecasts to Google Sheets", e);
      return null;
    }
    return forecast;
  }

  public Forecast updateForecast(Forecast forecast) {
    var existingForecast = findForecastById(forecast.getId());
    if (existingForecast == null) {
      log.error("Forecast with ID {} not found while updating", forecast.getId());
      return null;
    }

    existingForecast.setName(forecast.getName());
    existingForecast.setStartDate(forecast.getStartDate());
    existingForecast.setEndDate(forecast.getEndDate());
    existingForecast.setScenarioRates(forecast.getScenarioRates());

    repository.save(existingForecast.getInvestment());

    try {
      writeForecasts();
    } catch (IOException e) {
      log.error("Failed to write forecasts to Google Sheets", e);
      return null;
    }
    return existingForecast;
  }

  public Forecast deleteForecast(Long id) {
    var forecast = findForecastById(id);
    if (forecast == null) {
      log.error("Forecast with ID {} not found while deleting", id);
      return null;
    }

    var investment = forecast.getInvestment();
    investment.getForecasts().remove(forecast);
    repository.save(investment);

    try {
      writeForecasts();
    } catch (IOException e) {
      log.error("Failed to write forecasts to Google Sheets", e);
      return null;
    }
    return forecast;
  }

  private Forecast findForecastById(Long id) {
    return repository.findAll().stream()
        .flatMap(inv -> inv.getForecasts() != null ? inv.getForecasts().stream() : Stream.empty())
        .filter(f -> f.getId().equals(id))
        .findFirst()
        .orElse(null);
  }

  private void writeForecasts() throws IOException {
    googleSheetsForecastService.writeForecastsData(getForecasts());
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
