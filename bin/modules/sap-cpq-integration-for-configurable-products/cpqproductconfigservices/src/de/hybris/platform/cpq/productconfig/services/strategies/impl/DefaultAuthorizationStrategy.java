/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cpq.productconfig.services.strategies.impl;

import de.hybris.platform.apiregistryservices.exceptions.CredentialException;
import de.hybris.platform.apiregistryservices.model.AbstractCredentialModel;
import de.hybris.platform.apiregistryservices.model.BasicCredentialModel;
import de.hybris.platform.apiregistryservices.model.ConsumedDestinationModel;
import de.hybris.platform.apiregistryservices.services.ApiRegistryClientService;
import de.hybris.platform.apiregistryservices.strategies.ConsumedDestinationLocatorStrategy;
import de.hybris.platform.cpq.productconfig.services.AuthorizationService;
import de.hybris.platform.cpq.productconfig.services.client.CpqClient;
import de.hybris.platform.cpq.productconfig.services.client.CpqClientConstants;
import de.hybris.platform.cpq.productconfig.services.client.CpqClientUtil;
import de.hybris.platform.cpq.productconfig.services.data.AuthorizationData;
import de.hybris.platform.cpq.productconfig.services.data.CpqCredentialsData;
import de.hybris.platform.cpq.productconfig.services.data.TokenResponseData;
import de.hybris.platform.cpq.productconfig.services.strategies.AuthorizationStrategy;

import java.util.Map;

import com.google.common.base.Preconditions;
import com.hybris.charon.RawResponse;


/**
 * Default implementation of {@link AuthorizationService}
 */
public class DefaultAuthorizationStrategy implements AuthorizationStrategy
{
	static final long ONE_MINUTE_IN_MS = 60 * 1000l;
	protected static final String GRANT_CLIENT_CREDENTIALS = "client_credentials";
	protected static final String ADDITIONAL_ATTRIBUTE_DOMAIN = "cpqDomain";
	protected static final String ADDITIONAL_ATTRIBUTE_SCOPE = "cpqScope";
	protected static final String ADDITIONAL_ATTRIBUTE_UI_SCOPE = "cpqUiScope";

	private final ConsumedDestinationLocatorStrategy consumedDestinationLocatorStrategy;
	private final ApiRegistryClientService apiRegistryClientService;
	private final CpqClientUtil clientUtil;


	/**
	 * Default Constructor. Expects all required dependencies as constructor args
	 *
	 * @param consumedDestinationLocatorStrategy
	 *           consumedDestinationLocatorStrategy
	 * @param apiRegistryClientService
	 *           apiRegistryClientService
	 * @param clientUtil
	 *           client util
	 */
	public DefaultAuthorizationStrategy(final ConsumedDestinationLocatorStrategy consumedDestinationLocatorStrategy,
			final ApiRegistryClientService apiRegistryClientService, final CpqClientUtil clientUtil)
	{
		super();
		this.consumedDestinationLocatorStrategy = consumedDestinationLocatorStrategy;
		this.apiRegistryClientService = apiRegistryClientService;
		this.clientUtil = clientUtil;
	}

	/**
	 * @return the apiRegistryClientService
	 */
	protected ApiRegistryClientService getApiRegistryClientService()
	{
		return apiRegistryClientService;
	}

	protected ConsumedDestinationLocatorStrategy getConsumedDestinationLocatorStrategy()
	{
		return consumedDestinationLocatorStrategy;
	}

	@Override
	public CpqCredentialsData getCpqCredentialsForAdmin()
	{

		final CpqCredentialsData credentials = getCPQBasicAuthCredentials();
		final String scope = retrieveAdminScope();
		credentials.setScope(scope);
		return credentials;
	}

	protected String retrieveAdminScope()
	{
		final String scope = getAdditionalProps().get(ADDITIONAL_ATTRIBUTE_SCOPE);
		Preconditions.checkNotNull(scope, "We expect scope as part of additional properties");
		return scope;
	}

	protected Map<String, String> getAdditionalProps()
	{
		final ConsumedDestinationModel destination = getConsumedDestinationLocatorStrategy()
				.lookup(CpqClient.class.getSimpleName());
		return destination.getAdditionalProperties();
	}

	protected String retrieveClientScope()
	{
		final String scope = getAdditionalProps().get(ADDITIONAL_ATTRIBUTE_UI_SCOPE);
		Preconditions.checkNotNull(scope, "We expect a UI scope as part of additional properties");
		return scope;
	}

