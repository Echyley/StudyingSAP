/*
 *  Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.outboundsync.config

import de.hybris.bootstrap.annotations.IntegrationTest
import de.hybris.platform.outboundsync.OutboundChannelConfigurationBuilder
import de.hybris.platform.outboundsync.model.OutboundChannelConfigurationModel
import de.hybris.platform.outboundsync.model.OutboundSyncCronJobModel
import de.hybris.platform.outboundsync.model.OutboundSyncJobModel
import de.hybris.platform.outboundsync.model.OutboundSyncStreamConfigurationContainerModel
import de.hybris.platform.outboundsync.model.OutboundSyncStreamConfigurationModel
import de.hybris.platform.servicelayer.ServicelayerSpockSpecification
import de.hybris.platform.servicelayer.exceptions.ModelSavingException
import de.hybris.platform.servicelayer.model.ModelService
import de.hybris.platform.servicelayer.session.SessionService
import org.junit.Test
import spock.lang.AutoCleanup

import javax.annotation.Resource

import static de.hybris.platform.integrationservices.IntegrationObjectItemAttributeModelBuilder.integrationObjectItemAttribute
import static de.hybris.platform.integrationservices.IntegrationObjectItemModelBuilder.integrationObjectItem
import static de.hybris.platform.integrationservices.IntegrationObjectModelBuilder.integrationObject
import static de.hybris.platform.integrationservices.util.IntegrationTestUtil.findAll
import static de.hybris.platform.integrationservices.util.IntegrationTestUtil.findAny
import static de.hybris.platform.integrationservices.util.IntegrationTestUtil.removeAll
import static de.hybris.platform.outboundservices.ConsumedDestinationBuilder.consumedDestinationBuilder
import static de.hybris.platform.outboundsync.OutboundChannelConfigurationBuilder.outboundChannelConfigurationBuilder

@IntegrationTest
class ChannelConfigurationIntegrationTest extends ServicelayerSpockSpecification {

    private static final String TEST_NAME = 'ChannelConfiguration'
    private static final String INTEGRATION_OBJECT_CODE = "${TEST_NAME}_IO"
    private static final String CHANNEL_CODE = "${TEST_NAME}_Channel"
    private static final String CONTAINER_CODE = "${CHANNEL_CODE}Container"
    private static final String STREAM_ID_SUFFIX = "Stream"
    private static final String DESTINATION_ID = "${TEST_NAME}_ConsumedDestination"
    private static def originalLocale

    private def ioBuilder = integrationObject().withCode(INTEGRATION_OBJECT_CODE)
    @AutoCleanup('cleanup')
    OutboundChannelConfigurationBuilder channelBuilder = outboundChannelConfigurationBuilder()
            .withCode(CHANNEL_CODE)
            .withIntegrationObject(ioBuilder)
            .withConsumedDestination consumedDestinationBuilder().withId(DESTINATION_ID)

    @Resource
    ModelService modelService
    @Resource
    SessionService sessionService

    def setup() {
        originalLocale = sessionService.getAttribute('locale')
        sessionService.setAttribute('locale', Locale.ENGLISH)
    }

    def cleanup() {
        removeAll OutboundSyncStreamConfigurationContainerModel
        originalLocale = sessionService.getAttribute('locale')
    }

    @Test
    def 'channel config created but related configurations are not created when auto generate is false'() {
        given:
        ioBuilder.withItem(integrationObjectItem().withCode('Category').root())

        when:
        channelBuilder.withoutAutoGenerate().build()

        then:
        channelConfigExists(CHANNEL_CODE)

        and:
        !streamContainerExists(CONTAINER_CODE)
        !streamExists("${CHANNEL_CODE}_Category_${STREAM_ID_SUFFIX}")
        !jobExistsForStreamContainer(CONTAINER_CODE)
    }

    @Test
    def 'channel config created with one stream when io does not contain parent child relationship'() {
        given:
        ioBuilder.withItem(integrationObjectItem().withCode('Category').root())

        when:
        channelBuilder.withAutoGenerate().build()

        then:
        channelConfigExists(CHANNEL_CODE)
        streamContainerExists(CONTAINER_CODE)
        streamExists "${CHANNEL_CODE}_Category_${STREAM_ID_SUFFIX}"
        jobExistsForStreamContainer(CONTAINER_CODE)
    }

    @Test
    def 'channel config created with multiple streams when io contains parent child relationship'() {
        given:
        ioBuilder
                .withItem(integrationObjectItem().withCode('Order').root()
                        .withAttribute(integrationObjectItemAttribute().withName('code').unique())
                        .withAttribute(integrationObjectItemAttribute().withName('entries').withReturnItem('OrderEntry')))
                .withItem(integrationObjectItem().withCode('OrderEntry')
                        .withAttribute(integrationObjectItemAttribute().withName('product').withReturnItem('Product'))
                        .withAttribute(integrationObjectItemAttribute().withName('order').withReturnItem('Order')))
                .withItem(integrationObjectItem().withCode('Product'))

        when:
        channelBuilder.withAutoGenerate().build()

        then:
        channelConfigExists(CHANNEL_CODE)
        streamContainerExists(CONTAINER_CODE)
        streamExists "${CHANNEL_CODE}_Order_${STREAM_ID_SUFFIX}"
        streamExists "${CHANNEL_CODE}_OrderEntry_${STREAM_ID_SUFFIX}"
        jobExistsForStreamContainer(CONTAINER_CODE)

        and:
        !streamExists("${CHANNEL_CODE}_Product_${STREAM_ID_SUFFIX}")
    }

    @Test
    def 'channel config created with multiple streams when io contains no root items'() {
        given:
        ioBuilder
                .withItem(integrationObjectItem().withCode('MyOrder1').withType('Order'))
                .withItem(integrationObjectItem().withCode('MyOrderEntry').withType('OrderEntry'))
                .withItem(integrationObjectItem().withCode('MyProduct1').withType('Product'))

        when:
        channelBuilder.withAutoGenerate().build()

        then:
        channelConfigExists(CHANNEL_CODE)
        streamContainerExists(CONTAINER_CODE)
        streamExists "${CHANNEL_CODE}_Order_${STREAM_ID_SUFFIX}"
        streamExists "${CHANNEL_CODE}_OrderEntry_${STREAM_ID_SUFFIX}"
        streamExists "${CHANNEL_CODE}_Product_${STREAM_ID_SUFFIX}"
        jobExistsForStreamContainer(CONTAINER_CODE)
    }

    @Test
    def 'cannot create the same channel configuration more than once'() {
        given:
        ioBuilder.withItem(integrationObjectItem().withCode('Category').root())

        when:
        def channel1 = channelBuilder.withAutoGenerate().build()

        then:
        channelConfigExists(channel1.code)

        when:
        def channelConfig2 = new OutboundChannelConfigurationModel()
        channelConfig2.setCode(channel1.code)
        channelConfig2.setIntegrationObject(channel1.integrationObject)
        channelConfig2.setDestination(channel1.destination)
        channelConfig2.setAutoGenerate(true)
        modelService.save channelConfig2

        then:
        thrown ModelSavingException
    }

    @Test
    def 'two channel configs with different code having the same io and destination are created'() {
        given:
        ioBuilder.withItem(integrationObjectItem().withCode('Category').root())
        and:
        def channelConfig2Code = "${CHANNEL_CODE}2"

        when:
        channelBuilder.withCode(CHANNEL_CODE).withAutoGenerate().build()
        channelBuilder.withCode(channelConfig2Code).withAutoGenerate().build()

        then:
        channelConfigExists(CHANNEL_CODE)
        channelConfigExists(channelConfig2Code)

        and:
        findAll(OutboundSyncStreamConfigurationContainerModel).size() == 2
        findAll(OutboundSyncStreamConfigurationModel).size() == 2
        findAll(OutboundSyncJobModel).size() == 2
        findAll(OutboundSyncCronJobModel).size() == 2
    }

    def channelConfigExists(String code) {
        findAny(OutboundChannelConfigurationModel, { it.code.contains(code) }).isPresent()
    }

    def streamContainerExists(String id) {
        findAny(OutboundSyncStreamConfigurationContainerModel, { it.id.contains(id) }).isPresent()
    }

    def streamExists(String id) {
        findAny(OutboundSyncStreamConfigurationModel, { it.streamId.contains(id) }).isPresent()
    }

    def jobExistsForStreamContainer(String code) {
        findAny(OutboundSyncCronJobModel, { it.job?.streamConfigurationContainer?.id?.contains(code) }).isPresent()
    }
}
