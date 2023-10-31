/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.outboundservices.decorator.impl

import de.hybris.bootstrap.annotations.UnitTest
import de.hybris.platform.outboundservices.decorator.DecoratorContext
import de.hybris.platform.outboundservices.decorator.DecoratorContextErrorException
import de.hybris.platform.outboundservices.decorator.DecoratorExecution
import de.hybris.platform.testframework.JUnitPlatformSpecification
import org.junit.Test
import org.springframework.http.HttpHeaders
import spock.lang.Unroll

@UnitTest
class DefaultContextErrorDecoratorUnitTest extends JUnitPlatformSpecification {

    private static final String ERROR_MESSAGE = 'an error message for the exception in the test'
    private static final String ANOTHER_ERROR_MESSAGE = 'another error message for the exception in the test'

    def contextErrorDecorator = new DefaultContextErrorDecorator()
    def decoratorExecution = Stub(DecoratorExecution)

    @Test
    @Unroll
    def 'context error decorator isApplicable returns #applicable when context has #msg'() {
        expect:
        applicable == contextErrorDecorator.isApplicable(errorContext)

        where:
        msg         | errorContext                 | applicable
        "no errors" | decoratorContextNoErrors()   | false
        "errors"    | decoratorContextWithErrors() | true
    }

    @Test
    def 'an exception is thrown when errors are present in the context'() {
        when:
        contextErrorDecorator.decorate(new HttpHeaders(), [:], decoratorContextWithErrors(), decoratorExecution)

        then:
        def e = thrown(DecoratorContextErrorException)
        with(e.message) {
            contains("Errors found in the DecoratorContext")
            contains(ERROR_MESSAGE)
            contains(ANOTHER_ERROR_MESSAGE)
        }
    }

    private DecoratorContext decoratorContextWithErrors() {
        Stub(DecoratorContext) {
            hasErrors() >> true
            getErrors() >> [ERROR_MESSAGE, ANOTHER_ERROR_MESSAGE]
        }
    }

    private DecoratorContext decoratorContextNoErrors() {
        Stub(DecoratorContext) {
            hasErrors() >> false
        }
    }
}
