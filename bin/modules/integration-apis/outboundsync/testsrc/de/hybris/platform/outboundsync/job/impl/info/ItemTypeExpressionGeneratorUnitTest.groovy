/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.outboundsync.job.impl.info

import de.hybris.bootstrap.annotations.UnitTest
import de.hybris.platform.integrationservices.model.TypeDescriptor
import de.hybris.platform.testframework.JUnitPlatformSpecification
import org.junit.Test
import spock.lang.Specification
import spock.lang.Unroll

@UnitTest
class ItemTypeExpressionGeneratorUnitTest extends JUnitPlatformSpecification {
    def generator = new ItemTypeExpressionGenerator()

    @Test
    @Unroll
    def "generates #expr expression when provided type descriptor is #condition"() {
        expect:
        generator.generateExpression(typeDescriptor) == expr

        where:
        typeDescriptor       | expr          | condition
        null                 | null          | 'null'
        Stub(TypeDescriptor) | '#{itemtype}' | 'not null'
    }
}
