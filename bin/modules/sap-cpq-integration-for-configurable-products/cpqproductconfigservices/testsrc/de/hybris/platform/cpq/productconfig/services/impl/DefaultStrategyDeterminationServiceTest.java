/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cpq.productconfig.services.impl;

import static org.junit.Assert.assertSame;
import static org.mockito.BDDMockito.given;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.cpq.productconfig.services.EngineDeterminationService;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


/**
 * Unit test for {@link DefaultStrategyDeterminationService}
 */
@UnitTest
public class DefaultStrategyDeterminationServiceTest
{

	private DefaultStrategyDeterminationService<Object> classUnderTest;

	@Mock
	private EngineDeterminationService engineDeterminationService;
	@Mock
	private Object mockEngine;
	@Mock
	private Object defaultEngine;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
		// can't use @InjectMocks, but need to call constructor explicitly to make as engines have same type
		classUnderTest = new DefaultStrategyDeterminationService<Object>(engineDeterminationService, defaultEngine, mockEngine);
	}

	@Test
	public void testGetCurrentStrategyMock()
	{
		given(engineDeterminationService.isMockEngineActive()).willReturn(true);
		assertSame(mockEngine, classUnderTest.get());
	}

	@Test
	public void testGetCurrentStrategyDefault()
	{
		given(engineDeterminationService.isMockEngineActive()).willReturn(false);
		assertSame(defaultEngine, classUnderTest.get());
	}

}
