/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.core.configuration.dao.impl;

import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.sap.core.configuration.dao.GenericConfigurationDao;
import de.hybris.platform.servicelayer.internal.dao.AbstractItemDao;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.SearchResult;

import java.util.List;


/**
 * Implementation for interface {@link GenericConfigurationDao}.
 */
public class DefaultGenericConfigurationDao extends AbstractItemDao implements GenericConfigurationDao
{

	@Override
	public List<ItemModel> getAllModelsForCode(final String code)
	{
		final String queryString = //
		"SELECT {pk} FROM {" + code + "} ";
		final FlexibleSearchQuery query = new FlexibleSearchQuery(queryString);
		final SearchResult<ItemModel> result = search(query);
		return result.getResult();
	}

}
