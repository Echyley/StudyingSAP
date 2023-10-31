/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.odata2services.odata.schema.property

import de.hybris.bootstrap.annotations.UnitTest
import de.hybris.platform.integrationservices.model.TypeAttributeDescriptor
import de.hybris.platform.integrationservices.model.TypeDescriptor
import de.hybris.platform.odata2services.odata.schema.SchemaElementGenerator
import de.hybris.platform.testframework.JUnitPlatformSpecification
import org.apache.olingo.odata2.api.edm.provider.SimpleProperty
import org.junit.Test

@UnitTest
class PrimitivePropertyListElementGeneratorUnitTest extends JUnitPlatformSpecification {
    def keyPropertyGenerator = Stub IntegrationKeyPropertyElementGenerator
    def propertyGenerator = Stub(SchemaElementGenerator) {
        generate(_) >> { args -> simpleEdmProperty(args[0].attributeName) }
    }
    def propertyListGenerator = new PrimitivePropertyListElementGenerator(propertyGenerator, keyPropertyGenerator)

    @Test
    def "throws IllegalArgumentException when #generator is null"() {
        when:
        new PrimitivePropertyListElementGenerator(pGenerator, kGenerator)

        then:
        def e = thrown IllegalArgumentException
        e.message == "$generator cannot be null"

        where:
        generator           | pGenerator                   | kGenerator
        "propertyGenerator" | null                         | Stub(IntegrationKeyPropertyElementGenerator)
        "keyGenerator"      | Stub(SchemaElementGenerator) | null
    }

    @Test
    def "generates empty properties when item has #condition attributes"() {
        given: 'TypeDescriptor for the item model has no applicable attributes'
        def typeDescriptor = Stub(TypeDescriptor) {
            getAttributes() >> attributes
        }
        and: 'key generator does not generate a property for the item model'
        keyPropertyGenerator.generate(typeDescriptor) >> Optional.empty()

        expect:
        propertyListGenerator.generate(typeDescriptor).empty

        where:
        condition            | attributes
        'no'                 | []
        'only non-primitive' | [nonPrimitiveAttribute()]
        'only collection'    | [collectionAttribute()]
    }

    @Test
    def 'generates properties when item model contains simple primitive attribute'() {
        given: 'item model has a primitive attribute'
        def typeDescriptor = Stub(TypeDescriptor) {
            getAttributes() >> [primitiveAttribute('code')]
        }
        and: 'key generator does not generate a property for the item model'
        keyPropertyGenerator.generate(typeDescriptor) >> Optional.empty()

        when:
        def properties = propertyListGenerator.generate typeDescriptor

        then:
        properties.collect({ it.name }) == ['code']
    }

    @Test
    def 'generated properties include key property when it is generated'() {
        given: 'item model has primitive attributes'
        def typeDescriptor = Stub(TypeDescriptor) {
            getAttributes() >> [primitiveAttribute('code'), primitiveAttribute('name')]
        }
        and: 'key generator generates a key property'
        keyPropertyGenerator.generate(typeDescriptor) >> Optional.of(simpleEdmProperty('integrationKey'))

        when:
        def properties = propertyListGenerator.generate typeDescriptor

        then:
        properties.collect({ it.name }) == ['code', 'name', 'integrationKey']
    }

    static SimpleProperty simpleEdmProperty(String name) {
        new SimpleProperty().setName(name)
    }

    TypeAttributeDescriptor nonPrimitiveAttribute() {
        Stub(TypeAttributeDescriptor) {
            isPrimitive() >> false
        }
    }

    TypeAttributeDescriptor collectionAttribute() {
        Stub(TypeAttributeDescriptor) {
            isCollection() >> true
        }
    }

    TypeAttributeDescriptor primitiveAttribute(String name) {
        Stub(TypeAttributeDescriptor) {
            getAttributeName() >> name
            isPrimitive() >> true
        }
    }
}
