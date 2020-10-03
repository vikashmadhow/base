/*
 * Copyright (c) 2016 Vikash Madhow
 */

package ma.vi.base.tuple;

/**
 * A 5-tuple contains 5 typed objects accessible as the fields <b>a</b>, <b>b</b>, <b>c</b>,
 * <b>d</b> and <b>e</b>.
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class T5<A, B, C, D, E> extends AbstractTuple {
  public T5() {
    this(null, null, null, null, null);
  }

  public T5(A a, B b, C c, D d, E e) {
    this.a = a;
    this.b = b;
    this.c = c;
    this.d = d;
    this.e = e;
  }

  @Override
  public int size() {
    return 5;
  }

  @Override
  public Object get(int i) {
    return switch (i) {
      case 0 -> a;
      case 1 -> b;
      case 2 -> c;
      case 3 -> d;
      case 4 -> e;
      default -> throw new IndexOutOfBoundsException("Index " + i + " is out of bounds for a " + size() + "-tuple");
    };
  }

  @Override
  public T5<A, B, C, D, E> clone() throws CloneNotSupportedException {
    return (T5<A, B, C, D, E>) super.clone();
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

  public E e() {
    return e;
  }

  public static <A, B, C, D, E> T5<A, B, C, D, E> of(A a, B b, C c, D d, E e) {
    return new T5<>(a, b, c, d, e);
  }

  public final A a;
  public final B b;
  public final C c;
  public final D d;
  public final E e;
}