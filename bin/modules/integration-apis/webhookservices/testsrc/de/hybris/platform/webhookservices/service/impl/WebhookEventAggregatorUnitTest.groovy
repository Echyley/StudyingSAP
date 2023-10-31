/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.webhookservices.service.impl

import de.hybris.bootstrap.annotations.UnitTest
import de.hybris.platform.core.PK
import de.hybris.platform.testframework.JUnitPlatformSpecification
import de.hybris.platform.tx.AfterSaveEvent
import de.hybris.platform.webhookservices.config.WebhookServicesConfiguration
import de.hybris.platform.webhookservices.event.ItemCreatedEvent
import de.hybris.platform.webhookservices.event.ItemDeletedEvent
import de.hybris.platform.webhookservices.event.ItemSavedEvent
import de.hybris.platform.webhookservices.event.ItemUpdatedEvent
import de.hybris.platform.webhookservices.event.WebhookEvent
import org.junit.Test

@UnitTest
class WebhookEventAggregatorUnitTest extends JUnitPlatformSpecification {

    private static final def SOME_PK = PK.fromLong 0

    private static final def CREATED_EVENT = new ItemCreatedEvent(new AfterSaveEvent(SOME_PK, AfterSaveEvent.CREATE))
    private static final def UPDATED_EVENT = new ItemUpdatedEvent(new AfterSaveEvent(SOME_PK, AfterSaveEvent.UPDATE))
    private static final def SAVED_CREATE_EVENT = new ItemSavedEvent(new AfterSaveEvent(SOME_PK, AfterSaveEvent.CREATE))
    private static final def SAVED_UPDATE_EVENT = new ItemSavedEvent(new AfterSaveEvent(SOME_PK, AfterSaveEvent.UPDATE))
    private static final def DELETED_EVENT = new ItemDeletedEvent(new AfterSaveEvent(SOME_PK, AfterSaveEvent.REMOVE))

    private static final def SAVED_UPDATE_EVENT_FROM_CHILD =
            new ItemSavedEvent(new AfterSaveEvent(SOME_PK, AfterSaveEvent.UPDATE), true)

    private static final def UPDATED_EVENT_FROM_CHILD =
            new ItemUpdatedEvent(new AfterSaveEvent(SOME_PK, AfterSaveEvent.UPDATE), true)

    private def webhookServicesConfiguration = Stub WebhookServicesConfiguration
    private def aggregator = new WebhookEventAggregator(webhookServicesConfiguration)

    @Test
    def 'throws IllegalArgumentException when webhook services configuration is null'() {
        when:
        new WebhookEventAggregator(null)

        then:
        def ex = thrown IllegalArgumentException
        ex.message.contains 'WebhookServicesConfiguration cannot be null.'
    }

    @Test
    def 'return empty set when webhook events is #events'() {
        when:
        def finalEvents = aggregator.aggregate(events as Collection<WebhookEvent>)

        then:
        finalEvents.isEmpty()

        where:
        events << [null, []]
    }

    @Test
    def 'aggregate to original list of events when events are created by #creator item only'() {
        when:
        def finalEvents = aggregator.aggregate(entryEvents as Collection<WebhookEvent>)

        then:
        finalEvents == entryEvents as Set

        where:
        creator  | entryEvents
        "root"   | [SAVED_CREATE_EVENT, SAVED_UPDATE_EVENT, CREATED_EVENT, UPDATED_EVENT, DELETED_EVENT]
        "nested" | [SAVED_UPDATE_EVENT_FROM_CHILD, UPDATED_EVENT_FROM_CHILD]
    }

    @Test
    def 'events created by nested items are removed when an event that is not created by nested item is present'() {
        given:
        def originalEvents = [SAVED_CREATE_EVENT, UPDATED_EVENT, CREATED_EVENT, SAVED_UPDATE_EVENT, DELETED_EVENT]
        def createdEvents = [SAVED_UPDATE_EVENT_FROM_CHILD, UPDATED_EVENT_FROM_CHILD]

        when:
        def finalEvents = aggregator.aggregate(createdEvents + originalEvents + [DELETED_EVENT] as Collection<WebhookEvent>)

        then:
        finalEvents == originalEvents as Set
    }

    @Test
    def 'duplicate #event is discarded'() {
        given:
        List<WebhookEvent> duplicateEvents = event * 2

        when:
        def finalEvents = aggregator.aggregate(duplicateEvents)

        then:
        finalEvents == event as Set

        where:
        event << [[SAVED_CREATE_EVENT], [SAVED_UPDATE_EVENT], [UPDATED_EVENT],
                  [CREATED_EVENT], [SAVED_UPDATE_EVENT_FROM_CHILD], [UPDATED_EVENT_FROM_CHILD], [DELETED_EVENT]]
    }

