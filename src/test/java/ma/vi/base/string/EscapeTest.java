/*
 * Copyright (c) 2016 Vikash Madhow
 */

package ma.vi.base.string;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * Test for escaping strings.
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class EscapeTest {
  private Escape esc;

  @Before
  public void init() {
    String toEscape = "[,]\\";
    esc = new Escape(toEscape);
  }

  @Test
  public void escape() throws Exception {
    assertNull(esc.escape(null));
    assertEquals(esc.escape(""), "");
    assertEquals(esc.escape(" [ ] , \\ "), " \\[ \\] \\, \\\\ ");
  }

  @Test
  public void remap() throws Exception {
    assertNull(esc.map(null));
    assertEquals(esc.map(""), "");
    assertEquals(esc.map(" \\[ \\] \\, \\\\ "), " \ue000 \ue002 \ue001 \ue003 ");
    assertEquals(esc.map(" \\[ \\] \\, \\\\ \\"), " \ue000 \ue002 \ue001 \ue003 \\");
    assertEquals(esc.map(" \\[ \\] \\, \\\\ \\c"), " \ue000 \ue002 \ue001 \ue003 \\c");
  }

  @Test
  public void unescape() throws Exception {
    assertNull(esc.demap(null));
    assertEquals(esc.demap(""), "");
    assertEquals(esc.demap(" \ue000 \ue002 \ue001 \ue003 "), " [ ] , \\ ");
  }
}