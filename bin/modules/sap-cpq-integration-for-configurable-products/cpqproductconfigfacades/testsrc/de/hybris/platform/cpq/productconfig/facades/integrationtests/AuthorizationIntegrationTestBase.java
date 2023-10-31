/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cpq.productconfig.facades.integrationtests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import de.hybris.platform.cpq.productconfig.services.AuthorizationService;
import de.hybris.platform.cpq.productconfig.services.data.AuthorizationData;

import javax.annotation.Resource;

import org.junit.Test;


public abstract class AuthorizationIntegrationTestBase extends BaseIntegrationTest
{
	private static final int ONE_MINUTE = 1000 * 60;

	@Resource(name = "cpqProductConfigAuthorizationService")
	protected AuthorizationService authorizationService;

	@Test
	public void testGetCPQOauthTokenAdmin()
	{
		checkTokenData(authorizationService.getAuthorizationDataForAdmin(), true);
	}

	@Test
	public void testGetCPQOauthTokenAdminCached()
	{
		final AuthorizationData firstToken = authorizationService.getAuthorizationDataForAdmin();
		final AuthorizationData secondToken = authorizationService.getAuthorizationDataForAdmin();
		assertEquals(firstToken.getAccessToken(), secondToken.getAccessToken());
	}

	@Test
	public void testGetCPQOauthTokenClient()
	{
		checkTokenData(authorizationService.getAuthorizationDataForClient("0001010512"), false);
	}

	@Test
	public void testGetCPQOauthTokenClientNotCached()
	{
		final AuthorizationData firstToken = authorizationService.getAuthorizationDataForClient("0001010512");
		final AuthorizationData secondToken = authorizationService.getAuthorizationDataForClient("0001010512");
		assertNotEquals(firstToken.getAccessToken(), secondToken.getAccessToken());
	}

	protected void checkTokenData(final AuthorizationData tokenData, final boolean isAdmin)
	{
		assertNotNull(tokenData);
		assertNotNull(tokenData.getAccessToken());
		assertNotEquals(0,tokenData.getExpiresAt());
		final long expiresAt = tokenData.getExpiresAt();
		final long actualDate = System.currentTimeMillis() + getMinTokenLifeTime();
		assertTrue(String.format("expireAt (%s) shall be greater than actual date (%s)!", expiresAt, actualDate),
				expiresAt > actualDate);
		if (isAdmin)
		{
			assertNull("admin token shall not have an owner", tokenData.getOwnerId());
		}
		else
		{
			assertNotNull("client token shall have an owner", tokenData.getOwnerId());
		}
	}

	protected long getMinTokenLifeTime()
	{
		return ONE_MINUTE;
	}

}
