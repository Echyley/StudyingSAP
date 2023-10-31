/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.facades.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.sap.productconfig.services.SessionAccessService;
import de.hybris.platform.sap.productconfig.services.strategies.lifecycle.intf.ConfigurationModelCacheStrategy;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;


/**
 * Unit test for {@link SessionAccessFacadeImpl}
 */

@UnitTest
public class SessionAccessFacadeImplTest
{
	private final SessionAccessFacadeImpl classUnderTest = new SessionAccessFacadeImpl();

	@Mock
	private SessionAccessService sessionAccessService;

	@Mock
	private ConfigurationModelCacheStrategy configModelCacheStrategy;

	@Before
	public void setup()
	{
		MockitoAnnotations.initMocks(this);
		classUnderTest.setSessionAccessService(sessionAccessService);
		classUnderTest.setConfigModelCacheStrategy(configModelCacheStrategy);
	}

	@Test
	public void testService()
	{
		assertNotNull(classUnderTest.getSessionAccessService());
	}


	@Test
	public void testUiStatusForCartEntry()
	{
		final Object status = "1";
		final String cartEntryKey = "X";

		Mockito.when(sessionAccessService.getUiStatusForCartEntry(cartEntryKey)).thenReturn(status);
		classUnderTest.setUiStatusForCartEntry(cartEntryKey, status);
		assertEquals(status, classUnderTest.getUiStatusForCartEntry(cartEntryKey));
	}

	@Test
	public void testUiStatusForProduct()
	{
		final Object status = "1";
		final String productKey = "X";

		Mockito.when(sessionAccessService.getUiStatusForProduct(productKey)).thenReturn(status);
		classUnderTest.setUiStatusForProduct(productKey, status);
		assertEquals(status, classUnderTest.getUiStatusForProduct(productKey));
	}

	@Test
	public void testRemoveStatusForProduct()
	{
		final String productKey = "A";
		classUnderTest.removeUiStatusForProduct(productKey);
		Mockito.verify(sessionAccessService, Mockito.times(1)).removeUiStatusForProduct((productKey));
	}

	@Test
	public void testRemoveStatusForCartEntry()
	{
		final String cartEntryKey = "A";
		classUnderTest.removeUiStatusForCartEntry(cartEntryKey);
		Mockito.verify(sessionAccessService, Mockito.times(1)).removeUiStatusForCartEntry((cartEntryKey));
	}

}
