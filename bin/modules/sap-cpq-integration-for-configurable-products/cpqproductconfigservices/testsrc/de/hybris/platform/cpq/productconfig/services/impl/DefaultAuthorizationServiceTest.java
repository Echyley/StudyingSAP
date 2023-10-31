/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cpq.productconfig.services.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.apiregistryservices.exceptions.CredentialException;
import de.hybris.platform.cpq.productconfig.services.CacheAccessService;
import de.hybris.platform.cpq.productconfig.services.CacheKeyService;
import de.hybris.platform.cpq.productconfig.services.StrategyDeterminationService;
import de.hybris.platform.cpq.productconfig.services.cache.DefaultCacheKey;
import de.hybris.platform.cpq.productconfig.services.data.AuthorizationData;
import de.hybris.platform.cpq.productconfig.services.data.CpqCredentialsData;
import de.hybris.platform.cpq.productconfig.services.strategies.AuthorizationStrategy;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


/**
 * Unit test for {@link DefaultAuthorizationService}
 */
@RunWith(MockitoJUnitRunner.class)
@UnitTest
public class DefaultAuthorizationServiceTest
{
	private static final int HOUR_IN_MS = 60 * 60 * 1000;
	private static final String ACCESS_TOKEN = "7263534";
	private static final String OWNER_ID = "owner id";
	private static final String NEXT_ACCESS_TOKEN = "346746";
	private static final long ONE_SECOND_IN_MS = 1000l;

	@InjectMocks
	private DefaultAuthorizationService classUnderTest;

	@Mock
	private CacheAccessService<DefaultCacheKey, AuthorizationData> cacheAccessService;
	@Mock
	private CacheKeyService cacheKeyService;
	@Mock
	private AuthorizationStrategy authorizationStrategy;
	@Mock
	private StrategyDeterminationService<AuthorizationStrategy> strategyDeterminationService;
	@Mock
	private DefaultCacheKey cacheKey;

	private final CpqCredentialsData cpqCredentialsForAdmin = new CpqCredentialsData();
	private final CpqCredentialsData cpqCredentialsForClient = new CpqCredentialsData();
	private final AuthorizationData newAuthorizationData = new AuthorizationData();
	private final AuthorizationData cachedAurhorizationData = new AuthorizationData();
	private final AuthorizationData authorizationDataForClient = new AuthorizationData();


	@Before
	public void setUp() throws CredentialException
	{
		when(strategyDeterminationService.get()).thenReturn(authorizationStrategy);

		when(authorizationStrategy.getCpqCredentialsForAdmin()).thenReturn(cpqCredentialsForAdmin);
		when(authorizationStrategy.getAuthorizationData(cpqCredentialsForAdmin)).thenReturn(newAuthorizationData);
		newAuthorizationData.setAccessToken(NEXT_ACCESS_TOKEN);
		cachedAurhorizationData.setAccessToken(ACCESS_TOKEN);
		cachedAurhorizationData.setExpiresAt(System.currentTimeMillis() + HOUR_IN_MS);

		when(authorizationStrategy.getCpqCredentialsForClient(OWNER_ID)).thenReturn(cpqCredentialsForClient);
		when(authorizationStrategy.getAuthorizationData(cpqCredentialsForClient)).thenReturn(authorizationDataForClient);
		authorizationDataForClient.setOwnerId(OWNER_ID);
		when(authorizationStrategy.getTokenExpirationBuffer()).thenReturn(ONE_SECOND_IN_MS);

		when(cacheAccessService.getWithSupplier(same(cacheKey), any())).thenReturn(cachedAurhorizationData);

		when(cacheKeyService.createAuthorizationDataCacheKey()).thenReturn(cacheKey);
	}

	@Test
	public void testRetrieveAuthorizationData()
	{
		assertSame(newAuthorizationData, classUnderTest.retrieveAuthorizationData(cpqCredentialsForAdmin));
	}


	@Test
	public void testGetAuthorizationDataForAdmin()
	{
		assertEquals(ACCESS_TOKEN, classUnderTest.getAuthorizationDataForAdmin().getAccessToken());
		verify(cacheAccessService, times(1)).getWithSupplier(same(cacheKey), any());
	}

	@Test
	public void testGetAuthorizationDataForAdminDiscardsOldToken()
	{
		cachedAurhorizationData.setExpiresAt(System.currentTimeMillis() + ONE_SECOND_IN_MS - 1);// should also be considered invalid, if expires with in one second
		assertEquals(NEXT_ACCESS_TOKEN, classUnderTest.getAuthorizationDataForAdmin().getAccessToken());
		verify(cacheAccessService, times(1)).getWithSupplier(same(cacheKey), any());
		verify(cacheAccessService, times(1)).put(cacheKey, newAuthorizationData);
	}

	@Test
	public void testGetAuthorizationDataForClient()
	{
		assertSame(authorizationDataForClient, classUnderTest.getAuthorizationDataForClient(OWNER_ID));
	}

}
