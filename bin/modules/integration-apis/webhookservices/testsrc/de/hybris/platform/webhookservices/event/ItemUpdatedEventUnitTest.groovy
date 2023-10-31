/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.webhookservices.event

import de.hybris.bootstrap.annotations.UnitTest
import de.hybris.platform.core.PK
import de.hybris.platform.outboundservices.event.impl.DefaultEventType
import de.hybris.platform.testframework.JUnitPlatformSpecification
import de.hybris.platform.tx.AfterSaveEvent
import org.junit.Test
import spock.lang.Issue
import spock.lang.Shared

@UnitTest
@Issue('https://cxjira.sap.com/browse/IAPI-5224')
class ItemUpdatedEventUnitTest extends JUnitPlatformSpecification {

    private static final def DEFAULT_PK = PK.fromLong(4321)
    private static final def DEFAULT_PK_COMPARISON = PK.fromLong(1234)

    @Shared
    def updateAfterSaveEvent = Stub(AfterSaveEvent) {
        getPk() >> DEFAULT_PK
        getType() >> AfterSaveEvent.UPDATE
    }

    @Shared
    def updateAfterSaveEvent_comparison = Stub(AfterSaveEvent) {
        getPk() >> DEFAULT_PK_COMPARISON
        getType() >> AfterSaveEvent.UPDATE
    }

    @Shared
    def event = new ItemUpdatedEvent(updateAfterSaveEvent)

    @Test
    def "converts AfterSaveEvent Update type to Updated EventType"() {
        given:
        def event = new ItemUpdatedEvent(updateAfterSaveEvent)

        expect:
        event.eventType.is DefaultEventType.UPDATED
        event.pk.is DEFAULT_PK
    }

    @Test
    def "createdFromNestedItemEvent defaults to false"() {
        given:
        def event = new ItemUpdatedEvent(updateAfterSaveEvent)

        expect:
        !event.isCreatedFromNestedItemEvent()
    }

    @Test
    def "hashcode() equal is #result when second event #description"() {
        expect:
        (event.hashCode() == event2.hashCode()) == result

        where:
        description                                                                  | event2                                                | result
        'same object'                                                                | event                                                 | true
        'different object with same pk & same createdFromNestedItemEvent value'      | new ItemUpdatedEvent(updateAfterSaveEvent)            | true
        'different object with same pk & different createdFromNestedItemEvent value' | new ItemUpdatedEvent(updateAfterSaveEvent, true)      | false
        'different object with different pk & same createdFromNestedItemEvent value' | new ItemUpdatedEvent(updateAfterSaveEvent_comparison) | false
        'different object'                                                           | new Integer(1)                                        | false
    }

    @Test
    def "equals() is #result when compared to #description"() {
        expect:
        (event == event2) == result

        where:
        description                                                                  | event2                                                | result
        'same object & same createdFromNestedItemEvent value'                        | event                                                 | true
        'different object with same pk & same createdFromNestedItemEvent value'      | new ItemUpdatedEvent(updateAfterSaveEvent)            | true
        'different object with same pk & different createdFromNestedItemEvent value' | new ItemUpdatedEvent(updateAfterSaveEvent, true)      | false
        'different object with different pk & same createdFromNestedItemEvent value' | new ItemUpdatedEvent(updateAfterSaveEvent_comparison) | false
        'different object'                                                           | new Integer(1)                                        | false
        'null'                                                                       | null                                                  | false
    }
}

