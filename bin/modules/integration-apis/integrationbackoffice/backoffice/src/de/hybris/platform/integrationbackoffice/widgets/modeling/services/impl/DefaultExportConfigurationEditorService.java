/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.integrationbackoffice.widgets.modeling.services.impl;

import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.integrationbackoffice.widgets.modeling.services.ExportConfigurationEditorService;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.search.SearchResult;

import java.util.List;

/**
 * The default implementation for the interface {@link ExportConfigurationEditorService}.
 */
public class DefaultExportConfigurationEditorService implements ExportConfigurationEditorService
{
	private final FlexibleSearchService flexibleSearchService;

	/**
	 * Instantiates a new export configuration editor service.
	 *
	 * @param flexibleSearchService the flexible search service to perform database queries
	 */
	public DefaultExportConfigurationEditorService(final FlexibleSearchService flexibleSearchService)
	{
		this.flexibleSearchService = flexibleSearchService;
	}

	@Override
	public List<ItemModel> findItemModelInstances(final String itemTypeCode)
	{
		final String query = "SELECT {" + ItemModel.PK + "} FROM {" + itemTypeCode + " } ";
		final SearchResult<ItemModel> result = flexibleSearchService.search(new FlexibleSearchQuery(query));
		return result.getResult();
	}
}
