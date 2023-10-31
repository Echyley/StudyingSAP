/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cpq.productconfig.services.event;

import de.hybris.platform.servicelayer.event.ClusterAwareEvent;
import de.hybris.platform.servicelayer.event.PublishEventContext;
import de.hybris.platform.servicelayer.event.events.AbstractEvent;

import org.apache.log4j.Logger;


/**
 * Default implementation for CPQ cache invalidation. Should be published when CPQ configuration is outdated and discard
 * all cached configurations cluster wide.
 */
public class ProductConfigurationCPQCacheInvalidationEvent extends AbstractEvent implements ClusterAwareEvent
{

	private static final Logger LOG = Logger.getLogger(ProductConfigurationCPQCacheInvalidationEvent.class);
	private final String configId;
	public ProductConfigurationCPQCacheInvalidationEvent(final String configId)
	{
		super();
		this.configId = configId;
	}

	public String getConfigId()
	{
		return configId;
	}


	@Override
	public boolean canPublish(final PublishEventContext publishEventContext)
	{
		final boolean canPublish = publishEventContext.getSourceNodeId() != publishEventContext.getTargetNodeId();
		if (!canPublish && LOG.isDebugEnabled())
		{
			LOG.debug("Discarding clusterwide cache invalidation event is triggered for this configID: " + configId);
		}
		return canPublish;
	}
}
