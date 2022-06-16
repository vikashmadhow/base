/*
 * Copyright (c) 2018 Vikash Madhow
 */

package ma.vi.base.util;

import ma.vi.base.string.Escape;
import ma.vi.base.tuple.T2;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.Integer.parseInt;

/**
 * Utilities for converting values for one type to another.
 *
 * @author vikash.madhow@gmail.com
 */
public class Convert {
  /**
   * Returns true if the text could be a regular expression. This
   * is approximate as it only tests for common characters which appears
   * commonly in regular expressions (such as *).
   */
  public static boolean couldBeRegex(String text) {
    return text.indexOf('*')  != -1 || text.indexOf('+') != -1 ||
           text.indexOf('|')  != -1 || text.indexOf('?') != -1 ||
           text.indexOf('[')  != -1 || text.indexOf(']') != -1 ||
           text.indexOf('\\') != -1 || text.indexOf('$') != -1 ||
           text.indexOf('^')  != -1;
  }

  /**
   * Converts a value to string, applying a default format.
   */
  public static String defaultFormat(Object value) {
    if (value == null) {
      return null;
    } else {
      Format formatter = defaultFormatter(value.getClass());
      return formatter != null ? formatter.format(value) : value.toString();
    }
  }

  /**
   * Returns a format object for the specified target type. Returns null if no appropriate
   * formatter found.
   */
  public static Format defaultFormatter(Class<?> type) {
    return LocalDate.class.isAssignableFrom(type) ? DATE_FORMATTER
         : Number.class.isAssignableFrom(type)    ? (Numbers.isIntegral(type) ? INTEGER_FORMATTER : DECIMAL_FORMATTER)
         : null;
  }

  public static Object convert(Object value, Class<?> type) {
    return value == null                           ? null
         : type.isAssignableFrom(value.getClass()) ? value
         : convert(String.valueOf(value), type);
  }

  /**
   * Utility method to convert a string to an object.
   */
  public static Object convert(String text, Class<?> type) {
    try {
      if (text == null) {
        return null;

      } else if (Number.class.isAssignableFrom(type)) {
        if (Numbers.isIntegral(type)) {
          /*
           * Remove fractional part which will be ignored.
           */
          int pos = text.indexOf('.');
          if (pos != -1) {
            text = text.substring(0, pos);
            if (text.length() == 0) {
              text = "0";
            }
          }
        }
        return type.getConstructor(String.class).newInstance(text);
      }
      else if (Boolean  .class.isAssignableFrom(type)) { return type.getConstructor(String.class).newInstance(text); }
      else if (String   .class.isAssignableFrom(type)) { return text; }
      else if (Character.class.isAssignableFrom(type)) { return text.charAt(0); }
      else if (LocalDate.class.isAssignableFrom(type)) { return convertDate(text); }
      else if (UUID     .class.isAssignableFrom(type)) { return UUID.fromString(text); }
    } catch (Exception e) {
      throw e instanceof RuntimeException ? (RuntimeException) e : new RuntimeException(e);
    }
    throw new RuntimeException(type + " is not supported");
  }

  /**
   * Converts text value to the specified Esql type.
   */
  public static Object toType(Object value, String type) {
    if (type == null
     || value == null
     || type.equals("variable")) {
      return value;

    } else if (type.equals("date")) {
      return value instanceof Number n ? LocalDate.ofEpochDay(Duration.ofMillis(n.longValue()).toDays())
           : value instanceof String s ? convertDate(s)
           : value instanceof Date   d ? d.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
           : value;

    } else if (type.equals("time")) {
      return value instanceof Number n ? LocalDateTime.ofEpochSecond(Duration.ofMillis(n.longValue()).toSeconds(),
                                                                     Duration.ofMillis(n.longValue()).toNanosPart(),
                                                                     ZoneOffset.of(ZoneId.systemDefault().getId()))
                                                      .toLocalTime()
           : value instanceof String s ? DateTimeFormatter.ISO_LOCAL_TIME.parse(s)
           : value instanceof Date   d ? d.toInstant().atZone(ZoneId.systemDefault()).toLocalTime()
           : value;

    } else if (type.equals("datetime")) {
      return value instanceof Number n ? LocalDateTime.ofEpochSecond(Duration.ofMillis(n.longValue()).toSeconds(),
                                                                     Duration.ofMillis(n.longValue()).toNanosPart(),
                                                                     ZoneOffset.of(ZoneId.systemDefault().getId()))
           : value instanceof String s ? DateTimeFormatter.ISO_LOCAL_DATE_TIME.parse(s)
           : value instanceof Date   d ? d.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime()
           : value;

    } else {
      String s = value.toString().trim().toLowerCase();
      return switch(type) {
        case "bool"   -> value instanceof String   ? s.equals("1") || s.startsWith("t") || s.startsWith("y")
                       : value instanceof Number n ? n.intValue() != 0
                       : true;
        case "byte"   -> Byte   .valueOf(s);
        case "short"  -> Short  .valueOf(s);
        case "int"    -> Integer.valueOf(s);
        case "long"   -> Long   .valueOf(s);
        case "float"  -> Float  .valueOf(s);
        case "double" -> Double .valueOf(s);
        case "string",
             "text"   -> value.toString();
        case "uuid"   -> UUID.fromString(s);
        default       -> value;
      };
    }
  }

