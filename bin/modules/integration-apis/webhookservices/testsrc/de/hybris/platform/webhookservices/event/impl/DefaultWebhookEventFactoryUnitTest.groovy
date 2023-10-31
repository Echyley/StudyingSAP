/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.webhookservices.event.impl

import de.hybris.bootstrap.annotations.UnitTest
import de.hybris.platform.core.PK
import de.hybris.platform.core.model.ItemModel
import de.hybris.platform.integrationservices.model.IntegrationObjectModel
import de.hybris.platform.integrationservices.search.RootItemSearchResult
import de.hybris.platform.integrationservices.search.RootItemSearchService
import de.hybris.platform.integrationservices.service.ItemModelSearchService
import de.hybris.platform.testframework.JUnitPlatformSpecification
import de.hybris.platform.tx.AfterSaveEvent
import de.hybris.platform.webhookservices.event.ItemCreatedEvent
import de.hybris.platform.webhookservices.event.ItemDeletedEvent
import de.hybris.platform.webhookservices.event.ItemSavedEvent
import de.hybris.platform.webhookservices.event.ItemUpdatedEvent
import de.hybris.platform.webhookservices.event.WebhookEvent
import de.hybris.platform.webhookservices.model.WebhookConfigurationModel
import de.hybris.platform.webhookservices.service.WebhookConfigurationService
import org.junit.Test
import spock.lang.Issue
import spock.lang.Shared

@UnitTest
@Issue('https://cxjira.sap.com/browse/IAPI-5120')
class DefaultWebhookEventFactoryUnitTest extends JUnitPlatformSpecification {

    private static final CREATED = AfterSaveEvent.CREATE
    private static final UPDATED = AfterSaveEvent.UPDATE
    private static final REMOVED = AfterSaveEvent.REMOVE

    private static final DEFAULT_PARENT_PK = PK.fromLong(4322)
    private static final ITEM_PK = PK.fromLong(4321)

    @Shared
    def CREATE_AFTER_SAVE_EVENT = afterSaveEvent(CREATED)
    @Shared
    def UPDATE_AFTER_SAVE_EVENT = afterSaveEvent(UPDATED)
    @Shared
    def REMOVE_AFTER_SAVE_EVENT = afterSaveEvent(REMOVED)

    def webhookConfigurationService = Stub(WebhookConfigurationService)
    def itemModelSearchService = Stub(ItemModelSearchService)
    def rootItemSearchService = Stub(RootItemSearchService)

    def factory = new DefaultWebhookEventFactory(
            webhookConfigurationService: webhookConfigurationService,
            itemModelSearchService: itemModelSearchService,
            rootItemSearchService: rootItemSearchService)

    @Test
    def 'returns empty list when #propertyName is null'() {
        given:
        def factory = new DefaultWebhookEventFactory(
                webhookConfigurationService: webhookConfigService,
                itemModelSearchService: itemSearchService,
                rootItemSearchService: rootItemService)

        expect:
        factory.create(CREATE_AFTER_SAVE_EVENT).isEmpty()

        where:
        propertyName             | webhookConfigService          | itemSearchService        | rootItemService
        'WebhookConfigService'   | null                          | itemModelSearchService() | rootItemSearchService()
        'ItemModelSearchService' | webhookConfigurationService() | null                     | rootItemSearchService()
        'RootItemSearchService'  | webhookConfigurationService() | itemModelSearchService() | null
    }

    @Test
    def 'returns empty list when Item matching Event.pk is not found'() {
        given:
        itemModelSearchService.nonCachingFindByPk(_ as PK) >> Optional.empty()

        expect:
        factory.create(CREATE_AFTER_SAVE_EVENT).isEmpty()
    }

