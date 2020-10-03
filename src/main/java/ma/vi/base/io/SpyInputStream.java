/*
 * Copyright (c) 2018 Vikash Madhow
 */

package ma.vi.base.io;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * An input stream that can be used to "spy" on another input stream. It works by intercepting all reads on the spied
 * input stream and sending the read data to a target output stream such as System.out.
 *
 * @author vikash.madhow@gmail.com
 */
public class SpyInputStream extends FilterInputStream {
  /**
   * Construct a SpyInputStream wrapping the provided java.io.InputStream and sending intercepted reads to the specified
   * output stream.
   *
   * @param in     java.io.InputStream to wrap
   * @param target Target output stream for intercepted reads
   * @throws IOException If in or target is null
   */
  public SpyInputStream(InputStream in, OutputStream target) throws IOException {
    super(in);
    if (target == null) {
      throw new IOException("Target output stream must not be null");
    }
    this.target = target;
  }

  /**
   * Reads a byte.
   *
   * @return The byte read or -1 if end-of-input reached
   * @throws IOException If an error occurs during input
   */
  @Override
  public int read() throws IOException {
    int b = super.read();
    target.write(b);
    return b;
  }

  /**
   * Reads some bytes into the byte array.
   *
   * @param b byte array where read bytes will be copied
   * @return The number of bytes read
   * @throws IOException If an error occurs during input
   */
  @Override
  public int read(byte[] b) throws IOException {
    int count = super.read(b);
    target.write(b, 0, count);
    return count;
  }

  /**
   * Reads some bytes into the byte array.
   *
   * @param b      byte array where the read bytes will be copied
   * @param offset Read bytes will be copied into the byte array starting at this position
   * @param length Maximum number of bytes to read
   * @return The number of bytes actually read. May be less than length.
   * @throws IOException If an error occurs during input
   */
  @Override
  public int read(byte[] b, int offset, int length) throws IOException {
    int count = super.read(b, offset, length);
    target.write(b, offset, count);
    return count;
  }

  /**
   * Closes this stream.
   *
   * @throws IOException If an I/O error occurs
   */
  @Override
  public void close() throws IOException {
    super.close();
    target.close();
  }

  /**
   * Target output stream to send spied bytes to.
   */
  private OutputStream target;
}