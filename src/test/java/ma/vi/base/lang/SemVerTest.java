/*
 * Copyright (c) 2016 Vikash Madhow
 */

package ma.vi.base.lang;


import org.junit.jupiter.api.Test;

import static ma.vi.base.lang.SemVer.valid;
import static ma.vi.base.lang.SemVer.version;
import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class SemVerTest {
  @Test
  public void parsing() {
    assertEquals(new SemVer(1, 2, 3), version("1.2.3"));
    assertEquals(new SemVer(1, 2, 3, "test"), version("1.2.3-test"));
    assertEquals(new SemVer(1, 2, 3, "rc1.1"), version("1.2.3-rc1.1"));
    assertEquals(new SemVer(1, 2, 3, "beta-xyz-1.1"), version("1.2.3-beta-xyz-1.1"));
  }

  @Test
  public void validity() {
    assertTrue(valid("1.2.3"));
    assertTrue(valid("0.0.0"));
    assertTrue(valid("0.0.0-123"));
    assertTrue(valid("0.0.0-beta1"));
    assertTrue(valid("0.0.0-beta-xyz.12"));
    assertTrue(valid("100.101.100002-beta-xyz.12"));

    assertFalse(valid("100.101.100002-"));
    assertFalse(valid("100.-101.100002-"));
    assertFalse(valid("0.-0.0"));
    assertFalse(valid("-0.-0.0"));
    assertFalse(valid("-0.-0.-0"));
    assertFalse(valid("0.0-.0"));
    assertFalse(valid("0-.0-.0"));
  }

  @Test
  public void comparison() {
    assertTrue(version("1.2.3").compareTo(version("1.2.2")) > 0);
    assertTrue(version("1.3.2").compareTo(version("1.2.2")) > 0);
    assertTrue(version("2.2.3").compareTo(version("1.2.3")) > 0);
    assertTrue(version("10.0.0").compareTo(version("9.100000.10000000")) > 0);
    assertTrue(version("1.3.3").compareTo(version("1.2.10")) > 0);
    assertEquals(0, version("1.3.3").compareTo(version("1.3.3")));
    assertTrue(version("1.3.3").compareTo(version("1.3.3-beta")) > 0);
    assertTrue(version("0.0.0-b.2").compareTo(version("0.0.0-b.1")) > 0);
  }

  @Test
  public void increment() {
    assertEquals(version("1.2.3").incMajor(), version("2.2.3"));
    assertEquals(version("1.2.3").incMinor(), version("1.3.3"));
    assertEquals(version("1.2.3").incPatch(), version("1.2.4"));
    assertEquals(version("1.2.3-x").incMajor(), version("2.2.3-x"));
    assertEquals(version("1.2.3-x").incMinor(), version("1.3.3-x"));
    assertEquals(version("1.2.3-x").incPatch(), version("1.2.4-x"));
  }

  @Test
  public void string() {
    assertEquals("2.2.3", version("1.2.3").incMajor().toString());
    assertEquals("1.3.3", version("1.2.3").incMinor().toString());
    assertEquals("1.2.4", version("1.2.3").incPatch().toString());
    assertEquals("2.2.3-x", version("1.2.3-x").incMajor().toString());
    assertEquals("1.3.3-x", version("1.2.3-x").incMinor().toString());
    assertEquals("1.2.4-x", version("1.2.3-x").incPatch().toString());
  }
}