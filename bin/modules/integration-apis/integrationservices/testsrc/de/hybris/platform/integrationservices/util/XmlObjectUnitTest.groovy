/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.integrationservices.util

import de.hybris.bootstrap.annotations.UnitTest
import de.hybris.platform.testframework.JUnitPlatformSpecification
import org.junit.Test

@UnitTest
class XmlObjectUnitTest extends JUnitPlatformSpecification {

    private static final def MALFORMED_CONTENT_ERROR = 'Received input stream is in invalid state or contains unparseable XML'

    @Test
    void 'can be created from a valid InputStream'() {
        given:
        def xml = '<content />'
        def stream = new ByteArrayInputStream(xml.bytes)

        expect:
        XmlObject.createFrom stream
    }

    @Test
    void 'can be created from closed InputStream'() {
        given:
        def xml = '<content />'
        def stream = new ByteArrayInputStream(xml.bytes)
        stream.close()

        expect:
        XmlObject.createFrom stream
    }

    @Test
    void "cannot be created from an #condition"() {
        given:
        def emptyStream = new ByteArrayInputStream(content)

        when:
        XmlObject.createFrom emptyStream

        then:
        def e = thrown IllegalArgumentException
        e.message == MALFORMED_CONTENT_ERROR

        where:
        content         | condition
        new byte[0]     | 'empty InputStream'
        'not XML'.bytes | 'InputStream containing a malformed XML'
    }

    @Test
    void 'can be created from a valid XML string'() {
        expect:
        XmlObject.createFrom '<valid />'
    }

    @Test
    void "cannot be create from a malformedXml: #content"() {
        when:
        XmlObject.createFrom content

        then:
        def e = thrown IllegalArgumentException
        e.message == MALFORMED_CONTENT_ERROR

        where:
        content << ['<malformed>', '', 'not an XML']
    }

    @Test
    void "can get value of an existing #condition"() {
        given:
        def xml = XmlObject.createFrom content

        expect:
        xml.get(path) == result

        where:
        content                          | path              | result       | condition
        "<object string='some value' />" | '/object/@string' | 'some value' | 'attribute'
        '<number>10</number>'            | '//number'        | '10'         | 'text node'
    }

    @Test
    void 'gets empty string when the path does not exist'() {
        given:
        def xml = XmlObject.createFrom '<empty />'

        expect:
        xml.get('/value').isEmpty()
    }

    @Test
    void 'get returns first occurrence when multiple matches exists'() {
        given:
        def xml = XmlObject.createFrom '''
                <object>
                    <value>1</value>
                    <value>2</value>
                </object>
                '''.stripIndent()

        expect:
        xml.get('//value') == '1'
    }

    @Test
    void 'get throws exception when path is not a valid XPath expression'() {
        given:
        def xml = XmlObject.createFrom '<object />'
        def notAnXPath = '>>'

        when:
        xml.get notAnXPath

        then:
        def e = thrown IllegalArgumentException
        e.message == "$notAnXPath is not a valid XPath expression"
    }

    @Test
    void "count retrieves the number of matching nodes for expression: #path"() {
        given:
        def xml = XmlObject.createFrom '''
				         <products>
				             <product code="1">
				                 <rating>*****</rating>
				                 <rating>****</rating>
				                 <rating>*****</rating>
				             </product>
				             <product code="2"/>
				         </products>
                        '''.stripIndent()

        expect:
        xml.count(path) == expected

        where:
        path                                   | expected
        '//products/product'                   | 2
        "//products/product[@code='1']/rating" | 3
        "//products/product[@code='2']/rating" | 0
        '//books/product'                      | 0
    }

    @Test
    void "transform returns result of transformation nodes matching #path"() {
        given:
        def xml = XmlObject.createFrom '''
                                                <products>
                                                    <product code="1">
                                                        <ratings>
                                                         <rating>5</rating>
                                                         <rating>5</rating>
                                                         <rating>4</rating>
                                                        </ratings>
                                                    </product>
                                                    <product code="2"/>
                                                </products>
                                                '''.stripIndent()

        expect:
        xml.transform(path, func) == expected

        where:
        path                                    | func               | expected
        '//product/@code'                       | { it.value }       | ['1', '2']
        "//product[@code = '1']/ratings/rating" | { it.textContent } | ['5', '5', '4']
    }

    @Test
    void 'transform returns an empty List when no nodes match the path'() {
        given:
        def xml = XmlObject.createFrom '<products/>'
        def nonMatchingPath = '/products/product'

        expect:
        xml.transform(nonMatchingPath, {it.textContent}).isEmpty()
    }
}