  /**
   * Converts the value into a form that can be concatenated into a SQL statement.
   */
  public static String sqlParameter(Object value) {
    if      (value == null)                     { return "NULL"; }
    else if (value instanceof Boolean b)        { return b ? "TRUE" : "FALSE"; }
    else if (value instanceof LocalDate d)      { return Dates.toSqlDateLiteral(d); }
    else if (value instanceof LocalTime t)      { return Dates.toSqlDateLiteral(t); }
    else if (value instanceof LocalDateTime dt) { return Dates.toSqlDateLiteral(dt); }
    else if (value instanceof Number)           { return value.toString(); }
    else if (value instanceof Character)        { return "'" + value + "'"; }
    else if (value instanceof String)           { return "'" + Escape.escapeSqlString((String) value) + "'"; }
    else throw new IllegalArgumentException(value.getClass() + " cannot be converted to a form supported by SQL");
  }

  /**
   * Attempts to convert the value using most common formats.
   *
   * @param date The value to convert.
   * @return The converted value or null if the value was null.
   * @throws IllegalArgumentException If the value could not be converted to a date.
   */
  public static LocalDate convertDate(String date) throws IllegalArgumentException {
    T2<LocalDate, Boolean> converted = convertInternal(date);
    if (converted.b) return converted.a;
    else             throw new IllegalArgumentException("'" + date + "' could not be converted to a date");
  }

  /**
   * Returns true if the provided date in string format can be converted to a valid date.
   */
  public static boolean canConvert(String date) {
    return date == null || convertInternal(date).b;
  }

  /**
   * Converts the string value to a date. Return a pair, where the first value is
   * the converted date if successfully converted (or null otherwise), and the second
   * value is true when the conversion is successful, or false otherwise.
   */
  private static T2<LocalDate, Boolean> convertInternal(String value) {
    if (value != null) {
      /*
       * Remove all unnecessary characters prior to conversion.
       */
      boolean yearFound = false;
      boolean monthFound = false;
      boolean dayFound = false;

      int probableMonthStart = -1;
      int probableMonthEnd = -1;

      StringBuilder compressed = new StringBuilder();
      Matcher matcher = WORDS_NUMBERS.matcher(value);
      while (matcher.find()) {
        String token = value.substring(matcher.start(), matcher.end());
        if (YEAR_PATTERN.matcher(token).matches()) {
          yearFound = true;
          compressed.insert(0, token + " ");

          /*
           * Push probable month position correspondingly.
           */
          if (probableMonthStart != -1) {
            probableMonthStart += token.length() + 1;
            probableMonthEnd += token.length() + 1;
          }
        } else {
          if (MONTH_PATTERN.matcher(token).matches()) {
            monthFound = true;
          } else if (DAY_OR_MONTH_PATTERN.matcher(token).matches()) {
            /*
             * If we have already seen a month.
             */
            if (monthFound) {
              dayFound = true;
            } else {
              int number = parseInt(token);
              if ((dayFound || yearFound) && probableMonthStart == -1 && number > 0 && number <= 12) {
                /*
                 * Since we have already seen the year, this is a probable month.
                 */
                probableMonthStart = compressed.length();
                probableMonthEnd = probableMonthStart + token.length();
              } else {
                dayFound = true;
              }
            }
          }
          compressed.append(token).append(' ');
        }
      }

      if (!monthFound && probableMonthStart != -1) {
        int month = parseInt(compressed.substring(probableMonthStart, probableMonthEnd));
        compressed.delete(probableMonthStart, probableMonthEnd);
        compressed.insert(probableMonthStart, Month.get(month).name);
      }

      String reformatted = compressed.toString().trim();
      DateFormat[] formats = yearFound ? DATE_FORMATS_WITH_FOUR_DIGITS_YEAR : DATE_FORMATS_WITH_TWO_DIGITS_YEAR;
      for (DateFormat formatter : formats) {
        try {
          LocalDate date = formatter.parse(reformatted).toInstant()
                                    .atZone(ZoneId.systemDefault())
                                    .toLocalDate();
          return new T2<>(date, Boolean.TRUE);
        } catch (Exception ignore) {
        }
      }
      if (reformatted.length() == 8) {
        try {
          int year = parseInt(reformatted.substring(0, 4));
          int month = parseInt(reformatted.substring(4, 6));

          DateFormat formatter;
          if (year >= 1900 && year <= 2100 && month >= 1 && month <= 12) {
            formatter = yyyyMMdd;
          } else {
            formatter = ddMMyyyy;
          }
          LocalDate date = formatter.parse(reformatted).toInstant()
                                    .atZone(ZoneId.systemDefault())
                                    .toLocalDate();
          return new T2<>(date, Boolean.TRUE);
        } catch (Exception ignore) {
        }
      }
      return new T2<>(null, Boolean.FALSE);
    }
    return new T2<>(null, Boolean.TRUE);
  }

