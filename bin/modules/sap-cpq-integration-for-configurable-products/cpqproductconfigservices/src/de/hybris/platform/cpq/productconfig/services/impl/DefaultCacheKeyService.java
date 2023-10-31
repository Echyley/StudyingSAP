/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cpq.productconfig.services.impl;

import de.hybris.platform.core.Registry;
import de.hybris.platform.cpq.productconfig.services.CacheKeyService;
import de.hybris.platform.cpq.productconfig.services.cache.DefaultCacheKey;
import de.hybris.platform.regioncache.key.CacheUnitValueType;
import de.hybris.platform.site.BaseSiteService;

import java.util.HashMap;
import java.util.Map;


/**
 * Default implementation of the {@link CacheKeyService}
 */
public class DefaultCacheKeyService implements CacheKeyService
{

	protected static final String NO_ACTIVE_TENANT = "NO_ACTIVE_TENANT";
	protected static final String KEY_BASE_SITE = "BASE_SITE";
	protected static final String KEY_CONFIG_ID = "CONFIG_ID";
	protected static final String TYPECODE_AUTHORIZATION_DATA = "__AUTHORIZATION_DATA__";
	protected static final String TYPECODE_CONFIGURATION_SUMMARY = "__CONFIGURATION_SUMMARY__";

	private final BaseSiteService baseSiteService;

	/**
	 * Default constructor
	 *
	 * @param baseSiteService
	 *           base site service
	 */
	public DefaultCacheKeyService(final BaseSiteService baseSiteService)
	{
		super();
		this.baseSiteService = baseSiteService;
	}


	@Override
	public DefaultCacheKey createAuthorizationDataCacheKey(final String baseSite)
	{
		final Map<String, String> keys = new HashMap<>();
		keys.put(KEY_BASE_SITE, baseSite);
		return new DefaultCacheKey(keys, CacheUnitValueType.SERIALIZABLE, TYPECODE_AUTHORIZATION_DATA, getTenantId());
	}

	@Override
	public DefaultCacheKey createAuthorizationDataCacheKey()
	{
		return createAuthorizationDataCacheKey(getCurrentBaseSite());
	}

	protected String getTenantId()
	{
		if (Registry.hasCurrentTenant())
		{
			return Registry.getCurrentTenant().getTenantID();
		}
		return NO_ACTIVE_TENANT;
	}

	protected String getCurrentBaseSite()
	{
		return getBaseSiteService().getCurrentBaseSite().getUid();
	}


	protected BaseSiteService getBaseSiteService()
	{
		return baseSiteService;
	}


	@Override
	public DefaultCacheKey createConfigurationSummaryCacheKey(final String configId)
	{
		final Map<String, String> keys = new HashMap<>();
		keys.put(KEY_CONFIG_ID, configId);
		return new DefaultCacheKey(keys, CacheUnitValueType.SERIALIZABLE, TYPECODE_CONFIGURATION_SUMMARY, getTenantId());
	}

}
