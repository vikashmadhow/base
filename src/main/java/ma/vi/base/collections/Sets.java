/*
 * Copyright (c) 2018 Vikash Madhow
 */

package ma.vi.base.collections;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Set utilities
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class Sets {
  /**
   * Create a hashset from the values.
   */
  public static <T> Set<T> of(T... values) {
    return new HashSet<T>(Arrays.asList(values));
  }

  /**
   * Returns a new set formed by the union of the two supplied sets.
   */
  public static <T> Set<T> union(Set<? extends T> s1, Set<? extends T> s2) {
    Set<T> set = new HashSet<>();
    set.addAll(s1);
    set.addAll(s2);
    return set;
  }

  /**
   * Returns a new set formed by the intersection of the two supplied sets.
   */
  public static <T> Set<T> intersect(Set<? extends T> s1, Set<? extends T> s2) {
    Set<T> set = new HashSet<>();
    set.addAll(s1);
    set.retainAll(s2);
    return set;
  }

  /**
   * Returns a new set formed as the difference s1 - s2.
   */
  public static <T> Set<T> minus(Set<? extends T> s1, Set<? extends T> s2) {
    Set<T> set = new HashSet<>();
    set.addAll(s1);
    set.removeAll(s2);
    return set;
  }

  /**
   * Returns true if s1 contains any element from s2.
   * Returns false if s1 or s2 is empty.
   */
  public static boolean any(Set<?> s1, Set<?> s2) {
    for (Object element : s2) {
      if (s1.contains(element)) {
        return true;
      }
    }
    return false;
  }

  /**
   * Returns true if s1 contains all elements in s2 or if s2 is empty.
   * Returns false otherwise.
   */
  public static boolean all(Set<?> s1, Set<?> s2) {
    for (Object element : s2) {
      if (!s1.contains(element)) {
        return false;
      }
    }
    return true;
  }

  private Sets() {
  }
}