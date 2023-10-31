/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.core.saps4omservices.cache.service.impl;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.hybris.platform.regioncache.CacheValueLoadException;
import de.hybris.platform.regioncache.CacheValueLoader;
import de.hybris.platform.regioncache.key.CacheKey;
import de.hybris.platform.regioncache.region.impl.EHCacheRegion;
import de.hybris.platform.sap.core.saps4omservices.cache.exceptions.SAPS4OMHybrisCacheException;
import de.hybris.platform.sap.core.saps4omservices.cache.service.CacheAccess;
import de.hybris.platform.sap.core.saps4omservices.cache.service.GenericCacheKey;

/**
 * Standard cache implementation class.
 * 
 */
public class CacheAccessImpl extends EHCacheRegion implements CacheAccess
{

	private static final String CACHE_ACCESS_IMPL_CREATED = "CacheAccessImpl created for S4OM...";
	private static final Logger LOG = LoggerFactory.getLogger(CacheAccessImpl.class);

	/**
	 * Standard constructor.
	 * 
	 * @param name
	 *           name of  cache region
	 * @param maxEntries
	 *           maximum number of entries for the cache region
	 */
	public CacheAccessImpl(final String name, final int maxEntries)
	{
		super(name, maxEntries, "LRU", false, true, null);
		LOG.debug(CACHE_ACCESS_IMPL_CREATED);
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
		LOG.debug(CACHE_ACCESS_IMPL_CREATED);
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
		LOG.debug(CACHE_ACCESS_IMPL_CREATED);
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
		LOG.debug(CACHE_ACCESS_IMPL_CREATED);
	}

	@Override
	public void setHandledTypes(final String[] handledTypes)
	{
	  // unimplemented 
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
			LOG.error("IllegalStateException occured for CacheAccessImpl");
			return null;
		}
	}

	@Override
	public Set<Object> getKeys()
	{
		final Set<Object> ret = new HashSet<>();
		final Collection<CacheKey> allKeys = super.getAllKeys();
		
		allKeys.forEach( (final CacheKey theKey) -> {
			ret.add(theKey);
		});
		return ret;
	}

	@Override
	public void put(final Object key, final Object object) throws SAPS4OMHybrisCacheException
	{
		final GenericCacheKey genericCacheKey = generateGenericCacheKey(key);
		final DefaultCacheValueLoaderImpl<Object> loader = new DefaultCacheValueLoaderImpl();
		loader.setValue(object);
		remove(genericCacheKey);
		LOG.debug("Object with following key(s) put into cache region " + this.getName() + ": " + genericCacheKey.toString());
		super.getWithLoader(genericCacheKey, loader);
	}

	@Override
	public void putIfAbsent(final Object key, final Object object) throws SAPS4OMHybrisCacheException
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
	 *           the key for which absence is checked
	 * @return true if key is not in the cache, false if it is in the cache
	 */
	private boolean isAbsent(final Object key)
	{
		final GenericCacheKey genericCacheKey = generateGenericCacheKey(key);
		return !super.containsKey(genericCacheKey);
	}

	@Override
	public void remove(final Object key) throws SAPS4OMHybrisCacheException
	{
		final GenericCacheKey genericCacheKey = generateGenericCacheKey(key);
		LOG.debug("Object with following key(s) is removed from cache region " + this.getName() + ": " + genericCacheKey.toString());
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
		LOG.debug("Cache region {} cleared", this.getName());
		super.clearCache();
	}

	/**
	 * Generates a {@link GenericCacheKey} from key object.
	 * 
	 * @param key
	 *           to be converted to
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
			return new GenericCacheKey(key, GenericCacheKey.DEFAULT_SAP_TYPECODE_VALUE);
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
		 * Standard constructor method.
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
		 *           set the value to be loaded.
		 */
		
		public void setValue(final Object obj)
		{
			this.obj = (V) obj;
		}

	}

}
