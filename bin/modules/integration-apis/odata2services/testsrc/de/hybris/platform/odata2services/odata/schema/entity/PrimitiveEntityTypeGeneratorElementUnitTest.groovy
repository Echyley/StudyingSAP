/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.odata2services.odata.schema.entity

import de.hybris.bootstrap.annotations.UnitTest
import de.hybris.platform.integrationservices.model.TypeAttributeDescriptor
import de.hybris.platform.integrationservices.model.TypeDescriptor
import de.hybris.platform.testframework.JUnitPlatformSpecification
import org.apache.olingo.odata2.api.edm.provider.EntityType
import org.junit.Test

@UnitTest
class PrimitiveEntityTypeGeneratorElementUnitTest extends JUnitPlatformSpecification {

    private static final def JAVA_STRING_CLASS_PATH = "java.lang.String"
    private static final def ENTITY_TYPE = new EntityType()

    def elementTypeDescriptor = Stub TypeDescriptor
    def typeAttributeDescriptor = Stub TypeAttributeDescriptor
    def itemTypeDescriptor = Stub TypeDescriptor
    def primitiveCollectionMemberEntityTypeGenerator = Stub PrimitiveCollectionMemberEntityTypeGenerator
    def primitiveEntityTypeElementGenerator = new PrimitiveEntityTypeElementGenerator(
            primitiveCollectionMemberEntityTypeGenerator: primitiveCollectionMemberEntityTypeGenerator)

    def setup() {
        itemTypeDescriptor.getAttributes() >> [typeAttributeDescriptor]
        typeAttributeDescriptor.getAttributeType() >> elementTypeDescriptor
        elementTypeDescriptor.getItemCode() >> JAVA_STRING_CLASS_PATH

        primitiveCollectionMemberEntityTypeGenerator.generate(_ as String) >> ENTITY_TYPE
    }

    @Test
    def "generator returns empty set when type descriptor is null"() {
        expect:
        primitiveEntityTypeElementGenerator.generate(null) == [] as Set
    }

    @Test
    def "generate returns #result when #condition"() {
        given:
        descriptorHasCollectionAttribute(collectionFlag)
        descriptorHasPrimitiveAttribute(primitiveFlag)

        when:
        def generatedEntityTypes = primitiveEntityTypeElementGenerator.generate(itemTypeDescriptor)

        then:
        generatedEntityTypes == returnValue as Set

        where:
        condition                                               | collectionFlag | primitiveFlag | returnValue   | result
        "type descriptor has no collection attribute"           | false          | true          | []            | "empty set"
        "type descriptor has no primitive collection attribute" | true           | false         | []            | "empty set"
        "type descriptor has primitive collection attribute"    | true           | true          | [ENTITY_TYPE] | "correct entity type"

    }

    def descriptorHasPrimitiveAttribute(boolean b) {
        elementTypeDescriptor.isPrimitive() >> b
    }

    def descriptorHasCollectionAttribute(boolean b) {
        typeAttributeDescriptor.isCollection() >> b
    }
}
