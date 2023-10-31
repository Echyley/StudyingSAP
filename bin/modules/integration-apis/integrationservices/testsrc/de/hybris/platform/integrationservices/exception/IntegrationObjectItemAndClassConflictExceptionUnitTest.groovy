package de.hybris.platform.integrationservices.exception

import de.hybris.bootstrap.annotations.UnitTest
import de.hybris.platform.testframework.JUnitPlatformSpecification
import org.junit.Test

@UnitTest
class IntegrationObjectItemAndClassConflictExceptionUnitTest extends JUnitPlatformSpecification {

    @Test
    def "Message is formatted."() {
        given:
        def exception = new IntegrationObjectItemAndClassConflictException("IOName")

        expect:
        exception.getMessage().contains("Integration Object 'IOName' has both IntegrationObjectItem(s) and " +
                "IntegrationObjectClass(es) associated. This combination is incompatible and one of the types " +
                "must be removed.")
    }
}
