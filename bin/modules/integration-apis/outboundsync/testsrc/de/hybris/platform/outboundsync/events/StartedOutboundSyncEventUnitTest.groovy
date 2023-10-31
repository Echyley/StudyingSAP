/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.outboundsync.events

import de.hybris.bootstrap.annotations.UnitTest
import de.hybris.platform.core.PK
import de.hybris.platform.testframework.JUnitPlatformSpecification
import org.junit.Test
import spock.lang.Unroll

@UnitTest
class StartedOutboundSyncEventUnitTest extends JUnitPlatformSpecification {
    private static final def aCronJobPk = PK.fromLong(1234)
    private static final def anotherCronJobPk = PK.fromLong(4321)
    private static final def TIME = new Date()

    @Test
    @Unroll
    def "equals is #expectedEquals when cronjob 1 has pk 1234 and number of items is 2 and cronjob 2 has pk #jobPk and number of items is #itemsCount"() {
        given:
        def event1 = new StartedOutboundSyncEvent(aCronJobPk, TIME, 2)
        def event2 = new StartedOutboundSyncEvent(jobPk, time, itemsCount)

        expect:
        (event1 == event2) == expectedEquals

        where:
        jobPk            | time                                       | itemsCount | expectedEquals
        aCronJobPk       | TIME                                       | 2          | true
        anotherCronJobPk | TIME                                       | 2          | false
        aCronJobPk       | TIME                                       | 3          | false
        aCronJobPk       | Date.from(TIME.toInstant().plusSeconds(1)) | 2          | false
    }

    @Test
    def "setting startTime does not leak reference"() {
        given:
        def startTime = new Date()
        def startTimeCopy = new Date(startTime.getTime())
        def event = new StartedOutboundSyncEvent(aCronJobPk, startTime, 5)

        when:
        startTime.setTime(startTime.getTime()+1)
        
        then:
        event.startTime == startTimeCopy
    }

    @Test
    def "getting startTime does not leak reference"() {
        given:
        def startTime = new Date()
        def event = new StartedOutboundSyncEvent(aCronJobPk, startTime, 5)
        and:
        def copy = event.startTime

        when:
        copy.setTime(copy.getTime()+1)

        then:
        event.startTime == startTime
    }

    @Test
    def "equals is true when events are the same object"() {
        given:
        def event1 = new StartedOutboundSyncEvent(aCronJobPk, TIME, 5)

        expect:
        event1 == event1
    }

    @Test
    def 'is not equal to event of different type'() {
        given:
        def other = new CompletedOutboundSyncEvent(aCronJobPk, true, 5)

        expect:
        new StartedOutboundSyncEvent(aCronJobPk, TIME, 5) != other
    }

    @Test
    def "equals is false when comparing to null event"() {
        expect:
        !(new StartedOutboundSyncEvent(aCronJobPk, TIME, 5) == null)
    }

    @Test
    def "exception is thrown when instantiating the event with null #param"() {
        when:
        new StartedOutboundSyncEvent(pk, time, 1)

        then:
        thrown IllegalArgumentException

        where:
        param        | pk         | time
        'cronjob pk' | null       | TIME
        'start time' | aCronJobPk | null
    }

    @Test
    def "hashCode is #expectedEquals when cronjob 1 has pk 1234 and number of items is 2 and cronjob 2 has pk #jobPk and number of items is #itemsCount"() {
        given:
        def event1 = new StartedOutboundSyncEvent(aCronJobPk, TIME, 2)
        def event2 = new StartedOutboundSyncEvent(jobPk, time, itemsCount)

        expect:
        (event1.hashCode() == event2.hashCode()) == expectedEquals

        where:
        jobPk            | time                                      | itemsCount | expectedEquals
        aCronJobPk       | TIME                                      | 2          | true
        anotherCronJobPk | TIME                                      | 2          | false
        aCronJobPk       | TIME                                      | 3          | false
        aCronJobPk       | Date.from(TIME.toInstant().plusMillis(1)) | 2          | false
    }
}

