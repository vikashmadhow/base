/*
 * Copyright (c) 2018 Vikash Madhow
 */

package ma.vi.base.circular;

import java.util.NoSuchElementException;

/**
 * A simple circular queue of longs backed by an array where items
 * are written at the tail of the array and read off the head. When the tail
 * pointer reaches the end of the buffer, it wraps around to the start.
 * <p>
 * This buffer also supports push-back of items through the
 * {@link #unread()} and {@link #push(long)} methods.
 *
 * @author vikash.madhow@gmail.com
 */
public class CircularLongQueue {

  /**
   * Creates a new circular buffer of the specified size.
   *
   * @param size The buffer size. Must be greater than 1.
   */
  @SuppressWarnings("unchecked")
  public CircularLongQueue(int size) {
    if (size <= 1) {
      throw new IllegalArgumentException("Queue size must be greater than 1.");
    }
    this.size = size;
    this.buffer = new long[size];
  }

  /**
   * Returns true if the buffer is empty.
   */
  public boolean isEmpty() {
    return start >= end;
  }

  /**
   * Returns true if the buffer is full.
   */
  public boolean isFull() {
    return end - start >= size;
  }

  /**
   * Returns the first item in the queue and moves the read pointer
   * by one position.
   *
   * @throws NoSuchElementException If there are no more items to read.
   */
  public long pop() {
    if (isEmpty()) {
      throw new NoSuchElementException("Queue is empty.");
    } else {
      return buffer[modSize(postIncStart())];
    }
  }

  /**
   * Returns the first item in the queue and without changing the read pointer.
   *
   * @throws NoSuchElementException If there are no more items to read.
   */
  public long peek() {
    if (isEmpty()) {
      throw new NoSuchElementException("Queue is empty.");
    } else {
      return buffer[modSize(start)];
    }
  }

  /**
   * Unread the last item read by decrementing the pointer to the
   * next item to read in the queue. If this method is called
   * before any items have been read, null will be returned when
   * the top of the queue is read next.
   */
  public void unread() {
    decStart();
  }

  /**
   * Pushes one item at the front of the buffer such that it will
   * be the next item read.
   *
   * @param item The item to push back.
   */
  public void push(long item) {
    unread();
    buffer[modSize(start)] = item;
  }

  /**
   * Adds an item at the end of the queue, returning the item
   * previously at that position. This method never throws an
   * exception, wrapping around at the end of the queue, when
   * there is no space left.
   * <p>
   * Returns the item which was previously at the position where
   * the new item is added.
   */
  public long add(long item) {
    int next = modSize(end);
    long existing = buffer[next];
    buffer[next] = item;
    end += 1;
    return existing;
  }

  /**
   * Returns x modulus the size of the buffer.
   */
  private int modSize(int x) {
    return mod(x, size);
  }

  /**
   * Increment x by 1 taking care of properly wrapping around
   * {@code Integer.MAX_VALUE} and ensuring that the resulting
   * after overflowing has the same modulus respective to the
   * specified modulo value irrespective of whether overflow
   * happened or not.
   *
   * @param x      The value to increment.
   * @param modulo The modulus value.
   * @return x incremented with possible wrap-around to the negative side
   * if there was an overflow.
   */
  private int modInc(int x, int modulo) {
    if (x < Integer.MAX_VALUE) {
      x++;
    } else {
      /*
       * Wrap around to the negative side ensuring that
       * the next expected modulus value is preserved
       * despite the wraparound.
       */
      int remainder = mod(x, modulo);
      int nextRemainder = remainder == modulo - 1 ? 0 : remainder + 1;
      x = Integer.MIN_VALUE;
      while (mod(x, modulo) != nextRemainder) {
        x++;
      }
    }
    return x;
  }

  private int modDec(int x, int modulo) {
    if (x > Integer.MIN_VALUE) {
      x--;
    } else {
      /*
       * Wrap around to the positive side ensuring that
       * the next expected modulus value is preserved
       * despite the wraparound.
       */
      int remainder = mod(x, modulo);
      int prevRemainder = remainder == 0 ? modulo - 1 : remainder - 1;
      x = Integer.MAX_VALUE;
      while (mod(x, modulo) != prevRemainder) {
        x--;
      }
    }
    return x;
  }

  private int incStart() {
    return start = modInc(start, size);
  }

  private int decStart() {
    return start = modDec(start, size);
  }

  /**
   * Returns the current value of start and increments it with
   * {@link #modInc(int, int)} w.r.t to the size of the queue to
   * cater for overflow wrap-around.
   */
  private int postIncStart() {
    int value = start;
    start = modInc(start, size);
    return value;
  }

  private int postDecStart() {
    int value = start;
    start = modDec(start, size);
    return value;
  }

  /**
   * Returns a modulus b. The % operator in Java returns the remainder
   * between a and b which works fine as modulus unless a is negative.
   * This method takes care of that possibility.
   */
  public static int mod(int a, int b) {
    return (a % b + b) % b;
  }

  /**
   * Buffer size.
   */
  private final int size;

  /**
   * Buffer.
   */
  private final long[] buffer;

  /**
   * Position in buffer where the next character is to be read.
   */
  private int start;

  /**
   * end - 1 = position in buffer where the last character to be read
   * is available.
   */
  private int end;
}