/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.odata2services.odata.schema.association

import de.hybris.bootstrap.annotations.UnitTest
import de.hybris.platform.integrationservices.model.IntegrationObjectItemAttributeModel
import de.hybris.platform.integrationservices.model.TypeAttributeDescriptor
import de.hybris.platform.testframework.JUnitPlatformSpecification
import org.junit.Test

@UnitTest
class AssociationGeneratorRegistryUnitTest extends JUnitPlatformSpecification {
	def oneToOneAssociationGenerator = Stub(OneToOneAssociationGenerator)
	def complexAssociationGenerator = Stub(AssociationGenerator)
	def attribute = Stub(IntegrationObjectItemAttributeModel)
	def attributeDescriptor = Stub(TypeAttributeDescriptor)

	private AssociationGeneratorRegistry registry = new AssociationGeneratorRegistry(
			associationGenerators: [oneToOneAssociationGenerator, complexAssociationGenerator]
	)

	@Test
	def "getAssociationGenerator found for attributeDescriptor"()
	{
		given:
		oneToOneAssociationGenerator.isApplicable(attributeDescriptor) >> true

		when:
		final Optional<AssociationGenerator> associationGeneratorOptional = registry.getAssociationGenerator(attributeDescriptor)

		then:
		associationGeneratorOptional == Optional.of(oneToOneAssociationGenerator)
	}

	@Test
	def "getAssociationGenerator is not found for attributeDescriptor"()
	{
		given:
		oneToOneAssociationGenerator.isApplicable(attributeDescriptor) >> false

		when:
		final Optional<AssociationGenerator> associationGeneratorOptional = registry.getAssociationGenerator(attributeDescriptor)

		then:
		associationGeneratorOptional.isEmpty()
	}

	@Test
	def "getAssociationGenerator with null attribute descriptor"()
	{
		when:
		final Optional<AssociationGenerator> associationGeneratorOptional = registry.getAssociationGenerator(null as TypeAttributeDescriptor)

		then:
		associationGeneratorOptional.isEmpty()
	}
}
