package com.investTrack.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.investTrack.model.adapter.AdapterUtils;
import com.investTrack.model.adapter.InvestmentEntryAdapter;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;

public class InvestmentEntryAdapterTest {

  private final AdapterUtils adapterUtils = new AdapterUtils();

  private final InvestmentEntryAdapter adapter = new InvestmentEntryAdapter(adapterUtils);

  @Test
  public void testFromSheetValueRange_ShouldReturnNull_WhenEmptyValueRange() {
    var investment = newInvestment();

    var investmentEntry = adapter.fromSheetValueRange(List.of(), investment);

    assertNull(investmentEntry);
  }

  @Test
  public void testFromSheetValueRange_ShouldReturnAValidInvestmentEntry() {
    List<Object> sampleValueRange =
        List.of(1, "19/11/2023 0:00:00", "1,00 €", "2,00 €", "1,45%", "test");
    var investment = newInvestment();

    var investmentEntry = adapter.fromSheetValueRange(sampleValueRange, investment);

    assertEquals(1L, investmentEntry.getId());
    assertEquals(LocalDateTime.parse("2023-11-19T00:00:00"), investmentEntry.getDatetime());
    assertEquals(1.00, investmentEntry.getInitialInvestedAmount());
    assertEquals(2.00, investmentEntry.getReinvestedAmount());
    assertEquals(0.0145, investmentEntry.getProfitability(), 0.00001);
    assertEquals(investment, investmentEntry.getInvestment());
    assertEquals("test", investmentEntry.getComments());
  }

  @Test
  public void testFromSheetValueRange_ShouldReturnAValidInvestmentEntry_WhenNoComments() {
    List<Object> sampleValueRange = List.of(1, "19/11/2023 0:00:00", "1,00 €", "2,00 €", "1,45%");
    var investment = newInvestment();

    var investmentEntry = adapter.fromSheetValueRange(sampleValueRange, investment);

    assertEquals(1L, investmentEntry.getId());
    assertEquals(LocalDateTime.parse("2023-11-19T00:00:00"), investmentEntry.getDatetime());
    assertEquals(1.00, investmentEntry.getInitialInvestedAmount());
    assertEquals(2.00, investmentEntry.getReinvestedAmount());
    assertEquals(0.0145, investmentEntry.getProfitability(), 0.00001);
    assertEquals(investment, investmentEntry.getInvestment());
    assertEquals("", investmentEntry.getComments());
  }

  private Investment newInvestment() {
    return new Investment();
  }
}
