/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.integrationbackoffice.widgets.modeling.data;

import static org.junit.Assert.assertEquals;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.type.AttributeDescriptorModel;
import de.hybris.platform.core.model.type.ComposedTypeModel;
import de.hybris.platform.core.model.type.TypeModel;
import de.hybris.platform.integrationbackoffice.TypeCreatorTestUtils;
import de.hybris.platform.integrationbackoffice.widgets.modeling.data.dto.AbstractListItemDTO;
import de.hybris.platform.integrationbackoffice.widgets.modeling.data.dto.AttributeTypeDTO;
import de.hybris.platform.integrationbackoffice.widgets.modeling.data.dto.IntegrationMapKeyDTO;
import de.hybris.platform.integrationbackoffice.widgets.modeling.data.dto.ListItemAttributeDTO;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import org.junit.Test;

@UnitTest
public class IntegrationObjectDefinitionUnitTest
{
	private static final String ALIAS = "customDescription";

	private final IntegrationObjectDefinition integrationObjectDefinition = new IntegrationObjectDefinition();

	@Test
	public void returnAttributeWhenGetAttributeByAliasAndRegularAttributeExists()
	{
		final IntegrationMapKeyDTO parent = new IntegrationMapKeyDTO(new ComposedTypeModel(), "product");
		final TypeModel typeModel = TypeCreatorTestUtils.typeModel("notMatters");
		final AttributeDescriptorModel descriptorModel = TypeCreatorTestUtils.attributeDescriptorModel(typeModel);
		final AttributeTypeDTO attributeTypeDTO = AttributeTypeDTO.builder(descriptorModel).build();
		final ListItemAttributeDTO attribute = ListItemAttributeDTO.builder(attributeTypeDTO)
		                                                           .withSelected(true)
		                                                           .withAttributeName(ALIAS)
		                                                           .build();
		final List<AbstractListItemDTO> attributes = List.of(attribute);
		integrationObjectDefinition.setDefinitionMap(Map.of(parent, attributes));

		final ListItemAttributeDTO actualAttribute = integrationObjectDefinition.getAttributeByAlias(parent, ALIAS);

		assertEquals("Attribute returned has correct alias.", ALIAS, actualAttribute.getAlias());
	}

	@Test(expected = NoSuchElementException.class)
	public void exceptionWhenGetAttributeByAliasAndRegularAttributeDoesNotExist()
	{
		final IntegrationMapKeyDTO parent = new IntegrationMapKeyDTO(new ComposedTypeModel(), "product");
		integrationObjectDefinition.setDefinitionMap(Map.of(parent, Collections.emptyList()));
		integrationObjectDefinition.getAttributeByAlias(parent, ALIAS);
	}
}
