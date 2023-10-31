/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.runtime.ssc.impl;


import de.hybris.platform.servicelayer.util.ServicesUtil;

import java.util.List;

import org.springframework.context.ApplicationListener;

import com.google.common.base.Preconditions;
import com.sap.custdev.projects.fbs.slc.cml.util.CacheUtil;
import com.sap.custdev.projects.fbs.slc.logging.ILoggingHandle;
import com.sap.custdev.projects.fbs.slc.logging.LoggingServiceFactory;
import com.sap.util.cache.CacheFacade;
import com.sap.util.cache.CacheRegion;


/**
 * Listens to SSC cache invalidation event and collects all the keys to be invalidated and the from which cache region
 * and then removes all the kays from the received cache region name.
 */
public class SSCCacheInvalidationListener implements ApplicationListener<SSCCacheInvalidationEvent>
{

	private static final ILoggingHandle logging = LoggingServiceFactory
			.getLoggingHandle(SSCCacheInvalidationListener.class.getName());

	private CacheRegion cacheRegion;

	@Override
	public void onApplicationEvent(final SSCCacheInvalidationEvent sscCacheInvalidationEvent)
	{
		//Logic to receive and process the invalidation event
		final List<String> keyToInvalidate = sscCacheInvalidationEvent.getCacheInvalidationKeys();
		try
		{
			final CacheFacade cacheFacade = getCacheFacade(sscCacheInvalidationEvent.getCacheRegionName());
			for (final String key : keyToInvalidate)
			{
				//Logic for removing key from cache
				removeFromCache(key, cacheFacade);
			}
		}
		catch (final Exception e)
		{
			logging.errorT("Exception while removing the record from the cache" + e.getMessage());
		}
	}

	private CacheFacade getCacheFacade(final String cacheRegionName)
	{
		if (cacheRegion != null && cacheRegionName.equals(cacheRegion.getRegionConfigurationInfo().getName()))
		{
			return cacheRegion.getCacheFacade();
		}
		else
		{
			cacheRegion = CacheUtil.getCacheRegion(cacheRegionName);
			final CacheFacade cacheFacade = cacheRegion != null ? cacheRegion.getCacheFacade() : null;
			Preconditions.checkNotNull(cacheFacade,
					String.format("Fatal : Cachefacade of cache region '%s'can not be null.", cacheRegionName));
			return cacheFacade;
		}
	}

	private void removeFromCache(final String keyId, final CacheFacade cacheFacade)
	{
		ServicesUtil.validateParameterNotNull(cacheFacade, "Cachefacade cannot be null.");

		final Object objKey = cacheFacade.get(keyId);
		final String condRecordKey = objKey == null ? null : objKey.toString();
		//Remove this record from cache
		if (condRecordKey != null)
		{
			cacheFacade.remove(condRecordKey);
			logging.infoT("Condition Record : (" + condRecordKey + ") updated with the new data. Hence removed from cache");
		}
		else
		{
			// The condition is not part of the cache either
			//has never been loaded or already removed
		}
		// Now remove the entry for varnumh
		cacheFacade.remove(keyId);
	}

}
