/*
 * Copyright (c) 2016 Vikash Madhow
 */

package ma.vi.base.reflect;

import ma.vi.base.collections.Maps;
import ma.vi.base.tuple.T2;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.util.*;

import static java.util.Collections.singleton;
import static ma.vi.base.reflect.Dissector.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class ClassDissectorTest {

  static class A {
    public A() {
    }

    protected A(int x) {
    }

    public void a() {
    }

    public void a(int... a) {
    }

    protected void b(int a) {
    }

    protected int b(String a) {
      return 0;
    }

    private void c() {
    }

    private void c(int a, String b, Object... c) {
    }

    public static void k() {
    }

    @Override
    public String toString() {
      return "A";
    }

    public int e;
    protected String f;
    private Object g;
  }

  interface B {
    void a();

    void d();

    default int identity(int x) {
      return x;
    }

    int e = 10;
    String y = "test";
  }

  static class C extends A implements B {
    @Override
    public void d() {
    }

    @Override
    protected void b(int x) {
    }

    public static void k() {
    }

    private void x() {
    }

    public int e;
    protected Object f;
    private Object g;

    public int h;
    protected String i;
    private Object j;
  }

  static class D extends A implements B {
    @Override
    public void a() {
    }

    @Override
    public void d() {
    }

    public int e;
    protected String f;
    private Object g;

    public int h;
    protected String i;
    private Object j;
  }

  static record E(String a, int b) {}

  static Map<MethodDescriptor, Method> objectMethods;

  static {
    try {
      objectMethods = Maps.of(
        T2.of(methodDescriptor("getClass"), Object.class.getDeclaredMethod("getClass")),
        T2.of(methodDescriptor("clone"), Object.class.getDeclaredMethod("clone")),
        T2.of(methodDescriptor("finalize"), Object.class.getDeclaredMethod("finalize")),
        T2.of(methodDescriptor("notify"), Object.class.getDeclaredMethod("notify")),
        T2.of(methodDescriptor("notifyAll"), Object.class.getDeclaredMethod("notifyAll")),
        T2.of(methodDescriptor("wait"), Object.class.getDeclaredMethod("wait")),
        T2.of(methodDescriptor("wait", long.class), Object.class.getDeclaredMethod("wait", long.class)),
        T2.of(methodDescriptor("wait", long.class, int.class),
              Object.class.getDeclaredMethod("wait", long.class, int.class)),
        T2.of(methodDescriptor("toString"), Object.class.getDeclaredMethod("toString")),
        T2.of(methodDescriptor("equals", Object.class), A.class.getMethod("equals", Object.class)),
        T2.of(methodDescriptor("hashCode"), Object.class.getMethod("hashCode"))
      );
    } catch (Exception e) {
      throw e instanceof RuntimeException ? (RuntimeException)e : new RuntimeException(e);
    }
  }

  @Test
  public void dissectA() throws Exception {
    assertEquals(
        constructors(A.class),
        Set.of(A.class.getDeclaredConstructor(), A.class.getDeclaredConstructor(int.class))
    );

    Map<MethodDescriptor, Method> methods = new HashMap<>(objectMethods);
    assertEquals(
        methods(A.class),
        Maps.put(methods,
                 T2.of(methodDescriptor("a"), A.class.getDeclaredMethod("a")),
                 T2.of(methodDescriptor("a", int[].class), A.class.getDeclaredMethod("a", int[].class)),
                 T2.of(methodDescriptor("b", int.class), A.class.getDeclaredMethod("b", int.class)),
                 T2.of(methodDescriptor("b", String.class), A.class.getDeclaredMethod("b", String.class)),
                 T2.of(methodDescriptor("c"), A.class.getDeclaredMethod("c")),
                 T2.of(methodDescriptor("c", int.class, String.class, Object[].class),
                       A.class.getDeclaredMethod("c", int.class, String.class, Object[].class)),
                 T2.of(methodDescriptor("k"), A.class.getDeclaredMethod("k")),
                 T2.of(methodDescriptor("toString"), A.class.getDeclaredMethod("toString")))
    );

    assertEquals(
        fields(A.class),
        Maps.of(T2.of("e", A.class.getDeclaredField("e")),
                T2.of("f", A.class.getDeclaredField("f")),
                T2.of("g", A.class.getDeclaredField("g")))
    );

    assertEquals(
        componentClasses(A.class),
        Arrays.asList(A.class, Object.class)
    );
  }

  @Test
  public void dissectB() throws Exception {
    assertEquals(constructors(B.class), Collections.emptySet());

    assertEquals(
        methods(B.class),
        Maps.of(T2.of(methodDescriptor("a"), B.class.getDeclaredMethod("a")),
                T2.of(methodDescriptor("d"), B.class.getDeclaredMethod("d")),
                T2.of(methodDescriptor("identity", int.class), B.class.getDeclaredMethod("identity", int.class)))
    );

    assertEquals(
        fields(B.class),
        Maps.of(T2.of("e", B.class.getDeclaredField("e")),
                T2.of("y", B.class.getDeclaredField("y")))
    );

    assertEquals(componentClasses(B.class), Collections.singletonList(B.class));
  }

  @Test
  public void dissectC() throws Exception {
    assertEquals(constructors(C.class), singleton(C.class.getDeclaredConstructor()));

    Map<MethodDescriptor, Method> methods = new HashMap<>(objectMethods);
    assertEquals(
        methods(C.class),
        Maps.put(methods,
                 T2.of(methodDescriptor("a"), A.class.getDeclaredMethod("a")),
                 T2.of(methodDescriptor("a", int[].class), A.class.getDeclaredMethod("a", int[].class)),
                 T2.of(methodDescriptor("b", String.class), A.class.getDeclaredMethod("b", String.class)),
                 T2.of(methodDescriptor("c"), A.class.getDeclaredMethod("c")),
                 T2.of(methodDescriptor("c", int.class, String.class, Object[].class),
                       A.class.getDeclaredMethod("c", int.class, String.class, Object[].class)),
                 T2.of(methodDescriptor("k"), C.class.getDeclaredMethod("k")),
                 T2.of(methodDescriptor("toString"), A.class.getDeclaredMethod("toString")),

                 T2.of(methodDescriptor("identity", int.class), B.class.getDeclaredMethod("identity", int.class)),

                 T2.of(methodDescriptor("d"), C.class.getDeclaredMethod("d")),
                 T2.of(methodDescriptor("b", int.class), C.class.getDeclaredMethod("b", int.class)),
                 T2.of(methodDescriptor("x"), C.class.getDeclaredMethod("x"))
        )
    );

    assertEquals(
        fields(C.class),
        Maps.of(T2.of("e", A.class.getDeclaredField("e")),
                T2.of("y", B.class.getDeclaredField("y")),
                T2.of("f", C.class.getDeclaredField("f")),
                T2.of("g", C.class.getDeclaredField("g")),
                T2.of("h", C.class.getDeclaredField("h")),
                T2.of("i", C.class.getDeclaredField("i")),
                T2.of("j", C.class.getDeclaredField("j")))
    );

    assertEquals(
        componentClasses(C.class),
        Arrays.asList(C.class, B.class, A.class, Object.class)
    );
  }

  @Test
  public void dissectE() {
    Map<String, Property> props = Dissector.properties(E.class);
    assertEquals(props.keySet(), Set.of("a", "b"));
    E e = new E("One", 1);
    assertEquals(props.get("a").get(e), "One");
    assertEquals(props.get("b").get(e), 1);
  }
}