/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.outboundservices.batch

import com.google.gson.Gson
import de.hybris.bootstrap.annotations.UnitTest
import de.hybris.platform.odata2services.odata.impl.ODataBatchParsingException
import de.hybris.platform.odata2services.odata.schema.entity.EntitySetNameGenerator
import de.hybris.platform.outboundservices.batch.DefaultOutboundBatchPartRequestConsolidator
import de.hybris.platform.outboundservices.facade.OutboundBatchRequestPartDTO
import de.hybris.platform.testframework.JUnitPlatformSpecification
import org.junit.Test
import org.springframework.core.convert.converter.Converter
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.ResponseEntity

import static de.hybris.platform.outboundservices.facade.OutboundBatchRequestPartDTO.OutboundBatchRequestPartDTOBuilder

@UnitTest
class DefaultOutboundBatchPartRequestConsolidatorUnitTest extends JUnitPlatformSpecification {
    final def CONTENT_TYPE_HEADER = 'Content-Type'
    final def APPLICATION_JSON = 'application/json'
    final def TEST_BODY = ['body': "This is a test body."]
    final def TEST_CHANGE_ID = "123qkx321"
    final def TEST_BODY2 = ['body': "This is another test body."]
    final def POST_METHOD = HttpMethod.valueOf("POST")
    final def TEST_ENTITY_TYPE = "Product"
    final def BOUNDARY = "batch"

    @Test
    def "an exception is thrown when the consolidator is instantiated with null #parameterName"() {
        when:
        new DefaultOutboundBatchPartRequestConsolidator(nameGenerator, converter)

        then:
        thrown(IllegalArgumentException)

        where:
        nameGenerator                | converter       | parameterName
        null                         | Stub(Converter) | 'EntitySetNameGenerator'
        Stub(EntitySetNameGenerator) | null | 'HttpHeaderToMapConverter'
    }

    @Test
    def "consolidator can consolidate individual requests"() {
        given:
        def requestDTO = setupOutboundBatchRequestPartDTO()
        def stringBody = new Gson().toJson(TEST_BODY)
        def consolidator = new DefaultOutboundBatchPartRequestConsolidator(entitySetNameGenerator(TEST_ENTITY_TYPE), httpHeaderToMapConverter(CONTENT_TYPE_HEADER, APPLICATION_JSON))

        when:
        def consolidatedRequestBody = consolidator.consolidate([requestDTO], BOUNDARY)

        then:
        consolidatedRequestBody.contains("--${BOUNDARY}")
        consolidatedRequestBody.contains("${POST_METHOD.name()} ${TEST_ENTITY_TYPE}s HTTP/1.1")
        consolidatedRequestBody.contains("${CONTENT_TYPE_HEADER}: ${APPLICATION_JSON}")
        consolidatedRequestBody.contains("Content-Id: ${TEST_CHANGE_ID}")
        consolidatedRequestBody.contains(stringBody)
        consolidatedRequestBody.contains("--${BOUNDARY}--")
    }

    @Test
    def "consolidator can consolidate multiple individual requests"() {
        given:
        def stringBody = new Gson().toJson(TEST_BODY)
        def stringBody2 = new Gson().toJson(TEST_BODY2)
        def changeId = UUID.randomUUID().toString()

        def requestDTO = setupOutboundBatchRequestPartDTO()
        def requestDTO2 = setupOutboundBatchRequestPartDTO(CONTENT_TYPE_HEADER, APPLICATION_JSON, TEST_ENTITY_TYPE, POST_METHOD, TEST_BODY2, changeId)

        def consolidator = new DefaultOutboundBatchPartRequestConsolidator(entitySetNameGenerator(TEST_ENTITY_TYPE), httpHeaderToMapConverter(CONTENT_TYPE_HEADER, APPLICATION_JSON))

        when:
        def consolidatedRequestBody = consolidator.consolidate([requestDTO, requestDTO2], BOUNDARY)

        then:
        consolidatedRequestBody.contains("--${BOUNDARY}")
        consolidatedRequestBody.contains("${POST_METHOD.name()} ${TEST_ENTITY_TYPE}s HTTP/1.1")
        consolidatedRequestBody.contains("${CONTENT_TYPE_HEADER}: ${APPLICATION_JSON}")
        consolidatedRequestBody.contains(stringBody)
        consolidatedRequestBody.contains(stringBody2)
        consolidatedRequestBody.contains("Content-Id: ${TEST_CHANGE_ID}")
        consolidatedRequestBody.contains("Content-Id: ${changeId}")
        consolidatedRequestBody.contains("--${BOUNDARY}--")
    }

    @Test
    def "ODataBatchParsingException is thrown when converter returns null"() {
        given:
        def requestDTO = setupOutboundBatchRequestPartDTO()
        def converter = Stub(Converter) {
            convert(_) >> null
        }
        def consolidator = new DefaultOutboundBatchPartRequestConsolidator(entitySetNameGenerator("notMatters"), converter)

        when:
        consolidator.consolidate([requestDTO], "notMatters")

        then:
        thrown ODataBatchParsingException
    }

    HttpEntity httpEntity(final Map<String, Object> entityBody, final String key, final List<String> values) {
        Stub(ResponseEntity) {
            getBody() >> entityBody
            getHeaders() >> Stub(HttpHeaders) {
                keySet() >> Set.of(key)
                getOrEmpty(_) >> values
            }
        }
    }

    EntitySetNameGenerator entitySetNameGenerator(def name) {
        Stub(EntitySetNameGenerator) {
            generate(name) >> name + "s"
        }
    }

    Converter httpHeaderToMapConverter(def key_, def values) {
        Stub(Converter) {
            convert(_) >> Map.of(key_, values)
        }
    }

    OutboundBatchRequestPartDTO setupOutboundBatchRequestPartDTO(def headerKey = CONTENT_TYPE_HEADER,
                                                                 def headerValue = APPLICATION_JSON,
                                                                 def entityType = TEST_ENTITY_TYPE,
                                                                 def method = POST_METHOD,
                                                                 def entityBody = TEST_BODY,
                                                                 def changeId = TEST_CHANGE_ID) {
        OutboundBatchRequestPartDTOBuilder
                .outboundBatchRequestPartDTO()
                .withHttpEntity(httpEntity(entityBody, headerKey, [headerValue]))
                .withItemType(entityType)
                .withRequestType(method)
                .withChangeId(changeId)
                .build()
    }

}
