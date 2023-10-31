/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.integrationbackoffice.widgets.editor.utility;

import static de.hybris.platform.testframework.Assert.assertEquals;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.catalog.enums.ClassificationAttributeTypeEnum;
import de.hybris.platform.core.model.type.AttributeDescriptorModel;
import de.hybris.platform.core.model.type.ComposedTypeModel;
import de.hybris.platform.core.model.type.TypeModel;
import de.hybris.platform.integrationbackoffice.TypeCreatorTestUtils;
import de.hybris.platform.integrationbackoffice.widgets.modeling.data.IntegrationObjectDefinition;
import de.hybris.platform.integrationbackoffice.widgets.modeling.data.IntegrationObjectDefinitionDuplicationMap;
import de.hybris.platform.integrationbackoffice.widgets.modeling.data.dto.AbstractListItemDTO;
import de.hybris.platform.integrationbackoffice.widgets.modeling.data.dto.AttributeTypeDTO;
import de.hybris.platform.integrationbackoffice.widgets.modeling.data.dto.IntegrationMapKeyDTO;
import de.hybris.platform.integrationbackoffice.widgets.modeling.data.dto.ListItemAttributeDTO;
import de.hybris.platform.integrationbackoffice.widgets.modeling.data.dto.ListItemClassificationAttributeDTO;
import de.hybris.platform.integrationbackoffice.widgets.modeling.utility.EditorValidator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

@UnitTest
public class EditorValidatorUnitTest
{
	private final ComposedTypeModel c1 = new ComposedTypeModel();
	private final ComposedTypeModel c2 = new ComposedTypeModel();
	private final ComposedTypeModel c3 = new ComposedTypeModel();

	private IntegrationMapKeyDTO m1;
	private IntegrationMapKeyDTO m2;
	private IntegrationMapKeyDTO m3;

	private final AttributeDescriptorModel a1 = new AttributeDescriptorModel();
	private final AttributeDescriptorModel baseADM = new AttributeDescriptorModel();

	private AttributeTypeDTO a1Type;
	private AttributeTypeDTO baseType;

	@Before
	public void setup()
	{
		c1.setCode("Comp1");
		c2.setCode("Comp2");
		c3.setCode("Comp3");

		m1 = new IntegrationMapKeyDTO(c1, c1.getCode());
		m2 = new IntegrationMapKeyDTO(c2, c2.getCode());
		m3 = new IntegrationMapKeyDTO(c3, c3.getCode());

		final TypeModel t = new TypeModel();

		t.setCode("");
		a1.setUnique(true);
		a1.setAttributeType(t);
		baseADM.setAttributeType(t);
		baseADM.setUnique(false);

		a1Type = AttributeTypeDTO.builder(a1).build();
		baseType = AttributeTypeDTO.builder(baseADM).build();
	}

	@Test
	public void validateDefinitionsValidTest()
	{
		final IntegrationObjectDefinition validMap = new IntegrationObjectDefinition();

		final ListItemAttributeDTO itemSelect = ListItemAttributeDTO.builder(baseType)
		                                                            .withSelected(true)
		                                                            .build();
		final ListItemAttributeDTO itemNotSelect = ListItemAttributeDTO.builder(baseType).build();

		final List<AbstractListItemDTO> l1 = new ArrayList<>();
		final List<AbstractListItemDTO> l2 = new ArrayList<>();
		final List<AbstractListItemDTO> l3 = new ArrayList<>();

		l1.add(itemNotSelect);
		l1.add(itemNotSelect);
		l1.add(itemNotSelect);
		l1.add(itemSelect);

		l2.add(itemNotSelect);
		l2.add(itemNotSelect);
		l2.add(itemSelect);
		l2.add(itemSelect);

		l3.add(itemSelect);
		l3.add(itemNotSelect);
		l3.add(itemNotSelect);
		l3.add(itemSelect);

		validMap.setAttributesByKey(m1, l1);
		validMap.setAttributesByKey(m2, l2);
		validMap.setAttributesByKey(m3, l3);

		assertEquals("", EditorValidator.validateDefinitions(validMap));
	}

	@Test
	public void validateDefinitionsInvalidTest()
	{
		final IntegrationObjectDefinition invalidMap = new IntegrationObjectDefinition();

		final ListItemAttributeDTO itemSelect = ListItemAttributeDTO.builder(baseType)
		                                                            .withSelected(true)
		                                                            .build();
		final ListItemAttributeDTO itemNotSelect = ListItemAttributeDTO.builder(baseType).build();

		final List<AbstractListItemDTO> l1 = new ArrayList<>();
		final List<AbstractListItemDTO> l2 = new ArrayList<>();
		final List<AbstractListItemDTO> l3 = new ArrayList<>();

		//l1 has nothing

		l2.add(itemNotSelect);
		l2.add(itemNotSelect);
		l2.add(itemSelect);
		l2.add(itemSelect);

		l3.add(itemSelect);
		l3.add(itemNotSelect);
		l3.add(itemNotSelect);
		l3.add(itemSelect);

		invalidMap.setAttributesByKey(m1, l1);
		invalidMap.setAttributesByKey(m2, l2);
		invalidMap.setAttributesByKey(m3, l3);

		assertEquals("Comp1", EditorValidator.validateDefinitions(invalidMap));
	}

