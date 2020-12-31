/*
 * Copyright (c) 2016 Vikash Madhow
 */

package ma.vi.base.string;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.Character.*;

/**
 * Utility functions working on strings.
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class Strings {

//    /** A character-based splitter which should be faster than the regex-based String.split method. */
//    private static class CharacterSplit implements Iterator<String> {
//        private CharacterSplit(String text, char splitter) {
//            this.text = text;
//            this.splitter = splitter;
//        }
//
//        @Override public boolean hasNext() {
//            return currentPos < text.length();
//        }
//
//        @Override public String next() {
//            checkState(currentPos < text.length(), "No more tokens");
//            currentPos++;
//            int nextPos = text.indexOf(currentPos, splitter);
//            if (nextPos == -1) {
//                String token = text.substring(currentPos);
//                currentPos = text.length();
//                return token;
//
//            } else {
//                String token = text.substring(currentPos, nextPos);
//                currentPos = nextPos;
//                return token;
//            }
//        }
//
//        private final String text;
//        private final char splitter;
//        private int currentPos = -1;
//    }

  public static String shorten(String text) {
    return shorten(text, 20, "...");
  }

  public static String shorten(String text, int len, String suffix) {
    if (text.length() < len) {
      return text;
    } else {
      return text.substring(0, len) + suffix;
    }
  }

  /**
   * Return true if string conforms to this regular expression:
   * <p>
   * HEX{8}-HEX{4}-HEX{4}-HEX{4}-HEX{12}
   * <p>
   * where HEX is [A-Fa-f0-9]
   * <p>
   * I.e., it conforms to the hexadecimal representation of a UUID.
   */
  public static boolean isUUID(String text) {
    if (text != null) {
      int len = text.length();
      if (len == UUID_SEP.length) {
        for (int i = 0; i < len; i++) {
          char c = text.charAt(i);
          if (UUID_SEP[i] == 1) {
            if (c != '-') {
              return false;
            }
          } else if (!(c >= '0' && c <= '9')
              && !(c >= 'A' && c <= 'F')
              && !(c >= 'a' && c <= 'f')) {
            return false;
          }
        }
        return true;
      }
    }
    return false;
  }

  /**
   * Position of separators (1) in a UUID
   */
  private static final byte[] UUID_SEP = new byte[]{
      0, 0, 0, 0, 0, 0, 0, 0,
      1,
      0, 0, 0, 0,
      1,
      0, 0, 0, 0,
      1,
      0, 0, 0, 0,
      1,
      0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0
  };

  /**
   * Cleans the string by removing unnecessary white spaces, leading and
   * trailing non-alphanumeric characters and by fully lower case except
   * for the first character which is converted to upper case.
   */
  public static String clean(String value) {
    if (value == null) {
      return null;
    } else {
      return capFirst(trimPunctuations(collapseWhiteSpace(value)).toLowerCase());
    }
  }

  /**
   * Remove leading and trailing whitespaces and reduce all consecutive
   * whitespaces to one.
   */
  public static String collapseWhiteSpace(String value) {
    if (value == null) {
      return null;
    }
    // reduce consecutive whitespaces to one
    char last = ' ';
    StringBuilder s = new StringBuilder();
    for (char c : value.toCharArray()) {
      if (isWhitespace(c) && isWhitespace(last)) {
        continue;
      }
      s.append(c);
      last = c;
    }
    return s.toString().trim();
  }

  /**
   * Returns true if a string containing only lower-case letters
   * or non-alphabetic characters.
   */
  public static boolean isLower(String s) {
    if (s == null) {
      return true;
    }
    for (char c : s.toCharArray()) {
      if (isLetter(c) && !Character.isLowerCase(c)) {
        return false;
      }
    }
    return true;
  }

  /**
   * Returns true if a string containing only upper-case letters
   * or non-alphabetic characters.
   */
  public static boolean isUpper(String s) {
    return !isLower(s);
  }

  /**
   * Strip the string of anything but letters.
   */
  public static String lettersOnly(String s) {
    if (s == null) {
      return null;
    }
    StringBuilder lettersOnly = new StringBuilder();
    for (char c : s.toCharArray()) {
      if (isLetter(c)) {
        lettersOnly.append(c);
      }
    }
    return lettersOnly.toString();
  }

  /**
   * Remove all leading non-alphanumeric characters
   */
  public static String ltrimPunctuations(String value) {
    if (value == null) {
      return null;
    }

    int i = 0;
    for (; i < value.length(); i++) {
      char c = value.charAt(i);
      if (isLetter(c) || isDigit(c)) {
        break;
      }
    }
    return value.substring(i);
  }

  /**
   * Remove all trailing non-alphanumeric characters
   */
  public static String rtrimPunctuations(String value) {
    if (value == null) {
      return null;
    }

    int i = value.length() - 1;
    for (; i >= 0; i--) {
      char c = value.charAt(i);
      if (isLetter(c) || isDigit(c)) {
        break;
      }
    }
    return value.substring(0, i + 1);
  }

  /**
   * Remove all leading and trailing non-alphanumeric characters
   */
  public static String trimPunctuations(String value) {
    return ltrimPunctuations(rtrimPunctuations(value));
  }

  /**
   * Replace all characters with accents in value with their equivalent
   * characters with the accent removed.
   */
  public static String removeDiacritics(String value) {
    if (value == null) {
      return null;
    }

    StringBuilder buf = new StringBuilder();
    value = Normalizer.normalize(value, Normalizer.Form.NFD);
    for (char c : value.toCharArray()) {
      if (getType(c) != NON_SPACING_MARK) {
        buf.append(c);
      }
    }
    return buf.toString();
  }

  /**
   * Remove from the string any continuous sequence which does not match
   * the supplied regular expression.
   *
   * @param text            The text to preserve substrings from.
   * @param preservePattern Only sequences in text matching this pattern will
   *                        be preserved in the result.
   * @return The processed text.
   */
  public static String preserve(String text, Pattern preservePattern) {
    if (text == null || preservePattern == null) {
      return null;
    }

    StringBuilder result = new StringBuilder();
    Matcher matcher = preservePattern.matcher(text);
    while (matcher.find()) {
      result.append(text.substring(matcher.start(), matcher.end()));
    }
    return result.toString();
  }

  /**
   * Creates a label by expanding a camel case identifier.
   */
  public static String expandByCase(String text) {
    return String.join(" ", splitByCase(text));
  }

  /**
   * Splits a string by case boundaries. E.g., AnotherCasePhrase is split
   * to ['Another', 'Case', 'Phrase'].
   */
  public static List<String> splitByCase(String text) {
    if (text == null) {
      throw new IllegalArgumentException("Can't split null");
    }
    List<String> result = new ArrayList<>();
    Matcher matcher = CASE_SPLITTER.matcher(text);
    while (matcher.find()) {
      result.add(text.substring(matcher.start(), matcher.end()));
    }
    return result;
  }

  /**
   * Capitalize first character only.
   */
  public static String capFirst(String text) {
    if (text == null) {
      return null;
    }
    return switch (text.length()) {
      case 0 -> text;
      case 1 -> text.toUpperCase();
      default -> Character.toUpperCase(text.charAt(0)) + text.substring(1);
    };
  }

  /**
   * De-capitalize first character only.
   */
  public static String uncapFirst(String text) {
    if (text == null) {
      return null;
    }
    return switch (text.length()) {
      case 0 -> text;
      case 1 -> text.toLowerCase();
      default -> Character.toLowerCase(text.charAt(0)) + text.substring(1);
    };
  }

  /**
   * Returns a string of length characters chosen randomly from the supplied
   * character array.
   */
  public static String random(int length, char[] chars) {
    if (length <= 0) {
      length = 8;
    }
    if (chars == null || chars.length == 0) {
      chars = ALPHA_NUMERIC;
    }
    StringBuilder str = new StringBuilder();
    for (int i = 0; i < length; i++) {
      str.append(chars[random.nextInt(chars.length)]);
    }
    return str.toString();
  }

  public static String random() {
    return random(0, null);
  }

  public static String random(int length) {
    return random(length, null);
  }

