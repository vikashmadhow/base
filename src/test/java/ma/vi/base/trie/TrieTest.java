package ma.vi.base.trie;

import ma.vi.base.collections.CharIterable;
import ma.vi.base.tuple.T2;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Collections.emptySet;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
class TrieTest {
  @Test
  void baseTrie() {
    Trie<Character, Integer> trie = new Trie<>();
    String s1 = "This is a test";
    String s2 = "This is not a test";
    trie.put(new CharIterable(s1), 50);
    trie.put(new CharIterable(s2), 40);

    assertEquals(50, trie.get(new CharIterable(s1)));
    assertEquals(40, trie.get(new CharIterable(s2)));

    List<Character> k1 = s1.chars().boxed()
                           .map(c -> (char)c.intValue())
                           .collect(Collectors.toList());

    List<Character> k2 = s2.chars().boxed()
                           .map(c -> (char)c.intValue())
                           .collect(Collectors.toList());

    assertEquals(new HashSet<>(Arrays.asList(T2.of(k1, 50),
                                             T2.of(k2, 40))),
                 new HashSet<>(trie.getPrefixed(new CharIterable("This is"))));

    assertEquals(new HashSet<>(Collections.singletonList(T2.of(k1, 50))),
                 new HashSet<>(trie.getPrefixed(new CharIterable("This is a"))));

    assertEquals(new HashSet<>(Collections.singletonList(T2.of(k2, 40))),
                 new HashSet<>(trie.getPrefixed(new CharIterable("This is not"))));

    assertEquals(emptySet(),
                 new HashSet<>(trie.getPrefixed(new CharIterable("This is x"))));
  }

  @Test
  void stringTrie() {
    StringTrie<Integer> trie = new StringTrie<>();
    String s1 = "This is a test";
    String s2 = "This is not a test";
    trie.put(s1, 50);
    trie.put(s2, 40);

    assertEquals(50, trie.get(s1));
    assertEquals(40, trie.get(s2));

    assertEquals(new HashSet<>(Arrays.asList(T2.of(s1, 50),
                                             T2.of(s2, 40))),
                 new HashSet<>(trie.getPrefixed("This is")));

    assertEquals(new HashSet<>(Collections.singletonList(T2.of(s1, 50))),
                 new HashSet<>(trie.getPrefixed("This is a")));

    assertEquals(new HashSet<>(Collections.singletonList(T2.of(s2, 40))),
                 new HashSet<>(trie.getPrefixed("This is not")));

    assertEquals(emptySet(),
                 new HashSet<>(trie.getPrefixed("This is x")));

  }

  @Test
  void pathTrie() {
    PathTrie<Integer> trie = new PathTrie<>();
    String p1 = "/x/y/z/b";
    String p2 = "/x/y/p/a";
    String p3 = "x/y/p/a";
    trie.put(p1, 50);
    trie.put(p2, 40);
    trie.put(p3, 30);

    assertEquals(50, trie.get(p1));
    assertEquals(40, trie.get(p2));

    assertEquals(new HashSet<>(Arrays.asList(T2.of(p1, 50),
                                             T2.of(p2, 40))),
                 new HashSet<>(trie.getPrefixed("/x/y")));

    assertEquals(new HashSet<>(Arrays.asList(T2.of(p1, 50),
                                             T2.of(p2, 40))),
                 new HashSet<>(trie.getPrefixed("/")));

    assertEquals(new HashSet<>(Collections.singletonList(T2.of(p1, 50))),
                 new HashSet<>(trie.getPrefixed("/x/y/z")));

    assertEquals(new HashSet<>(Collections.singletonList(T2.of(p2, 40))),
                 new HashSet<>(trie.getPrefixed("/x/y/p")));

    assertEquals(emptySet(),
                 new HashSet<>(trie.getPrefixed("/a")));

    assertEquals(new HashSet<>(Arrays.asList(T2.of(p1, 50),
                                             T2.of(p2, 40))),
                 new HashSet<>(trie.getPrefixed("")));

    assertEquals(new HashSet<>(Arrays.asList(T2.of(p1, 50),
                                             T2.of(p2, 40))),
                 new HashSet<>(trie.getPrefixed("/x")));

    assertEquals(new HashSet<>(Arrays.asList(T2.of(p1, 50),
                                             T2.of(p2, 40))),
                 new HashSet<>(trie.getPrefixed("/x/")));

    assertEquals(new HashSet<>(Arrays.asList(T2.of(p1, 50),
                                             T2.of(p2, 40))),
                 new HashSet<>(trie.getPrefixed("/x/y")));

    assertEquals(new HashSet<>(Arrays.asList(T2.of(p1, 50),
                                             T2.of(p2, 40))),
                 new HashSet<>(trie.getPrefixed("/x/y/")));

    assertEquals(new HashSet<>(Collections.singletonList(T2.of(p3, 30))), new HashSet<>(trie.getPrefixed("x/y/")));

    assertEquals(30, trie.get(p3));
  }
}