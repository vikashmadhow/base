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
   * Returns the connected component that the given element belongs to.
   * If this is the first time that this element is seen it is added into
   * a new component which contains only itself. This function also performs
   * single-pass path-compression every time it is called to reduce the depth
   * of the union-find tree.
   */
  public T find(T element) {
    if (!components.containsKey(element)) {
      /*
       * A new element is treated as part of its own component
       * and added to the components map.
       */
      components.put(element, element);
      elements.put(element, new HashSet<>());
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
   * Put the two elements in the same component returning
   * the root component of the two. When two components are
   * merged all the elements bound to them are also merged.
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
    }
    return component2;
  }

  /**
   * Adds the item to the root component of the given element.
   */
  public void add(T element, E item) {
    T root = find(element);
    elements.get(root).add(item);
  }

  /**
   * Returns all the elements previously added to the component.
   */
  public Set<E> elements(T component) {
    return elements.get(find(component));
  }

  /**
   * Return the set of all components added to this structure.
   */
  public Set<T> components() {
    return elements.keySet();
  }

  private final Map<T, T> components = new ConcurrentHashMap<>();

  private final Map<T, Set<E>> elements = new ConcurrentHashMap<>();
}