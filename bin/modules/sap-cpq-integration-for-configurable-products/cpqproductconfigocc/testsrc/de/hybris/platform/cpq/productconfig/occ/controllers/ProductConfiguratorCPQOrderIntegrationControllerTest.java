/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cpq.productconfig.occ.controllers;

import static org.junit.Assert.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.cpq.productconfig.facades.OrderIntegrationFacade;
import de.hybris.platform.cpq.productconfig.facades.data.ProductConfigData;
import de.hybris.platform.cpq.productconfig.occ.ProductConfigWsDTO;
import de.hybris.platform.webservicescommons.mapping.DataMapper;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


/**
 * Unit test for {@link ProductConfiguratorCPQOrderIntegrationController}
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class ProductConfiguratorCPQOrderIntegrationControllerTest
{
	private static final int ENTRY_NUMBER = 3;
	private static final String CONFIG_ID = "c123";
	private static final String ORDER_ID = "order id";

	@InjectMocks
	private ProductConfiguratorCPQOrderIntegrationController classUnderTest;
	@Mock
	private OrderIntegrationFacade orderIntegrationFacade;
	@Mock
	private DataMapper dataMapper;

	private final ProductConfigData productConfigData = new ProductConfigData();
	private final ProductConfigWsDTO wsProductConfig = new ProductConfigWsDTO();

	@Before
	public void setup()
	{
		productConfigData.setConfigId(CONFIG_ID);
		when(orderIntegrationFacade.getConfigurationId(ORDER_ID, ENTRY_NUMBER)).thenReturn(productConfigData);
		wsProductConfig.setConfigId(CONFIG_ID);
		given(dataMapper.map(productConfigData, ProductConfigWsDTO.class)).willReturn(wsProductConfig);
	}

	@Test
	public void testGetOrderEntryConfiguartionIdInternal()
	{
		assertEquals(CONFIG_ID, classUnderTest.getConfigurationIdForOrderEntry(ORDER_ID, ENTRY_NUMBER).getConfigId());
	}

	@Test
	public void testGetConfigurationIdForOrderEntry()
	{
		assertEquals(CONFIG_ID, classUnderTest.getConfigurationIdForOrderEntry(ORDER_ID, ENTRY_NUMBER).getConfigId());
	}
}
