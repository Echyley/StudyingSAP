/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cpq.productconfig.occ.controllers;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.cpq.productconfig.facades.QuoteIntegrationFacade;
import de.hybris.platform.cpq.productconfig.facades.data.ProductConfigData;
import de.hybris.platform.cpq.productconfig.occ.ProductConfigWsDTO;
import de.hybris.platform.webservicescommons.mapping.DataMapper;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;


/**
 * Unit test for {@link ProductConfiguratorCPQQuoteIntegrationController}
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class ProductConfiguratorCPQQuoteIntegrationControllerTest
{
	private static final int ENTRY_NUMBER = 3;
	private static final String CONFIG_ID = "c123";
	private static final String QUOTE_ID = "quote id";

	@InjectMocks
	private ProductConfiguratorCPQQuoteIntegrationController classUnderTest;
	@Mock
	private QuoteIntegrationFacade quoteIntegrationFacade;
	@Mock
	private DataMapper dataMapper;

	private final ProductConfigWsDTO wsProductConfig = new ProductConfigWsDTO();

	@Before
	public void setup()
	{
		when(quoteIntegrationFacade.getConfigurationId(QUOTE_ID, ENTRY_NUMBER)).thenReturn(CONFIG_ID);
		wsProductConfig.setConfigId(CONFIG_ID);
		given(dataMapper.map(Mockito.any(ProductConfigData.class), eq(ProductConfigWsDTO.class))).willReturn(wsProductConfig);
	}

	@Test
	public void testGetQuoteEntryConfiguartionIdInternal()
	{
		assertEquals(CONFIG_ID, classUnderTest.getQuoteEntryConfiguartionIdInternal(QUOTE_ID, ENTRY_NUMBER).getConfigId());
	}

	@Test
	public void testGetConfigurationIdForQuoteEntry()
	{
		assertEquals(CONFIG_ID, classUnderTest.getConfigurationIdForQuoteEntry(QUOTE_ID, ENTRY_NUMBER).getConfigId());
	}
}
