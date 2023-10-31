/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.outboundsync.job.impl

import de.hybris.bootstrap.annotations.UnitTest
import de.hybris.platform.core.PK
import de.hybris.platform.cronjob.enums.CronJobResult
import de.hybris.platform.cronjob.enums.CronJobStatus
import de.hybris.platform.integrationservices.util.EventualCondition
import de.hybris.platform.outboundsync.events.AbortedOutboundSyncEvent
import de.hybris.platform.outboundsync.events.CompletedOutboundSyncEvent
import de.hybris.platform.outboundsync.events.IgnoredOutboundSyncEvent
import de.hybris.platform.outboundsync.events.OutboundSyncEvent
import de.hybris.platform.outboundsync.events.StartedOutboundSyncEvent
import de.hybris.platform.outboundsync.events.SystemErrorOutboundSyncEvent
import de.hybris.platform.outboundsync.model.OutboundSyncCronJobModel
import de.hybris.platform.testframework.JUnitPlatformSpecification
import org.junit.Test
import spock.lang.Shared

import javax.validation.constraints.NotNull
import java.util.concurrent.CountDownLatch

@UnitTest
class OutboundSyncJobStateAggregatorUnitTest extends JUnitPlatformSpecification {
    private static final def JOB_PK = PK.fromLong 8855
    private static final def STARTED_EVENT = startedEvent(1)
    private static final def COMPLETED_EVENT = completedSuccessfullyEvent(1)
    private static final def ABORTED_EVENT = abortedEvent(1)
    private static final def IGNORED_EVENT = ignoredEvent()
    private static final def SYSTEM_ERROR_EVENT = systemErrorEvent(1)

    @Shared
    def CONTEXT_JOB = Stub(OutboundSyncCronJobModel) {
        getPk() >> JOB_PK
    }
    def contextOutboundSyncJob = OutboundSyncJobStateAggregator.create CONTEXT_JOB

    @Test
    def 'has ID that is PK of the context job'() {
        expect:
        contextOutboundSyncJob.id == CONTEXT_JOB.pk
    }

    @Test
    def "cannot be created with null parameter"() {
        when:
        OutboundSyncJobStateAggregator.create(null)

        then:
        def e = thrown IllegalArgumentException
        e.message.contains OutboundSyncCronJobModel.class.getSimpleName()
    }

    @Test
    def 'initial status is RUNNING and result is UNKNOWN when the aggregator is created'() {
        expect:
        with(contextOutboundSyncJob.currentState) {
            cronJobStatus == CronJobStatus.RUNNING
            cronJobResult == CronJobResult.UNKNOWN
        }
    }

    @Test
    def "non-final #event.class.simpleName does not change the job status"() {
        when:
        contextOutboundSyncJob.applyEvent event

        then:
        with(contextOutboundSyncJob.currentState) {
            cronJobStatus == CronJobStatus.RUNNING
            cronJobResult == result
        }

        where:
        event           | result
        STARTED_EVENT   | CronJobResult.UNKNOWN
        COMPLETED_EVENT | CronJobResult.SUCCESS
        IGNORED_EVENT   | CronJobResult.SUCCESS
    }

    @Test
    def "final #event.class.simpleName changes the job status and result"() {
        given: 'state is one event away from completion'
        contextOutboundSyncJob.applyEvent initEvent

        when:
        contextOutboundSyncJob.applyEvent event

        then:
        with(contextOutboundSyncJob.currentState) {
            cronJobStatus == status
            cronJobResult == result
        }

        where:
        initEvent       | event              | status                 | result
        COMPLETED_EVENT | STARTED_EVENT      | CronJobStatus.FINISHED | CronJobResult.SUCCESS
        STARTED_EVENT   | COMPLETED_EVENT    | CronJobStatus.FINISHED | CronJobResult.SUCCESS
        STARTED_EVENT   | ABORTED_EVENT      | CronJobStatus.ABORTED  | CronJobResult.UNKNOWN
        STARTED_EVENT   | IGNORED_EVENT      | CronJobStatus.FINISHED | CronJobResult.SUCCESS
        STARTED_EVENT   | SYSTEM_ERROR_EVENT | CronJobStatus.FINISHED | CronJobResult.FAILURE
    }

