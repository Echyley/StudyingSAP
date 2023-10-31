/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.odata2services.odata.errors

import de.hybris.bootstrap.annotations.UnitTest
import de.hybris.platform.integrationservices.exception.IntegrationObjectItemAndClassConflictException
import de.hybris.platform.testframework.JUnitPlatformSpecification
import org.apache.olingo.odata2.api.commons.HttpStatusCodes
import org.apache.olingo.odata2.api.processor.ODataErrorContext
import org.junit.Test

@UnitTest
class IntegrationObjectItemAndClassConflictExceptionContextPopulatorUnitTest extends JUnitPlatformSpecification {

    private static final String ERROR_CODE = 'conflicting-schema'
    def contextPopulator = new IntegrationObjectItemAndClassConflictExceptionContextPopulator()

    @Test
    def 'populates error context'() {
        given:
        def exception = Stub(IntegrationObjectItemAndClassConflictException) {
            getMessage() >> 'my message'
        }
        def errorContext = new ODataErrorContext(exception: exception)

        when:
        contextPopulator.populate errorContext

        then:
        with(errorContext) {
            httpStatus == HttpStatusCodes.BAD_REQUEST
            errorCode == ERROR_CODE
            message == exception.message
        }
    }

    @Test
    def 'handles IntegrationObjectItemAndClassConflictException'() {
        expect:
        contextPopulator.exceptionClass == IntegrationObjectItemAndClassConflictException
    }
}
