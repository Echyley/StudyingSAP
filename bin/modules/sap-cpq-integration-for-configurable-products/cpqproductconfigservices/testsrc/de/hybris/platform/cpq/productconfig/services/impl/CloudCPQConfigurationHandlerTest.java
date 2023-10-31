/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cpq.productconfig.services.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.catalog.enums.ConfiguratorType;
import de.hybris.platform.cpq.productconfig.services.model.CloudCPQConfiguratorSettingsModel;
import de.hybris.platform.order.model.AbstractOrderEntryProductInfoModel;

import java.util.List;

import org.junit.Test;


/**
 * Unit test for {@link CloudCPQConfigurationHandler}
 */
@UnitTest
public class CloudCPQConfigurationHandlerTest
{
	private final CloudCPQConfigurationHandler classUnderTest = new CloudCPQConfigurationHandler();

	@Test
	public void testConvert()
	{
		assertTrue("convert should always return empty list", classUnderTest.convert(null, null).isEmpty());
	}

	@Test
	public void testCreateProductInfo()
	{
		final List<AbstractOrderEntryProductInfoModel> productInfos = classUnderTest
				.createProductInfo(new CloudCPQConfiguratorSettingsModel());
		assertNotNull(productInfos);
		assertEquals(1, productInfos.size());
		assertEquals(ConfiguratorType.CLOUDCPQCONFIGURATOR, productInfos.get(0).getConfiguratorType());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testCreateProductInfoNonCloudCPQSettings() throws IllegalArgumentException
	{
		classUnderTest.createProductInfo(null);
	}
}