    @Test
    def "job state does not change when the event does not have an applicable handler"() {
        given:
        def origState = contextOutboundSyncJob.currentState

        when:
        contextOutboundSyncJob.applyEvent new UnknownOutboundSyncEvent(JOB_PK)

        then:
        contextOutboundSyncJob.currentState.is origState
    }

    @Test
    def 'aggregates multiple events til final state'() {
        when:
        contextOutboundSyncJob.applyEvent ignoredEvent()
        then:
        contextOutboundSyncJob.currentState.cronJobStatus == CronJobStatus.RUNNING

        when:
        contextOutboundSyncJob.applyEvent completedSuccessfullyEvent(3)
        then:
        contextOutboundSyncJob.currentState.cronJobStatus == CronJobStatus.RUNNING

        when:
        contextOutboundSyncJob.applyEvent startedEvent(6)
        then:
        contextOutboundSyncJob.currentState.cronJobStatus == CronJobStatus.RUNNING

        when:
        contextOutboundSyncJob.applyEvent completedWithErrorEvent(2)
        then:
        contextOutboundSyncJob.currentState.cronJobStatus == CronJobStatus.FINISHED
    }

    @Test
    def "notifies state observers when #condition"() {
        given: 'job state is initialized with an event'
        contextOutboundSyncJob.applyEvent initEvent
        def prevState = contextOutboundSyncJob.currentState
        and: 'observers added'
        def observer1 = Mock OutboundSyncStateObserver
        contextOutboundSyncJob.addStateObserver observer1
        def observer2 = Mock OutboundSyncStateObserver
        contextOutboundSyncJob.addStateObserver observer2

        when: 'an event changing state is applied'
        contextOutboundSyncJob.applyEvent stateChangingEvent

        then: 'all observers are notified'
        1 * observer1.stateChanged(CONTEXT_JOB, { it != prevState })
        1 * observer2.stateChanged(CONTEXT_JOB, { it != prevState })

        where:
        condition                        | initEvent                     | stateChangingEvent
        'Started event received'         | ignoredEvent()                | startedEvent(100)
        'Aborted event received'         | ignoredEvent()                | abortedEvent(0)
        'SystemError event received'     | ignoredEvent()                | systemErrorEvent(0)
        'job result changes to success'  | startedEvent(10)              | completedSuccessfullyEvent(1)
        'job result changes to error'    | completedSuccessfullyEvent(1) | completedWithErrorEvent(1)
        'job status changes to finished' | startedEvent(1)               | ignoredEvent()
    }

    @Test
    def "does not notify state observers when #condition"() {
        given: 'job state is initialized with an event'
        contextOutboundSyncJob.applyEvent initEvent
        and: 'an observer added'
        def observer = Mock OutboundSyncStateObserver
        contextOutboundSyncJob.addStateObserver observer

        when: 'an event changing state is applied'
        contextOutboundSyncJob.applyEvent stateChangingEvent

        then: 'observers not notified'
        0 * observer._

        where:
        condition                   | initEvent                     | stateChangingEvent
        'success count incremented' | completedSuccessfullyEvent(2) | completedSuccessfullyEvent(1)
        'error count incremented'   | completedWithErrorEvent(1)    | completedWithErrorEvent(2)
    }

