/*
 * Copyright (c) 2018 Vikash Madhow
 */

package ma.vi.base.xml;

import ma.vi.base.lang.Builder;

import java.io.Reader;

import static ma.vi.base.lang.Errors.checkArgument;

/**
 * A builder for {@link XmlReader} providing a simple method for
 * overriding the latter's defaults. Usage scenario:
 *
 * <pre>
 *  XmlReader xml =
 *      XmlReaderBuilder.newBuilder(new StringReader(xmlText))
 *          .discardInterElementSpaces(false)   // do not discard spaces between elements
 *          .discardComments(false)             // do not discard comments
 *          .rewindCapacity(10)                 // allow rewinding up to 10 positions back
 *          .build();
 *  </pre>
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class XmlReaderBuilder implements Builder<XmlReader> {
  /**
   * Creates a new builder.
   */
  public static XmlReaderBuilder newBuilder(Reader reader) {
    checkArgument(reader != null, "Reader cannot be null");
    XmlReaderBuilder builder = new XmlReaderBuilder();
    builder.reader = reader;
    return builder;
  }

  /**
   * Changes the rewind capacity of the reader; default is 2.
   */
  public XmlReaderBuilder rewindCapacity(int rewindCapacity) {
    checkArgument(rewindCapacity >= 2, "Rewind capacity must be 2 or more");
    this.rewindCapacity = rewindCapacity;
    return this;
  }

  /**
   * Changes whether the reader discard inter-element spaces; default is true.
   */
  public XmlReaderBuilder discardInterElementSpaces(boolean discardInterElementSpaces) {
    this.discardInterElementSpaces = discardInterElementSpaces;
    return this;
  }

  /**
   * Changes whether the reader discard comments; default is true.
   */
  public XmlReaderBuilder discardComments(boolean discardComments) {
    this.discardComments = discardComments;
    return this;
  }

  /**
   * Changed whether to coalesce contiguous text or not; default is true.
   */
  public XmlReaderBuilder coalesceText(boolean coalesceText) {
    this.coalesceText = coalesceText;
    return this;
  }

  @Override
  public XmlReader build() {
    XmlReader xml = new XmlReader(reader, rewindCapacity);
    xml.discardInterElementSpaces = discardInterElementSpaces;
    xml.discardComments = discardComments;
    xml.coalesceText = coalesceText;
    return xml;
  }

  private XmlReaderBuilder() {
  }

  /**
   * The reader from which the XmlReader will read its input.
   */
  private Reader reader = null;

  /**
   * The the rewind capacity of the reader; default is 2.
   */
  private int rewindCapacity = 2;

  /**
   * Whether the reader discard inter-element spaces; default is true.
   */
  private boolean discardInterElementSpaces = true;

  /**
   * Whether the reader discard comments; default is true.
   */
  private boolean discardComments = true;

  /**
   * Whether to coalesce contiguous text or not; default is true.
   */
  private boolean coalesceText = true;
}