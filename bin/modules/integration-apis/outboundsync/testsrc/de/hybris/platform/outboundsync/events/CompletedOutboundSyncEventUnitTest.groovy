/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.outboundsync.events

import de.hybris.bootstrap.annotations.UnitTest
import de.hybris.platform.core.PK
import de.hybris.platform.outboundsync.dto.OutboundItemChange
import de.hybris.platform.outboundsync.dto.OutboundItemDTO
import de.hybris.platform.outboundsync.dto.OutboundItemDTOBuilder
import de.hybris.platform.outboundsync.dto.OutboundItemDTOGroup
import de.hybris.platform.outboundsync.job.OutboundItemFactory
import de.hybris.platform.testframework.JUnitPlatformSpecification
import org.junit.Test

@UnitTest
class CompletedOutboundSyncEventUnitTest extends JUnitPlatformSpecification {
    private static final def CRON_JOB_PK = PK.fromLong 1234
    private static final def ANOTHER_CRON_JOB_PK = PK.fromLong 4321
    private static final def ITEMS_COMPLETED = 2

    @Test
    def 'event cannot be instantiated with null job PK'() {
        when:
        new CompletedOutboundSyncEvent(null, true, ITEMS_COMPLETED)

        then:
        def e = thrown IllegalArgumentException
        e.message == 'CronJob PK cannot be null'
    }

    @Test
    def "event can be instantiated with #flag success status"() {
        given:
        def event = new CompletedOutboundSyncEvent(CRON_JOB_PK, flag, ITEMS_COMPLETED)

        expect:
        event.cronJobPk == CRON_JOB_PK
        event.success == flag

        where:
        flag << [true, false]
    }

    @Test
    def "event can be instantiated with #sign number of changes"() {
        given:
        def event = new CompletedOutboundSyncEvent(CRON_JOB_PK, false, cnt)

        expect:
        event.cronJobPk == CRON_JOB_PK
        event.changesCompleted == cnt

        where:
        cnt | sign
        -1  | 'negative'
        0   | 'zero'
        1   | 'positive'
    }

    @Test
    def 'event can be instantiated with not null item group'() {
        given:
        def group = groupWith(item(1), item(2))
        def event = new CompletedOutboundSyncEvent(group, true)

        expect:
        event.cronJobPk == group.cronJobPk
        event.changesCompleted == group.itemsCount
    }

    @Test
    def 'event can be instantiated with a list of groups'() {
        given:
        def groups = [groupWith(item(1), item(2)), groupWith(item(3))]
        def event = new CompletedOutboundSyncEvent(groups, true)

        expect:
        event.cronJobPk == groups[0].cronJobPk
        event.changesCompleted == 3
    }

    @Test
    def 'creates an event for successful synchronization of a single item'() {
        given:
        def event = CompletedOutboundSyncEvent.successfulSync item(1)

        expect:
        with(event) {
            isSuccess()
            changesCompleted == 1
            cronJobPk == CRON_JOB_PK
        }
    }

    @Test
    def "equals is #expectedEquals when #condition"() {
        given:
        def sample = new CompletedOutboundSyncEvent(CRON_JOB_PK, true, ITEMS_COMPLETED)
        def other = new CompletedOutboundSyncEvent(otherJobPk, otherSuccess, otherItemsCount)

        expect:
        (sample == other) == expectedEquals

        where:
        condition                     | otherJobPk          | otherSuccess | otherItemsCount     | expectedEquals
        "all fields are equal"        | CRON_JOB_PK         | true         | ITEMS_COMPLETED     | true
        "cronjob pk's are different"  | ANOTHER_CRON_JOB_PK | true         | ITEMS_COMPLETED     | false
        "success flags are different" | CRON_JOB_PK         | false        | ITEMS_COMPLETED     | false
        "item counts are different"   | CRON_JOB_PK         | true         | ITEMS_COMPLETED + 1 | false
    }

    @Test
    def "equals is true when events are the same object"() {
        given:
        def event = new CompletedOutboundSyncEvent(CRON_JOB_PK, false, 7)

        expect:
        event == event
    }

    @Test
    def 'is not equal to even of a different type'() {
        given:
        def event = new AbortedOutboundSyncEvent(CRON_JOB_PK, 7)

        expect:
        new CompletedOutboundSyncEvent(CRON_JOB_PK, false, 7) != event
    }

    @Test
    def "equals is false when comparing to null event"() {
        expect:
        !(new CompletedOutboundSyncEvent(CRON_JOB_PK, true, 5) == null)
    }

    @Test
    def "hashCode is #expectedEquals when #condition"() {
        given:
        def sample = new CompletedOutboundSyncEvent(CRON_JOB_PK, true, ITEMS_COMPLETED)
        def other = new CompletedOutboundSyncEvent(otherJobPk, otherSuccess, otherCompleted)

        expect:
        (sample.hashCode() == other.hashCode()) == expectedEquals

        where:
        condition                     | otherJobPk          | otherSuccess | otherCompleted      | expectedEquals | hashCodesEquals
        "all fields are equal"        | CRON_JOB_PK         | true         | ITEMS_COMPLETED     | true           | 'equals'
        "cronjob pk's are different"  | ANOTHER_CRON_JOB_PK | true         | ITEMS_COMPLETED     | false          | 'not equals'
        "success flags are different" | CRON_JOB_PK         | false        | ITEMS_COMPLETED     | false          | 'not equals'
        "item counts are different"   | CRON_JOB_PK         | false        | ITEMS_COMPLETED + 1 | false          | 'not equals'
    }

    @Test
    def 'toString includes only changesCompleted count when event is instantiated with the completed count parameter'() {
        given:
        def event = new CompletedOutboundSyncEvent(CRON_JOB_PK, true, ITEMS_COMPLETED)

        expect:
        with(event.toString()) {
            contains "cronJobPk=$CRON_JOB_PK"
            contains "changesCompleted=$ITEMS_COMPLETED"
            contains 'itemPKs=[]'
            contains 'success=true'
        }
    }

    @Test
    def "toString includes items in the group when event is instantiated with the items group parameter"() {
        given:
        def itemPk = 1L
        def group = groupWith(item(itemPk), item(itemPk))
        def event = new CompletedOutboundSyncEvent(group, false)
        expect:
        with(event.toString()) {
            contains "cronJobPk=$CRON_JOB_PK"
            contains "changesCompleted=$group.itemsCount"
            contains 'success=false'
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
        OutboundItemDTOBuilder.outboundItemDTO()
                .withItem(change)
                .withChannelConfigurationPK(nonImportantPk)
                .withCronJobPK(CRON_JOB_PK)
                .withIntegrationObjectPK(nonImportantPk)
                .build()
    }
}
