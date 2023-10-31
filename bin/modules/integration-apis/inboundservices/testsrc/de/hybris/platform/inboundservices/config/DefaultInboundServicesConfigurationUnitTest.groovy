/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.inboundservices.config

import de.hybris.bootstrap.annotations.UnitTest
import de.hybris.platform.servicelayer.config.ConfigurationService
import de.hybris.platform.testframework.JUnitPlatformSpecification
import org.apache.commons.configuration.Configuration
import org.apache.commons.configuration.ConversionException
import org.junit.Test
import spock.lang.Unroll

@UnitTest
class DefaultInboundServicesConfigurationUnitTest extends JUnitPlatformSpecification {

    private static final String SUCCESS_RETENTION_PROPERTY_KEY = "inboundservices.monitoring.success.payload.retention"
    private static final String ERROR_RETENTION_PROPERTY_KEY = "inboundservices.monitoring.error.payload.retention"
    private static final String MONITORING_ENABLED_KEY = "inboundservices.monitoring.enabled"

    def configuration = Stub(Configuration)

    def configurationService = Stub(ConfigurationService) {
        getConfiguration() >> configuration
    }

    def inboundServicesConfiguration = new DefaultInboundServicesConfiguration(
            configurationService: configurationService)

    @Test
    @Unroll
    def "payload retention is #value when success payload retention is #status"() {
        given:
        configuration.getBoolean(SUCCESS_RETENTION_PROPERTY_KEY) >> value
        expect:
        inboundServicesConfiguration.isPayloadRetentionForSuccessEnabled() == value

        where:
        status     | value
        'enabled'  | true
        'disabled' | false
    }

    @Test
    @Unroll
    def "success payload retention is disabled by default if #exceptionName is thrown"() {
        given:
        configuration.getBoolean(SUCCESS_RETENTION_PROPERTY_KEY) >> { throw exception }
        expect:
        !inboundServicesConfiguration.isPayloadRetentionForSuccessEnabled()

        where:
        exception                    | exceptionName
        new NoSuchElementException() | 'NoSuchElementException'
        new ConversionException()    | 'ConversionException'
    }

    @Test
    @Unroll
    def "error payload retention is #status if monitoring error payload retention is #value"() {
        given:
        configuration.getBoolean(ERROR_RETENTION_PROPERTY_KEY) >> value

        expect:
        inboundServicesConfiguration.isPayloadRetentionForErrorEnabled() == value

        where:
        status     | value
        'enabled'  | true
        'disabled' | false
    }

    @Test
    @Unroll
    def "error retention is enabled by default if #exceptionName is thrown"() {
        given:
        configuration.getBoolean(ERROR_RETENTION_PROPERTY_KEY) >> { throw exception }

        expect:
        inboundServicesConfiguration.isPayloadRetentionForErrorEnabled()

        where:
        exception                    | exceptionName
        new NoSuchElementException() | 'NoSuchElementException'
        new ConversionException()    | 'ConversionException'
    }

    @Test
    @Unroll
    def "monitoring is #status if inbound services monitoring is #value"() {
        given:
        configuration.getBoolean(MONITORING_ENABLED_KEY) >> value

        expect:
        inboundServicesConfiguration.isMonitoringEnabled() == value

        where:
        status     | value
        'enabled'  | true
        'disabled' | false
    }

    @Test
    @Unroll
    def "monitoring is disabled by default if #exceptionName is thrown"() {
        given:
        configuration.getBoolean(MONITORING_ENABLED_KEY) >> { throw exception }

        expect:
        !inboundServicesConfiguration.isMonitoringEnabled()

        where:
        exception                    | exceptionName
        new NoSuchElementException() | 'NoSuchElementException'
        new ConversionException()    | 'ConversionException'
    }
}
