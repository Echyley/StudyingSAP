/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.outboundsync

import de.hybris.bootstrap.annotations.UnitTest
import de.hybris.platform.core.PK
import de.hybris.platform.outboundsync.config.impl.OutboundSyncConfiguration
import de.hybris.platform.outboundsync.dto.OutboundItemDTOGroup
import de.hybris.platform.outboundsync.events.StartedOutboundSyncEvent
import de.hybris.platform.testframework.JUnitPlatformSpecification
import org.junit.Test

@UnitTest
class BatchReleaseStrategyUnitTest extends JUnitPlatformSpecification {

    private static final PK CRON_JOB_PK = PK.fromLong(77)
    def outboundSyncConfig = Stub(OutboundSyncConfiguration)
    def releaseStrategy = new BatchReleaseStrategy(outboundSyncConfig)
    def defaultDTOGroup = Stub(OutboundItemDTOGroup) {
        getCronJobPk() >> CRON_JOB_PK
        getItemsCount() >> 1
    }

    @Test
    def "cannot be instantiated with null OutboundSyncConfiguration"() {
        when:
        new BatchReleaseStrategy(null)

        then:
        def e = thrown IllegalArgumentException
        e.message.contains 'OutboundSyncConfiguration cannot be null'
    }

    @Test
    def "message is not released when for an empty list"() {
        expect:
        !releaseStrategy.canMessagesBeReleased([])
    }

    @Test
    def "message is released when the item group size reaches the configured limit"() {
        given:
        releaseStrategy.onEvent(new StartedOutboundSyncEvent(CRON_JOB_PK, new Date(), 2))
        and:
        outboundSyncConfig.getOutboundBatchLimit() >> 1

        expect:
        releaseStrategy.canMessagesBeReleased([defaultDTOGroup])
    }

    @Test
    def "message is released when item group size reaches the detected changes"() {
        given:
        def startedEvent = new StartedOutboundSyncEvent(CRON_JOB_PK, new Date(), 1)

        when:
        releaseStrategy.onEvent(startedEvent)

        then:
        releaseStrategy.canMessagesBeReleased([defaultDTOGroup])
    }

    @Test
    def "message is released when groups contain multiple items"() {
        given:
        def startedEvent = new StartedOutboundSyncEvent(CRON_JOB_PK, new Date(), 10)

        when:
        releaseStrategy.onEvent(startedEvent)

        then:
        releaseStrategy.canMessagesBeReleased([dtoGroupWithItems(3), dtoGroupWithItems(4), dtoGroupWithItems(2), dtoGroupWithItems(1)])
    }

    @Test
    def "message is not released when item group size is less than the detected changes"() {
        given:
        def startedEvent = new StartedOutboundSyncEvent(CRON_JOB_PK, new Date(), 2)

        when:
        releaseStrategy.onEvent(startedEvent)

        then:
        !releaseStrategy.canMessagesBeReleased([defaultDTOGroup])
    }

    @Test
    def "itemCount is updated on new event"() {
        given:
        def count1 = 2
        releaseStrategy.onEvent(new StartedOutboundSyncEvent(CRON_JOB_PK, new Date(), count1))
        and:
        def count2 = 3

        when:
        releaseStrategy.onEvent(new StartedOutboundSyncEvent(CRON_JOB_PK, new Date(), count2))

        then:
        !releaseStrategy.canMessagesBeReleased([defaultDTOGroup, defaultDTOGroup])
        releaseStrategy.canMessagesBeReleased([defaultDTOGroup, defaultDTOGroup, defaultDTOGroup])
    }

    @Test
    def "all items are eventually released when an initial release has reached the batch limit"() {
        given:
        def totalItemsCount = 8
        def startedEvent = new StartedOutboundSyncEvent(CRON_JOB_PK, new Date(), totalItemsCount)
        and:
        outboundSyncConfig.getOutboundBatchLimit() >> 5

        when:
        releaseStrategy.onEvent(startedEvent)

        then: 'configured batch limit is reached'
        releaseStrategy.canMessagesBeReleased([defaultDTOGroup, defaultDTOGroup, defaultDTOGroup, defaultDTOGroup, defaultDTOGroup])
        then: 'total items count is reached'
        releaseStrategy.canMessagesBeReleased([defaultDTOGroup, defaultDTOGroup, defaultDTOGroup])
    }

    @Test
    def "max batch limit is reached per number of groups even when individual item changes is higher"() {
        given:
        def startedEvent = new StartedOutboundSyncEvent(CRON_JOB_PK, new Date(), 24)
        and:
        outboundSyncConfig.getOutboundBatchLimit() >> 4

        when:
        releaseStrategy.onEvent(startedEvent)

        then: 'is not released when groups is lower than batch limit but total items is higher'
        !releaseStrategy.canMessagesBeReleased([dtoGroupWithItems(3), dtoGroupWithItems(3)])
        and: 'is released when the total of groups matches the batch limit'
        releaseStrategy.canMessagesBeReleased([dtoGroupWithItems(3), dtoGroupWithItems(3), dtoGroupWithItems(3), dtoGroupWithItems(3)])
    }

    OutboundItemDTOGroup dtoGroupWithItems(int items) {
        Stub(OutboundItemDTOGroup) {
            getCronJobPk() >> CRON_JOB_PK
            getItemsCount() >> items
        }
    }
}
