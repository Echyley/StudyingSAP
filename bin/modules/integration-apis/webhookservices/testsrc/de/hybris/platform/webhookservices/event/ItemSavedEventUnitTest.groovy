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
class ItemSavedEventUnitTest extends JUnitPlatformSpecification {

    private static final def DEFAULT_PK = PK.fromLong(4321)
    private static final def DEFAULT_PK_COMPARISON = PK.fromLong(1234)
    private static final def UNKNOWN_EVENT_TYPE = DefaultEventType.UNKNOWN

    @Shared
    def updateAfterSaveEvent = afterSaveEvent(DEFAULT_PK, AfterSaveEvent.UPDATE)

    @Shared
    def createAfterSaveEvent = afterSaveEvent(DEFAULT_PK, AfterSaveEvent.CREATE)

    @Shared
    def removeAfterSaveEvent = afterSaveEvent(DEFAULT_PK, AfterSaveEvent.REMOVE)

    private AfterSaveEvent afterSaveEvent(final PK pk, final int type) {
        Stub(AfterSaveEvent) {
            getPk() >> pk
            getType() >> type
        }
    }

    @Shared
    def createSavedEvent = new ItemSavedEvent(createAfterSaveEvent)

    @Test
    def "converts AfterSaveEvent type #afterSaveEventType.getType() to EventType #eventType.getType() type"() {
        given:
        def event = new ItemSavedEvent(afterSaveEventType)

        expect:
        eventType == event.getEventType()
        DEFAULT_PK == event.getPk()

        where:
        afterSaveEventType   | eventType
        createAfterSaveEvent | DefaultEventType.CREATED
        updateAfterSaveEvent | DefaultEventType.UPDATED
        removeAfterSaveEvent | UNKNOWN_EVENT_TYPE
    }

    @Test
    def "createdFromNestedItemEvent defaults to false"() {
        given:
        def event = new ItemSavedEvent(createAfterSaveEvent)

        expect:
        !event.isCreatedFromNestedItemEvent()
    }

    @Test
    def "hashcode() equal is #result when second event #description"() {
        expect:
        (createSavedEvent.hashCode() == event2.hashCode()) == result

        where:
        description                                                                         | event2                                                                           | result
        'same object'                                                                       | createSavedEvent                                                                 | true
        'event with same event type, pk & same createdFromNestedItemEvent value'            | new ItemSavedEvent(createAfterSaveEvent)                                         | true
        'event with different event type & same pk and createdFromNestedItemEvent value'    | new ItemSavedEvent(updateAfterSaveEvent)                                         | false
        'event with same event type and pk & different createdFromNestedItemEvent value'    | new ItemSavedEvent(createAfterSaveEvent, true)                                   | false
        'event with different pk, but same event type and createdFromNestedItemEvent value' | new ItemSavedEvent(afterSaveEvent(DEFAULT_PK_COMPARISON, AfterSaveEvent.CREATE)) | false
        'object'                                                                            | new Integer(1)                                                                   | false
    }

    @Test
    def "equals() is #result when compared to #description"() {

        expect:
        (createSavedEvent == event2) == result

        where:
        description                                                                         | event2                                                                           | result
        'same object'                                                                       | createSavedEvent                                                                 | true
        'event with same event type, pk & same createdFromNestedItemEvent value'            | new ItemSavedEvent(createAfterSaveEvent)                                         | true
        'event with different event type & same pk and createdFromNestedItemEvent value'    | new ItemSavedEvent(updateAfterSaveEvent)                                         | false
        'event with same event type and pk & different createdFromNestedItemEvent value'    | new ItemSavedEvent(createAfterSaveEvent, true)                                   | false
        'event with different pk, but same event type and createdFromNestedItemEvent value' | new ItemSavedEvent(afterSaveEvent(DEFAULT_PK_COMPARISON, AfterSaveEvent.CREATE)) | false
        'object'                                                                            | new Integer(1)                                                                   | false
        'null'                                                                              | null                                                                             | false
    }
}
