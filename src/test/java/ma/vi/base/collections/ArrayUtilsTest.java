package ma.vi.base.collections;

import junit.framework.TestCase;

import static ma.vi.base.collections.ArrayUtils.*;
import static org.junit.Assert.assertArrayEquals;

/**
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class ArrayUtilsTest extends TestCase {
  public void testInsertionSort() {
    assertArrayEquals(insertionSort(new Integer[0]), new Integer[0]);
    assertArrayEquals(insertionSort(new Integer[]{1}), new Integer[]{1});
    assertArrayEquals(insertionSort(new Integer[]{1,2,3,4,5,6,7}), new Integer[]{1,2,3,4,5,6,7});
    assertArrayEquals(insertionSort(new Integer[]{7,6,5,4,3,2,1}), new Integer[]{1,2,3,4,5,6,7});
    assertArrayEquals(insertionSort(new Integer[]{4,6,6,4,3,3,3}), new Integer[]{3,3,3,4,4,6,6});
  }

  public void testQuickSort() {
    assertArrayEquals(quickSort(new Integer[0]), new Integer[0]);
    assertArrayEquals(quickSort(new Integer[]{1}), new Integer[]{1});
    assertArrayEquals(quickSort(new Integer[]{1,2,3,4,5,6,7}), new Integer[]{1,2,3,4,5,6,7});
    assertArrayEquals(quickSort(new Integer[]{7,6,5,4,3,2,1}), new Integer[]{1,2,3,4,5,6,7});
    assertArrayEquals(quickSort(new Integer[]{4,6,6,4,3,3,3}), new Integer[]{3,3,3,4,4,6,6});
  }

  public void testKth() {
    assertSame(1, kth(new Integer[]{5,6,4,7,2,1,10,3,9,8}, 1));
    assertSame(2, kth(new Integer[]{5,6,4,7,2,1,10,3,9,8}, 2));
    assertSame(3, kth(new Integer[]{5,6,4,7,2,1,10,3,9,8}, 3));
    assertSame(4, kth(new Integer[]{5,6,4,7,2,1,10,3,9,8}, 4));
    assertSame(5, kth(new Integer[]{5,6,4,7,2,1,10,3,9,8}, 5));
    assertSame(6, kth(new Integer[]{5,6,4,7,2,1,10,3,9,8}, 6));
    assertSame(7, kth(new Integer[]{5,6,4,7,2,1,10,3,9,8}, 7));
    assertSame(8, kth(new Integer[]{5,6,4,7,2,1,10,3,9,8}, 8));
    assertSame(9, kth(new Integer[]{5,6,4,7,2,1,10,3,9,8}, 9));
    assertSame(10, kth(new Integer[]{5,6,4,7,2,1,10,3,9,8}, 10));
    assertNull(kth(new Integer[]{5,6,4,7,2,1,10,3,9,8}, 11));
  }
}