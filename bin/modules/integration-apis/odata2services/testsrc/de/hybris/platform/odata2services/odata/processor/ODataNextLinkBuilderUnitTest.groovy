/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.odata2services.odata.processor

import de.hybris.bootstrap.annotations.UnitTest
import de.hybris.platform.odata2services.odata.persistence.ItemLookupRequest
import de.hybris.platform.testframework.JUnitPlatformSpecification
import org.junit.Test

@UnitTest
class ODataNextLinkBuilderUnitTest extends JUnitPlatformSpecification {
    @Test
    def "Expected NextLink is returned when #msg "() {
        given:
        def builder = new ODataNextLinkBuilder()
        final String NEXT_LINK = 'https://anyhost/odata2webservices/anyio/anyentities?$top=2&$skiptoken=3'

        when:
        def nextLink = builder
                .withLookupRequest(stubItemLookupRequest(requestUrl))
                .withTotalCount(10) // any number larger than top and skip or skiptoken
                .build()

        then:
        nextLink == NEXT_LINK

        where:
        requestUrl                                                                | msg
        'https://anyhost/odata2webservices/anyio/anyentities?$skip=1&$top=2'      | "skip is the first query parameter"
        'https://anyhost/odata2webservices/anyio/anyentities?$top=2&$skip=1'      | "skip is not the first query parameter"
        'https://anyhost/odata2webservices/anyio/anyentities?$skiptoken=1&$top=2' | "skiptoken is the first query parameter"
        'https://anyhost/odata2webservices/anyio/anyentities?$top=2&$skiptoken=1' | "skiptoken is not the first query parameter"
    }

    private stubItemLookupRequest(final String reqUrl) {
        Stub(ItemLookupRequest) {
            getTop() >> 2
            getSkip() >> 1
            getRequestUri() >> URI.create(reqUrl)
        }
    }
}
