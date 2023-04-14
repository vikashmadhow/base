/*
 * Copyright (c) 2018 Vikash Madhow
 */

package ma.vi.base.util;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;

/**
 * Utility functions to work with dates.
 *
 * @author vikash.madhow@gmail.com
 */
public class Dates {
  public static LocalDate     date() { return LocalDate.now(); }
  public static LocalTime     time() { return LocalTime.now(); }
  public static LocalDateTime now () { return LocalDateTime.now(); }

  /**
   * Given year, return a date for the first day of the first month at 00:00:00.
   */
  public static LocalDateTime of(int year) {
    return of(year, 1, 1, 0, 0, 0, 0);
  }

  public static LocalDateTime of(int year, int month) {
    return of(year, month, 1, 0, 0, 0, 0);
  }

  public static LocalDateTime of(int year, int month, int day) {
    return of(year, month, day, 0, 0, 0, 0);
  }

  public static LocalDateTime of(int year, int month, int day, int hour) {
    return of(year, month, day, hour, 0, 0, 0);
  }

  public static LocalDateTime of(int year, int month, int day, int hour, int minute) {
    return of(year, month, day, hour, minute, 0, 0);
  }

  public static LocalDateTime of(int year, int month, int day, int hour, int minute, int second) {
    return of(year, month, day, hour, minute, second, 0);
  }

  public static LocalDateTime of(int year, int month, int day, int hour, int minute, int second, int milli) {
    return LocalDateTime.of(year, month, day, hour, minute, second, milli * 1000000);
  }

  /**
   * Converts a normal java.util.Date to a java.sql.Date.
   */
  public static Date toSqlDate(LocalDate date) {
    return Date.valueOf(date);
  }

  public static Timestamp toSqlTimestamp(LocalDateTime time) {
    return Timestamp.valueOf(time);
  }

  public static Object toSqlDate(Object value) {
    return value instanceof LocalDate     d ? toSqlDate(d)
         : value instanceof LocalDateTime t ? toSqlTimestamp(t)
         : value;
  }

  /**
   * Returns the date as a proper SQL literal for embedding in a query.
   * The literal is already enclosed in single-quotes.
   */
  public static String toSqlDateLiteral(LocalDate date) {
    return date == null ? "NULL"
         : '\'' + DateTimeFormatter.ISO_LOCAL_DATE.format(date) + '\'';
  }

  public static String toSqlDateLiteral(LocalTime time) {
    return time == null ? "NULL"
         : '\'' + DateTimeFormatter.ISO_LOCAL_TIME.format(time) + '\'';
  }

  /**
   * Returns the date as a proper SQL literal for embedding in a query.
   * The literal is already enclosed in single-quotes.
   */
  public static String toSqlDateLiteral(LocalDateTime date) {
    return date == null ? "NULL"
         : '\'' + DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(date) + '\'';
  }

  public static String toSqlDateLiteral() {
    return toSqlDateLiteral(LocalDate.now());
  }

  /**
   * Given a Java date formatting string, returns the equivalent
   * postgresql formatter string for the most common patterns. This
   * method does not cover all possible patterns.
   */
  public static String javaToPostgresqlDateFormat(String javaFormat) {
    String format = javaFormat.replace('y', 'Y');
    format = format.replaceAll("M{4,}", "Month");
    format = format.replace("MMM", "Mon");
    format = format.replaceAll("D+", "DDD");
    format = format.replaceAll("E{4,}", "Day");
    format = format.replaceAll("E+", "Dy");
    format = format.replaceAll("d+", "DD");

    format = format.replace("HH", "HH24");
    format = format.replace("hh", "HH12");
    format = format.replace("mm", "MI");
    format = format.replace("ss", "SS");
    format = format.replace("SSS", "MS");

    format = format.replace("z", "tz");
    format = format.replace("Z", "TZ");

    return format;
  }

  /**
   * Given an Excel date formatting string, returns the equivalent
   * Java formatter string for the most common patterns. This
   * method does not cover all possible patterns.
   */
  public static String excelToJavaDateFormat(String excelFormat) {
    String format = excelFormat.replace('y', 'Y');
    format = format.replaceAll("(?<!:)mmmm", "MMMM");
    format = format.replaceAll("(?<!:)mmm", "MMM");
    format = format.replaceAll("(?<!:)mm", "MM");
    format = format.replaceAll("(?<!:)m", "M");
    format = format.replace("dddd", "EE");
    format = format.replace("ddd", "E");
    format = format.replace("h", "H");
    return format;
  }

