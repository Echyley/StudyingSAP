/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.outboundsync.job.impl

import de.hybris.bootstrap.annotations.UnitTest
import de.hybris.platform.cronjob.enums.CronJobResult
import de.hybris.platform.cronjob.enums.CronJobStatus
import de.hybris.platform.testframework.JUnitPlatformSpecification
import org.junit.Test

import java.time.Instant

import static OutboundSyncStateBuilder.initialOutboundSyncState

@UnitTest
class OutboundSyncStateUnitTest extends JUnitPlatformSpecification {
    private static final def TOTAL_ITEMS = 5
    private static final def SUCCESS_ITEMS = 1
    private static final def ERROR_ITEMS = 2
    private static final def UNPROCESSED_ITEMS = 1
    private static final def START_TIME = new Date()
    private static final def END_TIME = Date.from Instant.now().plusSeconds(30)

    @Test
    def "builds successfully initial state when provided field is valid"() {
        given:
        def now = Instant.now().toEpochMilli()
        def executionDelta = now + 100
        def syncState = initialOutboundSyncState().build()

        expect:
        with(syncState) {
            totalItemsRequested.empty
            cronJobStatus == CronJobStatus.RUNNING
            cronJobResult == CronJobResult.UNKNOWN
            startTime.time >= now && startTime.time < executionDelta
            endTime == null
            successCount == 0
            errorCount == 0
            unprocessedCount == 0
            !aborted
            !systemicError
        }
    }

    @Test
    def "exception is thrown when #fieldCondition"() {
        when:
        initialOutboundSyncState()
                .withTotalItems(items)
                .withSuccessCount(successes)
                .withErrorCount(errors)
                .withUnprocessedCount(ignored)
                .build()

        then:
        thrown IllegalArgumentException

        where:
        fieldCondition                  | items | successes | errors | ignored
        'total items is negative'       | -1    | 0         | 0      | 0
        'success items is negative'     | 0     | -1        | 0      | 0
        'error items is negative'       | 0     | 0         | -1     | 0
        'unprocessed items is negative' | 0     | 0         | 0      | -1
    }

    @Test
    def "job status is RUNNING when total number of items is unknown and #field is applied"() {
        given: 'total number of successful items, error items and unprocessed items is less then total items count'
        def state = initialOutboundSyncState()
                .withSuccessCount(success)
                .withErrorCount(error)
                .withUnprocessedCount(ignored)
                .build()

        expect:
        state.cronJobStatus == CronJobStatus.RUNNING

        where:
        field               | success | error | ignored
        'success count'     | 1       | 0     | 0
        'error count'       | 0       | 1     | 0
        'unprocessed count' | 0       | 0     | 1
    }

    @Test
    def "job status is RUNNING while #condition"() {
        given: 'total number of successful items, error items and unprocessed items is less then total items count'
        def state = initialOutboundSyncState()
                .withTotalItems(TOTAL_ITEMS)
                .withSuccessCount(success)
                .withErrorCount(error)
                .withUnprocessedCount(ignored)
                .build()

        expect:
        state.cronJobStatus == CronJobStatus.RUNNING

        where:
        condition                 | success | error | ignored
        'no items processed'      | 0       | 0     | 0
        'not all items processed' | 1       | 1     | 1
    }

    @Test
    def "job status is #status when abort flag #aborted"() {
        given: 'total number of successful items, error items and unprocessed items equal the total items count'
        def state = initialOutboundSyncState()
                .withTotalItems(TOTAL_ITEMS)
                .withSuccessCount(3)
                .withErrorCount(1)
                .withUnprocessedCount(1)
                .withAborted(aborted)
                .build()

        expect:
        state.cronJobStatus == status
        state.endTime

        where:
        aborted | status
        true    | CronJobStatus.ABORTED
        false   | CronJobStatus.FINISHED
    }

    @Test
    def "job result is #result when #condition"() {
        given:
        def state = initialOutboundSyncState()
                .withSuccessCount(success)
                .withErrorCount(error)
                .withSystemicError(systemError)
                .build()

        expect:
        state.cronJobResult == result

        where:
        condition                         | success | error | systemError | result
        'no items processed'              | 0       | 0     | false       | CronJobResult.UNKNOWN
        'no error items present'          | 1       | 0     | false       | CronJobResult.SUCCESS
        'at least one error item present' | 1       | 1     | false       | CronJobResult.ERROR
        'systemic error occurred'         | 0       | 1     | true        | CronJobResult.FAILURE
    }

