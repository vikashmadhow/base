/*
 * Copyright (c) 2018 Vikash Madhow
 */

package ma.vi.base.config;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class ConfigurationTest {
  public static Configuration testPostgresConfig() {
    return new Configuration(Thread.currentThread().getContextClassLoader().getResource("app_postgres.yml").getPath(), false);
  }

  public static Configuration testMssqlConfig() {
    return new Configuration(Thread.currentThread().getContextClassLoader().getResource("app_mssql.yml").getPath(), false);
  }

  @Test
  public void load() {
    Configuration c = testPostgresConfig();
    assertEquals(8080, (Integer)c.get("server.port"));
    assertEquals("postgres", c.get("database.sys.name"));
    assertEquals("postgres", c.get("database.sys.password"));
    assertEquals("cso_data_dev", c.get("database.name"));
    assertEquals("127.0.0.1", c.get("database.host"));
    assertEquals(5432, (Integer)c.get("database.port"));
    assertEquals(Arrays.asList("-Xmx2048M", "-server", "-XX:-UseGCOverheadLimit"), c.get("jvm.parameters"));
  }

  @Test
  public void emptyConfigIsReadOnly() {
    Configuration c = Configuration.EMPTY;
    assertThrows(UnsupportedOperationException.class, () -> c.put("a", "b"));
  }

  @Test
  public void readOnlyNonEmptyConfig() {
    Configuration c = Configuration.of("a", "1", "b", "2");
    assertEquals("1", c.get("a"));
    assertEquals("2", c.get("b"));
    assertThrows(UnsupportedOperationException.class, () -> c.put("a", "b"));
  }

  @Test
  public void writableConfig() {
    Configuration c = new Configuration(new HashMap<>());
    c.put("a", "1");
    c.put("b", "2");
    assertEquals("1", c.get("a"));
    assertEquals("2", c.get("b"));
  }

  @Test
  public void configExtension() {
    Configuration c = new Configuration(new HashMap<>());
    c.put("a", "1");
    c.put("b", "2");
    Configuration d = c.extend(Map.of("c", "3", "d", "4"));
    assertEquals("1", d.get("a"));
    assertEquals("2", d.get("b"));
    assertEquals("3", d.get("c"));
    assertEquals("4", d.get("d"));

    assertEquals("1", c.get("a"));
    assertEquals("2", c.get("b"));
    assertNull(c.get("c"));
    assertNull(c.get("d"));
  }
}