/*
 * Copyright (c) 2018 Vikash Madhow
 */

package ma.vi.base.xml;

import java.util.Map;

import static javax.xml.stream.XMLStreamConstants.*;

/**
 * An atomic part of an XML document as read by the {@link XmlReader}.
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class Fragment {
  /**
   * The type of fragment.
   */
  public final Type type;

  /**
   * The text for the fragment corresponding to its type. For instance for a
   * {@link Type#T_START_ELEMENT} this will be the element name.
   */
  public final String text;

  /**
   * The attributes attached to the fragment. This is only applicable to XML
   * start elements (type {@link Type#T_START_ELEMENT}).
   */
  public final Map<String, String> attributes;

  /**
   * The type of fragment.
   */
  public enum Type {
    T_START_DOCUMENT(START_DOCUMENT, "Start document"),
    T_END_DOCUMENT(END_DOCUMENT, "End document"),
    T_START_ELEMENT(START_ELEMENT, "Start element"),
    T_END_ELEMENT(END_ELEMENT, "End element"),
    T_PROCESSING_INSTRUCTION(PROCESSING_INSTRUCTION, "Processing instruction"),
    T_COMMENT(COMMENT, "Comment"),
    T_TEXT(CHARACTERS, "Text");

    public final int code;
    public final String name;

    Type(int code, String name) {
      this.code = code;
      this.name = name;
    }

    /**
     * Returns the type of fragment corresponding to the XML tag code defined in
     * {@link javax.xml.stream.XMLStreamConstants}.
     */
    public static Type of(int code) {
      return switch (code) {
        case START_DOCUMENT -> T_START_DOCUMENT;
        case END_DOCUMENT -> T_END_DOCUMENT;
        case START_ELEMENT -> T_START_ELEMENT;
        case END_ELEMENT -> T_END_ELEMENT;
        case PROCESSING_INSTRUCTION -> T_PROCESSING_INSTRUCTION;
        case COMMENT -> T_COMMENT;
        case CHARACTERS, SPACE, CDATA -> T_TEXT;
        default -> throw new IllegalArgumentException("Unknown or unsupported XML tag code: " + code);
      };
    }
  }

  public Fragment(int type, String text, Map<String, String> attributes) {
    this(Type.of(type), text, attributes);
  }

  public Fragment(Type type, String text, Map<String, String> attributes) {
    this.type = type;
    this.text = text;
    this.attributes = attributes;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    Fragment element = (Fragment) o;

    if (type != element.type) return false;
    if (text != null ? !text.equals(element.text) : element.text != null) return false;
    return attributes.equals(element.attributes);
  }

  @Override
  public int hashCode() {
    int result = type.hashCode();
    result = 31 * result + (text != null ? text.hashCode() : 0);
    result = 31 * result + attributes.hashCode();
    return result;
  }

  @Override
  public String toString() {
    return type.name + ": " + text + ' ' + attributes;
  }
}