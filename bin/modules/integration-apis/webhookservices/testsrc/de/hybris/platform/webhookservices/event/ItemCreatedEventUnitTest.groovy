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
class ItemCreatedEventUnitTest extends JUnitPlatformSpecification {

    private static final def DEFAULT_PK = PK.fromLong(4321)
    private static final def DEFAULT_PK_COMPARISON = PK.fromLong(1234)

    @Shared
    def createAfterSaveEvent = Stub(AfterSaveEvent) {
        getPk() >> DEFAULT_PK
        getType() >> AfterSaveEvent.CREATE
    }

    @Shared
    def createAfterSaveEvent_comparison = Stub(AfterSaveEvent) {
        getPk() >> DEFAULT_PK_COMPARISON
        getType() >> AfterSaveEvent.CREATE
    }

    @Shared
    def event = new ItemCreatedEvent(createAfterSaveEvent)

    @Test
    def "converts AfterSaveEvent Created type to Created EventType"() {
        given:
        def event = new ItemCreatedEvent(createAfterSaveEvent)

        expect:
        event.eventType.is DefaultEventType.CREATED
        event.pk.is DEFAULT_PK
    }

    @Test
    def "createdFromNestedItemEvent defaults to false"() {
        given:
        def event = new ItemCreatedEvent(createAfterSaveEvent)

        expect:
        !event.isCreatedFromNestedItemEvent()
    }

    @Test
    def "hashcode() equal is #result when second event #description"() {
        expect:
        (event.hashCode() == event2.hashCode()) == result

        where:
        description                                                                  | event2                                                      | result
        'same object'                                                                | event                                                       | true
        'different object with same pk & same createdFromNestedItemEvent value'      | new ItemCreatedEvent(createAfterSaveEvent)                  | true
        'different object with different pk & same createdFromNestedItemEvent value' | new ItemCreatedEvent(createAfterSaveEvent_comparison)       | false
        'different object with same pk & different createdFromNestedItemEvent value' | new ItemCreatedEvent(createAfterSaveEvent_comparison, true) | false
        'different object'                                                           | new Integer(1)                                              | false
    }

    @Test
    def "equals() is #result when compared to #description"() {
        expect:
        (event == event2) == result

        where:
        description                                                                  | event2                                                | result
        'same object'                                                                | event                                                 | true
        'different object with same pk & same createdFromNestedItemEvent value'      | new ItemCreatedEvent(createAfterSaveEvent)            | true
        'different object with different pk & same createdFromNestedItemEvent value' | new ItemCreatedEvent(createAfterSaveEvent_comparison) | false
        'different object with same pk & different createdFromNestedItemEvent value' | new ItemCreatedEvent(createAfterSaveEvent, true)      | false
        'different object'                                                           | new Integer(1)                                        | false
        'null'                                                                       | null                                                  | false
    }
}
