/*
 *  Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.outboundsync.interceptor

import de.hybris.bootstrap.annotations.IntegrationTest
import de.hybris.platform.apiregistryservices.model.ConsumedDestinationModel
import de.hybris.platform.integrationservices.IntegrationObjectModelBuilder
import de.hybris.platform.integrationservices.util.IntegrationTestUtil
import de.hybris.platform.integrationservices.util.ItemTracker
import de.hybris.platform.outboundsync.model.OutboundChannelConfigurationModel
import de.hybris.platform.servicelayer.ServicelayerSpockSpecification
import org.junit.ClassRule
import org.junit.Rule
import org.junit.Test
import spock.lang.Issue
import spock.lang.Shared

import static de.hybris.platform.outboundservices.ConsumedDestinationBuilder.consumedDestinationBuilder
import static de.hybris.platform.outboundsync.OutboundChannelConfigurationBuilder.outboundChannelConfigurationBuilder

@IntegrationTest
@Issue('https://cxjira.sap.com/browse/IAPI-4703')
class OutboundChannelConfigurationModelingIntegrationTest extends ServicelayerSpockSpecification {

    private static final def PRODUCT_IO = 'ProductIO'
    private static final def CONSUMEDDESTINATION_ID = 'occDestination'
    private static final def OUTBOUNDCHANNELCONFIGURATION_CODE = 'outboundChannelConfiguration'

    @Shared
    @ClassRule
    IntegrationObjectModelBuilder io = IntegrationObjectModelBuilder.integrationObject().withCode(PRODUCT_IO)

    @Rule
    ItemTracker itemTracker = ItemTracker.track(OutboundChannelConfigurationModel, ConsumedDestinationModel)

    @Test
    def "cannot delete a consumed destination when it is assigned to an outbound channel configuration"() {
        given:
        outboundChannelConfigurationBuilder()
                .withCode(OUTBOUNDCHANNELCONFIGURATION_CODE)
                .withIntegrationObjectCode(PRODUCT_IO)
                .withConsumedDestination(consumedDestinationBuilder().withId(CONSUMEDDESTINATION_ID))
                .build()

        when:
        IntegrationTestUtil.importImpEx(
                'REMOVE ConsumedDestination; id[unique=true]',
                "                          ; $CONSUMEDDESTINATION_ID"
        )

        then:
        def e = thrown AssertionError
        e.message.contains "ConsumedDestinationModel [${CONSUMEDDESTINATION_ID}] cannot be deleted because it is used in one or more [${OutboundChannelConfigurationModel._TYPECODE} - ${OUTBOUNDCHANNELCONFIGURATION_CODE}]"
        and:
        IntegrationTestUtil.findAny(ConsumedDestinationModel, { it.id == CONSUMEDDESTINATION_ID }).present
    }

    @Test
    def "can delete a consumed destination when it is not assigned to any outbound channel configurations"() {
        given:
        consumedDestinationBuilder()
                .withId(CONSUMEDDESTINATION_ID)
                .withUrl("https://path/to/occ")
                .build()

        when:
        IntegrationTestUtil.importImpEx(
                'REMOVE ConsumedDestination; id[unique=true]',
                "                          ; $CONSUMEDDESTINATION_ID"
        )

        then:
        IntegrationTestUtil.findAny(ConsumedDestinationModel, { it.id == CONSUMEDDESTINATION_ID }).empty
    }
}