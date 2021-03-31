/*
 * Copyright (c) 2018 Vikash Madhow
 */

package ma.vi.base.reflect;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Stream;

import static java.lang.reflect.Modifier.isProtected;
import static java.lang.reflect.Modifier.isPublic;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;
import static ma.vi.base.lang.Errors.checkArgument;
import static ma.vi.base.string.Strings.capFirst;

/**
 * Utility functions to access the constructors, methods and fields
 * of classes, reflectively.
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class Dissector {
  /**
   * Return all constructors of the class, including private and protected ones
   * declared in the class.
   */
  public static Set<Constructor<?>> constructors(Class<?> cls) {
    checkArgument(cls != null, "Class must not be null");
    return ctorsCache.computeIfAbsent(cls, k -> {
      // add all public constructors
      Set<Constructor<?>> ctors = new HashSet<>();
      Collections.addAll(ctors, cls.getConstructors());

      // add non-public declared constructors
      Stream.of(cls.getDeclaredConstructors())
            .filter(c -> !isPublic(c.getModifiers()))
            .forEach(ctors::add);
      return ctors;
    });
  }

  /**
   * Returns the constructor of the class with the specified parameters or
   * {@link Optional#empty()}, otherwise.
   */
  public static <T> Optional<Constructor<T>> constructor(Class<T> cls, Class<?>... parameterTypes) {
    for (Constructor<?> ctor : constructors(cls)) {
      if (Arrays.equals(ctor.getParameterTypes(), parameterTypes)) {
        return Optional.of((Constructor<T>) ctor);
      }
    }
    return Optional.empty();
  }

  /**
   * Returns all the methods in the class including private and protected ones
   * declared in the class or in its superclasses. For protected methods only
   * the most specialised ones are returned to respect (to some degree) Java's
   * rules of method overriding.
   */
  public static Map<MethodDescriptor, Method> methods(Class<?> cls) {
    checkArgument(cls != null, "Class must not be null");
    return methodsCache.computeIfAbsent(cls, k -> {
      // add all public methods
      Map<MethodDescriptor, Method> methods = new HashMap<>();
      Arrays.stream(cls.getMethods()).forEach(m -> methods.put(methodDescriptor(m), m));

      // add non-public declared methods
      for (Class<?> cur = cls; cur != null; cur = cur.getSuperclass()) {
        for (Method m : cur.getDeclaredMethods()) {
          int mod = m.getModifiers();
          if (isProtected(mod)) {
            methods.putIfAbsent(methodDescriptor(m), m);
          } else if (!isPublic(mod)) {
            methods.put(methodDescriptor(m), m);
          }
        }
      }
      return methods;
    });
  }

  /**
   * Returns the methods of the class with the specified name.
   */
  public static List<Method> methods(Class<?> cls, String name) {
    return methods(cls).values().stream().filter(m -> m.getName().equals(name)).collect(toList());
  }

  /**
   * Returns the method of the class with the specified name and parameters or
   * {@link Optional#empty()} if such a method can't be found.
   */
  public static Optional<Method> method(Class<?> cls, String name, Class<?>... parameterTypes) {
    Method m = methods(cls).get(new MethodDescriptor(name, parameterTypes));
    return m == null ? Optional.empty() : Optional.of(m);
  }

  /**
   * Returns all the fields of the class.
   */
  public static Map<String, Field> fields(Class<?> cls) {
    checkArgument(cls != null, "Class must not be null");
    return fieldsCache.computeIfAbsent(cls, k -> {
      Map<String, Field> fields = new HashMap<>();
      Arrays.stream(cls.getFields()).forEach(f -> fields.put(f.getName(), f));

      for (Class<?> cur : componentClasses(cls)) {
        for (Field f : cur.getDeclaredFields()) {
          String fieldName = f.getName();
          if (!fields.containsKey(fieldName) && !isPublic(f.getModifiers())) {
            fields.put(fieldName, f);
          }
        }
      }
      return fields;
    });
  }

  /**
   * Returns the field of the class with the specified name or
   * {@link Optional#empty()} if such a field can't be found.
   */
  public static Optional<Field> field(Class<?> cls, String name) {
    Field f = fields(cls).getOrDefault(name, null);
    return f == null ? Optional.empty() : Optional.of(f);
  }

  /**
   * Returns all properties in the class.
   */
  public static Map<String, Property> properties(Class<?> cls) {
    checkArgument(cls != null, "Class must not be null");
    return propertiesCache.computeIfAbsent(cls, k -> {
      Map<String, Property> properties = new HashMap<>();

      // Add properties based on getters and setters
      Map<MethodDescriptor, Method> methods = methods(cls);
      for (Map.Entry<MethodDescriptor, Method> entry : methods.entrySet()) {
        MethodDescriptor desc = entry.getKey();

        Method method = entry.getValue();
        String methodName = method.getName();
        String propertyName = Property.propertyNameFromMethod(methodName);
        if (!properties.containsKey(propertyName)) {
          int mod = method.getModifiers();
          if (!Modifier.isStatic(mod)
              && !Modifier.isAbstract(mod)
              && Modifier.isPublic(mod)
              && (methodName.startsWith("is")
              || methodName.startsWith("get")
              || methodName.startsWith("set"))) {

            if (desc.parameterTypes.length == 0
                && (methodName.startsWith("is")
                || methodName.startsWith("get"))) {

              Class<?> type = method.getReturnType();
              Method setter = methods.get(new MethodDescriptor("set" + capFirst(propertyName), type));
              if (setter != null && !setter.getReturnType().equals(void.class)) {
                setter = null;
              }
              properties.put(propertyName, new Property(method, setter));

            } else if (desc.parameterTypes.length == 1
                && desc.name.startsWith("set")) {

              Class<?> type = desc.parameterTypes[0];
              Method getter = methods.get(new MethodDescriptor("get" + capFirst(propertyName)));
              if (getter == null) {
                getter = methods.get(new MethodDescriptor("is" + capFirst(propertyName)));
              }
              if (getter != null && !getter.getReturnType().equals(type)) {
                getter = null;
              }
              properties.put(propertyName, new Property(getter, method));
            }
          }
        }
      }

      // Add properties based on fields
      Map<String, Field> fields = fields(cls);
      for (Map.Entry<String, Field> entry : fields.entrySet()) {
        String propertyName = entry.getKey();
        if (!properties.containsKey(propertyName)) {
          Field field = entry.getValue();
          int mod = field.getModifiers();
          if (!Modifier.isStatic(mod) && !Modifier.isFinal(mod)) {
            properties.put(propertyName, new Property(field));
          }
        }
      }
      return properties;
    });
  }

  /**
   * Returns the property of the class with the specified name or
   * {@link Optional#empty()} if such a property can't be found.
   */
  public static Optional<Property> property(Class<?> cls, String name) {
    Property p = properties(cls).getOrDefault(name, null);
    return p == null ? Optional.empty() : Optional.of(p);
  }

  /**
   * Return any or all classes and interfaces composing the specified class (including
   * itself) in order of most specific (the class itself) to the least (java.lang.Object).
   */
  public static List<Class<?>> componentClasses(Class<?> cls) {
    if (!componentsCache.containsKey(cls)) {
      List<Class<?>> classes = new ArrayList<>();
      classes.add(cls);
      classes.addAll(Arrays.asList(cls.getInterfaces()));

      Class<?> superClass = cls.getSuperclass();
      if (superClass != null) {
        classes.addAll(componentClasses(cls.getSuperclass()));
      }
      componentsCache.put(cls, classes);
    }
    return componentsCache.get(cls);
  }

  /**
   * Creates and returns a method descriptor for the method.
   */
  public static MethodDescriptor methodDescriptor(Method m) {
    return methodDescriptor(m.getName(), m.getParameterTypes());
  }

  /**
   * Creates and returns a method descriptor for a method with the specified name and parameter types.
   */
  public static MethodDescriptor methodDescriptor(String methodName, Class<?>... parameterTypes) {
    return new MethodDescriptor(methodName, parameterTypes);
  }

  /**
   * A method descriptor can be used to identify a method by its name and parameter types.
   */
  public static class MethodDescriptor {
    MethodDescriptor(String name, Class<?>... parameterTypes) {
      this.name = name;
      this.parameterTypes = parameterTypes;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;

      MethodDescriptor methodDescriptor = (MethodDescriptor) o;
      if (!name.equals(methodDescriptor.name)) return false;
      return Arrays.equals(parameterTypes, methodDescriptor.parameterTypes);
    }

    @Override
    public int hashCode() {
      int result = name.hashCode();
      result = 31 * result + Arrays.hashCode(parameterTypes);
      return result;
    }

    @Override
    public String toString() {
      return name + '(' +
          Stream.of(parameterTypes)
              .map(Object::toString)
              .collect(joining(", ")) +
          ')';
    }

    public final String name;
    public final Class<?>[] parameterTypes;
  }

  /**
   * Constructors cache.
   */
  private final static ConcurrentMap<Class<?>, Set<Constructor<?>>> ctorsCache = new ConcurrentHashMap<>();

  /**
   * Methods cache.
   */
  private final static ConcurrentMap<Class<?>, Map<MethodDescriptor, Method>> methodsCache = new ConcurrentHashMap<>();

  /**
   * Fields cache.
   */
  private final static ConcurrentMap<Class<?>, Map<String, Field>> fieldsCache = new ConcurrentHashMap<>();

  /**
   * Properties cache.
   */
  private final static ConcurrentMap<Class<?>, Map<String, Property>> propertiesCache = new ConcurrentHashMap<>();

  /**
   * Class components cache.
   */
  private final static ConcurrentMap<Class<?>, List<Class<?>>> componentsCache = new ConcurrentHashMap<>();
}