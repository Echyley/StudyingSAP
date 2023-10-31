/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cpq.productconfig.facades.integrationtests.mock;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.catalog.enums.ProductInfoStatus;
import de.hybris.platform.commercefacades.order.data.ConfigurationInfoData;
import de.hybris.platform.cpq.productconfig.facades.integrationtests.AbstractOrderIntegrationTestBase;
import de.hybris.platform.cpq.productconfig.services.strategies.impl.mock.MockConfigurationLifecycleStrategy;

import java.util.List;

import javax.annotation.Resource;


/**
 * Integration test for Abstract Order Mock Integration
 */
@IntegrationTest
public class AbstractOrderMockIntegrationTest extends AbstractOrderIntegrationTestBase
{

	@Resource(name = "cpqProductConfigMockConfigurationLifecycleStrategy")
	private MockConfigurationLifecycleStrategy mockStrategy;



	@Override
	public void setUp() throws Exception
	{
		super.setUp();
		mockStrategy.setUseFallback(false);
	}

	@Override
	public void tearDown() throws Exception
	{
		mockStrategy.restoreDefaultFallbackBehaviour();
		super.tearDown();
	}


	@Override
	protected void checkConfigDeleted(final String configId)
	{
		try
		{
			configurationService.getConfigurationSummary(configId);
			fail("Configuration should be deleted.");
		}
		catch (final IllegalStateException ex)
		{
			// expected
		}

	}

	@Override
	protected void validateInfoDataList(final List<ConfigurationInfoData> infoDataList)
	{
		assertEquals(20, infoDataList.size());

		validateInfoDataSuccess(infoDataList.get(0), "CI#@#VERSION", "2");
		validateInfoDataSuccess(infoDataList.get(1), "CI#@#CURRENCY_ISO_CODE", "USD");

		validateInfoDataSuccess(infoDataList.get(2), "LI#0#KEY", "CANON_EOS_80D");
		validateInfoDataSuccess(infoDataList.get(3), "LI#0#NAME", "Canon EOS 80D");
		validateInfoDataSuccess(infoDataList.get(4), "LI#0#QTY", "");

		validateInfoDataSuccess(infoDataList.get(5), "LI#1#KEY", "SANDISK_EXTREME_PRO_128GB_SDXC");
		validateInfoDataSuccess(infoDataList.get(6), "LI#1#NAME", "SanDisk Extreme Pro 128GB SDXC");
		validateInfoDataSuccess(infoDataList.get(7), "LI#1#QTY", "1");
		validateInfoDataSuccess(infoDataList.get(8), "LI#1#FORMATTED_PRICE", "$100.00");
		validateInfoDataSuccess(infoDataList.get(9), "LI#1#PRICE_VALUE", "100.00");

		validateInfoDataSuccess(infoDataList.get(10), "LI#2#KEY", "CANON_RF_24-105MM_F/4L_IS_USM");
		validateInfoDataSuccess(infoDataList.get(11), "LI#2#NAME", "Canon RF 24-105mm f/4L IS USM");
		validateInfoDataSuccess(infoDataList.get(12), "LI#2#QTY", "");
		validateInfoDataSuccess(infoDataList.get(13), "LI#2#FORMATTED_PRICE", "$1,500.00");
		validateInfoDataSuccess(infoDataList.get(14), "LI#2#PRICE_VALUE", "1500.00");

		validateInfoDataSuccess(infoDataList.get(15), "LI#3#KEY", "LOWEPRO_STREETLINE_SL_140");
		validateInfoDataSuccess(infoDataList.get(16), "LI#3#NAME", "LowePro Streetline SL 140");
		validateInfoDataSuccess(infoDataList.get(17), "LI#3#QTY", "");
		validateInfoDataSuccess(infoDataList.get(18), "LI#3#FORMATTED_PRICE", "$110.00");
		validateInfoDataSuccess(infoDataList.get(19), "LI#3#PRICE_VALUE", "110.00");

	}

	@Override
	protected void validateInfoDataForDarkAddToCart(final ConfigurationInfoData configurationInfoData)
	{
		validateInfoData(configurationInfoData, ProductInfoStatus.ERROR, "LI#0#KEY", "CONF_POWER_SUPPLY_cpq");
	}

}
