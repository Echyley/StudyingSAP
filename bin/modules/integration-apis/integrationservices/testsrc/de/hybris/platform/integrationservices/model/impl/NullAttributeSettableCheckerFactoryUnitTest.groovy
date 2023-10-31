/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.integrationservices.model.impl

import de.hybris.bootstrap.annotations.UnitTest
import de.hybris.platform.testframework.JUnitPlatformSpecification
import org.junit.Test
import spock.lang.Unroll

@UnitTest
class NullAttributeSettableCheckerFactoryUnitTest extends JUnitPlatformSpecification {
    def factory = new NullAttributeSettableCheckerFactory()

    @Test
    @Unroll
    def "create for #descriptor should return NullAttributeSettableChecker"() {
        expect:
        NullAttributeSettableChecker.isInstance factory.create(descriptor)

        where:
        descriptor << [Stub(DefaultTypeAttributeDescriptor), Stub(ClassificationTypeAttributeDescriptor)]
    }
}
