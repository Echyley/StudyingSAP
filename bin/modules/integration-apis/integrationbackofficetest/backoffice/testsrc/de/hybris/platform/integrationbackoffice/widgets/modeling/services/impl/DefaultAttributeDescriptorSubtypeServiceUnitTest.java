/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.integrationbackoffice.widgets.modeling.services.impl;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.type.AttributeDescriptorModel;
import de.hybris.platform.core.model.type.ComposedTypeModel;
import de.hybris.platform.integrationbackoffice.widgets.modeling.builders.DataStructureBuilder;
import de.hybris.platform.integrationbackoffice.widgets.modeling.data.SubtypeData;
import de.hybris.platform.integrationbackoffice.widgets.modeling.data.dto.IntegrationMapKeyDTO;
import de.hybris.platform.integrationbackoffice.widgets.modeling.services.ReadService;

import java.util.HashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

@UnitTest
public class DefaultAttributeDescriptorSubtypeServiceUnitTest
{
	private ReadService readService;
	private DataStructureBuilder dataStructureBuilder;
	private DefaultAttributeDescriptorSubtypeService attributeDescriptorSubtypeService;

	private AttributeDescriptorModel attributeDescriptor;
	private final String attributeDescriptorQualifier = "product";

	private final ComposedTypeModel type = new ComposedTypeModel();
	private final ComposedTypeModel subtype = new ComposedTypeModel();

	private final IntegrationMapKeyDTO parentKeyDTO = mock(IntegrationMapKeyDTO.class);
	private final Set<SubtypeData> subtypeDataSet = new HashSet<>();

	@Before
	public void setUp()
	{
		readService = mock(ReadService.class);
		dataStructureBuilder = mock(DataStructureBuilder.class);
		attributeDescriptorSubtypeService = new DefaultAttributeDescriptorSubtypeService(readService, dataStructureBuilder);

		attributeDescriptor = new AttributeDescriptorModel();
		attributeDescriptor.setQualifier(attributeDescriptorQualifier);

		when(readService.getComplexTypeForAttributeDescriptor(attributeDescriptor)).thenReturn(type);
	}

	@Test
	public void returnTypeWhenFindSubtypeNotFound()
	{
		when(dataStructureBuilder.findSubtypeMatch(parentKeyDTO, attributeDescriptorQualifier, type, subtypeDataSet))
				.thenReturn(null);

		final ComposedTypeModel actualSubtype = attributeDescriptorSubtypeService.findSubtype(attributeDescriptor, subtypeDataSet,
				parentKeyDTO);

		assertEquals("Type of attribute is returned.", type, actualSubtype);
	}

	@Test
	public void returnSubtypeWhenFindSubtypeFound()
	{
		when(dataStructureBuilder.findSubtypeMatch(parentKeyDTO, attributeDescriptorQualifier, type, subtypeDataSet))
				.thenReturn(subtype);

		final ComposedTypeModel actualSubtype = attributeDescriptorSubtypeService.findSubtype(attributeDescriptor, subtypeDataSet,
				parentKeyDTO);

		assertEquals("Subtype of attribute is returned.", subtype, actualSubtype);
	}
}
