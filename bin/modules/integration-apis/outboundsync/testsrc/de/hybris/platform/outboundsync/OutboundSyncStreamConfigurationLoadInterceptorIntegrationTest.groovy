/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.outboundsync

import de.hybris.bootstrap.annotations.IntegrationTest
import de.hybris.platform.integrationservices.util.IntegrationTestUtil
import de.hybris.platform.integrationservices.util.JsonObject
import de.hybris.platform.outboundsync.config.impl.DefaultOutboundSyncConfiguration
import de.hybris.platform.outboundsync.model.OutboundSyncStreamConfigurationModel
import de.hybris.platform.outboundsync.util.OutboundSyncEssentialData
import de.hybris.platform.servicelayer.ServicelayerSpockSpecification
import de.hybris.platform.servicelayer.model.ModelService
import org.junit.ClassRule
import org.junit.Rule
import org.junit.Test
import spock.lang.AutoCleanup
import spock.lang.Shared

import javax.annotation.Resource

import static de.hybris.platform.integrationservices.IntegrationObjectItemAttributeModelBuilder.integrationObjectItemAttribute
import static de.hybris.platform.integrationservices.IntegrationObjectItemModelBuilder.integrationObjectItem
import static de.hybris.platform.integrationservices.IntegrationObjectModelBuilder.integrationObject
import static de.hybris.platform.outboundservices.ConsumedDestinationBuilder.consumedDestinationBuilder
import static de.hybris.platform.outboundsync.OutboundChannelConfigurationBuilder.outboundChannelConfigurationBuilder
import static de.hybris.platform.outboundsync.OutboundSyncStreamConfigurationBuilder.outboundSyncStreamConfigurationBuilder

@IntegrationTest
class OutboundSyncStreamConfigurationLoadInterceptorIntegrationTest extends ServicelayerSpockSpecification {
    private static final String TEST_NAME = "OutboundSyncStreamConfigurationLoadInterceptor"
    private static final String CHANNEL_CODE = "${TEST_NAME}_OutboundChannelConfiguration"
    private static final String IO = "${TEST_NAME}_IO"
    private static final String CONSUMED_DESTINATION = "${TEST_NAME}_ConsumedDestination"
    private static final String STREAM_ID = "${TEST_NAME}_StreamConfiguration"
    private static final String AUTOGENERATE_EXCLUDED_STREAM_IDS = "outboundsync.disabled.info.expression.auto.generation.stream.ids";

    @Resource(name = "outboundSyncConfiguration")
    private DefaultOutboundSyncConfiguration configurationService

    @Resource
    private ModelService modelService

    @Shared
    @ClassRule
    OutboundSyncEssentialData essentialData = OutboundSyncEssentialData.outboundSyncEssentialData()
    @Rule
    OutboundChannelConfigurationBuilder channelBuilder = outboundChannelConfigurationBuilder()
            .withCode(CHANNEL_CODE)
            .withConsumedDestination(consumedDestinationBuilder().withId(CONSUMED_DESTINATION))
            .withIntegrationObject integrationObject().withCode(IO)
            .withItem(integrationObjectItem().withCode('Order').root()
                    .withAttribute(integrationObjectItemAttribute().withName('code')))

    @AutoCleanup('cleanup')
    def streamConfigBuilder = outboundSyncStreamConfigurationBuilder()
            .withOutboundChannelCode(CHANNEL_CODE)
            .withId(STREAM_ID)

    @Test
    def 'infoExpression can be manually overridden when a new OutboundSyncStreamConfiguration id is included in outboundsync.disabled.info.expression.auto.generation.stream.ids'() {
        given: 'outboundsync.disabled.info.expression.auto.generation.stream.ids includes the stream id in its list of stream ids'
        def originalStreamIds = configurationService.getStringProperty(AUTOGENERATE_EXCLUDED_STREAM_IDS, "")
        configurationService.setProperty(AUTOGENERATE_EXCLUDED_STREAM_IDS, String.valueOf(STREAM_ID))

        when: 'the stream configuration infoExpression can be manually overidden'
        def newInfoExpression = '{ "key": "#{Order.doesntmatter}", "type": "#{itemtype}", "rootType": "#{itemtype}" }'
        final OutboundSyncStreamConfigurationModel streamConfig = streamConfigBuilder
                .withItemType('Order')
                .withInfoExpression(newInfoExpression)
                .build()

        then:
        streamConfig.infoExpression == newInfoExpression

        cleanup:
        configurationService.setProperty(AUTOGENERATE_EXCLUDED_STREAM_IDS, String.valueOf(originalStreamIds))
    }

    @Test
    def 'infoExpression is generated as empty json when a new OutboundSyncStreamConfiguration is created for item type that is not in the IO associated to the Configuration'() {
        given:
        final OutboundSyncStreamConfigurationModel streamConfig = streamConfigBuilder
                .withItemType('Catalog')
                .build()

        expect:
        JsonObject.createFrom(streamConfig.infoExpression).empty
    }

    @Test
    def 'infoExpression value is auto-calculated for OutboundSyncStreamConfiguration'() {
        given: 'a stream configuration for an item type that is in the IO associated to the configuration'
        final OutboundSyncStreamConfigurationModel streamConfig = streamConfigBuilder
                .withItemType('Order')
                .build()

        expect:
        def jsonInfo = JsonObject.createFrom streamConfig.infoExpression
        !jsonInfo.getString('key').empty
        !jsonInfo.getString('type').empty
        !jsonInfo.getString('rootType').empty
    }

    @Test
    def 'infoExpression is modified when a new unique attribute is added to the IO linked to the Configuration'() {
        given:
        final OutboundSyncStreamConfigurationModel streamConfig = streamConfigBuilder
                .withItemType('Order')
                .build()
        def oldInfo = JsonObject.createFrom streamConfig.infoExpression
        and:
        importNewUniqueAttributeForIO()

        when:
        OutboundSyncStreamConfigurationModel streamConfigWithNewInfoExpression = modelService.get(streamConfig.getPk())

        then:
        with(JsonObject.createFrom(streamConfigWithNewInfoExpression.infoExpression)) {
            getString('key').length() > oldInfo.getString('key').length()
            getString('type') == oldInfo.getString('type')
            getString('rootType') == oldInfo.getString('rootType')
        }
    }

    private static void importNewUniqueAttributeForIO() {
        IntegrationTestUtil.importImpEx(
                '$integrationItem = integrationObjectItem(integrationObject(code), code)[unique = true]',
                '$attributeName = attributeName[unique = true]',
                '$attributeDescriptor = attributeDescriptor(enclosingType(code), qualifier)',
                'INSERT_UPDATE IntegrationObjectItemAttribute; $integrationItem; $attributeName; $attributeDescriptor; unique',
                "                                            ; $IO:Order       ; name          ; Order:name          ; true  ")
    }
}
