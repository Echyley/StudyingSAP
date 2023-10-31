/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.odata2services.odata.schema.entity

import de.hybris.bootstrap.annotations.UnitTest
import de.hybris.platform.integrationservices.model.MapDescriptor
import de.hybris.platform.integrationservices.model.TypeAttributeDescriptor
import de.hybris.platform.integrationservices.model.TypeDescriptor
import de.hybris.platform.odata2services.odata.schema.SchemaElementGenerator
import de.hybris.platform.testframework.JUnitPlatformSpecification
import org.apache.olingo.odata2.api.edm.EdmSimpleTypeKind
import org.apache.olingo.odata2.api.edm.provider.EntityType
import org.apache.olingo.odata2.api.edm.provider.Key
import org.apache.olingo.odata2.api.edm.provider.Property
import org.apache.olingo.odata2.api.edm.provider.SimpleProperty
import org.junit.Test

@UnitTest
class MapEntryEntityTypeElementGeneratorUnitTest extends JUnitPlatformSpecification {

    def typeDescriptor = Stub TypeDescriptor

    SchemaElementGenerator<Optional<Key>, List<Property>> keyGenerator = Stub {
        generate(_ as List<Property>) >> Optional.empty()
    }
    def generator = new MapEntryEntityTypeElementGenerator(keyGenerator: keyGenerator)

    @Test
    def 'generates empty entity list when descriptor does not have map attributes'() {
        given:
        typeDescriptor.getAttributes() >> [nonMapAttribute()]

        expect:
        generator.generate(typeDescriptor).empty
    }

    @Test
    def 'generates empty entity list when attribute is localized'() {
        given:
        typeDescriptor.getAttributes() >> [localizedAttribute()]

        expect:
        generator.generate(typeDescriptor).empty
    }

    @Test
    def 'generates entity type for a map attribute'() {
        given:
        def entityName = 'SomeMapType'
        typeDescriptor.getAttributes() >> [mapAttribute(entityName)]

        when:
        def entityTypes = generator.generate(typeDescriptor)

        then: 'EntityType is generated'
        entityTypes.size() == 1
        and: 'the generated EntityType has expected name'
        def entity = entityTypes.first()
        entity.name == entityName
        and: 'the generated EntityType has two properties'
        entity.properties.size() == 2
    }

    @Test
    def 'generated entity type contains correct key property'() {
        given:
        typeDescriptor.getAttributes() >> [mapAttribute('SomeMapType')]

        when:
        def items = generator.generate(typeDescriptor)

        then:
        def property = findProperty(items.first(), 'key')
        property.annotationAttributes.collect({ it.name }) == ['Nullable', 's:IsUnique']
        property.annotationAttributes.collect({ it.text }) == ['false', 'true']
    }

    @Test
    def "generated key property correctly presents #platformType in EDM"() {
        given: "attribute map key has #platformType type"
        def mapDescriptor = mapDescriptor(platformType)
        typeDescriptor.getAttributes() >> [mapAttribute(mapDescriptor)]

        when:
        def items = generator.generate(typeDescriptor)

        then:
        def property = findProperty(items.first(), 'key')
        property.type == edmType

        where:
        platformType           | edmType
        'java.math.BigDecimal' | EdmSimpleTypeKind.Decimal
        'java.math.BigInteger' | EdmSimpleTypeKind.String
        'java.lang.Boolean'    | EdmSimpleTypeKind.Boolean
        'java.lang.Byte'       | EdmSimpleTypeKind.SByte
        'java.lang.Character'  | EdmSimpleTypeKind.String
        'java.util.Date'       | EdmSimpleTypeKind.DateTime
        'java.lang.Double'     | EdmSimpleTypeKind.Double
        'java.lang.Float'      | EdmSimpleTypeKind.Single
        'java.lang.Integer'    | EdmSimpleTypeKind.Int32
        'java.lang.Long'       | EdmSimpleTypeKind.Int64
        'java.lang.Short'      | EdmSimpleTypeKind.Int16
        'java.lang.String'     | EdmSimpleTypeKind.String
    }

    @Test
    def 'exception is thrown when map attribute key type is not a known primitive'() {
        given:
        def mapDescriptor = mapDescriptor('UnknownType', 'java.lang.String')
        typeDescriptor.getAttributes() >> [mapAttribute(mapDescriptor)]

        when:
        generator.generate(typeDescriptor)

        then:
        thrown IllegalArgumentException
    }

    @Test
    def 'generated entity type contains correct value property'() {
        given:
        typeDescriptor.getAttributes() >> [mapAttribute('SomeMapType')]

        when:
        def items = generator.generate(typeDescriptor)

        then:
        def property = findProperty(items.first(), 'value')
        property.annotationAttributes.collect({ it.name }) == ['Nullable']
        property.annotationAttributes.collect({ it.text }) == ['true']
    }

