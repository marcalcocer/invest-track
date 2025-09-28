package com.investTrack.model;

import static java.time.LocalDateTime.now;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class InvestmentEntryTest {
  @Test
  public void testInvestmentEntryConstructor_ShouldCalculateAllAttributes() {
    var datetime = now();
    var investment = Investment.builder().build();

    var investmentEntry = new InvestmentEntry(datetime, 5.0, 5.0, 0.1, "Comments", investment);

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
