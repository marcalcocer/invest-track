package com.investTrack.model.adapter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;

public class InvestmentAdapterTest {

  private final AdapterUtils adapterUtils = new AdapterUtils();

  private final InvestmentAdapter adapter = new InvestmentAdapter(adapterUtils);

  @Test
  public void testFromSheetValueRange_ShouldReturnNullWhenValueRangeIsEmpty() {
    var investment = adapter.fromSheetValueRange(List.of());
    assertNull(investment);
  }

  @Test
  public void testFromSheetValueRange_ShouldReturnAValidInvestment() {
    var investment = adapter.fromSheetValueRange(getSampleInvestmentValueRange());

    assertEquals(1L, investment.getId());
    assertEquals("name", investment.getName());
    assertEquals("description", investment.getDescription());
    assertEquals("USD", investment.getCurrency());
    assertEquals(LocalDateTime.parse("2023-12-20T10:00:00"), investment.getStartDateTime());
    assertNull(investment.getEndDateTime());
    assertFalse(investment.isReinvested());
  }

  private List<Object> getSampleInvestmentValueRange() {
    return List.of(
        1,
        "name",
        "description",
        "USD",
        "20/12/2023 10:00:00",
        "",
        false,
        "1,00",
        "2,00",
        "0,1",
        "20,06");
  }
}
