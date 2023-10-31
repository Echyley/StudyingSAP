/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.integrationbackoffice.widgets.common.services.impl;


import com.hybris.cockpitng.dataaccess.facades.type.DataAttribute;
import com.hybris.cockpitng.dataaccess.facades.type.DataType;

import de.hybris.platform.integrationbackoffice.widgets.common.services.IntegrationSortingService;

/**
 * Default implementation of IntegrationSortingService
 */
public class DefaultIntegrationSortingService implements IntegrationSortingService
{
	/**
	 * Verifies if {@link DataAttribute} is sortable.
	 *
	 * @param attribute a given attribute.
	 * @return true if the attribute is searchable, of type single and has {@link DataType} atomic or enum. Returns false otherwise.
	 */
	public boolean isAttributeSortable(final DataAttribute attribute)
	{
		if (attribute != null && attribute.isSearchable())
		{
			final DataType valueType = attribute.getValueType();
			final boolean isAttributeTypeSingle = DataAttribute.AttributeType.SINGLE == attribute.getAttributeType();
			return (isAttributeTypeSingle && (valueType.isAtomic() || valueType.isEnum()));
		}
		else {
			return false;
		}
	}
}
