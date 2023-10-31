/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.integrationbackoffice.widgets.modeling.services.impl;

import static de.hybris.platform.integrationbackoffice.IntegrationbackofficetestUtils.attributeDescriptorModel;
import static de.hybris.platform.integrationbackoffice.IntegrationbackofficetestUtils.composedTypeModel;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.type.AttributeDescriptorModel;
import de.hybris.platform.core.model.type.ComposedTypeModel;
import de.hybris.platform.integrationbackoffice.widgets.modeling.data.SubtypeData;
import de.hybris.platform.integrationbackoffice.widgets.modeling.data.TreeNodeData;
import de.hybris.platform.integrationbackoffice.widgets.modeling.data.dto.AbstractListItemDTO;
import de.hybris.platform.integrationbackoffice.widgets.modeling.data.dto.AttributeTypeDTO;
import de.hybris.platform.integrationbackoffice.widgets.modeling.data.dto.IntegrationMapKeyDTO;
import de.hybris.platform.integrationbackoffice.widgets.modeling.data.dto.ListItemAttributeDTO;
import de.hybris.platform.integrationbackoffice.widgets.modeling.services.AttributeDescriptorSubtypeService;
import de.hybris.platform.integrationbackoffice.widgets.modeling.services.ReadService;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

@UnitTest
public class DefaultTreeNodeDataSetGeneratorUnitTest
{
	private DefaultTreeNodeDataSetGenerator treeNodeDataSetGenerator;
	private ReadService readService;
	private AttributeDescriptorSubtypeService attrDescSubtypeService;

	private static final String TYPE_CODE1 = "Product";
	private static final ComposedTypeModel TYPE1 = composedTypeModel(TYPE_CODE1);
	private static final String DESCRIPTOR_QUALIFIER1 = "product";
	private static final AttributeDescriptorModel DESCRIPTOR1 = attributeDescriptorModel(TYPE1, DESCRIPTOR_QUALIFIER1);
	private static final AttributeTypeDTO ATTRIBUTE_TYPE_DTO1 = AttributeTypeDTO.builder(DESCRIPTOR1).build();
	private static final String ATTRIBUTE_ALIAS1 = "product1";
	private static final String TYPE_ALIAS1 = "Product1";
	private static final ListItemAttributeDTO ATTRIBUTE_DTO1 = ListItemAttributeDTO.builder(ATTRIBUTE_TYPE_DTO1)
	                                                                               .withAttributeName(ATTRIBUTE_ALIAS1)
	                                                                               .withTypeAlias(TYPE_ALIAS1)
	                                                                               .build();

	private static final Set<SubtypeData> SUBTYPE_DATA_SET = Collections.emptySet();
	private static final IntegrationMapKeyDTO PARENT_KEY = mock(IntegrationMapKeyDTO.class);

	@Before
	public void setUp()
	{
		readService = mock(ReadService.class);
		attrDescSubtypeService = mock(AttributeDescriptorSubtypeService.class);
		treeNodeDataSetGenerator = new DefaultTreeNodeDataSetGenerator(readService, attrDescSubtypeService);
	}

	@Test
	public void treeNodeDataWithAliasCreatedWhenGenerateAndAttributeExists()
	{
		// setup
		when(readService.getComplexTypeForAttributeDescriptor(DESCRIPTOR1)).thenReturn(TYPE1);
		when(attrDescSubtypeService.findSubtype(DESCRIPTOR1, SUBTYPE_DATA_SET, PARENT_KEY)).thenReturn(TYPE1);

		final Set<AttributeDescriptorModel> attributes = Set.of(DESCRIPTOR1);
		final List<AbstractListItemDTO> existingAttributes = List.of(ATTRIBUTE_DTO1);

		// test
		final Set<TreeNodeData> treeNodeDataSet = treeNodeDataSetGenerator.generate(attributes, existingAttributes,
				SUBTYPE_DATA_SET, PARENT_KEY);

		final TreeNodeData treeNodeData = treeNodeDataSet.iterator().next();
		final String attributeAlias = treeNodeData.getAlias();
		final String typeAlias = treeNodeData.getMapKeyDTO().getCode();

		assertEquals("Tree node data has attribute alias.", ATTRIBUTE_ALIAS1, attributeAlias);
		assertEquals("Tree node data has type alias.", TYPE_ALIAS1, typeAlias);
	}

	@Test
	public void treeNodeDataWithQualifierCreatedWhenGenerateAndAttributeDoesNotExist()
	{
		// setup
		when(readService.getComplexTypeForAttributeDescriptor(DESCRIPTOR1)).thenReturn(TYPE1);
		when(attrDescSubtypeService.findSubtype(DESCRIPTOR1, SUBTYPE_DATA_SET, PARENT_KEY)).thenReturn(TYPE1);

		final Set<AttributeDescriptorModel> attributes = Set.of(DESCRIPTOR1);
		final List<AbstractListItemDTO> existingAttributes = Collections.emptyList();

		// test
		final Set<TreeNodeData> treeNodeDataSet = treeNodeDataSetGenerator.generate(attributes, existingAttributes,
				SUBTYPE_DATA_SET, PARENT_KEY);

		final TreeNodeData treeNodeData = treeNodeDataSet.iterator().next();
		final String attributeAlias = treeNodeData.getAlias();
		final String typeAlias = treeNodeData.getMapKeyDTO().getCode();

		assertEquals("Tree node data doesn't have alias.", DESCRIPTOR_QUALIFIER1, attributeAlias);
		assertEquals("Tree node data doesn't type alias.", TYPE_CODE1, typeAlias);
	}

	@Test
	public void treeNodeDataWithAliasCreatedWhenGenerateInSelectedMode()
	{
		// setup
		when(readService.getComplexTypeForAttributeDescriptor(DESCRIPTOR1)).thenReturn(TYPE1);
		when(attrDescSubtypeService.findSubtype(DESCRIPTOR1, SUBTYPE_DATA_SET, PARENT_KEY)).thenReturn(TYPE1);

		final List<AbstractListItemDTO> attributes = List.of(ATTRIBUTE_DTO1);

		// test
		final Set<TreeNodeData> treeNodeDataSet = treeNodeDataSetGenerator.generateInSelectedMode(attributes, SUBTYPE_DATA_SET,
				PARENT_KEY);

		final TreeNodeData treeNodeData = treeNodeDataSet.iterator().next();
		final String attributeAlias = treeNodeData.getAlias();
		final String typeAlias = treeNodeData.getMapKeyDTO().getCode();

		assertEquals("Tree node data has attribute alias.", ATTRIBUTE_ALIAS1, attributeAlias);
		assertEquals("Tree node data has type alias.", TYPE_ALIAS1, typeAlias);
	}
}