    @Test
    def 'creating webhook events from #msg AfterSaveEvent for parent returns expected list'() {
        given:
        def originalItemModel = itemModel(ITEM_PK)
        itemModelSearchService.nonCachingFindByPk(ITEM_PK) >> Optional.of(originalItemModel)

        and: "a webhook configuration exists matching original item & its webhook event"
        webhookConfigurationService.findByEventAndItemMatchingRootItem(_ as WebhookEvent, originalItemModel)
                >> [Stub(WebhookConfigurationModel)]

        expect:
        factory.create(afterSaveEvent).containsAll(list)

        where:
        msg      | afterSaveEvent          | list
        'Create' | CREATE_AFTER_SAVE_EVENT | [itemSavedEvent(CREATE_AFTER_SAVE_EVENT), itemCreatedEvent(CREATE_AFTER_SAVE_EVENT)]
        'Update' | UPDATE_AFTER_SAVE_EVENT | [itemSavedEvent(UPDATE_AFTER_SAVE_EVENT), itemUpdatedEvent(UPDATE_AFTER_SAVE_EVENT)]
        'Remove' | REMOVE_AFTER_SAVE_EVENT | [itemDeletedEvent(REMOVE_AFTER_SAVE_EVENT)]
        'null'   | null                    | []
    }

    @Test
    def "creating webhook events from CREATE AfterSaveEvent for child returns SAVED and UPDATE for parent with child's events"() {
        given: "itemModel with owner present"
        def childItem = itemModel(ITEM_PK)
        itemModelSearchService.nonCachingFindByPk(ITEM_PK) >> Optional.of(childItem)

        and: "a webhook configuration exists matching original item & its webhook event"
        webhookConfigurationService.findByEventAndItemMatchingRootItem(_ as WebhookEvent, childItem)
                >> [Stub(WebhookConfigurationModel)]

        and: "a webhook configuration exists with original item type"
        def parentIO = Stub(IntegrationObjectModel)
        webhookConfigurationService.findByEventAndItemMatchingAnyItem(_ as WebhookEvent, childItem)
                >> [webhookConfigWithIO(parentIO)]

        and: "webhook configuration has a root matching a parent item"
        rootItemSearchService.findRoots(childItem, parentIO)
                >> RootItemSearchResult.createFrom([itemModel(DEFAULT_PARENT_PK)])

        when:
        def createdWebhookEvents = factory.create(CREATE_AFTER_SAVE_EVENT)

        then:
        createdWebhookEvents ==
                [
                        itemCreatedEvent(CREATE_AFTER_SAVE_EVENT),
                        itemSavedEvent(CREATE_AFTER_SAVE_EVENT),
                        itemUpdatedEvent(parentAfterSaveEvent(UPDATED), true),
                        itemSavedEvent(parentAfterSaveEvent(UPDATED), true)
                ]
    }

    @Test
    def "creating webhook events from UPDATE AfterSaveEvent for child returns SAVED and UPDATE parent with child's events"() {
        given: "itemModel with owner present"
        def parent = itemModel(DEFAULT_PARENT_PK)
        def originalItemModel = itemModel(ITEM_PK)
        itemModelSearchService.nonCachingFindByPk(ITEM_PK) >> Optional.of(originalItemModel)

        and: "a webhook configuration exists matching original item & its webhook event"
        webhookConfigurationService.findByEventAndItemMatchingRootItem(_ as WebhookEvent, originalItemModel)
                >> [Stub(WebhookConfigurationModel)]

        and: "a webhook configuration exists with original item type"
        def parentIO = Stub(IntegrationObjectModel)
        webhookConfigurationService.findByEventAndItemMatchingAnyItem(_ as WebhookEvent, originalItemModel)
                >> [webhookConfigWithIO(parentIO)]

        and: "webhook configuration has a root matching a parent item"
        rootItemSearchService.findRoots(originalItemModel, parentIO) >> RootItemSearchResult.createFrom([parent])

        when:
        def createdWebhookEvents = factory.create(UPDATE_AFTER_SAVE_EVENT)

        then:
        createdWebhookEvents ==
                [
                        itemUpdatedEvent(UPDATE_AFTER_SAVE_EVENT),
                        itemSavedEvent(UPDATE_AFTER_SAVE_EVENT),
                        itemUpdatedEvent(parentAfterSaveEvent(UPDATED), true),
                        itemSavedEvent(parentAfterSaveEvent(UPDATED), true)
                ]
    }

