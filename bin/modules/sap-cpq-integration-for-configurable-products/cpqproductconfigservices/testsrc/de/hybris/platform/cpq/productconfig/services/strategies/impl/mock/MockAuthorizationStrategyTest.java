/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cpq.productconfig.services.strategies.impl.mock;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.cpq.productconfig.services.data.AuthorizationData;
import de.hybris.platform.cpq.productconfig.services.data.CpqCredentialsData;

import org.junit.Test;


/**
 * Unit test for {@link MockAuthorizationStrategy}
 */
@UnitTest
public class MockAuthorizationStrategyTest
{
	private static final String OWNER_ID = "owner id";
	MockAuthorizationStrategy classUnderTest = new MockAuthorizationStrategy();
	private final CpqCredentialsData credentials = new CpqCredentialsData();

	@Test
	public void testGetAuthorizationDataTokenForAdmin()
	{
		final AuthorizationData authorizationData = classUnderTest.getAuthorizationData(classUnderTest.getCpqCredentialsForAdmin());
		final String[] components = authorizationData.getAccessToken().split("\\|");
		assertEquals("Mock token should have 2 components", 2, components.length);
		assertEquals(MockAuthorizationStrategy.MOCK_ACCESS_TOKEN, components[0]);
		assertNotNull(MockAuthorizationStrategy.MOCK_ACCESS_TOKEN, components[1]);
	}

	@Test
	public void testGetAuthorizationDataTokenForClient()
	{
		final AuthorizationData authorizationData = classUnderTest
				.getAuthorizationData(classUnderTest.getCpqCredentialsForClient(OWNER_ID));
		final String[] components = authorizationData.getAccessToken().split("\\|");
		assertEquals("Mock token should have 3 components", 3, components.length);
		assertEquals(MockAuthorizationStrategy.MOCK_ACCESS_TOKEN, components[0]);
		assertEquals("ownerId=" + OWNER_ID, components[1]);
		assertNotNull(MockAuthorizationStrategy.MOCK_ACCESS_TOKEN, components[2]);
	}

	@Test
	public void testGetAuthorizationDataOwnerIdForAdmin()
	{
		final AuthorizationData authorizationData = classUnderTest.getAuthorizationData(classUnderTest.getCpqCredentialsForAdmin());
		assertNull(authorizationData.getOwnerId());
	}

	@Test
	public void testGetAuthorizationDataOwnerIdForClient()
	{
		final AuthorizationData authorizationData = classUnderTest
				.getAuthorizationData(classUnderTest.getCpqCredentialsForClient(OWNER_ID));
		assertEquals(OWNER_ID, authorizationData.getOwnerId());
	}

	@Test
	public void testGetAuthorizationDataExpiresAt()
	{
		final AuthorizationData authorizationData = classUnderTest.getAuthorizationData(classUnderTest.getCpqCredentialsForAdmin());
		assertTrue("Mock token should have a short validity",
				System.currentTimeMillis() + MockAuthorizationStrategy.MOCK_TOKEN_VALIDITY_MS >= authorizationData.getExpiresAt());
	}

	@Test
	public void testGetAuthorizationDataTwiceTokenNotEquals()
	{
		final String token1 = classUnderTest.getAuthorizationData(classUnderTest.getCpqCredentialsForAdmin()).getAccessToken();
		final String token2 = classUnderTest.getAuthorizationData(classUnderTest.getCpqCredentialsForAdmin()).getAccessToken();
		assertNotEquals(token1, token2);
	}

	@Test
	public void testGetAuthorizationDataEndpoint()
	{
		assertEquals(MockAuthorizationStrategy.MOCK_ENDPOINT_URL,
				classUnderTest.getAuthorizationData(classUnderTest.getCpqCredentialsForAdmin()).getServiceEndpointUrl());
		assertEquals(MockAuthorizationStrategy.MOCK_ENDPOINT_URL,
				classUnderTest.getAuthorizationData(classUnderTest.getCpqCredentialsForClient(OWNER_ID)).getServiceEndpointUrl());
	}

	@Test
	public void testGetCpqCredentials()
	{
		final CpqCredentialsData result = classUnderTest.getCpqCredentialsForAdmin();
		assertNotNull(result);
		assertNull(result.getOwnerId());

	}

	@Test
	public void testGetCpqCredentialsForClient()
	{
		final CpqCredentialsData result = classUnderTest.getCpqCredentialsForClient(OWNER_ID);
		assertNotNull(result);
		assertEquals(OWNER_ID, result.getOwnerId());
	}

	@Test
	public void testGetTokenExpirationBuffer()
	{
		assertEquals(MockAuthorizationStrategy.ONE_SECOND_IN_MS, classUnderTest.getTokenExpirationBuffer());
	}
}
