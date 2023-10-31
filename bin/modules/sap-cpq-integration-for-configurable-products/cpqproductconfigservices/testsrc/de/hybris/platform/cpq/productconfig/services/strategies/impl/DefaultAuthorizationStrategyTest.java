/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cpq.productconfig.services.strategies.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.apiregistryservices.exceptions.CredentialException;
import de.hybris.platform.apiregistryservices.model.BasicCredentialModel;
import de.hybris.platform.apiregistryservices.model.ConsumedDestinationModel;
import de.hybris.platform.apiregistryservices.model.ConsumedOAuthCredentialModel;
import de.hybris.platform.apiregistryservices.services.ApiRegistryClientService;
import de.hybris.platform.apiregistryservices.strategies.ConsumedDestinationLocatorStrategy;
import de.hybris.platform.cpq.productconfig.services.EngineDeterminationService;
import de.hybris.platform.cpq.productconfig.services.client.CpqClient;
import de.hybris.platform.cpq.productconfig.services.client.CpqClientConstants;
import de.hybris.platform.cpq.productconfig.services.client.CpqClientUtil;
import de.hybris.platform.cpq.productconfig.services.data.AuthorizationData;
import de.hybris.platform.cpq.productconfig.services.data.CpqCredentialsData;
import de.hybris.platform.cpq.productconfig.services.data.TokenResponseData;
import de.hybris.platform.cpq.productconfig.services.impl.ObservableTestHelper;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import com.hybris.charon.RawResponse;

import rx.Observable;


/**
 * Unit test for {@link DefaultAuthorizationStrategy}
 */
@RunWith(MockitoJUnitRunner.class)
@UnitTest
public class DefaultAuthorizationStrategyTest
{
	private static final int ONE_DAY_IN_SEC = 60 * 60 * 24;
	private static final String OWNER_ID = "owner id";
	private static final String OAUTH_URL = "https://huhu";
	private static final String CLIENT_SECRET = "secret";
	private static final String CLIENT_ID = "clientId";
	private static final String SERVICE_URL = "https://cpq";
	private static final String USER_NAME = "guestuser";
	private static final String PASSWORD = "pw";
	private static final String DOMAIN = "teamtiger";
	private static final String SCOPE = "tiger scope";
	private static final String SCOPE_UI = "tiger ui scope";
	private static final String ACCESS_TOKEN = "7263534";

	@InjectMocks
	private DefaultAuthorizationStrategy classUnderTest;
	
	private DefaultAuthorizationStrategy classUnderTestSpy; 
	
	@InjectMocks
	private ObservableTestHelper helper;

	@Mock
	private ConsumedDestinationLocatorStrategy consumedDestinationLocatorStrategy;
	@Mock
	private ConsumedDestinationModel consumedDestination;
	@Mock
	private ConsumedOAuthCredentialModel consumedOauthCredentials;
	@Mock
	private BasicCredentialModel basicCredentials;
	@Mock
	private ConsumedDestinationModel consumedDestinationWithBasicAuthCredentials;
	@Mock
	private ApiRegistryClientService apiRegistryClientService;
	@Mock
	private CpqClient cpqClient;
	@Mock
	private EngineDeterminationService engineDeterminationService;
	@Mock
	private CpqClientUtil clientUtil;

	private final Map<String, String> additionalBasicAuthProps = new HashMap<>();
	private CpqCredentialsData cpqCredentials;
	private final TokenResponseData tokenResponse = new TokenResponseData();
	

