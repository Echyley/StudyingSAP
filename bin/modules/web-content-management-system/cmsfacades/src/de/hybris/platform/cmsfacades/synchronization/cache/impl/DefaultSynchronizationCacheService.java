/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cmsfacades.synchronization.cache.impl;

import de.hybris.platform.cms2.common.service.SessionCachedContextProvider;
import de.hybris.platform.cmsfacades.synchronization.cache.SynchronizationCacheService;
import de.hybris.platform.core.model.ItemModel;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Required;


/**
 * Default implementation of {@link SynchronizationCacheService}.
 */
public class DefaultSynchronizationCacheService implements SynchronizationCacheService
{
	public static final String SYNCHRONIZATION_ITEM_CACHE = "SYNCHRONIZATION_ITEM_CACHE";

	private SessionCachedContextProvider sessionCachedContextProvider;

	@Override
	public void initCache()
	{
		getSessionCachedContextProvider().getAllItemsFromMapCache(SYNCHRONIZATION_ITEM_CACHE);
	}

	@Override
	public <T extends Object> T getOrSetItemCache(final Supplier<T> supplier, final String cacheKey)
	{
		return (T) getFromCacheOrRetrieve(supplier, cacheKey);
	}

	@Override
	public <T> List<T> getOrSetItemListCache(Supplier<List<T>> supplier, String cacheKey)
	{
		return (List<T>) getFromCacheOrRetrieve(supplier, cacheKey);
	}

	@Override
	public <T extends Object> List<T> getOrSetItemListCache(final Supplier<List<T>> supplier, final String cachePrefix, final ItemModel... itemsForKeys)
	{
		final String cacheKey = cachePrefix + "_" + getCacheKey(itemsForKeys);
		return (List<T>) getFromCacheOrRetrieve(supplier, cacheKey);
	}

	/**
	 * Retrieves the value from cache if it exists in the cache. If not exist, the value is provided by the supplier and put in the cache.
	 * @param supplier the supplier of the value
	 * @param cacheKey the cache key
	 * @param <T> the type T can represent any java type (collection included)
	 * @return the value.
	 */
	protected <T> Object getFromCacheOrRetrieve(Supplier<T> supplier, String cacheKey)
	{
		final Map<String, T> cache = getSessionCachedContextProvider().getAllItemsFromMapCache(SYNCHRONIZATION_ITEM_CACHE);
		T cachedList = cache.get(cacheKey);

		if (Objects.isNull(cachedList))
		{
			cachedList = supplier.get();
			getSessionCachedContextProvider().addItemToMapCache(SYNCHRONIZATION_ITEM_CACHE, cacheKey, cachedList);
		}

		return cachedList;
	}

	/**
	 * Generates the cache based on the list of items.
	 * @param items the list of items.
	 * @return the cache key.
	 */
	protected String getCacheKey(final ItemModel... items)
	{
		return Arrays.stream(items).map(item -> item.getPk().getLongValueAsString()).collect(Collectors.joining());
	}

	public SessionCachedContextProvider getSessionCachedContextProvider()
	{
		return sessionCachedContextProvider;
	}

	@Required
	public void setSessionCachedContextProvider(
			final SessionCachedContextProvider sessionCachedContextProvider)
	{
		this.sessionCachedContextProvider = sessionCachedContextProvider;
	}
}
