/*
 * Copyright (c) 2018 Vikash Madhow
 */

package ma.vi.base.config;

import org.junit.jupiter.api.Test;

import java.util.Arrays;

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
    assertEquals(8080, c.get("server.port"));
    assertEquals("postgres", c.get("database.sys.name"));
    assertEquals("postgres", c.get("database.sys.password"));
    assertEquals("cso_data_dev", c.get("database.name"));
    assertEquals("127.0.0.1", c.get("database.host"));
    assertEquals(5432, c.get("database.port"));
    assertEquals(Arrays.asList("-Xmx2048M", "-server", "-XX:-UseGCOverheadLimit"), c.get("jvm.parameters"));
  }
}