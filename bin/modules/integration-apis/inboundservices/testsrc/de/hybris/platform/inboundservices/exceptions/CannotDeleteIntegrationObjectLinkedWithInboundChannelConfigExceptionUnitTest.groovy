/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.inboundservices.exceptions

import de.hybris.bootstrap.annotations.UnitTest
import de.hybris.platform.testframework.JUnitPlatformSpecification
import org.junit.Test

@UnitTest
class CannotDeleteIntegrationObjectLinkedWithInboundChannelConfigExceptionUnitTest extends JUnitPlatformSpecification {
    private static final def IO_CODE = 'does-not-matter'

    def exception = new CannotDeleteIntegrationObjectLinkedWithInboundChannelConfigException(IO_CODE)

    @Test
    def 'has no parameters'() {
        expect:
        exception.parameters.length == 0
    }

    @Test
    def 'message is initialized'() {
        expect:
        exception.message == exception.localizedMessage
        exception.message.contains "The [$IO_CODE] cannot be deleted because it is in use with at least one " +
                "InboundChannelConfiguration for Authentication. Please delete the related InboundChannelConfiguration and try again"
    }
}
