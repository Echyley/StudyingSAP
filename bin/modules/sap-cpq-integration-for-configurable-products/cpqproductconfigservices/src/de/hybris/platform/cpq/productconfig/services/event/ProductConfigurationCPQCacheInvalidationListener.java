/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cpq.productconfig.services.event;

import de.hybris.platform.cpq.productconfig.services.CacheAccessService;
import de.hybris.platform.cpq.productconfig.services.CacheKeyService;
import de.hybris.platform.cpq.productconfig.services.cache.DefaultCacheKey;
import de.hybris.platform.cpq.productconfig.services.data.ConfigurationSummaryData;
import de.hybris.platform.servicelayer.event.impl.AbstractEventListener;

import org.apache.log4j.Logger;


public class ProductConfigurationCPQCacheInvalidationListener
		extends AbstractEventListener<ProductConfigurationCPQCacheInvalidationEvent>
{
	private static final Logger LOG = Logger.getLogger(ProductConfigurationCPQCacheInvalidationListener.class.getName());

	@Override
	protected void onEvent(final ProductConfigurationCPQCacheInvalidationEvent event)
	{
		if (LOG.isDebugEnabled())
		{
			LOG.debug(
					"received cluster wide cache invalidation event for product configuration with configId=" + event.getConfigId());
		}
		getCPQCache().remove(getCacheKeyService().createConfigurationSummaryCacheKey(event.getConfigId()));
 	}

	protected CacheAccessService<DefaultCacheKey, ConfigurationSummaryData> getCPQCache()
	{
		throw new UnsupportedOperationException(
				"Please define in the spring configuration a <lookup-method> for getCPQCache().");
	}

	protected CacheKeyService getCacheKeyService()
	{
		throw new UnsupportedOperationException(
				"Please define in the spring configuration a <lookup-method> for getCacheKeyService().");
	}
}
