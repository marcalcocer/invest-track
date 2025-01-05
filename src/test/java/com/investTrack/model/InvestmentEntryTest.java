package com.investTrack.model;

import static java.time.LocalDateTime.now;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class InvestmentEntryTest {
  @Test
  public void testInvestmentEntryConstructor_ShouldCalculateAllAttributes() {
    var datetime = now();
    var investment = new Investment();

    var investmentEntry = new InvestmentEntry(1L, datetime, 5.0, 5.0, 0.1, investment, "Comments");

    assertEquals(1L, investmentEntry.getId());
    assertEquals(investment, investmentEntry.getInvestment());
    assertEquals(datetime, investmentEntry.getDatetime());
    assertEquals("Comments", investmentEntry.getComments());
    assertEquals(5.0, investmentEntry.getInitialInvestedAmount());
    assertEquals(5.0, investmentEntry.getReinvestedAmount());
    assertEquals(10.0, investmentEntry.getTotalInvestedAmount());
    assertEquals(0.1, investmentEntry.getProfitability());
    assertEquals(11.0, investmentEntry.getObtained());
    assertEquals(1.0, investmentEntry.getBenefit());
  }
}
