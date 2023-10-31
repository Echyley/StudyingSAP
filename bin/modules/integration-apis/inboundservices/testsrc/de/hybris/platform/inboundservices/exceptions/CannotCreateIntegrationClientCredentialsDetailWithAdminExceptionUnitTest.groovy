/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.inboundservices.exceptions

import de.hybris.bootstrap.annotations.UnitTest
import de.hybris.platform.testframework.JUnitPlatformSpecification
import org.junit.Test

@UnitTest
class CannotCreateIntegrationClientCredentialsDetailWithAdminExceptionUnitTest extends JUnitPlatformSpecification {
    def exception = new CannotCreateIntegrationClientCredentialsDetailWithAdminException()

    @Test
    def "does not have message parameters"() {
        expect:
        exception.parameters.length == 0
    }

    @Test
    def "has a message explaining the problem"() {
        expect:
        exception.message == exception.localizedMessage
        exception.message.contains 'Cannot create IntegrationClientCredentialsDetails with admin user'
    }
}
