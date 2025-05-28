package com.investTrack.api.google;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.investTrack.model.Investment;
import com.investTrack.model.InvestmentEntry;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;

public class GoogleSheetsInvestmentAdapterTest {
  private final GoogleSheetsInvestmentAdapter adapter = new GoogleSheetsInvestmentAdapter();

  @Test
  public void testToSheetValueRange_ShouldReturnList_WhenErrorsFormatingDatetime() {
    var startDate = LocalDateTime.of(2021, 1, 1, 0, 0, 0);
    var investment = new Investment(1L, "", "", "", startDate, null, false);
    investment.setEntries(List.of(new InvestmentEntry(null, 1.0, 2.0, 3.0, "", investment)));

    var valueRange = adapter.toSheetValueRange(investment);

    var expectedRange = List.of(1L, "", "", "", "01/01/2021 00:00:00", "", false, 1.0, 2.0, 3.0);
    assertEquals(expectedRange, valueRange);
  }

  @Test
  public void testToSheetValueRange_ShouldReturnList_WhenLastEntryIsNull() {
    var startDate = LocalDateTime.of(2021, 1, 1, 0, 0, 0);
    var investment =
        new Investment(1L, "Investment Name", "Description", "USD", startDate, null, false);

    var valueRange = adapter.toSheetValueRange(investment);

    var expectedRange =
        List.of(
            1L,
            "Investment Name",
            "Description",
            "USD",
            "01/01/2021 00:00:00",
            "",
            false,
            "",
            "",
            "");
    assertEquals(expectedRange, valueRange);
  }
}
