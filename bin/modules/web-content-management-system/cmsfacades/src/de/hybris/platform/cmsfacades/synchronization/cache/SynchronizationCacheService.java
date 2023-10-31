/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cmsfacades.synchronization.cache;

import de.hybris.platform.core.model.ItemModel;

import java.util.List;
import java.util.function.Supplier;


/**
 * Cache service used to cache elements during synchronization process.
 */
public interface SynchronizationCacheService
{
	/**
	 * Initialize the cache, it creates cache objects in current session.
	 */
	default void initCache()
	{
		// no op
	}

	/**
	 * Retrieves the list of items from the cache. If cache does not contain any items the supplier is called to populate the cache and return the value.
	 * @param supplier the supplier to execute if items does not exist in cache.
	 * @param cachePrefix the cache prefix (is concatenated with the cache key generated based on itemsForKeys)
	 * @param itemsForKeys the list of items that are used to generate a cache key.
	 * @param <T> the type T represents any java type.
	 * @return the list of items.
	 */
	<T extends Object> List<T> getOrSetItemListCache(final Supplier<List<T>> supplier, String cachePrefix, ItemModel... itemsForKeys);

	/**
	 * Retrieves the list of items from the cache. If cache does not contain any items the supplier is called to populate the cache and return the value.
	 * @param supplier the supplier to execute if items does not exist in cache.
	 * @param cacheKey the cache key
	 * @param <T> the type T represents any java type.
	 * @return the list of items.
	 */
	<T extends Object> List<T> getOrSetItemListCache(final Supplier<List<T>> supplier, String cacheKey);

	/**
	 * Retrieves the item from the cache. If cache does not contain an item the supplier is called to populate the cache and return the value.
	 * @param supplier the supplier to execute if item does not exist in cache.
	 * @param cacheKey the cache key
	 * @return the item
	 */
	<T extends Object> T getOrSetItemCache(final Supplier<T> supplier, String cacheKey);
}