    @Test
    def "creating webhook events from REMOVE AfterSaveEvent for child returns REMOVED and UPDATE parent with child's events"() {
        given: "itemModel with owner present"
        def parent = itemModel(DEFAULT_PARENT_PK)
        def originalItemModel = itemModel(ITEM_PK)
        itemModelSearchService.nonCachingFindByPk(ITEM_PK) >> Optional.of(originalItemModel)

        and: "a webhook configuration exists matching original item & its webhook event"
        webhookConfigurationService.findByEventAndItemMatchingRootItem(_ as WebhookEvent, originalItemModel)
                >> [Stub(WebhookConfigurationModel)]

        and: "a webhook configuration exists with original item type"
        def parentIO = Stub(IntegrationObjectModel)
        webhookConfigurationService.findByEventAndItemMatchingAnyItem(_ as WebhookEvent, originalItemModel)
                >> [webhookConfigWithIO(parentIO)]

        and: "webhook configuration has a root matching a parent item"
        rootItemSearchService.findRoots(originalItemModel, parentIO) >> RootItemSearchResult.createFrom([parent])

        when:
        def createdWebhookEvents = factory.create(REMOVE_AFTER_SAVE_EVENT)

        then:
        createdWebhookEvents ==
                [
                        itemDeletedEvent(REMOVE_AFTER_SAVE_EVENT),
                        itemUpdatedEvent(parentAfterSaveEvent(UPDATED), true),
                        itemSavedEvent(parentAfterSaveEvent(UPDATED), true)
                ]
    }

    @Test
    def "creating webhook events from #msg AfterSaveEvent for item with parent with webhook config for ONLY parent"() {
        given: "itemModel with owner present"
        def originalItemModel = itemModel(ITEM_PK)
        itemModelSearchService.nonCachingFindByPk(ITEM_PK) >> Optional.of(originalItemModel)

        and: "no webhook configuration exists matching original item & its webhook event"
        webhookConfigurationService.findByEventAndItemMatchingRootItem(_ as WebhookEvent, originalItemModel) >> []

        and: "a webhook configuration exists with original item type"
        def parentIO = Stub(IntegrationObjectModel)
        webhookConfigurationService.findByEventAndItemMatchingAnyItem(_ as WebhookEvent, originalItemModel)
                >> [webhookConfigWithIO(parentIO)]

        and: "webhook configuration has a root matching a parent item"
        rootItemSearchService.findRoots(originalItemModel, parentIO)
                >> RootItemSearchResult.createFrom([itemModel(DEFAULT_PARENT_PK)])

        when:
        def createdWebhookEvents = factory.create(originalEvent)

        then:
        createdWebhookEvents ==
                [
                        itemUpdatedEvent(parentAfterSaveEvent(UPDATED), true),
                        itemSavedEvent(parentAfterSaveEvent(UPDATED), true)
                ]

        where:
        msg       | originalEvent
        "Updated" | UPDATE_AFTER_SAVE_EVENT
        "Created" | CREATE_AFTER_SAVE_EVENT
        "Removed" | REMOVE_AFTER_SAVE_EVENT
    }

    @Test
    def "does not create webhook events for #msg root when no webhook configuration matches root and given webhook event"() {
        given:
        def rootItem = itemModel(DEFAULT_PARENT_PK)
        itemModelSearchService.nonCachingFindByPk(DEFAULT_PARENT_PK) >> Optional.of(rootItem)

        and: "no webhook configuration exists matching original item & its webhook event"
        webhookConfigurationService.findByEventAndItemMatchingRootItem(_ as WebhookEvent, rootItem) >> []

        and: "update webhook configuration exists with original item type"
        def parentIO = Stub(IntegrationObjectModel)
        webhookConfigurationService.findByEventAndItemMatchingAnyItem(_ as WebhookEvent, rootItem)
                >> [webhookConfigWithIO(parentIO)]

        and: "webhook configuration has a root matching item"
        rootItemSearchService.findRoots(rootItem, parentIO) >> RootItemSearchResult.createFrom([rootItem])

        expect:
        factory.create(afterSaveEvent).empty

        where:
        msg       | afterSaveEvent
        "Updated" | UPDATE_AFTER_SAVE_EVENT
        "Created" | CREATE_AFTER_SAVE_EVENT
        "Removed" | REMOVE_AFTER_SAVE_EVENT
    }

