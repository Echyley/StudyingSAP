/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.webhookservices.service

import de.hybris.bootstrap.annotations.UnitTest
import de.hybris.platform.apiregistryservices.model.ConsumedDestinationModel
import de.hybris.platform.apiregistryservices.model.DestinationTargetModel
import de.hybris.platform.core.PK
import de.hybris.platform.core.model.ItemModel
import de.hybris.platform.core.model.order.OrderModel
import de.hybris.platform.core.model.type.ComposedTypeModel
import de.hybris.platform.integrationservices.model.DescriptorFactory
import de.hybris.platform.integrationservices.model.IntegrationObjectItemModel
import de.hybris.platform.integrationservices.model.IntegrationObjectModel
import de.hybris.platform.integrationservices.model.TypeDescriptor
import de.hybris.platform.integrationservices.service.ItemModelSearchService
import de.hybris.platform.servicelayer.event.events.AbstractEvent
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery
import de.hybris.platform.servicelayer.search.FlexibleSearchService
import de.hybris.platform.servicelayer.search.SearchResult
import de.hybris.platform.testframework.JUnitPlatformSpecification
import de.hybris.platform.webhookservices.event.WebhookEvent
import de.hybris.platform.webhookservices.model.WebhookConfigurationModel
import de.hybris.platform.webhookservices.service.impl.DefaultWebhookConfigurationService
import org.junit.Test

@UnitTest
class DefaultWebhookConfigurationServiceUnitTest extends JUnitPlatformSpecification {

    def flexibleSearch = Stub FlexibleSearchService
    def descriptorFactory = Stub DescriptorFactory
    def itemModelSearchService = Stub ItemModelSearchService
    def service = new DefaultWebhookConfigurationService(flexibleSearch, descriptorFactory, itemModelSearchService)
    def destinationTarget = Mock DestinationTargetModel

    @Test
    def 'WebhookConfigurationService fails to instantiate when #property is null'() {
        when:
        new DefaultWebhookConfigurationService(searchService, factory, itemSearchService)

        then:
        def e = thrown IllegalArgumentException
        e.message == "$property cannot be null"

        where:
        property                 | searchService               | factory                 | itemSearchService
        "FlexibleSearchService"  | null                        | Stub(DescriptorFactory) | Stub(ItemModelSearchService)
        "DescriptorFactory"      | Stub(FlexibleSearchService) | null                    | Stub(ItemModelSearchService)
        "ItemModelSearchService" | Stub(FlexibleSearchService) | Stub(DescriptorFactory) | null
    }

    @Test
    def "getWebhookConfigurationsByEventAndItemModel returns an empty collection when event is #event and item is #item"() {
        given:
        itemModelSearchService.nonCachingFindByPk(_ as PK) >> Optional.of(Stub(WebhookConfigurationModel))

        expect:
        service.getWebhookConfigurationsByEventAndItemModel(event, item).empty

        where:
        event               | item
        null                | null
        null                | Stub(ItemModel)
        Stub(AbstractEvent) | null
    }

    @Test
    def "getWebhookConfigurationsByEventAndItemModel returns an empty collection when no configurations match given event"() {
        given:
        def event = Stub AbstractEvent

        and: "webhook configuration is found without caching"
        itemModelSearchService.nonCachingFindByPk(_ as PK) >> Optional.of(Stub(WebhookConfigurationModel))

        when:
        def configs = service.getWebhookConfigurationsByEventAndItemModel event, Stub(ItemModel)

        then:
        configs.empty

        and: 'Query contains the event class to search for'
        flexibleSearch.search(_ as FlexibleSearchQuery) >> { args ->
            with(args[0] as FlexibleSearchQuery) {
                assert queryParameters == ['eventClass': event.class.canonicalName]
            }
            Stub(SearchResult)
        }
    }

