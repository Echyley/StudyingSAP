/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.core.sapsalesordersimulation.cache.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.sap.core.sapsalesordersimulation.cache.exceptions.SAPHybrisCacheException;
import de.hybris.platform.sap.core.sapsalesordersimulation.cache.service.CacheAccess;
import de.hybris.platform.sap.sapsalesordersimulation.service.SapProductAvailability;


/**
 * Cache helper adds/retrieves objects from cache
 */
public class SapProductAvailabilityCache
{

	protected CacheAccess sapAtpCheckAvailabilityCacheRegion;
	private static final Logger LOGGER = LoggerFactory.getLogger(SapProductAvailabilityCache.class);
	/**
	 * read SapProductAvailability from cache
	 * 
	 * @param product
	 * @param customerId
	 * @return SapProductAvailability
	 */
	public SapProductAvailability readCachedProductAvailability(final ProductModel product, final String customerId,final String plant)
	{
		
		return (SapProductAvailability) getSapAtpCheckAvailabilityCacheRegion().get(createCacheKey(product, customerId,plant));
	}


	/**
	 * add SapProductAvailability to cache, in case of failure log error
	 * 
	 * @param availability
	 * @param product
	 * @param customerId
	 */
	public void cacheProductAvailability(final SapProductAvailability availability, final ProductModel product,
			final String customerId,String plant)
	{

		try
		{
			getSapAtpCheckAvailabilityCacheRegion().put(createCacheKey(product, customerId,plant), availability);
		}
		catch (final SAPHybrisCacheException e)
		{
			LOGGER.error("Error while adding SapProductAvailability to cache for availability ");
		}
		

	}

	protected String createCacheKey(final ProductModel product, final String customerId,String plant)
	{
		final StringBuilder plantCacheKey = new StringBuilder();

		plantCacheKey.append("SAP_ATP");

		plantCacheKey.append(product.getCode());
		plantCacheKey.append(plant);
		plantCacheKey.append(StringUtils.isEmpty(customerId) ? "CUSTNULL" : customerId);
		

		return plantCacheKey.toString();
	}


	public CacheAccess getSapAtpCheckAvailabilityCacheRegion() {
		return sapAtpCheckAvailabilityCacheRegion;
	}


	public void setSapAtpCheckAvailabilityCacheRegion(CacheAccess sapAtpCheckAvailabilityCacheRegion) {
		this.sapAtpCheckAvailabilityCacheRegion = sapAtpCheckAvailabilityCacheRegion;
	}


	

}
