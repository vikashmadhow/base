/**
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
module ma.vi.base {
  requires java.sql;
  requires jdk.unsupported;

  requires org.yaml.snakeyaml;
  requires org.codehaus.stax2;

  exports ma.vi.base.cache;
  exports ma.vi.base.circular;
  exports ma.vi.base.collections;
  exports ma.vi.base.config;
  exports ma.vi.base.crypt;
  exports ma.vi.base.io;
  exports ma.vi.base.lang;
  exports ma.vi.base.reflect;
  exports ma.vi.base.string;
  exports ma.vi.base.trie;
  exports ma.vi.base.tuple;
  exports ma.vi.base.unionfind;
  exports ma.vi.base.util;
  exports ma.vi.base.xml;
}