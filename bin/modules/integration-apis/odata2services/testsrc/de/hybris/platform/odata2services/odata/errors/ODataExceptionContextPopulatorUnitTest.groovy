/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.odata2services.odata.errors

import de.hybris.bootstrap.annotations.UnitTest
import de.hybris.platform.odata2services.odata.InvalidDataException
import de.hybris.platform.testframework.JUnitPlatformSpecification
import org.apache.olingo.odata2.api.exception.ODataException
import org.apache.olingo.odata2.api.exception.ODataRuntimeApplicationException
import org.apache.olingo.odata2.api.processor.ODataErrorContext
import org.junit.Test
import spock.lang.Unroll

@UnitTest
class ODataExceptionContextPopulatorUnitTest extends JUnitPlatformSpecification {
    def contextPopulator = new ODataExceptionContextPopulator(runtimeExceptionContextPopulator: Mock(ErrorContextPopulator))

    @Test
    @Unroll
    def "does not populate error context when #cause cause is not instanceof ODataRuntimeApplicationException"() {
        given:
        def exception = new ODataException('message', cause)
        def context = new ODataErrorContext(exception: exception)

        when:
        contextPopulator.populate context

        then:
        0 * contextPopulator.runtimeExceptionPopulator.populate(_)

        where:
        cause << [new IllegalArgumentException(), null]
    }

    @Test
    @Unroll
    def "populates error context by delegating #cause cause to the configured populator"() {
        given:
        def exception = new ODataException('message', cause)
        def context = new ODataErrorContext(exception: exception)

        when:
        contextPopulator.populate context

        then:
        1 * contextPopulator.runtimeExceptionPopulator.populate({ it.exception.is cause })

        where:
        cause << [new ODataRuntimeApplicationException('', Locale.ENGLISH), new InvalidDataException('msg', 'errCode', 'entityType')]
    }

    @Test
    def 'handles ODataException'() {
        expect:
        contextPopulator.exceptionClass == ODataException
    }
}
