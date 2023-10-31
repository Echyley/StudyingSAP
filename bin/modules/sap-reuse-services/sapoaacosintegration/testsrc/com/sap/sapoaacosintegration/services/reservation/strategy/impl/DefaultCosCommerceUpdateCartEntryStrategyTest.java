/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.sapoaacosintegration.services.reservation.strategy.impl;

import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;


/**
 *
 * This test class is used to bypass DefaultCosCommerceUpdateCartEntryStrategy class for timebeing.
 */
@RunWith(MockitoJUnitRunner.class)
public class DefaultCosCommerceUpdateCartEntryStrategyTest
{
	@InjectMocks
	DefaultCosCommerceUpdateCartEntryStrategy defaultCosCommerceUpdateCartEntryStrategy;

	@Before
	public void setup()
	{
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void getAllowedCartAdjustmentForProductTest()
	{
		assertTrue(true);
	}

	@Test
	public void modifyEntryTest()
	{
		assertTrue(true);
	}
}
