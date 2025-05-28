package com.investTrack.model;

import static java.time.LocalDateTime.now;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.Test;

public class InvestmentTest {

  @Test
  public void testInvestmentConstructor_ShouldCalculateAllAttributes() {
    var startDate = now();
    var endDate = now().minusDays(1);

    var investment =
        new Investment(1L, "Investment", "description", "EUR", startDate, endDate, true);

    assertEquals(1L, investment.getId());
    assertEquals("Investment", investment.getName());
    assertEquals("description", investment.getDescription());
    assertEquals("EUR", investment.getCurrency());
    assertEquals(startDate, investment.getStartDateTime());
    assertEquals(endDate, investment.getEndDateTime());
    assertTrue(investment.isReinvested());
  }

  @Test
  public void testGetLastEntry_ShouldReturnNull_WhenNoEntries() {
    var investment = new Investment();
    assertNull(investment.getLastEntry());
  }

  @Test
  public void testGetLastEntry_ShouldReturnNull_WhenEmptyEntries() {
    var investment = new Investment();
    investment.setEntries(List.of());
    assertNull(investment.getLastEntry());
  }

  @Test
  public void testGetLastEntry_ShouldReturnLastEntry_WhenEntriesPresent() {
    var investment = new Investment();
    var entry1 = new InvestmentEntry();
    var entry2 = new InvestmentEntry();
    investment.setEntries(List.of(entry1, entry2));

    assertEquals(entry2, investment.getLastEntry());
  }
}
