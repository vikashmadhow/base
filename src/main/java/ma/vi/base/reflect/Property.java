/*
 * Copyright (c) 2018 Vikash Madhow
 */

package ma.vi.base.reflect;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Objects;

import static ma.vi.base.lang.Errors.checkArgument;
import static ma.vi.base.lang.Errors.unchecked;
import static ma.vi.base.string.Strings.uncapFirst;

/**
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class Property {
  Property(Field field) {
    checkArgument(field != null, "Field must not be null");
    this.field = field;
    this.name = field.getName();
    this.getter = this.setter = null;
  }

  Property(String name, Method getter, Method setter) {
    checkArgument(getter != null || setter != null, "Both getter and setter cannot be null");
    this.field = null;
    this.name = name;
    this.getter = getter;
    this.setter = setter;
  }

  public Class<?> type() {
    return field != null ? field.getType()
                         : getter != null ? getter.getReturnType()
                                          : setter.getParameterTypes()[0];
  }

  public String name() {
    return name;
  }

  public Object get(Object object) {
    checkArgument(field != null || getter != null, "This property cannot be read from");
    return unchecked(() -> field != null ? field.get(object) : getter.invoke(object));
  }

  public void set(Object object, Object value) {
    checkArgument(field != null || setter != null, "This property cannot be written to");
    if (field != null) {
      unchecked(() -> field.set(object, value));
    } else {
      unchecked(() -> setter.invoke(object, value));
    }
  }

  public boolean isReadable() {
    return getter != null;
  }

  public boolean isWritable() {
    return getter != null;
  }

  public static String propertyNameFromMethod(String methodName) {
    if (methodName.startsWith("is")) {
      return uncapFirst(methodName.substring(2));
    } else if (methodName.startsWith("get") ||
        methodName.startsWith("set")) {
      return uncapFirst(methodName.substring(3));
    } else {
      return methodName;
    }
  }

  @Override
  public String toString() {
    return name();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    Property property = (Property)o;
    if (!Objects.equals(name, property.name)) return false;
    if (!Objects.equals(field, property.field)) return false;
    if (!Objects.equals(getter, property.getter)) return false;
    return Objects.equals(setter, property.setter);
  }

  @Override
  public int hashCode() {
    int result = field != null ? field.hashCode() : 0;
    result = 31 * result + name.hashCode();
    result = 31 * result + (getter != null ? getter.hashCode() : 0);
    result = 31 * result + (setter != null ? setter.hashCode() : 0);
    return result;
  }

  private final String name;
  private final Field field;
  private final Method getter;
  private final Method setter;
}
