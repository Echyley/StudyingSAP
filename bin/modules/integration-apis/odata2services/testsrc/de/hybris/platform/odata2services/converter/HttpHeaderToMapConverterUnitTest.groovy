/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.odata2services.converter

import de.hybris.bootstrap.annotations.UnitTest
import de.hybris.platform.testframework.JUnitPlatformSpecification
import org.junit.Test
import org.springframework.http.HttpHeaders

@UnitTest
class HttpHeaderToMapConverterUnitTest extends JUnitPlatformSpecification {

    def converter = new HttpHeaderToMapConverter()

    @Test
    def "converter converts HttpHeaders #values to #valuesInOdataResponse in ODataResponse"() {
        given:
        def key = "Content-Type"

        expect:
        converter.convert(httpHeaders(key, values)).get(key) == valuesInOdataResponse

        where:
        values                                           | valuesInOdataResponse
        []                                               | ""
        ["text/plain"]                                   | "text/plain"
        ["text/plain", "text/plain"]                     | "text/plain"
        ["text/plain", ""]                               | "text/plain"
        ["text/plain", "multipart/mixed; boundary=test"] | "text/plain,multipart/mixed; boundary=test"
    }

    HttpHeaders httpHeaders(final String key, final List<String> values) {
        def headers = new HttpHeaders()
        headers.addAll(key, values)
        headers
    }
}