    @Test
    def 'keeps notifying observers even when the first one throws unexpected exception'() {
        given: 'job state is initialized with an event'
        contextOutboundSyncJob.applyEvent startedEvent(1)
        def prevState = contextOutboundSyncJob.currentState
        and: 'observers added'
        def observer1 = Stub(OutboundSyncStateObserver) {
            stateChanged(CONTEXT_JOB, _ as OutboundSyncState) >> { throw new RuntimeException() }
        }
        contextOutboundSyncJob.addStateObserver observer1
        def observer2 = Mock OutboundSyncStateObserver
        contextOutboundSyncJob.addStateObserver observer2

        when: 'an event changing state is applied'
        contextOutboundSyncJob.applyEvent ignoredEvent()

        then: 'second observer is still notified'
        1 * observer2.stateChanged(CONTEXT_JOB, { it != prevState })
    }

    @Test
    def 'final state waits for both started and completed events and finishes in a FINISHED status'() {
        given: 'the events will be processed in a separate thread after a delay to cause wait of changing state to final'
        def job = OutboundSyncJobStateAggregator.create(CONTEXT_JOB)
        new EventThread(job, STARTED_EVENT).start()
        new EventThread(job, COMPLETED_EVENT).start()

        when: 'wait for finished state to occur'
        def finalState = job.getFinalState()

        then: 'final state should have a finished status'
        finalState.getCronJobStatus() == CronJobStatus.FINISHED
        finalState.getCronJobResult() == CronJobResult.SUCCESS
        allItemsAreProcessed(finalState)
    }

    @Test
    def 'final state waits for completed event and finishes in a FINISHED status'() {
        given: 'outboundSyncJob is initialize. Completed and started events are called'
        def job = OutboundSyncJobStateAggregator.create(CONTEXT_JOB)
        new EventThread(job, COMPLETED_EVENT).start()
        job.applyEvent STARTED_EVENT

        when: 'wait for finished state to occur'
        def finalState = job.getFinalState()

        then: 'final state should have a finished status'
        finalState.getCronJobStatus() == CronJobStatus.FINISHED
        finalState.getCronJobResult() == CronJobResult.SUCCESS
        allItemsAreProcessed(finalState)
    }

    @Test
    def 'final state waits for started event and finishes in a FINISHED status'() {
        given: 'outboundSyncJob is initialize. Started and completed events are called'
        def job = OutboundSyncJobStateAggregator.create(CONTEXT_JOB)
        new EventThread(job, STARTED_EVENT).start()
        job.applyEvent COMPLETED_EVENT

        when: 'wait for finished state to occur'
        def finalState = job.getFinalState()

        then: 'final state should have a finished status'
        finalState.getCronJobStatus() == CronJobStatus.FINISHED
        finalState.getCronJobResult() == CronJobResult.SUCCESS
        allItemsAreProcessed(finalState)
    }

    @Test
    def 'final state does not wait if the state is FINISHED already'() {
        given: 'started and completed events result in finished status before the final state is called'
        def job = OutboundSyncJobStateAggregator.create(CONTEXT_JOB)
        job.applyEvent STARTED_EVENT
        job.applyEvent COMPLETED_EVENT

        when: 'wait for finished state to occur'
        def finalState = job.getFinalState()

        then: 'final state should have a finished status'
        finalState.getCronJobStatus() == CronJobStatus.FINISHED
        finalState.getCronJobResult() == CronJobResult.SUCCESS
        allItemsAreProcessed(finalState)
    }

    @Test
    def 'final state reports a systematic error if await for final state was interrupted'() {
        given: 'job has a latch that throws InterruptedException'
        def job = OutboundSyncJobStateAggregator.create CONTEXT_JOB, createFailingLatch()

        when: 'wait for failure state to occur'
        def finalState = job.getFinalState()

        then: 'final state should have a systematic error with a FAILURE result and FINISHED status '
        finalState.isSystemicError()
        finalState.getCronJobResult() == CronJobResult.FAILURE
        finalState.getCronJobStatus() == CronJobStatus.FINISHED
    }

