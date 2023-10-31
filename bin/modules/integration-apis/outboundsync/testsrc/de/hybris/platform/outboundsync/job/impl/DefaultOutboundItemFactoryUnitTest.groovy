/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.outboundsync.job.impl

import de.hybris.bootstrap.annotations.UnitTest
import de.hybris.platform.core.PK
import de.hybris.platform.core.model.ItemModel
import de.hybris.platform.integrationservices.model.DescriptorFactory
import de.hybris.platform.integrationservices.model.IntegrationObjectModel
import de.hybris.platform.integrationservices.util.TestApplicationContext
import de.hybris.platform.outboundsync.dto.OutboundItemChange
import de.hybris.platform.outboundsync.dto.OutboundItemDTOBuilder
import de.hybris.platform.outboundsync.model.OutboundChannelConfigurationModel
import de.hybris.platform.servicelayer.exceptions.ModelLoadingException
import de.hybris.platform.servicelayer.model.ModelService
import de.hybris.platform.testframework.JUnitPlatformSpecification
import org.junit.Rule
import org.junit.Test

@UnitTest
class DefaultOutboundItemFactoryUnitTest extends JUnitPlatformSpecification {
    private static final Long itemPk = 1L
    private static final Long channelPk = 3L
    private def dto = OutboundItemDTOBuilder.outboundItemDTO()
            .withItem(Stub(OutboundItemChange) { getPK() >> itemPk })
            .withChannelConfigurationPK(channelPk)
            .withCronJobPK(PK.fromLong(4L))
            .build()

    private def descriptorFactory = Stub DescriptorFactory
    private def itemFactory = new DefaultOutboundItemFactory(descriptorFactory: descriptorFactory)

    @Rule
    TestApplicationContext applicationContext = new TestApplicationContext()

    @Test
    def "creates outbound item"() {
        given:
        itemFactory.modelService = Stub(ModelService) {
            get(PK.fromLong(itemPk)) >> Stub(ItemModel)
            get(PK.fromLong(channelPk)) >> Stub(OutboundChannelConfigurationModel) {
                getIntegrationObject() >> Stub(IntegrationObjectModel)
            }
        }

        when:
        def item = itemFactory.createItem(dto)

        then:
        item.changedItemModel.present
        item.integrationObject
        item.channelConfiguration
    }

    @Test
    def "gets DescriptorFactory from the context if not injected using setter"() {
        given:
        itemFactory.descriptorFactory = null
        and:
        def factory = Stub DescriptorFactory
        applicationContext.addBean 'integrationServicesDescriptorFactory', factory

        expect:
        itemFactory.descriptorFactory.is factory
    }

    @Test
    def "creates outbound item for a deleted item model"() {
        given:
        itemFactory.modelService = Stub(ModelService) {
            get(PK.fromLong(itemPk)) >> { throw new ModelLoadingException("") }
            get(PK.fromLong(channelPk)) >> Stub(OutboundChannelConfigurationModel) {
                getIntegrationObject() >> Stub(IntegrationObjectModel)
            }
        }

        when:
        def item = itemFactory.createItem(dto)

        then:
        !item.changedItemModel.present
        item.integrationObject
        item.channelConfiguration
    }

    @Test
    def "does not create outbound item for a deleted integration object model"() {
        given:
        itemFactory.modelService = Stub(ModelService) {
            get(PK.fromLong(itemPk)) >> Stub(ItemModel)
            get(PK.fromLong(channelPk)) >> Stub(OutboundChannelConfigurationModel) {
                getIntegrationObject() >> null
            }
        }

        when:
        itemFactory.createItem(dto)

        then:
        def e = thrown IllegalArgumentException
        e.message.contains 'IntegrationObjectDescriptor'
    }

    @Test
    def "does not create outbound item for a deleted channel configuration"() {
        given:
        itemFactory.modelService = Stub(ModelService) {
            get(PK.fromLong(itemPk)) >> Stub(ItemModel)
            get(PK.fromLong(channelPk)) >> { throw new ModelLoadingException("") }
        }

        when:
        itemFactory.createItem(dto)

        then:
        def e = thrown IllegalArgumentException
        e.message.contains 'channel configuration cannot be null'
    }
}
