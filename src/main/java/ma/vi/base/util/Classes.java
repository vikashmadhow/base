/*
 * Copyright (c) 2018 Vikash Madhow
 */

package ma.vi.base.util;

import ma.vi.base.collections.Maps;
import ma.vi.base.lang.NotFoundException;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Utility functions for working with classes.
 *
 * @author vikash.madhow@gmail.com
 */
public class Classes {

  /**
   * Loads the class with the specified name; if such a class cannot be
   * found, prepends the default package to the className and tries again.
   * If still could not be found, throws {@link NotFoundException}.
   */
  public static Class<?> loadClass(String className, String defaultPackage) throws NotFoundException {
    // load class
    try {
      return Class.forName(className);
    } catch (ClassNotFoundException e) {
      if (defaultPackage != null) {
        try {
          return Class.forName(defaultPackage + "." + className);
        } catch (ClassNotFoundException ne) {
          throw new NotFoundException("Could not load either " + className + " or " +
              defaultPackage + "." + className, ne);
        }
      } else {
        throw new NotFoundException("Could not load " + className, e);
      }
    }
  }

  /**
   * Loads the class with the specified name.
   */
  public static Class<?> loadClass(String className) throws NotFoundException {
    return loadClass(className, null);
  }

  /**
   * Returns true if the class is of a literal type, i.e., it can be edited
   * and viewed as a string of characters. Primitive types and their wrappers,
   * enums, subclasses of {@link CharSequence}, {@link Number} &amp; {@link Date}
   * are literals.
   */
  public static boolean isLiteral(Object type) {
    if (type != null) {
      Class<?> cls = type instanceof Class ? (Class<?>) type : type.getClass();
      return cls.isPrimitive()
          || cls.isEnum()
          || CharSequence.class.isAssignableFrom(cls)
          || Number.class.isAssignableFrom(cls)
          || Boolean.class.isAssignableFrom(cls)
          || Character.class.isAssignableFrom(cls)
          || Date.class.isAssignableFrom(cls);
    }
    return false;
  }

  /**
   * Returns true if specified class is a wrapper for a primitive type.
   */
  public static boolean isWrapperType(Class<?> cls) {
    return wrapperToPrimitive.containsKey(cls);
  }

  /**
   * Returns the wrapper type for the primitive type passed. Throws IllegalArgumentException
   * if the passed type is not primitive.
   */
  public static Class<?> getWrapperType(Class<?> cls) throws IllegalArgumentException {
    if (!cls.isPrimitive()) {
      throw new IllegalArgumentException("The passed class is not  a primitive type.");
    }
    return getWrapperTypeOrSelf(cls);
  }

  /**
   * Returns the wrapper type for the primitive type passed. Returns the passed class itself
   * if that class is not primitive.
   */
  public static Class<?> getWrapperTypeOrSelf(Class<?> cls) {
    return cls.isPrimitive() ? primitiveToWrapper.get(cls) : cls;
  }

  /**
   * Returns the primitive type for the wrapper type passed. Throws IllegalArgumentException
   * if the passed type is not a wrapper type.
   */
  public static Class<?> getPrimitiveType(Class<?> cls) throws IllegalArgumentException {
    Class<?> primitive = getPrimitiveTypeOrSelf(cls);
    if (primitive == cls) {
      throw new IllegalArgumentException("The passed class is not a wrapper type.");
    }
    return primitive;
  }

  /**
   * Returns the primitive type for the wrapper type passed. Returns the passed class itself
   * if that class is not a wrapper type.
   */
  public static Class<?> getPrimitiveTypeOrSelf(Class<?> cls) {
    Class<?> primitive = wrapperToPrimitive.get(cls);
    return primitive == null ? cls : primitive;
  }

  private Classes() {
  }

  /**
   * A map from wrapper types to their equivalent primitive.
   */
  private static final Map<Class<?>, Class<?>> wrapperToPrimitive = new HashMap<Class<?>, Class<?>>();

  /**
   * A map from primitive types to their equivalent wrappers.
   */
  private static final Map<Class<?>, Class<?>> primitiveToWrapper;

  // initialize maps
  static {
    wrapperToPrimitive.put(Boolean.class, boolean.class);
    wrapperToPrimitive.put(Byte.class, byte.class);
    wrapperToPrimitive.put(Short.class, short.class);
    wrapperToPrimitive.put(Integer.class, int.class);
    wrapperToPrimitive.put(Long.class, long.class);
    wrapperToPrimitive.put(Float.class, float.class);
    wrapperToPrimitive.put(Double.class, double.class);
    wrapperToPrimitive.put(Character.class, char.class);
    wrapperToPrimitive.put(Void.class, void.class);

    primitiveToWrapper = Maps.invert(wrapperToPrimitive);
  }
}