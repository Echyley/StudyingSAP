/*
 *  Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.webhookservices.service

import de.hybris.bootstrap.annotations.IntegrationTest
import de.hybris.platform.apiregistryservices.enums.DestinationChannel
import de.hybris.platform.apiregistryservices.model.DestinationTargetModel
import de.hybris.platform.core.PK
import de.hybris.platform.core.model.order.OrderModel
import de.hybris.platform.integrationservices.model.IntegrationObjectModel
import de.hybris.platform.integrationservices.util.IntegrationObjectTestUtil
import de.hybris.platform.integrationservices.util.IntegrationTestUtil
import de.hybris.platform.integrationservices.util.impex.ModuleEssentialData
import de.hybris.platform.outboundservices.ConsumedDestinationBuilder
import de.hybris.platform.servicelayer.ServicelayerSpockSpecification
import de.hybris.platform.tx.AfterSaveEvent
import de.hybris.platform.variants.model.VariantProductModel
import de.hybris.platform.webhookservices.WebhookConfigurationBuilder
import de.hybris.platform.webhookservices.event.ItemSavedEvent
import de.hybris.platform.webhookservices.event.ItemCreatedEvent
import de.hybris.platform.webhookservices.util.WebhookServicesEssentialData
import org.junit.ClassRule
import org.junit.Test
import spock.lang.AutoCleanup
import spock.lang.Shared
import spock.lang.Unroll

import javax.annotation.Resource

import static de.hybris.platform.outboundservices.ConsumedDestinationBuilder.consumedDestinationBuilder
import static de.hybris.platform.webhookservices.WebhookConfigurationBuilder.webhookConfiguration

@IntegrationTest
class WebhookConfigurationServiceIntegrationTest extends ServicelayerSpockSpecification {

    private static final String IO = "WebhookConfigurationService_IO"
    private static final def ORDER_IO_CODE = "${IO}_ORDER"
    private static final def itemSavedEvent = new ItemSavedEvent(new AfterSaveEvent(PK.fromLong(1), AfterSaveEvent.CREATE))
    private static final def itemCreatedEvent = new ItemCreatedEvent(new AfterSaveEvent(PK.fromLong(1), AfterSaveEvent.CREATE))

    @Shared
    @ClassRule
    ModuleEssentialData essentialData = WebhookServicesEssentialData.webhookServicesEssentialData()
    @AutoCleanup('cleanup')
    WebhookConfigurationBuilder webhookBuilder = webhookConfiguration()

    @Resource
    private WebhookConfigurationService webhookConfigurationService

    def cleanup() {
        IntegrationTestUtil.removeAll IntegrationObjectModel
    }

    @Test
    @Unroll
    def "getWebhookConfigurationsByEventAndItemModel only returns WebhookConfigurations matching event and item model"() {
        given:
        def intObjOrder = IntegrationObjectTestUtil.createIntegrationObject ORDER_IO_CODE
        IntegrationObjectTestUtil.createIntegrationObjectRootItem intObjOrder, 'Order'

        and:
        def productIOCode = "${IO}_PRODUCT"
        def intObjProduct = IntegrationObjectTestUtil.createIntegrationObject productIOCode
        IntegrationObjectTestUtil.createIntegrationObjectRootItem intObjProduct, 'Product'

        and:
        webhookBuilder
                .withIntegrationObject(orderIOCode)
                .withDestination(consumedDestination('webhookOrderDestination'))
                .withEvent(itemEvent)
                .build()
        webhookBuilder
                .withIntegrationObject(productIOCode)
                .withDestination(consumedDestination('webhookProductDestination'))
                .build()

        when:
        def result = webhookConfigurationService.getWebhookConfigurationsByEventAndItemModel(event, new OrderModel())

        then:
        result.size() == 1
        result.count {
            it.integrationObject.code == orderIOCode && it.eventType == eventType
        } == 1

        where:
        description     | orderIOCode   | itemEvent        | eventType                      | event
        "saved event"   | ORDER_IO_CODE | ItemSavedEvent   | ItemSavedEvent.canonicalName   | itemSavedEvent
        "created event" | ORDER_IO_CODE | ItemCreatedEvent | ItemCreatedEvent.canonicalName | itemCreatedEvent
    }

    @Test
    def "getWebhookConfigurationsByEventAndItemModel returns results when item is a subtype of the integration object's root item"() {
        given:
        def productIOCode = "${IO}_PRODUCT"
        def intObjProduct = IntegrationObjectTestUtil.createIntegrationObject productIOCode
        IntegrationObjectTestUtil.createIntegrationObjectRootItem intObjProduct, 'Product'

        and:
        def productDest = consumedDestination('webhookProductDestination')

        and:
        webhookBuilder
                .withIntegrationObject(productIOCode)
                .withDestination(productDest)
                .build()

        when:
        def result = webhookConfigurationService.getWebhookConfigurationsByEventAndItemModel(itemSavedEvent, new VariantProductModel())

        then:
        result.size() == 1
        result.count {
            it.integrationObject.code == productIOCode && it.eventType == ItemSavedEvent.canonicalName
        } == 1
    }

    ConsumedDestinationBuilder consumedDestination(String id) {
        DestinationTargetModel target = IntegrationTestUtil.findAny(DestinationTargetModel, {
            it.destinationChannel == DestinationChannel.WEBHOOKSERVICES
        }).orElseThrow { new IllegalStateException('A webhook destination target not found') } as DestinationTargetModel
        consumedDestinationBuilder()
                .withId(id)
                .withUrl('https://does.not/matter')
                .withDestinationTarget(target)
    }
}