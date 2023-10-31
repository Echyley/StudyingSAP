/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cpq.productconfig.services.strategies.impl;

import de.hybris.platform.apiregistryservices.exceptions.CredentialException;
import de.hybris.platform.apiregistryservices.services.ApiRegistryClientService;
import de.hybris.platform.cpq.productconfig.services.AuthorizationService;
import de.hybris.platform.cpq.productconfig.services.client.CpqClient;
import de.hybris.platform.cpq.productconfig.services.strategies.CPQInteractionStrategy;


/**
 * Default implementation of the {@link CPQInteractionStrategy}
 */
public class DefaultCPQInteractionStrategy implements CPQInteractionStrategy
{
	private final ApiRegistryClientService apiRegistryClientService;
	private final AuthorizationService authorizationService;

	/**
	 * Injection of mandatory beans
	 *
	 * @param apiRegistryClientService
	 * @param authorizationService
	 */
	public DefaultCPQInteractionStrategy(final ApiRegistryClientService apiRegistryClientService,
			final AuthorizationService authorizationService)
	{
		this.apiRegistryClientService = apiRegistryClientService;
		this.authorizationService = authorizationService;
	}

	protected ApiRegistryClientService getApiRegistryClientService()
	{
		return apiRegistryClientService;
	}

	protected AuthorizationService getAuthorizationService()
	{
		return authorizationService;
	}

	@Override
	public CpqClient getClient()
	{
		final CpqClient cpqClient;
		try
		{
			cpqClient = getApiRegistryClientService().lookupClient(CpqClient.class);
		}
		catch (final CredentialException e)
		{
			throw new IllegalStateException("Could not get client", e);
		}
		return cpqClient;
	}

	@Override
	public String getAuthorizationString()
	{
		return "Bearer " + getAuthorizationService().getAuthorizationDataForAdmin().getAccessToken();
	}

	@Override
	public String getClientAuthorizationString(final String ownerId)
	{
		return "Bearer " + getAuthorizationService().getAuthorizationDataForClient(ownerId).getAccessToken();
	}

}
