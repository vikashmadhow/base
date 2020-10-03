/*
 * Copyright (c) 2018 Vikash Madhow
 */

package ma.vi.base.reflect;

import ma.vi.base.lang.NotFoundException;
import ma.vi.base.tuple.T2;

import java.util.Optional;

/**
 * Reflectively read and write an object state.
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class State {
  public static Object get(Object object, String path) {
    T2<Object, Property> p = property(object, path);
    return p == null ? null : p.b.get(p.a);
  }

  public static void set(Object object, String path, Object value) {
    T2<Object, Property> p = property(object, path);
    if (p == null) {
      throw new NullPointerException("Could not set the value of the property referenced by path " + path +
          " in " + object + " to " + value + " as some intermediate part" +
          " of the path is null");
    } else {
      p.b.set(p.a, value);
    }
  }

  private static T2<Object, Property> property(Object object, String path) {
    int pos = path.indexOf('.');
    String member = pos == -1 ? path : path.substring(0, pos);
    String rest = pos == -1 ? null : path.substring(pos + 1);

    Class<?> cls = object.getClass();
    Optional<Property> p = Dissector.property(cls, member);
    if (p.isPresent()) {
      Property property = p.get();
      if (rest == null) {
        return T2.of(object, property);
      } else {
        Object value = property.get(object);
        if (value == null) {
          return null;
        } else {
          return property(value, rest);
        }
      }
    }
    throw new NotFoundException("Property " + member + " could not be found in " + cls);
  }
}