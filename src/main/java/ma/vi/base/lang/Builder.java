/*
 * Copyright (c) 2016 Vikash Madhow
 */

package ma.vi.base.lang;

/**
 * A builder knows how to build things. They are useful to
 * override configuration defaults in a nice fluent API before
 * building objects with substantial configuration options.
 *
 * <p>
 * An example builder usage would be:
 * <pre>
 * T t = TBuilder.newBuilder().x(x).y(y).build()
 * </pre>
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public interface Builder<T> {
  /**
   * Builds and return the required object.
   */
  T build();
}