  /**
   * Makes the following corrections to the year value, if necessary:
   * <ul>
   * <li>If year is negative it is multiplied by -1.</li>
   *
   * <li>If the year is less than 100, century is added: either 2000 if the
   * year is less or equal to the current year (w/o century) or 1999
   * otherwise.</li>
   *
   * <li>If the year is of between 200 and 209, a 0 is added after the '2'
   * to make it into 2000 to 2009.</li>
   *
   * <li>If the year is of between 20000 and 20099, a 0 is removed after
   * the '2' to make it into 2000 to 2009.</li>
   * </ul>
   *
   * @param year The year to correct.
   * @return The corrected year.
   */
  public static int correctYear(int year) {
    // make into positive
    if (year < 0) {
      year *= -1;
    }

    // add correct century: 1900 or 2000 to make it into the current or a past year
    if (year < 100) {
      int currentYear = Calendar.getInstance().get(Calendar.YEAR);
      year += year + 2000 > currentYear ? 1900 : 2000;
    }

    // add 0 to correct mistyping at 2nd position
    if (year >= 200 && year <= 299) {
      year += 1800;
    }

    // remove 0 to correct mistyping at 2nd position
    if (year >= 20000 && year <= 20099) {
      year -= 18000;
    }
    return year;
  }

  public static boolean isBefore(String    date1, String    date2) { return isBefore(LocalDate.parse(date1), LocalDate.parse(date2)); }
  public static boolean isBefore(LocalDate date1, String    date2) { return isBefore(date1, LocalDate.parse(date2)); }
  public static boolean isBefore(String    date1, LocalDate date2) { return isBefore(LocalDate.parse(date1), date2); }
  public static boolean isBefore(LocalDate date1, LocalDate date2) { return date1.isBefore(date2); }

  public static boolean isAfter(String    date1, String    date2) { return isAfter(LocalDate.parse(date1), LocalDate.parse(date2)); }
  public static boolean isAfter(LocalDate date1, String    date2) { return isAfter(date1, LocalDate.parse(date2)); }
  public static boolean isAfter(String    date1, LocalDate date2) { return isAfter(LocalDate.parse(date1), date2); }
  public static boolean isAfter(LocalDate date1, LocalDate date2) { return date1.isAfter(date2); }

  public static boolean isSame(String    date1, String    date2) { return isSame(LocalDate.parse(date1), LocalDate.parse(date2)); }
  public static boolean isSame(LocalDate date1, String    date2) { return isSame(date1, LocalDate.parse(date2)); }
  public static boolean isSame(String    date1, LocalDate date2) { return isSame(LocalDate.parse(date1), date2); }
  public static boolean isSame(LocalDate date1, LocalDate date2) { return date1.isEqual(date2); }

  public static boolean isSameOrBefore(String    date1, String    date2) { return isSameOrBefore(LocalDate.parse(date1), LocalDate.parse(date2)); }
  public static boolean isSameOrBefore(LocalDate date1, String    date2) { return isSameOrBefore(date1, LocalDate.parse(date2)); }
  public static boolean isSameOrBefore(String    date1, LocalDate date2) { return isSameOrBefore(LocalDate.parse(date1), date2); }
  public static boolean isSameOrBefore(LocalDate date1, LocalDate date2) { return isSame(date1, date2) || isBefore(date1, date2); }

  public static boolean isSameOrAfter(String    date1, String    date2) { return isSameOrAfter(LocalDate.parse(date1), LocalDate.parse(date2)); }
  public static boolean isSameOrAfter(LocalDate date1, String    date2) { return isSameOrAfter(date1, LocalDate.parse(date2)); }
  public static boolean isSameOrAfter(String    date1, LocalDate date2) { return isSameOrAfter(LocalDate.parse(date1), date2); }
  public static boolean isSameOrAfter(LocalDate date1, LocalDate date2) { return isSame(date1, date2) || isAfter(date1, date2); }

