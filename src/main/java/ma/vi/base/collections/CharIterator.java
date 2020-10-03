package ma.vi.base.collections;

import java.util.Iterator;

/**
 * An iterator over characters in character sequence.
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class CharIterator implements Iterator<Character> {
  /**
   * Creates an iterator that will iterate over the provided items
   */
  public CharIterator(CharSequence sequence) {
    this.sequence = sequence;
  }

  @Override
  public synchronized boolean hasNext() {
    return position < sequence.length();
  }

  @Override
  public synchronized Character next() {
    return sequence.charAt(position++);
  }

  /**
   * Character sequence to iterate over
   */
  private final CharSequence sequence;

  /**
   * Current position of iterator which is the position of the item that
   * will be returned by {@link #next()}.
   */
  private int position = 0;
}