    @Test
    def "getWebhookConfigurationsByEventAndItemModel returns an empty collection when event matches but integration object's root item is null"() {
        given: "Found event but integration object's root item is null"
        def config = webhookConfigWithIntObjRootItem(null)
        flexibleSearch.search(_ as FlexibleSearchQuery) >> Stub(SearchResult) {
            getResult() >> [config]
        }

        and: "webhook configuration is found without caching"
        itemModelSearchService.nonCachingFindByPk(_ as PK) >> Optional.of(Stub(WebhookConfigurationModel))

        when:
        def configs = service.getWebhookConfigurationsByEventAndItemModel Stub(AbstractEvent), new OrderModel()

        then: 'No events found with an Order Integration Object'
        configs.empty
    }

    @Test
    def "getWebhookConfigurationsByEventAndItemModel only returns configuration matching given event and item model"() {
        given: "Found multiple configurations with Integration Object of Product and Order root items"
        def configOfProduct = webhookConfigWithIntObjRootItem(itemOfType('Product'))
        def configOfOrder = webhookConfigWithIntObjRootItem(itemOfType('Order'))
        flexibleSearch.search(_ as FlexibleSearchQuery) >> Stub(SearchResult) {
            getResult() >> [configOfProduct, configOfOrder]
        }
        descriptorFactory.createItemTypeDescriptor({ it.type.code == 'Order' }) >> Stub(TypeDescriptor) {
            isInstance(_ as OrderModel) >> true
        }
        descriptorFactory.createItemTypeDescriptor({ it.type.code == 'Product' }) >> Stub(TypeDescriptor) {
            isInstance(_ as OrderModel) >> false
        }
        and: "webhook configuration is found without caching"
        itemModelSearchService.nonCachingFindByPk(configOfOrder.getPk()) >> Optional.of(configOfOrder)
        itemModelSearchService.nonCachingFindByPk(configOfProduct.getPk()) >> Optional.of(configOfProduct)

        when: 'Searching for an event with an Integration Object root item of Order type'
        def configs = service.getWebhookConfigurationsByEventAndItemModel Stub(AbstractEvent), new OrderModel()

        then: 'Only config matching Order is returned'
        configs == [configOfOrder]
    }

    @Test
    def "getWebhookConfigurationsByEventAndItemModel returns an empty collection when nonCachingFindByPk returns optional empty for the webhook configuration pk"() {
        given: "Found 1 configuration with matching event and root item"
        def config = webhookConfigWithIntObjRootItem(itemOfType('Order'))
        flexibleSearch.search(_ as FlexibleSearchQuery) >> Stub(SearchResult) {
            getResult() >> [config]
        }
        descriptorFactory.createItemTypeDescriptor({ it.type.code == 'Order' }) >> Stub(TypeDescriptor) {
            isInstance(_ as OrderModel) >> true
        }
        and: "itemModelSearchService fails to find the webhook config by it's pk"
        itemModelSearchService.nonCachingFindByPk(config.pk) >> Optional.empty()

        when: 'Searching for an event with an Integration Object root item of Order type'
        def configs = service.getWebhookConfigurationsByEventAndItemModel Stub(AbstractEvent), new OrderModel()

        then:
        configs.empty
    }

    @Test
    def "findByEventAndItemMatchingRootItem returns an empty collection when event is #event and item is #item"() {
        given: "webhook configuration is found without caching"
        itemModelSearchService.nonCachingFindByPk(_ as PK) >> Optional.of(Stub(WebhookConfigurationModel))
        expect:
        service.findByEventAndItemMatchingRootItem(event, item).empty

        where:
        event              | item
        null               | null
        null               | Stub(ItemModel)
        Stub(WebhookEvent) | null
    }

    @Test
    def "findByEventAndItemMatchingRootItem returns an empty collection when no configurations match given event"() {
        given:
        def event = Stub WebhookEvent
        and: "webhook configuration is found without caching"
        itemModelSearchService.nonCachingFindByPk(_ as PK) >> Optional.of(Stub(WebhookConfigurationModel))

        when:
        def configs = service.findByEventAndItemMatchingRootItem event, Stub(ItemModel)

        then:
        configs.empty

        and: 'Query contains the event class to search for'
        flexibleSearch.search(_ as FlexibleSearchQuery) >> { args ->
            with(args[0] as FlexibleSearchQuery) {
                assert queryParameters == ['eventClass': event.class.canonicalName]
            }
            Stub(SearchResult)
        }
    }

