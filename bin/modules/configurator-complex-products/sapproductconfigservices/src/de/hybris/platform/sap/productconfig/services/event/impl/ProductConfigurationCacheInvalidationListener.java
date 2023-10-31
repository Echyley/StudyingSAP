/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.services.event.impl;

import de.hybris.platform.sap.productconfig.runtime.interf.event.ProductConfigurationCacheInvalidationEvent;
import de.hybris.platform.sap.productconfig.services.cache.ProductConfigurationCacheAccessService;
import de.hybris.platform.servicelayer.event.impl.AbstractEventListener;

import org.apache.log4j.Logger;


/**
 * This event listener reacts on {@link ProductConfigurationCacheInvalidationEvent}s and makes sure that corresponding
 * configuration state is deleted from the cache of this cluster node.
 */
public class ProductConfigurationCacheInvalidationListener
		extends AbstractEventListener<ProductConfigurationCacheInvalidationEvent>
{
	private static final Logger LOG = Logger.getLogger(ProductConfigurationCacheInvalidationListener.class.getName());

	@Override
	protected void onEvent(final ProductConfigurationCacheInvalidationEvent event)
	{
		if (LOG.isDebugEnabled())
		{
			LOG.debug(
					"received cluster wide cache invalidation event for product configuration with configId=" + event.getConfigId());
		}
		getCacheAccessService().removeConfigAttributeState(event.getConfigId(), event.getContextAttributes());
	}

	protected ProductConfigurationCacheAccessService getCacheAccessService()
	{
		throw new UnsupportedOperationException(
				"Please define in the spring configuration a <lookup-method> for getCacheAccessService().");
	}


}
