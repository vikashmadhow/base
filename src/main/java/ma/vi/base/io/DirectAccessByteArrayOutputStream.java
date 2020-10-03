/*
 * Copyright (c) 2018 Vikash Madhow
 */

package ma.vi.base.io;

import java.io.ByteArrayOutputStream;

/**
 * Same as java.io.ByteArrayOutputStream but provides public access to the underlying byte array. This is useful when
 * you do not want a copy of underlying byte buffer made (which toByteArray() method does).
 *
 * @author vikash.madhow@gmail.com
 */
public class DirectAccessByteArrayOutputStream extends ByteArrayOutputStream {
  public byte[] getBuffer() {
    return buf;
  }

  public int getCount() {
    return count;
  }
}
