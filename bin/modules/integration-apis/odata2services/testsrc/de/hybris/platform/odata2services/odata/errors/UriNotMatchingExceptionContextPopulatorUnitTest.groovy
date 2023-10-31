/*
 *  Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.odata2services.odata.errors

import de.hybris.bootstrap.annotations.UnitTest
import de.hybris.platform.testframework.JUnitPlatformSpecification
import org.apache.olingo.odata2.api.commons.HttpStatusCodes
import org.apache.olingo.odata2.api.processor.ODataErrorContext
import org.apache.olingo.odata2.api.uri.UriNotMatchingException
import org.junit.Test

@UnitTest
class UriNotMatchingExceptionContextPopulatorUnitTest extends JUnitPlatformSpecification {
    def contextPopulator = new UriNotMatchingExceptionContextPopulator()

    @Test
    def "provides error context values for UriNotMatchingException"() {
        given:
        def attributeName = "catalog"

        def ex = new UriNotMatchingException(UriNotMatchingException.PROPERTYNOTFOUND.addContent(attributeName))
        def context = new ODataErrorContext(exception: ex)

        when:
        contextPopulator.populate context

        then:
        with(context) {
            httpStatus == HttpStatusCodes.NOT_FOUND
            locale == Locale.ENGLISH
            errorCode == "missing_attribute"
            message == "Could not find property with name: '$attributeName'."
        }
    }

    @Test
    def "does not populate context when context exception is not an instanceof UriNotMatchingException"() {
        given:
        def ex = new Exception('IGNORE - test exception')
        def context = new ODataErrorContext(exception: ex)

        when:
        contextPopulator.populate context

        then:
        with(context) {
            !httpStatus
            !locale
            !errorCode
            !message
        }
    }

    @Test
    def 'handles UriNotMatchingException'() {
        expect:
        contextPopulator.exceptionClass == UriNotMatchingException
    }
}
