/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.dataimportcommons.log

import de.hybris.bootstrap.annotations.UnitTest
import de.hybris.platform.testframework.JUnitPlatformSpecification
import org.junit.Rule
import org.junit.Test
import org.slf4j.Logger
import org.slf4j.Marker

@UnitTest
class LogUnitTest extends JUnitPlatformSpecification {
    private static final def LOGGER_CLASS = LogUnitTest
    private static final def OBJECT = 'String'
    private static final def EXCEPTION = new Exception('Throwable')

    @Rule
    public LogProbe probe = LogProbe.create(LOGGER_CLASS)

    @Test
    def "#method(String) prints message as is"() {
        given:
        def msg = 'String'

        when:
        logger(logLevel)."$method" msg

        then:
        with(theOnlyLoggedRecord()) {
            level == logLevel
            message == msg
            !exception
        }

        where:
        method  | logLevel
        'info'  | LogLevel.INFO
        'warn'  | LogLevel.WARN
        'error' | LogLevel.ERROR
        'debug' | LogLevel.DEBUG
        'trace' | LogLevel.TRACE
    }

    @Test
    def "#method(Marker, String) prints message as is"() {
        given:
        def msg = 'String'

        when:
        logger(logLevel)."$method" Stub(Marker), msg

        then:
        with(theOnlyLoggedRecord()) {
            level == logLevel
            message == msg
            !exception
        }

        where:
        method  | logLevel
        'info'  | LogLevel.INFO
        'warn'  | LogLevel.WARN
        'error' | LogLevel.ERROR
        'debug' | LogLevel.DEBUG
        'trace' | LogLevel.TRACE
    }

    @Test
    def "#method('{}', Throwable) does not print stack trace when exception has a placeholder in the message"() {
        given:
        def msg = 'Message {}'

        when:
        logger(LogLevel.TRACE)."$method" msg, EXCEPTION as Object

        then:
        with(theOnlyLoggedRecord()) {
            level == logLevel
            message == "Message $EXCEPTION"
            !exception
        }

        where:
        method  | logLevel
        'info'  | LogLevel.INFO
        'warn'  | LogLevel.WARN
        'error' | LogLevel.ERROR
        'debug' | LogLevel.DEBUG
        'trace' | LogLevel.TRACE
    }

    @Test
    def "#method(Marker, '{}', Throwable) does not print stack trace when exception has a prlacehorlder in the message"() {
        given:
        def msg = 'Message {}'

        when:
        logger(LogLevel.TRACE)."$method" Stub(Marker), msg, EXCEPTION as Object

        then:
        with(theOnlyLoggedRecord()) {
            level == logLevel
            message == "Message $EXCEPTION"
            !exception
        }

        where:
        method  | logLevel
        'info'  | LogLevel.INFO
        'warn'  | LogLevel.WARN
        'error' | LogLevel.ERROR
        'debug' | LogLevel.DEBUG
        'trace' | LogLevel.TRACE
    }

    @Test
    def "#method('{}{}', Throwable, Throwable) does not print stack trace when there are placeholders for the exceptions in the message"() {
        given:
        def ex1 = new Throwable('Throwable 1')
        def ex2 = new Throwable('Throwable 2')

        when:
        logger(LogLevel.TRACE)."$method" 'Message {}, {}', ex1, ex2

        then:
        with(theOnlyLoggedRecord()) {
            level == logLevel
            message == "Message $ex1, $ex2"
            !exception
        }

        where:
        method  | logLevel
        'info'  | LogLevel.INFO
        'warn'  | LogLevel.WARN
        'error' | LogLevel.ERROR
        'debug' | LogLevel.DEBUG
        'trace' | LogLevel.TRACE
    }

    @Test
    def "#method(Marker, '{}{}', Throwable, Throwable) does not print stack trace when there are placeholders for the exceptions in the message"() {
        given:
        def ex1 = new Throwable('Throwable 1')
        def ex2 = new Throwable('Throwable 2')

        when:
        logger(LogLevel.TRACE)."$method" Stub(Marker), 'Message {}, {}', ex1, ex2

        then:
        with(theOnlyLoggedRecord()) {
            level == logLevel
            message == "Message $ex1, $ex2"
            !exception
        }

        where:
        method  | logLevel
        'info'  | LogLevel.INFO
        'warn'  | LogLevel.WARN
        'error' | LogLevel.ERROR
        'debug' | LogLevel.DEBUG
        'trace' | LogLevel.TRACE
    }

