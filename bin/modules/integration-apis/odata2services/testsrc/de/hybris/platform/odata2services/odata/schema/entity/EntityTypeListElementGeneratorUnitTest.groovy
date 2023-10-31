/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.odata2services.odata.schema.entity


import de.hybris.platform.integrationservices.model.TypeDescriptor
import de.hybris.platform.odata2services.odata.schema.SchemaElementGenerator
import de.hybris.platform.testframework.JUnitPlatformSpecification
import org.apache.olingo.odata2.api.edm.provider.EntityType
import org.junit.Test

class EntityTypeListElementGeneratorUnitTest extends JUnitPlatformSpecification {

    def generator1 = Stub(SchemaElementGenerator)
    def generator2 = Stub(SchemaElementGenerator)
    def elementGenerator = new EntityTypeListElementGenerator(entityTypeGenerators: [generator1, generator2])

    @Test
    def "EntityTypeListElementGenerator does not leak references"() {
        given:
        def entityGenerators = [generator1]

        when:
        def generator = new EntityTypeListElementGenerator(entityTypeGenerators: entityGenerators)
        and: 'clearing the lists should not affect what is set'
        entityGenerators.clear()

        then:
        !generator.entityTypeGenerators.empty
    }

    @Test
    def "generate returns an empty list when #condition"() {
        given:
        elementGenerator.entityTypeGenerators = entityGenerators

        when:
        def typeList = elementGenerator.generate(descriptors)

        then:
        typeList.empty

        where:
        descriptors            | entityGenerators               | condition
        [Stub(TypeDescriptor)] | null                           | "entity type generators is null"
        [Stub(TypeDescriptor)] | []                             | "entity type generators is empty"
        null                   | [Stub(SchemaElementGenerator)] | "passed collection of type descriptors is null"
        []                     | [Stub(SchemaElementGenerator)] | "passed collection of type descriptors is empty"
    }

    @Test
    def "generate returns correct list of entity types for all descriptors using all generators"() {
        given:
        def descriptor1 = typeDescriptor()
        def descriptor2 = typeDescriptor()
        def entity1 = entityType('1')
        def entity2 = entityType('2')
        def entity3 = entityType('3')
        def entity4 = entityType('4')
        def entity5 = entityType('5')
        generator1.generate(descriptor1) >> [entity1, entity5]
        generator1.generate(descriptor2) >> [entity2]
        generator2.generate(descriptor1) >> [entity3]
        generator2.generate(descriptor2) >> [entity4]

        when:
        def typeList = elementGenerator.generate([descriptor1, descriptor2])

        then:
        with(typeList) {
            size() == 5
            containsAll([entity2, entity1, entity4, entity3, entity5])
        }
    }

    @Test
    def "generate returns list of entity types of distinct names only"() {
        given:
        def descriptor = typeDescriptor()
        def commonName = "some-name"
        def entity1 = entityType(commonName)
        def entity2 = entityType(commonName)
        def entity3 = entityType(commonName)
        generator1.generate(descriptor) >> [entity1, entity3]
        generator2.generate(descriptor) >> [entity2]

        when:
        def typeList = elementGenerator.generate([descriptor])

        then:
        with(typeList) {
            size() == 1
            get(0).name == commonName
        }
    }

    def typeDescriptor() {
        Stub(TypeDescriptor)
    }

    def entityType(name) {
        Stub(EntityType) { getName() >> name }
    }
}
