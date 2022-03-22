/*
 * Copyright (c) 2018 Vikash Madhow
 */

package ma.vi.base.config;

import ma.vi.base.crypt.Obfuscator;
import ma.vi.base.tuple.T2;
import org.yaml.snakeyaml.Yaml;

import java.io.FileReader;
import java.io.Reader;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static ma.vi.base.lang.Errors.unchecked;

/**
 * Simple YAML configuration reader.
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class Configuration {

  /**
   * Read configuration from a YAML file producing a map of string to objects.
   * For example, this config file:
   * <pre>
   * database:
   *   server:
   *     host: xyz
   *     port: 123
   *   user:
   *     name: avi
   *     pass: emi
   * </pre>
   *
   * will produce this map (without the quotes for strings):
   * <pre>
   * "database.server.host" to "xyz"
   * "database.server.port" to 123
   * "database.user.name" to "avi"
   * "database.user.pass" to "emi"
   * </pre>
   *
   * @param configFile The file containing the YAML configuration
   * @param unobfuscatePasswords Whether to unobfuscate passwords. If this is true
   *                             the value of all keys containing the substring
   *                             "password" or "secret" will be unobfuscated using
   *                             {@link Obfuscator#unobfuscate(String)}.
   */
  public Configuration(String configFile, boolean unobfuscatePasswords) {
    this(unchecked(() -> new FileReader(configFile)), unobfuscatePasswords);
  }

  /**
   * Similar to {@link #Configuration(String, boolean)} but reading the configuration
   * data from the supplied reader.
   */
  public Configuration(Reader in, boolean unobfuscatePasswords) {
    this.configuration = new HashMap<>();
    Yaml yaml = new Yaml();
    read(yaml.load(in), unobfuscatePasswords, "");
    unchecked(in::close);
  }

  /**
   * Creates a copy of the supplied configuration.
   */
  public Configuration(Configuration config) {
    this(new HashMap<>(config.configuration));
  }

  /**
   * Creates a configuration from the map.
   */
  public Configuration(Map<String, Object> config) {
    this.configuration = config;
  }

  /**
   * Create a configuration from the list of values.
   */
  @SafeVarargs
  public static Configuration of(T2<String, Object>... values) {
    Configuration c = new Configuration(new HashMap<>());
    for (T2<String, Object> value: values) {
      c.put(value.a(), value.b());
    }
    return c;
  }

  public static Configuration of(String k1, Object v1) {
    return new Configuration(Map.of(k1, v1));
  }

  public static Configuration of(String k1, Object v1,
                                 String k2, Object v2) {
    return new Configuration(Map.of(k1, v1,
                                    k2, v2));
  }

  public static Configuration of(String k1, Object v1,
                                 String k2, Object v2,
                                 String k3, Object v3) {
    return new Configuration(Map.of(k1, v1,
                                    k2, v2,
                                    k3, v3));
  }

  public static Configuration of(String k1, Object v1,
                                 String k2, Object v2,
                                 String k3, Object v3,
                                 String k4, Object v4) {
    return new Configuration(Map.of(k1, v1,
                                    k2, v2,
                                    k3, v3,
                                    k4, v4));
  }

  public static Configuration of(String k1, Object v1,
                                 String k2, Object v2,
                                 String k3, Object v3,
                                 String k4, Object v4,
                                 String k5, Object v5) {
    return new Configuration(Map.of(k1, v1,
                                    k2, v2,
                                    k3, v3,
                                    k4, v4,
                                    k5, v5));
  }

  public static Configuration of(String k1, Object v1,
                                 String k2, Object v2,
                                 String k3, Object v3,
                                 String k4, Object v4,
                                 String k5, Object v5,
                                 String k6, Object v6) {
    return new Configuration(Map.of(k1, v1,
                                    k2, v2,
                                    k3, v3,
                                    k4, v4,
                                    k5, v5,
                                    k6, v6));
  }

  public static Configuration of(String k1, Object v1,
                                 String k2, Object v2,
                                 String k3, Object v3,
                                 String k4, Object v4,
                                 String k5, Object v5,
                                 String k6, Object v6,
                                 String k7, Object v7) {
    return new Configuration(Map.of(k1, v1,
                                    k2, v2,
                                    k3, v3,
                                    k4, v4,
                                    k5, v5,
                                    k6, v6,
                                    k7, v7));
  }

  public static Configuration of(String k1, Object v1,
                                 String k2, Object v2,
                                 String k3, Object v3,
                                 String k4, Object v4,
                                 String k5, Object v5,
                                 String k6, Object v6,
                                 String k7, Object v7,
                                 String k8, Object v8) {
    return new Configuration(Map.of(k1, v1,
                                    k2, v2,
                                    k3, v3,
                                    k4, v4,
                                    k5, v5,
                                    k6, v6,
                                    k7, v7,
                                    k8, v8));
  }

  public static Configuration of(String k1, Object v1,
                                 String k2, Object v2,
                                 String k3, Object v3,
                                 String k4, Object v4,
                                 String k5, Object v5,
                                 String k6, Object v6,
                                 String k7, Object v7,
                                 String k8, Object v8,
                                 String k9, Object v9) {
    return new Configuration(Map.of(k1, v1,
                                    k2, v2,
                                    k3, v3,
                                    k4, v4,
                                    k5, v5,
                                    k6, v6,
                                    k7, v7,
                                    k8, v8,
                                    k9, v9));
  }

  /**
   * Creates a new configuration from this configuration and adds all elements
   * from the supplied config, replacing values for any existing keys.
   */
  public Configuration extend(Map<String, Object> newConfig) {
    Configuration config = new Configuration(this);
    config.putAll(newConfig);
    return config;
  }

  public <X> X get(String key) {
    return (X)configuration.get(key);
  }

  public <X> X get(String key, X defaultValue) {
    if (configuration.containsKey(key)) {
      return (X)configuration.get(key);
    } else {
      return defaultValue;
    }
  }

  public boolean has(String key) {
    return configuration.containsKey(key);
  }

  public void put(String key, Object value) {
    configuration.put(key, value);
  }

  public void putAll(Map<String, Object> values) {
    configuration.putAll(values);
  }

  public void remove(String key) {
    configuration.remove(key);
  }

  public Map<String, Object> asMap() {
    return Collections.unmodifiableMap(configuration);
  }

  private void read(Map<String, Object> values,
                    boolean             unobfuscatePasswords,
                    String              prefix) {
    for (Map.Entry<String, Object> e: values.entrySet()) {
      String key = e.getKey();
      Object value = e.getValue();
      if (value instanceof Map) {
        read((Map<String, Object>)value, unobfuscatePasswords, prefix + key + '.');
      } else {
        if (unobfuscatePasswords && (key.toLowerCase().contains("password") || key.toLowerCase().contains("secret"))) {
          /*
           * Secrets and passwords are stored in obfuscated form: unobfuscate
           */
          if (value != null) {
            value = Obfuscator.unobfuscate(value.toString());
          }
        }
        this.configuration.put(prefix + key, value);
      }
    }
  }

  public static final Configuration EMPTY = new Configuration(Collections.emptyMap());

  private final Map<String, Object> configuration;
}