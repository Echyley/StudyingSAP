/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.outboundsync.job.impl.handlers

import de.hybris.bootstrap.annotations.UnitTest
import de.hybris.platform.core.PK
import de.hybris.platform.cronjob.enums.CronJobStatus
import de.hybris.platform.outboundsync.events.AbortedOutboundSyncEvent
import de.hybris.platform.testframework.JUnitPlatformSpecification
import org.junit.Test

import static de.hybris.platform.outboundsync.job.impl.OutboundSyncStateBuilder.initialOutboundSyncState

@UnitTest
class AbortedOutboundSyncEventHandlerUnitTest extends JUnitPlatformSpecification {
    private static final def EVENT = new AbortedOutboundSyncEvent(PK.fromLong(1), 3)

    def handler = AbortedOutboundSyncEventHandler.createHandler()

    @Test
    def 'getHandledEventClass returns AbortedOutboundSyncEvent'() {
        expect:
        handler.getHandledEventClass() == AbortedOutboundSyncEvent
    }

    @Test
    def 'AbortedOutboundSyncEvent sets state to ABORTED'() {
        given:
        def initialState = initialOutboundSyncState().build()

        when:
        def updatedState = handler.handle EVENT, initialState

        then:
        updatedState.cronJobStatus == CronJobStatus.ABORTED
    }

    @Test
    def 'AbortedOutboundSyncEvent increments unprocessed items count by the number of changes in the event'() {
        given:
        def initialState = initialOutboundSyncState().withUnprocessedCount(2).build()

        when:
        def updatedState = handler.handle EVENT, initialState

        then:
        updatedState.unprocessedCount == initialState.unprocessedCount + EVENT.changesProcessed
    }
}
