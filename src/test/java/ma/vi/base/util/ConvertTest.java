package ma.vi.base.util;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class ConvertTest {
  @Test
  public void esqlTypeConversions() {
    assertEquals(1L,     Convert.toType("1", "long"));
    assertEquals(0L,     Convert.toType("0", "long"));
    assertEquals(1000L,  Convert.toType("1000", "long"));
    assertEquals(-1L,    Convert.toType("-1", "long"));
    assertEquals(-1000L, Convert.toType("-1000", "long"));

    assertEquals(1L,     Convert.toType(1L, "long"));
    assertEquals(0L,     Convert.toType(0L, "long"));
    assertEquals(1000L,  Convert.toType(1000L, "long"));
    assertEquals(-1L,    Convert.toType(-1L, "long"));
    assertEquals(-1000L, Convert.toType(-1000L, "long"));

    assertEquals(1,     Convert.toType("1", "int"));
    assertEquals(0,     Convert.toType("0", "int"));
    assertEquals(1000,  Convert.toType("1000", "int"));
    assertEquals(-1,    Convert.toType("-1", "int"));
    assertEquals(-1000, Convert.toType("-1000", "int"));

    assertEquals(1,     Convert.toType(1, "int"));
    assertEquals(0,     Convert.toType(0, "int"));
    assertEquals(1000,  Convert.toType(1000, "int"));
    assertEquals(-1,    Convert.toType(-1, "int"));
    assertEquals(-1000, Convert.toType(-1000, "int"));

    assertEquals(true, Convert.toType("1", "bool"));
    assertEquals(true, Convert.toType("y", "bool"));
    assertEquals(true, Convert.toType("Y", "bool"));
    assertEquals(true, Convert.toType("t", "bool"));
    assertEquals(true, Convert.toType("T", "bool"));
    assertEquals(true, Convert.toType("true", "bool"));
    assertEquals(true, Convert.toType("True", "bool"));

    assertEquals(false, Convert.toType("0", "bool"));
    assertEquals(false, Convert.toType("n", "bool"));
    assertEquals(false, Convert.toType("N", "bool"));
    assertEquals(false, Convert.toType("f", "bool"));
    assertEquals(false, Convert.toType("F", "bool"));
    assertEquals(false, Convert.toType("false", "bool"));
    assertEquals(false, Convert.toType("False", "bool"));

    UUID u = UUID.randomUUID();
    assertEquals(u, Convert.toType(u.toString(), "uuid"));
    assertEquals(u, Convert.convert(u.toString(), UUID.class));

    assertEquals(date(1977, 5, 17), Convert.toType("17-05-1977", "date"));
    assertEquals(date(1977, 5, 17), Convert.toType("17-5-1977", "date"));
    assertEquals(date(1977, 5, 17), Convert.toType("17 5 1977", "date"));
    assertEquals(date(1977, 5, 17), Convert.toType("17 05 1977", "date"));
    assertEquals(date(1977, 5, 17), Convert.toType("17-MAY-1977", "date"));
    assertEquals(date(1977, 5, 17), Convert.toType("17 MAY 1977", "date"));

    assertEquals(date(2077, 6, 30), Convert.toType("2077-06-30", "date"));
    assertEquals(date(2077, 6, 30), Convert.toType("2077-6-30", "date"));
    assertEquals(date(2077, 6, 30), Convert.toType("2077 06 30", "date"));
    assertEquals(date(2077, 6, 30), Convert.toType("2077 6 30", "date"));
    assertEquals(date(2077, 6, 30), Convert.toType("2077 JUN 30", "date"));
    assertEquals(date(2077, 6, 30), Convert.toType("2077-JUN-30", "date"));
    assertEquals(date(2077, 6, 30), Convert.toType("2077 JUNE 30", "date"));
    assertEquals(date(2077, 6, 30), Convert.toType("2077-JUNE-30", "date"));
  }

  private static LocalDate date(int year, int month, int day) {
    return LocalDate.of(year, month, day);
  }
}
