/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.integrationservices.model.impl

import de.hybris.bootstrap.annotations.UnitTest
import de.hybris.platform.integrationservices.model.AttributeValueGetterFactory
import de.hybris.platform.integrationservices.model.AttributeValueSetterFactory
import de.hybris.platform.integrationservices.model.TypeAttributeDescriptor
import de.hybris.platform.testframework.JUnitPlatformSpecification
import org.junit.Test
import spock.lang.Unroll

@UnitTest
class DefaultAttributeValueAccessorFactoryUnitTest extends JUnitPlatformSpecification {
    def getterFactory = Stub AttributeValueGetterFactory
    def setterFactory = Stub AttributeValueSetterFactory
    def factory = new DefaultAttributeValueAccessorFactory(getterFactory: getterFactory, setterFactory: setterFactory)

    @Test
    @Unroll
    def "create with #descriptor type attribute descriptor returns #valueAccessorType value accessor"() {
        when:
        def accessor = factory.create descriptor

        then:
        valueAccessorType.isInstance accessor

        where:
        descriptor                    | valueAccessorType
        Stub(TypeAttributeDescriptor) | DelegatingAttributeValueAccessor
        null                          | DelegatingAttributeValueAccessor
    }

    @Test
    def 'can be used without setting the getter and setter factories'() {
        expect: 'accessor can be created without having getter and setter factories set'
        new DefaultAttributeValueAccessorFactory().create Stub(TypeAttributeDescriptor)
    }

    @Test
    def 'can be used with getter and setter factories explicitly set to null'() {
        expect: 'accessor can be created without having getter and setter factories set'
        new DefaultAttributeValueAccessorFactory(getterFactory: null, setterFactory: null)
                .create Stub(TypeAttributeDescriptor)
    }
}
