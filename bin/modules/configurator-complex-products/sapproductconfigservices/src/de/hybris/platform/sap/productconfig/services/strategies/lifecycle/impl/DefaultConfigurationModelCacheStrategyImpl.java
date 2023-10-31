/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.services.strategies.lifecycle.impl;

import de.hybris.platform.sap.productconfig.runtime.interf.event.ProductConfigurationCacheInvalidationEvent;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ConfigModel;
import de.hybris.platform.sap.productconfig.services.cache.ProductConfigurationCacheAccessService;
import de.hybris.platform.sap.productconfig.services.strategies.lifecycle.intf.ConfigurationModelCacheStrategy;
import de.hybris.platform.servicelayer.event.EventService;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;


/**
 * Default implementation of the {@link ConfigurationModelCacheStrategy}. It uses the hybris caches to store any data
 * and hence delegates to the {@link ProductConfigurationCacheAccessService}.
 */
public class DefaultConfigurationModelCacheStrategyImpl extends SessionServiceAware implements ConfigurationModelCacheStrategy
{
	private static final Logger LOG = Logger.getLogger(DefaultConfigurationModelCacheStrategyImpl.class.getName());
	private ProductConfigurationCacheAccessService cacheAccessService;
	private EventService eventService;

	@Override
	public ConfigModel getConfigurationModelEngineState(final String configId)
	{
		return getCacheAccessService().getConfigurationModelEngineState(configId);
	}

	@Override
	public void setConfigurationModelEngineState(final String configId, final ConfigModel configModel)
	{
		getCacheAccessService().setConfigurationModelEngineState(configId, configModel);
	}

	@Override
	public void purge()
	{
		getSessionAccessService().purge();
	}


	@Override
	public void removeConfigAttributeState(final String configId)
	{
		getCacheAccessService().removeConfigAttributeState(configId);
		final Map<String, String> cacheKeys = new HashMap<>();
		getCacheAccessService().populateCacheKeyContextAttributes(cacheKeys);
		if (LOG.isDebugEnabled())
		{
			LOG.debug(
					"publishing cluster wide cache invalidation event for product configuration with configId=" + configId);
		}
		getEventService().publishEvent(new ProductConfigurationCacheInvalidationEvent(configId, cacheKeys ));

	}

	protected ProductConfigurationCacheAccessService getCacheAccessService()
	{
		return cacheAccessService;
	}

	@Required
	public void setCacheAccessService(final ProductConfigurationCacheAccessService cacheAccessService)
	{
		this.cacheAccessService = cacheAccessService;
	}

	protected EventService getEventService()
	{
		return eventService;
	}

	@Required
	public void setEventService(final EventService eventService)
	{
		this.eventService = eventService;
	}

}
