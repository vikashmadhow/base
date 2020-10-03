/*
 * Copyright (c) 2016 Vikash Madhow
 */

package ma.vi.base.tuple;

/**
 * A 4-tuple contains 4 typed objects accessible as the fields <b>a</b>, <b>b</b>, <b>c</b>
 * and <b>d</b>.
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class T4<A, B, C, D> extends AbstractTuple {
  public T4() {
    this(null, null, null, null);
  }

  public T4(A a, B b, C c, D d) {
    this.a = a;
    this.b = b;
    this.c = c;
    this.d = d;
  }

  @Override
  public int size() {
    return 4;
  }

  @Override
  public Object get(int i) {
    return switch (i) {
      case 0 -> a;
      case 1 -> b;
      case 2 -> c;
      case 3 -> d;
      default -> throw new IndexOutOfBoundsException("Index " + i + " is out of bounds for a " + size() + "-tuple");
    };
  }

  @Override
  public T4<A, B, C, D> clone() throws CloneNotSupportedException {
    return (T4<A, B, C, D>) super.clone();
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

  public D d() {
    return d;
  }

  public static <A, B, C, D> T4<A, B, C, D> of(A a, B b, C c, D d) {
    return new T4<>(a, b, c, d);
  }

  public final A a;
  public final B b;
  public final C c;
  public final D d;
}