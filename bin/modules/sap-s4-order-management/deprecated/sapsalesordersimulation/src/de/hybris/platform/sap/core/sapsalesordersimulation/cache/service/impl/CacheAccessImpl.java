/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.core.sapsalesordersimulation.cache.service.impl;

import de.hybris.platform.regioncache.CacheValueLoadException;
import de.hybris.platform.regioncache.CacheValueLoader;
import de.hybris.platform.regioncache.key.CacheKey;
import de.hybris.platform.regioncache.region.impl.EHCacheRegion;
import de.hybris.platform.sap.core.sapsalesordersimulation.cache.service.CacheAccess;
import de.hybris.platform.sap.core.sapsalesordersimulation.cache.service.GenericCacheKey;
import de.hybris.platform.sap.core.sapsalesordersimulation.cache.exceptions.SAPHybrisCacheException;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;


/**
 * Standard cache implementation class.
 * 
 */
public class CacheAccessImpl extends EHCacheRegion implements CacheAccess
{

	private static final String CACHE_ACCESS_IMPL_CREATED = "CacheAccessImpl created ...";
	private static final Logger LOGGER = Logger.getLogger(CacheAccessImpl.class.getName());

	/**
	 * Standard constructor.
	 * 
	 * @param name
	 *           name of the cache region
	 * @param maxEntries
	 *           maximum number of entries
	 */
	public CacheAccessImpl(final String name, final int maxEntries)
	{
		super(name, maxEntries, "LRU", false, true, null);
		if (LOGGER.isDebugEnabled())
		{
			LOGGER.debug(CACHE_ACCESS_IMPL_CREATED);
		}
	}

	/**
	 * Standard constructor.
	 * 
	 * @param name
	 *           name of the cache region
	 * @param maxEntries
	 *           maximum number of entries
	 * @param evictionPolicy
	 *           eviction policy which is used
	 */
	public CacheAccessImpl(final String name, final int maxEntries, final String evictionPolicy)
	{
		super(name, maxEntries, evictionPolicy, false, true, null);
		if (LOGGER.isDebugEnabled())
		{
			LOGGER.debug(CACHE_ACCESS_IMPL_CREATED);
		}
	}

	/**
	 * Standard constructor.
	 * 
	 * @param name
	 *           name of the cache region
	 * @param maxEntries
	 *           maximum number of entries
	 * @param evictionPolicy
	 *           eviction policy which is used
	 * @param exclusiveComputation
	 *           flag indicating if exclusive computation is used
	 * @param statsEnabled
	 *           flag indicating if statistics are stored or not
	 */
	public CacheAccessImpl(final String name, final int maxEntries, final String evictionPolicy,
			final boolean exclusiveComputation, final boolean statsEnabled)
	{
		super(name, maxEntries, evictionPolicy, exclusiveComputation, statsEnabled, null);
		if (LOGGER.isDebugEnabled())
		{
			LOGGER.debug(CACHE_ACCESS_IMPL_CREATED);
		}
	}

	/**
	 * Standard constructor.
	 * 
	 * @param name
	 *           name of the cache region
	 * @param maxEntries
	 *           maximum number of entries
	 * @param evictionPolicy
	 *           eviction policy which is used
	 * @param exclusiveComputation
	 *           flag indicating if exclusive computation is used
	 * @param statsEnabled
	 *           flag indicating if statistics are stored or not
	 * @param ttlSeconds
	 *           time to live of the stored objects in seconds
	 */
	public CacheAccessImpl(final String name, final int maxEntries, final String evictionPolicy,
			final boolean exclusiveComputation, final boolean statsEnabled, final Long ttlSeconds)
	{
		super(name, maxEntries, evictionPolicy, exclusiveComputation, statsEnabled, ttlSeconds);
		if (LOGGER.isDebugEnabled())
		{
			LOGGER.debug(CACHE_ACCESS_IMPL_CREATED);
		}
	}

	@Override
	public void setHandledTypes(final String[] handledTypes)
	{
		//
	}

