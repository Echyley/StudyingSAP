/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.core.saps4omservices.cache.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ObjectUtils;

import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.sap.core.saps4omservices.cache.exceptions.SAPS4OMHybrisCacheException;
import de.hybris.platform.sap.core.saps4omservices.cache.service.CacheAccess;
import de.hybris.platform.sap.saps4omservices.services.SapS4OMProductAvailability;



/**
 * Cache helper adds/retrieves objects from cache
 */
public class SapS4OMProductAvailabilityCache
{

	private CacheAccess sapS4OMAtpCheckAvailabilityCacheRegion;
	private static final Logger LOG = LoggerFactory.getLogger(SapS4OMProductAvailabilityCache.class);
	/**
	 * read SapS4OMProductAvailability from cache
	 * 
	 * @param product
	 * @param customerId
	 * @return SapS4OMProductAvailability
	 */
	public SapS4OMProductAvailability readCachedProductAvailability(final ProductModel product, final String customerId,final String plant)
	{
		LOG.debug("Fetch product availability called from cache");
		return (SapS4OMProductAvailability) getSapS4OMAtpCheckAvailabilityCacheRegion().get(createCacheKey(product, customerId,plant));
	}


	/**
	 * add SapS4OMProductAvailability to cache, in case of failure log error
	 * 
	 * @param availability
	 * @param product
	 * @param customerId
	 */
	public void cacheProductAvailability(final SapS4OMProductAvailability availability, final ProductModel product,
			final String customerId,String plant)
	{

		try
		{
			LOG.debug("add product availability to cache");
			getSapS4OMAtpCheckAvailabilityCacheRegion().put(createCacheKey(product, customerId,plant), availability);
		}
		catch (final SAPS4OMHybrisCacheException e)
		{
			LOG.error("Error while adding SapS4OMProductAvailability to cache for availability ");
		}
		

	}

	/**
	 * Remove SapS4OMProductAvailability from cache for given product, customerId and plant
	 * 
	 * @param product product
	 * @param customerId customer id
	 * @param plant plant
	 */
	public void invalidateProductAvailability(final ProductModel product, final String customerId,String plant)
	{
		try
		{
			LOG.debug("Remove product availability from cache");
			getSapS4OMAtpCheckAvailabilityCacheRegion().remove(createCacheKey(product, customerId,plant));
		}
		catch (final SAPS4OMHybrisCacheException e)
		{
			LOG.error("Error while removing SapS4OMProductAvailability from cache for availability ");
		}
	}

	protected String createCacheKey(final ProductModel product, final String customerId,String plant)
	{
		final StringBuilder plantCacheKey = new StringBuilder();

		plantCacheKey.append("SAP_ATP");

		plantCacheKey.append(product.getCode());
		plantCacheKey.append(plant);
		plantCacheKey.append(ObjectUtils.isEmpty(customerId) ? "CUSTNULL" : customerId);
		

		return plantCacheKey.toString();
	}


	public CacheAccess getSapS4OMAtpCheckAvailabilityCacheRegion() {
		return sapS4OMAtpCheckAvailabilityCacheRegion;
	}


	public void setSapS4OMAtpCheckAvailabilityCacheRegion(CacheAccess sapS4OMAtpCheckAvailabilityCacheRegion) {
		this.sapS4OMAtpCheckAvailabilityCacheRegion = sapS4OMAtpCheckAvailabilityCacheRegion;
	}


	

}
