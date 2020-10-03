/*
 * Copyright (c) 2016 Vikash Madhow
 */

package ma.vi.base.lang;

import org.junit.Test;

import java.util.Date;

import static ma.vi.base.lang.Literal.*;
import static ma.vi.base.reflect.Classes.classOf;
import static org.junit.Assert.*;

/**
 * Test literalization code.
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class LiteralTest {

  enum E {a, b, c}

  @Test
  public void stringLiteralization() throws Exception {
    assertEquals(toText(String.class, null), NULL_LITERAL);
    assertEquals(toText(String.class, "This is a test"), "This is a test");
    assertEquals(toText(String.class, ""), "");
  }

  @Test
  public void intLiteralization() throws Exception {
    assertEquals(toText(int.class, null), NULL_LITERAL);
    assertEquals(toText(int.class, 0), "0");
    assertEquals(toText(int.class, 1), "1");
    assertEquals(toText(int.class, -1), "-1");
    assertEquals(toText(int.class, Integer.MIN_VALUE), String.valueOf(Integer.MIN_VALUE));
    assertEquals(toText(int.class, Integer.MAX_VALUE), String.valueOf(Integer.MAX_VALUE));
    assertEquals(toText(Integer.class, null), NULL_LITERAL);
    assertEquals(toText(Integer.class, 0), "0");
    assertEquals(toText(Integer.class, 1), "1");
    assertEquals(toText(Integer.class, -1), "-1");
    assertEquals(toText(Integer.class, Integer.MIN_VALUE), String.valueOf(Integer.MIN_VALUE));
    assertEquals(toText(Integer.class, Integer.MAX_VALUE), String.valueOf(Integer.MAX_VALUE));
  }

  @Test
  public void longLiteralization() throws Exception {
    assertEquals(toText(long.class, null), NULL_LITERAL);
    assertEquals(toText(long.class, 0L), "0");
    assertEquals(toText(long.class, 1L), "1");
    assertEquals(toText(long.class, -1L), "-1");
    assertEquals(toText(long.class, Long.MIN_VALUE), String.valueOf(Long.MIN_VALUE));
    assertEquals(toText(long.class, Long.MAX_VALUE), String.valueOf(Long.MAX_VALUE));
    assertEquals(toText(Long.class, null), NULL_LITERAL);
    assertEquals(toText(Long.class, 0L), "0");
    assertEquals(toText(Long.class, 1L), "1");
    assertEquals(toText(Long.class, -1L), "-1");
    assertEquals(toText(Long.class, Long.MIN_VALUE), String.valueOf(Long.MIN_VALUE));
    assertEquals(toText(Long.class, Long.MAX_VALUE), String.valueOf(Long.MAX_VALUE));
  }

  @Test
  public void shortLiteralization() throws Exception {
    assertEquals(toText(short.class, null), NULL_LITERAL);
    assertEquals(toText(short.class, (short)0), "0");
    assertEquals(toText(short.class, (short)1), "1");
    assertEquals(toText(short.class, (short)-1), "-1");
    assertEquals(toText(short.class, Short.MIN_VALUE), String.valueOf(Short.MIN_VALUE));
    assertEquals(toText(short.class, Short.MAX_VALUE), String.valueOf(Short.MAX_VALUE));
    assertEquals(toText(Short.class, null), NULL_LITERAL);
    assertEquals(toText(Short.class, (short)0), "0");
    assertEquals(toText(Short.class, (short)1), "1");
    assertEquals(toText(Short.class, (short)-1), "-1");
    assertEquals(toText(Short.class, Short.MIN_VALUE), String.valueOf(Short.MIN_VALUE));
    assertEquals(toText(Short.class, Short.MAX_VALUE), String.valueOf(Short.MAX_VALUE));
  }

  @Test
  public void byteLiteralization() throws Exception {
    assertEquals(toText(byte.class, null), NULL_LITERAL);
    assertEquals(toText(byte.class, (byte)0), "0");
    assertEquals(toText(byte.class, (byte)1), "1");
    assertEquals(toText(byte.class, (byte)-1), "-1");
    assertEquals(toText(byte.class, Byte.MIN_VALUE), String.valueOf(Byte.MIN_VALUE));
    assertEquals(toText(byte.class, Byte.MAX_VALUE), String.valueOf(Byte.MAX_VALUE));
    assertEquals(toText(Byte.class, null), NULL_LITERAL);
    assertEquals(toText(Byte.class, (byte)0), "0");
    assertEquals(toText(Byte.class, (byte)1), "1");
    assertEquals(toText(Byte.class, (byte)-1), "-1");
    assertEquals(toText(Byte.class, Byte.MIN_VALUE), String.valueOf(Byte.MIN_VALUE));
    assertEquals(toText(Byte.class, Byte.MAX_VALUE), String.valueOf(Byte.MAX_VALUE));
  }

  @Test
  public void charLiteralization() throws Exception {
    assertEquals(toText(char.class, null), NULL_LITERAL);
    assertEquals(toText(char.class, '0'), "0");
    assertEquals(toText(char.class, '\1'), "\1");
    assertEquals(toText(char.class, '\ue000'), "\ue000");
    assertEquals(toText(char.class, Character.MIN_VALUE), String.valueOf(Character.MIN_VALUE));
    assertEquals(toText(char.class, Character.MAX_VALUE), String.valueOf(Character.MAX_VALUE));
    assertEquals(toText(Character.class, null), NULL_LITERAL);
    assertEquals(toText(Character.class, '0'), "0");
    assertEquals(toText(Character.class, '\1'), "\1");
    assertEquals(toText(Character.class, '\ue000'), "\ue000");
    assertEquals(toText(Character.class, Character.MIN_VALUE), String.valueOf(Character.MIN_VALUE));
    assertEquals(toText(Character.class, Character.MAX_VALUE), String.valueOf(Character.MAX_VALUE));
  }

  @Test
  public void floatLiteralization() throws Exception {
    assertEquals(toText(float.class, null), NULL_LITERAL);
    assertEquals(toText(float.class, 0F), "0.0");
    assertEquals(toText(float.class, 1F), "1.0");
    assertEquals(toText(float.class, -1F), "-1.0");
    assertEquals(toText(float.class, 1.1E-20F), "1.1E-20");
    assertEquals(toText(float.class, 1.1E20F), "1.1E20");
    assertEquals(toText(float.class, -1.1E-20F), "-1.1E-20");
    assertEquals(toText(float.class, -1.1E20F), "-1.1E20");
    assertEquals(toText(float.class, Float.MIN_VALUE), String.valueOf(Float.MIN_VALUE));
    assertEquals(toText(float.class, Float.MAX_VALUE), String.valueOf(Float.MAX_VALUE));
    assertEquals(toText(Float.class, null), NULL_LITERAL);
    assertEquals(toText(Float.class, 0F), "0.0");
    assertEquals(toText(Float.class, 1F), "1.0");
    assertEquals(toText(Float.class, -1F), "-1.0");
    assertEquals(toText(Float.class, 1.1E-20F), "1.1E-20");
    assertEquals(toText(Float.class, 1.1E20F), "1.1E20");
    assertEquals(toText(Float.class, -1.1E-20F), "-1.1E-20");
    assertEquals(toText(Float.class, -1.1E20F), "-1.1E20");
    assertEquals(toText(Float.class, Float.MIN_VALUE), String.valueOf(Float.MIN_VALUE));
    assertEquals(toText(Float.class, Float.MAX_VALUE), String.valueOf(Float.MAX_VALUE));
  }

  @Test
  public void doubleLiteralization() throws Exception {
    assertEquals(toText(double.class, null), NULL_LITERAL);
    assertEquals(toText(double.class, 0D), "0.0");
    assertEquals(toText(double.class, 1D), "1.0");
    assertEquals(toText(double.class, -1D), "-1.0");
    assertEquals(toText(double.class, 1.1E-20D), "1.1E-20");
    assertEquals(toText(double.class, 1.1E20D), "1.1E20");
    assertEquals(toText(double.class, -1.1E-20D), "-1.1E-20");
    assertEquals(toText(double.class, -1.1E20D), "-1.1E20");
    assertEquals(toText(double.class, Double.MIN_VALUE), String.valueOf(Double.MIN_VALUE));
    assertEquals(toText(double.class, Double.MAX_VALUE), String.valueOf(Double.MAX_VALUE));
    assertEquals(toText(Double.class, null), NULL_LITERAL);
    assertEquals(toText(Double.class, 0D), "0.0");
    assertEquals(toText(Double.class, 1D), "1.0");
    assertEquals(toText(Double.class, -1D), "-1.0");
    assertEquals(toText(Double.class, 1.1E-20D), "1.1E-20");
    assertEquals(toText(Double.class, 1.1E20D), "1.1E20");
    assertEquals(toText(Double.class, -1.1E-20D), "-1.1E-20");
    assertEquals(toText(Double.class, -1.1E20D), "-1.1E20");
    assertEquals(toText(Double.class, Double.MIN_VALUE), String.valueOf(Double.MIN_VALUE));
    assertEquals(toText(Double.class, Double.MAX_VALUE), String.valueOf(Double.MAX_VALUE));
  }

  @Test
  public void booleanLiteralization() throws Exception {
    assertEquals(toText(boolean.class, null), NULL_LITERAL);
    assertEquals(toText(boolean.class, true), "true");
    assertEquals(toText(boolean.class, false), "false");
    assertEquals(toText(Boolean.class, null), NULL_LITERAL);
    assertEquals(toText(Boolean.class, true), "true");
    assertEquals(toText(Boolean.class, false), "false");
  }

  @Test
  public void dateLiteralization() throws Exception {
    assertEquals(toText(Date.class, null), NULL_LITERAL);
    assertEquals(toText(Date.class, DateLiteral.df.parse("1-JAN-2015 22:15:21.123")), "01-Jan-2015 22:15:21.123");
    assertEquals(toText(Date.class, DateLiteral.df.parse("1-JAN-2015 1:1:1.1")), "01-Jan-2015 01:01:01.001");
  }

  @Test
  public void enumLiteralization() throws Exception {
    assertEquals(toText(E.class, null), NULL_LITERAL);
    assertEquals(toText(E.class, E.a), "a");
    assertEquals(toText(E.class, E.b), "b");
    assertEquals(toText(E.class, E.c), "c");
  }

  @Test
  public void arrayLiteralization() throws Exception {
    int[] a = new int[]{1, 2, 3};
    String[][] b = new String[][]{new String[]{"a"}, {"b", "c"}, {"[,]", "[,]"}};
    E[] c = new E[]{E.a, E.a, E.b, E.c};
    int[] d = new int[0];
    int[][] e = new int[][]{{}, {}};
    int[][][] f = new int[][][]{{{1, 2, 3}, {4}}, {{7, 8}}};

    assertEquals(toText(a.getClass(), null), NULL_LITERAL);
    assertEquals(toText(classOf(a), a), "[1,2,3]");
    assertEquals(toText(classOf(b), b), "[[a],[b,c],[\\[\\,\\],\\[\\,\\]]]");
    assertEquals(toText(classOf(c), c), "[a,a,b,c]");
    assertEquals(toText(classOf(d), d), "[]");
    assertEquals(toText(classOf(e), e), "[[],[]]");
    assertEquals(toText(classOf(f), f), "[[[1,2,3],[4]],[[7,8]]]");
  }


  @Test
  public void stringReconstruction() throws Exception {
    assertNull(toValue(String.class, NULL_LITERAL));
    assertEquals(toValue(String.class, "This is a test"), "This is a test");
    assertEquals(toValue(String.class, ""), "");
  }

  @Test
  public void intReconstruction() throws Exception {
    assertNull(toValue(int.class, NULL_LITERAL));
    assertEquals((int)toValue(int.class, "0"), 0);
    assertEquals((int)toValue(int.class, "1"), 1);
    assertEquals((int)toValue(int.class, "-1"), -1);
    assertEquals((int)toValue(int.class, String.valueOf(Integer.MIN_VALUE)), Integer.MIN_VALUE);
    assertEquals((int)toValue(int.class, String.valueOf(Integer.MAX_VALUE)), Integer.MAX_VALUE);
    assertNull(toValue(Integer.class, NULL_LITERAL));
    assertEquals((int)toValue(Integer.class, "0"), 0);
    assertEquals((int)toValue(Integer.class, "1"), 1);
    assertEquals((int)toValue(Integer.class, "-1"), -1);
    assertEquals((int)toValue(Integer.class, String.valueOf(Integer.MIN_VALUE)), Integer.MIN_VALUE);
    assertEquals((int)toValue(Integer.class, String.valueOf(Integer.MAX_VALUE)), Integer.MAX_VALUE);
  }

  @Test
  public void longReconstruction() throws Exception {
    assertNull(toValue(long.class, NULL_LITERAL));
    assertEquals((long)toValue(long.class, "0"), 0L);
    assertEquals((long)toValue(long.class, "1"), 1L);
    assertEquals((long)toValue(long.class, "-1"), -1L);
    assertEquals((long)toValue(long.class, String.valueOf(Long.MIN_VALUE)), Long.MIN_VALUE);
    assertEquals((long)toValue(long.class, String.valueOf(Long.MAX_VALUE)), Long.MAX_VALUE);
    assertNull(toValue(Long.class, NULL_LITERAL));
    assertEquals((long)toValue(Long.class, "0"), 0L);
    assertEquals((long)toValue(Long.class, "1"), 1L);
    assertEquals((long)toValue(Long.class, "-1"), -1L);
    assertEquals((long)toValue(Long.class, String.valueOf(Long.MIN_VALUE)), Long.MIN_VALUE);
    assertEquals((long)toValue(Long.class, String.valueOf(Long.MAX_VALUE)), Long.MAX_VALUE);
  }

  @Test
  public void shortReconstruction() throws Exception {
    assertNull(toValue(short.class, NULL_LITERAL));
    assertEquals((short)toValue(short.class, "0"), 0);
    assertEquals((short)toValue(short.class, "1"), 1);
    assertEquals((short)toValue(short.class, "-1"), -1);
    assertEquals((short)toValue(short.class, String.valueOf(Short.MIN_VALUE)), Short.MIN_VALUE);
    assertEquals((short)toValue(short.class, String.valueOf(Short.MAX_VALUE)), Short.MAX_VALUE);
    assertNull(toValue(Short.class, NULL_LITERAL));
    assertEquals((short)toValue(Short.class, "0"), 0);
    assertEquals((short)toValue(Short.class, "1"), 1);
    assertEquals((short)toValue(Short.class, "-1"), -1);
    assertEquals((short)toValue(Short.class, String.valueOf(Short.MIN_VALUE)), Short.MIN_VALUE);
    assertEquals((short)toValue(Short.class, String.valueOf(Short.MAX_VALUE)), Short.MAX_VALUE);
  }

  @Test
  public void byteReconstruction() throws Exception {
    assertNull(toValue(byte.class, NULL_LITERAL));
    assertEquals((byte)toValue(byte.class, "0"), 0);
    assertEquals((byte)toValue(byte.class, "1"), 1);
    assertEquals((byte)toValue(byte.class, "-1"), -1);
    assertEquals((byte)toValue(byte.class, String.valueOf(Byte.MIN_VALUE)), Byte.MIN_VALUE);
    assertEquals((byte)toValue(byte.class, String.valueOf(Byte.MAX_VALUE)), Byte.MAX_VALUE);
    assertNull(toValue(Byte.class, NULL_LITERAL));
    assertEquals((byte)toValue(Byte.class, "0"), 0);
    assertEquals((byte)toValue(Byte.class, "1"), 1);
    assertEquals((byte)toValue(Byte.class, "-1"), -1);
    assertEquals((byte)toValue(Byte.class, String.valueOf(Byte.MIN_VALUE)), Byte.MIN_VALUE);
    assertEquals((byte)toValue(Byte.class, String.valueOf(Byte.MAX_VALUE)), Byte.MAX_VALUE);
  }

  @Test
  public void charReconstruction() throws Exception {
    assertNull(toValue(char.class, NULL_LITERAL));
    assertEquals((char)toValue(char.class, "0"), '0');
    assertEquals((char)toValue(char.class, "\1"), '\1');
    assertEquals((char)toValue(char.class, "\ue000"), '\ue000');
    assertEquals((char)toValue(char.class, String.valueOf(Character.MIN_VALUE)), Character.MIN_VALUE);
    assertEquals((char)toValue(char.class, String.valueOf(Character.MAX_VALUE)), Character.MAX_VALUE);
    assertNull(toValue(Character.class, NULL_LITERAL));
    assertEquals((char)toValue(Character.class, "0"), '0');
    assertEquals((char)toValue(Character.class, "\1"), '\1');
    assertEquals((char)toValue(Character.class, "\ue000"), '\ue000');
    assertEquals((char)toValue(Character.class, String.valueOf(Character.MIN_VALUE)), Character.MIN_VALUE);
    assertEquals((char)toValue(Character.class, String.valueOf(Character.MAX_VALUE)), Character.MAX_VALUE);
  }

  @Test
  public void floatReconstruction() throws Exception {
    assertNull(toValue(float.class, NULL_LITERAL));
    assertEquals((double)toValue(float.class, "0"), 0.0F, 1E-10);
    assertEquals((double)toValue(float.class, "1"), 1.0F, 1E-10);
    assertEquals((double)toValue(float.class, "-1"), -1.0F, 1E-10);
    assertEquals((double)toValue(float.class, "1.1E-20"), 1.1E-20F, 1E-10);
    assertEquals((double)toValue(float.class, "1.1E20"), 1.1E20F, 1E-10);
    assertEquals((double)toValue(float.class, "-1.1E-20"), -1.1E-20F, 1E-10);
    assertEquals((double)toValue(float.class, "-1.1E20"), -1.1E20F, 1E-10);
    assertEquals((double)toValue(float.class, String.valueOf(Float.MIN_VALUE)), Float.MIN_VALUE, 1E-10);
    assertEquals((double)toValue(float.class, String.valueOf(Float.MAX_VALUE)), Float.MAX_VALUE, 1E-10);
    assertNull(toValue(Float.class, NULL_LITERAL));
    assertEquals((double)toValue(Float.class, "0"), 0.0F, 1E-10);
    assertEquals((double)toValue(Float.class, "1"), 1.0F, 1E-10);
    assertEquals((double)toValue(Float.class, "-1"), -1.0F, 1E-10);
    assertEquals((double)toValue(Float.class, "1.1E-20"), 1.1E-20F, 1E-10);
    assertEquals((double)toValue(Float.class, "1.1E20"), 1.1E20F, 1E-10);
    assertEquals((double)toValue(Float.class, "-1.1E-20"), -1.1E-20F, 1E-10);
    assertEquals((double)toValue(Float.class, "-1.1E20"), -1.1E20F, 1E-10);
    assertEquals((double)toValue(Float.class, String.valueOf(Float.MIN_VALUE)), Float.MIN_VALUE, 1E-10);
    assertEquals((double)toValue(Float.class, String.valueOf(Float.MAX_VALUE)), Float.MAX_VALUE, 1E-10);
  }

  @Test
  public void doubleReconstruction() throws Exception {
    assertNull(toValue(double.class, NULL_LITERAL));
    assertEquals(toValue(double.class, "0"), 0.0D, 1E-10);
    assertEquals(toValue(double.class, "1"), 1.0D, 1E-10);
    assertEquals(toValue(double.class, "-1"), -1.0D, 1E-10);
    assertEquals(toValue(double.class, "1.1E-20"), 1.1E-20D, 1E-10);
    assertEquals(toValue(double.class, "1.1E20"), 1.1E20D, 1E-10);
    assertEquals(toValue(double.class, "-1.1E-20"), -1.1E-20D, 1E-10);
    assertEquals(toValue(double.class, "-1.1E20"), -1.1E20D, 1E-10);
    assertEquals(toValue(double.class, String.valueOf(Double.MIN_VALUE)), Double.MIN_VALUE, 1E-10);
    assertEquals(toValue(double.class, String.valueOf(Double.MAX_VALUE)), Double.MAX_VALUE, 1E-10);
    assertNull(toValue(Double.class, NULL_LITERAL));
    assertEquals(toValue(Double.class, "0"), 0.0D, 1E-10);
    assertEquals(toValue(Double.class, "1"), 1.0D, 1E-10);
    assertEquals(toValue(Double.class, "-1"), -1.0D, 1E-10);
    assertEquals(toValue(Double.class, "1.1E-20"), 1.1E-20D, 1E-10);
    assertEquals(toValue(Double.class, "1.1E20"), 1.1E20D, 1E-10);
    assertEquals(toValue(Double.class, "-1.1E-20"), -1.1E-20D, 1E-10);
    assertEquals(toValue(Double.class, "-1.1E20"), -1.1E20D, 1E-10);
    assertEquals(toValue(Double.class, String.valueOf(Double.MIN_VALUE)), Double.MIN_VALUE, 1E-10);
    assertEquals(toValue(Double.class, String.valueOf(Double.MAX_VALUE)), Double.MAX_VALUE, 1E-10);
  }

  @Test
  public void booleanReconstruction() throws Exception {
    assertNull(toValue(boolean.class, NULL_LITERAL));
    assertEquals(toValue(boolean.class, "true"), true);
    assertEquals(toValue(boolean.class, "false"), false);
    assertNull(toValue(Boolean.class, NULL_LITERAL));
    assertEquals(toValue(Boolean.class, "true"), true);
    assertEquals(toValue(Boolean.class, "false"), false);
  }

  @Test
  public void dateReconstruction() throws Exception {
    assertNull(toValue(Date.class, NULL_LITERAL));
    assertEquals(toValue(Date.class, "01-Jan-2015 22:15:21.123"), DateLiteral.df.parse("01-JAN-2015 22:15:21.123"));
    assertEquals(toValue(Date.class, "01-Jan-2015 01:01:01.001"), DateLiteral.df.parse("01-JAN-2015 01:01:01.001"));
  }

  @Test
  public void enumReconstruction() throws Exception {
    assertNull(toValue(E.class, NULL_LITERAL));
    assertEquals(toValue(E.class, "a"), E.a);
    assertEquals(toValue(E.class, "b"), E.b);
    assertEquals(toValue(E.class, "c"), E.c);
  }

  @Test
  public void arrayReconstruction() throws Exception {
    int[] a = new int[]{1, 2, 3};
    String[][] b = new String[][]{new String[]{"a"}, {"b", "c"}, {"[,]", "[,]"}};
    E[] c = new E[]{E.a, E.a, E.b, E.c};
    int[] d = new int[0];
    int[][] e = new int[][]{{}, {}};
    int[][][] f = new int[][][]{{{1, 2, 3}, {4}}, {{7, 8}}};

    assertArrayEquals(toValue(a.getClass(), NULL_LITERAL), null);
    assertArrayEquals(toValue(classOf(a), "[1,2,3]"), a);
    assertArrayEquals(toValue(classOf(b), "[[a],[b,c],[\\[\\,\\],\\[\\,\\]]]"), b);
    assertArrayEquals(toValue(classOf(c), "[a,a,b,c]"), c);
    assertArrayEquals(toValue(classOf(d), "[]"), d);
    assertArrayEquals(toValue(classOf(e), "[[],[]]"), e);
    assertArrayEquals(toValue(classOf(f), "[[[1,2,3],[4]],[[7,8]]]"), f);
  }
}