/*
 * Copyright (c) 2018 Vikash Madhow
 */

package ma.vi.base.util;

import ma.vi.base.lang.NotFoundException;

/**
 * Month constants.
 *
 * @author vikash.madhow@gmail.com
 */
public enum Month {
  JANUARY("January", "Jan", 1),
  FEBRUARY("February", "Feb", 2),
  MARCH("March", "Mar", 3),
  APRIL("April", "Apr", 4),
  MAY("May", "May", 5),
  JUNE("June", "Jun", 6),
  JULY("July", "Jul", 7),
  AUGUST("August", "Aug", 8),
  SEPTEMBER("September", "Sep", 9),
  OCTOBER("October", "Oct", 10),
  NOVEMBER("November", "Nov", 11),
  DECEMBER("December", "Dec", 12),

  // Special month used to capture returns for the end-of-year
  // bonus which is considered a salary for the imaginary 13th
  // month of the year.
  THIRTEENTH("Thirteenth", "13th", 13);

  /**
   * Name.
   */
  public final String name;

  /**
   * Short name.
   */
  public final String shortName;

  /**
   * Ordinal for the month.
   */
  public final Integer code;

  /**
   * Returns the month corresponding to the code or UNKNOWN otherwise.
   */
  public static Month getMonth(String name) throws NotFoundException {
    // consider only first three characters which are sufficient for comparison
    name = name.trim();
    if (name.length() > 3) {
      name = name.substring(0, 3);
    }
    for (Month month : values()) {
      if (month.shortName.equalsIgnoreCase(name)) {
        return month;
      }
    }
    throw new NotFoundException("Could not find month corresponding to '" + name + "'");
  }

  /**
   * Returns the enum value corresponding to the code.
   */
  public static Month get(Integer ordinal) throws NotFoundException {
    for (Month month : values()) {
      if (month.code.equals(ordinal)) {
        return month;
      }
    }
    throw new NotFoundException("Could not find enum value corresponding to code #" + ordinal);
  }

  /**
   * Construct.
   */
  Month(String name, String shortName, Integer code) {
    this.name = name.trim();
    this.shortName = shortName.trim();
    this.code = code;
  }
}