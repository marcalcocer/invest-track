package com.investTrack.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.List;
import org.junit.jupiter.api.Test;

public class InvestmentTest {

  @Test
  public void testGetLastEntry_ShouldReturnNull_WhenNoEntries() {
    var investment = Investment.builder().build();
    assertNull(investment.getLastEntry());
  }

  @Test
  public void testGetLastEntry_ShouldReturnNull_WhenEmptyEntries() {
    var investment = Investment.builder().build();
    investment.setEntries(List.of());
    assertNull(investment.getLastEntry());
  }

  @Test
  public void testGetLastEntry_ShouldReturnLastEntry_WhenEntriesPresent() {
    var investment = Investment.builder().build();
    var entry1 = InvestmentEntry.builder().build();
    var entry2 = InvestmentEntry.builder().build();
    investment.setEntries(List.of(entry1, entry2));

    assertEquals(entry2, investment.getLastEntry());
  }
}
