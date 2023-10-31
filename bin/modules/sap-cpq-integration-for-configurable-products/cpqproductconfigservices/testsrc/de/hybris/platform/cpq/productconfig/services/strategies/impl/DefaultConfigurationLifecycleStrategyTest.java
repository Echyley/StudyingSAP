/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cpq.productconfig.services.strategies.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.apiregistryservices.exceptions.CredentialException;
import de.hybris.platform.cpq.productconfig.services.BusinessContextService;
import de.hybris.platform.cpq.productconfig.services.client.CpqClient;
import de.hybris.platform.cpq.productconfig.services.client.CpqClientConstants;
import de.hybris.platform.cpq.productconfig.services.client.CpqClientUtil;
import de.hybris.platform.cpq.productconfig.services.data.ConfigurationCloneData;
import de.hybris.platform.cpq.productconfig.services.data.ConfigurationCloneRequest;
import de.hybris.platform.cpq.productconfig.services.data.ConfigurationCreateRequest;
import de.hybris.platform.cpq.productconfig.services.data.ConfigurationCreatedData;
import de.hybris.platform.cpq.productconfig.services.data.ConfigurationCreatedResponseData;
import de.hybris.platform.cpq.productconfig.services.data.ConfigurationPatchRequest;
import de.hybris.platform.cpq.productconfig.services.data.ConfigurationSummaryData;
import de.hybris.platform.cpq.productconfig.services.impl.ObservableTestHelperLifeCycle;
import de.hybris.platform.cpq.productconfig.services.strategies.CPQInteractionStrategy;

import java.util.Collections;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatcher;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.hybris.charon.RawResponse;
import com.hybris.charon.exp.NotFoundException;

import rx.Observable;


