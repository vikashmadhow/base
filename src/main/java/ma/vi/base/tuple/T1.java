/*
 * Copyright (c) 2016 Vikash Madhow
 */

package ma.vi.base.tuple;

/**
 * A 1-tuple contains one object accessible as the field <b>a</b>.
 * 'a' is modifiable to allow this object to be used as an object holder.
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class T1<A> extends AbstractTuple {
  public T1() {
    this(null);
  }

  public T1(A a) {
    this.a = a;
  }

  @Override
  public int size() {
    return 1;
  }

  @Override
  public Object get(int i) {
    if (i == 0) {
      return a;
    }
    throw new IndexOutOfBoundsException("Index " + i + " is out of bounds for a 1-tuple");
  }

  @Override
  public T1<A> clone() throws CloneNotSupportedException {
    return (T1<A>) super.clone();
  }

  public A a() {
    return a;
  }

  public static <A> T1<A> of(A a) {
    return new T1<>(a);
  }

  public A a;
}