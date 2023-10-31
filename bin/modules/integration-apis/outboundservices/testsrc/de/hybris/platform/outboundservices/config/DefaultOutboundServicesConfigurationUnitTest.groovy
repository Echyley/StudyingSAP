/*
 *  Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.outboundservices.config

import de.hybris.bootstrap.annotations.UnitTest
import de.hybris.platform.servicelayer.config.ConfigurationService
import de.hybris.platform.testframework.JUnitPlatformSpecification
import org.apache.commons.configuration.Configuration
import org.apache.commons.configuration.ConversionException
import org.junit.Test
import spock.lang.Unroll

import java.time.Duration

@UnitTest
class DefaultOutboundServicesConfigurationUnitTest extends JUnitPlatformSpecification {

    private static final String MAX_RESPONSE_PAYLOAD_SIZE_KEY = 'outboundservices.response.payload.max.size.bytes'
    private static final int DEFAULT_MAX_RESPONSE_PAYLOAD_SIZE = 1024
    private static final String SUCCESS_RETENTION_PROPERTY_KEY = 'outboundservices.monitoring.success.payload.retention'
    private static final String ERROR_RETENTION_PROPERTY_KEY = 'outboundservices.monitoring.error.payload.retention'
    private static final String MONITORING_ENABLED_KEY = 'outboundservices.monitoring.enabled'
    private static final String REQUEST_EXECUTION_TIMEOUT_KEY = 'outboundservices.request.execution.timeout.millisecs'
    private static final long DEFAULT_REQUEST_EXECUTION_TIMEOUT_VALUE_MILLISECS = 5000
    private static final def MAX_POOL_CONNECTIONS_KEY = 'outboundservices.httpclient.max.connections'
    private static final def DEFAULT_MAX_POOL_CONNECTIONS = 5
    private static final def CONNECTION_KEEP_ALIVE_KEY = 'outboundservices.httpclient.connections.keep-alive'
    private static final def DEFAULT_CONNECTION_KEEP_ALIVE_MILLISEC = 500L
    private static final def CONNECTION_TIMEOUT_KEY = 'outboundservices.httpclient.connections.connectionTimeout'
    private static final def DEFAULT_CONNECTION_TIMEOUT_MILLISEC = 3000L
    private static final def CONNECTION_VALIDITY_KEY = 'outboundservices.httpclient.connections.validity'
    private static final def DEFAULT_CONNECTION_VALIDITY_MILLISEC = 500L

    def configuration = Mock Configuration

    def configurationService = Stub(ConfigurationService) {
        getConfiguration() >> configuration
    }

    def outboundServicesConfiguration = new DefaultOutboundServicesConfiguration(configurationService: configurationService)

    @Test
    @Unroll
    def "success payload retention #status when outboundservices.monitoring.success.payload.retention is #value"() {
        given:
        configuration.getBoolean(SUCCESS_RETENTION_PROPERTY_KEY) >> value

        expect:
        outboundServicesConfiguration.isPayloadRetentionForSuccessEnabled() == value

        where:
        status     | value
        'enabled'  | true
        'disabled' | false
    }

    @Test
    @Unroll
    def "success payload retention is enabled by default if #exceptionName is thrown"() {
        given:
        configuration.getBoolean(SUCCESS_RETENTION_PROPERTY_KEY) >> { throw exception }

        expect:
        !outboundServicesConfiguration.isPayloadRetentionForSuccessEnabled()

        where:
        exception                    | exceptionName
        new NoSuchElementException() | 'NoSuchElementException'
        new ConversionException()    | 'ConversionException'
    }

    @Test
    def "error retention is disabled if outboundservices.monitoring.error.payload.retention is false"() {
        given:
        configuration.getBoolean(ERROR_RETENTION_PROPERTY_KEY) >> false

        expect:
        !outboundServicesConfiguration.isPayloadRetentionForErrorEnabled()
    }

    @Test
    @Unroll
    def "error retention is enabled by default if #exceptionName is thrown"() {
        given:
        configuration.getBoolean(ERROR_RETENTION_PROPERTY_KEY) >> { throw exception }

        expect:
        outboundServicesConfiguration.isPayloadRetentionForErrorEnabled()

        where:
        exception                    | exceptionName
        new NoSuchElementException() | 'NoSuchElementException'
        new ConversionException()    | 'ConversionException'
    }

    @Test
    @Unroll
    def "Monitoring is #status if outboundservices.monitoring.enabled is #value"() {
        given:
        configuration.getBoolean(MONITORING_ENABLED_KEY) >> value

        expect:
        outboundServicesConfiguration.isMonitoringEnabled() == value

        where:
        status     | value
        'enabled'  | true
        'disabled' | false
    }

    @Test
    @Unroll
    def "Monitoring is disabled by default if #exceptionName is thrown"() {
        given:
        configuration.getBoolean(MONITORING_ENABLED_KEY) >> { throw exception }

        expect:
        !outboundServicesConfiguration.isMonitoringEnabled()

        where:
        exception                    | exceptionName
        new NoSuchElementException() | 'NoSuchElementException'
        new ConversionException()    | 'ConversionException'
    }

    @Test
    def "max response payload size is returned if outboundservices.response.payload.max.size.bytes is set"() {
        given:
        configuration.getInt(MAX_RESPONSE_PAYLOAD_SIZE_KEY) >> DEFAULT_MAX_RESPONSE_PAYLOAD_SIZE

        expect:
        outboundServicesConfiguration.getMaximumResponsePayloadSize() == DEFAULT_MAX_RESPONSE_PAYLOAD_SIZE
    }

    @Test
    @Unroll
    def "max response payload size is the default value if #exceptionName is thrown"() {
        given:
        configuration.getInt(MAX_RESPONSE_PAYLOAD_SIZE_KEY) >> { throw exception }

        expect:
        outboundServicesConfiguration.getMaximumResponsePayloadSize() == DEFAULT_MAX_RESPONSE_PAYLOAD_SIZE

        where:
        exception                    | exceptionName
        new NoSuchElementException() | 'NoSuchElementException'
        new ConversionException()    | 'ConversionException'
    }

    @Test
    def "max response payload size is set successfully"() {
        given:
        def exampleMaximumResponsePayloadSize = 1000

        when:
        outboundServicesConfiguration.setMaximumResponsePayloadSize(exampleMaximumResponsePayloadSize)

        then:
        1 * configuration.setProperty(MAX_RESPONSE_PAYLOAD_SIZE_KEY, String.valueOf(exampleMaximumResponsePayloadSize))
    }

    @Test
    def "getRequestExecutionTimeout returns default timeout value"() {
        given:
        configuration.getLong(REQUEST_EXECUTION_TIMEOUT_KEY) >> DEFAULT_REQUEST_EXECUTION_TIMEOUT_VALUE_MILLISECS

        expect:
        outboundServicesConfiguration.getRequestExecutionTimeout() == DEFAULT_REQUEST_EXECUTION_TIMEOUT_VALUE_MILLISECS
    }

    @Test
    def "setRequestExecutionTimeout sets a new timeout value successfully"() {
        given:
        def newTimeout = 5000

        when:
        outboundServicesConfiguration.setRequestExecutionTimeout(newTimeout)

        then:
        1 * configuration.setProperty(REQUEST_EXECUTION_TIMEOUT_KEY, String.valueOf(newTimeout))
    }

    @Test
    def "setRequestExecutionTimeout sets a default timeout when its value is negative"() {
        given:
        def negativeTimeout = -5000

        when:
        outboundServicesConfiguration.setRequestExecutionTimeout(negativeTimeout)

        then:
        1 * configuration.setProperty(REQUEST_EXECUTION_TIMEOUT_KEY,
                String.valueOf(DEFAULT_REQUEST_EXECUTION_TIMEOUT_VALUE_MILLISECS))
    }

    @Test
    @Unroll
    def "request execution timeout is the default value if #exceptionName is thrown"() {
        given:
        configuration.getLong(REQUEST_EXECUTION_TIMEOUT_KEY) >> { throw exception }

        expect:
        outboundServicesConfiguration.getRequestExecutionTimeout() == DEFAULT_REQUEST_EXECUTION_TIMEOUT_VALUE_MILLISECS

        where:
        exception                    | exceptionName
        new NoSuchElementException() | 'NoSuchElementException'
        new ConversionException()    | 'ConversionException'
    }

    @Test
    def 'max connection pool size retrieves the configured value'() {
        given:
        def configuredValue = 1
        configuration.getInt(MAX_POOL_CONNECTIONS_KEY) >> configuredValue

        expect:
        outboundServicesConfiguration.maxConnectionPoolSize == configuredValue
    }

    @Test
    @Unroll
    def "max connection pool size has default value when the value is #condition"() {
        given:
        configuration.getInt(MAX_POOL_CONNECTIONS_KEY) >> { throw ex }

        expect:
        outboundServicesConfiguration.maxConnectionPoolSize == DEFAULT_MAX_POOL_CONNECTIONS

        where:
        condition        | ex
        'not configured' | new NoSuchElementException()
        'invalid'        | new ConversionException()
    }

    @Test
    def 'max connection pool size can be changed at runtime'() {
        given:
        def updatedMax = DEFAULT_MAX_POOL_CONNECTIONS + 2

        when:
        outboundServicesConfiguration.maxConnectionPoolSize = updatedMax

        then:
        1 * configuration.setProperty(MAX_POOL_CONNECTIONS_KEY, "$updatedMax")
    }

    @Test
    def 'connection keep-alive time retrieves the configured value'() {
        given:
        def configuredValue = Duration.ofSeconds 3
        configuration.getLong(CONNECTION_KEEP_ALIVE_KEY) >> configuredValue.toMillis()

        expect:
        outboundServicesConfiguration.connectionKeepAlive == configuredValue
    }

    @Test
    def 'connection keep-alive time has default value when the configured value is negative'() {
        given:
        configuration.getLong(CONNECTION_KEEP_ALIVE_KEY) >> -1

        expect:
        outboundServicesConfiguration.connectionKeepAlive.toMillis() == DEFAULT_CONNECTION_KEEP_ALIVE_MILLISEC
    }

    @Test
    @Unroll
    def "connection keep-alive time has default value when the value is #condition"() {
        given:
        configuration.getLong(CONNECTION_KEEP_ALIVE_KEY) >> { throw ex }

        expect:
        outboundServicesConfiguration.connectionKeepAlive.toMillis() == DEFAULT_CONNECTION_KEEP_ALIVE_MILLISEC

        where:
        condition        | ex
        'not configured' | new NoSuchElementException()
        'invalid'        | new ConversionException()
    }

    @Test
    def 'connection keep-alive time can be changed at runtime'() {
        given:
        def updatedDuration = DEFAULT_CONNECTION_KEEP_ALIVE_MILLISEC + 1000

        when:
        outboundServicesConfiguration.connectionKeepAlive = Duration.ofMillis(updatedDuration)

        then:
        1 * configuration.setProperty(CONNECTION_KEEP_ALIVE_KEY, "$updatedDuration")
    }

    @Test
    def 'connection keep-alive time is reset to default value when it is set with null value'() {
        when:
        outboundServicesConfiguration.connectionKeepAlive = null

        then:
        1 * configuration.setProperty(CONNECTION_KEEP_ALIVE_KEY, "$DEFAULT_CONNECTION_KEEP_ALIVE_MILLISEC")
    }

    @Test
    def 'connection timeout retrieves the configured value'() {
        given:
        def configuredValue = Duration.ofSeconds 10
        configuration.getLong(CONNECTION_TIMEOUT_KEY) >> configuredValue.toMillis()

        expect:
        outboundServicesConfiguration.connectionTimeout == configuredValue
    }

    @Test
    def 'connection timeout has default value when the configured value is negative'() {
        given:
        configuration.getLong(CONNECTION_TIMEOUT_KEY) >> -1

        expect:
        outboundServicesConfiguration.connectionTimeout.toMillis() == DEFAULT_CONNECTION_TIMEOUT_MILLISEC
    }

    @Test
    @Unroll
    def "connection timeout has default value when the value is #condition"() {
        given:
        configuration.getLong(CONNECTION_TIMEOUT_KEY) >> { throw ex }

        expect:
        outboundServicesConfiguration.connectionTimeout.toMillis() == DEFAULT_CONNECTION_TIMEOUT_MILLISEC

        where:
        condition        | ex
        'not configured' | new NoSuchElementException()
        'invalid'        | new ConversionException()
    }

    @Test
    def 'connection timeout can be changed at runtime'() {
        given:
        def updatedTimeout = DEFAULT_CONNECTION_TIMEOUT_MILLISEC + 5000

        when:
        outboundServicesConfiguration.connectionTimeout = Duration.ofMillis(updatedTimeout)

        then:
        1 * configuration.setProperty(CONNECTION_TIMEOUT_KEY, "$updatedTimeout")
    }

    @Test
    def 'connection timeout is reset to default value when it is set with null value'() {
        when:
        outboundServicesConfiguration.connectionTimeout = null

        then:
        1 * configuration.setProperty(CONNECTION_TIMEOUT_KEY, "$DEFAULT_CONNECTION_TIMEOUT_MILLISEC")
    }

    @Test
    def 'connection validity period retrieves the configured value'() {
        given:
        def configuredValue = Duration.ofMinutes 1
        configuration.getLong(CONNECTION_VALIDITY_KEY) >> configuredValue.toMillis()

        expect:
        outboundServicesConfiguration.idleConnectionValidityPeriod == configuredValue
    }

    @Test
    def 'connection validity period has default value when the configured value is negative'() {
        given:
        configuration.getLong(CONNECTION_VALIDITY_KEY) >> -1

        expect:
        outboundServicesConfiguration.idleConnectionValidityPeriod.toMillis() == DEFAULT_CONNECTION_VALIDITY_MILLISEC
    }

    @Test
    @Unroll
    def "connection validity period has default value when the value is #condition"() {
        given:
        configuration.getLong(CONNECTION_VALIDITY_KEY) >> { throw ex }

        expect:
        outboundServicesConfiguration.idleConnectionValidityPeriod.toMillis() == DEFAULT_CONNECTION_VALIDITY_MILLISEC

        where:
        condition        | ex
        'not configured' | new NoSuchElementException()
        'invalid'        | new ConversionException()
    }

    @Test
    def 'connection validity period can be changed at runtime'() {
        given:
        def updatedValue = DEFAULT_CONNECTION_TIMEOUT_MILLISEC * 2

        when:
        outboundServicesConfiguration.idleConnectionValidityPeriod = Duration.ofMillis(updatedValue)

        then:
        1 * configuration.setProperty(CONNECTION_VALIDITY_KEY, "$updatedValue")
    }

    @Test
    def 'connection validity period is reset to default value when it is set with null value'() {
        when:
        outboundServicesConfiguration.idleConnectionValidityPeriod = null

        then:
        1 * configuration.setProperty(CONNECTION_VALIDITY_KEY, "$DEFAULT_CONNECTION_VALIDITY_MILLISEC")
    }
}
