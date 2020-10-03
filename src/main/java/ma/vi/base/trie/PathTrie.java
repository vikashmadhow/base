package ma.vi.base.trie;

import ma.vi.base.tuple.T2;

import java.util.Arrays;
import java.util.List;

import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;

/**
 * Trie specialised for paths, which are a sequence of strings.
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class PathTrie<V> extends Trie<String, V> {
  public PathTrie() {
    this("/");
  }

  public PathTrie(String separator) {
    this.separator = separator;
  }

  public V put(String path, V value) {
    return super.put(Arrays.asList(path.split(separator)), value);
  }

  public V get(String path) {
    return super.get(Arrays.asList(path.split(separator)));
  }

  public List<T2<String, V>> getPrefixed(String path) {
    return toPath(super.getPrefixed(Arrays.asList(path.split(separator))));
  }

  public void delete(String path) {
    super.delete(Arrays.asList(path.split(separator)));
  }

  public boolean deletePrefixed(String path) {
    return super.deletePrefixed(Arrays.asList(path.split(separator)));
  }

  protected List<T2<String, V>> toPath(List<T2<List<String>, V>> from) {
    return from.stream()
               .map(t -> T2.of(t.a.stream()
                                  .map(Object::toString)
                                  .collect(joining(separator)),
                               t.b))
               .collect(toList());
  }

  private final String separator;
}
