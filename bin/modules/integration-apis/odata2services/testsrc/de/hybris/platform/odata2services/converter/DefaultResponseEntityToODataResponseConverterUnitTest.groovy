/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.odata2services.converter

import de.hybris.bootstrap.annotations.UnitTest
import de.hybris.platform.testframework.JUnitPlatformSpecification
import org.junit.Test
import org.springframework.core.convert.converter.Converter
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity

@UnitTest
class DefaultResponseEntityToODataResponseConverterUnitTest extends JUnitPlatformSpecification {
    def converter = new DefaultResponseEntityToODataResponseConverter(multiValueMapToMapConverter("", ""))

    @Test
    def "cannot be instantiated with null header converter"() {
        when:
        new DefaultResponseEntityToODataResponseConverter(null)

        then:
        thrown(IllegalArgumentException)
    }

    @Test
    def "converter can converts ResponseEntity."() {
        given:
        def response = responseEntityStub(statusCode as int, "", "", [])

        expect:
        converter.convert(response).getStatus().getStatusCode() == statusCode as int

        where:
        statusCode << [201, 404]
    }

    @Test
    def "converter converts ResponseEntity with the same responseBody"() {
        given:
        def body = "This is for test"
        def response = responseEntityStub(201, body, "", [])

        expect:
        converter.convert(response).getEntity() == body
    }

    @Test
    def "converter converts header #values to #valuesInOdataResponse in ODataResponse"() {
        given:
        def key = "Content-Type"
        def converter = new DefaultResponseEntityToODataResponseConverter(multiValueMapToMapConverter(key, consolidatedValue))
        def response = responseEntityStub(201, "", key, values)

        expect:
        converter.convert(response).getContentHeader() == valuesInOdataResponse

        where:
        values                                           | consolidatedValue                           | valuesInOdataResponse
        []                                               | ""                                          | ""
        ["text/plain"]                                   | "text/plain"                                | "text/plain"
        ["text/plain", "text/plain"]                     | "text/plain"                                | "text/plain"
        ["text/plain", ""]                               | "text/plain"                                | "text/plain"
        ["text/plain", "multipart/mixed; boundary=test"] | "text/plain,multipart/mixed; boundary=test" | "text/plain,multipart/mixed; boundary=test"
    }

    ResponseEntity responseEntityStub(final int statusCode, final String responseBody, final String key, final List<String> values) {
        Stub(ResponseEntity) {
            getBody() >> responseBody
            getStatusCode() >> HttpStatus.valueOf(statusCode)
            getHeaders() >> Stub(HttpHeaders) {
                keySet() >> Set.of(key)
                getOrEmpty(_) >> values
            }
        }
    }

    Converter multiValueMapToMapConverter(final String key_, final String values) {
        Stub(Converter) {
            convert(_) >> Map.of(key_, values)
        }
    }
}
