/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.integrationservices.model.impl

import de.hybris.bootstrap.annotations.UnitTest
import de.hybris.platform.integrationservices.model.ClassificationAttributeValueConverter
import de.hybris.platform.integrationservices.model.ClassificationAttributeValueHandler
import de.hybris.platform.integrationservices.model.TypeAttributeDescriptor
import de.hybris.platform.servicelayer.model.ModelService
import de.hybris.platform.testframework.JUnitPlatformSpecification
import org.junit.Test
import spock.lang.Unroll

@UnitTest
class DefaultAttributeValueSetterFactoryUnitTest extends JUnitPlatformSpecification {

    def factory = new DefaultAttributeValueSetterFactory(
            modelService: Stub(ModelService),
            valueHandlers: [],
            valueConverters: [])

    @Test
    @Unroll
    def "create with #descriptor type attribute descriptor returns #valueSetterType value getter"() {
        when:
        def setter = factory.create descriptor

        then:
        valueSetterType.isInstance setter

        where:
        descriptor                                  | valueSetterType
        Stub(TypeAttributeDescriptor)               | StandardAttributeValueSetter
        Stub(ClassificationTypeAttributeDescriptor) | ClassificationAttributeValueSetter
        null                                        | NullAttributeValueSetter
    }

    @Test
    def "handlers and converters are never null"() {
        given:
        def factory = new DefaultAttributeValueSetterFactory(
                modelService: Stub(ModelService),
                valueHandlers: null,
                valueConverters: null)

        expect:
        with(factory) {
            valueHandlers == []
            valueConverters == []
        }
    }

    @Test
    @Unroll
    def "#setterMethod does not leak reference when InputList is not null"() {
        given:
        def setterList = InputList
        factory."$setterMethod"(setterList)

        when:
        setterList.clear()

        then:
        !factory."$getterMethod"().empty

        where:
        getterMethod         | setterMethod         | InputList
        "getValueHandlers"   | "setValueHandlers"   | [Stub(ClassificationAttributeValueHandler)]
        "getValueConverters" | "setValueConverters" | [Stub(ClassificationAttributeValueConverter)]
    }

    @Test
    @Unroll
    def "#setterMethod does not leak reference when InputList is  null"() {
        given:
        factory."$setterMethod"(null)

        expect:
        factory."$getterMethod"().empty

        where:
        getterMethod         | setterMethod
        "getValueHandlers"   | "setValueHandlers"
        "getValueConverters" | "setValueConverters"
    }

    @Test
    @Unroll
    def "#getterMethod returns immutable list"() {
        given:
        factory."$setterMethod"(InputList)
        def returnedList = factory."$getterMethod"()

        when:
        returnedList << appendList

        then:
        thrown UnsupportedOperationException

        where:
        getterMethod         | setterMethod         | InputList | appendList
        "getValueHandlers"   | "setValueHandlers"   | []        | Stub(ClassificationAttributeValueHandler)
        "getValueConverters" | "setValueConverters" | []        | Stub(ClassificationAttributeValueConverter)
    }
}
