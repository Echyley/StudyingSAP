/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.outboundsync.events

import de.hybris.bootstrap.annotations.UnitTest
import de.hybris.platform.core.PK
import de.hybris.platform.outboundsync.dto.OutboundItemChange
import de.hybris.platform.outboundsync.dto.OutboundItemDTO
import de.hybris.platform.testframework.JUnitPlatformSpecification
import org.junit.Test

@UnitTest
class IgnoredOutboundSyncEventUnitTest extends JUnitPlatformSpecification {
    private static final def JOB_PK = PK.fromLong(1)
    private static final def OTHER_JOB_PK = PK.fromLong(2)
    private static final def CHANGED_ITEM_PK = 3L
    private static final int ITEMS_IGNORED = 3
    private static final def EVENT = new IgnoredOutboundSyncEvent(JOB_PK)

    @Test
    def 'exception is thrown when instantiating the event with null CronJob PK'() {
        when:
        new IgnoredOutboundSyncEvent(null as PK)

        then:
        def e = thrown IllegalArgumentException
        e.message.contains 'CronJob PK'
    }

    @Test
    def 'can be instantiated with an OutboundItemDTO'() {
        given:
        def item = itemDTO()
        def event = new IgnoredOutboundSyncEvent(item)

        expect:
        event.cronJobPk == item.cronJobPK
    }

    @Test
    def "IgnoredOutboundSyncEvent(cronJobPk=1, itemsIgnored=3) equals #other is #equalsResult"() {
        expect:
        (EVENT == other) == equalsResult

        where:
        other                                               | equalsResult
        new IgnoredOutboundSyncEvent(JOB_PK)                | true
        new IgnoredOutboundSyncEvent(OTHER_JOB_PK)          | false
        new AbortedOutboundSyncEvent(JOB_PK, ITEMS_IGNORED) | false
    }

    @Test
    def "equals itself"() {
        expect:
        EVENT == EVENT
    }

    @Test
    def "equals is false when comparing to null event"() {
        expect:
        !(EVENT == null)
    }

    @Test
    def "hashCode is #isEquals for IgnoredOutboundSyncEvent{cronJobPk=1} and #other"() {
        expect:
        (EVENT.hashCode() == other.hashCode()) == expectedEquals

        where:
        other                                      | expectedEquals | isEquals
        new IgnoredOutboundSyncEvent(JOB_PK)       | true           | "equal"
        new IgnoredOutboundSyncEvent(OTHER_JOB_PK) | false          | "not equal"
    }

    @Test
    def 'toString includes only job PK when event is instantiated with PK parameter'() {
        given:
        def event = new IgnoredOutboundSyncEvent(JOB_PK)

        expect:
        with(event.toString()) {
            contains "cronJobPk=$JOB_PK"
            contains 'itemPK=null'
        }
    }

    @Test
    def "toString includes the item PK too when event is instantiated with OutboundItemDTO parameter"() {
        given:
        def item = itemDTO()
        def event = new IgnoredOutboundSyncEvent(item)

        expect:
        with(event.toString()) {
            contains "cronJobPk=$item.cronJobPK"
            contains("itemPK=$item.item.PK")
        }
    }

    private OutboundItemDTO itemDTO() {
        Stub(OutboundItemDTO) {
            getCronJobPK() >> JOB_PK
            getItem() >> Stub(OutboundItemChange) {
                getPK() >> CHANGED_ITEM_PK
            }
        }
    }
}
