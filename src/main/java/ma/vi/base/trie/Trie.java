package ma.vi.base.trie;

import ma.vi.base.tuple.T2;

import java.util.*;

import static java.util.Collections.emptyList;

/**
 * A trie keeps sequences of a unit type U mapped to a value
 * type V. A trie of unit type Character, for instance, would
 * store sequences of characters (i.e. strings) mapped to
 * objects of some value type.
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class Trie<U, V> {

  public V put(Iterable<U> sequence, V value) {
    Node<U, V> node = root;
    for (U u: sequence) {
      if (node.children.containsKey(u)) {
        node = node.children.get(u);
      } else {
        Node<U, V> child = new Node<>();
        node.children.put(u, child);
        node = child;
      }
    }
    V previous = node.value;
    node.value = value;
    return previous;
  }

  public V get(Iterable<U> sequence) {
    Node<U, V> node = find(sequence.iterator());
    return node == null ? null : node.value;
  }

  public List<T2<List<U>, V>> getPrefixed(Iterable<U> sequence) {
    Node<U, V> node = root;
    List<U> prefix = new ArrayList<>();
    for (U u: sequence) {
      prefix.add(u);
      if (node.children.containsKey(u)) {
        node = node.children.get(u);
      } else {
        return emptyList();
      }
    }
    return getPrefixed(prefix, node);
  }

  public void delete(Iterable<U> sequence) {
    delete(sequence.iterator(), root);
  }

  private boolean delete(Iterator<U> sequence, Node<U, V> node) {
    if (sequence.hasNext()) {
      U u = sequence.next();
      if (node.children.containsKey(u)) {
        boolean deletedAll = delete(sequence, node.children.get(u));
        if (deletedAll) {
          node.children.remove(u);
        }
      }
    } else {
      node.value = null;
    }
    return node.children.isEmpty();
  }

  public boolean deletePrefixed(Iterable<U> sequence) {
    Node<U, V> node = find(sequence.iterator());
    if (node != null) {
      node.children.clear();
    }
    return false;
  }

  protected Node<U, V> find(Iterator<U> sequence) {
    Node<U, V> node = root;
    while (sequence.hasNext()) {
      U u = sequence.next();
      if (node.children.containsKey(u)) {
        node = node.children.get(u);
      } else {
        return null;
      }
    }
    return node;
  }

  private List<T2<List<U>, V>> getPrefixed(List<U> prefix, Node<U, V> startFrom) {
    List<T2<List<U>, V>> values = new ArrayList<>();
    List<U> list = new ArrayList<>(prefix);
    if (startFrom.value != null) {
      values.add(T2.of(list, startFrom.value));
    }
    for (Map.Entry<U, Node<U, V>> child: startFrom.children.entrySet()) {
      List<U> newPrefix = new ArrayList<>(prefix);
      newPrefix.add(child.getKey());
      values.addAll(getPrefixed(newPrefix, child.getValue()));
    }
    return values;
  }

  protected static class Node<U, V> {
    public final Map<U, Node<U, V>> children = new HashMap<>();
    V value;
  }

  private final Node<U, V> root = new Node<>();
}
