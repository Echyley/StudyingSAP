package de.hybris.platform.outboundservices.event.impl

import de.hybris.bootstrap.annotations.UnitTest
import de.hybris.platform.testframework.JUnitPlatformSpecification
import org.junit.Test
import spock.lang.Issue

@UnitTest
@Issue("https://cxjira.sap.com/browse/IAPI-5212")
class DefaultEventTypeUnitTest extends JUnitPlatformSpecification {

    private static final def TEST_EVENT_TYPE = new DefaultEventType('TEST_EVENT_TYPE')

    @Test
    def "hashcode() equal is #result when second event is #description"() {
        expect:
        (TEST_EVENT_TYPE.hashCode() == event2.hashCode()) == result

        where:
        description                           | event2                                  | result
        'same event object'                   | TEST_EVENT_TYPE                         | true
        'different object with same type'     | new DefaultEventType('TEST_EVENT_TYPE') | true
        'different event with different type' | new DefaultEventType('DIFFERENT')       | false
        'different object'                    | new Integer(1)                          | false
    }

    @Test
    def "equals() is #res when compared to #description"() {
        expect:
        (TEST_EVENT_TYPE == event2) == res

        where:
        description                           | event2                                  | res
        'same event object'                   | TEST_EVENT_TYPE                         | true
        'different object with same type'     | new DefaultEventType('TEST_EVENT_TYPE') | true
        'different event with different type' | new DefaultEventType('DIFFERENT')       | false
        'different object'                    | new Integer(1)                          | false
        'null'                                | null                                    | false
    }

    @Test
    def "expect #description when creating event type with type value #eventTypeValue"() {
        expect:
        event.getType() == result

        where:
        description          | event                      | eventTypeValue    | result
        'Test event type'    | TEST_EVENT_TYPE            | 'TEST_EVENT_TYPE' | 'TEST_EVENT_TYPE'
        'Unknown event type' | new DefaultEventType(null) | null              | 'Unknown'
    }
}
