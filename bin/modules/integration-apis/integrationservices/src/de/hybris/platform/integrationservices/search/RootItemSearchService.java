/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.integrationservices.search;

import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.integrationservices.model.IntegrationObjectModel;
import de.hybris.platform.integrationservices.model.TypeDescriptor;

import javax.validation.constraints.NotNull;

/**
 * A service that provides methods for retrieving root items
 * defined within the {@link IntegrationObjectModel}.
 */
public interface RootItemSearchService
{
	/**
	 * Searches for a root item within the provided {@link ItemModel}
	 * using the {@link IntegrationObjectModel} definition.
	 * Root item may be the {@param item} itself.
	 *
	 * @param item the item to navigate to the root item from
	 * @param io   integration object definition
	 * @return {@link RootItemSearchResult} result of the search
	 */
	@NotNull RootItemSearchResult findRoots(ItemModel item, IntegrationObjectModel io);


	/**
	 * Searches for a root item within the provided {@link ItemModel}
	 * using the {@link TypeDescriptor} definition.
	 * Root item may be the {@param item} itself.
	 *
	 * @param item           the item to navigate to the root item from
	 * @param typeDescriptor integration object item descriptor
	 * @return {@link RootItemSearchResult} result of the search
	 */
	@NotNull RootItemSearchResult findRoots(ItemModel item, TypeDescriptor typeDescriptor);
}
