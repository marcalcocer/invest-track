package com.investTrack.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.investTrack.service.SummaryService;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;

public class SummaryServiceTest {
  private final SummaryService summaryService = new SummaryService();

  @Test
  public void testCalculateSummary_ShouldReturnASummary() {
    var investment1 = newInvestment(true);
    investment1.setEntries(List.of(new InvestmentEntry(null, 1000, 0, 0.2, "", investment1)));

    var investment2 = newInvestment(true);
    investment2.setEntries(List.of(new InvestmentEntry(null, 800, 1200, 0.1, "", investment2)));

    var investment3 = newInvestment(false);
    investment3.setEntries(List.of(new InvestmentEntry(null, 800, 2200, 0.15, "", investment3)));

    var investment4 = newInvestment(false);
    investment4.setEntries(List.of(new InvestmentEntry(null, 500, 0, 0.05, "", investment4)));

    var investment5 = newInvestment(false);
    investment5.setEndDateTime(LocalDateTime.now().minusYears(1));
    investment5.setEntries(List.of(new InvestmentEntry(null, 500, 0, 0.05, "", investment5)));

    var investments = List.of(investment1, investment2, investment3, investment4, investment5);

    var summary = summaryService.calculateSummary(investments);

    assertEquals(6500, summary.getInvestedAmount());
    assertEquals(7375, summary.getObtained());
    assertEquals(875, summary.getBenefit());
    assertEquals(0.1346, summary.getProfitability(), 0.0001);
  }

  @Test
  public void testCalculateSummary_ShouldReturnASummaryWhenInvestmentWithoutLastEntry() {
    var investment1 = newInvestment(true);
    investment1.setEntries(List.of(new InvestmentEntry(null, 1000, 0, 0.2, "", investment1)));

    var investment2 = newInvestment(true);
    investment2.setEntries(List.of(new InvestmentEntry(null, 800, 1200, 0.1, "", investment2)));

    var investment3 = newInvestment(false);
    investment3.setEntries(List.of(new InvestmentEntry(null, 800, 2200, 0.15, "", investment3)));

    var investment4 = newInvestment(false);
    // No entries for this investment
    var investments = List.of(investment1, investment2, investment3, investment4);

    var summary = summaryService.calculateSummary(investments);

    assertEquals(6000, summary.getInvestedAmount());
    assertEquals(6850, summary.getObtained());
    assertEquals(850, summary.getBenefit());
    assertEquals(0.1416, summary.getProfitability(), 0.0001);
  }

  @Test
  public void testCalculateSummary_ShouldReturnASummaryWhen0Dividing() {
    var investment1 = newInvestment(true);
    investment1.setEntries(List.of(new InvestmentEntry(null, 0, 0, 0.2, "", investment1)));

    var investment2 = newInvestment(true);
    investment2.setEntries(List.of(new InvestmentEntry(null, 0, 0, 0.1, "", investment2)));

    var investment3 = newInvestment(false);
    investment3.setEntries(List.of(new InvestmentEntry(null, 0, 0, 0.15, "", investment3)));

    var investment4 = newInvestment(false);
    investment4.setEntries(List.of(new InvestmentEntry(null, 0, 0, 0.05, "", investment4)));

    var investments = List.of(investment1, investment2, investment3, investment4);

    var summary = summaryService.calculateSummary(investments);

    assertEquals(0, summary.getInvestedAmount());
    assertEquals(0, summary.getObtained());
    assertEquals(0, summary.getBenefit());
    assertEquals(0, summary.getProfitability());
  }

  private Investment newInvestment(boolean reinvested) {
    return new Investment(1L, "name", "description", "USD", null, null, reinvested);
  }
}
