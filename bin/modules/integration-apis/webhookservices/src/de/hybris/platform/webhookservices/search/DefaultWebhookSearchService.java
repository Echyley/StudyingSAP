/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.webhookservices.search;

import de.hybris.platform.core.PK;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.integrationservices.service.ItemModelSearchService;
import de.hybris.platform.webhookservices.cache.WebhookCacheService;

import javax.validation.constraints.NotNull;

import java.util.Optional;

import com.google.common.base.Preconditions;


/**
 * Default webhook search service.
 */
public class DefaultWebhookSearchService implements ItemModelSearchService
{
	private final WebhookCacheService webhookCacheService;
	private final ItemModelSearchService itemModelSearchService;

	/**
	 * Instantiate the DefaultWebhookSearchService
	 *
	 * @param webhookCacheService    to look up an item from the cache
	 * @param itemModelSearchService to look up an item from the database
	 */
	protected DefaultWebhookSearchService(@NotNull final WebhookCacheService webhookCacheService,
			@NotNull final ItemModelSearchService itemModelSearchService)
	{
		Preconditions.checkArgument(webhookCacheService != null, "WebhookCacheService cannot be null");
		Preconditions.checkArgument(itemModelSearchService != null, "ItemModelSearchService cannot be null");

		this.webhookCacheService = webhookCacheService;
		this.itemModelSearchService = itemModelSearchService;
	}

	@Override
	public <T extends ItemModel> Optional<T> nonCachingFindByPk(final PK pk)
	{
		final Optional<T> optionalItem = itemModelSearchService.nonCachingFindByPk(pk);
		return optionalItem.isPresent() ? optionalItem : webhookCacheService.findItem(pk);
	}

}