    @Test
    def "allItemsProcessed is #res when totalItems is #total, success is 3, errors is 2, and ignored is 1"() {
        given:
        def state = initialOutboundSyncState()
                .withTotalItems(total)
                .withSuccessCount(3)
                .withErrorCount(2)
                .withUnprocessedCount(1)
                .build()

        expect:
        state.allItemsProcessed == res

        where:
        total | res
        7     | false
        6     | true
        5     | true
    }

    @Test
    def 'allItemsProcessed is false when totalItems is unknown and success, errors, and ignored items applied'() {
        given:
        def state = initialOutboundSyncState()
                .withSuccessCount(30)
                .withErrorCount(20)
                .withUnprocessedCount(10)
                .build()

        expect:
        !state.allItemsProcessed
    }

    @Test
    def 'state is finished when there are no items to process'() {
        given:
        def state = initialOutboundSyncState()
                .withTotalItems(0)
                .build()

        expect:
        state.allItemsProcessed
        state.cronJobStatus == CronJobStatus.FINISHED
        state.cronJobResult == CronJobResult.SUCCESS
    }

    @Test
    def 'remaining items count is the difference between total items number and already processed items'() {
        given:
        def errorCnt = 1
        def successCnt = 2
        def ignoredCnt = 3
        def totalCnt = 10
        def state = initialOutboundSyncState().withTotalItems(totalCnt)
                .withSuccessCount(successCnt)
                .withErrorCount(errorCnt)
                .withUnprocessedCount(ignoredCnt)
                .build()

        expect:
        state.remainingItemsCount.present
        state.remainingItemsCount.asInt == totalCnt - successCnt - errorCnt - ignoredCnt
    }

    @Test
    def 'remaining items count is empty when total items number is not known'() {
        given:
        def state = initialOutboundSyncState().withSuccessCount(1)
                .withErrorCount(2)
                .withUnprocessedCount(3)
                .build()

        expect:
        state.remainingItemsCount.empty
    }

    @Test
    def "two outbound sync states are not equals when #condition"() {
        given:
        def state1 = outboundSyncStateBuilder().build()

        expect:
        state1 != state2

        where:
        condition                          | state2
        "totalItemsRequested is different" | outboundSyncStateBuilder().withTotalItems(35).build()
        "startDate is different"           | outboundSyncStateBuilder().withStartTime(END_TIME).build()
        "successCount is different"        | outboundSyncStateBuilder().withSuccessCount(77777).build()
        "errorCount is different"          | outboundSyncStateBuilder().withErrorCount(9999999).build()
        "postAbortCount is different"      | outboundSyncStateBuilder().withUnprocessedCount(19283746).build()
        "endDate is different"             | outboundSyncStateBuilder().withEndTime(START_TIME).build()
        "abort is different"               | outboundSyncStateBuilder().withAborted(true).build()
        "systemicError is different"       | outboundSyncStateBuilder().withSystemicError(true).build()
    }

    @Test
    def "two outbound sync states are equals"() {
        given:
        def state1 = outboundSyncState()
        def state2 = outboundSyncState()

        expect:
        state1 == state2
        and:
        state1 == state1
    }

    @Test
    def "two outbound sync states have different hashCodes when #condition"() {
        given:
        def state1 = outboundSyncState()

        expect:
        state1.hashCode() != state2.hashCode()

        where:
        condition                          | state2
        "totalItemsRequested is different" | outboundSyncStateBuilder().withTotalItems(35).build()
        "startDate is different"           | outboundSyncStateBuilder().withStartTime(END_TIME).build()
        "successCount is different"        | outboundSyncStateBuilder().withSuccessCount(77777).build()
        "errorCount is different"          | outboundSyncStateBuilder().withErrorCount(9999999).build()
        "postAbortCount is different"      | outboundSyncStateBuilder().withUnprocessedCount(19283746).build()
        "endDate is different"             | outboundSyncStateBuilder().withEndTime(START_TIME).build()
        "abort is different"               | outboundSyncStateBuilder().withAborted(true).build()
        "systemicError is different"       | outboundSyncStateBuilder().withSystemicError(true).build()
    }

    @Test
    def "two outbound sync states have the same hashCode"() {
        given:
        def state1 = outboundSyncState()
        def state2 = outboundSyncState()

        expect:
        state1.hashCode() == state2.hashCode()
    }