    @Test
    def "#method('{}{}', #vararr) does not print stack trace when exception is not last parameter"() {
        when:
        logger(LogLevel.TRACE)."$method" '{}, {}, ...', params.toArray()

        then:
        with(theOnlyLoggedRecord()) {
            level == logLevel
            message == "${params[0]}, ${params[1]}, ..."
            !exception
        }

        where:
        method  | params                                          | vararr                        | logLevel
        'info'  | [new Object(), new Object(), EXCEPTION, OBJECT] | 'obj1, obj2, exception, obj3' | LogLevel.INFO
        'info'  | [new Exception(), new Exception()]              | 'exception1, exception2'      | LogLevel.INFO
        'warn'  | [new Object(), new Object(), EXCEPTION, OBJECT] | 'obj1, obj2, exception, obj3' | LogLevel.WARN
        'warn'  | [new Exception(), new Exception()]              | 'exception1, exception2'      | LogLevel.WARN
        'error' | [new Object(), new Object(), EXCEPTION, OBJECT] | 'obj1, obj2, exception, obj3' | LogLevel.ERROR
        'error' | [new Exception(), new Exception()]              | 'exception1, exception2'      | LogLevel.ERROR
        'debug' | [new Object(), new Object(), EXCEPTION, OBJECT] | 'obj1, obj2, exception, obj3' | LogLevel.DEBUG
        'debug' | [new Exception(), new Exception()]              | 'exception1, exception2'      | LogLevel.DEBUG
        'trace' | [new Object(), new Object(), EXCEPTION, OBJECT] | 'obj1, obj2, exception, obj3' | LogLevel.TRACE
        'trace' | [new Exception(), new Exception()]              | 'exception1, exception2'      | LogLevel.TRACE
    }

    @Test
    def "#method(Marker, '{}{}', #vararr) does not print stack trace when exception is not last parameter"() {
        when:
        logger(LogLevel.TRACE)."$method" Stub(Marker), '{}, {}, ...', params.toArray()

        then:
        with(theOnlyLoggedRecord()) {
            level == logLevel
            message == "${params[0]}, ${params[1]}, ..."
            !exception
        }

        where:
        method  | params                                          | vararr                        | logLevel
        'info'  | [new Object(), new Object(), EXCEPTION, OBJECT] | 'obj1, obj2, exception, obj3' | LogLevel.INFO
        'info'  | [new Exception(), new Exception()]              | 'exception1, exception2'      | LogLevel.INFO
        'warn'  | [new Object(), new Object(), EXCEPTION, OBJECT] | 'obj1, obj2, exception, obj3' | LogLevel.WARN
        'warn'  | [new Exception(), new Exception()]              | 'exception1, exception2'      | LogLevel.WARN
        'error' | [new Object(), new Object(), EXCEPTION, OBJECT] | 'obj1, obj2, exception, obj3' | LogLevel.ERROR
        'error' | [new Exception(), new Exception()]              | 'exception1, exception2'      | LogLevel.ERROR
        'debug' | [new Object(), new Object(), EXCEPTION, OBJECT] | 'obj1, obj2, exception, obj3' | LogLevel.DEBUG
        'debug' | [new Exception(), new Exception()]              | 'exception1, exception2'      | LogLevel.DEBUG
        'trace' | [new Object(), new Object(), EXCEPTION, OBJECT] | 'obj1, obj2, exception, obj3' | LogLevel.TRACE
        'trace' | [new Exception(), new Exception()]              | 'exception1, exception2'      | LogLevel.TRACE
    }

    @Test
    def "#method('message', Throwable) prints stack trace"() {
        given:
        def msg = 'message, Throwable'

        when:
        logger(logLevel)."$method" msg, EXCEPTION

        then:
        with(theOnlyLoggedRecord()) {
            level == logLevel
            message == msg
            exception == EXCEPTION
        }

        where:
        method  | logLevel
        'info'  | LogLevel.INFO
        'warn'  | LogLevel.WARN
        'error' | LogLevel.ERROR
        'debug' | LogLevel.DEBUG
        'trace' | LogLevel.TRACE
    }

