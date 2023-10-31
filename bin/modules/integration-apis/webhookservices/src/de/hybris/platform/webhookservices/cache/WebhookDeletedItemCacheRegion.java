/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.webhookservices.cache;

import de.hybris.platform.regioncache.region.impl.EHCacheRegion;

/**
 * Cache region used to store deleted items.
 */
public class WebhookDeletedItemCacheRegion extends EHCacheRegion
{
	/**
	 * Initiates the region cache
	 * @param name                   region cache name
	 * @param maxEntries             maximum number of entries
	 * @param evictionPolicy         eviction policy
	 * @param exclusiveComputation   flag for exclusive computation
	 * @param statsEnabled           flag to enable statistics
	 * @param ttlSeconds             time to live in seconds for the cached items
	 */
	public WebhookDeletedItemCacheRegion(final String name, final int maxEntries, final String evictionPolicy,
	                                     final boolean exclusiveComputation,
	                                     final boolean statsEnabled,
	                                     final Long ttlSeconds)
	{
		super(name, maxEntries, evictionPolicy, exclusiveComputation, statsEnabled, ttlSeconds);
	}

	/**
	 * Override to disable clear cache button in HAC
	 */
	@Override
	public void clearCache()
	{
		// disable clear cache button in HAC
	}
}
