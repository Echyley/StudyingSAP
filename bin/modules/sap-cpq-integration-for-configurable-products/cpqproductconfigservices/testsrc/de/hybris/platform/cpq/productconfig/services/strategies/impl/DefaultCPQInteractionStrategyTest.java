/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cpq.productconfig.services.strategies.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.apiregistryservices.exceptions.CredentialException;
import de.hybris.platform.apiregistryservices.services.ApiRegistryClientService;
import de.hybris.platform.cpq.productconfig.services.AuthorizationService;
import de.hybris.platform.cpq.productconfig.services.client.CpqClient;
import de.hybris.platform.cpq.productconfig.services.data.AuthorizationData;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;


/**
 * Unit test for {@link DefaultCPQInteractionStrategy}
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultCPQInteractionStrategyTest
{
	private static final String ACCESS_TOKEN = "625343";
	private static final String CLIENT_TOKEN = "123456";
	private static final String OWNER_ID = "user123";
	private static final String AUTHORIZATION_STRING = "Bearer " + ACCESS_TOKEN;
	private static final String CLIENT_AUTHORIZATION_STRING = "Bearer " + CLIENT_TOKEN;

	@Mock
	private AuthorizationService authorizationService;
	@Mock
	private ApiRegistryClientService apiRegistryClientService;
	@Mock
	private CpqClient client;

	@InjectMocks
	private DefaultCPQInteractionStrategy classUnderTest;
	private final AuthorizationData authorizationData = new AuthorizationData();
	private final AuthorizationData clientAuthorizationData = new AuthorizationData();

	@Before
	public void setup() throws CredentialException
	{
		authorizationData.setAccessToken(ACCESS_TOKEN);
		clientAuthorizationData.setAccessToken(CLIENT_TOKEN);
		when(apiRegistryClientService.lookupClient(CpqClient.class)).thenReturn(client);
		when(authorizationService.getAuthorizationDataForAdmin()).thenReturn(authorizationData);
		when(authorizationService.getAuthorizationDataForClient(OWNER_ID)).thenReturn(clientAuthorizationData);
	}

	@Test
	public void testApiRegistryClientService()
	{
		assertEquals(classUnderTest.getApiRegistryClientService(), apiRegistryClientService);
	}

	@Test
	public void testAuthorizationService()
	{
		assertEquals(classUnderTest.getAuthorizationService(), authorizationService);
	}


	@Test
	public void testGetAuthorizationString()
	{
		assertEquals(AUTHORIZATION_STRING, classUnderTest.getAuthorizationString());
	}

	@Test
	public void testGetClientAuthorizationString()
	{
		assertEquals(CLIENT_AUTHORIZATION_STRING, classUnderTest.getClientAuthorizationString(OWNER_ID));
	}

	@Test
	public void testGetClient()
	{
		final CpqClient result = classUnderTest.getClient();
		assertNotNull(result);
		assertEquals(client, result);
	}

	@Test(expected = IllegalStateException.class)
	public void testCannotGetClient() throws CredentialException
	{
		Mockito.when(apiRegistryClientService.lookupClient(CpqClient.class)).thenThrow(CredentialException.class);
		classUnderTest.getClient();
	}

}
