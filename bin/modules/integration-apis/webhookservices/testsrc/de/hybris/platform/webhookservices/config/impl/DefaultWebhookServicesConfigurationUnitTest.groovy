/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.webhookservices.config.impl

import de.hybris.bootstrap.annotations.UnitTest
import de.hybris.platform.servicelayer.config.ConfigurationService
import de.hybris.platform.testframework.JUnitPlatformSpecification
import org.apache.commons.configuration.Configuration
import org.apache.commons.configuration.ConversionException

@UnitTest
class DefaultWebhookServicesConfigurationUnitTest extends JUnitPlatformSpecification {

    private static final String EVENT_CONSOLIDATION_ENABLED_KEY = "webhookservices.event.consolidation.enabled"
    private static final boolean DEFAULT_EVENT_CONSOLIDATION_ENABLED_KEY = false

    def configuration = Stub Configuration
    def configurationService = Stub(ConfigurationService) {
        getConfiguration() >> configuration
    }

    def webhookServicesConfiguration = new DefaultWebhookServicesConfiguration(configurationService: configurationService)

    def "event consolidation is #status when webhookservices.event.consolidation.enabled is #value"() {
        given:
        configuration.getBoolean(EVENT_CONSOLIDATION_ENABLED_KEY) >> value

        expect:
        webhookServicesConfiguration.isEventConsolidationEnabled() == value

        where:
        status     | value
        'enabled'  | true
        'disabled' | false
    }

    def "event consolidation defaults to disabled when the service throws #exception.class.simpleName"() {
        given:
        configuration.getBoolean(EVENT_CONSOLIDATION_ENABLED_KEY) >> { throw exception }

        expect:
        webhookServicesConfiguration.isEventConsolidationEnabled() == DEFAULT_EVENT_CONSOLIDATION_ENABLED_KEY

        where:
        exception << [new NoSuchElementException(), new ConversionException()]
    }
}