    @Test
    def "findByEventAndItemMatchingRootItem returns an empty collection when event matches but integration object's root item is null"() {
        given: "Found event but integration object's root item is null"
        def config = webhookConfigWithIntObjRootItem(null)
        flexibleSearch.search(_ as FlexibleSearchQuery) >> Stub(SearchResult) {
            getResult() >> [config]
        }
        and: "webhook configuration is found without caching"
        itemModelSearchService.nonCachingFindByPk(_ as PK) >> Optional.of(Stub(WebhookConfigurationModel))

        when:
        def configs = service.findByEventAndItemMatchingRootItem Stub(WebhookEvent), new OrderModel()

        then: 'No events found with an Order Integration Object'
        configs.empty
    }

    @Test
    def "findByEventAndItemMatchingRootItem only returns configuration matching given event and item model"() {
        given: "Found multiple configurations with Integration Object of Product and Order root items"
        def configOfProduct = webhookConfigWithIntObjRootItem(itemOfType('Product'))
        def configOfOrder = webhookConfigWithIntObjRootItem(itemOfType('Order'))
        flexibleSearch.search(_ as FlexibleSearchQuery) >> Stub(SearchResult) {
            getResult() >> [configOfProduct, configOfOrder]
        }
        descriptorFactory.createItemTypeDescriptor({ it.type.code == 'Order' }) >> Stub(TypeDescriptor) {
            isInstance(_ as OrderModel) >> true
        }
        descriptorFactory.createItemTypeDescriptor({ it.type.code == 'Product' }) >> Stub(TypeDescriptor) {
            isInstance(_ as OrderModel) >> false
        }
        and: "webhook configuration is found without caching"
        itemModelSearchService.nonCachingFindByPk(configOfOrder.getPk()) >> Optional.of(configOfOrder)
        itemModelSearchService.nonCachingFindByPk(configOfProduct.getPk()) >> Optional.of(configOfProduct)

        when: 'Searching for an event with an Integration Object root item of Order type'
        def configs = service.findByEventAndItemMatchingRootItem Stub(WebhookEvent), new OrderModel()

        then: 'Only config matching Order is returned'
        configs == [configOfOrder]
    }

    @Test
    def "findByEventAndItemMatchingRootItem returns an empty collection when nonCachingFindByPk returns optional empty for the webhook configuration pk"() {
        given: "Found 1 configuration with matching event and root item"
        def config = webhookConfigWithIntObjRootItem(itemOfType('Order'))
        flexibleSearch.search(_ as FlexibleSearchQuery) >> Stub(SearchResult) {
            getResult() >> [config]
        }
        descriptorFactory.createItemTypeDescriptor({ it.type.code == 'Order' }) >> Stub(TypeDescriptor) {
            isInstance(_ as OrderModel) >> true
        }
        and: "itemModelSearchService fails to find the webhook config by it's pk"
        itemModelSearchService.nonCachingFindByPk(config.pk) >> Optional.empty()

        when: 'Searching for an event with an Integration Object root item of Order type'
        def configs = service.findByEventAndItemMatchingRootItem Stub(WebhookEvent), new OrderModel()

        then:
        configs.empty
    }

    @Test
    def "findByEventAndItemMatchingAnyItem returns an empty collection when no configurations match event"() {
        given:
        def event = Stub WebhookEvent
        and: "webhook configuration is found without caching"
        itemModelSearchService.nonCachingFindByPk(_ as PK) >> Optional.of(Stub(WebhookConfigurationModel))

        when:
        def configs = service.findByEventAndItemMatchingAnyItem event, Stub(ItemModel)

        then:
        configs.empty

        and: 'Query contains the event class to search for'
        flexibleSearch.search(_ as FlexibleSearchQuery) >> { args ->
            with(args[0] as FlexibleSearchQuery) {
                assert queryParameters == ['eventClass': event.class.canonicalName]
            }
            Stub(SearchResult)
        }
    }

