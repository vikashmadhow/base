package ma.vi.base.trie;

import ma.vi.base.collections.CharIterable;
import ma.vi.base.tuple.T2;

import java.util.List;

import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;

/**
 * Trie specialised for strings.
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class StringTrie<V> extends Trie<Character, V> {
  public V put(String sequence, V value) {
    return super.put(new CharIterable(sequence), value);
  }

  public V get(String sequence) {
    return super.get(new CharIterable(sequence));
  }

  public List<T2<String, V>> getPrefixed(String sequence) {
    return charListToString(super.getPrefixed(new CharIterable(sequence)));
  }

  public void delete(String sequence) {
    super.delete(new CharIterable(sequence));
  }

  public boolean deletePrefixed(String sequence) {
    return super.deletePrefixed(new CharIterable(sequence));
  }

  protected List<T2<String, V>> charListToString(List<T2<List<Character>, V>> from) {
    return from.stream()
               .map(t -> T2.of(t.a.stream()
                                  .map(Object::toString)
                                  .collect(joining()),
                               t.b))
               .collect(toList());
  }
}
