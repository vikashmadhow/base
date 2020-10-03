/*
 * Copyright (c) 2018 Vikash Madhow
 */

package ma.vi.base.crypt;

import java.security.MessageDigest;

import static java.lang.Integer.toHexString;
import static java.nio.charset.StandardCharsets.UTF_8;
import static ma.vi.base.lang.Errors.unchecked;

/**
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class Hashing {
  public static String sha256(String text) {
    try {
      MessageDigest md = MessageDigest.getInstance("SHA-256");
      byte[] hashInBytes = md.digest(text.getBytes(UTF_8));
      return (toHex(hashInBytes));
    } catch (Exception e) {
      throw unchecked(e);
    }
  }

  public static String toHex(byte[] bytes) {
    StringBuilder sb = new StringBuilder();
    for (byte b : bytes) {
      sb.append(toHexString(b & 0xff));
    }
    return sb.toString();
  }
}