/*
 * Copyright (c) 2016 Vikash Madhow
 */

package ma.vi.base.string;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * <p>
 * Simple character escaping facility which can escape a set of characters in texts by
 * preceding them with a start of escape sequence character (default \). The set, specified
 * on construction, is applied using the {@link #escape(String)} method.
 * </p>
 *
 * <p>
 * The {@link #map(String)} method can then temporarily change the escaped characters
 * in a string to characters in the unicode private use area (e000-f8ff) so that its
 * processing is simplified.
 * </p>
 *
 * <p>
 * Finally, the {@link #demap(String)} method can be used to reconstruct the original
 * string (without backslash for escaped characters) from a remapped string.
 * </p>
 *
 * <p>
 * These three methods should thus be used together for processing string containing
 * characters which might conflict with surrounding characters when embedded in a
 * larger body of text (such as character data in XML).
 * </p>
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class Escape {
  public Escape(String escapeChars) {
    this('\\', escapeChars);
  }

  public Escape(char startOfEscape, String escapeChars) {
    checkArgument(escapeChars.length() > 0, "No characters were provided for escaping");
    this.startOfEscape = startOfEscape;
    this.escapeChars = escapeChars.toCharArray();
    ordinals = new HashMap<>();
    for (int i = 0; i < this.escapeChars.length; i++) {
      char c = this.escapeChars[i];
      checkArgument(!ordinals.containsKey(c), "Character '" + c + "' has been specified more than " +
          "once for escaping");
      ordinals.put(c, i);
    }
  }

  /**
   * Returns the text with any of the characters to escape
   * preceded with the start of escape character (default \).
   */
  public String escape(String text) {
    if (text == null) {
      return null;
    } else {
      StringBuilder escaped = new StringBuilder();
      for (int i = 0; i < text.length(); i++) {
        char c = text.charAt(i);
        if (ordinals.containsKey(c)) {
          escaped.append(startOfEscape);
        }
        escaped.append(c);
      }
      return escaped.toString();
    }
  }

  public String map(String text) {
    return map(text, null);
  }

  /**
   * This method can then be used to temporarily change the escaped characters in the
   * string to characters in the unicode private use area (e000-f8ff) so that processing
   * of the string would not encounter them and there is very little risk that those
   * remapped characters would conflict with existing ones in the string.
   */
  public String map(String text, String[] replacements) {
    if (text == null) {
      return null;
    } else {
      StringBuilder remapped = new StringBuilder();
      boolean inEscape = false;
      for (int i = 0; i < text.length(); i++) {
        char c = text.charAt(i);
        if (inEscape) {
          if (ordinals.containsKey(c)) {
            int pos = ordinals.get(c);
            if (replacements != null && replacements.length > pos) {
              remapped.append(replacements[pos]);
            } else {
              remapped.append((char) (PUA + pos));
            }
          } else {
            remapped.append(startOfEscape).append(c);
          }
          inEscape = false;
        } else if (c == startOfEscape) {
          inEscape = true;
        } else {
          remapped.append(c);
        }
      }
      if (inEscape) {
        remapped.append(startOfEscape);
      }
      return remapped.toString();
    }
  }

  /**
   * This method reconstructs the original string (without backslash for escaped
   * characters) from a remapped string.
   */
  public String demap(String text) {
    if (text == null) {
      return null;
    } else {
      StringBuilder unescaped = new StringBuilder();
      for (int i = 0; i < text.length(); i++) {
        char c = text.charAt(i);
        if (c >= PUA && c - PUA < escapeChars.length) {
          c = escapeChars[c - PUA];
        }
        unescaped.append(c);
      }
      return unescaped.toString();
    }
  }

  /**
   * Escape a string so that it can be embedded in a JSON object.
   */
  public static String escapeJsonString(String value) {
    return value.replace("\\", "\\\\") //.replace("'", "\\'")
        .replace("\"", "\\\"").replace("\n", "\\n")
        .replace("\r", "\\r").replace("\t", "\\t")
        .replace("\f", "\\f").replace("\b", "\\b");
  }

//    public static final Escape Json = new Escape('\\', "\\\"bfnrt");

  /**
   * Escapes a string so that it can be embedded in an SQL query.
   */
  public static String escapeSqlString(String value) {
    return value.replace("\\", "\\\\").replace("'", "''")
        .replace("\b", "\\b").replace("\f", "\\f")
        .replace("\n", "\\n").replace("\r", "\\r")
        .replace("\t", "\\t");
  }

  /**
   * Escapes single quotes in an ESQL query.
   */
  public static String escapeEsqlQuote(String value) {
    return value.replace("'", "%q");
  }

//    /**
//     * Unescape a string obtained from an SQL context to produce its actual value.
//     */
//    public static String unescapeSqlString(CharSequence value) {
//        String s = value.toString().replace("\\\\", "\\")
//                       .replace("''",  "'")
//                       .replace("\\b", "\b")
//                       .replace("\\f", "\f")
//                       .replace("\\n", "\n")
//                       .replace("\\r", "\r")
//                       .replace("\\t", "\t");
//        return insertUnicodeCodePoints(s);
//    }
//
//    /**
//     * Insert characters from unicode code points
//     */
//    public static String insertUnicodeCodePoints(String s) {
//        int lastPos = 0;
//        StringBuilder st = new StringBuilder();
//        Matcher matcher = UnicodeEscape.matcher(s);
//        while (matcher.find()) {
//            st.append(s, lastPos, matcher.start());
//            st.append(toChars(parseInt(matcher.group(1), 16)));
//            lastPos = matcher.end();
//        }
//        return st.append(s.substring(lastPos)).toString();
//    }
//
//    public static final Escape DOUBLE_QUOTE_ESC = new Escape('"');
//    public static final Escape SINGLE_QUOTE_ESC = new Escape('\'');
//
//    /**
//     * Pattern to match unicode escapes of the form \\\\uABCD.
//     */
//    public static final Pattern UnicodeEscape = Pattern.compile("\\\\u([0-9a-fA-F]{4})");

  /**
   * The character used to signal the start of an escape sequence.
   * Default is \
   */
  private final char startOfEscape;

  /**
   * The characters to escape, in the order that they were supplied
   * to this class on construction.
   */
  private final char[] escapeChars;

  /**
   * The character to escape are mapped to their position in the original
   * array supplied on construction and those positions are used as the
   * ordinal value of the character. These ordinals are added to the unicode
   * {@link #PUA} to the remapped code point for a character in a piece of
   * text when remapping.
   */
  private final Map<Character, Integer> ordinals;

  /**
   * The Unicode Private Use Area (PUA) is from E000 to F8FF. During processing
   * (before unescaping), the escaped characters can temporarily be remapped to
   * this area so as not to interfere with the general processing of the string.
   */
  private static final char PUA = '\uE000';

  public static final SimpleDateFormat SERVER_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
  public static final SimpleDateFormat SERVER_TIME_FORMAT = new SimpleDateFormat("HH:mm:ss.SSS");
  public static final SimpleDateFormat SERVER_DATE_TIME_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
}
