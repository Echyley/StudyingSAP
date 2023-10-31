/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.integrationbackoffice.exceptions

import de.hybris.bootstrap.annotations.UnitTest
import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException
import de.hybris.platform.testframework.JUnitPlatformSpecification
import org.junit.Test

@UnitTest
class ExportConfigurationModelNotFoundExceptionUnitTest extends JUnitPlatformSpecification {
    private static final def CAUSE_OF_EXCEPTION = new ModelNotFoundException('message does not matter')

    def exception = new ExportConfigurationModelNotFoundException(CAUSE_OF_EXCEPTION)

    @Test
    def 'cause passed to the constructor can be read back'() {
        expect:
        exception.cause.is CAUSE_OF_EXCEPTION
    }

    @Test
    def 'has message explaining the problem'() {
        expect:
        exception.message == 'An entity or an entity instance may have been deleted. Please refresh the list of instances.'
    }
}
