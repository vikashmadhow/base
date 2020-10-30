package ma.vi.base.unionfind;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
class UnionFindTest {

  @Test
  void find() {
    UnionFind<Integer, ?> uf = new UnionFind<>();
    assertEquals(1, uf.find(1));
    assertEquals(2, uf.find(2));
    assertEquals(2, uf.find(2));
    assertEquals(1, uf.find(1));
    assertEquals(3, uf.find(3));
  }

  @Test
  void union() {
    UnionFind<Integer, String> uf = new UnionFind<>();
    assertEquals(1, uf.find(1));
    assertEquals(2, uf.find(2));
    assertEquals(2, uf.find(2));
    assertEquals(1, uf.find(1));
    assertEquals(3, uf.find(3));
    assertEquals(3, uf.components());
    uf.add(1, "One");
    uf.add(1, "Uno");
    uf.add(2, "Two");
    uf.add(2, "Deuce");

    uf.union(1, 2);
    assertTrue(uf.find(1) == 1 || uf.find(1) == 2);
    assertTrue(uf.find(2) == 1 || uf.find(2) == 2);
    assertSame(uf.find(1), uf.find(2));
    assertNotEquals(uf.find(1), uf.find(3));
    assertNotEquals(uf.find(2), uf.find(3));

    uf.union(2, 3);
    assertSame(uf.find(1), uf.find(3));
    assertSame(uf.find(2), uf.find(3));
  }
}