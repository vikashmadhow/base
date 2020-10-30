package ma.vi.base.unionfind;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Union-find structure to hold and find information on connected components
 * in amortized constant time.
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class UnionFind<T, E> {

  /**
   * Returns the set of the connected components while also performing simple
   * single-pass path-compression.
   */
  public T find(T component) {
    if (!components.containsKey(component)) {
      /*
       * A new element is treated as part of its own component
       * and added to the components map.
       */
      components.put(component, component);
      elements.put(component, new HashSet<>());
      setCount += 1;
      return component;

    } else if (components.get(component).equals(component)) {
      return component;

    } else {
      /*
       * path compression: a -> b -> c becomes a -> c
       */
      components.put(component, components.get(components.get(component)));
      return find(components.get(component));
    }
  }

  /**
   * Put the two elements in the same component returning
   * the root component of the two.
   */
  public T union(T element1, T element2) {
    T component1 = find(element1);
    T component2 = find(element2);
    if (!component1.equals(component2)) {
      components.put(component1, component2);

      /*
       * Move all elements bound to component1 to component2
       * as the latter is the new root.
       */
      elements.get(component2).addAll(elements.get(component1));
      elements.remove(component1);

      setCount -= 1;
    }
    return component2;
  }

  public void add(T component, E element) {
    T root = find(component);
    elements.get(root).add(element);
  }

  public Set<E> elements(T component) {
    return elements.get(find(component));
  }

  public Set<T> components() {
    return elements.keySet();
  }

  /**
   * Return the number of connected components in the structure.
   */
//  public int components() {
//    return setCount;
//  }

  private int setCount;
  private final Map<T, T> components = new ConcurrentHashMap<>();
  private final Map<T, Set<E>> elements = new ConcurrentHashMap<>();
}