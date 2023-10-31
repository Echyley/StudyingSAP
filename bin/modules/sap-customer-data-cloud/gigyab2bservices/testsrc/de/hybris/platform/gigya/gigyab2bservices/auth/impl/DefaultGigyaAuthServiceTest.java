/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.gigya.gigyab2bservices.auth.impl;

import java.util.Collections;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.core.model.user.UserGroupModel;
import de.hybris.platform.gigya.gigyab2bservices.data.GigyaActionsAccessData;
import de.hybris.platform.gigya.gigyab2bservices.data.GigyaActionsData;
import de.hybris.platform.gigya.gigyab2bservices.data.GigyaAssetTemplatesData;
import de.hybris.platform.gigya.gigyab2bservices.data.GigyaAssetsData;
import de.hybris.platform.gigya.gigyab2bservices.data.GigyaAuthData;
import de.hybris.platform.gigya.gigyab2bservices.data.GigyaAuthRequestData;
import de.hybris.platform.gigya.gigyab2bservices.data.GigyaFunctionalRolesData;
import de.hybris.platform.gigya.gigyab2bservices.token.GigyaTokenGenerator;
import de.hybris.platform.gigya.gigyaservices.model.GigyaConfigModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.site.BaseSiteService;


/**
 * Test class for DefaultGigyaAuthService
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultGigyaAuthServiceTest
{

	private static final String TOKEN = "token";
	private static final String SAMPLE_GROUP = "sample-group";
	private static final String SAMPLE_AUTH_URL = "https://url.com";
	private static final String SAMPLE_AUTH_REQUEST_KEY = "auth-req-key";
	private static final String SAMPLE_AUTH_REQUEST_SECRET = "auth-req-secret";

	@InjectMocks
	private final DefaultGigyaAuthService gigyaAuthService = new DefaultGigyaAuthService()
	{

		@Override
		protected HttpEntity createHttpEntity(final CustomerModel customer, final HttpHeaders headers)
		{
			return httpEntity;
		}

	};

	@Mock
	private HttpEntity httpEntity;

	@Mock
	private Converter<CustomerModel, GigyaAuthRequestData> gigyaAuthRequestConverter;

	@Mock
	private BaseSiteService baseSiteService;

	@Mock
	private GigyaTokenGenerator gigyaTokenGenerator;

	@Mock
	private UserService userService;

	@Mock
	private RestTemplate restTemplate;

	@Mock
	private BaseSiteModel baseSite;

	@Mock
	private GigyaConfigModel gigyaConfig;

	@Mock
	private GigyaAuthRequestData requestData;

	@Mock
	private B2BCustomerModel customer;

	@Mock
	private ResponseEntity response;

	@Mock
	private GigyaAuthData gigyaAuthData;

	@Mock
	private GigyaAssetsData assets;

	@Mock
	private GigyaAssetTemplatesData assetTemplates;

	@Mock
	private GigyaFunctionalRolesData functionalRoles;

	@Mock
	private GigyaActionsData actionsData;

	@Mock
	private GigyaActionsAccessData accessData;

	@Mock
	private UserGroupModel userGroup;

	@Test
	public void testAssignAuthorisationsToCustomerWhenAuthorizationExists()
	{
		Mockito.lenient().when(baseSiteService.getCurrentBaseSite()).thenReturn(baseSite);
		Mockito.lenient().when(baseSite.getGigyaConfig()).thenReturn(gigyaConfig);
		Mockito.lenient().when(gigyaConfig.getAuthorizationUrl()).thenReturn(SAMPLE_AUTH_URL);
		Mockito.lenient().when(gigyaConfig.getAuthRequestKey()).thenReturn(SAMPLE_AUTH_REQUEST_KEY);
		Mockito.lenient().when(gigyaConfig.getAuthRequestSecret()).thenReturn(SAMPLE_AUTH_REQUEST_SECRET);

		Mockito.lenient().when(gigyaTokenGenerator.generate(ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.anyInt())).thenReturn(TOKEN);
		Mockito.lenient().when(gigyaAuthRequestConverter.convert(customer)).thenReturn(requestData);

		Mockito.lenient().when(restTemplate.exchange(gigyaConfig.getAuthorizationUrl(), HttpMethod.POST, httpEntity, GigyaAuthData.class))
				.thenReturn(response);
		Mockito.lenient().when(response.getBody()).thenReturn(gigyaAuthData);
		Mockito.lenient().when(gigyaAuthData.getAssets()).thenReturn(assets);
		Mockito.lenient().when(assets.getAssetTemplates()).thenReturn(assetTemplates);
		Mockito.lenient().when(assetTemplates.getCommerceFunctionalRoles()).thenReturn(functionalRoles);
		Mockito.lenient().when(functionalRoles.getActions()).thenReturn(actionsData);
		Mockito.lenient().when(actionsData.getAccessList()).thenReturn(Collections.singletonList(accessData));
		Mockito.lenient().when(userService.getUserGroupForUID(SAMPLE_GROUP)).thenReturn(userGroup);

		Mockito.lenient().when(accessData.getAttributes())
				.thenReturn(Collections.singletonMap("Name", Collections.singletonList(SAMPLE_GROUP)));

		gigyaAuthService.assignAuthorisationsToCustomer(customer);

		Mockito.verify(customer).setGroups(ArgumentMatchers.anySet());
	}

	@Test
	public void testAssignAuthorisationsToCustomerWhenFetchingAuthorizationFails()
	{
		Mockito.lenient().when(baseSiteService.getCurrentBaseSite()).thenReturn(baseSite);
		Mockito.lenient().when(baseSite.getGigyaConfig()).thenReturn(gigyaConfig);
		Mockito.lenient().when(gigyaConfig.getAuthorizationUrl()).thenReturn(SAMPLE_AUTH_URL);
		Mockito.lenient().when(gigyaConfig.getAuthRequestKey()).thenReturn(SAMPLE_AUTH_REQUEST_KEY);
		Mockito.lenient().when(gigyaConfig.getAuthRequestSecret()).thenReturn(SAMPLE_AUTH_REQUEST_SECRET);

		Mockito.lenient().when(gigyaTokenGenerator.generate(ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.anyInt())).thenReturn(TOKEN);
		Mockito.lenient().when(gigyaAuthRequestConverter.convert(customer)).thenReturn(requestData);
		Mockito.lenient().when(restTemplate.exchange(gigyaConfig.getAuthorizationUrl(), HttpMethod.POST, httpEntity, GigyaAuthData.class))
				.thenThrow(RestClientException.class);

		Mockito.lenient().when(accessData.getAttributes())
				.thenReturn(Collections.singletonMap("Name", Collections.singletonList(SAMPLE_GROUP)));

		gigyaAuthService.assignAuthorisationsToCustomer(customer);
		//verify that setGroups is not called and the method exits gracefully
		Mockito.verify(customer, Mockito.never()).setGroups(ArgumentMatchers.anySet());
	}

	@Test
	public void testRemoveAuthorisationsOfCustomerWhenCustomerExists()
	{
		Mockito.lenient().when(customer.getGroups()).thenReturn(Collections.singleton(userGroup));
		Mockito.lenient().when(userGroup.getUid()).thenReturn("b2badmingroup");

		gigyaAuthService.removeAuthorisationsOfCustomer(customer);

		Mockito.verify(customer).setGroups(Collections.emptySet());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testRemoveAuthorisationsOfCustomerWhenCustomerDoesntExists()
	{
		gigyaAuthService.removeAuthorisationsOfCustomer(null);
	}
}