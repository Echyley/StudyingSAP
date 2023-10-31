/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.outboundsync.job.impl.handlers

import de.hybris.bootstrap.annotations.UnitTest
import de.hybris.platform.core.PK
import de.hybris.platform.cronjob.enums.CronJobResult
import de.hybris.platform.outboundsync.events.SystemErrorOutboundSyncEvent
import de.hybris.platform.testframework.JUnitPlatformSpecification
import org.junit.Test

import static de.hybris.platform.outboundsync.job.impl.OutboundSyncStateBuilder.initialOutboundSyncState

@UnitTest
class SystemErrorOutboundSyncEventHandlerUnitTest extends JUnitPlatformSpecification {
    private static final def EVENT = new SystemErrorOutboundSyncEvent(PK.fromLong(1), 2)

    def handler = SystemErrorOutboundSyncEventHandler.createHandler()

    @Test
    def 'getHandledEventClass returns SystemErrorOutboundSyncEvent'() {
        expect:
        handler.getHandledEventClass() == SystemErrorOutboundSyncEvent
    }

    @Test
    def 'SystemErrorOutboundSyncEvent changes result to FAILURE'() {
        given:
        def initialState = initialOutboundSyncState().build()

        when:
        def updatedState = handler.handle EVENT, initialState

        then:
        updatedState.cronJobResult == CronJobResult.FAILURE
    }

    @Test
    def 'SystemErrorOutboundSyncEvent increments unprocessed items count by the number of changes in the event'() {
        given:
        def initialState = initialOutboundSyncState().withUnprocessedCount(1).build()

        when:
        def updatedState = handler.handle EVENT, initialState

        then:
        updatedState.unprocessedCount == initialState.unprocessedCount + EVENT.changesProcessed
    }
}
