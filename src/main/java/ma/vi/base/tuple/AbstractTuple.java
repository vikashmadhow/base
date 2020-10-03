/*
 * Copyright (c) 2016 Vikash Madhow
 */

package ma.vi.base.tuple;

import java.util.Iterator;
import java.util.Objects;

/**
 * Provides a default implementation of the java.lang.Object methods for a tuple.
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public abstract class AbstractTuple implements Tuple {
  @Override
  public boolean equals(Object obj) {
    if (obj == this) {
      return true;
    }
    if (!(obj instanceof Tuple)) {
      return false;
    }
    Tuple other = (Tuple) obj;
    int size = size();
    if (size != other.size()) {
      return false;
    }
    for (int i = 0; i < size; i++) {
      Object a = get(i);
      Object b = other.get(i);
      if (!Objects.equals(a, b)) {
        return false;
      }
    }
    return true;
  }

  @Override
  public int hashCode() {
    int hash = 5;
    for (int i = 0; i < size(); i++) {
      Object element = get(i);
      hash = 23 * hash + (element != null ? element.hashCode() : 0);
    }
    return hash;
  }

  @Override
  public String toString() {
    StringBuilder s = new StringBuilder("(");
    for (int i = 0; i < size(); i++) {
      s.append(i == 0 ? "" : ", ").append(get(i));
    }
    return s.append(')').toString();
  }

  @Override
  public Tuple clone() throws CloneNotSupportedException {
    return (Tuple) super.clone();
  }

  /**
   * Implementation of iterator using the {@link #asArray} method of this class (instead
   * of the default interface one) which is slightly better as it caches the created
   * array.
   */
  @Override
  public Iterator<Object> iterator() {
    return asList().iterator();
  }

  /**
   * Implementation using {@link #size} and {@link #get} to create array representation
   * caching the array on first creation.
   */
  @Override
  public Object[] asArray() {
    if (objects == null) {
      int size = size();
      objects = new Object[size];
      for (int i = 0; i < size; i++) {
        objects[i] = get(i);
      }
    }
    return objects;
  }

  /**
   * Cache of the array of elements, used for iterating.
   */
  private transient Object[] objects;
}
