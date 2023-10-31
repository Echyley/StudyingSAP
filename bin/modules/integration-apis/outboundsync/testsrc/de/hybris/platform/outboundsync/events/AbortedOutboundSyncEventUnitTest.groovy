/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.outboundsync.events

import de.hybris.bootstrap.annotations.UnitTest
import de.hybris.platform.core.PK
import de.hybris.platform.outboundsync.dto.OutboundItemChange
import de.hybris.platform.outboundsync.dto.OutboundItemDTO
import de.hybris.platform.outboundsync.dto.OutboundItemDTOGroup
import de.hybris.platform.outboundsync.job.OutboundItemFactory
import de.hybris.platform.testframework.JUnitPlatformSpecification
import org.junit.Test

@UnitTest
class AbortedOutboundSyncEventUnitTest extends JUnitPlatformSpecification {

    private static final def A_CRONJOB_PK = PK.fromLong(1234)
    private static final def ANOTHER_CRONJOB_PK = PK.fromLong(4321)
    private static final int ITEMS_PROCESSED = 3

    private static final def EVENT = new AbortedOutboundSyncEvent(A_CRONJOB_PK, ITEMS_PROCESSED)

    @Test
    def 'event cannot be instantiated with null job PK'() {
        when:
        new AbortedOutboundSyncEvent(null, ITEMS_PROCESSED)

        then:
        def e = thrown IllegalArgumentException
        e.message == 'CronJob PK cannot be null'
    }

    @Test
    def "event can be instantiated with #sign number of changes"() {
        given:
        def event = new AbortedOutboundSyncEvent(A_CRONJOB_PK, cnt)

        expect:
        event.cronJobPk == A_CRONJOB_PK
        event.changesProcessed == cnt

        where:
        cnt | sign
        -1  | 'negative'
        0   | 'zero'
        1   | 'positive'
    }

    @Test
    def "event can be instantiated with #condition item group"() {
        given:
        def event = new AbortedOutboundSyncEvent(A_CRONJOB_PK, group)

        expect:
        event.cronJobPk == A_CRONJOB_PK
        event.changesProcessed == itemsCount

        where:
        group                       | itemsCount | condition
        null                        | 0          | 'null'
        groupWith(item(1), item(2)) | 2          | 'not null'
    }

    @Test
    def "event can be instantiated with a list of item groups"() {
        given:
        def event = new AbortedOutboundSyncEvent(A_CRONJOB_PK, [groupWith(item(1)), groupWith(item(2), item(3))])

        expect:
        event.cronJobPk == A_CRONJOB_PK
        event.changesProcessed == 3
    }

    @Test
    def "#initialEvent equals #other is #expectedEquals"() {
        expect:
        (EVENT == other) == expectedEquals

        where:
        initialEvent | other                                                             | expectedEquals
        EVENT        | new AbortedOutboundSyncEvent(A_CRONJOB_PK, ITEMS_PROCESSED)       | true
        EVENT        | new AbortedOutboundSyncEvent(ANOTHER_CRONJOB_PK, ITEMS_PROCESSED) | false
        EVENT        | new AbortedOutboundSyncEvent(A_CRONJOB_PK, 6)                     | false
        EVENT        | new IgnoredOutboundSyncEvent(A_CRONJOB_PK)                        | false
    }

    @Test
    def "AbortedOutboundSyncEvent{cronJobPk=1234} equals itself"() {
        expect:
        EVENT == EVENT
    }

    @Test
    def "equals is false when comparing to null event"() {
        expect:
        !(EVENT == null)
    }

    @Test
    def "exception is thrown when instantiating the event with null cronjob pk"() {
        when:
        new AbortedOutboundSyncEvent(null, ITEMS_PROCESSED)

        then:
        thrown(IllegalArgumentException)
    }

    @Test
    def "hashCode is #expectedEquals between #initialEvent and #other"() {
        expect:
        (EVENT.hashCode() == other.hashCode()) == expectedEquals

        where:
        initialEvent | other                                                             | expectedEquals | isEquals
        EVENT        | new AbortedOutboundSyncEvent(A_CRONJOB_PK, ITEMS_PROCESSED)       | true           | "is equals"
        EVENT        | new AbortedOutboundSyncEvent(ANOTHER_CRONJOB_PK, ITEMS_PROCESSED) | false          | "is not equals"
        EVENT        | new AbortedOutboundSyncEvent(A_CRONJOB_PK, 6)                     | false          | "is not equals"
    }

    @Test
    def "toString includes only itemsProcessed count when event is instantiated with #param parameter"() {
        expect:
        with(event.toString()) {
            contains "cronJobPk=$A_CRONJOB_PK"
            contains 'itemsProcessed=0'
            contains('itemPKs=[]')
        }

        where:
        event                                            | param
        new AbortedOutboundSyncEvent(A_CRONJOB_PK, 0)    | 'processed count'
        new AbortedOutboundSyncEvent(A_CRONJOB_PK, null) | 'null items group'
    }

    @Test
    def "toString includes items in the group when event is instantiated with non-null items group parameter"() {
        given:
        def itemPk = 1L
        def group = groupWith(item(itemPk), item(itemPk))
        def event = new AbortedOutboundSyncEvent(A_CRONJOB_PK, group)

        expect:
        with(event.toString()) {
            contains "cronJobPk=$A_CRONJOB_PK"
            contains "itemsProcessed=$group.itemsCount"
            contains("itemPKs=[$itemPk, $itemPk]")
        }
    }

    private OutboundItemDTOGroup groupWith(OutboundItemDTO... items) {
        OutboundItemDTOGroup.from items as List, Stub(OutboundItemFactory)
    }

    private OutboundItemDTO item(long pk) {
        def nonImportantPk = 0L
        def change = Stub(OutboundItemChange) {
            getPK() >> pk
        }
        OutboundItemDTO.Builder.item()
                .withItem(change)
                .withChannelConfigurationPK(nonImportantPk)
                .withCronJobPK(A_CRONJOB_PK)
                .withIntegrationObjectPK(nonImportantPk)
                .build()
    }
}
