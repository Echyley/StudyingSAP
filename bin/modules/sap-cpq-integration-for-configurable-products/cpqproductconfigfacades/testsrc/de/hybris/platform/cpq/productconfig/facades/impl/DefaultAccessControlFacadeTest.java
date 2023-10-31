/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cpq.productconfig.facades.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.cpq.productconfig.facades.data.AccessControlData;
import de.hybris.platform.cpq.productconfig.services.AuthorizationService;
import de.hybris.platform.cpq.productconfig.services.BusinessContextService;
import de.hybris.platform.cpq.productconfig.services.data.AuthorizationData;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


/**
 * Unit test for {@link DefaultAccessControlFacade}
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultAccessControlFacadeTest
{
	private static final long TOKEN_EXPIRATION = 83756398475639876l;
	private static final String ACCESS_TOKEN = "access token";
	private static final String ENDPOINT_URL = "endpoint url";
	private static final String OWNER_ID = "owner id";

	@InjectMocks
	private DefaultAccessControlFacade classUnderTest;

	@Mock
	private BusinessContextService businessContextService;
	@Mock
	private AuthorizationService authorizationService;

	private final AuthorizationData authorizationData = new AuthorizationData();

	@Before
	public void setup()
	{
		when(businessContextService.getOwnerId()).thenReturn(OWNER_ID);
		authorizationData.setServiceEndpointUrl(ENDPOINT_URL);
		authorizationData.setAccessToken(ACCESS_TOKEN);
		authorizationData.setExpiresAt(TOKEN_EXPIRATION);
		authorizationData.setOwnerId(OWNER_ID);
		when(authorizationService.getAuthorizationDataForClient(OWNER_ID)).thenReturn(authorizationData);
	}


	@Test
	public void testPerformAccessControlForClient()
	{
		final AccessControlData result = classUnderTest.performAccessControlForClient();
		assertNotNull(result);
		assertEquals(ENDPOINT_URL, result.getServiceEndpointUrl());
		assertEquals(ACCESS_TOKEN, result.getAccessToken());
		assertEquals(TOKEN_EXPIRATION, result.getAccessTokenExpiration());
		assertEquals(OWNER_ID, result.getOwnerId());
		verify(businessContextService).sendBusinessContextToCPQ();
	}
}
