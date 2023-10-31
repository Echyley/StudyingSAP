/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.webhookservices.cache;

import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.integrationservices.cache.impl.BaseIntegrationCache;
import de.hybris.platform.regioncache.key.CacheKey;


/**
 * Webhook deleted item cache.
 */
public class WebhookDeletedItemCache extends BaseIntegrationCache<WebhookDeletedItemCacheKey, ItemModel>
{
	@Override
	protected CacheKey toCacheKey(final WebhookDeletedItemCacheKey key)
	{
		return key;
	}

	@Override
	protected Class<ItemModel> getValueType()
	{
		return ItemModel.class;
	}
}
