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

    assertEquals(3500, summary.getInvestedAmount());
    assertEquals(3975, summary.getObtained());
    assertEquals(475, summary.getBenefit());
    assertEquals(0.1357, summary.getProfitability(), 0.0001);
    assertEquals(3600, summary.getRealInvested());
    assertEquals(375, summary.getRealBenefit());
    assertEquals(0.10416, summary.getRealProfitability(), 0.0001);
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

    assertEquals(3000, summary.getInvestedAmount());
    assertEquals(3450, summary.getObtained());
    assertEquals(450, summary.getBenefit());
    assertEquals(0.15, summary.getProfitability(), 0.0001);
    assertEquals(2600, summary.getRealInvested());
    assertEquals(850, summary.getRealBenefit());
    assertEquals(0.326, summary.getRealProfitability(), 0.001);
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
    assertEquals(0, summary.getRealInvested());
    assertEquals(0, summary.getRealBenefit());
    assertEquals(0, summary.getRealProfitability());
  }

  private Investment newInvestment(boolean reinvested) {
    return new Investment(1L, "name", "description", "USD", null, null, reinvested);
  }
}
