package com.investTrack.api.google;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.investTrack.model.Investment;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;

public class GoogleSheetsInvestmentAdapterTest {
  private final GoogleSheetsInvestmentAdapter adapter = new GoogleSheetsInvestmentAdapter();

  @Test
  public void testToSheetValueRange_ShouldThrowDateTimeException_WhenErrorsFormatingDatetime() {
    var startDate = LocalDateTime.of(2021, 1, 1, 0, 0, 0);
    var investment = new Investment(1L, "", "", "", startDate, null, false, 0.0, 0.0, 0.0);

    var valueRange = adapter.toSheetValueRange(investment);

    var expectedRange = List.of(1L, "", "", "", "01/01/2021 00:00:00", "", false, 0.0, 0.0, 0.0);
    assertEquals(expectedRange, valueRange);
  }
}
