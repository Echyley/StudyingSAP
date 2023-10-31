/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.odata2services.odata.schema.navigation

import de.hybris.bootstrap.annotations.UnitTest
import de.hybris.platform.integrationservices.model.TypeDescriptor
import de.hybris.platform.odata2services.odata.schema.SchemaElementGenerator
import de.hybris.platform.testframework.JUnitPlatformSpecification
import org.apache.olingo.odata2.api.edm.provider.NavigationProperty
import org.junit.Test
import spock.lang.Unroll

@UnitTest
class NavigationPropertyListGeneratorRegistryUnitTest extends JUnitPlatformSpecification {
	def registry = new NavigationPropertyListGeneratorRegistry()

	@Test
    @Unroll
	def "resulting list contains 0 navigation properties from #generators generator list"() {
		given:
		registry.schemaElementGenerators = generators

		expect:
		registry.generate(Stub(TypeDescriptor)).empty

        where:
        generators << [null, []]
	}

	@Test
	@Unroll
	def "resulting list contains #resultSize navigation properties from #generators.size creators"() {
		given:
		registry.setSchemaElementGenerators(generators)

		when:
		def result = registry.generate(Stub(TypeDescriptor))

		then:
		result.size() == resultSize

		where:
		generators                                                                                   | resultSize
		[schemaElementGeneratorWithNumberOfResults(2), schemaElementGeneratorWithNumberOfResults(1)] | 3
		[schemaElementGeneratorWithNumberOfResults(1), schemaElementGeneratorWithNumberOfResults(0)] | 1
		[schemaElementGeneratorWithNumberOfResults(5)]                                               | 5
		[]                                                                                           | 0
	}

	@Test
	def 'setting schemaElementGenerators to null results in an empty list of generators'() {
		given:
		registry.schemaElementGenerators = null

		expect:
		registry.schemaElementGenerators.empty
	}

	@Test
	def "setting schemaElementGenerators do not leak references when generator list is not null"() {
		given:
		def generators = [schemaElementGeneratorWithNumberOfResults(5)]

		when:
		registry.schemaElementGenerators = generators
		and:
		generators.clear()

		then:
		!registry.schemaElementGenerators.empty
	}

	@Test
	def "getting schemaElementGenerators do not leak references"() {
		given:
		def generators = [schemaElementGeneratorWithNumberOfResults(5)]
		registry.schemaElementGenerators = generators
		and:
		def copy = registry.schemaElementGenerators

		when:
		copy.clear()

		then:
		registry.schemaElementGenerators == generators
	}

	@Test
	def 'setting generators to null results in an empty list of generators'() {
		given:
		registry.generators = null

		expect:
		registry.generators.empty
	}

	@Test
	def "setting generators does not leak references"() {
		given:
		def generators = [schemaElementGeneratorWithNumberOfResults(1)]

		when:
		registry.generators = generators
		and:
		generators.clear()

		then:
		!registry.generators.empty
	}

	@Test
	def "getting generators does not leak references"() {
		given:
		def generators = [schemaElementGeneratorWithNumberOfResults(1)]
		registry.generators = generators
		and:
		def copy = registry.generators

		when:
		copy.clear()

		then:
		registry.generators == generators
	}

	def schemaElementGeneratorWithNumberOfResults(def count) {
		Stub(SchemaElementGenerator) {
			generate(_ as TypeDescriptor) >> [Stub(NavigationProperty)] * count
		}
	}
}
