/*
 * Copyright (c) 2018 Vikash Madhow
 */

package ma.vi.base.io;

import java.io.IOException;
import java.io.Reader;

/**
 * <p> This reader places a bound on the length of the input that can be read from it. It responds with an IOException
 * if an attempt is made to read beyond this bound. </p>
 *
 * @author vikash.madhow@gmail.com
 */
public class BoundedReader extends Reader {
  /**
   * Underlying reader.
   */
  private Reader in = null;

  /**
   * Content length (in bytes) of the message. Set on construction.
   */
  private int bound = -1;

  /**
   * Number of bytes that have been read from HTTP body by the various read() methods. This is used to prevent reading
   * beyond the HTTP body.
   */
  private int charsRead = 0;

  /**
   * Construct a BoundedInputStream wrapping the provided java.io.InputStream and with the provided bound.
   *
   * @param in    java.io.InputStream to wrap
   * @param bound Bounded length
   * @throws IOException If the provided java.io.InputStream is null or if the bound is negative.
   */
  public BoundedReader(Reader in, int bound) throws IOException {
    if (in == null) {
      throw new IOException("Reader must not be null");
    }
    if (bound < 0) {
      throw new IOException("Bound must not be negative");
    }

    this.in = in;
    this.bound = bound;
  }

  /**
   * Returns the number of bytes that can be read without blocking
   */
  public int available() throws IOException {
    if (charsRead >= bound) {
      return 0;
    } else {
      return bound - charsRead;
    }
  }

  /**
   * Reads a byte.
   *
   * @return The byte read or -1 if end-of-input reached
   * @throws IOException If an error occurs during input
   */
  @Override
  public int read() throws IOException {
    if (charsRead == bound) {
      charsRead++;
      return -1;
    } else if (charsRead > bound) {
      throw new IOException("Read beyond end-of-stream");
    } else {
      charsRead++;
      return in.read();
    }
  }

  /**
   * Reads some bytes into the byte array.
   *
   * @param c char array where read characters will be copied
   * @return The number of bytes read
   * @throws IOException If an error occurs during input
   */
  @Override
  public int read(char[] c) throws IOException {
    return read(c, 0, c.length);
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
  public int read(char[] b, int offset, int length) throws IOException {
    if (charsRead == bound) {
      charsRead++;
      return -1;
    } else if (charsRead > bound) {
      throw new IOException("Read beyond end-of-stream");
    } else {
      // don't let caller try to read more bytes than available
      int available = bound - charsRead;
      if (length > available) {
        length = available;
      }

      // read as much as possible
      int count = 0, read = 0;
      while ((length > 0)
          && ((count = in.read(b, offset, length)) != -1)) {
        length -= count;
        offset += count;
        read += count;
      }
      charsRead += read;
      return read == 0 ? -1 : read;
    }
  }

  /**
   * Closes the underlying stream.
   *
   * @throws IOException If an I/O error occurs
   */
  @Override
  public void close() throws IOException {
    in.close();
  }
}
