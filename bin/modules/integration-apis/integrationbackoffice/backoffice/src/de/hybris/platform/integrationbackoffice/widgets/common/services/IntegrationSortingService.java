/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.integrationbackoffice.widgets.common.services;

import com.hybris.cockpitng.dataaccess.facades.type.DataAttribute;

/**
 * Service to verify if data attribute is sortable
 */
public interface IntegrationSortingService
{
	/**
	 * Verifies if a {@link DataAttribute} is sortable.
	 *
	 * @param attribute a given data attribute
	 * @return true if data attribute is sortable
	 */
	boolean isAttributeSortable(DataAttribute attribute);
}
