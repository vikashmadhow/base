/*
 * Copyright (c) 2016 Vikash Madhow
 */

package ma.vi.base.lang;

import sun.misc.Unsafe;

import java.lang.reflect.Field;

import static ma.vi.base.lang.Errors.unchecked;

/**
 * Loads {@link Unsafe} by extracting the "theUnsafe" field of
 * sun.misc.Unsafe through reflection. This technique is taken from how
 * sun.corba.Bridge obtains a reference to the Unsafe class.
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public final class UnsafeLoader {
  /* Load Unsafe from theUnsafe private field in sun.misc.Unsafe class. This
   * code is modified from sun.corba.Bridge class. The static unsafe
   * method in the Unsafe class check that the caller's classloader is the
   * System Classloader before returning an instance; otherwise it throws a
   * SecurityException and therefore cannot be used anywhere but inside the
   * java.* and sun.* classes. */
  static {
    try {
      Field field = Unsafe.class.getDeclaredField("theUnsafe");
      field.setAccessible(true);
      unsafe = (Unsafe) field.get(null);
    } catch (Exception e) {
      exception = unchecked(e);
    }
  }

  /**
   * Returns a reference to the Unsafe object or throws any Exception that
   * was caught when the Unsafe object was being loaded.
   */
  public static Unsafe unsafe() {
    if (exception != null) {
      throw exception;
    } else if (unsafe == null) {
      throw new RuntimeException("Error: unsafe reference is null");
    } else {
      return unsafe;
    }
  }

  private UnsafeLoader() {
  }

  /**
   * Sole reference to the Unsafe object for creating objects without invoking
   * any constructor.
   */
  private static Unsafe unsafe;

  /**
   * Any exception caught during loading of the unsafe reference is stored
   * in this field and thrown when the unsafe is requested through the
   * {@link #unsafe()} method.
   */
  private static RuntimeException exception = null;
}