/*
 * Copyright (c) 2018 Vikash Madhow
 */

package ma.vi.base.io;

import java.io.IOException;
import java.io.Writer;

/**
 * This writer places a bound on the length of the output that can be written to it. It responds with an IOException if
 * an attempt is made to write beyond this bound.
 *
 * @author vikash.madhow@gmail.com
 */
public class BoundedWriter extends Writer {
  /**
   * Construct a BoundedOutputStream around the specified outputstream with the specified bound.
   *
   * @param out   java.io.OutputStream Wrapped output stream.
   * @param bound Bound length
   * @throws IOException If the provided java.io.OutputStream is null or the bound is negative.
   */
  public BoundedWriter(Writer out, int bound) throws IOException {
    if (out == null) {
      throw new IOException("Writer must not be null");
    }
    if (bound < 0) {
      throw new IOException("Bound must not be negative");
    }

    this.out = out;
    this.bound = bound;
  }

  /**
   * Inherited from java.io.OutputStream
   */
  @Override
  public void flush() throws IOException {
    out.flush();
  }

  /**
   * Inherited from java.io.OutputStream
   */
  @Override
  public void write(int b) throws IOException {
    if (charsWritten >= bound) {
      throw new IOException("Write beyond message bound (" + bound + ")");
    } else {
      out.write(b);
      charsWritten++;
    }
  }

  /**
   * Inherited from java.io.OutputStream
   */
  @Override
  public void write(char[] c) throws IOException {
    write(c, 0, c.length);
  }

  /**
   * Inherited from java.io.OutputStream
   */
  @Override
  public void write(char[] b, int offset, int length) throws IOException {
    if (charsWritten >= bound) {
      throw new IOException("Write beyond message bound (" + bound + ")");
    } else {
      // don't let caller write more bytes than possible
      if (length > bound - charsWritten) {
        throw new IOException("Write beyond message bound (" + bound + ")");
      } else {
        // write as much as possible
        out.write(b, offset, length);
        charsWritten += length;
      }
    }
  }

  /**
   * Inherited from java.io.OutputStream
   */
  @Override
  public void close() throws IOException {
    out.close();
  }

  /**
   * Underlying writer.
   */
  private Writer out = null;

  /**
   * Maximum length (in bytes) of the message. Set on construction.
   */
  private int bound = -1;

  /**
   * Number of bytes that have already been written.
   */
  private int charsWritten = 0;
}
