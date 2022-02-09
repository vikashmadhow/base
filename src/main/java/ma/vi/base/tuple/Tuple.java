/*
 * Copyright (c) 2016 Vikash Madhow
 */

package ma.vi.base.tuple;

import java.io.Serializable;
import java.util.*;

/**
 * Interface of all tuples.
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public interface Tuple extends Iterable<Object>, Serializable, Cloneable {
  /**
   * Returns the number of elements in the tuple.
   */
  int size();

  /**
   * Returns the element as the specified index.
   */
  Object get(int index);

  /**
   * Default implementation of iterates over the array produced by {@link #asArray}.
   */
  default Iterator<Object> iterator() {
    return asList().iterator();
  }

  /**
   * Default implementation uses {@link #size} and {@link #get} to create array representation.
   */
  default Object[] asArray() {
    int size = size();
    Object[] objects = new Object[size];
    for (int i = 0; i < size; i++) {
      objects[i] = get(i);
    }
    return objects;
  }

  default List<Object> asList() {
    return Arrays.asList(asArray());
  }

  /**
   * A utility method to find a tuple among a collection of tuples with an element value at the
   * specified element index.
   */
  static <T extends Tuple> T findByTupleElement(Collection<T> tuples, Object element, int elementIndexInTuple) {
    if (tuples != null) {
      for (T tuple : tuples) {
        Object value = tuple.get(elementIndexInTuple);
        if (Objects.equals(value, element)) {
          return tuple;
        }
      }
    }
    return null;
  }

  /**
   * A utility method to remove tuples among a collection of tuples based on its value at the
   * specified element index. Returns the number of elements removed.
   */
  static <T extends Tuple> int removeByTupleElement(Collection<T> tuples, Object element, int elementIndexInTuple) {
    int removed = 0;
    if (tuples != null) {
      for (Iterator<T> i = tuples.iterator(); i.hasNext(); ) {
        T t = i.next();
        Object value = t.get(elementIndexInTuple);
        if (Objects.equals(value, element)) {
          i.remove();
          removed++;
        }
      }
    }
    return removed;
  }
}
