/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.odata2services.odata.processor.handler.persistence.batch

import de.hybris.bootstrap.annotations.UnitTest
import de.hybris.platform.testframework.JUnitPlatformSpecification
import org.apache.olingo.odata2.api.batch.BatchHandler
import org.apache.olingo.odata2.api.processor.ODataRequest
import org.junit.Test

@UnitTest
class DefaultChangeSetParamUnitTest extends JUnitPlatformSpecification {

    @Test
    def "fields are null if not set"() {
        given:
        def param = DefaultChangeSetParam.changeSetParam().build()

        expect:
        with(param) {
            !batchHandler
            !requests
        }
    }

    @Test
    def "fields are not null if set"() {
        given:
        def batchHandler = Stub BatchHandler
        def requests = [Stub(ODataRequest)]

        and:
        def param = DefaultChangeSetParam.changeSetParam()
                .withBatchHandler(batchHandler)
                .withRequests(requests)
                .build()

        expect:
        with(param) {
            batchHandler == batchHandler
            requests == requests
        }
    }

    @Test
    def "withRequests does not leak reference when requests is not null"() {
        given:
        def requests = [Stub(ODataRequest)]
        def param = DefaultChangeSetParam
                .changeSetParam()
                .withRequests(requests)
                .build()

        when:
        requests.clear()

        then:
        !param.requests.empty
    }

    @Test
    def "withRequests does not leak reference when requests is null"() {
        given:
        def param = DefaultChangeSetParam
                .changeSetParam()
                .withRequests(null)
                .build()

        expect:
        param.requests.empty
    }

    @Test
    def "getRequests returns immutable list"() {
        given:
        def param = DefaultChangeSetParam
                .changeSetParam()
                .withRequests([])
                .build()
        def requests = param.requests

        when:
        requests << Stub(ODataRequest)

        then:
        thrown UnsupportedOperationException
    }
}
