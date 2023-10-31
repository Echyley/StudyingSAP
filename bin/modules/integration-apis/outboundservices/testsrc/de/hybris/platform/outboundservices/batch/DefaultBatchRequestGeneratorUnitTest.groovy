/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.outboundservices.batch

import de.hybris.bootstrap.annotations.UnitTest
import de.hybris.platform.odata2services.odata.impl.ODataBatchParsingException
import de.hybris.platform.outboundservices.batch.DefaultBatchRequestGenerator
import de.hybris.platform.outboundservices.facade.OutboundBatchRequestPartDTO
import de.hybris.platform.outboundservices.service.OutboundMultiPartRequestConsolidator
import de.hybris.platform.testframework.JUnitPlatformSpecification
import org.apache.commons.lang3.tuple.Pair
import org.junit.Test
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod

import static de.hybris.platform.outboundservices.facade.OutboundBatchRequestPartDTO.OutboundBatchRequestPartDTOBuilder.outboundBatchRequestPartDTO

@UnitTest
class DefaultBatchRequestGeneratorUnitTest extends JUnitPlatformSpecification {

    private static final String ACCEPT = "Accept"
    private static final String APPLICATION_JSON = 'application/json'
    private static final String CONTENT_TYPE = 'Content-Type'
    private static final String CONTENT_TYPE_FOR_BATCH_PATTERN = "^multipart/mixed;boundary=.+"

    @Test
    def "constructor throws exception when multi part request consolidator is null "() {
        when:
        new DefaultBatchRequestGenerator(null)

        then:
        def e = thrown IllegalArgumentException
        e.message.contains("Multi-Part request consolidator must be provided")
    }

    @Test
    def "Generated batch request has the body that consolidator provides"() {
        given:
        def body = "mocked consolidated body"
        def generator = new DefaultBatchRequestGenerator(outboundMultiPartRequestConsolidator(body))

        when:
        def request = generator.generate([outboundBatchRequestPartDTO(httpHeaders([Pair.of("key", "notMatters")]))])

        then:
        request.getBody() == body
    }

    @Test
    def "Generated batch request has the same headers as batchPart request has except for Content-type"() {
        given:
        def generator = new DefaultBatchRequestGenerator(outboundMultiPartRequestConsolidator(''))
        def passportHeader = "SAP-PASSPORT"
        def passportValue = '2a0300e6'
        def partHeaders = httpHeaders([Pair.of(ACCEPT, APPLICATION_JSON), Pair.of(CONTENT_TYPE, APPLICATION_JSON),
                     Pair.of(passportHeader, passportValue)]);

        when:
        def request = generator.generate([outboundBatchRequestPartDTO(partHeaders)])

        then: 'Batch request has the same headers of batchPart request.'
        def batchHeaders = request.getHeaders()
        batchHeaders.getAccept().stream().map(media -> media.toString()).toList().join() == APPLICATION_JSON
        batchHeaders.get(passportHeader).get(0) == passportValue

        and: "The generator has the expected batch content-type"
        batchHeaders.getContentType().toString().matches(CONTENT_TYPE_FOR_BATCH_PATTERN)

        and: "The batch part headers have the provided header values"
        partHeaders.get(ACCEPT).get(0) == APPLICATION_JSON
        partHeaders.get(passportHeader).get(0) == passportValue
        partHeaders.get(CONTENT_TYPE).get(0) == APPLICATION_JSON
    }

    @Test
    def "constructor throws exception when given a empty list of DTO"() {
        when:
        new DefaultBatchRequestGenerator(outboundMultiPartRequestConsolidator('')).generate(Collections.emptyList())

        then:
        thrown ODataBatchParsingException
    }

    OutboundMultiPartRequestConsolidator outboundMultiPartRequestConsolidator(def batchBody) {
        Stub(OutboundMultiPartRequestConsolidator) {
            consolidate(_ as List<OutboundBatchRequestPartDTO>, _ as String) >> batchBody
        }
    }

    OutboundBatchRequestPartDTO outboundBatchRequestPartDTO(def headers) {
        outboundBatchRequestPartDTO()
                .withHttpEntity(new HttpEntity<Map<String, Object>>(headers))
                .withItemType("testType")
                .withRequestType(HttpMethod.POST)
                .withChangeId("7")
                .build()
    }

    HttpHeaders httpHeaders(final Collection<Pair<String, String>> keyValues) {
        def httpHeaders = new HttpHeaders();
        keyValues.stream().forEach(keyValue -> httpHeaders.set(keyValue.getKey(), keyValue.getValue()))
        return httpHeaders;
    }

}
