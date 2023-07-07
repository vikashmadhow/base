/*
 * Copyright (c) 2018 Vikash Madhow
 */

package ma.vi.base.util;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DecimalFormat;

import static java.math.RoundingMode.HALF_UP;

/**
 * Utility functions on numbers.
 *
 * @author vikash.madhow@gmail.com
 */
public class Numbers {
  /**
   * Return the shortest number object which can hold the passed value.
   */
  public static Number shortestIntegralType(long value) {
    return value >= -128           && value <= 127            ? (byte) value :
           value >= -32_768        && value <= 32_767         ? (short)value :
           value >= -2_147_483_648 && value <= 2_147_483_647  ? (int)  value : value;
  }

  /**
   * Return true if the specified number is an integer.
   */
  public static boolean isIntegral(Number number) {
    return number instanceof Byte
        || number instanceof Short
        || number instanceof Integer
        || number instanceof Long
        || number instanceof BigInteger;
  }

  /**
   * Return true if the specified type is an integer.
   */
  public static boolean isIntegral(Class<?> numberType) {
    return Byte      .class.isAssignableFrom(numberType)
        || Short     .class.isAssignableFrom(numberType)
        || Integer   .class.isAssignableFrom(numberType)
        || Long      .class.isAssignableFrom(numberType)
        || byte      .class.isAssignableFrom(numberType)
        || short     .class.isAssignableFrom(numberType)
        || int       .class.isAssignableFrom(numberType)
        || long      .class.isAssignableFrom(numberType)
        || BigInteger.class.isAssignableFrom(numberType);
  }

  /**
   * Return true if the specified number is a areal.
   */
  public static boolean isReal(Number number) {
    return number instanceof Float
        || number instanceof Double
        || number instanceof BigDecimal;
  }

  /**
   * Return true if the specified type is a real number.
   */
  public static boolean isReal(Class<?> numberType) {
    return Float     .class.isAssignableFrom(numberType)
        || Double    .class.isAssignableFrom(numberType)
        || float     .class.isAssignableFrom(numberType)
        || double    .class.isAssignableFrom(numberType)
        || BigDecimal.class.isAssignableFrom(numberType);
  }

  /**
   * Promote the number to long.
   */
  public static Long promoteToLong(Number value) {
    return value == null         ? null
         : value instanceof Long ? (Long)value
         : value.longValue();
  }

  /**
   * Promote the number to double.
   */
  public static Double promoteToDouble(Number value) {
    return value == null           ? null
         : value instanceof Double ? (Double)value
         : value.doubleValue();
  }

  /**
   * Converts a string representation to the longest possible number throwing
   * NumberFormatException if not possible. Returns null if the string is null
   * or empty.
   */
  public static Number convert(String number) throws NumberFormatException {
    if (number == null) {
      return null;
    } else {
      number = number.trim();
      if (number.length() == 0) {
        return null;
      } else {
        number = number.replaceAll(",", "");
        if (number.contains(".") || number.contains("E") || number.contains("e")) {
          try {
            return Double.valueOf(number);
          } catch (NumberFormatException e1) {
            try {
              return Float.valueOf(number);
            } catch (NumberFormatException e2) {
            }
          }
        }
        try {
          return Long.valueOf(number);
        } catch (NumberFormatException e3) {
          try {
            return Integer.valueOf(number);
          } catch (NumberFormatException e4) {
            try {
              return Short.valueOf(number);
            } catch (NumberFormatException e5) {
              return Byte.valueOf(number);
            }
          }
        }
      }
    }
  }

  /**
   * Converts a number to the specified target number type.
   */
  public static Number convert(Number number, Class<?> toNumberType) {
    if (number == null || toNumberType == null) {
      return number;
    }
    if (toNumberType.isPrimitive()) {
      toNumberType = Classes.getWrapperType(toNumberType);
    }

    // Round instead of truncating
    if (isReal(number) && isIntegral(toNumberType)) {
      number = Math.round(number instanceof Double ? number.doubleValue() : number.floatValue());
    }

    if      (Byte   .class.equals(toNumberType))  { return number.byteValue();   }
    else if (Short  .class.equals(toNumberType))  { return number.shortValue();  }
    else if (Integer.class.equals(toNumberType))  { return number.intValue();    }
    else if (Long   .class.equals(toNumberType))  { return number.longValue();   }
    else if (Float  .class.equals(toNumberType))  { return number.floatValue();  }
    else if (Double .class.equals(toNumberType))  { return number.doubleValue(); }

    throw new IllegalArgumentException("Could not convert " + number
        + " of type " + number.getClass() + " to " + toNumberType);
  }

