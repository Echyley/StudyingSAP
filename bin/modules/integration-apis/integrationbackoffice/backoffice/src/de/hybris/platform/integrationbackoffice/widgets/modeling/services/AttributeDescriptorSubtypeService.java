/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.integrationbackoffice.widgets.modeling.services;

import de.hybris.platform.core.model.type.AttributeDescriptorModel;
import de.hybris.platform.core.model.type.ComposedTypeModel;
import de.hybris.platform.integrationbackoffice.widgets.modeling.data.SubtypeData;
import de.hybris.platform.integrationbackoffice.widgets.modeling.data.dto.IntegrationMapKeyDTO;

import java.util.Set;

/**
 * Finds the subtype of an {@link AttributeDescriptorModel}.
 */
public interface AttributeDescriptorSubtypeService
{
	/**
	 * Finds the subtype of an {@link AttributeDescriptorModel}.
	 *
	 * @param attributeDescriptor the {@link AttributeDescriptorModel} that may have a subtype.
	 * @param subtypeDataSet      the set of all attributes' {@link SubtypeData}.
	 * @param parentKeyDTO        the parent type instance of the attribute. Used to find the {@link SubtypeData} of the attribute within the subtypeDataSet.
	 * @return the subtype of the {@link AttributeDescriptorModel}. If the {@link AttributeDescriptorModel} doesn't have a subtype,
	 * its complex type will be returned instead. Both are represented by a {@link ComposedTypeModel}.
	 */
	ComposedTypeModel findSubtype(final AttributeDescriptorModel attributeDescriptor,
	                              final Set<SubtypeData> subtypeDataSet,
	                              final IntegrationMapKeyDTO parentKeyDTO);
}
