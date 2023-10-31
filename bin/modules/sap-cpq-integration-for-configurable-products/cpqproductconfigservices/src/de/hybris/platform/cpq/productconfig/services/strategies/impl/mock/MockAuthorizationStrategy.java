/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cpq.productconfig.services.strategies.impl.mock;

import de.hybris.platform.cpq.productconfig.services.data.AuthorizationData;
import de.hybris.platform.cpq.productconfig.services.data.CpqCredentialsData;
import de.hybris.platform.cpq.productconfig.services.strategies.AuthorizationStrategy;

import java.util.UUID;

import org.apache.log4j.Logger;


/**
 * Mock implementation of {@link AuthorizationStrategy}. Does not do much as authorization is no concern for the mock
 */
public class MockAuthorizationStrategy implements AuthorizationStrategy
{
	private static final Logger LOG = Logger.getLogger(MockAuthorizationStrategy.class);

	static final long ONE_SECOND_IN_MS = 1000l;
	/**
	 * time a mock token is considered valid in ms
	 */
	public static final long MOCK_TOKEN_VALIDITY_MS = ONE_SECOND_IN_MS * 10; // 10 seconds
	/**
	 * Indicates that no access token is needed for mock
	 */
	public static final String MOCK_ACCESS_TOKEN = "MOCK_TOKEN";

	/**
	 * Indicates that there is no real endpoint corresponding to the mock
	 */
	public static final String MOCK_ENDPOINT_URL = "MOCK ENDPOINT URL";

	@Override
	public AuthorizationData getAuthorizationData(final CpqCredentialsData credentials)
	{
		final AuthorizationData mockAuthorizationData = new AuthorizationData();
		mockAuthorizationData.setServiceEndpointUrl(MOCK_ENDPOINT_URL);
		mockAuthorizationData.setAccessToken(getAccessToken(credentials));
		mockAuthorizationData.setExpiresAt(System.currentTimeMillis() + MOCK_TOKEN_VALIDITY_MS);
		mockAuthorizationData.setOwnerId(credentials.getOwnerId());
		LOG.debug("generating new mock token: " + mockAuthorizationData.getAccessToken());
		return mockAuthorizationData;
	}

	protected String getAccessToken(final CpqCredentialsData credentials)
	{
		final StringBuilder builder = new StringBuilder(MOCK_ACCESS_TOKEN);
		if (null != credentials.getOwnerId())
		{
			builder.append("|ownerId=").append(credentials.getOwnerId());
		}
		builder.append("|").append(UUID.randomUUID());
		return builder.toString();
	}

	@Override
	public CpqCredentialsData getCpqCredentialsForAdmin()
	{
		return new CpqCredentialsData();
	}

	@Override
	public CpqCredentialsData getCpqCredentialsForClient(final String ownerId)
	{
		final CpqCredentialsData cpqCredentialsData = new CpqCredentialsData();
		cpqCredentialsData.setOwnerId(ownerId);
		return cpqCredentialsData;
	}

	@Override
	public final long getTokenExpirationBuffer()
	{
		return ONE_SECOND_IN_MS;
	}

}