  /**
   * Number equality, ignoring actual number type.
   */
  public static boolean equals(Number number1, Number number2) {
    if (number1 == null || number2 == null) {
      return number1 == number2;
    } else {
      return isIntegral(number1)
           ? promoteToLong(number1).equals(promoteToLong(number2))
           : promoteToDouble(number1).equals(promoteToDouble(number2));
    }
  }

  /**
   * Returns the minimum value in the given variable list of numbers.
   */
  public static int min(int... nums) {
    if (nums.length == 0) {
      return Integer.MIN_VALUE;
    } else {
      int min = nums[0];
      for (int i = 1; i < nums.length; i++) {
        if (nums[i] < min) {
          min = nums[i];
        }
      }
      return min;
    }
  }

  /**
   * Returns the minimum value in the given variable list of numbers.
   */
  public static long min(long... nums) {
    if (nums.length == 0) {
      return Long.MIN_VALUE;
    } else {
      long min = nums[0];
      for (int i = 1; i < nums.length; i++) {
        if (nums[i] < min) {
          min = nums[i];
        }
      }
      return min;
    }
  }

  /**
   * Returns the minimum value in the given variable list of numbers.
   */
  public static float min(float... nums) {
    if (nums.length == 0) {
      return Float.MIN_VALUE;
    } else {
      float min = nums[0];
      for (int i = 1; i < nums.length; i++) {
        if (nums[i] < min) {
          min = nums[i];
        }
      }
      return min;
    }
  }

  /**
   * Returns the minimum value in the given variable list of numbers.
   */
  public static double min(double... nums) {
    if (nums.length == 0) {
      return Float.MIN_VALUE;
    } else {
      double min = nums[0];
      for (int i = 1; i < nums.length; i++) {
        if (nums[i] < min) {
          min = nums[i];
        }
      }
      return min;
    }
  }

  /**
   * Returns the maximum value in the given variable list of numbers.
   */
  public static int max(int... nums) {
    if (nums.length == 0) {
      return Integer.MAX_VALUE;
    } else {
      int max = nums[0];
      for (int i = 1; i < nums.length; i++) {
        if (nums[i] > max) {
          max = nums[i];
        }
      }
      return max;
    }
  }

  /**
   * Returns the maximum value in the given variable list of numbers.
   */
  public static long max(long... nums) {
    if (nums.length == 0) {
      return Long.MAX_VALUE;
    } else {
      long max = nums[0];
      for (int i = 1; i < nums.length; i++) {
        if (nums[i] > max) {
          max = nums[i];
        }
      }
      return max;
    }
  }

  /**
   * Returns the maximum value in the given variable list of numbers.
   */
  public static float max(float... nums) {
    if (nums.length == 0) {
      return Float.MAX_VALUE;
    } else {
      float max = nums[0];
      for (int i = 1; i < nums.length; i++) {
        if (nums[i] > max) {
          max = nums[i];
        }
      }
      return max;
    }
  }

  /**
   * Returns the maximum value in the given variable list of numbers.
   */
  public static double max(double... nums) {
    if (nums.length == 0) {
      return Float.MAX_VALUE;
    } else {
      double max = nums[0];
      for (int i = 1; i < nums.length; i++) {
        if (nums[i] > max) {
          max = nums[i];
        }
      }
      return max;
    }
  }

  public static double round(double value) {
    return round(value, 0);
  }

  public static double round(double value, int decimals) {
//    NumberFormat nf = NumberFormat.getNumberInstance();
//    nf.setRoundingMode(HALF_UP);
//    nf.setMaximumFractionDigits(decimals);
//    return parseDouble(nf.format(value));
    return new BigDecimal(String.valueOf(value))
                .setScale(decimals, HALF_UP)
                .doubleValue();
  }

  private Numbers() {}

  public static final DecimalFormat MONEY_FORMAT;
  public static final DecimalFormat INTEGER_FORMAT;

  static {
    MONEY_FORMAT = new DecimalFormat();
    MONEY_FORMAT.setGroupingUsed(true);
    MONEY_FORMAT.setMinimumFractionDigits(2);
    MONEY_FORMAT.setMaximumFractionDigits(2);

    INTEGER_FORMAT = new DecimalFormat();
    INTEGER_FORMAT.setGroupingUsed(true);
    INTEGER_FORMAT.setMinimumFractionDigits(0);
    INTEGER_FORMAT.setMaximumFractionDigits(0);
  }
}