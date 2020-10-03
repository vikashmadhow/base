/*
 * Copyright (c) 2018 Vikash Madhow
 */

package ma.vi.base.io;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * An output stream that can be used to "spy" on another output stream. It works by intercepting all writes on the spied
 * output stream and sending the written data to a target output stream such as System.out.
 *
 * @author vikash.madhow@gmail.com
 */
public class SpyOutputStream extends FilterOutputStream {
  /**
   * Target output stream to send spied bytes to.
   */
  protected OutputStream target = null;

  /**
   * Construct a SpyOutputStream wrapping the provided java.io.OutputStream and sending intercepted writes to the
   * specified output stream.
   *
   * @param out    java.io.OutputStream to wrap
   * @param target Target output stream for intercepted reads
   * @throws IOException If out or target is null
   */
  public SpyOutputStream(OutputStream out, OutputStream target) throws IOException {
    super(out);
    if (target == null) {
      throw new IOException("Target output stream must not be null");
    }

    this.target = target;
  }

  /**
   * Writes a byte.
   *
   * @throws IOException If an error occurs during output
   */
  @Override
  public void write(int b) throws IOException {
    super.write(b);
    target.write(b);
  }

  /**
   * Write some bytes
   *
   * @param b byte array to write
   * @throws IOException If an error occurs during input
   */
  @Override
  public void write(byte[] b) throws IOException {
    super.write(b);
    target.write(b);
  }

  /**
   * Write some bytes
   *
   * @param b      byte array
   * @param offset Writing will start at this offset in the array
   * @param length The number of bytes starting from offset to write
   * @throws IOException If an error occurs during output
   */
  @Override
  public void write(byte[] b, int offset, int length) throws IOException {
    super.write(b, offset, length);
    target.write(b, offset, length);
  }

  @Override
  public void close() throws IOException {
    super.close();
    target.close();
  }
}
