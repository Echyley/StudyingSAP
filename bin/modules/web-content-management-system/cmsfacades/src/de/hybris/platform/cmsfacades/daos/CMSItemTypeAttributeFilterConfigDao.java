/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cmsfacades.daos;

import de.hybris.platform.cmsfacades.model.CMSItemTypeAttributeFilterConfigModel;

import java.util.List;


/**
 * Dao to retrieve {@link CMSItemTypeAttributeFilterConfigModel} objects.
 */
public interface CMSItemTypeAttributeFilterConfigDao
{
	/**
	 * Retrieves all attribute configurations.
	 * @return the list of {@link CMSItemTypeAttributeFilterConfigModel}
	 */
	List<CMSItemTypeAttributeFilterConfigModel> getAllAttributeConfigurations();
}
