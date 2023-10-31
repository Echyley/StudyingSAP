/*
 * [y] hybris Platform
 *
 * Copyright (c) 2019 SAP SE or an SAP affiliate company.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.outboundservices.client.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.apiregistryservices.model.BasicCredentialModel;
import de.hybris.platform.apiregistryservices.model.ConsumedDestinationModel;
import de.hybris.platform.apiregistryservices.model.ExposedOAuthCredentialModel;
import de.hybris.platform.outboundservices.cache.impl.DestinationRestTemplateCache;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.support.BasicAuthenticationInterceptor;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestOperations;
import org.springframework.web.client.RestTemplate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultIntegrationBasicRestTemplateCreatorUnitTest
{
	private static final String DESTINATION_URL = "https://localhost:9002/odata2webservices/InboundProduct/Products";
	private static final String USERNAME = "admin";
	private static final String PASS = "nimda";

	@Mock(lenient = true)
	private ClientHttpRequestFactory clientHttpRequestFactory;

	@InjectMocks
	private DefaultIntegrationBasicRestTemplateCreator basicRestTemplateCreator;

	@Mock(lenient = true)
	private ConsumedDestinationModel consumedDestination;
	@Mock(lenient = true)
	private ExposedOAuthCredentialModel oAuthCredential;
	@Mock(lenient = true)
	private BasicCredentialModel basicCredential;
	@Mock(lenient = true)
	private MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter;
	@Mock(lenient = true)
	private ClientHttpRequestInterceptor interceptor;
	@Mock(lenient = true)
	private DestinationRestTemplateCache destinationRestTemplateCache;

	List<ClientHttpRequestInterceptor> interceptors;
	List<HttpMessageConverter<Object>> messageConverters;

	@Before
	public void setup()
	{
		lenient().when(consumedDestination.getUrl()).thenReturn(DESTINATION_URL);

		lenient().when(consumedDestination.getCredential()).thenReturn(basicCredential);
		lenient().when(basicCredential.getUsername()).thenReturn(USERNAME);
		lenient().when(basicCredential.getPassword()).thenReturn(PASS);

		messageConverters = Collections.singletonList(mappingJackson2HttpMessageConverter);
		interceptors = Collections.singletonList(interceptor);
		basicRestTemplateCreator.setMessageConverters(messageConverters);
		basicRestTemplateCreator.setRequestInterceptors(interceptors);
		basicRestTemplateCreator.setCache(destinationRestTemplateCache);
	}

	@Test
	public void shouldCreateRestTemplate()
	{
		verifyRestTemplateCreatedCorrectly(basicRestTemplateCreator.create(consumedDestination));
	}

	@Test
	public void shouldUseCachedRestTemplate()
	{
		final RestTemplate restTemplate = mock(RestTemplate.class);
		lenient().when(destinationRestTemplateCache.get(any())).thenReturn(restTemplate);

		final ConsumedDestinationModel destination = mock(ConsumedDestinationModel.class);
		lenient().when(destination.getCredential()).thenReturn(mock(BasicCredentialModel.class));

		final RestTemplate actualRestTemplate = (RestTemplate) basicRestTemplateCreator.create(destination);

		assertThat(actualRestTemplate).isEqualTo(restTemplate);
	}

	@Test
	public void shouldThrowUnsupportedRestTemplateException()
	{
		lenient().when(consumedDestination.getCredential()).thenReturn(oAuthCredential);

		assertThatThrownBy(() -> basicRestTemplateCreator.create(consumedDestination))
				.isInstanceOf(UnsupportedRestTemplateException.class);
	}

	@Test
	public void shouldOnlyHaveBasicAuthorizationInterceptor()
	{
		basicRestTemplateCreator.setRequestInterceptors(null);
		final RestTemplate restTemplate = (RestTemplate) basicRestTemplateCreator.create(consumedDestination);
		assertThat(restTemplate.getInterceptors().size()).isGreaterThanOrEqualTo(1);
		assertThat(restTemplate.getInterceptors().stream().anyMatch(i -> i instanceof BasicAuthenticationInterceptor)).isTrue();
	}

	@Test
	public void messageConvertersIsEmptyWhenSetWithNull()
	{
		basicRestTemplateCreator.setMessageConverters(null);
		assertThat(basicRestTemplateCreator.getMessageConverters()).isEmpty();
	}

	@Test
	public void setMessageConvertersDoesNotLeakReferences()
	{
		final List<HttpMessageConverter<Object>> converters = new ArrayList<>();
		converters.add(mappingJackson2HttpMessageConverter);

		basicRestTemplateCreator.setMessageConverters(converters);
		converters.clear();

		assertThat(basicRestTemplateCreator.getMessageConverters()).isNotEmpty();
	}

	@Test
	public void getMessageConvertersDoesNotLeakReferences()
	{
		final List<HttpMessageConverter<Object>> copy = basicRestTemplateCreator.getMessageConverters();
		copy.clear();

		assertThat(basicRestTemplateCreator.getMessageConverters()).isNotEmpty();
	}

	@Test
	public void requestInterceptorsIsEmptyWhenSetWithNull()
	{
		basicRestTemplateCreator.setRequestInterceptors(null);
		assertThat(basicRestTemplateCreator.getRequestInterceptors()).isEmpty();
	}

	@Test
	public void setRequestInterceptorsDoesNotLeakReference()
	{
		final List<ClientHttpRequestInterceptor> interceptors = new ArrayList<>();
		interceptors.add(interceptor);

		basicRestTemplateCreator.setRequestInterceptors(interceptors);
		interceptors.clear();

		assertThat(basicRestTemplateCreator.getRequestInterceptors()).isNotEmpty();
	}

	@Test
	public void getRequestInterceptorsDoesNotLeakReferences()
	{
		final List<ClientHttpRequestInterceptor> copy = basicRestTemplateCreator.getRequestInterceptors();
		copy.clear();

		assertThat(basicRestTemplateCreator.getRequestInterceptors()).isNotEmpty();
	}

	private void verifyRestTemplateCreatedCorrectly(final RestOperations restOperations)
	{
		assertThat(restOperations).isExactlyInstanceOf(RestTemplate.class);

		final RestTemplate restTemplate = (RestTemplate) restOperations;

		assertThat(restTemplate.getInterceptors()).isNotEmpty();
		assertThat(restTemplate.getInterceptors()).hasAtLeastOneElementOfType(BasicAuthenticationInterceptor.class);

		assertThat(restTemplate.getInterceptors().stream().anyMatch(i -> i instanceof BasicAuthenticationInterceptor)).isTrue();
		assertThat(restTemplate.getMessageConverters()).contains(mappingJackson2HttpMessageConverter);
		assertThat(restTemplate.getInterceptors()).contains(interceptor);
		assertThat(restTemplate.getErrorHandler()).isInstanceOf(DefaultResponseErrorHandler.class);
	}

}