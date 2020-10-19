/*
 * Copyright (c) 2018 Vikash Madhow
 */

package ma.vi.base.collections;

import ma.vi.base.string.Escape;
import ma.vi.base.util.Convert;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * Simple array utility functions.
 *
 * @author vikash.madhow@gmail.com
 */
public class ArrayUtils {
  /**
   * Unwrap the array into its primitive array form.
   */
  public static int[] unwrap(Integer[] wrapped) {
    int[] unwrapped = new int[wrapped.length];
    for (int i = 0; i < wrapped.length; i++) {
      unwrapped[i] = wrapped[i];
    }
    return unwrapped;
  }

  /**
   * Unwrap the array into its primitive array form.
   */
  public static long[] unwrap(Long[] wrapped) {
    long[] unwrapped = new long[wrapped.length];
    for (int i = 0; i < wrapped.length; i++) {
      unwrapped[i] = wrapped[i];
    }
    return unwrapped;
  }

  /**
   * Unwrap the array into its primitive array form.
   */
  public static double[] unwrap(Double[] wrapped) {
    double[] unwrapped = new double[wrapped.length];
    for (int i = 0; i < wrapped.length; i++) {
      unwrapped[i] = wrapped[i];
    }
    return unwrapped;
  }

  /**
   * Unwrap the array into its primitive array form.
   */
  public static Integer[] wrap(int[] unwrapped) {
    Integer[] wrapped = new Integer[unwrapped.length];
    for (int i = 0; i < unwrapped.length; i++) {
      wrapped[i] = unwrapped[i];
    }
    return wrapped;
  }

  /**
   * Unwrap the array into its primitive array form.
   */
  public static Long[] wrap(long[] unwrapped) {
    Long[] wrapped = new Long[unwrapped.length];
    for (int i = 0; i < unwrapped.length; i++) {
      wrapped[i] = unwrapped[i];
    }
    return wrapped;
  }

  /**
   * Unwrap the array into its primitive array form.
   */
  public static Double[] wrap(double[] unwrapped) {
    Double[] wrapped = new Double[unwrapped.length];
    for (int i = 0; i < unwrapped.length; i++) {
      wrapped[i] = unwrapped[i];
    }
    return wrapped;
  }

  /**
   * Converts the long array into a double array with the same length and content (casted appropriately).
   */
  public static double[] toDoubleArray(final long[] values) {
    double[] doubles = new double[values.length];
    for (int i = 0; i < values.length; i++) {
      doubles[i] = values[i];
    }
    return doubles;
  }

  /**
   * Converts the double array into a long array with the same length and content (casted appropriately).
   * Precision will be lost obviously.
   */
  public static long[] toLongArray(final double[] values) {
    long[] longs = new long[values.length];
    for (int i = 0; i < values.length; i++) {
      longs[i] = (long) values[i];
    }
    return longs;
  }

  /**
   * Returns a copy of the array with the supplied element appended to.
   */
  public static <T> T[] append(T[] array, T element) {
    T[] concat = Arrays.copyOf(array, array.length + 1);
    concat[array.length] = element;
    return concat;
  }

  /**
   * Returns a concatenation of the 2 arrays.
   */
  public static <T> T[] concat(T[] array1, T[] array2) {
    T[] concat = Arrays.copyOf(array1, array1.length + array2.length);
    System.arraycopy(array2, 0, concat, array1.length, array2.length);
    return concat;
  }

  /**
   * Returns a new array with all the elements of the original except those
   * from the index {@code start} to {@code end - 1}. If start == end, no elements
   * are deleted and the passed array is returned unchanged. If the end &lt; start,
   * an {@link IllegalArgumentException} is thrown.
   */
  public static <T> T[] delete(T[] array, int start, int end) {
    int deletedLength = end - start;
    if (deletedLength == 0) {
      return array;
    } else if (deletedLength < 0) {
      throw new IllegalArgumentException("The number of deleted items is less than 0, start=" +
          start + ", end=" + end);
    } else {
      Class<?> componentType = array.getClass().getComponentType();
      T[] deleted = (T[]) Array.newInstance(componentType, array.length - deletedLength);
      int i = 0;
      for (; i < start; i++) {
        deleted[i] = array[i];
      }
      i = end;
      for (; i < array.length; i++) {
        deleted[i - deletedLength] = array[i];
      }
      return deleted;
    }
  }

