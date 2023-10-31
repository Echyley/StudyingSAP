/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.outboundservices.monitoring

import de.hybris.bootstrap.annotations.UnitTest
import de.hybris.platform.integrationservices.util.timeout.IntegrationExecutionException
import de.hybris.platform.testframework.JUnitPlatformSpecification
import org.junit.Test
import spock.lang.Unroll

@UnitTest
class OutboundRequestExecutionExceptionUnitTest extends JUnitPlatformSpecification {

    private static final Exception EXCEPTION = new Exception("IGNORED - Testing exception")

    @Test
    @Unroll
    def "Exception is created with the cause #cause"() {
        when:
        def e = new OutboundRequestExecutionException(cause)

        then:
        e.message == 'Request encountered an exception.'
        e.cause == expectedCause

        where:
        cause                                        | expectedCause
        EXCEPTION                                    | EXCEPTION
        new IntegrationExecutionException(EXCEPTION) | EXCEPTION
    }
}
