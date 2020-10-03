/*
 * Copyright (c) 2016 Vikash Madhow
 */

package ma.vi.base.tuple;

/**
 * A 3-tuple contains 3 typed objects accessible as the fields <b>a</b>, <b>b</b> and <b>c</b>.
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class T3<A, B, C> extends AbstractTuple {
  public T3() {
    this(null, null, null);
  }

  public T3(A a, B b, C c) {
    this.a = a;
    this.b = b;
    this.c = c;
  }

  @Override
  public int size() {
    return 3;
  }

  @Override
  public Object get(int i) {
    return switch (i) {
      case 0 -> a;
      case 1 -> b;
      case 2 -> c;
      default -> throw new IndexOutOfBoundsException("Index " + i + " is out of bounds for a " + size() + "-tuple");
    };
  }

  @Override
  public T3<A, B, C> clone() throws CloneNotSupportedException {
    return (T3<A, B, C>) super.clone();
  }

  public A a() {
    return a;
  }

  public B b() {
    return b;
  }

  public C c() {
    return c;
  }

  public static <A, B, C> T3<A, B, C> of(A a, B b, C c) {
    return new T3<>(a, b, c);
  }

  public final A a;
  public final B b;
  public final C c;
}