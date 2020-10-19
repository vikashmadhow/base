/*
 * Copyright (c) 2018 Vikash Madhow
 */

package ma.vi.base.xml;

import org.codehaus.stax2.XMLInputFactory2;
import org.codehaus.stax2.XMLStreamReader2;

import javax.xml.stream.XMLStreamException;
import java.io.Reader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.function.Predicate;

import static java.util.Collections.emptyMap;
import static javax.xml.stream.XMLStreamConstants.*;
import static ma.vi.base.lang.Errors.checkArgument;
import static ma.vi.base.lang.Errors.unchecked;
import static ma.vi.base.xml.Fragment.Type.*;

/**
 * An XML reader providing a simplified iterator interface to a streaming XML parser
 * with support for backtracking to an arbitrary number of steps, specified in the
 * constructor or, better, through {@link XmlReaderBuilder}.
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class XmlReader implements Iterable<Fragment>, Iterator<Fragment>, AutoCloseable {

  public XmlReader(Reader in) {
    this(in, 2);
  }

  public XmlReader(Reader in, int backtracking) {
    checkArgument(in != null, "Reader cannot be null");
    checkArgument(backtracking >= 2, "Backtracking length must be 2 or more");
    try {
      xml = (XMLStreamReader2) XMLInputFactory2.newInstance().createXMLStreamReader(in);
      buffer = new Fragment[backtracking];
    } catch (XMLStreamException e) {
      throw new RuntimeException("Could not open xml for stream reading", e);
    }
  }

  @Override
  public Iterator<Fragment> iterator() {
    return this;
  }

  /**
   * Returns true if there are more document elements to read.
   */
  @Override
  public boolean hasNext() {
    return unchecked(() -> readPos <= bufferPos || xml.hasNext(), "No more XML fragments");
  }

  /**
   * Returns the next document element.
   */
  @Override
  public Fragment next() {
    if (readPos > bufferPos) {
      Fragment de = _next();
      bufferPos++;
      readPos++;
      buffer[bufferPos % buffer.length] = de;
      return de;
    } else {
      return buffer[readPos++ % buffer.length];
    }
  }

  /**
   * Keeps calling {@link #next()} while the test remains true. Returns the
   * element read which made the test false or null if the end of the document
   * was reached before.
   */
  public Fragment nextWhile(Predicate<Fragment> test) {
    Fragment de = null;
    while (hasNext() && test.test(de = next())) ;
    return de == null || test.test(de) ? null : de;
  }

  /**
   * Rewind the buffer by the specified number of positions. That many elements must have
   * been read already and this number cannot be greater than the rewind capacity of the
   * reader.
   */
  public void rewind(int positions) {
    checkArgument(positions > 0 && positions <= buffer.length,
        "Number of positions to rewind must be greater than 0 and <= " + buffer.length);
    checkArgument(readPos - positions >= 0, "Reader cannot be rewound by " + positions +
        " elements as that many elements have not been read yet");
    readPos -= positions;
  }

  /**
   * Returns the element that was returned by the last call to next().
   */
  public Fragment previous() {
    return previous(1);
  }

  /**
   * Returns the element read previously by the number of positions specified. That is,
   * previous(0) is the element returned by last call to next(), previous(1) is the element
   * returned by the call to next() before that, and so on.
   */
  public Fragment previous(int positions) {
    checkArgument(positions > 0 && positions <= buffer.length,
        "Number of positions to look back at must be greater than 0 and <= " + buffer.length);
    checkArgument(readPos - positions >= 0, "Cannot look back " + positions + " positions as " +
        "that many elements have not been read yet");
    return buffer[(readPos - positions) % buffer.length];
  }

  public Fragment previousOrNull() {
    return previousOrNull(1);
  }

  public Fragment previousOrNull(int positions) {
    if (positions > 0 && positions <= buffer.length && readPos - positions >= 0) {
      return buffer[(readPos - positions) % buffer.length];
    }
    return null;
  }

  /**
   * Close the underlying stream parser; does not close the reader from which the
   * XML data was being read from.
   */
  @Override
  public void close() {
    unchecked(xml::close);
  }

  private Fragment _next() {
    if (lookAhead != null) {
      Fragment element = lookAhead;
      lookAhead = null;
      return element;

    } else try {
      int tag = xml.next();
      if (discardComments && tag == COMMENT) {
        return next();
      } else {
        return element(previousOrNull(), tag);
      }

    } catch (XMLStreamException e) {
      throw new RuntimeException(e);
    }
  }

  private Fragment element(Fragment before, int tag) throws XMLStreamException {
    switch (tag) {
      case END_ELEMENT:
        return new Fragment(T_END_ELEMENT, xml.getLocalName(), emptyMap());

      case START_ELEMENT:
        Map<String, String> attributes = new HashMap<>();
        for (int i = 0; i < xml.getAttributeCount(); i++) {
          attributes.put(xml.getAttributeLocalName(i), xml.getAttributeValue(i));
        }
        return new Fragment(T_START_ELEMENT, xml.getLocalName(), attributes);

      case CDATA:
      case CHARACTERS:
      case SPACE:
        Fragment de;
        if (coalesceText) {
          StringBuilder coalesced = new StringBuilder(xml.getText());
          int nextTag = xml.next();
          while (nextTag == CDATA || nextTag == CHARACTERS || nextTag == SPACE || nextTag == COMMENT) {
            if (nextTag != COMMENT) {
              coalesced.append(xml.getText());
            }
            nextTag = xml.next();
          }
          de = new Fragment(T_TEXT, coalesced.toString(), emptyMap());
          lookAhead = element(de, nextTag);

        } else {
          de = new Fragment(T_TEXT, xml.getText(), emptyMap());
          lookAhead = element(de, xml.next());
        }

        if (discardInterElementSpaces
            && (before == null
            || before.type != T_START_ELEMENT
            || lookAhead.type != T_END_ELEMENT)
//            && whitespace().matchesAllOf(de.text)) {
            && de.text.trim().length() == 0) {
          return _next();

        } else {
          return de;
        }

      case COMMENT:
        return new Fragment(T_COMMENT, xml.getText(), emptyMap());

      case PROCESSING_INSTRUCTION:
        return new Fragment(T_PROCESSING_INSTRUCTION, null,
            Map.of("PITarget", xml.getPITarget(), "PIData", xml.getPIData()));

      default:
        return new Fragment(tag, null, emptyMap());
    }
  }

  /**
   * Whether to discard space between elements if that is all there is between them.
   * This eliminates much redundant space such as those between an element and its
   * immediate child element. Spaces are still returned if there are also non-space
   * characters among the text.
   */
  boolean discardInterElementSpaces = true;

  /**
   * Whether to ignore comments or not; default is to ignore them. When comments are
   * ignored, they do not appear in the iteration stream.
   */
  boolean discardComments = true;

  /**
   * Whether to coalesce contiguous text or not; default is true. When coalescing text,
   * comments appearing in the middle of text are ignored.
   */
  boolean coalesceText = true;

  /**
   * The rewinding buffer hold elements to allow for backtracking.
   */
  private final Fragment[] buffer;

  /**
   * The position of last element read into the buffer.
   */
  private int bufferPos = -1;

  /**
   * The position where the next element will be read from the buffer.
   */
  private int readPos = 0;

  /**
   * A one-item look-ahead used in text coalescing.
   */
  private Fragment lookAhead;

  /**
   * The underlying streaming parser.
   */
  private final XMLStreamReader2 xml;
}