/*
 * Copyright (c) 2018 Vikash Madhow
 */

package ma.vi.base.collections;

import ma.vi.base.tuple.T2;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import static ma.vi.base.lang.Errors.checkArgument;

/**
 * Utilities to work with Maps.
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class Maps {
  /**
   * Given a root map and a set of n objects Obj(1) to Obj(n), a multiLevelGet
   * will get the first object from the root map. This first object is expected
   * to be a map or null. If it is a map, this map is in turn used to
   * get Obj(2) and so on until Obj(n-1). The map for Obj(n-1) is then used
   * to get Obj(n) and whatever b is there is returned. If any level
   * returns null, the whole method returns null.
   *
   * @param map     Root map
   * @param objects The objects at each level in proper order.
   */
  public static <T> T multiLevelGet(Map map, Object... objects) {
    for (int i = 0; i < objects.length - 1; i++) {
      map = (Map) map.get(objects[i]);
      if (map == null) {
        return null;
      }
    }
    return (T) map.get(objects[objects.length - 1]);
  }

  /**
   * Given a root map and a set of n objects O(1) to O(n), a multiLevelPut
   * will first get O(1) from the root map which is expected to also be a
   * map. If this is null, a HashMap is created and associated to O(1) in the
   * root map. This process is repeated up to O(n-2), the map for which is
   * used to associate O(n-1) to O(n).
   *
   * @param map     The root map
   * @param objects The objects at each level in proper order.
   * @return The previous b associated with O(n-1) if any or null.
   * Null can also indicate that O(n-1) was previously associated
   * to the null b.
   */
  public static Object multiLevelPut(Map map, Object... objects) {
    for (int i = 0; i < objects.length - 2; i++) {
      Map levelMap = (Map) map.get(objects[i]);
      if (levelMap == null) {
        levelMap = new HashMap();
        map.put(objects[i], levelMap);
      }
      map = levelMap;
    }
    return map.put(objects[objects.length - 2], objects[objects.length - 1]);
  }

  /**
   * Create a hashmap from the array of pairs.
   */
  public static <A, B> Map<A, B> of(T2<A, B>... pairs) {
    return put(new HashMap<>(), pairs);
  }

  /**
   * Adds all the pairs to the map and return it.
   */
  public static <A, B> Map<A, B> put(Map<A, B> map, T2<A, B>... pairs) {
    for (T2<A, B> t2 : pairs) {
      map.put(t2.a, t2.b);
    }
    return map;
  }

  public static <A, B> Map<A, B> put(Map<A, B> map, Map<A, B> values) {
    for (Map.Entry<A, B> t2 : values.entrySet()) {
      map.put(t2.getKey(), t2.getValue());
    }
    return map;
  }

  /**
   * Returns a string representation of the map.
   */
  public static String toString(Map<?, ?> map) {
    if (map == null) {
      return "";
    } else {
      return map.entrySet().stream()
          .map(e -> String.valueOf(e.getKey()) + ": " + String.valueOf(e.getValue()))
          .collect(Collectors.joining(", ", "{", "}"));
    }
  }

  /**
   * Returns the inverse of a map, i.e, for every mapping a-&gt;b in the map, the
   * return map will contain the mapping b-&gt;a. Inverse can only exist for one-to-one
   * maps, i.e., if the original maps contains two mappings from different keys to the
   * same b (a-&gt;b, c-&gt;b), the inverse will only contain one such mapping
   * since the map cannot contain the same keys twice. Which mapping is present in the inverse
   * depends on the type of the source map and the natural order of its keys.
   */
  public static <A, B> Map<B, A> invert(Map<A, B> map) {
    if (map == null) {
      return null;
    }
    Map<B, A> inv = new HashMap<>();
    for (Map.Entry<A, B> entry : map.entrySet()) {
      A key = entry.getKey();
      B value = entry.getValue();
      checkArgument(!inv.containsKey(value),
                    "Map is not invertible as value " + value + " is mapped to " +
                    "both " + key + " and " + inv.get(value));
      inv.put(value, key);
    }
    return inv;
  }

  private Maps() {
  }
}