    @Test
    def "generated value property correctly presents #platformType in EDM"() {
        given: "attribute map key has #platformType type"
        def mapDescriptor = mapDescriptor('java.lang.String', platformType)
        typeDescriptor.getAttributes() >> [mapAttribute(mapDescriptor)]

        when:
        def items = generator.generate(typeDescriptor)

        then:
        def property = findProperty(items.first(), 'value')
        property.type == edmType

        where:
        platformType           | edmType
        'java.math.BigDecimal' | EdmSimpleTypeKind.Decimal
        'java.math.BigInteger' | EdmSimpleTypeKind.String
        'java.lang.Boolean'    | EdmSimpleTypeKind.Boolean
        'java.lang.Byte'       | EdmSimpleTypeKind.SByte
        'java.lang.Character'  | EdmSimpleTypeKind.String
        'java.util.Date'       | EdmSimpleTypeKind.DateTime
        'java.lang.Double'     | EdmSimpleTypeKind.Double
        'java.lang.Float'      | EdmSimpleTypeKind.Single
        'java.lang.Integer'    | EdmSimpleTypeKind.Int32
        'java.lang.Long'       | EdmSimpleTypeKind.Int64
        'java.lang.Short'      | EdmSimpleTypeKind.Int16
        'java.lang.String'     | EdmSimpleTypeKind.String
    }

    @Test
    def 'exception is thrown when map attribute value type is not a known primitive'() {
        given:
        def mapDescriptor = mapDescriptor('java.lang.String', 'UnknownType')
        typeDescriptor.getAttributes() >> [mapAttribute(mapDescriptor)]

        when:
        generator.generate(typeDescriptor)

        then:
        thrown IllegalArgumentException
    }

    @Test
    def 'generated entity type contains correct key produced by the key generator'() {
        given: 'key generator produces the key'
        def key = new Key()
        generator.keyGenerator = Stub(SchemaElementGenerator) {
            generate(_ as List<Property>) >> Optional.of(key)
        }
        and: 'the descriptor has a map attribute'
        typeDescriptor.getAttributes() >> [mapAttribute()]

        when:
        def items = generator.generate(typeDescriptor)

        then:
        items.first().key.is key
    }

    @Test
    def 'generated entity type has null key when generator did not produce a key'() {
        given: 'key generator produces the key'
        generator.keyGenerator = Stub(SchemaElementGenerator) {
            generate(_ as List<Property>) >> Optional.empty()
        }
        and: 'the descriptor has a map attribute'
        typeDescriptor.getAttributes() >> [mapAttribute()]

        when:
        def items = generator.generate(typeDescriptor)

        then:
        !items.first().key
    }

    @Test
    def 'generated entity type has null key when generator is not injected'() {
        given: 'generator does not have a key generator injected'
        generator.keyGenerator = null
        and: 'the descriptor has a map attribute'
        typeDescriptor.getAttributes() >> [mapAttribute()]

        when:
        def items = generator.generate(typeDescriptor)

        then:
        !items.first().key
    }

    TypeAttributeDescriptor mapAttribute(String mapTypeCode = 'Map') {
        mapAttribute(mapDescriptor(), mapTypeCode)
    }

    MapDescriptor mapDescriptor(String keyType = 'java.lang.String', String valueType = 'java.lang.String') {
        Stub(MapDescriptor) {
            getKeyType() >> primitiveTypeDescriptor(keyType)
            getValueType() >> primitiveTypeDescriptor(valueType)
        }
    }

    TypeAttributeDescriptor mapAttribute(MapDescriptor mapType, String mapTypeCode = 'Map') {
        Stub(TypeAttributeDescriptor) {
            isMap() >> true
            getMapDescriptor() >> Optional.of(mapType)
            getAttributeType() >> typeDescriptor(mapTypeCode)
        }
    }

    TypeDescriptor primitiveTypeDescriptor(String code) {
        Stub(TypeDescriptor) {
            getItemCode() >> code
            isPrimitive() >> true
        }
    }

    TypeDescriptor typeDescriptor(String code) {
        Stub(TypeDescriptor) {
            getItemCode() >> code
        }
    }

    TypeAttributeDescriptor nonMapAttribute() {
        Stub(TypeAttributeDescriptor) {
            isMap() >> false
        }
    }

    TypeAttributeDescriptor localizedAttribute() {
        Stub(TypeAttributeDescriptor) {
            isMap() >> true
            isLocalized() >> true
        }
    }

    TypeDescriptor nonPrimitiveTypeDescriptor() {
        Stub(TypeDescriptor) {
            isPrimitive() >> false
        }
    }

    SimpleProperty findProperty(EntityType entity, String name) {
        entity.properties.find { it.name == name } as SimpleProperty
    }
}

