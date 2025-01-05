package com.investTrack.model.adapter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.DateTimeException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

public class AdapterUtilsTest {
  private final AdapterUtils adapter = new AdapterUtils();

  @Test
  public void testParseLong_ShouldReturnALongValue() {
    var value = adapter.parseLong("123");
    assertEquals(123L, value);
  }

  @Test
  public void testParseLong_ShouldThrowNumberFormatException_WhenParsingANonLong() {
    assertThrows(NumberFormatException.class, () -> adapter.parseLong("not-a-long"));
  }

  @Test
  public void testParseCurrencyDouble_ShouldReturnADoubleValue() {
    var value = adapter.parseCurrencyDouble("123.45,99 €");
    assertEquals(12345.99, value);
  }

  @Test
  public void testParseCurrencyDouble_ShouldThrowNumberFormatException_WhenParsingANonDouble() {
    assertThrows(NumberFormatException.class, () -> adapter.parseCurrencyDouble("not-a-double"));
  }

  @ParameterizedTest
  @CsvSource({"23.45%, 0.2345", "23%, 0.23", "'0,23', 0.23"})
  public void parsePercentageDouble_ShouldReturnADoubleValue(String percentage, double expected) {
    var value = adapter.parsePercentageDouble(percentage);
    assertEquals(expected, value);
  }

  @Test
  public void testParsePercentageDouble_ShouldThrowNumberFormatException_WhenParsingANonDouble() {
    assertThrows(NumberFormatException.class, () -> adapter.parsePercentageDouble("not-a-double"));
  }

  @Test
  public void testParseDateTime_ShouldReturnNull_WhenEmptyString() {
    var value = adapter.parseDateTime("");
    assertNull(value);
  }

  @ParameterizedTest
  @CsvSource({
    "20/12/2023 10:00:00, 2023-12-20T10:00",
    "19/11/2023 0:00:00, 2023-11-19T00:00",
    "1/02/2024 0:00:00, 2024-02-01T00:00",
    "1/02/2024 0:00:00, 2024-02-01T00:00"
  })
  public void testParseDateTime_ShouldReturnADateTime(String datetime, String expected) {
    var value = adapter.parseDateTime(datetime);
    assertEquals(expected, value.toString());
  }

  @Test
  public void testParseDateTime_ShouldThrowDateTimeException_WhenParsingANonDateTime() {
    assertThrows(DateTimeException.class, () -> adapter.parseDateTime("not-a-datetime"));
  }

  @Test
  public void testParseBoolean_ShouldReturnABooleanValue() {
    var value = adapter.parseBoolean("true");
    assertTrue(value);
  }

  @Test
  public void testParseString_ShouldReturnAnEmptyString_WhenNullEntry() {
    var value = adapter.parseString(null);
    assertEquals("", value);
  }

  @Test
  public void testParseString_ShouldReturnAStringValue() {
    var value = adapter.parseString("string");
    assertEquals("string", value);
  }

  @Test
  public void testMandatoryField_ShouldThrowIllegalArgumentException_WhenNull() {
    assertThrows(IllegalArgumentException.class, () -> adapter.mandatoryField(null));
  }

  @Test
  public void testMandatoryField_ShouldThrowIllegalArgumentException_WhenEmptyString() {
    assertThrows(IllegalArgumentException.class, () -> adapter.mandatoryField(""));
  }

  @Test
  public void testMandatoryField_ShouldReturnTheValue() {
    var value = adapter.mandatoryField("value");
    assertEquals("value", value);
  }
}
