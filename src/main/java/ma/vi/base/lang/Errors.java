/*
 * Copyright (c) 2016 Vikash Madhow
 */

package ma.vi.base.lang;

import java.util.concurrent.Callable;
import java.util.function.Predicate;

/**
 * A utility class for working with Exceptions and other errors.
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class Errors {
  public static String rootErrorMessage(Throwable e) {
    return rootErrorMessage(e, t -> false);
  }

  /**
   * Returns the message attached to the root cause of the specified exception
   * or the name of the root cause if latter is null.
   */
  public static String rootErrorMessage(Throwable e, Predicate<Throwable> stopCondition) {
    Throwable cause;
    String error = e.getMessage();
    while (!stopCondition.test(e) && (cause = e.getCause()) != null) {
      e = cause;
      if (e.getMessage() != null) {
        error = e.getMessage();
      }
    }
    if (error == null) {
      error = e.toString();
    }
    return error;
  }

  /**
   * Return the deepest non-null cause of the exception.
   */
  public static Throwable rootCause(Throwable e) {
    Throwable cause;
    while ((cause = e.getCause()) != null) {
      e = cause;
    }
    return e;
  }

  public static RuntimeException unchecked(Throwable e) {
    return unchecked(e, null);
  }

  /**
   * If e is a runtime exception (i.e. an unchecked exception), cast and return it
   * otherwise wrap it into one. This is useful to treat any exceptions as unchecked.
   */
  public static RuntimeException unchecked(Throwable e, String message) {
    return e instanceof RuntimeException
           ? (RuntimeException) e
           : new RuntimeException(message, e);
  }

  public static <T> T unchecked(Callable<T> func) {
    return unchecked(func, null);
  }

  /**
   * Runs the callable, trapping and rethrowing any checked exception as unchecked runtime exceptions.
   */
  public static <T> T unchecked(Callable<T> func, String message) {
    try {
      return func.call();
    } catch (Exception e) {
      throw unchecked(e, message);
    }
  }

  public static void unchecked(Try func) {
    unchecked(func, null);
  }

  /**
   * Runs the Runnable, trapping and rethrowing any checked exception as unchecked runtime exceptions.
   */
  public static void unchecked(Try func, String message) {
    try {
      func.call();
    } catch (Exception e) {
      throw unchecked(e, message);
    }
  }

  /**
   * A functional interface for wrapping a block of code which can throw an exception
   * to be passed for execution to the {@link #unchecked(Try)} method.
   */
  @FunctionalInterface
  public interface Try {
    void call() throws Exception;
  }

  private Errors() {
  }
}
