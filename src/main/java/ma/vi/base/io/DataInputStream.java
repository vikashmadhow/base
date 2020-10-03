/*
 * Copyright (c) 2018 Vikash Madhow
 */

package ma.vi.base.io;

import java.io.DataInput;
import java.io.IOException;
import java.io.InputStream;

/**
 * Wraps a {@link DataInput} in an {@link InputStream}.
 *
 * @author vikash.madhow@gmail.com
 */
public class DataInputStream extends InputStream {
  public DataInputStream(DataInput input) {
    this.input = input;
  }

  @Override
  public int read() throws IOException {
    return input.readUnsignedByte();
  }

  /**
   * The underlying input.
   */
  private final DataInput input;
}