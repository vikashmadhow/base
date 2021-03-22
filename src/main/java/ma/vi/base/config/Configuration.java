/*
 * Copyright (c) 2018 Vikash Madhow
 */

package ma.vi.base.config;

import ma.vi.base.crypt.Obfuscator;
import ma.vi.base.tuple.T2;
import org.yaml.snakeyaml.Yaml;

import java.io.FileReader;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;

import static ma.vi.base.lang.Errors.unchecked;

/**
 * Simple YAML configuration reader.
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class Configuration extends HashMap<String, Object> {

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
    Yaml yaml = new Yaml();
    read(yaml.load(in), unobfuscatePasswords, "");
    unchecked(in::close);
  }

  /**
   * Creates a copy of the supplied configuration.
   */
  public Configuration(Configuration config) {
    putAll(config);
  }

  /**
   * Create a configuration from the list of values.
   */
  public static Configuration of(T2<String, Object>... values) {
    Configuration c = new Configuration();
    for (T2<String, Object> value: values) {
      c.put(value.a(), value.b());
    }
    return c;
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

  private void read(Map<String, Object> config, boolean unobfuscatePasswords, String prefix) {
    for(Map.Entry<String, Object> e: config.entrySet()) {
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
        put(prefix + key, value);
      }
    }
  }

  private Configuration() {}

  public static final Configuration EMPTY = new Configuration();
}