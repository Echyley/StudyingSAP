/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cmsfacades.daos.impl;

import de.hybris.platform.cms2.servicelayer.daos.impl.AbstractCMSItemDao;
import de.hybris.platform.cmsfacades.daos.CMSItemTypeAttributeFilterConfigDao;
import de.hybris.platform.cmsfacades.model.CMSItemTypeAttributeFilterConfigModel;
import de.hybris.platform.servicelayer.search.SearchResult;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Default implementation of {@link CMSItemTypeAttributeFilterConfigDao}
 */
public class DefaultCMSItemTypeAttributeFilterConfigDao extends AbstractCMSItemDao implements
		CMSItemTypeAttributeFilterConfigDao
{
	@Override
	public List<CMSItemTypeAttributeFilterConfigModel> getAllAttributeConfigurations()
	{
		final Map<String, Object> queryParameters = new HashMap<>();
		final SearchResult<CMSItemTypeAttributeFilterConfigModel> result =
				search("SELECT {" + CMSItemTypeAttributeFilterConfigModel.PK + "} FROM {" + CMSItemTypeAttributeFilterConfigModel._TYPECODE + "}", queryParameters);

		return result.getResult();
	}
}