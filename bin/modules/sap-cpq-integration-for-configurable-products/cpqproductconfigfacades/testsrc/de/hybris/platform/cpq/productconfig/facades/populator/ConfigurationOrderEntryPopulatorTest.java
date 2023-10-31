/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cpq.productconfig.facades.populator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verifyZeroInteractions;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.catalog.enums.ConfiguratorType;
import de.hybris.platform.catalog.enums.ProductInfoStatus;
import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.cpq.productconfig.services.ConfigurationService;
import de.hybris.platform.cpq.productconfig.services.ConfigurationServiceLayerHelper;
import de.hybris.platform.cpq.productconfig.services.model.CloudCPQOrderEntryProductInfoModel;


/**
 * Unit test for {@link ConfigurationOrderEntryPopulator}
 */
@RunWith(MockitoJUnitRunner.class)
@UnitTest
public class ConfigurationOrderEntryPopulatorTest
{
	@InjectMocks
	private ConfigurationOrderEntryPopulator<AbstractOrderEntryModel> classUnderTest;

	@Mock
	private ConfigurationService cpqConfigService;

	@Mock
	private ConfigurationServiceLayerHelper cpqServiceHelper;

	private final AbstractOrderEntryModel entry = new AbstractOrderEntryModel();
	private final CloudCPQOrderEntryProductInfoModel cpqOrderEntryInfoModel = new CloudCPQOrderEntryProductInfoModel();
	private final OrderEntryData entryData = new OrderEntryData();

	private static String CONFIG_ID = "conf123";

	@Before
	public void setUp()
	{
		cpqOrderEntryInfoModel.setConfiguratorType(ConfiguratorType.CLOUDCPQCONFIGURATOR);
		cpqOrderEntryInfoModel.setConfigurationId(CONFIG_ID);
		given(cpqConfigService.getNumberOfConfigurationIssues(CONFIG_ID, entry)).willReturn(3);
		given(cpqServiceHelper.getCPQInfo(entry)).willReturn(cpqOrderEntryInfoModel);
	}

	@Test
	public void testPopulateCPQEntryErrors()
	{
		classUnderTest.populate(entry, entryData);
		assertEquals(Integer.valueOf(3), entryData.getStatusSummaryMap().get(ProductInfoStatus.ERROR));
	}

	@Test
	public void testPopulateCPQEntryNoErrors()
	{
		given(cpqConfigService.getNumberOfConfigurationIssues(CONFIG_ID, entry)).willReturn(0);
		classUnderTest.populate(entry, entryData);
		assertEquals(Integer.valueOf(0), entryData.getStatusSummaryMap().get(ProductInfoStatus.SUCCESS));
	}

	@Test
	public void testPopulateNonCPQEntry()
	{
		given(cpqServiceHelper.getCPQInfo(entry)).willReturn(null);
		classUnderTest.populate(entry, entryData);
		assertNull(entryData.getStatusSummaryMap());
	}

	@Test
	public void testPopulateCPQEntryNoConfigId()
	{
		cpqOrderEntryInfoModel.setConfigurationId("");
		classUnderTest.populate(entry, entryData);
		assertEquals(Integer.valueOf(1), entryData.getStatusSummaryMap().get(ProductInfoStatus.ERROR));
		verifyZeroInteractions(cpqConfigService);
	}

}
