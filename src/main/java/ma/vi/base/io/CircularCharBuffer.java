/*
 * Copyright (c) 2018 Vikash Madhow
 */

package ma.vi.base.io;

import java.io.IOException;
import java.io.Reader;
import java.util.NoSuchElementException;
import java.util.Objects;

/**
 * A simple circular character buffer backed by an array where characters
 * are written at the tail of the array and read off the head. When the tail
 * pointer reaches the end of the buffer, it wraps around to the start.
 * <p>
 * This buffer also supports push-back of characters through the
 * {@link #unread()} and {@link #push(char)} methods.
 *
 * @author vikash.madhow@gmail.com
 */
public class CircularCharBuffer {

  /**
   * Creates a new circular buffer for the specified reader and
   * of the specified size (in characters).
   *
   * @param reader The reader to buffer.
   * @param size   The buffer size. Must be greater than 1.
   */
  public CircularCharBuffer(Reader reader, int size) {
    Objects.requireNonNull(reader, "Reader is null.");
    if (size <= 1) {
      throw new IllegalArgumentException("Buffer size must be greater than 1.");
    }
    this.in = reader;
    this.size = size;
    this.buffer = new char[size];
  }

  /**
   * True if there are more characters to read from the buffer
   * and the reader.
   */
  public boolean hasNext() {
    if (start < end) {
      return true;
    } else if (!endOfStream) {
      readMore();
      return start < end;
    } else {
      return false;
    }
  }

  /**
   * Returns the next character in the buffer or the reader.
   *
   * @throws NoSuchElementException If there are no more characters to read.
   */
  public char next() {
    if (start >= end) {
      checkEndOfStream();
      readMore();
    }
    if (start >= end) {
      throw new NoSuchElementException("No more characters.");
    } else {
      return buffer[modSize(start++)];
    }
  }

  /**
   * Unread the last character read by decrementing pointer to the
   * next character to read in the buffer. If this method is called
   * before any character has been read, the null character will be
   * returned when the buffer is read next.
   *
   * @throws NoMoreSpaceException If the buffer is full.
   */
  public void unread() {
    checkBufferFull();
    --start;
  }

  /**
   * Pushes one character at the front of the buffer such that it will
   * be the next character read.
   *
   * @param c The character to push back.
   * @throws NoMoreSpaceException If the buffer is full.
   */
  public void push(char c) {
    unread();
    buffer[modSize(start)] = c;
  }

  /**
   * Returns true if the buffer is full.
   */
  public boolean isFull() {
    return end - start >= size;
  }

  /**
   * Read from the reader into the buffer. Triggered internally by hasNext
   * and next.
   */
  private void readMore() {
    checkEndOfStream();
    checkBufferFull();

    try {
      int offset = modSize(end);
      int startAbs = modSize(start);
      int length = offset < startAbs ? startAbs - offset : size - offset;

      int read = in.read(buffer, offset, length);
      if (read < 0) {
        endOfStream = true;
      } else {
        end += read;
      }
    } catch (IOException ioe) {
      throw new RuntimeException(ioe);
    }
  }

  /**
   * Returns x modulus the size of the buffer.
   */
  private int modSize(int x) {
    return mod(x, size);
  }

  /**
   * Returns a modulus b. The % operator in Java returns the remainder
   * between a and b which works fine as modulus unless a is negative.
   * This method takes care of that possibility.
   */
  private int mod(int a, int b) {
    return (a % b + b) % b;
  }

  /**
   * @throws IllegalStateException If the end of stream has been reached.
   */
  private void checkEndOfStream() {
    if (endOfStream) {
      throw new IllegalStateException("End-of-stream reached.");
    }
  }

  /**
   * @throws NoMoreSpaceException If the buffer is full.
   */
  private void checkBufferFull() {
    if (isFull()) {
      throw new NoMoreSpaceException("Buffer is full.");
    }
  }

  /**
   * Underlying reader.
   */
  private final Reader in;

  /**
   * Buffer size.
   */
  private final int size;

  /**
   * Buffer.
   */
  private final char[] buffer;

  /**
   * Position in buffer where the next character is to be read.
   */
  private int start;

  /**
   * end - 1 = position in buffer where the last character to be read
   * is available.
   */
  private int end;

  /**
   * Set to true when the end of stream has been reached.
   */
  private boolean endOfStream;
}