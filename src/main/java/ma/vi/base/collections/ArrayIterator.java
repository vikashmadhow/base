package ma.vi.base.collections;

import java.util.Iterator;

/**
 * An iterator over arrays.
 *
 * @param <E> The component type of the array to iterate over
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class ArrayIterator<E> implements Iterator<E> {
  /**
   * Creates an iterator that will iterate over the provided items
   */
  @SafeVarargs
  public ArrayIterator(E... items) {
    this.items = items;
  }

  @Override
  public synchronized boolean hasNext() {
    return position < items.length;
  }

  @Override
  public synchronized E next() {
    return items[position++];
  }

  /**
   * Array to iterate over
   */
  private final E[] items;

  /**
   * Current position of iterator which is the position of the item that
   * will be returned by {@link #next()}.
   */
  private int position = 0;
}
