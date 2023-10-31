/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.services.impl;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commerceservices.service.data.ProductConfigurationItem;
import de.hybris.platform.core.model.order.OrderEntryModel;
import de.hybris.platform.sap.productconfig.services.model.CPQConfiguratorSettingsModel;

import java.util.Collection;

import org.junit.Before;
import org.junit.Test;


/**
 * Unit test for {@link CPQConfigurationHandler}
 */
@UnitTest
public class CPQConfigurationHandlerTest
{
	private CPQConfigurationHandler classUnderTest;

	@Before
	public void setUp()
	{
		classUnderTest = new CPQConfigurationHandler();
	}

	@Test
	public void testCreateProductInfo() throws IllegalArgumentException
	{
		final CPQConfiguratorSettingsModel productSettings = new CPQConfiguratorSettingsModel();
		assertNotNull(classUnderTest.createProductInfo(productSettings));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testCreateProductInfoNonCPQSettings() throws IllegalArgumentException
	{
		classUnderTest.createProductInfo(null);
	}

	@Test
	public void testConvert()
	{
		final Collection<ProductConfigurationItem> items = null;
		final OrderEntryModel entry = null;
		assertTrue(classUnderTest.convert(items, entry).isEmpty());
	}
}
