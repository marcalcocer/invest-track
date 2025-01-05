package com.investTrack.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.investTrack.service.SummaryService;
import java.util.List;
import org.junit.jupiter.api.Test;

public class SummaryServiceTest {
  private final SummaryService summaryService = new SummaryService();

  @Test
  public void testCalculateSummary_ShouldReturnASummary() {
    var investments =
        List.of(
            new Investment(1L, "name", "description", "USD", null, null, true, 1000, 0, 0.2),
            new Investment(2L, "name", "description", "USD", null, null, true, 800, 1200, 0.1),
            new Investment(2L, "name", "description", "USD", null, null, false, 800, 2200, 0.15),
            new Investment(2L, "name", "description", "USD", null, null, false, 500, 0, 0.05));

    var summary = summaryService.calculateSummary(investments);

    assertEquals(3500, summary.getInvestedAmount());
    assertEquals(3975, summary.getObtained());
    assertEquals(475, summary.getBenefit());
    assertEquals(0.1357, summary.getProfitability(), 0.0001);
    assertEquals(3100, summary.getRealInvested());
    assertEquals(875, summary.getRealBenefit());
    assertEquals(0.2822, summary.getRealProfitability(), 0.0001);
  }

  @Test
  public void testCalculateSummary_ShouldReturnASummaryWhen0Dividing() {
    var investments =
        List.of(
            new Investment(1L, "name", "description", "USD", null, null, true, 0, 0, 0.2),
            new Investment(2L, "name", "description", "USD", null, null, true, 0, 0, 0.1),
            new Investment(2L, "name", "description", "USD", null, null, false, 0, 0, 0.15),
            new Investment(2L, "name", "description", "USD", null, null, false, 0, 0, 0.05));

    var summary = summaryService.calculateSummary(investments);

    assertEquals(0, summary.getInvestedAmount());
    assertEquals(0, summary.getObtained());
    assertEquals(0, summary.getBenefit());
    assertEquals(0, summary.getProfitability());
    assertEquals(0, summary.getRealInvested());
    assertEquals(0, summary.getRealBenefit());
    assertEquals(0, summary.getRealProfitability());
  }
}
