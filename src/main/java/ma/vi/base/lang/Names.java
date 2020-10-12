/*
 * Copyright (c) 2016 Vikash Madhow
 */

package ma.vi.base.lang;

import java.io.File;
import java.io.IOException;
import java.util.Random;
import java.util.Set;
import java.util.regex.Pattern;

import static java.lang.Character.isLetter;
import static java.lang.Integer.max;
import static java.lang.System.Logger.Level.WARNING;
import static ma.vi.base.string.Strings.ALPHABETIC;
import static ma.vi.base.string.Strings.random;

/**
 * Utilities for working with names.
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class Names {
  public static String uniqueSequentialName(String name, Set<String> takenNames, int sequence) {
    if (name == null) {
      name = random(8, ALPHABETIC);
    }
    if (!takenNames.contains(name)) {
      return name;
    } else {
      int len = name.length();
      StringBuilder uniqueName = new StringBuilder(name);
      do {
        uniqueName.delete(len, uniqueName.length()).append(sequence);
        sequence++;
      } while (takenNames.contains(uniqueName.toString()));
      return uniqueName.toString();
    }
  }

  public static String uniqueSequentialName(String name, Set<String> takenNames) {
    return uniqueSequentialName(name, takenNames, 2);
  }

  public static String uniqueRandomName(String name, Set<String> takenNames) {
    if (name == null) {
      name = random(8, ALPHABETIC);
    }
    if (!takenNames.contains(name)) {
      return name;
    } else {
      int len = name.length();
      int range = takenNames.size();
      StringBuilder uniqueName = new StringBuilder(name);
      do {
        uniqueName.delete(len, uniqueName.length()).append(random.nextInt(max(range, 1000)));
      } while (takenNames.contains(uniqueName.toString()));
      return uniqueName.toString();
    }
  }

  /**
   * Check if the name is a valid xml element name. A valid xml element name:
   * <ul>
   * <li>must start with a letter or underscore;</li>
   * <li>cannot start with the letters xml (or XML, or Xml, etc)</li>
   * <li>Element names can contain letters, digits, hyphens, underscores, and periods.</li>
   * <li>Element names cannot contain spaces.</li>
   * </ul>
   */
  public static boolean isValidXmlName(String name) {
    return name != null
        && name.length() > 0
        && (isLetter(name.charAt(0)) || name.charAt(0) != '_')
        && (name.length() < 3 || !name.substring(0, 3).toLowerCase().equals("xml"))
        && (!invalidXmlElementNameChar.matcher(name).find());
  }

  /**
   * Returns a unique filename in the temporary system folder pointed to by the 'java.io.tmpdir'
   * system property. The filename will consist of 8 random alphanumeric characters only.
   *
   * @see #uniqueFileName(File, String, String, int, char[])
   */
  public static String uniqueFileName() throws IOException {
    return uniqueFileName(null);
  }

  /**
   * Returns a unique filename in the specified folder. The filename will consist of
   * 8 random alphanumeric characters only.
   *
   * @see #uniqueFileName(File, String, String, int, char[])
   */
  public static String uniqueFileName(File folder) throws IOException {
    return uniqueFileName(folder, null, null);
  }

  /**
   * returns a unique filename in the specified folder with the supplied prefix and suffix.
   * The filename will consist of 20 alphanumeric characters only in addition to the prefix and
   * suffix
   *
   * @see #uniqueFileName(File, String, String, int, char[])
   */
  public static String uniqueFileName(File folder, String prefix, String suffix) throws IOException {
    return uniqueFileName(folder, prefix, suffix, 20);
  }

  /**
   * returns a unique filename in the specified folder with the supplied prefix and suffix.
   * The filename will consist of length alphanumeric characters only in addition to the
   * supplied prefix and suffix.
   *
   * @see #uniqueFileName(File, String, String, int, char[])
   */
  public static String uniqueFileName(File folder, String prefix, String suffix, int length) throws IOException {
    return uniqueFileName(folder, prefix, suffix, length, null);
  }

  /**
   * <p>
   * Returns a unique filename in the specified folder with the supplied prefix and suffix.
   * The filename will consist of length characters chosen randomly from the supplied character
   * array in addition to the prefix and suffix.
   * </p>
   * <p><b><i>This method will iterate until it can generate a filename that does not exist
   * in the specified folder. If length is too low and the supplied character array has
   * too few characters, the method may require several iterations to generate a unique
   * filename or might even iterate indefinitely.</i></b></p>
   */
  public static String uniqueFileName(File folder, String prefix, String suffix, int length, char[] chars) throws IOException {
    if (folder == null) {
      folder = new File(System.getProperty("java.io.tmpdir"));
    }
    if (!folder.isDirectory()) {
      throw new IOException(folder + " does not denote a directory");
    }
    if (prefix == null) {
      prefix = "";
    }
    if (suffix == null) {
      suffix = "";
    }

    // generate filename and test for existence.
    // stop when one which does not exist in the specified folder is found.
    int iteration = 0;
    String filename;
    do {
      if (iteration == 1) {
        log.log(WARNING, "More than one iteration required. Uou can increase " +
            "the required filename length, provide more characters or a " +
            "target folder with less files.");
      }
      filename = prefix + random(length, chars) + suffix;
      iteration++;
    }
    while (new File(folder, filename).exists());
    return filename;
  }

  /**
   * Returns the extension of the name or the empty if none. Returns null if
   * supplied name is null. The extension is the last part of the name occurring
   * after a period ('.').
   */
  public static String extension(String name) {
    if (name == null) {
      return null;
    }
    int pos = name.lastIndexOf(".");
    return pos == -1 ? "" : name.substring(pos + 1);
  }

  /**
   * Random number generator
   */
  private static final Random random = new Random();

  /**
   * An xml element name can contain a (unicode) letter, digits, hyphens, underscores and periods.
   * This pattern matches any character outside these classes and can thus be used to find invalid
   * element names.
   */
  private static final Pattern invalidXmlElementNameChar = Pattern.compile("[^\\p{L}\\d\\-_.]");

//  /**
//   * Matches [a-zA-Z0-9_]
//   */
//  private static final CharMatcher dotOrDollar = CharMatcher.anyOf(".$");
//  private static final CharMatcher letterDigitUnderscore = javaLetterOrDigit().or(is('_'));
//  private static final CharMatcher notLetterDigitUnderscore = letterDigitUnderscore.negate();

  /**
   * logger for this class
   */
  private static final System.Logger log = System.getLogger(Names.class.getName());
}