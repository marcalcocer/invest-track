package com.investTrack.model;

import static java.time.LocalDateTime.now;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class InvestmentTest {

  @Test
  public void testInvestmentConstructor_ShouldCalculateAllAttributes() {
    var startDate = now();
    var endDate = now().minusDays(1);

    var investment =
        new Investment(
            1L, "Investment", "description", "EUR", startDate, endDate, true, 3.0, 2.0, 0.1);

    assertEquals(1L, investment.getId());
    assertEquals("Investment", investment.getName());
    assertEquals("description", investment.getDescription());
    assertEquals("EUR", investment.getCurrency());
    assertEquals(startDate, investment.getStartDateTime());
    assertEquals(endDate, investment.getEndDateTime());
    assertTrue(investment.isReinvested());
    assertEquals(3.0, investment.getInitialInvestedAmount());
    assertEquals(2.0, investment.getReinvestedAmount());
    assertEquals(5.0, investment.getTotalInvestedAmount());
    assertEquals(0.1, investment.getProfitability());
    assertEquals(5.5, investment.getObtained());
    assertEquals(0.5, investment.getBenefit());
  }
}