    @Test
    def "#method(Marker, 'message', Throwable) prints stack trace"() {
        given:
        def msg = 'message, Throwable'

        when:
        logger(logLevel)."$method" Stub(Marker), msg, EXCEPTION

        then:
        with(theOnlyLoggedRecord()) {
            level == logLevel
            message == msg
            exception == EXCEPTION
        }

        where:
        method  | logLevel
        'info'  | LogLevel.INFO
        'warn'  | LogLevel.WARN
        'error' | LogLevel.ERROR
        'debug' | LogLevel.DEBUG
        'trace' | LogLevel.TRACE
    }

    @Test
    def "#method('{}', Object, Throwable) prints stack trace"() {
        given:
        def msg = 'Message {}'

        when:
        logger(logLevel)."$method" msg, OBJECT, EXCEPTION

        then:
        with(theOnlyLoggedRecord()) {
            level == logLevel
            message == "Message $OBJECT"
            exception == EXCEPTION
        }

        where:
        method  | logLevel
        'info'  | LogLevel.INFO
        'warn'  | LogLevel.WARN
        'error' | LogLevel.ERROR
        'debug' | LogLevel.DEBUG
        'trace' | LogLevel.TRACE
    }

    @Test
    def "#method(Marker, '{}', Object, Throwable) prints stack trace"() {
        given:
        def msg = 'Message {}'

        when:
        logger(logLevel)."$method" Stub(Marker), msg, OBJECT, EXCEPTION

        then:
        with(theOnlyLoggedRecord()) {
            level == logLevel
            message == "Message $OBJECT"
            exception == EXCEPTION
        }

        where:
        method  | logLevel
        'info'  | LogLevel.INFO
        'warn'  | LogLevel.WARN
        'error' | LogLevel.ERROR
        'debug' | LogLevel.DEBUG
        'trace' | LogLevel.TRACE
    }

    @Test
    def "#method('{}{}', #vararr) prints stack trace"() {
        when:
        logger(logLevel)."$method" '{}, {}, ...', params.toArray()

        then:
        with(theOnlyLoggedRecord()) {
            level == logLevel
            message == "${params[0]}, ${params[1]}, ..."
            exception == EXCEPTION
        }

        where:
        method  | params                                                | vararr                  | logLevel
        'info'  | [new Object(), new Object(), EXCEPTION]               | 'obj1, obj2, ex'        | LogLevel.INFO
        'info'  | [new Exception(), new Exception(), OBJECT, EXCEPTION] | 'ex1, ex2, object, ex3' | LogLevel.INFO
        'warn'  | [new Object(), new Object(), EXCEPTION]               | 'obj1, obj2, ex'        | LogLevel.WARN
        'warn'  | [new Exception(), new Exception(), OBJECT, EXCEPTION] | 'ex1, ex2, object, ex3' | LogLevel.WARN
        'error' | [new Object(), new Object(), EXCEPTION]               | 'obj1, obj2, ex'        | LogLevel.ERROR
        'error' | [new Exception(), new Exception(), OBJECT, EXCEPTION] | 'ex1, ex2, object, ex3' | LogLevel.ERROR
        'debug' | [new Object(), new Object(), EXCEPTION]               | 'obj1, obj2, ex'        | LogLevel.DEBUG
        'debug' | [new Exception(), new Exception(), OBJECT, EXCEPTION] | 'ex1, ex2, object, ex3' | LogLevel.DEBUG
        'trace' | [new Object(), new Object(), EXCEPTION]               | 'obj1, obj2, ex'        | LogLevel.TRACE
        'trace' | [new Exception(), new Exception(), OBJECT, EXCEPTION] | 'ex1, ex2, object, ex3' | LogLevel.TRACE
    }