  /**
   * Returns the first index where value is found in the array, or -1 if
   * the value is not in the array.
   */
  public static <T> int indexOf(T[] array, T value) {
    for (int i = 0; i < array.length; i++) {
      if (value == null ? array[i] == null : value.equals(array[i])) {
        return i;
      }
    }
    return -1;
  }

  /**
   * Returns the last index where value is found in the array, or -1 if
   * the value is not in the array.
   */
  public static <T> int lastIndexOf(T[] array, T value) {
    for (int i = array.length - 1; i >= 0; i--) {
      if (value == null ? array[i] == null : value.equals(array[i])) {
        return i;
      }
    }
    return -1;
  }

  /**
   * Returns an iterator of the elements.
   */
  public static <E> Iterator<E> iterate(E... items) {
    return new ArrayIterator<>(items);
  }

  /**
   * Returns a array of the base component type from a comma-separated list of items.
   */
  public static <T> T[] toArray(String content, Class<T> componentType) {
    String remapped = ARRAY_ESCAPE.map(content);
    List<T> list = new ArrayList<>();
    for (String el : remapped.split(",")) {
      list.add((T) Convert.convert(ARRAY_ESCAPE.demap(el), componentType));
    }
    return list.toArray((T[]) Array.newInstance(componentType, 0));
  }

  public static <T extends Comparable<T>> T[] insertionSort(T[] array) {
    return insertionSort(array, 0, array.length);
  }

  public static <T extends Comparable<T>> T[] insertionSort(T[] array, int start, int end) {
    for (int i = start + 1; i < end; i++) {
      T value = array[i];
      int j = i;
      while (j > start && array[j-1].compareTo(value) > 0) {
        array[j] = array[j-1];
        j -= 1;
      }
      array[j] = value;
    }
    return array;
  }

  public static <T extends Comparable<T>> T[] quickSort(T[] array) {
    return quickSort(array, 0, array.length - 1);
  }

  public static <T extends Comparable<T>> T[] quickSort(T[] array, int start, int end) {
    if (start < end) {
      int p = partition(array, start, end);
      quickSort(array, start, p - 1);
      quickSort(array, p + 1, end);
    }
    return array;
  }

  public static <T extends Comparable<T>> int partition(T[] array, int start, int end) {
    int i = start - 1;
    T pivot = array[end];
    for (int j = start; j < end; j++) {
      if (array[j].compareTo(pivot) < 0) {
        i += 1;
        swap(array, i, j);
      }
    }
    i += 1;
    swap(array, i, end);
    return i;
  }

  public static <T> void swap(T[] array, int i, int j) {
    T temp = array[i];
    array[i] = array[j];
    array[j] = temp;
  }

  public static <T extends Comparable<T>> T kth(T[] array, int k) {
    return kth(array, k, 0, array.length - 1);
  }

  public static <T extends Comparable<T>> T kth(T[] array, int k, int start, int end) {
    if (start <= end) {
      int p = partition(array, start, end);
      int position = p - start + 1;
      if (position < k) {
        return kth(array, k - position, p + 1, end);
      } else if (position > k) {
        return kth(array, k, start, p - 1);
      } else {
        return array[p];
      }
    }
    return null;
  }

  /**
   * Escape commas and square brackets in array elements as these characters
   * are used to encode the start, end and separation of elements in the array
   * encoding (especially in places without native support for arrays).
   */
  public static final Escape ARRAY_ESCAPE = new Escape("\\,[]");

  private ArrayUtils() {}
}