	@Override
	public Object get(final Object key)
	{
		final GenericCacheKey genericCacheKey = generateGenericCacheKey(key);
		try
		{
			return super.get(genericCacheKey);
		}
		catch (final IllegalStateException e)
		{
			LOGGER.error("IllegalStateException occured");
			return null;
		}
	}

	@Override
	public Set<Object> getKeys()
	{
		final Set<Object> ret = new HashSet<>();
		final Collection<CacheKey> allKeys = super.getAllKeys();
		for (final CacheKey theKey : allKeys)
		{
			ret.add(theKey);
		}
		return ret;
	}

	@Override
	public void put(final Object key, final Object object) throws SAPHybrisCacheException
	{
		final GenericCacheKey genericCacheKey = generateGenericCacheKey(key);
		final DefaultCacheValueLoaderImpl<Object> loader = new DefaultCacheValueLoaderImpl();
		loader.setValue(object);
		remove(genericCacheKey);
		if (LOGGER.isDebugEnabled())
		{
			LOGGER.debug("Object with following key(s) put into cache region " + this.getName() + ": " + genericCacheKey.toString());
		}
		super.getWithLoader(genericCacheKey, loader);
	}

	@Override
	public void putIfAbsent(final Object key, final Object object) throws SAPHybrisCacheException
	{
		if (isAbsent(key))
		{
			put(key, object);
		}
	}

	/**
	 * Checks if the key is in cache.
	 * 
	 * @param key
	 *           the key for which the absence is checked
	 * @return true if key is not in cache, false if it is in cache
	 */
	private boolean isAbsent(final Object key)
	{
		final GenericCacheKey genericCacheKey = generateGenericCacheKey(key);
		return !super.containsKey(genericCacheKey);
	}

	@Override
	public void remove(final Object key) throws SAPHybrisCacheException
	{
		final GenericCacheKey genericCacheKey = generateGenericCacheKey(key);
		if (LOGGER.isDebugEnabled())
		{
			LOGGER.debug("Object with following key(s) removed from cache region " + this.getName() + ": " + genericCacheKey.toString());
		}
		super.remove(genericCacheKey, false);
	}

	@Override
	public int getNumObjects()
	{
		return super.getAllKeys().size();
	}

	@Override
	public Object getWithLoader(final Object key, final CacheValueLoader<?> loader)
	{
		final GenericCacheKey genericCacheKey = generateGenericCacheKey(key);
		return super.getWithLoader(genericCacheKey, loader);
	}

	@Override
	public void clearCache()
	{
		if (LOGGER.isDebugEnabled())
		{
			LOGGER.debug("Cache region " + this.getName() + "cleared.");
		}
		super.clearCache();
	}

	/**
	 * Generates a {@link GenericCacheKey} from key object.
	 * 
	 * @param key
	 *           to be converted
	 * @return {@link GenericCacheKey}
	 */
	private GenericCacheKey generateGenericCacheKey(final Object key)
	{
		if (key instanceof GenericCacheKey)
		{
			return (GenericCacheKey) key;
		}
		else
		{
			return new GenericCacheKey(key, GenericCacheKey.DEFAULT_SAP_TYPECODE);
		}
	}

	/**
	 * Inner implementation of the {@link CacheValueLoader} which is used for method <code>put</code>.
	 * 
	 * @param <V>
	 */
	private static class DefaultCacheValueLoaderImpl<V> implements CacheValueLoader<V>
	{

		private V obj;

		/**
		 * Standard constructor.
		 */
		public DefaultCacheValueLoaderImpl()
		{
			super();
		}

		@Override
		public V load(final CacheKey arg0) throws CacheValueLoadException
		{
			return this.obj;
		}

		/**
		 * Set value to be loaded from {@link CacheValueLoader}.
		 * 
		 * @param obj
		 *           value to be loaded.
		 */
		
		public void setValue(final Object obj)
		{
			this.obj = (V) obj;
		}

	}

}
