/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.integrationbackoffice.exceptions

import de.hybris.bootstrap.annotations.UnitTest
import de.hybris.platform.testframework.JUnitPlatformSpecification
import org.junit.Test

@UnitTest
class ExportConfigurationEntityNotSelectedExceptionUnitTest extends JUnitPlatformSpecification {
    def exception = new ExportConfigurationEntityNotSelectedException()

    @Test
    def 'message is initialized after instantiation'() {
        expect:
        exception.message == 'No entity is currently selected in the editor.'
    }

    @Test
    def 'parameters are empty'() {
        expect:
        exception.parameters.length == 0
    }
}
