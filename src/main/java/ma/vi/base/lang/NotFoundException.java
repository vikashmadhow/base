/*
 * Copyright (c) 2016 Vikash Madhow
 */

package ma.vi.base.lang;

/**
 * A exception thrown when something was not found by the system.
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class NotFoundException extends RuntimeException {
  public NotFoundException(Throwable cause) {
    super(cause);
  }

  public NotFoundException(String message, Throwable cause) {
    super(message, cause);
  }

  public NotFoundException(String message) {
    super(message);
  }

  public NotFoundException() {
  }
}