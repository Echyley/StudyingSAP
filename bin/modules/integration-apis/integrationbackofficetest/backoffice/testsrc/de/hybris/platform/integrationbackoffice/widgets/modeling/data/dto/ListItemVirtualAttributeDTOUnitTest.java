/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.integrationbackoffice.widgets.modeling.data.dto;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.assertEquals;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.type.ComposedTypeModel;
import de.hybris.platform.core.model.type.TypeModel;
import de.hybris.platform.integrationbackoffice.TypeCreatorTestUtils;
import de.hybris.platform.integrationbackoffice.widgets.modeling.data.IntegrationObjectDefinition;
import de.hybris.platform.integrationservices.model.IntegrationObjectVirtualAttributeDescriptorModel;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class ListItemVirtualAttributeDTOUnitTest
{
	@Test
	public void testListItemVirtualAttributeDTOConstructor()
	{
		final TypeModel typeModel = new TypeModel();
		typeModel.setCode("Product");

		final IntegrationObjectVirtualAttributeDescriptorModel retrievalDescriptor = new IntegrationObjectVirtualAttributeDescriptorModel();
		retrievalDescriptor.setCode("Test retrieval");
		retrievalDescriptor.setLogicLocation("testpath");
		retrievalDescriptor.setType(typeModel);

		final AbstractListItemDTO actual = ListItemVirtualAttributeDTO.builder(retrievalDescriptor)
		                                                              .withSelected(true)
		                                                              .build();

		assertEquals(typeModel.getCode(), actual.getDescription());
	}

	@Test
	public void testFindMatchFound()
	{
		final TypeModel typeModel = new TypeModel();
		typeModel.setCode("Product");
		final IntegrationObjectVirtualAttributeDescriptorModel retrievalModel = new IntegrationObjectVirtualAttributeDescriptorModel();
		retrievalModel.setType(typeModel);

		final IntegrationObjectVirtualAttributeDescriptorModel retrievalModel2 = new IntegrationObjectVirtualAttributeDescriptorModel();
		retrievalModel2.setType(typeModel);

		final ComposedTypeModel compType1 = new ComposedTypeModel();
		final IntegrationMapKeyDTO mapKey1 = new IntegrationMapKeyDTO(compType1, "Product");

		final ListItemVirtualAttributeDTO dtoToMatch = ListItemVirtualAttributeDTO.builder(retrievalModel)
		                                                                          .withSelected(true)
		                                                                          .withAttributeName("product")
		                                                                          .build();
		final ListItemVirtualAttributeDTO dto2 = ListItemVirtualAttributeDTO.builder(retrievalModel)
		                                                                    .withSelected(true)
		                                                                    .withAttributeName("product2")
		                                                                    .build();

		final IntegrationObjectDefinition testMap = new IntegrationObjectDefinition();
		final List<AbstractListItemDTO> dtos = new ArrayList<>();
		dtos.add(dtoToMatch);
		dtos.add(dto2);
		testMap.setAttributesByKey(mapKey1, dtos);

		final AbstractListItemDTO result = dtoToMatch.findMatch(testMap, mapKey1);

		assertEquals(dtoToMatch, result);
	}

	@Test
	public void testFindMatchNotFound() throws NoSuchElementException
	{
		final TypeModel typeModel = new TypeModel();
		typeModel.setCode("Product");
		final IntegrationObjectVirtualAttributeDescriptorModel retrievalModel = new IntegrationObjectVirtualAttributeDescriptorModel();
		retrievalModel.setType(typeModel);

		final ComposedTypeModel compType1 = new ComposedTypeModel();
		final IntegrationMapKeyDTO mapKey1 = new IntegrationMapKeyDTO(compType1, "Product");

		final IntegrationObjectVirtualAttributeDescriptorModel retrievalModel2 = new IntegrationObjectVirtualAttributeDescriptorModel();
		retrievalModel2.setType(typeModel);

		final ListItemVirtualAttributeDTO dtoToMatch = ListItemVirtualAttributeDTO.builder(retrievalModel)
		                                                                          .withSelected(true)
		                                                                          .withAttributeName("product")
		                                                                          .build();
		final ListItemVirtualAttributeDTO dto2 = ListItemVirtualAttributeDTO.builder(retrievalModel2)
		                                                                    .withSelected(true)
		                                                                    .withAttributeName("product2")
		                                                                    .build();

		final IntegrationObjectDefinition testMap = new IntegrationObjectDefinition();
		final List<AbstractListItemDTO> dtos = new ArrayList<>();
		dtos.add(dto2);
		dtos.add(dto2);
		testMap.setAttributesByKey(mapKey1, dtos);

		assertThatThrownBy(() -> dtoToMatch.findMatch(testMap, mapKey1))
				.isInstanceOf(NoSuchElementException.class)
				.hasMessage("No matching VirtualAttribute was found.");
	}

	@Test
	public void testGetTypeCodeNoAlias()
	{
		final IntegrationObjectVirtualAttributeDescriptorModel virtualAttributeDescriptorModel = TypeCreatorTestUtils.integrationObjectVirtualAttributeDescriptorModel(
				"Test");

		final ListItemVirtualAttributeDTO dto = ListItemVirtualAttributeDTO.builder(virtualAttributeDescriptorModel).build();

		assertEquals("Test", dto.getAlias());
		assertEquals("String", dto.getTypeCode());
	}
}