    @Test
    def 'stop() changes state to aborted and correctly reports unprocessed items when total number of items is known'() {
        given: 'job state was initialized and some, but not all, items processed'
        def totalItemsCount = 7
        def errorItemsCount = 1
        def syncedItemsCount = 2
        applyEventsToContextJob startedEvent(totalItemsCount), completedWithErrorEvent(errorItemsCount),
                completedSuccessfullyEvent(syncedItemsCount)
        and: 'there is an observer'
        def observer = Mock OutboundSyncStateObserver
        contextOutboundSyncJob.addStateObserver observer

        when: 'the job is stopped'
        contextOutboundSyncJob.stop()

        then: 'the state is aborted'
        with(contextOutboundSyncJob.currentState) {
            cronJobStatus == CronJobStatus.ABORTED
            unprocessedCount == totalItemsCount - errorItemsCount - syncedItemsCount
        }
        and: 'the observer is notified about the state change'
        1 * observer.stateChanged(CONTEXT_JOB, { it.cronJobStatus == CronJobStatus.ABORTED })
    }

    @Test
    def 'stop() changes state to ABORTED when total number of items is unknown'() {
        given: 'items processed without knowing the total number of items'
        def syncedItemsCount = 2
        contextOutboundSyncJob.applyEvent completedSuccessfullyEvent(syncedItemsCount)
        and: 'there is an observer'
        def observer = Mock OutboundSyncStateObserver
        contextOutboundSyncJob.addStateObserver observer

        when: 'the job is stopped'
        contextOutboundSyncJob.stop()

        then: 'the state is aborted'
        with(contextOutboundSyncJob.currentState) {
            cronJobStatus == CronJobStatus.ABORTED
            successCount == syncedItemsCount
            unprocessedCount == 0
            totalItemsRequested.empty
        }
        and: 'the observer is notified about the state change'
        1 * observer.stateChanged(CONTEXT_JOB, { it.cronJobStatus == CronJobStatus.ABORTED })
    }

    private void applyEventsToContextJob(OutboundSyncEvent... events) {
        events.each { contextOutboundSyncJob.applyEvent it }
    }

    static StartedOutboundSyncEvent startedEvent(int count) {
        new StartedOutboundSyncEvent(JOB_PK, new Date(), count)
    }

    static CompletedOutboundSyncEvent completedSuccessfullyEvent(int count) {
        new CompletedOutboundSyncEvent(JOB_PK, true, count)
    }

    static CompletedOutboundSyncEvent completedWithErrorEvent(int count) {
        new CompletedOutboundSyncEvent(JOB_PK, false, count)
    }

    static IgnoredOutboundSyncEvent ignoredEvent() {
        new IgnoredOutboundSyncEvent(JOB_PK)
    }

    static AbortedOutboundSyncEvent abortedEvent(int count) {
        new AbortedOutboundSyncEvent(JOB_PK, count)
    }

    static SystemErrorOutboundSyncEvent systemErrorEvent(int count) {
        new SystemErrorOutboundSyncEvent(JOB_PK, count)
    }

    private CountDownLatch createFailingLatch() {
        Stub(CountDownLatch) {
            await() >> { throw new InterruptedException() }
        }
    }

    private static boolean allItemsAreProcessed(final OutboundSyncState state) {
        def totalCount = state.totalItemsRequested.empty ? 0 : state.totalItemsRequested.getAsInt()
        totalCount == state.unprocessedCount + state.successCount + state.errorCount
    }

    private static class UnknownOutboundSyncEvent extends OutboundSyncEvent {
        UnknownOutboundSyncEvent(final PK cronJobPk) {
            super(cronJobPk)
        }
    }

    def class EventThread extends Thread {
        private final OutboundSyncJob job
        private final OutboundSyncEvent event

        EventThread(@NotNull OutboundSyncJob job, @NotNull final OutboundSyncEvent event) {
            this.job = job
            this.event = event
        }

        @Override
        void run() {
            applyEvent()
        }

        void applyEvent() {
            EventualCondition.pause(500)
            job.applyEvent event
        }
    }
}