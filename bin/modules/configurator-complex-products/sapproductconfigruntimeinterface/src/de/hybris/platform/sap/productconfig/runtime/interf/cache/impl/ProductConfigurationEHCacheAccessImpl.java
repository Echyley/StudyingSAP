/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.runtime.interf.cache.impl;

import de.hybris.platform.regioncache.CacheValueLoader;
import de.hybris.platform.regioncache.key.CacheKey;
import de.hybris.platform.regioncache.region.impl.EHCacheRegion;
import de.hybris.platform.sap.productconfig.runtime.interf.cache.ProductConfigurationCacheAccess;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;


public class ProductConfigurationEHCacheAccessImpl<K extends CacheKey, V> implements ProductConfigurationCacheAccess<K, V>
{
	private static final Logger LOGGER = Logger.getLogger(ProductConfigurationEHCacheAccessImpl.class);

	private EHCacheRegion cache;

	@Override
	public V get(final K key)
	{
		final V cachedObj = (V) getCache().get(key);
		if (LOGGER.isDebugEnabled())
		{
			LOGGER.debug(String.format(" %s of type %s identified by key %s",
					null == cachedObj ? "requested to get from cache, but did't find object" : "got from cache",
					key.getTypeCode(), key));
		}
		return cachedObj;
	}

	@Override
	public Set<K> getKeys()
	{
		return new HashSet<>((Collection<K>) getCache().getAllKeys());
	}

	@Override
	public V getWithSupplier(final K key, final Supplier<V> supplier)
	{
		logPutOperation(key);
		return getWithLoader(key, new SupplierCacheValueLoaderImpl<>(supplier));
	}

	@Override
	public V getWithLoader(final K key, final CacheValueLoader<V> loader)
	{
		logPutOperation(key);
		return (V) getCache().getWithLoader(key, loader);
	}

	@Override
	public void put(final K key, final V value)
	{
		if (getCache().containsKey(key))
		{
			remove(key);
		}

		final DefaultCacheValueLoaderImpl defaultLoader = new DefaultCacheValueLoaderImpl<>(value);
		getWithLoader(key, defaultLoader);
	}

	@Override
	public void putIfAbsent(final K key, final V value)
	{
		if (!getCache().containsKey(key))
		{
			logPutOperation(key);
			put(key, value);
		}
	}

	private void logPutOperation(final K key)
	{
		if (LOGGER.isDebugEnabled())
		{
			LOGGER.debug(String.format("caching value of type %s identified by key %s", key.getTypeCode(), key));
		}
	}

	@Override
	public void remove(final K key)
	{
		final Object cachedObj = getCache().remove(key, false);
		if (LOGGER.isDebugEnabled())
		{
			LOGGER.debug(String.format(" %s of type %s identified by key %s",
					null == cachedObj ? "requested to remove from cache, but did't find object" : "removed from cache object",
					key.getTypeCode(), key));
		}
	}

	@Override
	public void clearCache()
	{
		if (LOGGER.isDebugEnabled())
		{
			LOGGER.debug("Cache region " + LOGGER.getName() + "cleared.");
		}
		getCache().clearCache();
	}

	protected EHCacheRegion getCache()
	{
		return cache;
	}

	@Required
	public void setCache(final EHCacheRegion cache)
	{
		this.cache = cache;
	}

	private static class DefaultCacheValueLoaderImpl<V> implements CacheValueLoader<V>
	{
		private final V value;

		public DefaultCacheValueLoaderImpl(final V value)
		{
			this.value = value;
		}

		@Override
		public V load(final CacheKey key)
		{
			return value;
		}

	}

	private static class SupplierCacheValueLoaderImpl<V> implements CacheValueLoader<V>
	{
		private final Supplier<V> supplier;

		public SupplierCacheValueLoaderImpl(final Supplier<V> supplier)
		{
			this.supplier = supplier;
		}

		@Override
		public V load(final CacheKey key)
		{
			return supplier.get();
		}

	}

}
