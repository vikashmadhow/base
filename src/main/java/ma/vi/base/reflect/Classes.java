/*
 * Copyright (c) 2018 Vikash Madhow
 */

package ma.vi.base.reflect;

import ma.vi.base.collections.Maps;
import ma.vi.base.lang.NotFoundException;
import ma.vi.base.tuple.T2;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import static ma.vi.base.lang.Errors.unchecked;
import static org.apache.commons.lang3.StringUtils.countMatches;

/**
 * Utility functions for working reflectively with classes.
 *
 * @author vikash.madhow@gmail.com
 */
public class Classes {
  /**
   * Returns true if the supplied class is a sub-class of the super-type whose fully-qualified
   * name has been supplied. This method uses reflection to test against super-types which might
   * not be available or convenient to load.
   */
  public static boolean isSubclassOf(Class<?> cls, String superType) {
    T2<Class<?>, String> pair = new T2<>(cls, superType);
    if (subclassCache.containsKey(pair)) {
      return subclassCache.get(pair);
    } else {
      boolean subclass = false;
      for (Class<?> component: Dissector.componentClasses(cls)) {
        if (component.getName().equals(superType)) {
          subclass = true;
          break;
        }
      }
      subclassCache.put(pair, subclass);
      return subclass;
    }
  }

  /**
   * Utility for creating a new instance of the class using a constructor matching the types
   * of the supplied parameters, wrapping any exceptions as RuntimeExceptions. For primitive
   * types, an instance of the corresponding wrapper type is created instead.
   * <p>
   * The type of the parameters of the constructor to used are determined from the parameters
   * supplied. This requires that all parameters are non-null; null parameters are assumed to
   * be of object type otherwise.
   */
  public static Object instanceOf(Class<?> cls, Object... parameters) {
    List<T2<Object, Class<?>>> paramsWithType = new ArrayList<>();
    for (Object parameter: parameters) {
      paramsWithType.add(T2.of(parameter, parameter == null ? Object.class : parameter.getClass()));
    }
    return instanceOfWithTypes(cls, paramsWithType.toArray(new T2[0]));
  }

  /**
   * Utility for creating a new instance of the class using a constructor matching the types
   * of the supplied parameters, wrapping any exceptions as RuntimeExceptions. For primitive
   * types, an instance of the corresponding wrapper type is created instead.
   */
  public static Object instanceOfWithTypes(Class<?> cls, T2<Object, Class<?>>... parameters) {
    if (cls.isPrimitive()) {
      cls = wrapperClassOf(cls);
    }
    if (Character.class.equals(cls) && parameters.length > 0 &&
        parameters[0].a instanceof String && ((String)parameters[0].a).length() > 0) {
      // For creation of characters, use only first character of string if
      // a string is provided as the construction parameter.
      parameters[0] = T2.of(((String)parameters[0].a).charAt(0), Character.class);
    }

    // get parameter types
    Object[] params = new Object[parameters.length];
    Class<?>[] types = new Class<?>[parameters.length];
    for (int i = 0; i < parameters.length; i++) {
      params[i] = parameters[i].a;
      types[i] = parameters[i].b;
    }
    try {
      if (cls.isEnum()) {
        Method valueOf = cls.getDeclaredMethod("valueOf", types);
        return valueOf.invoke(null, params);
      } else {
        // search for matching constructor and build if found
        Constructor ctor = cls.getDeclaredConstructor(types);
        if (!Modifier.isAbstract(cls.getModifiers())) {
          ctor.setAccessible(true);
          return ctor.newInstance(params);
        } else {
          throw new IllegalArgumentException(cls + " does not have a constructor taking the following " +
                                                 "parameters " + Arrays.toString(types) + "  or constructor is abstract.");
        }
      }
    } catch (Exception e) {
      throw unchecked(e);
    }
  }

  /**
   * Creates a new instance of the class or returns null if construction failed.
   */
  public static <T> T instanceOfOrNull(Class<T> cls, Object... params) {
    try {
      return (T)instanceOf(cls, params);
    } catch (Exception e) {
      return null;
    }
  }

  /**
   * Utility method to gets the class of a value, casting it
   * to the proper type.
   */
  public static <T> Class<T> classOf(T value) {
    return (Class<T>)value.getClass();
  }

  /**
   * Loads the named class.
   */
  public static Class<?> classOf(String name) throws NotFoundException {
    return classOf(name, null);
  }