  public static boolean isBetween(String    date, String    from, String    to) { return isSameOrAfter(date, from) && isSameOrBefore(date, to); }
  public static boolean isBetween(LocalDate date, String    from, String    to) { return isSameOrAfter(date, from) && isSameOrBefore(date, to); }
  public static boolean isBetween(String    date, LocalDate from, String    to) { return isSameOrAfter(date, from) && isSameOrBefore(date, to); }
  public static boolean isBetween(String    date, String    from, LocalDate to) { return isSameOrAfter(date, from) && isSameOrBefore(date, to); }
  public static boolean isBetween(LocalDate date, LocalDate from, String    to) { return isSameOrAfter(date, from) && isSameOrBefore(date, to); }
  public static boolean isBetween(LocalDate date, String    from, LocalDate to) { return isSameOrAfter(date, from) && isSameOrBefore(date, to); }
  public static boolean isBetween(String    date, LocalDate from, LocalDate to) { return isSameOrAfter(date, from) && isSameOrBefore(date, to); }
  public static boolean isBetween(LocalDate date, LocalDate from, LocalDate to) { return isSameOrAfter(date, from) && isSameOrBefore(date, to); }

  public static long daysBetween(String    to, String    from) { return daysBetween(LocalDate.parse(to), LocalDate.parse(from)); }
  public static long daysBetween(LocalDate to, String    from) { return daysBetween(to, LocalDate.parse(from)); }
  public static long daysBetween(String    to, LocalDate from) { return daysBetween(LocalDate.parse(to), from); }
  public static long daysBetween(LocalDate to, LocalDate from) { return ChronoUnit.DAYS.between(from, to); }

  public static long monthsBetween(String    to, String    from) { return monthsBetween(LocalDate.parse(to), LocalDate.parse(from)); }
  public static long monthsBetween(LocalDate to, String    from) { return monthsBetween(to, LocalDate.parse(from)); }
  public static long monthsBetween(String    to, LocalDate from) { return monthsBetween(LocalDate.parse(to), from); }
  public static long monthsBetween(LocalDate to, LocalDate from) { return ChronoUnit.MONTHS.between(from, to); }

  public static long yearsBetween(String    to, String    from) { return yearsBetween(LocalDate.parse(to), LocalDate.parse(from)); }
  public static long yearsBetween(LocalDate to, String    from) { return yearsBetween(to, LocalDate.parse(from)); }
  public static long yearsBetween(String    to, LocalDate from) { return yearsBetween(LocalDate.parse(to), from); }
  public static long yearsBetween(LocalDate to, LocalDate from) { return ChronoUnit.YEARS.between(from, to); }

  public static LocalDate startOfMonth(LocalDate date) {
    return LocalDate.of(date.getYear(), date.getMonth(), 1);
  }
  public static LocalDateTime startOfMonth(LocalDateTime date) {
    return LocalDateTime.of(date.getYear(), date.getMonth(), 1,
                            date.getHour(), date.getMinute(), date.getSecond(),
                            date.getNano());
  }

  public static LocalDate endOfMonth(LocalDate date) {
    var month = date.getMonthValue();
    if (month == 12) {
      return LocalDate.of(date.getYear() + 1, 1, 1).minusDays(1);
    } else {
      return LocalDate.of(date.getYear(), month + 1, 1).minusDays(1);
    }
  }
  public static LocalDateTime endOfMonth(LocalDateTime date) {
    var month = date.getMonthValue();
    if (month == 12) {
      return LocalDateTime.of(date.getYear() + 1, 1, 1,
                              date.getHour(), date.getMinute(), date.getSecond(),
                              date.getNano()).minusDays(1);
    } else {
        return LocalDateTime.of(date.getYear(), month + 1, 1,
                                date.getHour(), date.getMinute(), date.getSecond(),
                                date.getNano()).minusDays(1);
    }
  }

  public static LocalDate startOfYear(LocalDate date) {
    return LocalDate.of(date.getYear(), 1, 1);
  }
  public static LocalDateTime startOfYear(LocalDateTime date) {
    return LocalDateTime.of(date.getYear(), 1, 1,
                            date.getHour(), date.getMinute(), date.getSecond(),
                            date.getNano());
  }

  public static LocalDate endOfYear(LocalDate date) {
    return LocalDate.of(date.getYear(), 12, 31);
  }
  public static LocalDateTime endOfYear(LocalDateTime date) {
    return LocalDateTime.of(date.getYear(), 12, 31,
                            date.getHour(), date.getMinute(), date.getSecond(),
                            date.getNano());
  }

  public static String format(LocalDate date)                { return format(date, "dd-MMM-yyyy"); }
  public static String format(LocalDate date, String format) { return DateTimeFormatter.ofPattern(format).format(date); }
}
