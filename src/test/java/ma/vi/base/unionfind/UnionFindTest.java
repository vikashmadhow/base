package ma.vi.base.unionfind;

import org.junit.jupiter.api.Test;

import java.util.Set;

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
    assertEquals(Set.of(1, 2, 3), uf.components());
  }

  @Test
  void union() {
    UnionFind<Integer, String> uf = new UnionFind<>();
    assertEquals(1, uf.find(1));
    assertEquals(2, uf.find(2));
    assertEquals(2, uf.find(2));
    assertEquals(1, uf.find(1));
    assertEquals(3, uf.find(3));
    assertEquals(Set.of(1, 2, 3), uf.components());

    uf.add(1, "One");
    uf.add(1, "Uno");
    uf.add(2, "Two");
    uf.add(2, "Deuce");
    uf.add(3, "Three");
    uf.add(3, "Trio");

    assertEquals(Set.of("Uno", "One"), uf.elements(1));
    assertEquals(Set.of("Two", "Deuce"), uf.elements(2));
    assertEquals(Set.of("Trio", "Three"), uf.elements(3));

    Integer root = uf.union(1, 2);
    assertEquals(Set.of(root, 3), uf.components());
    assertEquals(Set.of("Uno", "Two", "Deuce", "One"), uf.elements(root));
    assertEquals(Set.of("Trio", "Three"), uf.elements(3));

    assertEquals(uf.find(1), uf.find(1));
    assertNotEquals(uf.find(1), uf.find(3));
    assertNotEquals(uf.find(2), uf.find(3));

    root = uf.union(2, 3);
    assertEquals(Set.of(root), uf.components());
    assertEquals(Set.of("Uno", "Two", "Trio", "Three", "Deuce", "One"), uf.elements(root));

    assertSame(uf.find(1), uf.find(3));
    assertSame(uf.find(2), uf.find(3));
  }
}