	@Override
	public CpqCredentialsData getCpqCredentialsForClient(final String ownerId)
	{
		final CpqCredentialsData credentials = getCPQBasicAuthCredentials();
		credentials.setOwnerId(ownerId);
		credentials.setScope(retrieveClientScope());
		return credentials;
	}

	protected CpqCredentialsData getCPQBasicAuthCredentials()
	{
		final ConsumedDestinationModel destination = getConsumedDestinationLocatorStrategy()
				.lookup(CpqClient.class.getSimpleName());
		final CpqCredentialsData credentials = new CpqCredentialsData();
		addBasicAuthAttributesFromDestination(destination, credentials);
		addBasicAuthAttributesFromCredentials(destination, credentials);
		return credentials;
	}

	protected void addBasicAuthAttributesFromDestination(final ConsumedDestinationModel destination,
			final CpqCredentialsData credentials)
	{
		Preconditions.checkNotNull(destination, "Destination not available");
		final Map<String, String> additionalProperties = destination.getAdditionalProperties();
		Preconditions.checkNotNull(additionalProperties, "We expect additional properties");
		final String domain = additionalProperties.get(ADDITIONAL_ATTRIBUTE_DOMAIN);
		Preconditions.checkNotNull(domain, "We expect domain as part of additional properties");
		credentials.setDomain(domain);
	}

	protected void addBasicAuthAttributesFromCredentials(final ConsumedDestinationModel destination,
			final CpqCredentialsData credentials)
	{
		final AbstractCredentialModel abstractCredentialModel = destination.getCredential();
		final BasicCredentialModel basicCredential;
		if (abstractCredentialModel instanceof BasicCredentialModel)
		{
			basicCredential = (BasicCredentialModel) abstractCredentialModel;
		}
		else
		{
			throw new IllegalStateException("The destination does not basic auth credentials of type BasicCredentialModel");
		}
		credentials.setUsername(basicCredential.getUsername());
		credentials.setPassword(basicCredential.getPassword());
	}

	protected String getServiceEndpointOAuth()
	{
		final ConsumedDestinationModel destination = getConsumedDestinationLocatorStrategy()
				.lookup(CpqClient.class.getSimpleName());
		return destination.getUrl();
	}

	@Override
	public AuthorizationData getAuthorizationData(final CpqCredentialsData credentials)
	{
		try
		{
			final AuthorizationData sessionAttributes = new AuthorizationData();
			final TokenResponseData tokenResponse = getOauth2Token(credentials);
			sessionAttributes.setAccessToken(tokenResponse.getAccess_token());
			sessionAttributes.setExpiresAt(System.currentTimeMillis() + (tokenResponse.getExpires() * 1000));
			sessionAttributes.setServiceEndpointUrl(getServiceEndpointOAuth());
			sessionAttributes.setOwnerId(credentials.getOwnerId());
			return sessionAttributes;
		}
		catch (final CredentialException e)
		{
			throw new IllegalStateException("Oauth2 token could not be read", e);
		}
	}


	protected TokenResponseData getOauth2Token(final CpqCredentialsData credentials) throws CredentialException
	{
		final CpqClient cpqClient = getApiRegistryClientService().lookupClient(CpqClient.class);
		final String credentialsAsString = concatenateCredentials(credentials);
		final RawResponse<TokenResponseData> rawResponse = clientUtil.toResponse(cpqClient.token(credentialsAsString));
		clientUtil.checkHTTPStatusCode("TOKEN", CpqClientConstants.HTTP_STATUS_OK, rawResponse);
		return clientUtil.toResponse(rawResponse.content());
	}

	protected String concatenateCredentials(final CpqCredentialsData credentials)
	{
		final StringBuilder credentialBuilder = new StringBuilder();
		credentialBuilder.append("grant_type=password&username=") //
				.append(credentials.getUsername()) //
				.append("&password=") //
				.append(credentials.getPassword()) //
				.append("&domain=") //
				.append(credentials.getDomain()); //
		appendIfValuePresent(credentialBuilder, "&scope=", credentials.getScope());
		appendIfValuePresent(credentialBuilder, "&ownerId=", credentials.getOwnerId());
		return credentialBuilder.toString();
	}

	protected void appendIfValuePresent(final StringBuilder builder, final String key, final String value)
	{
		if (value != null)
		{
			builder.append(key).append(value);
		}
	}

	@Override
	public long getTokenExpirationBuffer()
	{
		return ONE_MINUTE_IN_MS;
	}
}
