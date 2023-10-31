/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.runtime.cps.event.impl;

import de.hybris.platform.sap.productconfig.runtime.cps.cache.CPSCache;
import de.hybris.platform.sap.productconfig.runtime.interf.event.ProductConfigurationCacheInvalidationEvent;
import de.hybris.platform.servicelayer.event.impl.AbstractEventListener;

import org.apache.log4j.Logger;


/**
 * This event listener reacts on {@link ProductConfigurationCacheInvalidationEvent}s and makes sure that corresponding
 * configuration state is deleted from the cache of this cluster node.
 */
public class ProductConfigurationCPSCacheInvalidationListener
		extends AbstractEventListener<ProductConfigurationCacheInvalidationEvent>
{
	private static final Logger LOG = Logger.getLogger(ProductConfigurationCPSCacheInvalidationListener.class.getName());

	@Override
	protected void onEvent(final ProductConfigurationCacheInvalidationEvent event)
	{
		if (LOG.isDebugEnabled())
		{
			LOG.debug(
					"received cluster wide cache invalidation event for product configuration with configId=" + event.getConfigId());
		}
		getCPSCache().removeConfiguration(event.getConfigId(), event.getContextAttributes());
	}

	protected CPSCache getCPSCache()
	{
		throw new UnsupportedOperationException("Please define in the spring configuration a <lookup-method> for getCPSCache().");
	}


}