    @Test
    def '#inputEvents are consolidated into #squashedEvents'() {
        given:
        webhookServicesConfiguration.isEventConsolidationEnabled() >> true

        when:
        def finalEvents = aggregator.aggregate(inputEvents as Collection<WebhookEvent>)

        then:
        finalEvents == squashedEvents as Set

        where:
        inputEvents                                                            | squashedEvents
        [CREATED_EVENT]                                                        | [CREATED_EVENT]
        [CREATED_EVENT, UPDATED_EVENT]                                         | [CREATED_EVENT]
        [CREATED_EVENT, UPDATED_EVENT, UPDATED_EVENT]                          | [CREATED_EVENT]
        [CREATED_EVENT, CREATED_EVENT]                                         | [CREATED_EVENT]
        [UPDATED_EVENT]                                                        | [UPDATED_EVENT]
        [UPDATED_EVENT, UPDATED_EVENT]                                         | [UPDATED_EVENT]

        [SAVED_CREATE_EVENT]                                                   | [SAVED_CREATE_EVENT]
        [SAVED_CREATE_EVENT, SAVED_UPDATE_EVENT]                               | [SAVED_CREATE_EVENT]
        [SAVED_CREATE_EVENT, SAVED_UPDATE_EVENT, SAVED_UPDATE_EVENT]           | [SAVED_CREATE_EVENT]
        [SAVED_CREATE_EVENT, SAVED_CREATE_EVENT]                               | [SAVED_CREATE_EVENT]
        [SAVED_UPDATE_EVENT]                                                   | [SAVED_UPDATE_EVENT]
        [SAVED_UPDATE_EVENT, SAVED_UPDATE_EVENT]                               | [SAVED_UPDATE_EVENT]

        [SAVED_CREATE_EVENT, CREATED_EVENT]                                    | [SAVED_CREATE_EVENT, CREATED_EVENT]
        [SAVED_CREATE_EVENT, SAVED_CREATE_EVENT, CREATED_EVENT]                | [SAVED_CREATE_EVENT, CREATED_EVENT]
        [SAVED_CREATE_EVENT, CREATED_EVENT, CREATED_EVENT]                     | [SAVED_CREATE_EVENT, CREATED_EVENT]

        [SAVED_CREATE_EVENT, UPDATED_EVENT]                                    | [SAVED_CREATE_EVENT, UPDATED_EVENT]
        [SAVED_CREATE_EVENT, SAVED_CREATE_EVENT, UPDATED_EVENT]                | [SAVED_CREATE_EVENT, UPDATED_EVENT]
        [SAVED_CREATE_EVENT, UPDATED_EVENT, UPDATED_EVENT]                     | [SAVED_CREATE_EVENT, UPDATED_EVENT]

        [CREATED_EVENT, SAVED_UPDATE_EVENT]                                    | [CREATED_EVENT, SAVED_UPDATE_EVENT]
        [CREATED_EVENT, CREATED_EVENT, SAVED_UPDATE_EVENT]                     | [CREATED_EVENT, SAVED_UPDATE_EVENT]
        [CREATED_EVENT, SAVED_UPDATE_EVENT, SAVED_UPDATE_EVENT]                | [CREATED_EVENT, SAVED_UPDATE_EVENT]

        [CREATED_EVENT, UPDATED_EVENT, SAVED_CREATE_EVENT, SAVED_UPDATE_EVENT] | [CREATED_EVENT, SAVED_CREATE_EVENT]
        [UPDATED_EVENT, UPDATED_EVENT, SAVED_UPDATE_EVENT, SAVED_UPDATE_EVENT] | [UPDATED_EVENT, SAVED_UPDATE_EVENT]

        [DELETED_EVENT]                                                        | [DELETED_EVENT]
        [CREATED_EVENT, DELETED_EVENT, CREATED_EVENT]                          | [CREATED_EVENT, DELETED_EVENT]
        [UPDATED_EVENT, DELETED_EVENT, UPDATED_EVENT]                          | [UPDATED_EVENT, DELETED_EVENT]
        [CREATED_EVENT, UPDATED_EVENT, DELETED_EVENT]                          | [CREATED_EVENT, DELETED_EVENT]

        [SAVED_CREATE_EVENT, DELETED_EVENT, SAVED_CREATE_EVENT]                | [SAVED_CREATE_EVENT, DELETED_EVENT]
        [SAVED_UPDATE_EVENT, DELETED_EVENT, SAVED_UPDATE_EVENT]                | [SAVED_UPDATE_EVENT, DELETED_EVENT]
        [SAVED_CREATE_EVENT, SAVED_UPDATE_EVENT, DELETED_EVENT]                | [SAVED_CREATE_EVENT, DELETED_EVENT]
    }
}