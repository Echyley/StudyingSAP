/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cpq.productconfig.services.impl;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.cpq.productconfig.services.CacheAccessService;
import de.hybris.platform.cpq.productconfig.services.cache.DefaultCacheKey;
import de.hybris.platform.cpq.productconfig.services.data.AuthorizationData;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


/**
 * Unit test for {@link DefaultEngineDeterminationService}
 */
@UnitTest
public class DefaultEngineDeterminationServiceTest
{
	private DefaultEngineDeterminationService classUnderTest;

	@Mock
	private CacheAccessService<DefaultCacheKey, AuthorizationData> cacheAccessService;

	@Before
	public void initialize()
	{
		MockitoAnnotations.initMocks(this);
		// can't use dependency injection, as we have one non-mockable primitive constructor argument
		classUnderTest = new DefaultEngineDeterminationService(cacheAccessService, false);
	}

	@Test
	public void testIsMockMode()
	{
		final boolean isMockMode = classUnderTest.isMockEngineActive();
		assertFalse(isMockMode);
	}

	@Test
	public void testCanSetMockMode()
	{
		classUnderTest.setMockEngineActive(true);
		assertTrue(classUnderTest.isMockEngineActive());
		verify(cacheAccessService).clearCache();
	}

	@Test
	public void testIsEngineSwitchAllowed()
	{
		assertTrue(classUnderTest.isEngineSwitchAllowed());
	}

	@Test(expected = IllegalStateException.class)
	public void testFailIfSwicthNotAllowed()
	{
		classUnderTest = spy(classUnderTest); // spy required to simulate case when switch not allowed (currentTenant != 'junit').
		given(classUnderTest.isEngineSwitchAllowed()).willReturn(false);
		classUnderTest.setMockEngineActive(true);
	}

	@Test
	public void testCanResetMockMode()
	{
		classUnderTest.setMockEngineActive(true);
		classUnderTest.reset();
		assertFalse(classUnderTest.isMockEngineActive());
		verify(cacheAccessService, times(2)).clearCache();
	}
}
