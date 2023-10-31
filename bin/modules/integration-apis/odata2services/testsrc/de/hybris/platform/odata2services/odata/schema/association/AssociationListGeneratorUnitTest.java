/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.odata2services.odata.schema.association;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;


import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.integrationservices.model.DescriptorFactory;
import de.hybris.platform.integrationservices.model.IntegrationObjectItemAttributeModel;
import de.hybris.platform.integrationservices.model.IntegrationObjectItemModel;
import de.hybris.platform.integrationservices.model.TypeAttributeDescriptor;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.apache.olingo.odata2.api.edm.provider.Association;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class AssociationListGeneratorUnitTest
{
	private static final IntegrationObjectItemAttributeModel SIMPLE_ATTRIBUTE = new IntegrationObjectItemAttributeModel();
	private static final TypeAttributeDescriptor ATTRIBUTE_DESCRIPTOR = mock(TypeAttributeDescriptor.class);
	private static final Association ASSOCIATION = new Association();

	@InjectMocks
	private AssociationListGenerator associationListGenerator;
	@Mock(lenient = true)
	private AssociationGeneratorRegistry mockAssociationGeneratorRegistry;
	@Mock(lenient = true)
	private AssociationGenerator mockAssociationGenerator;
	@Mock(lenient = true)
	private DescriptorFactory descriptorFactory;

	@Before
	public void setup()
	{
		doReturn(ATTRIBUTE_DESCRIPTOR).when(descriptorFactory).createTypeAttributeDescriptor(SIMPLE_ATTRIBUTE);
		doReturn(Optional.of(mockAssociationGenerator))
				.when(mockAssociationGeneratorRegistry).getAssociationGenerator(ATTRIBUTE_DESCRIPTOR);
		doReturn(ASSOCIATION).when(mockAssociationGenerator).generate(ATTRIBUTE_DESCRIPTOR);
	}

	@Test
	public void testGenerateForAssociationPresent()
	{
		final IntegrationObjectItemModel item = integrationObjectItem(SIMPLE_ATTRIBUTE);

		final List<Association> associationList = associationListGenerator.generate(Set.of(item));

		assertThat(associationList).containsOnly(ASSOCIATION);
	}

	@Test
	public void testGenerateForNoAssociations()
	{
		lenient().when(mockAssociationGeneratorRegistry.getAssociationGenerator(ATTRIBUTE_DESCRIPTOR)).thenReturn(Optional.empty());
		final IntegrationObjectItemModel item = integrationObjectItem(SIMPLE_ATTRIBUTE);

		final List<Association> associationList = associationListGenerator.generate(Set.of(item));

		assertThat(associationList).isEmpty();
	}

	@Test
	public void testGeneratorForNotApplicableAssociation()
	{
		lenient().when(mockAssociationGenerator.generate(ATTRIBUTE_DESCRIPTOR)).thenReturn(null);
		final IntegrationObjectItemModel item = integrationObjectItem(SIMPLE_ATTRIBUTE);

		final List<Association> associationList = associationListGenerator.generate(Set.of(item));

		assertThat(associationList).isEmpty();
	}

	@Test
	public void testGenerateNullParameter()
	{
		assertThatThrownBy(() -> associationListGenerator.generate(null))
				.isInstanceOf(IllegalArgumentException.class);
	}

	private IntegrationObjectItemModel integrationObjectItem(final IntegrationObjectItemAttributeModel... attributes)
	{
		final var item = new IntegrationObjectItemModel();
		item.setAttributes(Set.of(attributes));
		return item;
	}
}
