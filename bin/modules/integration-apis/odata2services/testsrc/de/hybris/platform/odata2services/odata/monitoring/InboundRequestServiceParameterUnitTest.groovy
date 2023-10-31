/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.odata2services.odata.monitoring

import de.hybris.bootstrap.annotations.UnitTest
import de.hybris.platform.inboundservices.model.InboundRequestMediaModel
import de.hybris.platform.integrationservices.enums.HttpMethod
import de.hybris.platform.testframework.JUnitPlatformSpecification
import org.junit.Test
import spock.lang.Shared
import spock.lang.Unroll

import static de.hybris.platform.odata2services.odata.monitoring.InboundRequestServiceParameter.inboundRequestServiceParameter

@UnitTest
class InboundRequestServiceParameterUnitTest extends JUnitPlatformSpecification {

    @Shared
    def parameter = inboundRequestServiceParameter().build()

    @Test
    @Unroll
    def "build parameter object when #condition"() {
        expect:
        with(param) {
            requests == []
            responses == []
            medias == []
            httpMethod == null
            userId == ""
            sapPassport == null
        }

        where:
        condition                | param
        "all fields not set"     | parameterFieldsNotSet()
        "all fields set to null" | parameterFieldsAllSetToNull()
    }

    @Test
    def "build parameter object with fields set"() {
        given:
        def requests = [Stub(RequestBatchEntity)]
        def responses = [Stub(ResponseChangeSetEntity)]
        def medias = [Stub(InboundRequestMediaModel)]
        def httpMethod = HttpMethod.POST
        def userId = "admin"
        def sapPassport = 'my-passport'

        when:
        def param = inboundRequestServiceParameter()
                .withRequests(requests)
                .withResponses(responses)
                .withMedias(medias)
                .withHttpMethod(httpMethod)
                .withUserId(userId)
                .withSapPassport(sapPassport)
                .build()

        then:
        with(param) {
            requests == requests
            responses == responses
            medias == medias
            httpMethod == httpMethod
            userId == userId
            sapPassport == sapPassport
        }
    }

    @Test
    @Unroll
    def "equal = #expected when #condition"() {
        expect:
        (parameter == anotherParam) == expected

        where:
        condition               | anotherParam                                                    | expected
        "same param object"     | parameter                                                       | true
        "same fields"           | inboundRequestServiceParameter().build()                        | true
        "different user"        | inboundRequestServiceParameter().withUserId("admin").build()    | false
        "different sapPassport" | inboundRequestServiceParameter().withSapPassport('abc').build() | false
        "different obj type"    | new Object()                                                    | false
        "other obj is null"     | null                                                            | false
    }

    @Test
    @Unroll
    def "hashcodes equal = #expected when #condition"() {
        expect:
        (parameter.hashCode() == anotherParam.hashCode()) == expected

        where:
        condition               | anotherParam                                                    | expected
        "same param object"     | parameter                                                       | true
        "same fields"           | inboundRequestServiceParameter().build()                        | true
        "different user"        | inboundRequestServiceParameter().withUserId("admin").build()    | false
        "different sapPassport" | inboundRequestServiceParameter().withSapPassport('abc').build() | false
        "different obj type"    | new Object()                                                    | false
    }

    @Test
    @Unroll
    def "#getterMethod returns immutable list"() {
        given:
        def param = inboundRequestServiceParameter()
                ."$setterMethod"([])
                .build()
        def returnedList = param."$getterMethod"()

        when:
        returnedList << addItem

        then:
        thrown UnsupportedOperationException

        where:
        getterMethod   | setterMethod    | addItem
        "getRequests"  | "withRequests"  | Stub(RequestBatchEntity)
        "getResponses" | "withResponses" | Stub(ResponseChangeSetEntity)
        "getMedias"    | "withMedias"    | Stub(InboundRequestMediaModel)

    }

    @Test
    def "InboundRequestServiceParameter's constructor do not leak reference"() {
        given:
        def requests = [Stub(RequestBatchEntity)]
        def responses = [Stub(ResponseChangeSetEntity)]
        def medias = [Stub(InboundRequestMediaModel)]

        when:
        def param = inboundRequestServiceParameter()
                .withRequests(requests)
                .withResponses(responses)
                .withMedias(medias)
                .build()

        requests.clear()
        responses.clear()
        medias.clear()

        then:
        !param.requests.empty
        !param.responses.empty
        !param.medias.empty
    }

    def parameterFieldsNotSet() {
        inboundRequestServiceParameter().build()
    }

    def parameterFieldsAllSetToNull() {
        inboundRequestServiceParameter()
                .withRequests(null)
                .withResponses(null)
                .withMedias(null)
                .withHttpMethod(null)
                .withUserId(null)
                .withSapPassport(null)
                .build()
    }
}