    @Test
    def 'getStartTime() does not leak immutability'() {
        given:
        def original = outboundSyncStateBuilder().withStartTime(START_TIME).build()

        when:
        original.startTime.setTime(START_TIME.time - 1000)

        then:
        original.startTime == START_TIME
    }

    @Test
    def 'getEndTime() does not leak immutability'() {
        given:
        def original = outboundSyncStateBuilder().withEndTime(END_TIME).build()

        when:
        original.endTime.setTime(END_TIME.time - 1000)

        then:
        original.endTime == END_TIME
    }

    @Test
    def 'changes in the builder Date field values do not leak to previously created items'() {
        given: 'a date value'
        def date = new Date()
        and: 'a state is created '
        def state = initialOutboundSyncState()
                .withStartTime(date)
                .withEndTime(date)
                .build()

        when: 'the date is mutated'
        date.time = date.time - 60_000

        then: 'the state did not mutate'
        state.startTime != date
        state.endTime != date
    }

    @Test
    def 'initial state can be changed through the builder'() {
        given:
        def initState = initialOutboundSyncState().build()
        and:
        def startTime = Instant.now().plusSeconds(30)
        def endTime = Instant.now().plusSeconds(45)

        when:
        def changedState = OutboundSyncStateBuilder.from(initState)
                .withTotalItems(TOTAL_ITEMS)
                .withSuccessCount(1)
                .withErrorCount(1)
                .withUnprocessedCount(1)
                .withStartTime(startTime)
                .withEndTime(endTime)
                .withAborted(true)
                .withSystemicError(true)
                .build()

        then:
        !changedState.is(initState)
        with(changedState) {
            changedState.totalItemsRequested != initState.totalItemsRequested
            changedState.successCount != initState.successCount
            changedState.errorCount != initState.errorCount
            changedState.unprocessedCount != initState.unprocessedCount
            changedState.startTime == Date.from(startTime)
            changedState.endTime == Date.from(endTime)
            changedState.aborted != initState.aborted
            changedState.systemicError != initState.systemicError
        }
    }

    @Test
    def 'totalItemsCount cannot be changed once set'() {
        given:
        def stateWithTotalCounts = initialOutboundSyncState().withTotalItems(TOTAL_ITEMS).build()

        when:
        def updatedState = OutboundSyncStateBuilder.from(stateWithTotalCounts).withTotalItems(TOTAL_ITEMS + 1).build()

        then:
        updatedState.totalItemsRequested == OptionalInt.of(TOTAL_ITEMS)
    }

    @Test
    def 'startDate cannot be reset to null'() {
        given:
        def initialState = initialOutboundSyncState().build()

        when:
        def updatedState = OutboundSyncStateBuilder.from(initialState).withStartTime(null as Date).build()

        then:
        updatedState.startTime == initialState.startTime
    }

    @Test
    def 'cannot create a state from a null state'() {
        when:
        OutboundSyncStateBuilder.from(null)

        then:
        thrown IllegalArgumentException
    }

    @Test
    def "can be presented as PerformResult: #result, #status"() {
        given:
        def performResult = state.build().asPerformResult()

        expect:
        performResult.result == result
        performResult.status == status

        where:
        state                                                            | result                | status
        initialOutboundSyncState()                                       | CronJobResult.UNKNOWN | CronJobStatus.RUNNING
        initialOutboundSyncState().withSuccessCount(1)                   | CronJobResult.SUCCESS | CronJobStatus.RUNNING
        initialOutboundSyncState().withSuccessCount(1).withErrorCount(1) | CronJobResult.ERROR   | CronJobStatus.RUNNING
        initialOutboundSyncState().withSuccessCount(1).withTotalItems(1) | CronJobResult.SUCCESS | CronJobStatus.FINISHED
        initialOutboundSyncState().withErrorCount(1).withTotalItems(1)   | CronJobResult.ERROR   | CronJobStatus.FINISHED
        initialOutboundSyncState().withTotalItems(0)                     | CronJobResult.SUCCESS | CronJobStatus.FINISHED
        initialOutboundSyncState().withSystemicError(true)               | CronJobResult.FAILURE | CronJobStatus.FINISHED
        initialOutboundSyncState().withAborted(true)                     | CronJobResult.UNKNOWN | CronJobStatus.ABORTED
        initialOutboundSyncState().withSuccessCount(1).withAborted(true) | CronJobResult.SUCCESS | CronJobStatus.ABORTED
        initialOutboundSyncState().withErrorCount(1).withAborted(true)   | CronJobResult.ERROR   | CronJobStatus.ABORTED
    }

