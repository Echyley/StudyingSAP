/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cpq.productconfig.services.impl;

import de.hybris.platform.core.Registry;
import de.hybris.platform.cpq.productconfig.services.CacheAccessService;
import de.hybris.platform.cpq.productconfig.services.EngineDeterminationService;
import de.hybris.platform.cpq.productconfig.services.cache.DefaultCacheKey;
import de.hybris.platform.cpq.productconfig.services.data.AuthorizationData;

import org.apache.log4j.Logger;


/**
 * Default implementation of {@link EngineDeterminationService}
 */
public class DefaultEngineDeterminationService implements EngineDeterminationService
{
	private static final Logger LOG = Logger.getLogger(DefaultEngineDeterminationService.class);

	private static final String JUNIT = "junit";

	private final boolean defaultMockEngineActive;
	private boolean mockEngineActive;
	private final CacheAccessService<DefaultCacheKey, AuthorizationData> cacheAccessService;


	/**
	 * Inject mandatory beans
	 *
	 * @param cacheAccessService
	 *           Used to invalidate caches on change of the engine
	 * @param defaultMockEngineActive
	 *           default behaviour - can be configured via local.properties
	 *           <code>cpqproductconfigservices.mockEngineActive=false</code><br>
	 *           will restore this value on {@link #reset()}
	 */
	public DefaultEngineDeterminationService(final CacheAccessService<DefaultCacheKey, AuthorizationData> cacheAccessService,
			final boolean defaultMockEngineActive)
	{
		this.cacheAccessService = cacheAccessService;
		this.defaultMockEngineActive = defaultMockEngineActive;
		this.mockEngineActive = defaultMockEngineActive;
		logMockEngineWarning();
	}

	private void logMockEngineWarning()
	{
		if (isMockEngineActive())
		{
			LOG.info("Mock engine is active. This is for test purposes only and must not be used in a production environment.");
		}
	}

	protected CacheAccessService<DefaultCacheKey, AuthorizationData> getCacheAccessService()
	{
		return cacheAccessService;
	}

	/**
	 * @param mockEngineActive
	 *           Is mock engine active?
	 */
	public void setMockEngineActive(final boolean mockEngineActive)
	{
		if (!isEngineSwitchAllowed())
		{
			throw new IllegalStateException("Switching between mock/real Engine is only supported for junit tenant");
		}
		getCacheAccessService().clearCache();
		this.mockEngineActive = mockEngineActive;
		logMockEngineWarning();
	}

	@Override
	public boolean isMockEngineActive()
	{
		return this.mockEngineActive;
	}

	/**
	 * reset the engine state to the configured default value of the system
	 */
	public void reset()
	{
		setMockEngineActive(defaultMockEngineActive);
	}

	protected boolean isEngineSwitchAllowed()
	{
		if (Registry.hasCurrentTenant())
		{
			return JUNIT.equals(Registry.getCurrentTenant().getTenantID());
		}
		return true;
	}

}
