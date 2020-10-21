package ma.vi.base.collections;

import ma.vi.base.string.Strings;
import org.junit.jupiter.api.*;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Units tests for {@link DiskBasedCollection}.
 *
 * @author vikash.madhow@gmail.com
 */
public class DiskBasedCollectionTest {
  public DiskBasedCollectionTest() {
  }

  @BeforeAll
  public static void setUpClass() throws Exception {
  }

  @AfterAll
  public static void tearDownClass() throws Exception {
  }

  @BeforeEach
  public void setUp() {
    instance = new DiskBasedCollection<String>(Comparator.naturalOrder());
  }

  @AfterEach
  public void tearDown() {
    instance.close();
  }

  private DiskBasedCollection<String> instance;

  /**
   * Test of size method, of class DiskBasedCollection.
   */
  @Test
  public void testSize() {
    System.out.println("size");
    assertEquals(instance.size(), 0);

    instance.add("Test1");
    instance.add("Test2");

    assertEquals(instance.size(), 2);
  }

  /**
   * Test of add method, of class DiskBasedCollection.
   */
  @Test
  public void testBasicAdd() {
    assertArrayEquals(instance.toArray(new String[0]), new String[0]);
    String[] items = new String[]{"Test1", "Test2", "Test3"};
    for (String item: items) instance.add(item);
    assertArrayEquals(instance.toArray(new String[0]), items);
  }

  @Test
  public void testAdvancedAdd() {
    // generate and add 100 random strings
    String[] items = new String[1000];
    for (int i = 0; i < items.length; i++) {
      items[i] = Strings.random(20);
      instance.add(items[i]);
    }
    Arrays.sort(items);
    assertArrayEquals(instance.toArray(new String[0]), items);
  }

  /**
   * Test of contains method, of class DiskBasedCollection.
   */
  @Test
  public void testContains() {
    String[] items = new String[]{"Test1", "Test2", "Test3"};
    for (String item: items) instance.add(item);

    assertTrue(instance.contains("Test1"));
    assertFalse(instance.contains("Test4"));
  }

  /**
   * Test of iterator method, of class DiskBasedCollection.
   */
  @Test
  public void testIterator() {
    String[] items = new String[]{"Test3", "Test1", "Test2"};
    for (String item: items) instance.add(item);

    Iterator<String> i = instance.iterator();
    assertEquals(i.next(), "Test1");
    assertEquals(i.next(), "Test2");
    assertEquals(i.next(), "Test3");
  }

  /**
   * Test of remove method, of class DiskBasedCollection.
   */
//    @Test
//    public void testRemove()
//    {
//        String[] items = new String[] {"Test1", "Test2", "Test3"};
//        for (String item: items) instance.add(item);
//
//        instance.remove("Test2");
//        assertArrayEquals(instance.toArray(new String[0]), new String[] {"Test1", "Test3"});
//    }
//
//    /**
//     * Test of removeAll method, of class DiskBasedCollection.
//     */
//    @Test
//    public void testRemoveAll()
//    {
//        String[] items = new String[] {"Test1", "Test2", "Test3", "Test4"};
//        for (String item: items) instance.add(item);
//
//        instance.removeAll(Arrays.asList("Test1", "Test3"));
//        assertArrayEquals(instance.toArray(new String[0]), new String[] {"Test2", "Test4"});
//
//        instance.add("Test5");
//        assertArrayEquals(instance.toArray(new String[0]), new String[] {"Test2", "Test4", "Test5"});
//
//        instance.add("Test3");
//        assertArrayEquals(instance.toArray(new String[0]), new String[] {"Test2", "Test3", "Test4", "Test5"});
//    }

  /**
   * Test of clear method, of class DiskBasedCollection.
   */
  @Test
  public void testClear() {
    instance.clear();
  }

  /**
   * Test of close method, of class DiskBasedCollection.
   */
  @Test
  public void testClose() {
    instance.close();
  }
}