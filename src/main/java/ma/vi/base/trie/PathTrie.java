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
    return super.put(Arrays.asList(split(path)), value);
  }

  public V get(String path) {
    return super.get(Arrays.asList(split(path)));
  }

  public List<T2<String, V>> getPrefixed(String path) {
    return toPath(super.getPrefixed(Arrays.asList(split(path))));
  }

  public void delete(String path) {
    super.delete(Arrays.asList(split(path)));
  }

  public boolean deletePrefixed(String path) {
    return super.deletePrefixed(Arrays.asList(split(path)));
  }

  protected List<T2<String, V>> toPath(List<T2<List<String>, V>> from) {
    return from.stream()
               .map(t -> T2.of(t.a.stream()
                                  .map(Object::toString)
                                  .collect(joining(separator)),
                               t.b))
               .collect(toList());
  }

  private String[] split(String path) {
    String[] p = path.split(separator, -1);
    if (p.length > 1 && p[p.length - 1].length() == 0) {
      p = Arrays.copyOfRange(p, 0, p.length - 1);
    }
    return p;
  }

  private final String separator;
}