    @Test
    def "state changed is #res when #condition"() {
        given:
        def prevState = prevStateBuilder.build()
        def newState = newStateBuilder.build()

        expect:
        newState.isChangedFrom(prevState) == res

        where:
        condition                       | res   | prevStateBuilder                               | newStateBuilder
        'total items is known'          | true  | initialOutboundSyncState()                     | initialOutboundSyncState().withTotalItems(1)
        'result changes to SUCCESS'     | true  | initialOutboundSyncState()                     | initialOutboundSyncState().withSuccessCount(1)
        'result changes to ERROR'       | true  | initialOutboundSyncState().withSuccessCount(1) | initialOutboundSyncState().withSuccessCount(1).withErrorCount(1)
        'job is finished'               | true  | initialOutboundSyncState().withTotalItems(1)   | initialOutboundSyncState().withUnprocessedCount(1)
        'error count incremented'       | false | initialOutboundSyncState().withErrorCount(1)   | initialOutboundSyncState().withErrorCount(2)
        'success count incremented'     | false | initialOutboundSyncState().withErrorCount(1)   | initialOutboundSyncState().withErrorCount(1).withSuccessCount(1)
        'unprocessed count incremented' | false | initialOutboundSyncState()                     | initialOutboundSyncState().withUnprocessedCount(1)
        'start time changed'            | false | initialOutboundSyncState()                     | initialOutboundSyncState().withStartTime(new Date())
        'end time changed'              | false | initialOutboundSyncState()                     | initialOutboundSyncState().withEndTime(new Date())
        'aborted changed'               | true  | initialOutboundSyncState()                     | initialOutboundSyncState().withAborted(true)
        'systemicError changed'         | true  | initialOutboundSyncState()                     | initialOutboundSyncState().withSystemicError(true)
    }

    @Test
    def "the state is not final when total number of items is #totalCondition and #itemsCondition items processed"() {
        given:
        def state = builder.build()

        expect:
        !state.final

        where:
        builder                                                        | totalCondition | itemsCondition
        initialOutboundSyncState()                                     | 'unknown'      | 'no'
        initialOutboundSyncState().withSuccessCount(1)                 | 'unknown'      | 'some'
        initialOutboundSyncState().withTotalItems(2).withErrorCount(1) | 'known'        | 'not all'
    }

    @Test
    def "the state is final when #condition"() {
        given:
        def state = builder.build()

        expect:
        state.final

        where:
        builder                                                          | condition
        initialOutboundSyncState().withAborted(true)                     | 'the sync is aborted'
        initialOutboundSyncState().withSystemicError(true)               | 'a systemic error happened'
        initialOutboundSyncState().withTotalItems(1).withSuccessCount(1) | 'all items processed'
    }

    @Test
    def 'building states by subsequent calls to the same builder produces different instances of the state'() {
        given:
        def builder = initialOutboundSyncState()

        when:
        def state1 = builder.build()
        def state2 = builder.build()

        then:
        !state1.is(state2)
    }

    @Test
    def 'changes in builder between the build() calls do not leak to previously created items'() {
        given:
        def now = Instant.now()
        def builder = initialOutboundSyncState()
                .withStartTime(Date.from(now))
                .withEndTime(Date.from(now.plusSeconds(1)))
                .withSuccessCount(1)
                .withErrorCount(2)
                .withUnprocessedCount(3)
        def state1 = builder.build()

        when:
        def state2 = builder
                .withStartTime(Date.from(now.plusSeconds(10)))
                .withEndTime(Date.from(now.plusSeconds(20)))
                .withSuccessCount(10)
                .withErrorCount(20)
                .withUnprocessedCount(30)
                .build()

        then:
        state1.startTime != state2.startTime
        state1.endTime != state2.endTime
        state1.successCount != state2.successCount
        state1.errorCount != state2.errorCount
        state1.unprocessedCount != state2.unprocessedCount
    }

    def outboundSyncState() {
        outboundSyncStateBuilder().build()
    }

    private static OutboundSyncStateBuilder outboundSyncStateBuilder() {
        initialOutboundSyncState()
                .withSuccessCount(SUCCESS_ITEMS)
                .withErrorCount(ERROR_ITEMS)
                .withUnprocessedCount(UNPROCESSED_ITEMS)
                .withStartTime(START_TIME)
                .withEndTime(END_TIME)
    }
}
