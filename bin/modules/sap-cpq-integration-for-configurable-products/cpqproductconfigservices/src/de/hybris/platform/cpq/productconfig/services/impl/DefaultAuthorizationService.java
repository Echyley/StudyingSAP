/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cpq.productconfig.services.impl;

import de.hybris.platform.cpq.productconfig.services.AuthorizationService;
import de.hybris.platform.cpq.productconfig.services.CacheAccessService;
import de.hybris.platform.cpq.productconfig.services.CacheKeyService;
import de.hybris.platform.cpq.productconfig.services.StrategyDeterminationService;
import de.hybris.platform.cpq.productconfig.services.cache.DefaultCacheKey;
import de.hybris.platform.cpq.productconfig.services.data.AuthorizationData;
import de.hybris.platform.cpq.productconfig.services.data.CpqCredentialsData;
import de.hybris.platform.cpq.productconfig.services.strategies.AuthorizationStrategy;


/**
 * Default implementation of {@link AuthorizationService}
 */
public class DefaultAuthorizationService implements AuthorizationService
{
	private final CacheAccessService<DefaultCacheKey, AuthorizationData> cacheAccessService;
	private final CacheKeyService cacheKeyService;
	private final StrategyDeterminationService<AuthorizationStrategy> authorizationStrategyDeterminationService;


	/**
	 * Default Constructor. Expects all required dependencies as constructor args
	 *
	 *
	 * @param cacheAccessService
	 *           cache access service
	 * @param cacheKeyService
	 *           cache key service
	 * @param authorizationStrategyDeterminationService
	 *           to decide whether to use mock or default authorization strategy
	 *
	 */
	public DefaultAuthorizationService(final CacheAccessService<DefaultCacheKey, AuthorizationData> cacheAccessService,
			final CacheKeyService cacheKeyService,
			final StrategyDeterminationService<AuthorizationStrategy> authorizationStrategyDeterminationService)
	{

		this.cacheAccessService = cacheAccessService;
		this.cacheKeyService = cacheKeyService;
		this.authorizationStrategyDeterminationService = authorizationStrategyDeterminationService;
	}

	protected CacheAccessService<DefaultCacheKey, AuthorizationData> getCacheAccessService()
	{
		return cacheAccessService;
	}

	protected CacheKeyService getCacheKeyService()
	{
		return cacheKeyService;
	}

	@Override
	public AuthorizationData getAuthorizationDataForAdmin()
	{
		final CpqCredentialsData credentials = getAuthorizationStrategyDeterminationService().get().getCpqCredentialsForAdmin();
		final DefaultCacheKey cacheKey = getCacheKeyService().createAuthorizationDataCacheKey();
		AuthorizationData currentTokenData = getCacheAccessService().getWithSupplier(cacheKey,
				() -> retrieveAuthorizationData(credentials));
		final long expirationBuffer = getAuthorizationStrategyDeterminationService().get().getTokenExpirationBuffer();
		if (currentTokenData.getExpiresAt() - expirationBuffer < System.currentTimeMillis())
		{
			// token is expired or expires within one second.
			// get a new token and put it instead of removing and getting with supplier, as remove is not guaranteed to have an immediate effect
			currentTokenData = retrieveAuthorizationData(credentials);
			cacheAccessService.put(cacheKey, currentTokenData);
		}
		return currentTokenData;
	}

	protected AuthorizationData retrieveAuthorizationData(final CpqCredentialsData credentials)
	{
		return getAuthorizationStrategyDeterminationService().get().getAuthorizationData(credentials);
	}

	@Override
	public AuthorizationData getAuthorizationDataForClient(final String ownerId)
	{
		final CpqCredentialsData credentials = getAuthorizationStrategyDeterminationService().get()
				.getCpqCredentialsForClient(ownerId);
		return retrieveAuthorizationData(credentials);
	}

	protected StrategyDeterminationService<AuthorizationStrategy> getAuthorizationStrategyDeterminationService()
	{
		return authorizationStrategyDeterminationService;
	}


}
