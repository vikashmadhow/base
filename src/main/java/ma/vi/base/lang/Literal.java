/*
 * Copyright (c) 2016 Vikash Madhow
 */

package ma.vi.base.lang;

import ma.vi.base.reflect.Classes;
import ma.vi.base.string.Escape;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.*;

import static com.google.common.base.Preconditions.checkArgument;
import static ma.vi.base.lang.Errors.unchecked;
import static ma.vi.base.reflect.Classes.componentType;

/**
 * <p>
 * A literal is a value (object or primitive) which can be represented completely
 * as a piece of text (ideally small and easily human-readable) and which can be
 * reconstructed exactly from such a text representation. Values which fit this
 * informal definition includes primitives and their wrappers, strings and dates.
 * </p>
 *
 * <p>
 * Literals can be saved as simple string values for text serialization instead
 * of as references.
 * </p>
 *
 * <p>
 * A stronger definition of a literal is any value which cannot refer, directly or
 * transitively, to itself, thus its representation as a graph will always be acyclic.
 * With such a definition any object which does not contain a reference to itself
 * can be made into a literal. This is in line with text representations such as XML
 * which can be used to represent any object graph which does not have cyclic
 * references. However, if applied completely, such a definition would convert
 * a large number of objects to a single text form which would defeat the goal
 * of the Mapper to provide a representation-independent intermediate form of an
 * object graph.
 * </p>
 *
 * <p>
 * The above definition allows for arrays of literals to be treated as literals and,
 * since an array of literal is a literal, both single-dimensional and multi-dimensional
 * arrays of literals are literals. Such arrays are literalized as '[i0, i1, ...., in]'
 * with the text of each item escaped where necessary to not conflict with the surrounding
 * square braces and the item separator (,).
 * </p>
 *
 * @param <T> The type of values that this is a literal for.
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public interface Literal<T> {
  /**
   * Construct the value from its string representation.
   */
  T toValue(String repr);

  /**
   * Returns the string representation of the value.
   */
  String toText(T value);

  /**
   * The default representation of null as text used when literalising arrays of literal types
   * and serializing objects to text.
   */
  String NULL_LITERAL = "\\N";

  /**
   * Returns the Literal object for literalizing value of the specified class, or null
   * if the class is not a literal. Literals for unknown literal classes should first be
   * registered through {@link Register#add(Class, Literal)} for this method to return
   * true for them.
   */
  static <T> Literal<T> literal(Class<T> cls) {
    return Register.get(cls);
  }

  /**
   * Returns true if the class is a literal type. Primitive types and their wrappers,
   * enums, {@link String}, {@link Number} and {@link Date} are literals, and
   * so are any class for which a has been registered in the the Literal register
   * ({@link Register}).
   */
  static boolean isLiteral(Class<?> cls) {
    return Register.isLiteral(cls);
  }

  /**
   * This is a convenience method equivalent to getting the literal for the class
   * through {@link #literal(Class)} and using its {@link #toValue(String)}
   * method to get reconstruct the value from the specified string representation.
   */
  static <T> T toValue(Class<T> cls, String repr) {
    return literal(cls).toValue(repr);
  }

  /**
   * This is a convenience method equivalent to getting the literal for the class
   * through {@link #literal(Class)} and using its {@link #toText(Object)}}
   * method to get the string representation for specified value.
   */
  static <T> String toText(Class<T> cls, T value) {
    return literal(cls).toText(value);
  }

  /**
   * Literalize nulls as the {@link #NULL_LITERAL} so that subclasses need only
   * code for the non-null cases.
   */
  abstract class NullableLiteral<T> implements Literal<T> {
    @Override
    public T toValue(String repr) {
      return repr == null || repr.equals(NULL_LITERAL) ? null : toValueNonNull(repr);
    }

    @Override
    public String toText(T value) {
      return value == null ? NULL_LITERAL : toTextNonNull(value);
    }

    /**
     * Called to reconstruct the value from its string representation when
     * the later is non-null.
     */
    protected abstract T toValueNonNull(String repr);

    /**
     * Called to produce the string representation of the value when the
     * latter is non-null.
     */
    protected abstract String toTextNonNull(T value);

    NullableLiteral() {
    }
  }

  /**
   * Uses the {@link #toString()} method of the object to produce its text representation and
   * a constructor taking a single-argument of String type reconstruct object from
   * its string representation.
   */
  class ReflectiveLiteral<T> extends NullableLiteral<T> {
    private ReflectiveLiteral(Class<T> cls) {
      constructor = unchecked(() -> cls.getDeclaredConstructor(String.class));
    }

    @Override
    public T toValueNonNull(String repr) {
      return unchecked(() -> constructor.newInstance(repr));
    }

    @Override
    public String toTextNonNull(T value) {
      return value.toString();
    }

    /**
     * Single string argument constructor for constructing the literals from their
     * string representation.
     */
    private final Constructor<T> constructor;
  }

  /**
   * Similar to {@link ReflectiveLiteral} but uses the wrapper class corresponding
   * to a primitive class.
   */
  class PrimitiveLiteral<T> extends NullableLiteral<T> {
    private PrimitiveLiteral(Class<T> cls) {
      wrapperClass = Classes.wrapperClassOf(cls);
      constructor = unchecked(() -> (Constructor<T>) wrapperClass.getDeclaredConstructor(String.class));
    }

    @Override
    public T toValueNonNull(String repr) {
      return unchecked(() -> constructor.newInstance(repr));
    }

    @Override
    public String toTextNonNull(T value) {
      return value.toString();
    }

    /**
     * The wrapper class for the primitive type being literalized.
     */
    private final Class<?> wrapperClass;

    /**
     * Single string argument constructor for constructing the literals from their
     * string representation.
     */
    private final Constructor<T> constructor;
  }

  /**
   * A literalizer for the character type.
   */
  class CharacterLiteral extends NullableLiteral<Character> {
    @Override
    public Character toValueNonNull(String repr) {
      return repr.charAt(0);
    }

    @Override
    public String toTextNonNull(Character value) {
      return value.toString();
    }

    private CharacterLiteral() {
    }
  }

  /**
   * Literalization of dates using the {@link SimpleDateFormat} "dd-MMM-yyyy HH:mm:ss.SSS Z".
   */
  class DateLiteral extends NullableLiteral<Date> {
    @Override
    public Date toValueNonNull(String repr) {
      return unchecked(() -> df.parse(repr));
    }

    @Override
    public String toTextNonNull(Date value) {
      return df.format(value);
    }

    private DateLiteral() {
    }

    /**
     * Date formatter for parsing and formatting dates in their literal forms.
     */
    static final SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss.SSS");
  }

  /**
   * Literalization of enums.
   */
  class EnumLiteral<T extends Enum<T>> extends NullableLiteral<T> {
    private EnumLiteral(Class<T> cls) {
      valueOf = unchecked(() -> cls.getDeclaredMethod("valueOf", String.class));
    }

    @Override
    public T toValueNonNull(String repr) {
      return unchecked(() -> (T) valueOf.invoke(null, repr));
    }

    @Override
    public String toTextNonNull(T value) {
      return value.toString();
    }

    /**
     * The enum valueOf method for reconstruction.
     */
    private final Method valueOf;
  }

  /**
   * Strings are literalized as themselves.
   */
  class StringLiteral extends NullableLiteral<String> {
    @Override
    protected String toValueNonNull(String repr) {
      return repr;
    }

    @Override
    protected String toTextNonNull(String value) {
      return value;
    }

    private StringLiteral() {
    }
  }

  /**
   * Array of literals are literals, literalized as '[a, b, c, ....]'. This is a recursive definition
   * allowing higher dimensional arrays of literals to be also considered literals.
   */
  class ArrayLiteral<T> extends NullableLiteral<T> {
    private ArrayLiteral(Class<T> arrayClass) {
      componentType = arrayClass.getComponentType();
      componentTypeLiteral = literal(componentType);
      multiDimensional = componentType.isArray();
    }

    @Override
    protected T toValueNonNull(String repr) {
      List<Object> items = new ArrayList<>();
      String remapped = ARRAY_ESC.map(repr);
      char c = remapped.charAt(1);
      if (c != ']') {
        int level = 0;
        StringBuilder item = new StringBuilder();
        for (int i = 1; i < remapped.length() - 1; i++) {
          c = remapped.charAt(i);
          if (c == '[') {
            level++;
            item.append(c);
          } else if (c == ']') {
            level--;
            item.append(c);
          } else if (c == ',') {
            if (level == 0) {
              items.add(itemValue(item.toString()));
              item.delete(0, item.length());
            } else {
              item.append(c);
            }
          } else {
            item.append(c);
          }
        }
        if (item.length() > 0) {
          items.add(itemValue(item.toString()));
        }
      }

      // put list of read items into an array to return
      int i = 0;
      T array = (T) Array.newInstance(componentType, items.size());
      for (Object obj : items) Array.set(array, i++, obj);
      return array;
    }

    private Object itemValue(String item) {
      return componentTypeLiteral.toValue(multiDimensional ? item : ARRAY_ESC.demap(item));
    }

    @Override
    protected String toTextNonNull(T array) {
      StringBuilder repr = new StringBuilder("[");
      int length = Array.getLength(array);
      for (int i = 0; i < length; i++) {
        Object item = Array.get(array, i);
        if (i > 0) {
          repr.append(',');
        }
        String itemAsText = componentTypeLiteral.toText(item);
        repr.append(multiDimensional ? itemAsText : ARRAY_ESC.escape(itemAsText));
      }
      return repr.append("]").toString();
    }

    /**
     * The component type of the array (can also be an array in case of multi-dimensional arrays).
     */
    private final Class<?> componentType;

    /**
     * The literal the component type.
     */
    private final Literal componentTypeLiteral;

    /**
     * Whether this is a literal for single or multi-dimensional array.
     * In a multi-dimensional array, the component types are also arrays.
     */
    private final boolean multiDimensional;

    /**
     * When literalizing arrays, square brackets are used to surround the array and the
     * comma for separating items inside. These characters should thus be escaped in the
     * literal forms of the array items and un-escaped when reconstructing the array. The
     * {@link Escape} utility class is used for this purpose.
     */
    private static final Escape ARRAY_ESC = new Escape("[,]");
  }

  /**
   * The register for literal types.
   */
  class Register {
    public <T> void add(Class<T> cls, Literal<T> literal) {
      checkArgument(!isBaseLiteral(cls), cls + " is a basic literal class whose literalization " +
          "is already defined and cannot be replaced as it could break the relationships " +
          "between the existing classes in the system");
      register.put(cls, literal);
    }

    public void remove(Class<?> cls) {
      checkArgument(!isBaseLiteral(cls), cls + " is a basic literal class whose literalization " +
          "is already defined and cannot be removed as it could break the relationships " +
          "between the existing classes in the system");
      register.remove(cls);
    }

    public static <T> Literal<T> get(Class<T> cls) {
      Literal<T> literal = (Literal<T>) register.get(cls);
      if (literal == null) {
        // auto-register known base classes
        if (cls.isEnum()) {
          literal = new EnumLiteral(cls);
        } else if (char.class.equals(cls) ||
            (Character.class.equals(cls))) {
          literal = (Literal<T>) new CharacterLiteral();
        } else if (cls.isPrimitive()) {
          literal = new PrimitiveLiteral<>(cls);
        } else if (cls.isArray() &&
            isLiteral(cls.getComponentType())) {
          literal = new ArrayLiteral<>(cls);
        }

        if (literal != null) {
          synchronized (Literal.class) {
            if (!register.containsKey(cls)) {
              register.put(cls, literal);
            }
          }
        }
      }
      return literal;
    }

    public static boolean isLiteral(Class<?> cls) {
      return register.containsKey(cls) ||
          cls.isEnum() ||
          cls.isPrimitive() ||
          (cls.isArray() && register.containsKey(componentType(cls)));

    }

    public static boolean isBaseLiteral(Class<?> cls) {
      return baseLiterals.containsKey(cls) ||
          cls.isEnum() ||
          cls.isPrimitive() ||
          (cls.isArray() && baseLiterals.containsKey(componentType(cls)));
    }

    private Register() {
    }

    private static final Map<Class<?>, Literal<?>> baseLiterals = new HashMap<>();
    private static final Map<Class<?>, Literal<?>> register = new HashMap<>();

    static {
      baseLiterals.put(Date.class, new DateLiteral());
      baseLiterals.put(String.class, new StringLiteral());
      baseLiterals.put(StringBuilder.class, new ReflectiveLiteral<>(StringBuilder.class));
      baseLiterals.put(StringBuffer.class, new ReflectiveLiteral<>(StringBuffer.class));

      baseLiterals.put(Boolean.class, new ReflectiveLiteral<>(Boolean.class));
      baseLiterals.put(Character.class, new CharacterLiteral());
      baseLiterals.put(Long.class, new ReflectiveLiteral<>(Long.class));
      baseLiterals.put(Integer.class, new ReflectiveLiteral<>(Integer.class));
      baseLiterals.put(Short.class, new ReflectiveLiteral<>(Short.class));
      baseLiterals.put(Byte.class, new ReflectiveLiteral<>(Byte.class));
      baseLiterals.put(Float.class, new ReflectiveLiteral<>(Float.class));
      baseLiterals.put(Double.class, new ReflectiveLiteral<>(Double.class));

      baseLiterals.put(BigInteger.class, new ReflectiveLiteral<>(BigInteger.class));
      baseLiterals.put(BigDecimal.class, new ReflectiveLiteral<>(BigDecimal.class));

      baseLiterals.put(boolean.class, new PrimitiveLiteral<>(Boolean.class));
      baseLiterals.put(char.class, new CharacterLiteral());
      baseLiterals.put(long.class, new PrimitiveLiteral<>(Long.class));
      baseLiterals.put(int.class, new PrimitiveLiteral<>(Integer.class));
      baseLiterals.put(short.class, new PrimitiveLiteral<>(Short.class));
      baseLiterals.put(byte.class, new PrimitiveLiteral<>(Byte.class));
      baseLiterals.put(float.class, new PrimitiveLiteral<>(Float.class));
      baseLiterals.put(double.class, new PrimitiveLiteral<>(Double.class));

      register.putAll(baseLiterals);
    }
  }
}
