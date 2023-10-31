/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.webhookservices.exceptions

import de.hybris.bootstrap.annotations.UnitTest
import de.hybris.platform.testframework.JUnitPlatformSpecification
import org.junit.Test

@UnitTest
class CannotDeleteIntegrationObjectLinkedWithWebhookConfigExceptionUnitTest extends JUnitPlatformSpecification {
    private static final def IO_CODE = "notMatters"

    def exception = new CannotDeleteIntegrationObjectLinkedWithWebhookConfigException(IO_CODE)

    @Test
    def "parameters are empty"() {
        expect:
        exception.parameters.length == 0
    }

    @Test
    def 'message is initialized'() {
        expect:
        exception.message == exception.localizedMessage
        exception.message.contains("The [$IO_CODE] cannot be deleted because it is in use with at least one " +
                'WebhookConfiguration. Please delete the related WebhookConfiguration and try again.')
    }
}