//    public static String makeUnique(Set<String> existing, String prefix) {
//        String p = prefix;
//        while (existing.contains(p)) {
//            p = prefix + '_' + random(4);
//        }
//        existing.add(p);
//        return p;
//    }

  /**
   * Change a name by adding a random substring until it is unique
   * within the set of names provided. If the name has an optional
   * suffix following a period (i.e. an extension), the random string
   * is added before the extension. If the name is not present in
   * the set of names initially, it is returned unchanged. The unique
   * unique name is added to the set before returning.
   *
   * @param names Set of existing names.
   * @param name  Name to make unique.
   * @return A unique name as per the set of names.
   */
  public static String makeUnique(Set<String> names, String name, boolean addToNames) {
    int pos = name.lastIndexOf('.');
    String previousExt = pos == -1 ? "" : name.substring(pos);
    String previousName = pos == -1 ? name : name.substring(0, pos);
    while (names.contains(name)) {
      name = previousName + "_" + Strings.random(4) + previousExt;
    }
    if (addToNames) {
      names.add(name);
    }
    return name;
  }

  public static String makeUnique(Set<String> names, String name) {
    return makeUnique(names, name, true);
  }

  private Strings() {
  }

  /**
   * alpha-numeric characters; useful to generate random alpha-numeric strings
   */
  public static final char[] ALPHA_NUMERIC = new char[]{
      '0', '1', '2', '3', '4', '5', '6', '7', '8',
      '9', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h',
      'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q',
      'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'};

  /**
   * numeric characters; useful to generate random numeric strings
   */
  public static final char[] NUMERIC = new char[]{
      '0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};

  /**
   * alphabetic characters; useful to generate random alphabetic strings
   */
  public static final char[] ALPHABETIC = new char[]{
      'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h',
      'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q',
      'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'};

  /**
   * Random number generator
   */
  private static final Random random = new Random();

  /**
   * Regular expression to split camel case identifier into separate words.
   */
  public static final Pattern CASE_SPLITTER = Pattern.compile("[a-z]+|[A-Z][a-z]+|[A-Z]+|[0-9]+");
}