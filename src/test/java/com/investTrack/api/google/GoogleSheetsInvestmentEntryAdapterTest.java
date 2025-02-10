package com.investTrack.api.google;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.investTrack.model.Investment;
import com.investTrack.model.InvestmentEntry;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;

public class GoogleSheetsInvestmentEntryAdapterTest {
  private final GoogleSheetsInvestmentEntryAdapter adapter =
      new GoogleSheetsInvestmentEntryAdapter();

  @Test
  public void testToSheetValueRange_ShouldThrowDateTimeException_WhenErrorsFormatingDatetime() {
    var dateTime = LocalDateTime.of(2021, 1, 1, 0, 0, 0);
    var entry = new InvestmentEntry(dateTime, 1.0, 2.0, 1.0, "comments", new Investment());

    var valueRange = adapter.toSheetValueRange(entry);

    var expectedRange = List.of("01/01/2021 00:00:00", 1.0, 2.0, 1.0, "comments");
    assertEquals(expectedRange, valueRange);
  }
}