	@Before
	public void initialize() throws CredentialException
	{
		tokenResponse.setAccess_token(ACCESS_TOKEN);
		tokenResponse.setExpires(ONE_DAY_IN_SEC);
		
		cpqCredentials = new CpqCredentialsData();
		cpqCredentials.setDomain(DOMAIN);
		cpqCredentials.setUsername(USER_NAME);
		cpqCredentials.setPassword(PASSWORD);
		cpqCredentials.setScope(SCOPE);
		cpqCredentials.setOwnerId(OWNER_ID);

		classUnderTestSpy = Mockito.spy(classUnderTest);

		final Observable<RawResponse<TokenResponseData>> tokenObs = helper.mockResponse(tokenResponse,
				CpqClientConstants.HTTP_STATUS_OK);

		when(consumedDestinationLocatorStrategy.lookup(CpqClient.class.getSimpleName()))
				.thenReturn(consumedDestinationWithBasicAuthCredentials);
		when(consumedDestination.getCredential()).thenReturn(consumedOauthCredentials);
		when(consumedDestinationWithBasicAuthCredentials.getCredential()).thenReturn(basicCredentials);
		when(consumedDestinationWithBasicAuthCredentials.getAdditionalProperties()).thenReturn(additionalBasicAuthProps);
		when(consumedDestinationWithBasicAuthCredentials.getUrl()).thenReturn(SERVICE_URL);
		when(basicCredentials.getUsername()).thenReturn(USER_NAME);
		when(basicCredentials.getPassword()).thenReturn(PASSWORD);
		when(apiRegistryClientService.lookupClient(CpqClient.class)).thenReturn(cpqClient);
		Mockito.doReturn(tokenResponse).when(classUnderTestSpy).getOauth2Token(Mockito.any());
		

		additionalBasicAuthProps.put(DefaultAuthorizationStrategy.ADDITIONAL_ATTRIBUTE_DOMAIN, DOMAIN);
		additionalBasicAuthProps.put(DefaultAuthorizationStrategy.ADDITIONAL_ATTRIBUTE_SCOPE, SCOPE);
		additionalBasicAuthProps.put(DefaultAuthorizationStrategy.ADDITIONAL_ATTRIBUTE_UI_SCOPE, SCOPE_UI);
	}

	@Test
	public void testConsumedDestinationLocatorStrategy()
	{
		assertEquals(consumedDestinationLocatorStrategy, classUnderTest.getConsumedDestinationLocatorStrategy());
	}

	@Test
	public void testGetAuthorizationData()
	{
		
		final AuthorizationData sessionAttributes = classUnderTestSpy.getAuthorizationData(cpqCredentials);
		assertNotNull(sessionAttributes);
		assertEquals(ACCESS_TOKEN, sessionAttributes.getAccessToken());
	}

	@Test
	public void testGetAuthorizationDataExpires()
	{
		final AuthorizationData sessionAttributes = classUnderTestSpy.getAuthorizationData(cpqCredentials);
		assertNotNull(sessionAttributes);
		final long expectedValue = System.currentTimeMillis() + (ONE_DAY_IN_SEC * 1000);
		// note that in productive code we use System.currentTimeMillis(), so we can not now the exact value, as it depends on test execution time
		final int delta = Long.compare(expectedValue, sessionAttributes.getExpiresAt());
		assertTrue("expected expiration data more than 1 second different from the actual one, " + delta + " ms",
				delta > -1000 && delta < 1000);
	}

	@Test(expected = IllegalStateException.class)
	public void testAuthorizationDataInvalidCredentials() throws CredentialException
	{
		when(apiRegistryClientService.lookupClient(CpqClient.class)).thenThrow(CredentialException.class);
		classUnderTest.getAuthorizationData(cpqCredentials);
	}

	@Test
	public void testConcatenateCredentialsForAdmin()
	{
		cpqCredentials.setOwnerId(null);
		final String credentialsAsString = classUnderTest.concatenateCredentials(cpqCredentials);
		assertNotNull(credentialsAsString);
		assertTrue(credentialsAsString.contains(PASSWORD));
		assertTrue(credentialsAsString.contains(USER_NAME));
		assertTrue(credentialsAsString.contains(DOMAIN));
		assertTrue(credentialsAsString.contains(SCOPE));
	}

	@Test
	public void testConcatenateCredentialsForClient()
	{
		cpqCredentials.setScope(null);
		final String credentialsAsString = classUnderTest.concatenateCredentials(cpqCredentials);
		assertNotNull(credentialsAsString);
		assertTrue(credentialsAsString.contains(PASSWORD));
		assertTrue(credentialsAsString.contains(USER_NAME));
		assertTrue(credentialsAsString.contains(DOMAIN));
		assertTrue(credentialsAsString.contains(OWNER_ID));
	}

