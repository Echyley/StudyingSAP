/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.integrationbackoffice.widgets.modeling.services;

import de.hybris.platform.core.model.type.AttributeDescriptorModel;
import de.hybris.platform.integrationbackoffice.widgets.modeling.data.SubtypeData;
import de.hybris.platform.integrationbackoffice.widgets.modeling.data.TreeNodeData;
import de.hybris.platform.integrationbackoffice.widgets.modeling.data.dto.AbstractListItemDTO;
import de.hybris.platform.integrationbackoffice.widgets.modeling.data.dto.IntegrationMapKeyDTO;

import java.util.List;
import java.util.Set;

/**
 * Generates a set of {@link TreeNodeData}.
 */
public interface TreeNodeDataSetGenerator
{
	/**
	 * Generates {@link TreeNodeData} for provided attributes.
	 *
	 * @param attributes         the set of attributes to generate {@link TreeNodeData} for.
	 * @param existingAttributes the list of selected attributes.
	 * @param subtypeDataSet     the set of all attributes' {@link SubtypeData}.
	 * @param parentKey          the parent type instance of the attributes. Used to find the {@link SubtypeData} of the attributes within the subtypeDataSet.
	 * @return a set of {@link TreeNodeData}.
	 */
	Set<TreeNodeData> generate(Set<AttributeDescriptorModel> attributes,
	                           List<AbstractListItemDTO> existingAttributes,
	                           Set<SubtypeData> subtypeDataSet,
	                           IntegrationMapKeyDTO parentKey);

	/**
	 * Generates {@link TreeNodeData} for provided selected attributes.
	 *
	 * @param attributes     the list of selected attributes.
	 * @param subtypeDataSet the set of all attributes' {@link SubtypeData}.
	 * @param parentKey      the parent type instance of the attributes. Used to find the {@link SubtypeData} of the attributes within the subtypeDataSet.
	 * @return a set of {@link TreeNodeData}.
	 */
	Set<TreeNodeData> generateInSelectedMode(List<AbstractListItemDTO> attributes,
	                                         Set<SubtypeData> subtypeDataSet,
	                                         IntegrationMapKeyDTO parentKey);
}