    @Test
    def "#method(Marker, '{}{}', #vararr) prints stack trace"() {
        when:
        logger(logLevel)."$method" Stub(Marker), '{}, {}, ...', params.toArray()

        then:
        with(theOnlyLoggedRecord()) {
            level == logLevel
            message == "${params[0]}, ${params[1]}, ..."
            exception == EXCEPTION
        }

        where:
        method  | params                                                | vararr                  | logLevel
        'info'  | [new Object(), new Object(), EXCEPTION]               | 'obj1, obj2, ex'        | LogLevel.INFO
        'info'  | [new Exception(), new Exception(), OBJECT, EXCEPTION] | 'ex1, ex2, object, ex3' | LogLevel.INFO
        'warn'  | [new Object(), new Object(), EXCEPTION]               | 'obj1, obj2, ex'        | LogLevel.WARN
        'warn'  | [new Exception(), new Exception(), OBJECT, EXCEPTION] | 'ex1, ex2, object, ex3' | LogLevel.WARN
        'error' | [new Object(), new Object(), EXCEPTION]               | 'obj1, obj2, ex'        | LogLevel.ERROR
        'error' | [new Exception(), new Exception(), OBJECT, EXCEPTION] | 'ex1, ex2, object, ex3' | LogLevel.ERROR
        'debug' | [new Object(), new Object(), EXCEPTION]               | 'obj1, obj2, ex'        | LogLevel.DEBUG
        'debug' | [new Exception(), new Exception(), OBJECT, EXCEPTION] | 'ex1, ex2, object, ex3' | LogLevel.DEBUG
        'trace' | [new Object(), new Object(), EXCEPTION]               | 'obj1, obj2, ex'        | LogLevel.TRACE
        'trace' | [new Exception(), new Exception(), OBJECT, EXCEPTION] | 'ex1, ex2, object, ex3' | LogLevel.TRACE
    }

    @Test
    def "trace is #result when log level is set to #level"() {
        expect:
        with(logger(level)) {
            isTraceEnabled() == expected
            isTraceEnabled(Stub(Marker)) == expected
        }

        where:
        level          | expected | result
        LogLevel.NONE  | false    | 'disabled'
        LogLevel.INFO  | false    | 'disabled'
        LogLevel.ERROR | false    | 'disabled'
        LogLevel.WARN  | false    | 'disabled'
        LogLevel.DEBUG | false    | 'disabled'
        LogLevel.TRACE | true     | 'enabled'
    }

    @Test
    def "debug is #result when log level is set to #level"() {
        expect:
        with(logger(level)) {
            isDebugEnabled() == expected
            isDebugEnabled(Stub(Marker)) == expected
        }

        where:
        level          | expected | result
        LogLevel.NONE  | false    | 'disabled'
        LogLevel.INFO  | false    | 'disabled'
        LogLevel.ERROR | false    | 'disabled'
        LogLevel.WARN  | false    | 'disabled'
        LogLevel.DEBUG | true     | 'enabled'
        LogLevel.TRACE | true     | 'enabled'
    }

    @Test
    def "warn is #result when log level is set to #level"() {
        expect:
        with(logger(level)) {
            isWarnEnabled() == expected
            isWarnEnabled(Stub(Marker)) == expected
        }

        where:
        level          | expected | result
        LogLevel.NONE  | false    | 'disabled'
        LogLevel.INFO  | false    | 'disabled'
        LogLevel.ERROR | false    | 'disabled'
        LogLevel.WARN  | true     | 'enabled'
        LogLevel.DEBUG | true     | 'enabled'
        LogLevel.TRACE | true     | 'enabled'
    }

    @Test
    def "error is #result when log level is set to #level"() {
        expect:
        with(logger(level)) {
            isErrorEnabled() == expected
            isErrorEnabled(Stub(Marker)) == expected
        }

        where:
        level          | expected | result
        LogLevel.NONE  | false    | 'disabled'
        LogLevel.INFO  | false    | 'disabled'
        LogLevel.ERROR | true     | 'enabled'
        LogLevel.WARN  | true     | 'enabled'
        LogLevel.DEBUG | true     | 'enabled'
        LogLevel.TRACE | true     | 'enabled'
    }

    @Test
    def "info is #result when log level is set to #level"() {
        expect:
        with(logger(level)) {
            isInfoEnabled() == expected
            isInfoEnabled(Stub(Marker)) == expected
        }

        where:
        level          | expected | result
        LogLevel.NONE  | false    | 'disabled'
        LogLevel.INFO  | true     | 'enabled'
        LogLevel.ERROR | true     | 'enabled'
        LogLevel.WARN  | true     | 'enabled'
        LogLevel.DEBUG | true     | 'enabled'
        LogLevel.TRACE | true     | 'enabled'
    }

    private Logger logger(LogLevel level) {
        probe.withLogLevel(level)
        Log.getLogger(LOGGER_CLASS)
    }

    private LogRecord theOnlyLoggedRecord() {
        def records = probe.allLoggedRecords
        assert records.size() == 1
        records[0]
    }
}
