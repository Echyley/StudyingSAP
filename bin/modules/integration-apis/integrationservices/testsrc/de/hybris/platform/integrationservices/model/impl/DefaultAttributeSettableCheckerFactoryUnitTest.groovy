/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.integrationservices.model.impl

import de.hybris.bootstrap.annotations.UnitTest
import de.hybris.platform.testframework.JUnitPlatformSpecification
import org.junit.Test
import spock.lang.Unroll

@UnitTest
class DefaultAttributeSettableCheckerFactoryUnitTest extends JUnitPlatformSpecification {
    def factory = new DefaultAttributeSettableCheckerFactory()

    @Test
    @Unroll
    def "create for DefaultTypeAttributeDescriptor should return #clazz"() {
        given:
        factory.standardAttributeSettableChecker = checker
        and:
        def descriptor = Stub DefaultTypeAttributeDescriptor

        expect:
        clazz.isInstance factory.create(descriptor)

        where:
        checker                               | clazz
        null                                  | NullAttributeSettableChecker
        Stub(DefaultAttributeSettableChecker) | DefaultAttributeSettableChecker
    }

    @Test
    @Unroll
    def "create for ClassificationTypeAttributeDescriptor should return #clazz"() {
        given:
        factory.classificationAttributeSettableChecker = checker
        and:
        def descriptor = Stub ClassificationTypeAttributeDescriptor

        expect:
        clazz.isInstance factory.create(descriptor)

        where:
        checker                                      | clazz
        null                                         | NullAttributeSettableChecker
        Stub(ClassificationAttributeSettableChecker) | ClassificationAttributeSettableChecker
    }


}
