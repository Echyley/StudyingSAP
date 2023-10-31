/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.outboundservices.cache.impl;

import de.hybris.platform.integrationservices.cache.impl.BaseIntegrationCache;
import de.hybris.platform.outboundservices.cache.DestinationRestTemplateCacheKey;
import de.hybris.platform.outboundservices.cache.RestTemplateCache;
import de.hybris.platform.regioncache.key.CacheKey;

import org.springframework.web.client.RestTemplate;

public class DestinationRestTemplateCache extends BaseIntegrationCache<DestinationRestTemplateCacheKey, RestTemplate> implements RestTemplateCache
{
	@Override
	protected CacheKey toCacheKey(final DestinationRestTemplateCacheKey key)
	{
		return InternalDestinationRestTemplateCacheKey.from(key);
	}

	@Override
	protected Class<RestTemplate> getValueType()
	{
		return RestTemplate.class;
	}
}
