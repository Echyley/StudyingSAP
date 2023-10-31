/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cpq.productconfig.facades.integrationtests.mock;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.cpq.productconfig.facades.integrationtests.AuthorizationIntegrationTestBase;
import de.hybris.platform.cpq.productconfig.services.data.AuthorizationData;
import de.hybris.platform.cpq.productconfig.services.strategies.impl.mock.MockAuthorizationStrategy;

import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.awaitility.Awaitility;
import org.junit.Test;


/**
 * Integration test for Authorization Mock Integration
 */
@IntegrationTest
public class AuthorizationMockIntegrationTest extends AuthorizationIntegrationTestBase
{

	private static Logger LOG = Logger.getLogger(AuthorizationMockIntegrationTest.class);

	@Override
	protected void checkTokenData(final AuthorizationData tokenData, final boolean isAdmin)
	{
		super.checkTokenData(tokenData, isAdmin);
		assertTrue("expecting mock token, not real token.",
				tokenData.getAccessToken().startsWith(MockAuthorizationStrategy.MOCK_ACCESS_TOKEN));
	}

	/**
	 * CPQ Tokens are long lived, for Mock we use explicitly short lived tokens so we can test renewal
	 *
	 * @throws InterruptedException
	 */
	@Test
	public synchronized void testAdminTokenAutomaticallyRenewdAfterExpirtaion() throws InterruptedException
	{
		assumeTrueAndLog("need short lived mock tokens for this test; < one minute",
				MockAuthorizationStrategy.MOCK_TOKEN_VALIDITY_MS < 60 * 1000, LOG);
		final String token1 = cpqInteractionStrategy.getAuthorizationString();
		Awaitility.with().pollDelay(MockAuthorizationStrategy.MOCK_TOKEN_VALIDITY_MS / 2, TimeUnit.MILLISECONDS).await()
				.until(() -> token1 != null);
		final String token2 = cpqInteractionStrategy.getAuthorizationString();
		Awaitility.with().pollDelay(MockAuthorizationStrategy.MOCK_TOKEN_VALIDITY_MS / 2, TimeUnit.MILLISECONDS).await()
				.until(() -> token2 != null);
		final String token3 = cpqInteractionStrategy.getAuthorizationString();
		assertEquals("Token should match, as the first token has not yet expired, so we should get the same token.", token1,
				token2);
		assertNotEquals("Token should differ, as the first token should have already been expired, so that a new token is fecthed.",
				token2, token3);
	}


	@Override
	protected long getMinTokenLifeTime()
	{
		return MockAuthorizationStrategy.MOCK_TOKEN_VALIDITY_MS / 2;
	}
}