	@Test
	public void validateHasKeyValidTest()
	{
		final IntegrationObjectDefinition validMap = new IntegrationObjectDefinition();

		final ListItemAttributeDTO itemUnique = ListItemAttributeDTO.builder(a1Type)
		                                                            .withSelected(true)
		                                                            .build();
		final ListItemAttributeDTO itemCustomUnique = ListItemAttributeDTO.builder(baseType)
		                                                                  .withSelected(true)
		                                                                  .withCustomUnique(true)
		                                                                  .build();
		final ListItemAttributeDTO itemSelect = ListItemAttributeDTO.builder(baseType)
		                                                            .withSelected(true)
		                                                            .build();
		final ListItemAttributeDTO itemNotSelect = ListItemAttributeDTO.builder(baseType).build();

		final List<AbstractListItemDTO> l1 = new ArrayList<>();
		final List<AbstractListItemDTO> l2 = new ArrayList<>();
		final List<AbstractListItemDTO> l3 = new ArrayList<>();

		l1.add(itemNotSelect);
		l1.add(itemNotSelect);
		l1.add(itemNotSelect);
		l1.add(itemUnique);

		l2.add(itemNotSelect);
		l2.add(itemNotSelect);
		l2.add(itemSelect);
		l2.add(itemSelect);
		l2.add(itemCustomUnique);

		l3.add(itemSelect);
		l3.add(itemNotSelect);
		l3.add(itemNotSelect);
		l3.add(itemSelect);
		l3.add(itemUnique);
		l3.add(itemCustomUnique);

		validMap.setAttributesByKey(m1, l1);
		validMap.setAttributesByKey(m2, l2);
		validMap.setAttributesByKey(m3, l3);

		assertEquals("", EditorValidator.validateHasKey(validMap));
	}

	@Test
	public void validateHasKeyInvalidTest()
	{
		final IntegrationObjectDefinition validMap = new IntegrationObjectDefinition();

		final ListItemAttributeDTO itemUnique = ListItemAttributeDTO.builder(a1Type)
		                                                            .withSelected(true)
		                                                            .build();
		final ListItemAttributeDTO itemCustomUnique = ListItemAttributeDTO.builder(baseType)
		                                                                  .withSelected(true)
		                                                                  .withCustomUnique(true)
		                                                                  .build();
		final ListItemAttributeDTO itemSelect = ListItemAttributeDTO.builder(baseType)
		                                                            .withSelected(true)
		                                                            .build();
		final ListItemAttributeDTO itemNotSelect = ListItemAttributeDTO.builder(baseType).build();

		final List<AbstractListItemDTO> l1 = new ArrayList<>();
		final List<AbstractListItemDTO> l2 = new ArrayList<>();
		final List<AbstractListItemDTO> l3 = new ArrayList<>();

		l1.add(itemNotSelect);
		l1.add(itemNotSelect);
		l1.add(itemNotSelect);
		l1.add(itemUnique);

		l2.add(itemNotSelect);
		l2.add(itemNotSelect);
		l2.add(itemSelect);
		l2.add(itemSelect);

		l3.add(itemSelect);
		l3.add(itemNotSelect);
		l3.add(itemNotSelect);
		l3.add(itemSelect);
		l3.add(itemUnique);
		l3.add(itemCustomUnique);

		validMap.setAttributesByKey(m1, l1);
		validMap.setAttributesByKey(m2, l2);
		validMap.setAttributesByKey(m3, l3);

		assertEquals("Comp2", EditorValidator.validateHasKey(validMap));
	}

	@Test
	public void validateHasNoDuplicateAttributeNamesTest()
	{
		final ComposedTypeModel c1 = new ComposedTypeModel();
		final ComposedTypeModel c2 = new ComposedTypeModel();

		c1.setCode("Product");
		c2.setCode("ProductSubtype");

		final IntegrationMapKeyDTO prodKey = new IntegrationMapKeyDTO(c1, "Product");
		final IntegrationMapKeyDTO subKey = new IntegrationMapKeyDTO(c2, "ProductSubtype");

		final ClassificationAttributeTypeEnum e = ClassificationAttributeTypeEnum.NUMBER;
		final ListItemClassificationAttributeDTO dto1 = TypeCreatorTestUtils.createClassificationAttributeDTO("attr1", "q1", e, "category");
		dto1.setAlias("attrAlias");

		final IntegrationObjectDefinitionDuplicationMap dupeMap = new IntegrationObjectDefinitionDuplicationMap();
		final Map<String, List<AbstractListItemDTO>> innerMap = new HashMap<>();

		final List<AbstractListItemDTO> list = new ArrayList<>();
		list.add(dto1);
		list.add(dto1);

		innerMap.put("q1", list);
		dupeMap.setAttributesByKey(prodKey, innerMap);
		dupeMap.setAttributesByKey(subKey, innerMap);

		final List<String> expected = new ArrayList<>();
		expected.add("Product");
		expected.add("ProductSubtype");

		final String result = EditorValidator.validateHasNoDuplicateAttributeNames(dupeMap);
		final List<String> actual = Arrays.asList(result.split(", "));

		assert (actual.containsAll(expected));
	}
}
