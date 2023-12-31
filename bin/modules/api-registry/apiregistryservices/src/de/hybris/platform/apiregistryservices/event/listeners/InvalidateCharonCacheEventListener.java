/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.apiregistryservices.event.listeners;

import de.hybris.platform.apiregistryservices.event.InvalidateCharonCacheEvent;
import de.hybris.platform.apiregistryservices.factory.ClientFactory;
import de.hybris.platform.servicelayer.event.impl.AbstractEventListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;


public class InvalidateCharonCacheEventListener extends AbstractEventListener<InvalidateCharonCacheEvent>
{
	private ClientFactory clientFactory;
	private static final Logger LOG = LoggerFactory.getLogger(InvalidateCharonCacheEventListener.class);

	@Override
	protected void onEvent(final InvalidateCharonCacheEvent invalidateCharonCacheEvent)
	{
		clientFactory.inValidateCache(invalidateCharonCacheEvent.getCacheKey());
		LOG.debug("Charon cache is invalidated for key: {}", invalidateCharonCacheEvent.getCacheKey());
	}

	protected ClientFactory getClientFactory()
	{
		return clientFactory;
	}

	@Required
	public void setClientFactory(final ClientFactory clientFactory)
	{
		this.clientFactory = clientFactory;
	}
}
