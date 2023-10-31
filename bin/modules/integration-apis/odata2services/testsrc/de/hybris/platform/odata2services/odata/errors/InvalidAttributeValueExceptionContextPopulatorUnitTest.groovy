/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.odata2services.odata.errors

import de.hybris.bootstrap.annotations.UnitTest
import de.hybris.platform.integrationservices.exception.InvalidAttributeValueException
import de.hybris.platform.integrationservices.model.TypeAttributeDescriptor
import de.hybris.platform.testframework.JUnitPlatformSpecification
import org.apache.olingo.odata2.api.commons.HttpStatusCodes
import org.apache.olingo.odata2.api.processor.ODataErrorContext
import org.junit.Test

@UnitTest
class InvalidAttributeValueExceptionContextPopulatorUnitTest extends JUnitPlatformSpecification {
    def populator = new InvalidAttributeValueExceptionContextPopulator()

    @Test
    def 'Does not populates context values if context does not contain InvalidAttributeValueException'() {
        given:
        def context = new ODataErrorContext(exception: new RuntimeException())

        when:
        populator.populate(context)

        then:
        with(context) {
            !httpStatus
            !errorCode
            !message
            !locale
        }
    }

    @Test
    def 'Populates error context values'() {
        given:
        def attributeDesc = Stub(TypeAttributeDescriptor)
        def exception = new InvalidAttributeValueException('value', attributeDesc)
        def context = new ODataErrorContext(exception: exception)

        when:
        populator.populate(context)

        then:
        with(context) {
            httpStatus == HttpStatusCodes.BAD_REQUEST
            errorCode == 'invalid_parameter_value'
            locale == Locale.ENGLISH
            message == exception.message
        }
    }

    @Test
    def 'Handles InvalidAttributeValueException'() {
        expect:
        populator.exceptionClass == InvalidAttributeValueException
    }
}