    @Test
    def "findByEventAndItemMatchingAnyItem returns an empty collection when event matches but integration object's item set is empty"() {
        given: "Found event but integration object's item set is empty"
        def config = webhookConfigWithIntObjItem(null)
        flexibleSearch.search(_ as FlexibleSearchQuery) >> Stub(SearchResult) {
            getResult() >> [config]
        }

        when:
        def configs = service.findByEventAndItemMatchingAnyItem Stub(WebhookEvent), new OrderModel()

        then: 'No events found with an Order Integration Object'
        configs.empty
    }

    @Test
    def "findByEventAndItemMatchingAnyItem only returns configuration matching event and item model"() {
        given: "Found multiple configurations with Integration Object of Product and Order items"
        def configOfProduct = webhookConfigWithIntObjItem(itemOfType('Product'))
        def configOfOrder = webhookConfigWithIntObjItem(itemOfType('Order'))
        flexibleSearch.search(_ as FlexibleSearchQuery) >> Stub(SearchResult) {
            getResult() >> [configOfProduct, configOfOrder]
        }
        descriptorFactory.createItemTypeDescriptor({ it.type.code == 'Order' }) >> Stub(TypeDescriptor) {
            isInstance(_ as OrderModel) >> true
        }
        descriptorFactory.createItemTypeDescriptor({ it.type.code == 'Product' }) >> Stub(TypeDescriptor) {
            isInstance(_ as OrderModel) >> false
        }
        and: "webhook configuration is found without caching"
        itemModelSearchService.nonCachingFindByPk(configOfOrder.getPk()) >> Optional.of(configOfOrder)
        itemModelSearchService.nonCachingFindByPk(configOfProduct.getPk()) >> Optional.of(configOfProduct)

        when: 'Searching for an event with an Integration Object item of Order type'
        def configs = service.findByEventAndItemMatchingAnyItem Stub(WebhookEvent), new OrderModel()

        then: 'Only config matching Order is returned'
        configs == [configOfOrder]
    }

    @Test
    def "findByEventAndItemMatchingAnyItem returns an empty collection when nonCachingFindByPk returns optional empty for the webhook configuration pk"() {
        given: "Found 1 configuration with matching event and item"
        def config = webhookConfigWithIntObjItem(itemOfType('Order'))
        flexibleSearch.search(_ as FlexibleSearchQuery) >> Stub(SearchResult) {
            getResult() >> [config]
        }
        descriptorFactory.createItemTypeDescriptor({ it.type.code == 'Order' }) >> Stub(TypeDescriptor) {
            isInstance(_ as OrderModel) >> true
        }
        and: "itemModelSearchService fails to find the webhook config by it's pk"
        itemModelSearchService.nonCachingFindByPk(config.pk) >> Optional.empty()

        when: 'Searching for an event with an Integration Object item of Order type'
        def configs = service.findByEventAndItemMatchingAnyItem Stub(WebhookEvent), new OrderModel()

        then:
        configs.empty
    }


    def itemOfType(def type) {
        Stub(IntegrationObjectItemModel) {
            getType() >> Stub(ComposedTypeModel) {
                getCode() >> type
            }
        }
    }

    def webhookConfigWithIntObjRootItem(IntegrationObjectItemModel rootItem) {
        def intObj = Stub(IntegrationObjectModel) {
            getRootItem() >> rootItem
        }
        new WebhookConfigurationModel(integrationObject: intObj, destination: destinationWithTarget(destinationTarget))
    }

    def webhookConfigWithIntObjItem(IntegrationObjectItemModel item) {
        def intObj = Stub(IntegrationObjectModel) {
            getItems() >> { item ? [item] : [] }
        }
        new WebhookConfigurationModel(integrationObject: intObj, destination: destinationWithTarget(destinationTarget))
    }

    def destinationWithTarget(def target) {
        new ConsumedDestinationModel(destinationTarget: target)
    }
}
