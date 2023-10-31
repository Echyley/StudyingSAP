/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.outboundservices.client.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.apiregistryservices.model.AbstractCredentialModel;
import de.hybris.platform.apiregistryservices.model.BasicCredentialModel;
import de.hybris.platform.apiregistryservices.model.ConsumedDestinationModel;
import de.hybris.platform.apiregistryservices.model.ConsumedOAuthCredentialModel;
import de.hybris.platform.apiregistryservices.model.ExposedOAuthCredentialModel;
import de.hybris.platform.outboundservices.cache.RestTemplateCache;
import de.hybris.platform.outboundservices.cache.impl.DefaultDestinationRestTemplateCacheKey;
import de.hybris.platform.outboundservices.cache.impl.DestinationOauthRestTemplateId;
import de.hybris.platform.webservicescommons.model.OAuthClientDetailsModel;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.http.OAuth2ErrorHandler;
import org.springframework.security.oauth2.client.resource.OAuth2ProtectedResourceDetails;
import org.springframework.security.oauth2.client.token.grant.client.ClientCredentialsResourceDetails;
import org.springframework.web.client.RestOperations;
import org.springframework.web.client.RestTemplate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultIntegrationOAuth2RestTemplateCreatorUnitTest
{
	private static final String DESTINATION_URL = "https://localhost:9002/odata2webservices/InboundProduct/Products";
	private static final String OAUTH_URL = "https://localhost:9002/authorizationserver/oauth/token";
	private static final String CLIENT_ID = "admin";
	private static final String CLIENT_SECRET = "nimda";
	private static final String SCOPE = "scope";

	@InjectMocks
	private DefaultIntegrationOAuth2RestTemplateCreator oAuth2RestTemplateCreator;

	@Mock(lenient = true)
	private ClientHttpRequestFactory clientHttpRequestFactory;
	@Mock(lenient = true)
	private MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter;
	@Mock(lenient = true)
	private ClientHttpRequestInterceptor interceptor;
	@Mock(lenient = true)
	private RestTemplateCache restTemplateCache;
	@Mock(lenient = true)
	private OAuth2ResourceDetailsGeneratorFactory oAuth2ResourceDetailsGeneratorFactory;
	@Mock(lenient = true)
	private ExposedOAuth2ResourceDetailsGenerator exposedOAuth2ResourceDetailsGenerator;

	@Before
	public void setup() throws IOException
	{
		final ClientHttpRequest request = httpRequest(httpResponse());
		doReturn(request).when(clientHttpRequestFactory).createRequest(any(URI.class), any(HttpMethod.class));

		oAuth2RestTemplateCreator.setMessageConverters(Collections.singletonList(mappingJackson2HttpMessageConverter));
		oAuth2RestTemplateCreator.setRequestInterceptors(Collections.singletonList(interceptor));

		doReturn(true).when(oAuth2ResourceDetailsGeneratorFactory).isApplicable(any(ExposedOAuthCredentialModel.class));
		doReturn(true).when(oAuth2ResourceDetailsGeneratorFactory).isApplicable(any(ConsumedOAuthCredentialModel.class));
		doReturn(exposedOAuth2ResourceDetailsGenerator).when(oAuth2ResourceDetailsGeneratorFactory).getGenerator(any(ExposedOAuthCredentialModel.class));

		doReturn(resourceDetails()).when(exposedOAuth2ResourceDetailsGenerator).createResourceDetails(any());
	}

	private ClientHttpRequest httpRequest(final ClientHttpResponse response) throws IOException
	{
		final ClientHttpRequest request = mock(ClientHttpRequest.class);
		lenient().when(request.getHeaders()).thenReturn(new HttpHeaders());
		lenient().when(request.getBody()).thenReturn(mock(OutputStream.class));
		lenient().when(request.execute()).thenReturn(response);
		return request;
	}

	private ClientHttpResponse httpResponse() throws IOException
	{
		final HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.TEXT_PLAIN);

		final ClientHttpResponse response = mock(ClientHttpResponse.class);
		lenient().when(response.getHeaders()).thenReturn(headers);
		lenient().when(response.getBody()).thenReturn(new ByteArrayInputStream("access_token=123".getBytes()));
		return response;
	}

	@Test
	public void shouldCreateRestTemplateIfItIsNotAvailableInCache()
	{
		final RestOperations restOperations = oAuth2RestTemplateCreator.create(consumedDestination());

		assertThat(restOperations)
				.isExactlyInstanceOf(OAuth2RestTemplate.class)
				.hasFieldOrPropertyWithValue("resource.clientId", CLIENT_ID)
				.hasFieldOrPropertyWithValue("resource.clientSecret", CLIENT_SECRET)
				.hasFieldOrPropertyWithValue("resource.accessTokenUri", OAUTH_URL)
				.hasFieldOrPropertyWithValue("resource.scope", List.of(SCOPE));

		final OAuth2RestTemplate restTemplate = (OAuth2RestTemplate) restOperations;
		assertThat(restTemplate.getMessageConverters()).contains(mappingJackson2HttpMessageConverter);
		assertThat(restTemplate.getInterceptors()).contains(interceptor);
		assertThat(restTemplate.getErrorHandler()).isInstanceOf(OAuth2ErrorHandler.class);
	}

	@Test
	public void doesNotCreateRestTemplateIfItIsAvailableInCache()
	{
		final ConsumedDestinationModel destination = consumedDestination();
		final RestTemplate cachedTemplate = givenCacheContainsTemplateForDestination(destination);

		final RestOperations createdTemplate = oAuth2RestTemplateCreator.create(destination);

		assertThat(createdTemplate).isSameAs(cachedTemplate);
	}

	@Test
	public void createRestTemplateIfCacheContainsTemplatesOnlyForDifferentDestination()
	{
		final ConsumedDestinationModel destination1 = consumedDestination();
		final ConsumedDestinationModel destination2 = consumedDestination();
		final RestTemplate cachedTemplate = givenCacheContainsTemplateForDestination(destination1);

		final RestOperations createdTemplate = oAuth2RestTemplateCreator.create(destination2);

		assertThat(createdTemplate).isNotSameAs(cachedTemplate);
	}

	@Test
	public void shouldThrowUnsupportedRestTemplateExceptionWhenConsumedDestinationIsNotOAuth()
	{
		doThrow(UnsupportedRestTemplateException.class).when(oAuth2ResourceDetailsGeneratorFactory).isApplicable(any(BasicCredentialModel.class));

		final ConsumedDestinationModel destination = consumedDestination(mock(BasicCredentialModel.class));

		assertThatThrownBy(() -> oAuth2RestTemplateCreator.create(destination))
				.isInstanceOf(UnsupportedRestTemplateException.class);
	}

	@Test
	public void mayNotHaveInterceptors()
	{
		oAuth2RestTemplateCreator.setRequestInterceptors(null);

		final OAuth2RestTemplate restTemplate = (OAuth2RestTemplate) oAuth2RestTemplateCreator.create(consumedDestination());

		assertThat(restTemplate.getInterceptors()).isEmpty();
	}

	@Test
	public void messageConvertersIsEmptyWhenSetWithNull()
	{
		oAuth2RestTemplateCreator.setMessageConverters(null);
		assertThat(oAuth2RestTemplateCreator.getMessageConverters()).isEmpty();
	}

	@Test
	public void setMessageConvertersDoesNotLeakReferences()
	{
		final List<HttpMessageConverter<Object>> converters = new ArrayList<>();
		converters.add(mappingJackson2HttpMessageConverter);

		oAuth2RestTemplateCreator.setMessageConverters(converters);
		converters.clear();

		assertThat(oAuth2RestTemplateCreator.getMessageConverters()).isNotEmpty();
	}

	@Test
	public void getMessageConvertersDoesNotLeakReferences()
	{
		final List<HttpMessageConverter<Object>> copy = oAuth2RestTemplateCreator.getMessageConverters();
		copy.clear();

		assertThat(oAuth2RestTemplateCreator.getMessageConverters()).isNotEmpty();
	}

	@Test
	public void requestInterceptorsIsEmptyWhenSetWithNull()
	{
		oAuth2RestTemplateCreator.setRequestInterceptors(null);
		assertThat(oAuth2RestTemplateCreator.getRequestInterceptors()).isEmpty();
	}

	@Test
	public void setRequestInterceptorsDoesNotLeakReference()
	{
		final List<ClientHttpRequestInterceptor> interceptors = new ArrayList<>();
		interceptors.add(interceptor);

		oAuth2RestTemplateCreator.setRequestInterceptors(interceptors);
		interceptors.clear();

		assertThat(oAuth2RestTemplateCreator.getRequestInterceptors()).isNotEmpty();
	}

	@Test
	public void getRequestInterceptorsDoesNotLeakReferences()
	{
		final List<ClientHttpRequestInterceptor> copy = oAuth2RestTemplateCreator.getRequestInterceptors();
		copy.clear();

		assertThat(oAuth2RestTemplateCreator.getRequestInterceptors()).isNotEmpty();
	}

	private RestTemplate givenCacheContainsTemplateForDestination(final ConsumedDestinationModel dest)
	{
		final RestTemplate cachedTemplate = mock(RestTemplate.class);
		doReturn(cachedTemplate).when(restTemplateCache).get(cacheKey(dest));
		return cachedTemplate;
	}

	private static DefaultDestinationRestTemplateCacheKey cacheKey(final ConsumedDestinationModel destination)
	{
		return DefaultDestinationRestTemplateCacheKey.from(DestinationOauthRestTemplateId.from(destination));
	}

	private static ConsumedDestinationModel consumedDestination()
	{
		final OAuthClientDetailsModel client = mock(OAuthClientDetailsModel.class);
		lenient().when(client.getOAuthUrl()).thenReturn(OAUTH_URL);
		lenient().when(client.getClientId()).thenReturn(CLIENT_ID);
		lenient().when(client.getScope()).thenReturn(Set.of(SCOPE));

		final ExposedOAuthCredentialModel credential = mock(ExposedOAuthCredentialModel.class);
		lenient().when(credential.getOAuthClientDetails()).thenReturn(client);
		lenient().when(credential.getPassword()).thenReturn(CLIENT_SECRET);

		return consumedDestination(credential);
	}

	private static OAuth2ProtectedResourceDetails resourceDetails()
	{
		final ClientCredentialsResourceDetails resourceDetails = new ClientCredentialsResourceDetails();
		resourceDetails.setAccessTokenUri(OAUTH_URL);
		resourceDetails.setClientId(CLIENT_ID);
		resourceDetails.setClientSecret(CLIENT_SECRET);
		resourceDetails.setScope(List.of(SCOPE));

		return resourceDetails;
	}

	private static ConsumedDestinationModel consumedDestination(final AbstractCredentialModel credential)
	{
		final ConsumedDestinationModel destination = mock(ConsumedDestinationModel.class);
		lenient().when(destination.getUrl()).thenReturn(DESTINATION_URL);
		lenient().when(destination.getCredential()).thenReturn(credential);
		return destination;
	}
}
