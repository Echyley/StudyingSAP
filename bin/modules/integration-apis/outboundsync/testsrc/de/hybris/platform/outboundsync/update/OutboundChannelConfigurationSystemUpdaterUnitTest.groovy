/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.outboundsync.update

import de.hybris.bootstrap.annotations.UnitTest
import de.hybris.platform.outboundsync.model.OutboundChannelConfigurationModel
import de.hybris.platform.servicelayer.model.ModelService
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery
import de.hybris.platform.servicelayer.search.FlexibleSearchService
import de.hybris.platform.servicelayer.search.SearchResult
import de.hybris.platform.testframework.JUnitPlatformSpecification
import org.junit.Test
import spock.lang.Specification


@UnitTest
class OutboundChannelConfigurationSystemUpdaterUnitTest extends JUnitPlatformSpecification {

    def modelService = Mock(ModelService)
    def flexSearchService = Stub(FlexibleSearchService)

    def outboundChannelConfigurationSystemUpdater = new OutboundChannelConfigurationSystemUpdater(modelService, flexSearchService)

    @Test
    def "outbound channel configuration models are updated with default value when they are found with null synchronizeDelete"() {
        given:
        def channelConfig1 = outboundChannelConfigurationWithNullSynchronizeDelete()
        def channelConfig2 = outboundChannelConfigurationWithNullSynchronizeDelete()
        channelConfigurationsFound([channelConfig1, channelConfig2])

        when:
        outboundChannelConfigurationSystemUpdater.populateNullSynchronizeDeleteWithDefaultValues()

        then:
        2 * modelService.save(_ as OutboundChannelConfigurationModel) >> { args ->
            def channelConfig = args[0]
            assert channelConfig.synchronizeDelete == false
        }
    }

    @Test
    def "no outbound channel configuration models are updated when none are found with null synchronizeDelete"() {
        given:
        channelConfigurationsFound([])

        when:
        outboundChannelConfigurationSystemUpdater.populateNullSynchronizeDeleteWithDefaultValues()

        then:
        0 * modelService.save(_) 
    }

    private def channelConfigurationsFound(List<OutboundChannelConfigurationModel> channelConfigurationsFound) {
        flexSearchService.search(_ as FlexibleSearchQuery) >> Stub(SearchResult) {
            getResult() >> channelConfigurationsFound
        }
    }

    private def outboundChannelConfigurationWithNullSynchronizeDelete() {
        def model = new OutboundChannelConfigurationModel()
        model.setSynchronizeDelete(null)
        model
    }
}