  /**
   * Loads the class with the specified name; if such a class cannot be
   * found, prepends the default package to the className and tries again.
   * If it still could not be found, throws {@link NotFoundException}.
   */
  public static Class<?> classOf(String type, String defaultPackage) {
    return loadedClasses.computeIfAbsent(T2.of(type, defaultPackage), fullType -> {
      switch (type) {
        case "int":       return int.class;
        case "short":     return short.class;
        case "long":      return long.class;
        case "byte":      return byte.class;
        case "char":      return char.class;
        case "float":     return float.class;
        case "double":    return double.class;
        case "boolean":   return boolean.class;
        case "void":      return void.class;
        default:
          int pos = type.indexOf('[');
          if (pos != -1) {
            // For array types, create the array with the right component
            // type and number of dimensions and return its class.
            int dimensions = countMatches(type, '[');
            Class<?> componentType = classOf(type.substring(0, pos));
            return Array.newInstance(componentType, new int[dimensions]).getClass();

          } else try {
            return Class.forName(type);

          } catch (ClassNotFoundException e) {
            if (defaultPackage != null) try {
              return Class.forName(defaultPackage + "." + type);

            } catch (ClassNotFoundException ne) {
              throw new NotFoundException("Could not load class named " + type + " or " +
                                              defaultPackage + "." + type, ne);
            }
            else {
              throw new NotFoundException("Could not load " + type, e);
            }
          }
      }
    });
  }

  /**
   * Returns the type of the object as a string. If the object is null,
   * return "NullType". If the object is an array, returns type[x][][]...
   * where type is the component type of the array, x is the length of
   * the first dimension of the array with the number of '[]' (including
   * the first one) corresponding to the number of dimensions of the array.
   * Only the first dimension has its length specified as the other the
   * lengths of subsequent dimensions will be variable. Otherwise return
   * the name of the class of the object as returned by {@link Class#getName()}.
   */
  public static String typeOf(Object object) {
    if (object == null) {
      return "NullType";
    } else {
      Class<?> cls = object.getClass();
      if (cls.isArray()) {
        Class<?> componentType = cls.getComponentType();
        int length = Array.getLength(object);
        StringBuilder type = new StringBuilder("[").append(length).append("]");
        while (componentType != null) {
          Class<?> next = componentType.getComponentType();
          if (next != null) {
            type.append("[]");
          } else {
            type.insert(0, componentType.getName());
          }
          componentType = next;
        }
        return type.toString();
      } else {
        return cls.getName();
      }
    }
  }

  /**
   * Gets the smallest component type of an array. The smallest component type
   * of an array is the same irrespective of the number of dimensions it has; for
   * instance both int[] and int[][][] has the same int component type.
   * <p>
   * Throws IllegalArgumentException if cls is not an array.
   */
  public static Class<?> componentType(Class<?> cls) {
    if (!cls.isArray()) {
      throw new IllegalArgumentException(cls + " is not an array");
    } else {
      Class<?> componentType = null;
      while ((cls = cls.getComponentType()) != null) {
        componentType = cls;
      }
      return componentType;
    }
  }

  /**
   * Returns true if specified class is a wrapper for a primitive type.
   */
  public static boolean isWrapperType(Class<?> cls) {
    return wrapperToPrimitive.containsKey(cls);
  }

  /**
   * Returns the wrapper type for the primitive type passed. Returns the passed class itself
   * if that class is not primitive.
   */
  public static Class<?> wrapperClassOf(Class<?> cls) {
    return cls.isPrimitive() ? primitiveToWrapper.get(cls) : cls;
  }

  /**
   * Returns the primitive type for the wrapper type passed. Returns the passed class itself
   * if that class is not a wrapper type.
   */
  public static Class<?> primitiveClassOf(Class<?> cls) {
    Class<?> primitive = wrapperToPrimitive.get(cls);
    return primitive == null ? cls : primitive;
  }

  private Classes() {
  }

  /**
   * Classes loaded through {@link #classOf(String, String)}.
   */
  private final static ConcurrentMap<T2<String, String>, Class<?>> loadedClasses = new ConcurrentHashMap<>();

  /**
   * Cache for subclass relationships.
   */
  private static final Map<T2<Class<?>, String>, Boolean> subclassCache = new WeakHashMap<>();

  /**
   * A map from wrapper types to their equivalent primitive.
   */
  private static final Map<Class<?>, Class<?>> wrapperToPrimitive = new HashMap<>(9);

  /**
   * A map from primitive types to their equivalent wrappers.
   */
  private static final Map<Class<?>, Class<?>> primitiveToWrapper;

  // initialize maps
  static {
    wrapperToPrimitive.put(Boolean.class,   boolean.class);
    wrapperToPrimitive.put(Byte.class,      byte.class);
    wrapperToPrimitive.put(Short.class,     short.class);
    wrapperToPrimitive.put(Integer.class,   int.class);
    wrapperToPrimitive.put(Long.class,      long.class);
    wrapperToPrimitive.put(Float.class,     float.class);
    wrapperToPrimitive.put(Double.class,    double.class);
    wrapperToPrimitive.put(Character.class, char.class);
    wrapperToPrimitive.put(Void.class,      void.class);

    primitiveToWrapper = Maps.invert(wrapperToPrimitive);
  }
}