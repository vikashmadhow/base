/*
 * Copyright (c) 2018 Vikash Madhow
 */

package ma.vi.base.io;

import java.io.DataOutput;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Wraps a {@link DataOutput} in an {@link OutputStream}.
 *
 * @author vikash.madhow@gmail.com
 */
public class DataOutputStream extends OutputStream {
  public DataOutputStream(DataOutput output) {
    this.output = output;
  }

  @Override
  public void write(int b) throws IOException {
    output.writeByte(b);
  }

  /**
   * The underlying input.
   */
  private final DataOutput output;
}