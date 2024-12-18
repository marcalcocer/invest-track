package com.investTrack.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.DateTimeException;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;

public class InvestmentAdapterTest {
  private final InvestmentAdapter adapter = new InvestmentAdapter();

  @Test
  public void testFromSheetValueRange_ShouldThrowNumberFormatException_WhenParsingALong() {
    List<Object> values = List.of("not-a-long");
    assertThrows(NumberFormatException.class, () -> adapter.fromSheetValueRange(values));
  }

  @Test
  public void testFromSheetValueRange_ShouldThrowNumberFormatException_WhenParsingADateTime() {
    List<Object> values = List.of(1L, "", "", "", "not-a-datetime");
    assertThrows(DateTimeException.class, () -> adapter.fromSheetValueRange(values));
  }

  @Test
  public void testFromSheetValueRange_ShouldThrowNumberFormatException_WhenParsingADouble() {
    List<Object> values =
        List.of(
            1L,
            "",
            "",
            "",
            "20/12/2023 10:00:00",
            "20/12/2023 10:00:00",
            true,
            false,
            "not-a-double");
    assertThrows(NumberFormatException.class, () -> adapter.fromSheetValueRange(values));
  }

  @Test
  public void testFromSheetValueRange_ShouldReturnAValidInvestment() {
    var investment = adapter.fromSheetValueRange(getSampleInvestmentValueRange());

    assertEquals(getExpectedInvestment(), investment);
  }

  private Investment getExpectedInvestment() {
    return Investment.builder()
        .id(1L)
        .name("name")
        .description("description")
        .currency("USD")
        .startDateTime(LocalDateTime.parse("2023-12-20T10:00:00"))
        .endDateTime(null)
        .isActive(true)
        .isReinvested(false)
        .initialInvestedAmount(250.00)
        .reinvestedAmount(0.00)
        .totalInvestedAmount(250.00)
        .profitability(20.06)
        .totalObtained(1300.15)
        .totalBenefit(50.15)
        .benefitFromInitialAmount(50.15)
        .profitabilityFromInitialAmount(20.06)
        .build();
  }

  private List<Object> getSampleInvestmentValueRange() {
    return List.of(
        1,
        "name",
        "description",
        "USD",
        "20/12/2023 10:00:00",
        "",
        true,
        false,
        "250,00",
        "0,00",
        "250,00",
        "20,06",
        "1.300,15",
        "50,15 ",
        "50,15 ",
        "20,06");
  }
}
