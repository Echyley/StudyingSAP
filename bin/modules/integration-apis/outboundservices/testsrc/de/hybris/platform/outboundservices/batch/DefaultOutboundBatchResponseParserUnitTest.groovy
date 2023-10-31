package de.hybris.platform.outboundservices.batch

import com.google.gson.Gson
import de.hybris.bootstrap.annotations.UnitTest
import de.hybris.platform.odata2services.odata.impl.ODataBatchParsingException
import de.hybris.platform.outboundservices.batch.DefaultOutboundBatchResponseParser
import de.hybris.platform.outboundservices.util.BatchResponseBodyBuilder.SingleResponseBuilder
import de.hybris.platform.testframework.JUnitPlatformSpecification
import org.apache.olingo.odata2.api.processor.ODataResponse
import org.junit.Test
import org.springframework.core.convert.converter.Converter
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity

import static de.hybris.platform.outboundservices.util.BatchResponseBodyBuilder.batchPartResponseBuilder
import static de.hybris.platform.outboundservices.util.BatchResponseBodyBuilder.batchResponseBuilder
import static de.hybris.platform.outboundservices.util.BatchResponseBodyBuilder.changeSetBuilder
import static de.hybris.platform.outboundservices.util.BatchResponseBodyBuilder.singleResponseBuilder

@UnitTest
class DefaultOutboundBatchResponseParserUnitTest extends JUnitPlatformSpecification {

    @Test
    def "null converter fails the precondition check"(){
        when:
        new DefaultOutboundBatchResponseParser(null)

        then:
        def e = thrown IllegalArgumentException
        e.message.contains "ResponseEntity To ODataResponse converter can't be null."
    }

    @Test
    def "parser parses a ResponseEntity with #condition and returns #result"() {
        given:
        def responseEntity = responseEntity(subResponses)
        def batchResponseParser = new DefaultOutboundBatchResponseParser(Stub(Converter) {
            convert(_) >> Stub(ODataResponse) {
                getContentHeader() >> responseEntity.getHeaders().get("Content-Type").join(',')
                getEntityAsStream() >> new ByteArrayInputStream(responseEntity.getBody().getBytes())
            }
        })

        when:
        def parsedResponseEntities = batchResponseParser.parseMultiPartResponse(responseEntity)

        then:
        parsedResponseEntities.size() == size
        parsedResponseEntities.stream().map(response -> response.getStatusCode().value()).toList() == parsedResponsesStatusCode


        where:
        subResponses                                                                                                           | size | parsedResponsesStatusCode | condition                    | result
        [[singleResponseBuilder(201)]]                                                                                         | 1    | [201]                     | '1 changeset and 1 request'  | '1 ResponseEntity'
        [[singleResponseBuilder(201), singleResponseBuilder(202)]]                                                             | 2    | [201, 202]                | '1 changeset and 2 requests' | '2 ResponseEntities'
        [[singleResponseBuilder(201), singleResponseBuilder(202)], [singleResponseBuilder(202), singleResponseBuilder(204)]]   | 4    | [201, 202, 202, 204]      | '2 changeset and 4 requests' | '4 ResponseEntities'
        [[singleResponseBuilder(201), singleResponseBuilder(204)], [singleResponseBuilder(202)], [singleResponseBuilder(204)]] | 4    | [201, 204, 202, 204]      | '3 changeset and 4 requests' | '4 ResponseEntities'
    }

    @Test
    def "The parsed responseEntity contains the headers of the single responses in the batch response body"() {
        given:
        def bodyOfIndividualRequest = requestBodyFromJson(["notMatters": ""])
        def contentId = 'testContentId'
        def responseEntity = responseEntity(singleResponseBuilder(201, contentId, bodyOfIndividualRequest))
        def batchResponseParser = new DefaultOutboundBatchResponseParser(Stub(Converter) {
            convert(_) >> Stub(ODataResponse) {
                getContentHeader() >> responseEntity.getHeaders().get("Content-Type").join(',')
                getEntityAsStream() >> new ByteArrayInputStream(responseEntity.getBody().getBytes())
            }
        })

        when:
        def responseEntityParsed = batchResponseParser.parseMultiPartResponse(responseEntity)

        then:
        responseEntityParsed.get(0).getHeaders().get("Content-ID") == [contentId]
    }

    @Test
    def "The parsed responseEntity contains the body of the single responses in the batch response body"() {
        given:
        def message = ["testMessage": "The Accept header of batchPart request is json."]
        def bodyOfIndividualRequest = requestBodyFromJson(message)
        def responseEntity = responseEntity(singleResponseBuilder(201, bodyOfIndividualRequest))
        def batchResponseParser = new DefaultOutboundBatchResponseParser(Stub(Converter) {
            convert(_) >> Stub(ODataResponse) {
                getContentHeader() >> responseEntity.getHeaders().get("Content-Type").join(',')
                getEntityAsStream() >> new ByteArrayInputStream(responseEntity.getBody().getBytes())
            }
        })

        when:
        def responseEntityParsed = batchResponseParser.parseMultiPartResponse(responseEntity)

        then:
        responseEntityParsed.get(0).getBody() == message
    }