  private static final DateFormat yyyyMMdd = new SimpleDateFormat("yyyyMMdd");
  private static final DateFormat ddMMyyyy = new SimpleDateFormat("ddMMyyyy");

  /**
   * Common date formats used for 4-digit years.
   */
  public static final DateFormat[] DATE_FORMATS_WITH_FOUR_DIGITS_YEAR = {
      // date and time in 24-hour with seconds and time zone
      new SimpleDateFormat("yyyy MMM d HH mm ss z"),
      new SimpleDateFormat("yyyy d MMM HH mm ss z"),

      // date and time in 12-hour (am, pm) with seconds
      new SimpleDateFormat("yyyy MMM d KK mm ss a"),
      new SimpleDateFormat("yyyy d MMM KK mm ss a"),

      // date and time in 24-hour with seconds
      new SimpleDateFormat("yyyy MMM d HH mm ss"),
      new SimpleDateFormat("yyyy d MMM HH mm ss"),

      // date and time in 12-hour (am, pm) without seconds
      new SimpleDateFormat("yyyy MMM d KK mm a"),
      new SimpleDateFormat("yyyy d MMM KK mm a"),

      // date and time in 24-hour without seconds with timezone
      new SimpleDateFormat("yyyy MMM d HH mm z"),
      new SimpleDateFormat("yyyy d MMM HH mm z"),

      // date and time in 24-hour without seconds
      new SimpleDateFormat("yyyy MMM d HH mm"),
      new SimpleDateFormat("yyyy d MMM HH mm"),

      // date only, no time
      new SimpleDateFormat("yyyy MMM d"),
      new SimpleDateFormat("yyyy d MMM"),
  };

  /**
   * Common date formats used for 2-digits years.
   */
  public static final DateFormat[] DATE_FORMATS_WITH_TWO_DIGITS_YEAR =
      {
          // date and time in 24-hour with seconds and time zone
          new SimpleDateFormat("d MMM yy HH mm ss z"),
          new SimpleDateFormat("MMM d yy HH mm ss z"),

          // date and time in 12-hour (am, pm) with seconds
          new SimpleDateFormat("d MMM yy KK mm ss a"),
          new SimpleDateFormat("MMM d yy KK mm ss a"),

          // date and time in 24-hour with seconds
          new SimpleDateFormat("d MMM yy HH mm ss"),
          new SimpleDateFormat("MMM d yy HH mm ss"),

          // date and time in 12-hour (am, pm) without seconds
          new SimpleDateFormat("d MMM yy KK mm a"),
          new SimpleDateFormat("MMM d yy KK mm a"),

          // date and time in 24-hour without seconds with timezone
          new SimpleDateFormat("d MMM yy HH mm z"),
          new SimpleDateFormat("MMM d yy HH mm z"),

          // date and time in 24-hour without seconds
          new SimpleDateFormat("d MMM yy HH mm"),
          new SimpleDateFormat("MMM d yy HH mm"),

          // date only, no time
          new SimpleDateFormat("d MMM yy"),
          new SimpleDateFormat("MMM d yy"),
      };

  public static final Pattern WORDS_NUMBERS = Pattern.compile("(?i)[a-z]+|[0-9]+");

  /**
   * A formatter for printing the month and year of a date.
   */
  public static final SimpleDateFormat MONTH_YEAR = new SimpleDateFormat("MMMM, yyyy");

  /**
   * A pattern for matching years.
   */
  public static final Pattern YEAR_PATTERN = Pattern.compile("\\d{4}");

  /**
   * A numeric day or month.
   */
  public static final Pattern DAY_OR_MONTH_PATTERN = Pattern.compile("\\d{1,2}");

  /**
   * A pattern for matching with the name of a month.
   */
  public static final Pattern MONTH_PATTERN = Pattern.compile(
      "(?i)j[a-z]*|f[a-z]*|m[a-z]*|a[a-z]*|s[a-z]*|o[a-z]*|n[a-z]*|d[a-z]*");

  /**
   * Default date format with unbreakable hyphens characters (\u2011)
   * separating the date components.
   */
  public static final String DATE_FORMAT = "dd-MMM-yyyy"; // "dd\u2011MMM\u2011yy";

  /**
   * Integer format.
   */
  public static final String INTEGER_FORMAT = "############################,##0";

  /**
   * Decimal format.
   */
  public static final String DECIMAL_FORMAT = "############################,##0.00";

  /**
   * Integer formatter.
   */
  public static final DecimalFormat INTEGER_FORMATTER = new DecimalFormat(INTEGER_FORMAT);

  /**
   * Decimal formatter.
   */
  public static final DecimalFormat DECIMAL_FORMATTER = new DecimalFormat(DECIMAL_FORMAT);

  /**
   * Default date formatter.
   */
  public static final SimpleDateFormat DATE_FORMATTER = new SimpleDateFormat(DATE_FORMAT);
}
