/*
 * Copyright (c) 2016 Vikash Madhow
 */

package ma.vi.base.xml;

import ma.vi.base.collections.Maps;
import ma.vi.base.tuple.T2;
import org.junit.jupiter.api.Test;

import java.io.StringReader;
import java.util.Arrays;
import java.util.List;
import java.util.stream.StreamSupport;

import static java.util.Collections.emptyMap;
import static java.util.Spliterator.ORDERED;
import static java.util.Spliterators.spliteratorUnknownSize;
import static java.util.stream.Collectors.toList;
import static ma.vi.base.xml.Fragment.Type.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class XmlReaderTest {

  final String xml1 = "<a a='b' c='d'>test</a>";

  final String xml2 =
      "<a a='b' c='d'>\n" +
          "test\n <!-- A comment --> suffix\n" +
          "  <b>\n" +
          "    <!-- Another comment -->\n" +
          "    <c>c</c>\n" +
          "    <d>d</d>\n" +
          "    <e>   </e>\n" +
          "  </b>\n" +
          "</a>";

  final String charts =
      "<?xml version=\"1.0\" encoding=\"windows-1252\"?>\n" +
          "\n" +
          "<!--\n" +
          "    Document   : charts.xml\n" +
          "    Created on : 02 February 2011, 15:46\n" +
          "    Author     : Vikash Madhow\n" +
          "    Description: Definition of charts in the system.\n" +
          "-->\n" +
          "<!-- PPIA -->\n" +
          "<obj_ref_charts>\n" +
          "\t<chart type='test' att='lsd'>\n" +
          "\t\t<name>Price Trend</name>\n" +
          "\t\t<fields>\n" +
          "\t\t\t<chartField>\n" +
          "\t\t\t\t<sequence>84412</sequence>\n" +
          "\t\t\t\t<expression>concat(monthlyItemSpecPrice.monthYear.year, \", \", label(monthlyItemSpecPrice.monthYear.month))</expression>\n" +
          "\t\t\t\t<title>Monthly item spec price</title>\n" +
          "\t\t\t\t<useAsCategory>true</useAsCategory>\n" +
          "\t\t\t</chartField>\n" +
          "\t\t\t<chartField>\n" +
          "\t\t\t\t<sequence>84413</sequence>\n" +
          "\t\t\t\t<member>price</member>\n" +
          "\t\t\t\t<expression>price</expression>\n" +
          "\t\t\t\t<title>Price</title>\n" +
          "\t\t\t\t<aggregateOperation>5</aggregateOperation>\n" +
          "\t\t\t\t<useAsCategory>false</useAsCategory>\n" +
          "\t\t\t</chartField>\n" +
          "\t\t</fields>\n" +
          "\t\t<defaultTitle>Price Trend</defaultTitle>\n" +
          "\t\t<columnsPerPage>1</columnsPerPage>\n" +
          "\t\t<entity><entityClass>mu.gov.cso.domain.priceindex.ppia.ItemSpecPrice</entityClass></entity>\n" +
          "\t\t<category>\n" +
          "\t\t\t<name>Item Specification</name>\n" +
          "\t\t\t<department><name>P&amp;P&gt;IA\t<![CDATA[te<>st]]></name></department>\n" +
          "\t\t</category>\n" +
          "\t\t<operation>2</operation>\n" +
          "\t</chart>\n" +
          "</obj_ref_charts>";

  @Test
  public void textXml1() {
    List<Fragment> elements = StreamSupport.stream(spliteratorUnknownSize(new XmlReader(new StringReader(xml1)), ORDERED), false)
                                           .collect(toList());
    assertEquals(elements, Arrays.asList(
        new Fragment(T_START_ELEMENT, "a", Maps.of(T2.of("a", "b"), T2.of("c", "d"))),
        new Fragment(T_TEXT, "test", emptyMap()),
        new Fragment(T_END_ELEMENT, "a", emptyMap()),
        new Fragment(T_END_DOCUMENT, null, emptyMap())
    ));
  }

  @Test
  public void textXml1WithRewind() {
    XmlReader in = new XmlReader(new StringReader(xml1), 4);

    assertTrue(in.hasNext());
    Fragment start = new Fragment(T_START_ELEMENT, "a", Maps.of(T2.of("a", "b"), T2.of("c", "d")));
    assertEquals(in.next(), start);
    assertEquals(in.previous(), start);

    assertTrue(in.hasNext());
    Fragment text = new Fragment(T_TEXT, "test", emptyMap());
    assertEquals(in.next(), text);
    assertEquals(in.previous(), text);
    assertEquals(in.previous(2), start);

    assertTrue(in.hasNext());
    Fragment end = new Fragment(T_END_ELEMENT, "a", emptyMap());
    assertEquals(in.next(), end);
    assertEquals(in.previous(), end);
    assertEquals(in.previous(2), text);
    assertEquals(in.previous(3), start);

    assertTrue(in.hasNext());
    Fragment endDoc = new Fragment(T_END_DOCUMENT, null, emptyMap());
    assertEquals(in.next(), endDoc);
    assertEquals(in.previous(), endDoc);
    assertEquals(in.previous(2), end);
    assertEquals(in.previous(3), text);
    assertEquals(in.previous(4), start);

    assertFalse(in.hasNext());
    in.rewind(1);
    assertTrue(in.hasNext());
    assertEquals(in.next(), endDoc);
    assertFalse(in.hasNext());

    in.rewind(2);
    assertTrue(in.hasNext());
    assertEquals(in.next(), end);
    assertEquals(in.next(), endDoc);
    assertFalse(in.hasNext());

    in.rewind(3);
    assertTrue(in.hasNext());
    assertEquals(in.next(), text);
    assertEquals(in.next(), end);
    assertEquals(in.next(), endDoc);
    assertFalse(in.hasNext());

    in.rewind(4);
    assertTrue(in.hasNext());
    assertEquals(in.next(), start);
    assertEquals(in.next(), text);
    assertEquals(in.next(), end);
    assertEquals(in.next(), endDoc);
    assertFalse(in.hasNext());
  }

  @Test
  public void textXml2() {
        /*
        "<a a='b' c='d'>\n" +
        "test\n <!-- A comment --> suffix\n" +
        "  <b>\n" +
        "    <c>c</c>\n" +
        "    <d>d</d>\n" +
        "    <e>   </e>\n" +
        "  </b>\n" +
        "</a>";
       */
    List<Fragment> elements = StreamSupport.stream(spliteratorUnknownSize(new XmlReader(new StringReader(xml2)), ORDERED), false)
                                           .collect(toList());
    assertEquals(elements, Arrays.asList(
        new Fragment(T_START_ELEMENT, "a", Maps.of(T2.of("a", "b"), T2.of("c", "d"))),
        new Fragment(T_TEXT, "\ntest\n  suffix\n  ", emptyMap()),
        new Fragment(T_START_ELEMENT, "b", emptyMap()),
        new Fragment(T_START_ELEMENT, "c", emptyMap()),
        new Fragment(T_TEXT, "c", emptyMap()),
        new Fragment(T_END_ELEMENT, "c", emptyMap()),
        new Fragment(T_START_ELEMENT, "d", emptyMap()),
        new Fragment(T_TEXT, "d", emptyMap()),
        new Fragment(T_END_ELEMENT, "d", emptyMap()),
        new Fragment(T_START_ELEMENT, "e", emptyMap()),
        new Fragment(T_TEXT, "   ", emptyMap()),
        new Fragment(T_END_ELEMENT, "e", emptyMap()),
        new Fragment(T_END_ELEMENT, "b", emptyMap()),
        new Fragment(T_END_ELEMENT, "a", emptyMap()),
        new Fragment(T_END_DOCUMENT, null, emptyMap())
    ));
  }

  @Test
  public void textXml2InclComments() {
        /*
        "<a a='b' c='d'>\n" +
        "test\n <!-- A comment --> suffix\n" +
        "  <b>\n" +
        "    <!-- Another comment -->\n" +
        "    <c>c</c>\n" +
        "    <d>d</d>\n" +
        "    <e>   </e>\n" +
        "  </b>\n" +
        "</a>";
       */

    List<Fragment> elements = StreamSupport.stream(spliteratorUnknownSize(
                                  XmlReaderBuilder.newBuilder(new StringReader(xml2))
                                                  .coalesceText(false)
                                                  .discardComments(false)
                                                  .build(), ORDERED), false)
                                           .collect(toList());
    assertEquals(elements, Arrays.asList(
        new Fragment(T_START_ELEMENT, "a", Maps.of(T2.of("a", "b"), T2.of("c", "d"))),
        new Fragment(T_TEXT, "\ntest\n ", emptyMap()),
        new Fragment(T_COMMENT, " A comment ", emptyMap()),
        new Fragment(T_TEXT, " suffix\n  ", emptyMap()),
        new Fragment(T_START_ELEMENT, "b", emptyMap()),
        new Fragment(T_COMMENT, " Another comment ", emptyMap()),
        new Fragment(T_START_ELEMENT, "c", emptyMap()),
        new Fragment(T_TEXT, "c", emptyMap()),
        new Fragment(T_END_ELEMENT, "c", emptyMap()),
        new Fragment(T_START_ELEMENT, "d", emptyMap()),
        new Fragment(T_TEXT, "d", emptyMap()),
        new Fragment(T_END_ELEMENT, "d", emptyMap()),
        new Fragment(T_START_ELEMENT, "e", emptyMap()),
        new Fragment(T_TEXT, "   ", emptyMap()),
        new Fragment(T_END_ELEMENT, "e", emptyMap()),
        new Fragment(T_END_ELEMENT, "b", emptyMap()),
        new Fragment(T_END_ELEMENT, "a", emptyMap()),
        new Fragment(T_END_DOCUMENT, null, emptyMap())
    ));
  }

  @Test
  public void testCharts() {
    for (Fragment de: XmlReaderBuilder.newBuilder(new StringReader(charts)).build()) {
      System.out.println(de);
    }
  }
}