    @Test
    def "creating webhook events from #msg AfterSaveEvent for item returns only those events that have webhook config associated"() {
        given: "itemModel with owner present"
        def originalItemModel = itemModel(ITEM_PK)
        itemModelSearchService.nonCachingFindByPk(ITEM_PK) >> Optional.of(originalItemModel)

        webhookConfigurationService.findByEventAndItemMatchingRootItem(_ as WebhookEvent, originalItemModel) >> []

        and: "an ItemSavedEvent webhook configuration exists with original item type"
        def parentIO = Stub(IntegrationObjectModel)
        webhookConfigurationService.findByEventAndItemMatchingAnyItem(_ as ItemSavedEvent, originalItemModel)
                >> [webhookConfigWithIO(parentIO)]

        and: "an ItemUpdatedEvent webhook configuration does not exist for original item type"
        webhookConfigurationService.findByEventAndItemMatchingAnyItem(_ as ItemUpdatedEvent, originalItemModel) >> []

        and: "webhook configuration has a root matching a parent item"
        rootItemSearchService.findRoots(originalItemModel, parentIO)
                >> RootItemSearchResult.createFrom([itemModel(DEFAULT_PARENT_PK)])

        when:
        def createdWebhookEvents = factory.create(originalEvent)

        then:
        createdWebhookEvents.size() == 1
        createdWebhookEvents.contains itemSavedEvent(parentAfterSaveEvent(UPDATED), true)

        where:
        msg       | originalEvent
        "Updated" | UPDATE_AFTER_SAVE_EVENT
        "Created" | CREATE_AFTER_SAVE_EVENT
        "Removed" | REMOVE_AFTER_SAVE_EVENT
    }

    @Test
    def 'creating webhook events from #msg AfterSaveEvent for item returns empty list when no webhook config exists for parent or item'() {
        given: "itemModel with owner present"
        def originalItemModel = itemModel(ITEM_PK)
        itemModelSearchService.nonCachingFindByPk(ITEM_PK) >> Optional.of(originalItemModel)

        webhookConfigurationService.findByEventAndItemMatchingRootItem(_ as WebhookEvent, originalItemModel) >> []
        webhookConfigurationService.findByEventAndItemMatchingAnyItem(_ as WebhookEvent, originalItemModel) >> []

        expect:
        factory.create(UPDATE_AFTER_SAVE_EVENT).isEmpty()

        where:
        msg      | afterSavedEvent
        'UPDATE' | UPDATE_AFTER_SAVE_EVENT
        'CREATE' | CREATE_AFTER_SAVE_EVENT
        "REMOVE" | REMOVE_AFTER_SAVE_EVENT
    }

    private ItemModel itemModel(final PK pk = ITEM_PK) {
        Stub(ItemModel) {
            getPk() >> pk
        }
    }

    private WebhookConfigurationModel webhookConfigWithIO(io) {
        Stub(WebhookConfigurationModel) {
            getIntegrationObject() >> io
        }
    }

    private AfterSaveEvent afterSaveEvent(final int eventType, final PK pk = ITEM_PK) {
        Stub(AfterSaveEvent) {
            getPk() >> pk
            getType() >> eventType
        }
    }

    private AfterSaveEvent parentAfterSaveEvent(final int eventType) {
        afterSaveEvent(eventType, DEFAULT_PARENT_PK)
    }

    private static ItemSavedEvent itemSavedEvent(final AfterSaveEvent event, final boolean fromChild = false) {
        new ItemSavedEvent(event, fromChild)
    }

    private static ItemCreatedEvent itemCreatedEvent(final AfterSaveEvent event, final boolean fromChild = false) {
        new ItemCreatedEvent(event, fromChild)
    }

    private static ItemUpdatedEvent itemUpdatedEvent(final AfterSaveEvent event, final boolean fromChild = false) {
        new ItemUpdatedEvent(event, fromChild)
    }

    private static ItemDeletedEvent itemDeletedEvent(final AfterSaveEvent event, final boolean fromChild = false) {
        new ItemDeletedEvent(event)
    }

    private ItemModelSearchService itemModelSearchService() {
        def item = itemModel(ITEM_PK)
        Stub(ItemModelSearchService) {
            nonCachingFindByPk(ITEM_PK) >> Optional.of(item)
        }
    }

    private WebhookConfigurationService webhookConfigurationService() {
        Stub(WebhookConfigurationService) {
            findByEventAndItemMatchingRootItem(_ as WebhookEvent, _ as ItemModel) >> [Stub(WebhookConfigurationModel)]
        }
    }

    private RootItemSearchService rootItemSearchService() {
        Stub(RootItemSearchService) {
            findRoots(_ as ItemModel, _ as IntegrationObjectModel) >> { List args ->
                RootItemSearchResult.createFrom([args[0]])
            }
        }
    }
}
