/*
 * Copyright (c) 2018 Vikash Madhow
 */

package ma.vi.base.io;

import java.io.File;
import java.io.IOException;

import static ma.vi.base.string.Strings.random;

/**
 * File utilities.
 *
 * @author vikash.madhow@gmail.com
 */
public class Files {
  /**
   * Returns a unique filename in the temporary system folder pointed to by the 'java.io.tmpdir' system property. The
   * filename will consist of 8 random alphanumeric characters only.
   *
   * @see #getUniqueFileName(File, String, String, int, char[])
   */
  public static String getUniqueFileName() throws IOException {
    return getUniqueFileName(new File(System.getProperty("java.io.tmpdir")));
  }

  /**
   * Returns a unique filename in the specified folder. The filename will consist of 8 random alphanumeric characters
   * only.
   *
   * @see #getUniqueFileName(File, String, String, int, char[])
   */
  public static String getUniqueFileName(File folder) throws IOException {
    return getUniqueFileName(folder, null, null);
  }

  /**
   * Returns a unique filename in the specified folder with the supplied prefix and suffix. The filename will consist of
   * 20 alphanumeric characters only in addition to the prefix and suffix
   *
   * @see #getUniqueFileName(File, String, String, int, char[])
   */
  public static String getUniqueFileName(File folder, String prefix, String suffix) throws IOException {
    return getUniqueFileName(folder, prefix, suffix, 20);
  }

  /**
   * Returns a unique filename in the specified folder with the supplied prefix and suffix. The filename will consist of
   * length alphanumeric characters only in addition to the supplied prefix and suffix.
   *
   * @see #getUniqueFileName(File, String, String, int, char[])
   */
  public static String getUniqueFileName(File folder, String prefix, String suffix, int length) throws IOException {
    return getUniqueFileName(folder, prefix, suffix, length, null);
  }

  /**
   * <p>Returns a unique filename in the specified folder with the supplied prefix and suffix. The filename will
   * consist of length characters chosen randomly from the supplied character array in addition to the prefix and
   * suffix. </p> <p> <p><b><i>This method will iterate until it can generate a filename that does not exist in the
   * specified folder. If length is too low and the supplied character array has too few characters, the method may
   * require several iterations to generate a unique filename or might even iterate indefinitely. Therefore the length
   * should be a minimum of 8 and the chars array should contain a minimum of 10 characters.</i></b></p>
   */
  public static String getUniqueFileName(File folder, String prefix, String suffix, int length, char[] chars) throws IOException {
    if (folder == null) {
      throw new IOException("Folder must not be null");
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
        log.log(System.Logger.Level.WARNING,
            "More than one iteration required. Please increase " +
                "required filename length, provide more characters " +
                "or a target folder with relatively few files (<1000).");
      }

      filename = prefix + random(length, chars) + suffix;
      iteration++;
    }
    while (new File(folder, filename).exists());
    return filename;
  }

  /**
   * Returns the file extension or the empty if none. Returns null if supplied filename is null.
   */
  public static String getExtension(String filename) {
    if (filename == null) {
      return null;
    }

    int pos = filename.lastIndexOf(".");
    return pos == -1 ? "" : filename.substring(pos + 1);
  }

  /**
   * Produces a name which can be used to create a file by replacing
   * invalid characters in filename with valid defaults.
   */
  public static String normaliseFilename(String name) {
    return name.replace('\\', '-').replace('/', '-').replace(':', '-')
        .replace('"', '\'').replace(';', ',').replace('$', '-')
        .replace('%', '-');
  }

  private Files() {
  }

  /**
   * Logger for this class
   */
  private static System.Logger log = System.getLogger(Files.class.getName());
}