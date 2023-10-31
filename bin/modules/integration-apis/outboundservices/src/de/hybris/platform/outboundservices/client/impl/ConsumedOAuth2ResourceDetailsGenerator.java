/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.outboundservices.client.impl;

import static de.hybris.platform.apiregistryservices.services.impl.DefaultApiRegistryClientService.CLIENT_SCOPE;

import de.hybris.platform.apiregistryservices.model.AbstractCredentialModel;
import de.hybris.platform.apiregistryservices.model.ConsumedDestinationModel;
import de.hybris.platform.apiregistryservices.model.ConsumedOAuthCredentialModel;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.springframework.security.oauth2.client.resource.OAuth2ProtectedResourceDetails;
import org.springframework.security.oauth2.client.token.grant.client.ClientCredentialsResourceDetails;

/**
 * Implementation of {@link OAuth2ResourceDetailsGenerator} that generates the resource details from a
 * {@link ConsumedOAuthCredentialModel}
 */
public class ConsumedOAuth2ResourceDetailsGenerator implements OAuth2ResourceDetailsGenerator
{
	@Override
	public boolean isApplicable(final AbstractCredentialModel credentialModel)
	{
		return credentialModel instanceof ConsumedOAuthCredentialModel;
	}

	@Override
	public OAuth2ProtectedResourceDetails createResourceDetails(final ConsumedDestinationModel destination)
	{
		final ConsumedOAuthCredentialModel credential = (ConsumedOAuthCredentialModel) destination.getCredential();
		final ClientCredentialsResourceDetails resource = new ClientCredentialsResourceDetails();
		resource.setAccessTokenUri(credential.getOAuthUrl());
		resource.setClientId(credential.getClientId());
		resource.setClientSecret(credential.getClientSecret());
		resource.setScope(deriveScope(destination));
		return resource;
	}

	private List<String> deriveScope(final ConsumedDestinationModel destination)
	{
		final Map<String, String> additionalProperties = destination.getAdditionalProperties();
		final String scopes = additionalProperties.get(CLIENT_SCOPE);

		return scopes != null ? List.of(scopes.split(",")) : Collections.emptyList();
	}
}