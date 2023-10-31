/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.integrationservices.populator

import de.hybris.bootstrap.annotations.UnitTest
import de.hybris.platform.core.model.user.CustomerModel
import de.hybris.platform.core.model.user.EmployeeModel
import de.hybris.platform.integrationservices.model.AttributeValueAccessor
import de.hybris.platform.integrationservices.model.TypeAttributeDescriptor
import de.hybris.platform.integrationservices.model.TypeDescriptor
import de.hybris.platform.integrationservices.service.IntegrationObjectConversionService
import de.hybris.platform.testframework.JUnitPlatformSpecification
import org.junit.Test
import spock.lang.Issue

@UnitTest
class DefaultComposedType2MapPopulatorUnitTest extends JUnitPlatformSpecification {

    private static final def ATTR_NAME = "testAttribute"
    private static final def QUALIFIER = "qualifier"
    private static final def ITEM_MATCHING_ATTRIBUTE_TYPE = new CustomerModel()
    private static final def ITEM_NOT_MATCHING_ATTRIBUTE_TYPE = new EmployeeModel()
    private static final def CONVERTED_VALUE = [convertedAttributeName: 'convertedValue']

    def populator = new DefaultComposedType2MapPopulator()

    Map<String, Object> targetMap = [:]
    private def attributeDescriptor = Stub(TypeAttributeDescriptor) {
        getAttributeName() >> ATTR_NAME
        getQualifier() >> QUALIFIER
        getAttributeType() >> Stub(TypeDescriptor) {
            isInstance(_) >> { List args -> ITEM_MATCHING_ATTRIBUTE_TYPE.class.isInstance(args[0]) }
        }
    }

    private def pojo = new Object()
    private def conversionService = Stub(IntegrationObjectConversionService) {
        convert(_ as ItemToMapConversionContext) >> CONVERTED_VALUE
    }

    def setup() {
        populator.setConversionService(conversionService)
    }

    @Test
    def "populates to map when attribute value is ItemModel matching the attribute type"() {
        given:
        attributeHasValue(ITEM_MATCHING_ATTRIBUTE_TYPE)

        when:
        populator.populate conversionContext(), targetMap

        then:
        targetMap == [(ATTR_NAME): CONVERTED_VALUE]
    }

    @Test
    def "does not populate to map when item is null"() {
        given:
        attributeHasValue(null)

        when:
        populator.populate conversionContext(), targetMap

        then:
        targetMap.isEmpty()
    }

    @Test
    @Issue('https://cxjira.sap.com/browse/IAPI-5057')
    def 'does not populate to map when attribute value is ItemModel not matching the attribute type'() {
        given:
        attributeHasValue(ITEM_NOT_MATCHING_ATTRIBUTE_TYPE)

        when:
        populator.populate conversionContext(), targetMap

        then:
        targetMap.isEmpty()
    }

    @Test
    def "does not populate to map when item is primitive"() {
        given:
        attributeIsPrimitive()

        when:
        populator.populate conversionContext(), targetMap

        then:
        targetMap.isEmpty()
    }

    @Test
    def "does not populate to map when item is enum"() {
        given:
        attributeIsEnum()

        when:
        populator.populate conversionContext(), targetMap

        then:
        targetMap.isEmpty()
    }

    @Test
    def "does not populate to map when item is collection"() {
        given:
        attributeIsCollection()

        when:
        populator.populate conversionContext(), targetMap

        then:
        targetMap.isEmpty()
    }

    @Test
    def "does not populate to map when item is map"() {
        given:
        attributeIsMap()

        when:
        populator.populate conversionContext(), targetMap

        then:
        targetMap.isEmpty()
    }

    private ItemToMapConversionContext conversionContext() {
        Stub(ItemToMapConversionContext) {
            getPayloadObject() >> pojo
            getTypeDescriptor() >>
                    Stub(TypeDescriptor) {
                        getAttributes() >> [attributeDescriptor]
                    }
        }
    }

    def attributeIsPrimitive() {
        attributeDescriptor.getAttributeType() >> Stub(TypeDescriptor) {
            isPrimitive() >> true
        }
    }

    def attributeIsEnum() {
        attributeDescriptor.getAttributeType() >> Stub(TypeDescriptor) {
            isEnumeration() >> true
        }
    }

    def attributeIsCollection() {
        attributeDescriptor.isCollection() >> true
    }

    def attributeIsMap() {
        attributeDescriptor.isMap() >> true
    }

    def attributeHasValue(Object value) {
        attributeDescriptor.accessor() >> Stub(AttributeValueAccessor) {
            getValue(pojo) >> value
        }
    }
}
