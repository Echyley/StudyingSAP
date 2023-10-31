/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.odata2services.odata.schema.entity

import de.hybris.bootstrap.annotations.UnitTest
import de.hybris.platform.integrationservices.model.IntegrationObjectItemModel
import de.hybris.platform.odata2services.odata.schema.SchemaElementGenerator
import de.hybris.platform.testframework.JUnitPlatformSpecification
import org.apache.olingo.odata2.api.edm.provider.EntityType
import org.junit.Test

@UnitTest
class EntityTypeListGeneratorUnitTest extends JUnitPlatformSpecification {
	def generator1 = Stub(SchemaElementGenerator)
	def generator2 = Stub(SchemaElementGenerator)
	def entityTypeListGenerator = new EntityTypeListGenerator()

	def setup() {
		entityTypeListGenerator.setEntityTypeGenerators([generator1, generator2])
	}

	@Test
	def "setEntityTypeGenerator does not leak references when generator list is not null"() {
		given:
		def generators = [generator1]

		when:
		entityTypeListGenerator.entityTypeGenerators = generators
		and: 'clearing the lists should not affect what is set'
		generators.clear()

		then:
		!entityTypeListGenerator.entityTypeGenerators.empty
	}

	@Test
	def "setEntityTypeGenerator does not leak references when generator list is null"() {
		given:
		entityTypeListGenerator.entityTypeGenerators = null

		expect:
		entityTypeListGenerator.entityTypeGenerators.empty
	}

	@Test
	def "entity list generator returns unique entity types"() {
		given:
		def addressEntityType = givenEntityType("address")
		def integerEntityType = givenEntityType("integer")
		def longEntityType = givenEntityType("long")
		generator1.generate(_) >> [addressEntityType, integerEntityType]
		generator2.generate(_) >> [integerEntityType, longEntityType]

		expect:
		def result = entityTypeListGenerator.generate([Stub(IntegrationObjectItemModel)])
		result.size() == 3
		result.containsAll([integerEntityType, addressEntityType, longEntityType])
	}

	@Test
	def "null integrationObjectItemModel collection fails precondition check"() {
		when:
		entityTypeListGenerator.generate(null)

		then:
		thrown(IllegalArgumentException)
	}

	def "empty collection results in empty list"() {
		when:
		def typeList = entityTypeListGenerator.generate(Collections.emptyList())

		then:
		typeList.size() == 0
	}

	def givenEntityType(def name) {
		def entityType = Stub(EntityType)
		entityType.getName() >> name
		return entityType
	}
}
