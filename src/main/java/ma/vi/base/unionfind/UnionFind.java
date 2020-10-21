package ma.vi.base.unionfind;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Union-find structure to hold and find information on connected components
 * in amortized constant time.
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class UnionFind<T> {

  /**
   * Returns the set of the connected components while also performing simple
   * single-pass path-compression.
   */
  public T find(T element) {
    if (!components.containsKey(element)) {
      /*
       * A new element is treated as part of its own component
       * and added to the components map.
       */
      components.put(element, element);
      setCount += 1;
      return element;

    } else if (components.get(element).equals(element)) {
      return element;

    } else {
      /*
       * path compression: a -> b -> c becomes a -> c
       */
      components.put(element, components.get(components.get(element)));
      return find(components.get(element));
    }
  }

  /**
   * Put the two elements in the same component.
   */
  public void union(T element1, T element2) {
    T component1 = find(element1);
    T component2 = find(element2);
    if (!component1.equals(component2)) {
      components.put(component1, component2);
      setCount -= 1;
    }
  }

  /**
   * Return the number of connected components in the structure.
   */
  public int components() {
    return setCount;
  }

  private int setCount;
  private final Map<T, T> components = new ConcurrentHashMap<>();
}