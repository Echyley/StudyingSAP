/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.outboundservices.client.impl;

import de.hybris.platform.apiregistryservices.model.ConsumedDestinationModel;
import de.hybris.platform.outboundservices.cache.DestinationRestTemplateId;
import de.hybris.platform.outboundservices.cache.impl.DestinationOauthRestTemplateId;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.token.grant.client.ClientCredentialsAccessTokenProvider;

/**
 * The default implementation for OAuth2RestTemplate creator.
 */
public class DefaultIntegrationOAuth2RestTemplateCreator extends AbstractRestTemplateCreator
{
	private OAuth2ResourceDetailsGeneratorFactory oAuth2ResourceDetailsGeneratorFactory;

	@Override
	public boolean isApplicable(final ConsumedDestinationModel destination)
	{
		return oAuth2ResourceDetailsGeneratorFactory.isApplicable(destination.getCredential());
	}

	@Override
	protected OAuth2RestTemplate createRestTemplate(final ConsumedDestinationModel destination)
	{
		final OAuth2ResourceDetailsGenerator generator = getoAuth2ResourceDetailsGeneratorFactory().getGenerator(destination.getCredential());
		final OAuth2RestTemplate oAuth2RestTemplate = new OAuth2RestTemplate(generator.createResourceDetails(destination));
		final ClientHttpRequestFactory clientFactory = getClientHttpRequestFactory();
		oAuth2RestTemplate.setAccessTokenProvider(accessTokenProvider(clientFactory));
		oAuth2RestTemplate.setRequestFactory(clientFactory);
		oAuth2RestTemplate.getAccessToken();
		addInterceptors(oAuth2RestTemplate);
		addMessageConverters(oAuth2RestTemplate);
		return oAuth2RestTemplate;
	}

	protected ClientCredentialsAccessTokenProvider accessTokenProvider(final ClientHttpRequestFactory clientFactory)
	{
		final ClientCredentialsAccessTokenProvider provider = new ClientCredentialsAccessTokenProvider();
		provider.setRequestFactory(clientFactory);

		return provider;
	}

	@Override
	protected DestinationRestTemplateId getDestinationRestTemplateId(final ConsumedDestinationModel destinationModel)
	{
		return DestinationOauthRestTemplateId.from(destinationModel);
	}

	protected OAuth2ResourceDetailsGeneratorFactory getoAuth2ResourceDetailsGeneratorFactory()
	{
		return oAuth2ResourceDetailsGeneratorFactory;
	}

	@Required
	public void setoAuth2ResourceDetailsGeneratorFactory(final OAuth2ResourceDetailsGeneratorFactory oAuth2ResourceDetailsGeneratorFactory)
	{
		this.oAuth2ResourceDetailsGeneratorFactory = oAuth2ResourceDetailsGeneratorFactory;
	}
}
