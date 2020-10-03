/*
 * Copyright (c) 2018 Vikash Madhow
 */

package ma.vi.base.io;

import java.io.IOException;
import java.io.OutputStream;

/**
 * <p> This output stream places a bound on the length of the output that can be written to it. It responds with an
 * IOException if an attempt is made to write beyond this bound. </p>
 *
 * @author vikash.madhow@gmail.com
 */
public class BoundedOutputStream extends OutputStream {
  /**
   * Underlying output stream.
   */
  protected OutputStream out = null;

  /**
   * Maximum length (in bytes) of the message. Set on construction.
   */
  protected int bound = -1;

  /**
   * Number of bytes that have already been written.
   */
  protected int bytesWritten = 0;

  /**
   * Construct a BoundedOutputStream around the specified outputstream with the specified bound.
   *
   * @param out   java.io.OutputStream Wrapped output stream.
   * @param bound Bound length
   * @throws IOException If the provided java.io.OutputStream is null or the bound is negative.
   */
  public BoundedOutputStream(OutputStream out, int bound) throws IOException {
    if (out == null) {
      throw new IOException("OutputStream must not be null");
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
    if (bytesWritten >= bound) {
      throw new IOException("Write beyond message bound (" + bound + ")");
    } else {
      out.write(b);
      bytesWritten++;
    }
  }

  /**
   * Inherited from java.io.OutputStream
   */
  @Override
  public void write(byte[] b) throws IOException {
    write(b, 0, b.length);
  }

  /**
   * Inherited from java.io.OutputStream
   */
  @Override
  public void write(byte[] b, int offset, int length) throws IOException {
    if (bytesWritten >= bound) {
      throw new IOException("Write beyond message bound (" + bound + ")");
    } else {
      // don't let caller write more bytes than possible
      if (length > bound - bytesWritten) {
        throw new IOException("Write beyond message bound (" + bound + ")");
      } else {
        // write as much as possible
        out.write(b, offset, length);
        bytesWritten += length;
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
}
