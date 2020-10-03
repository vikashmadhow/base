/*
 * Copyright (c) 2018 Vikash Madhow
 */

package ma.vi.base.io;

/**
 * Thrown if there is no more space left in a data structure
 * for executing some operation.
 *
 * @author vikash.madhow@gmail.com
 */
public class NoMoreSpaceException extends RuntimeException {
  public NoMoreSpaceException() {
  }

  public NoMoreSpaceException(String message) {
    super(message);
  }

  public NoMoreSpaceException(String message, Throwable cause) {
    super(message, cause);
  }

  public NoMoreSpaceException(Throwable cause) {
    super(cause);
  }

  public NoMoreSpaceException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }
}