	@Test
	public void testGetServiceEndpointOAuth()
	{
		final String serviceEndpoint = classUnderTest.getServiceEndpointOAuth();
		assertEquals(SERVICE_URL, serviceEndpoint);
	}


	@Test
	public void testGetCPQBasicAuthCredentials()
	{
		final CpqCredentialsData cpqCredentials = classUnderTest.getCPQBasicAuthCredentials();
		assertNotNull(cpqCredentials);
		assertEquals(USER_NAME, cpqCredentials.getUsername());
		assertEquals(PASSWORD, cpqCredentials.getPassword());
		assertEquals(DOMAIN, cpqCredentials.getDomain());
	}

	@Test
	public void testRetrieveAdminScope()
	{
		assertEquals(SCOPE, classUnderTest.retrieveAdminScope());
	}

	@Test
	public void testRetrieveClientScope()
	{
		assertEquals(SCOPE_UI, classUnderTest.retrieveClientScope());
	}


	@Test(expected = IllegalStateException.class)
	public void testAddBasicAuthAttributesFromCredentialsWrongCredentials()
	{
		final CpqCredentialsData credentials = new CpqCredentialsData();
		classUnderTest.addBasicAuthAttributesFromCredentials(consumedDestination, credentials);
	}

	@Test
	public void testAddBasicAuthAttributesFromDestination()
	{
		final CpqCredentialsData credentials = new CpqCredentialsData();
		classUnderTest.addBasicAuthAttributesFromDestination(consumedDestinationWithBasicAuthCredentials, credentials);
		assertEquals(DOMAIN, credentials.getDomain());
	}

	@Test(expected = NullPointerException.class)
	public void testAddBasicAuthAttributesFromDestinationNoAttributes()
	{
		Mockito.when(consumedDestinationWithBasicAuthCredentials.getAdditionalProperties()).thenReturn(null);
		final CpqCredentialsData credentials = new CpqCredentialsData();
		classUnderTest.addBasicAuthAttributesFromDestination(consumedDestinationWithBasicAuthCredentials, credentials);
	}

	@Test(expected = NullPointerException.class)
	public void testAddBasicAuthAttributesFromDestinationWrongAttributes()
	{
		additionalBasicAuthProps.clear();
		final CpqCredentialsData credentials = new CpqCredentialsData();
		classUnderTest.addBasicAuthAttributesFromDestination(consumedDestinationWithBasicAuthCredentials, credentials);
	}

	@Test(expected = NullPointerException.class)
	public void testAddBasicAuthAttributesFromDestinationNoDestination()
	{
		classUnderTest.addBasicAuthAttributesFromDestination(null, cpqCredentials);
	}

	@Test
	public void testRegistryService()
	{
		assertEquals(apiRegistryClientService, classUnderTest.getApiRegistryClientService());
	}

	@Test
	public void testGetCpqCredentialsForAdmin()
	{
		final CpqCredentialsData result = classUnderTest.getCpqCredentialsForAdmin();
		assertEquals(DOMAIN, result.getDomain());
		assertEquals(SCOPE, result.getScope());
		assertEquals(USER_NAME, result.getUsername());
		assertEquals(PASSWORD, result.getPassword());
		assertNull(result.getOwnerId());
	}

	@Test
	public void testGetCpqCredentialsForClient()
	{
		final CpqCredentialsData result = classUnderTest.getCpqCredentialsForClient(OWNER_ID);
		assertEquals(DOMAIN, result.getDomain());
		assertEquals(SCOPE_UI, result.getScope());
		assertEquals(USER_NAME, result.getUsername());
		assertEquals(PASSWORD, result.getPassword());
		assertEquals(OWNER_ID, result.getOwnerId());
	}

	@Test
	public void testGetTokenExpirationBuffer()
	{
		assertEquals(DefaultAuthorizationStrategy.ONE_MINUTE_IN_MS, classUnderTest.getTokenExpirationBuffer());
	}

}
