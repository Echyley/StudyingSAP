/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.outboundservices.update

import de.hybris.bootstrap.annotations.UnitTest
import de.hybris.platform.integrationservices.enums.HttpMethod
import de.hybris.platform.outboundservices.model.OutboundRequestModel
import de.hybris.platform.servicelayer.model.ModelService
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery
import de.hybris.platform.servicelayer.search.FlexibleSearchService
import de.hybris.platform.servicelayer.search.SearchResult
import de.hybris.platform.testframework.JUnitPlatformSpecification
import org.junit.Test

@UnitTest
class OutboundRequestSystemUpdaterUnitTest extends JUnitPlatformSpecification {

    def modelService = Mock(ModelService)
    def flexSearchService = Stub(FlexibleSearchService)

    def outboundRequestSystemUpdater = new OutboundRequestSystemUpdater(modelService, flexSearchService)

    @Test
    def "outbound channel configuration models are updated with default value when they are found with null synchronizeDelete"() {
        given:
        outboundRequestsFound([outboundRequestWithNullHttpMethod(), outboundRequestWithNullHttpMethod()])

        when:
        outboundRequestSystemUpdater.populateNullHttpMethodWithDefaultValues()

        then:
        2 * modelService.save(_ as OutboundRequestModel) >> { args ->
            def outboundRequest = args[0]
            assert outboundRequest.httpMethod == HttpMethod.POST
        }
    }

    @Test
    def "no outbound channel configuration models are updated when none are found with null synchronizeDelete"() {
        given:
        outboundRequestsFound([])

        when:
        outboundRequestSystemUpdater.populateNullHttpMethodWithDefaultValues()

        then:
        0 * modelService.save(_)
    }

    private def outboundRequestsFound(List<OutboundRequestModel> outboundRequestsFound) {
        flexSearchService.search(_ as FlexibleSearchQuery) >> Stub(SearchResult) {
            getResult() >> outboundRequestsFound
        }
    }

    private def outboundRequestWithNullHttpMethod() {
        def model = new OutboundRequestModel()
        model.setHttpMethod(null)
        model
    }
}
