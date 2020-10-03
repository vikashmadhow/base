/*
 * Copyright (c) 2016 Vikash Madhow
 */

package ma.vi.base.tuple;

/**
 * A 2-tuple contains a pair of objects accessible as the fields <b>a</b> and <b>b</b>.
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class T2<A, B> extends AbstractTuple {
  public T2() {
    this(null, null);
  }

  public T2(A a, B b) {
    this.a = a;
    this.b = b;
  }

  @Override
  public int size() {
    return 2;
  }

  @Override
  public Object get(int i) {
    return switch (i) {
      case 0 -> a;
      case 1 -> b;
      default -> throw new IndexOutOfBoundsException("Index " + i + " is out of bounds for a " + size() + "-tuple");
    };
  }

  @Override
  public T2<A, B> clone() throws CloneNotSupportedException {
    return (T2<A, B>) super.clone();
  }

  public static <A, B> T2<A, B> of(A a, B b) {
    return new T2<>(a, b);
  }

  public A a() {
    return a;
  }

  public B b() {
    return b;
  }

  public A a;
  public B b;
}