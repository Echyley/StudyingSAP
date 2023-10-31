/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.core.sapsalesordersimulation.cache.service;

import de.hybris.platform.regioncache.CacheValueLoader;
import de.hybris.platform.sap.core.sapsalesordersimulation.cache.exceptions.SAPHybrisCacheException;

import java.util.Set;


/**
 * The <code>CacheAccess</code> provides the direct access to a cache region. This class should only be used for the
 * migration of existing SAP application modules. For other caching scenarios use the Hybris Cache Framework.
 * 
 */
public interface CacheAccess
{

	/**
	 * Gets an object from the cache for the given key. <br>
	 * 
	 * @param key
	 *           name of object which should be retrieved from the cache
	 * @return the object or <code>null</code> if the object could not be found in the cache
	 */
	public Object get(Object key);

	/**
	 * Provide the keys of all objects, which are currently in the cache region.<br>
	 * 
	 * @return all keys.
	 */
	public Set<Object> getKeys();

	/**
	 * Puts an object into the cache region.
	 * 
	 * @param key
	 *           key of object
	 * @param object
	 *           object which is put into the cache
	 * @throws SAPHybrisCacheException
	 *            thrown if something goes wrong while adding an object to cache
	 */
	public void put(Object key, Object object) throws SAPHybrisCacheException;

	/**
	 * Puts an object into the cache region, only if no object with same key already exists. <br>
	 * 
	 * @param name
	 *           name of object
	 * @param object
	 *           object which is put into the cache
	 * @throws SAPHybrisCacheException
	 *            thrown if something goes wrong while adding an object to cache
	 */
	public void putIfAbsent(Object name, Object object) throws SAPHybrisCacheException;

	/**
	 * Removes an object from the cache. The cached object cannot be longer used.
	 * 
	 * @param key
	 *           the name of the object which has to be removed from the cache
	 * @throws SAPHybrisCacheException
	 *            if removing object fails
	 */
	public void remove(Object key) throws SAPHybrisCacheException;

	/**
	 * Returns number of objects currently stored in the cache.
	 * 
	 * @return number of objects currently stored in the cache.
	 */
	public int getNumObjects();

	/**
	 * Gets an object from the cache using the loader to get the value. <br>
	 * 
	 * @param key
	 *           the name of the object which has to loaded
	 * @param loader
	 *           the loader object which is used to load the value
	 * @return number of objects currently stored in the cache.
	 */
	public Object getWithLoader(Object key, CacheValueLoader<?> loader);

	/**
	 * Removes all elements from cache.
	 */
	public void clearCache();

}
