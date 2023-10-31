/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.outboundsync.exceptions

import de.hybris.bootstrap.annotations.UnitTest
import de.hybris.platform.testframework.JUnitPlatformSpecification
import org.junit.Test

@UnitTest
class CannotDeleteIntegrationObjectLinkedWithOutboundChannelConfigExceptionUnitTest extends JUnitPlatformSpecification {
    private static final def IO_CODE = 'notMatters'
    private static final def OCC_CODE = 'occNotMatters'

    def exception = new CannotDeleteIntegrationObjectLinkedWithOutboundChannelConfigException(IO_CODE, OCC_CODE)

    @Test
    def 'message is initialized'() {
        expect:
        exception.message == exception.localizedMessage
        exception.localizedMessage.contains("The [$IO_CODE] cannot be deleted because it is in use with "
                + "OutboundChannelConfiguration: $OCC_CODE . Please delete the related OutboundChannelConfiguration "
                + "and try again.")
    }

    @Test
    def 'exception parameters includes only the OutboundChannelConfiguration code'() {
        expect:
        exception.parameters == [OCC_CODE] as Object[]
    }
}
