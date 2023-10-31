/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.inboundservices.persistence.impl;

import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.inboundservices.persistence.ItemDeletionService;
import de.hybris.platform.integrationservices.search.ItemSearchRequest;
import de.hybris.platform.integrationservices.search.ItemNotFoundException;
import de.hybris.platform.integrationservices.search.ItemSearchService;
import de.hybris.platform.integrationservices.search.validation.ItemSearchRequestValidator;
import de.hybris.platform.servicelayer.model.ModelService;

import java.util.Collections;
import java.util.List;

import javax.validation.constraints.NotNull;

import com.google.common.base.Preconditions;

/**
 * Default implementation for {@link ItemDeletionService}
 */
public class DefaultItemDeletionService implements ItemDeletionService
{
	private final ItemSearchService itemSearchService;
	private final ModelService modelService;
	private List<ItemSearchRequestValidator> deleteItemValidators;

	/**
	 * Instantiates this service with required dependencies
	 * @param service a model service to use for deleting the item
	 * @param search a search service to use for searching the item to be deleted.
	 */
	public DefaultItemDeletionService(@NotNull final ModelService service, @NotNull final ItemSearchService search)
	{
		Preconditions.checkArgument(service != null, "ModelService is required");
		Preconditions.checkArgument(search != null, "ItemSearchService is required");
		modelService = service;
		itemSearchService = search;
		deleteItemValidators = Collections.emptyList();
	}

	/**
	 * {@inheritDoc}
	 * @throws ItemNotFoundException when an item is not found
	 */
	@Override
	public void deleteItem(final ItemSearchRequest searchRequest)
	{
		deleteItemValidators.forEach(v -> v.validate(searchRequest));
		final ItemModel item = lookupItem(searchRequest);
		modelService.remove(item);
	}

	/**
	 * Sets validators to be used for validating the search request in {@link #deleteItem(ItemSearchRequest)} invocation.
	 *
	 * @param validators validators to be called sequentially before searching for the item
	 *                   to be deleted.
	 */
	public void setDeleteItemValidators(final List<ItemSearchRequestValidator> validators)
	{
		deleteItemValidators = validators != null
				? List.copyOf(validators)
				: Collections.emptyList();
	}

	private ItemModel lookupItem(final ItemSearchRequest request)
	{
		return itemSearchService
				.findUniqueItem(request)
				.orElseThrow(() -> new ItemNotFoundException(request.getRequestedItem().orElse(null)));
	}
}
