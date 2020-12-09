/*
 * Copyright (c) 2018 Vikash Madhow
 */

package ma.vi.base.config;

import ma.vi.base.crypt.Obfuscator;
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
   * Package-protected constructor is used for testing.
   */
  public Configuration(String configFile, boolean unobfuscatePasswords) {
    this(unchecked(() -> new FileReader(configFile)), unobfuscatePasswords);
  }

  /**
   * Package-protected constructor is used for testing.
   */
  public Configuration(Reader in, boolean unobfuscatePasswords) {
    Yaml yaml = new Yaml();
    read(yaml.load(in), unobfuscatePasswords, "");
    unchecked(in::close);
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
}