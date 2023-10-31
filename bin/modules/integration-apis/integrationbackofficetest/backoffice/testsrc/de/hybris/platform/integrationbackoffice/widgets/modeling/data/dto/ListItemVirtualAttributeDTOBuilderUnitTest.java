/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.integrationbackoffice.widgets.modeling.data.dto;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.type.TypeModel;
import de.hybris.platform.integrationservices.model.IntegrationObjectVirtualAttributeDescriptorModel;

import org.apache.commons.lang.StringUtils;
import org.junit.Before;
import org.junit.Test;

@UnitTest
public class ListItemVirtualAttributeDTOBuilderUnitTest
{
	private ListItemVirtualAttributeDTOBuilder builder;
	private final IntegrationObjectVirtualAttributeDescriptorModel retrievalDescriptor = new IntegrationObjectVirtualAttributeDescriptorModel();
	private static final String RETRIEVAL_DESCRIPTOR_CODE = "code";

	@Before
	public void init()
	{
		retrievalDescriptor.setType(new TypeModel());
		retrievalDescriptor.setCode(RETRIEVAL_DESCRIPTOR_CODE);
		builder = new ListItemVirtualAttributeDTOBuilder(retrievalDescriptor);
	}

	@Test
	public void instantiatesListItemWithDefaultValuesWhenNoArgumentsProvided()
	{
		final ListItemVirtualAttributeDTO actual = builder.build();
		assertFalse("Selected field is false.", actual.isSelected());
		assertFalse("Custom unique field is false.", actual.isCustomUnique());
		assertFalse("Autocreate field is false.", actual.isAutocreate());
		assertEquals("Attribute name field is retrieval descriptor code.", RETRIEVAL_DESCRIPTOR_CODE, actual.getAlias());
		assertEquals("Mandatory retrieval descriptor field is set.", retrievalDescriptor, actual.getRetrievalDescriptor());
	}

	@Test
	public void instantiatesWithValuesProvidedWhenArgumentsProvided()
	{
		final String attributeName = "localPrice";
		final ListItemVirtualAttributeDTO actual = builder.withSelected(true)
		                                                  .withCustomUnique(true)
		                                                  .withAutocreate(true)
		                                                  .withAttributeName(attributeName)
		                                                  .build();
		assertTrue("Selected field is set.", actual.isSelected());
		assertTrue("Custom unique field is set.", actual.isCustomUnique());
		assertTrue("Autocreate field is set.", actual.isAutocreate());
		assertEquals("Attribute name field is set.", attributeName, actual.getAlias());
		assertEquals("Retrieval descriptor field is set.", retrievalDescriptor, actual.getRetrievalDescriptor());
	}
}
