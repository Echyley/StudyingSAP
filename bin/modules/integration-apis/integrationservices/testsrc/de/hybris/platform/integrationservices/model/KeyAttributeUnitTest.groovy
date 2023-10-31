/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.integrationservices.model

import de.hybris.bootstrap.annotations.UnitTest
import de.hybris.platform.integrationservices.exception.LocalizedKeyAttributeException
import de.hybris.platform.testframework.JUnitPlatformSpecification
import org.junit.Test
import spock.lang.Unroll

@UnitTest
class KeyAttributeUnitTest extends JUnitPlatformSpecification {
    @Test
    def 'throws exception when instantiated with null TypeAttributeDescriptor'() {
        when:
        new KeyAttribute(null as TypeAttributeDescriptor)

        then:
        def e = thrown IllegalArgumentException
        e.message.contains TypeAttributeDescriptor.simpleName
    }

    @Test
    def "throws exception when instantiated with localized attribute"() {
        given: 'attribute is localized'
        def attribute = Stub(TypeAttributeDescriptor) {
            isLocalized() >> true
        }

        when:
        new KeyAttribute(attribute)

        then:
        def e = thrown LocalizedKeyAttributeException
        e.attributeDescriptor.is attribute
    }

    @Test
    def "reads attribute's item type code"() {
        given:
        def descriptor = attributeDescriptor('DoesNotMatter', 'SomeItemCode', 'DoesNotMatter')
        def attribute = new KeyAttribute(descriptor)

        expect:
        attribute.itemCode == descriptor.typeDescriptor.itemCode
    }

    @Test
    def "reads attribute name"() {
        given:
        def descriptor = attributeDescriptor('DoesNotMatter', 'DoesNotMatter', 'SomeAttributeName')
        def attribute = new KeyAttribute(descriptor)

        expect:
        attribute.name == descriptor.attributeName
    }

    @Test
    def "reads attribute descriptor"() {
        given:
        def descriptor = Stub TypeAttributeDescriptor
        def attribute = new KeyAttribute(descriptor)

        expect:
        attribute.attributeDescriptor == descriptor
    }

    @Test
    @Unroll
    def "is not equal when the other attribute #condition"() {
        given:
        def attribute = new KeyAttribute(attributeDescriptor('AnIO', 'AnItem', 'AnAttribute'))

        expect:
        attribute != other

        where:
        condition                   | other
        'is null'                   | null
        'is not KeyAttribute'       | Stub(IntegrationObjectItemAttributeModel)
        'has different object code' | new KeyAttribute(attributeDescriptor('DifferentIO', 'AnItem', 'AnAttribute'))
        'has different item code'   | new KeyAttribute(attributeDescriptor('AnIO', 'DifferentItem', 'AnAttribute'))
        'has different name'        | new KeyAttribute(attributeDescriptor('AnIO', 'AnItem', 'DifferentAttribute'))
    }

    @Test
    def "is equal when the other attribute has same object code, item code and attribute name"() {
        given:
        def attribute = new KeyAttribute(attributeDescriptor('AnIO', 'AnItem', 'AnAttribute'))
        def another = new KeyAttribute(attributeDescriptor('AnIO', 'AnItem', 'AnAttribute'))

        expect:
        attribute == another
    }

    @Test
    @Unroll
    def "hashCode is not equal when the other attribute #condition"() {
        given:
        def attribute = new KeyAttribute(attributeDescriptor('AnIO', 'AnItem', 'AnAttribute'))
        def other = new KeyAttribute(descriptor)

        expect:
        attribute.hashCode() != other.hashCode()

        where:
        condition                   | descriptor
        'has different object code' | attributeDescriptor('DifferentIO', 'AnItem', 'AnAttribute')
        'has different item code'   | attributeDescriptor('AnIO', 'DifferentItem', 'AnAttribute')
        'has different name'        | attributeDescriptor('AnIO', 'AnItem', 'DifferentAttribute')
    }

    @Test
    def "hashCode is equal when the other attribute has same object code, item code and attribute name"() {
        given:
        def attribute = new KeyAttribute(attributeDescriptor('AnIO', 'AnItem', 'AnAttribute'))
        def other = new KeyAttribute(attributeDescriptor('AnIO', 'AnItem', 'AnAttribute'))

        expect:
        attribute.hashCode() == other.hashCode()
    }

    private TypeAttributeDescriptor attributeDescriptor(String ioCode, String itemCode, String name) {
        Stub(TypeAttributeDescriptor) {
            getAttributeName() >> name
            getTypeDescriptor() >> Stub(TypeDescriptor) {
                getItemCode() >> itemCode
                getIntegrationObjectCode() >> ioCode
            }
        }
    }
}
