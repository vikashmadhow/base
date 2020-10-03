/*
 * Copyright (c) 2018 Vikash Madhow
 */

package ma.vi.base.io;

import java.io.*;
import java.nio.charset.Charset;

/**
 * Useful input/output operations as static methods.
 *
 * @author vikash.madhow@gmail.com
 */
public class IO {
  /**
   * Transfer as much data as possible from in to out.
   *
   * @param in  Input stream to transfer from
   * @param out Output stream to transfer to
   * @return The number of bytes transfered
   * @throws IOException
   */
  public static int transfer(InputStream in, OutputStream out) throws IOException {
    int transfered = 0;
    int read = 0;
    byte[] buffer = new byte[1024];

    while ((read = in.read(buffer)) != -1) {
      out.write(buffer, 0, read);
      transfered += read;
    }
    return transfered;
  }

  /**
   * Transfer up to count bytes from in and write to out.
   */
  public static int transfer(InputStream in, OutputStream out, int count) throws IOException {
    return transfer(new BoundedInputStream(in, count), out);
  }

  /**
   * Transfer as much data as possible from in to out.
   *
   * @param in  Reader to transfer from
   * @param out Writer to transfer to
   * @return the number of characters transfered
   * @throws IOException
   */
  public static int transfer(Reader in, Writer out) throws IOException {
    int transfered = 0;
    int read = 0;
    char[] buffer = new char[1024];

    while ((read = in.read(buffer)) != -1) {
      out.write(buffer, 0, read);
      transfered += read;
    }
    return transfered;
  }

  /**
   * Transfer up to count characters from in and write to out.
   */
  public static int transfer(Reader in, Writer out, int count) throws IOException {
    return transfer(new BoundedReader(in, count), out);
  }

  /**
   * Copy content of in file to out file. Return number of bytes copied.
   */
  public static int transfer(File inFile, File outFile) throws IOException {
    InputStream in = null;
    OutputStream out = null;
    try {
      in = new FileInputStream(inFile);
      out = new FileOutputStream(outFile);
      return transfer(in, out);
    } finally {
      if (in != null) {
        in.close();
      }
      if (out != null) {
        out.close();
      }
    }
  }

  /**
   * Copy up to count bytes from in file to out file. Return number of bytes copied.
   */
  public static int transfer(File inFile, File outFile, int count) throws IOException {
    InputStream in = null;
    OutputStream out = null;
    try {
      in = new BoundedInputStream(new FileInputStream(inFile), count);
      out = new FileOutputStream(outFile);
      return transfer(in, out);
    } finally {
      if (in != null) {
        in.close();
      }
      if (out != null) {
        out.close();
      }
    }
  }

  /**
   * Read the content of the stream using the default charset and return it as a String
   */
  public static String readAllAsString(InputStream in) throws IOException {
    return readAllAsString(new InputStreamReader(in));
  }

  /**
   * Read the content of the stream using the specified charset and return it as a String
   */
  public static String readAllAsString(InputStream in, Charset cs) throws IOException {
    return readAllAsString(new InputStreamReader(in, cs));
  }

  /**
   * Read the content of the stream and return it as a byte array
   */
  public static byte[] readAll(InputStream in) throws IOException {
    int count;
    byte[] buffer = new byte[1024];
    ByteArrayOutputStream byteArray = new ByteArrayOutputStream();
    while ((count = in.read(buffer)) != -1) {
      byteArray.write(buffer, 0, count);
    }

    return byteArray.toByteArray();
  }

  /**
   * Read the content of the reader and return it as a String
   */
  public static String readAllAsString(Reader in) throws IOException {
    StringBuilder str = new StringBuilder();

    int count;
    char[] buffer = new char[1024];
    while ((count = in.read(buffer)) != -1) {
      str.append(buffer, 0, count);
    }

    return str.toString();
  }

  /**
   * Read the content of the reader and return it as a char[]
   */
  public static char[] readAll(Reader in) throws IOException {
    String str = readAllAsString(in);
    char chr[] = new char[str.length()];
    str.getChars(0, str.length(), chr, 0);
    return chr;
  }

  private IO() {
  }
}