    @Test
    def "The parser throws an ODataBatchParsingException when a batchPart response's body is not json"() {
        given:
        def bodyOfBatchPartResponse = "A simple string."
        def responseEntity = responseEntity(singleResponseBuilder(201, bodyOfBatchPartResponse))
        def batchResponseParser = new DefaultOutboundBatchResponseParser(Stub(Converter) {
            convert(_) >> null
        })

        when:
        batchResponseParser.parseMultiPartResponse(responseEntity)

        then:
        thrown ODataBatchParsingException
    }

    @Test
    def "The parser throws an ODataBatchParsingException when the input ResponseEntity fails to be converted"() {
        given:
        def responseEntity = responseEntity([[singleResponseBuilder(201)]], "batch", "batch", "image/gif")
        def batchResponseParser = new DefaultOutboundBatchResponseParser(Stub(Converter) {
            convert(_) >> null
        })

        when:
        batchResponseParser.parseMultiPartResponse(responseEntity)

        then:
        thrown ODataBatchParsingException
    }

    @Test
    def "The parser throws an ODataBatchParsingException when the contentType is not multipart/mixed"() {
        given:
        def responseEntity = responseEntity([[singleResponseBuilder(201)]], "batch", "batch", "image/gif")
        def batchResponseParser = new DefaultOutboundBatchResponseParser(Stub(Converter) {
            convert(_) >> Stub(ODataResponse) {
                getContentHeader() >> responseEntity.getHeaders().get("Content-Type").join(',')
                getEntityAsStream() >> new ByteArrayInputStream(responseEntity.getBody().getBytes())
            }
        })

        when:
        batchResponseParser.parseMultiPartResponse(responseEntity)

        then:
        thrown ODataBatchParsingException
    }

    @Test
    def "The parser throws an ODataBatchParsingException when the boundaries in header and body not matching"() {
        given:
        def boundaryInBody = 'batch'
        def boundaryInHeader = 'test'
        def responseEntity = responseEntity([[singleResponseBuilder(201)]], boundaryInHeader, boundaryInBody)
        def batchResponseParser = new DefaultOutboundBatchResponseParser(Stub(Converter) {
            convert(_) >> Stub(ODataResponse) {
                getContentHeader() >> responseEntity.getHeaders().get("Content-Type").join(',')
                getEntityAsStream() >> new ByteArrayInputStream(responseEntity.getBody().getBytes())
            }
        })

        when:
        batchResponseParser.parseMultiPartResponse(responseEntity)

        then:
        thrown ODataBatchParsingException
    }

    ResponseEntity<String> responseEntity(final List<List<SingleResponseBuilder>> singleResponseBuilders, final String boundaryInHeader = "batch",
                                          final String boundaryInBody = "batch", final String batchType = "multipart/mixed") {
        def batchResponseBodyBuilder = batchResponseBuilder().withBoundary(boundaryInBody)
        for (List<SingleResponseBuilder> batchPart : singleResponseBuilders) {
            def batchPartResponseBuilder = batchPartResponseBuilder()
            def changeSetBuilder = changeSetBuilder()
            for (SingleResponseBuilder responseBuilder : batchPart) {
                changeSetBuilder.withSingleResponse(responseBuilder)
            }
            batchResponseBodyBuilder.withBatchPart(batchPartResponseBuilder.withChangeSet(changeSetBuilder))
        }
        return responseEntityStub(batchType, boundaryInHeader, batchResponseBodyBuilder.build())
    }

    ResponseEntity<String> responseEntity(final SingleResponseBuilder singleResponseBuilder, final String boundaryInHeader = "batch",
                                          final String boundaryInBody = "batch", final String batchType = "multipart/mixed") {
        def batchBody = batchResponseBuilder().withBoundary(boundaryInBody)
                .withSingleResponse(singleResponseBuilder).build()
        return responseEntityStub(batchType, boundaryInHeader, batchBody)
    }

    ResponseEntity responseEntityStub(final String batchType, final String batchBoundary, final String batchResponseBody) {
        Stub(ResponseEntity) {
            getBody() >> batchResponseBody
            getStatusCode() >> HttpStatus.valueOf(202)
            getHeaders() >> Stub(HttpHeaders) {
                keySet() >> ["Content-Type"]
                get("Content-Type") >> ["${batchType}; boundary=${batchBoundary};charset=UTF-8" as String]
            }
        }
    }

    String requestBodyFromJson(Map jsonPayload){
       return new Gson().toJson(jsonPayload)
    }

}
