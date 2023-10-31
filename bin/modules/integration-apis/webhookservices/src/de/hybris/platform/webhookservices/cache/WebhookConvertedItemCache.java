/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.webhookservices.cache;

import de.hybris.platform.integrationservices.cache.impl.BaseIntegrationCache;
import de.hybris.platform.regioncache.key.CacheKey;
import de.hybris.platform.webhookservices.dto.WebhookItemPayload;


/**
 * Cache for a deleted item to be sent to a webhook
 */
public class WebhookConvertedItemCache extends BaseIntegrationCache<WebhookDeletedItemCacheKey, WebhookItemPayload>
{
	@Override
	protected CacheKey toCacheKey(final WebhookDeletedItemCacheKey key)
	{
		return key;
	}

	@Override
	protected Class<WebhookItemPayload> getValueType()
	{
		return WebhookItemPayload.class;
	}
}