/**
 * Unit test for {@link DefaultConfigurationLifecycleStrategy}
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultConfigurationLifecycleStrategyTest
{
	private static final String PRODUCT_CODE = "product";
	private static final String CONFIG_ID = "eab2-5243";
	private static final String CONFIG_ID2 = "adsf-5244";
	private static final String CPQ_SESSION_ID = "cpqSession123";
	private static final String ACCESS_TOKEN = "625343";
	private static final String CLIENT_TOKEN = "123456";
	private static final String OWNER_ID = "user123";
	private static final String AUTHORIZATION_STRING = "Bearer " + ACCESS_TOKEN;
	private static final String CLIENT_AUTHORIZATION_STRING = "Bearer " + CLIENT_TOKEN;

	@InjectMocks
	private DefaultConfigurationLifecycleStrategy classUnderTest;

	@InjectMocks
	private ObservableTestHelperLifeCycle helper;

	@Mock
	private CpqClientUtil clientUtil;
	@Mock
	private CPQInteractionStrategy cpqInteractionStrategy;
	@Mock
	private CpqClient client;
	@Mock
	private BusinessContextService businessContextService;

	protected ConfigurationSummaryData configurationSummary = new ConfigurationSummaryData();
	protected ConfigurationCloneData configurationCloneData = new ConfigurationCloneData();
	protected ConfigurationCreatedResponseData configurationCreatedResponseData = new ConfigurationCreatedResponseData();
	protected ArgumentCaptor<ConfigurationCreateRequest> createRequestCaptor = ArgumentCaptor
			.forClass(ConfigurationCreateRequest.class);

	@Before
	public void initialize() throws CredentialException
	{
		when(businessContextService.getOwnerId()).thenReturn(OWNER_ID);
		when(cpqInteractionStrategy.getClient()).thenReturn(client);
		when(cpqInteractionStrategy.getAuthorizationString()).thenReturn(AUTHORIZATION_STRING);
		when(cpqInteractionStrategy.getClientAuthorizationString(OWNER_ID)).thenReturn(CLIENT_AUTHORIZATION_STRING);

		configurationCreatedResponseData.setConfigurationId(CONFIG_ID);
		final Observable<RawResponse<ConfigurationCreatedResponseData>> initObs = helper.mockResponse(
				configurationCreatedResponseData,
				CpqClientConstants.HTTP_STATUS_CREATED,
				Collections.singletonMap(CpqClientConstants.HTTP_HEADER_CPQ_SESSION_ID, CPQ_SESSION_ID));
		when(client.createConfiguration(eq(CLIENT_AUTHORIZATION_STRING), eq(true), any(ConfigurationCreateRequest.class)))
				.thenReturn(initObs);

		configurationCloneData.setConfigurationId(CONFIG_ID2);
		final Observable<RawResponse<ConfigurationCloneData>> cloneObs = helper.mockResponse(configurationCloneData,
				CpqClientConstants.HTTP_STATUS_CREATED);
		when(client.clone(eq(AUTHORIZATION_STRING), eq(true), eq(CONFIG_ID), any(ConfigurationCloneRequest.class)))
				.thenReturn(cloneObs);

		final Observable<RawResponse<Object>> deleteObs = helper.mockEmptyResponse(CpqClientConstants.HTTP_STATUS_NO_CONTENT);
		when(client.deleteConfiguration(AUTHORIZATION_STRING, CONFIG_ID)).thenReturn(deleteObs);

		final Observable<RawResponse<ConfigurationSummaryData>> summaryObs = helper.mockResponse(configurationSummary,
				CpqClientConstants.HTTP_STATUS_OK);
		when(client.getConfiguration(CLIENT_AUTHORIZATION_STRING, CONFIG_ID)).thenReturn(summaryObs);
	}


	@Test
	public void testCreateConfiguration()
	{
		assertEquals(CONFIG_ID, classUnderTest.createConfiguration(PRODUCT_CODE, OWNER_ID));
		verify(client).createConfiguration(eq(CLIENT_AUTHORIZATION_STRING), eq(true), createRequestCaptor.capture());
		assertEquals(PRODUCT_CODE, createRequestCaptor.getValue().getProductSystemId());
	}

	@Test
	public void testInitConfigurationt()
	{
		final ConfigurationCreatedData result = classUnderTest.initConfiguration(PRODUCT_CODE, OWNER_ID);
		assertEquals(CPQ_SESSION_ID, result.getSessionId());
		assertEquals(CONFIG_ID, result.getConfigId());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testInitConfigurationSessionId()
	{
		final Observable<RawResponse<ConfigurationCreatedResponseData>> initObs = helper
				.mockEmptyResponse(CpqClientConstants.HTTP_STATUS_CREATED);
		when(client.createConfiguration(eq(CLIENT_AUTHORIZATION_STRING), eq(true), any(ConfigurationCreateRequest.class)))
				.thenReturn(initObs);
		classUnderTest.initConfiguration(PRODUCT_CODE, OWNER_ID);
	}

	@Test
	public void testGetConfigurationSummary()
	{
		assertSame(configurationSummary, classUnderTest.getConfigurationSummary(CONFIG_ID));
	}

	@Test
	public void testDeleteConfiguration()
	{
		assertTrue(classUnderTest.deleteConfiguration(CONFIG_ID));
	}

	@Test
	public void testDeleteConfigurationNotExisting()
	{
		// CPQ returns a 404 in case configuration is not existing
		final Observable<RawResponse<Object>> deleteObs = helper.mockEmptyResponse(CpqClientConstants.HTTP_STATUS_NOT_FOUND);
		when(client.deleteConfiguration(AUTHORIZATION_STRING, CONFIG_ID)).thenReturn(deleteObs);
		final RawResponse<Object> rawResponse = clientUtil.toResponse(deleteObs);
		doThrow(new NotFoundException(CpqClientConstants.HTTP_STATUS_NOT_FOUND, "")).when(clientUtil).checkHTTPStatusCode("DELETE",
				CpqClientConstants.HTTP_STATUS_NO_CONTENT, rawResponse);
		assertFalse(classUnderTest.deleteConfiguration(CONFIG_ID));
	}

	@Test
	public void testCloneConfiguration()
	{
		final ArgumentCaptor<ConfigurationCloneRequest> cloneCaptor = ArgumentCaptor.forClass(ConfigurationCloneRequest.class);
		assertEquals(CONFIG_ID2, classUnderTest.cloneConfiguration(CONFIG_ID, true));
		verify(client).clone(eq(AUTHORIZATION_STRING), eq(true), eq(CONFIG_ID), cloneCaptor.capture());
		assertTrue(cloneCaptor.getValue().isIsPermanent());
	}

	@Test
	public void testCloneConfigurationNonPermanent()
	{
		final ArgumentCaptor<ConfigurationCloneRequest> cloneCaptor = ArgumentCaptor.forClass(ConfigurationCloneRequest.class);
		assertEquals(CONFIG_ID2, classUnderTest.cloneConfiguration(CONFIG_ID, false));
		verify(client).clone(eq(AUTHORIZATION_STRING), eq(true), eq(CONFIG_ID), cloneCaptor.capture());
		assertFalse(cloneCaptor.getValue().isIsPermanent());
	}

	@Test
	public void testMakeConfigurationPermanent()
	{
		classUnderTest.makeConfigurationPermanent(CONFIG_ID);
		final ArgumentMatcher<ConfigurationPatchRequest> isPernamentTrueMatcher = new ArgumentMatcherCPR<ConfigurationPatchRequest>();
		verify(client).makePermanent(eq(AUTHORIZATION_STRING), eq(CONFIG_ID), argThat(isPernamentTrueMatcher));
	}


	public class ArgumentMatcherCPR<ConfigurationPatchRequest> implements ArgumentMatcher{
		@Override
		public boolean matches(final Object argument)
		{
			return ((de.hybris.platform.cpq.productconfig.services.data.ConfigurationPatchRequest) argument).isIsPermanent();
		}
	}

}
