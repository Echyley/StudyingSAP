/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.integrationservices.scripting

import de.hybris.bootstrap.annotations.UnitTest
import de.hybris.platform.testframework.JUnitPlatformSpecification
import org.junit.Test
import spock.lang.Unroll

@UnitTest
class LogicLocationSchemeUnitTest extends JUnitPlatformSpecification {

    @Test
    @Unroll
    def "from returns an existing #scheme scheme"() {
        expect:
        LogicLocationScheme.from(schemeString) == scheme

        where:
        schemeString | scheme
        'model'      | LogicLocationScheme.MODEL
        'bean'       | LogicLocationScheme.BEAN
    }

    @Test
    @Unroll
    def "from throws an exception when scheme #unsupportedScheme is not supported"() {
        when:
        LogicLocationScheme.from(unsupportedScheme)

        then:
        def e = thrown UnsupportedLogicLocationSchemeException
        e.message == "$unsupportedScheme is unsupported"

        where:
        unsupportedScheme << ['', null, 'unsupportedScheme']
    }
}
