/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.occ.controllers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.sap.productconfig.facades.ConfigurationOverviewFacade;
import de.hybris.platform.sap.productconfig.facades.ConfigurationQuoteIntegrationFacade;
import de.hybris.platform.sap.productconfig.facades.overview.ConfigurationOverviewData;
import de.hybris.platform.sap.productconfig.occ.ConfigurationOverviewWsDTO;
import de.hybris.platform.sap.productconfig.occ.util.MessageHandler;
import de.hybris.platform.webservicescommons.errors.exceptions.NotFoundException;
import de.hybris.platform.webservicescommons.mapping.DataMapper;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class ProductConfiguratorCCPQuoteIntegrationControllerTest
{
	private static final String QUOTE_ID = "001";

	private static final String CONFIG_ID = "config123";

	@Mock
	private ConfigurationQuoteIntegrationFacade configQuoteFacade;

	@Mock
	private ConfigurationOverviewFacade configOvFacade;

	@Mock
	private DataMapper dataMapper;

	@Mock
	private MessageHandler messageHandler;

	@InjectMocks
	private ProductConfiguratorCCPQuoteIntegrationController classUnderTest;

	private final ConfigurationOverviewData configOvData = new ConfigurationOverviewData();
	private final ConfigurationOverviewWsDTO configOvWsDTO = new ConfigurationOverviewWsDTO();

	@Before
	public void setUp()
	{
		MockitoAnnotations.openMocks(this);
		configOvData.setId(CONFIG_ID);
		configOvWsDTO.setId(CONFIG_ID);
		given(configQuoteFacade.getConfiguration(QUOTE_ID, 3)).willThrow(new IllegalArgumentException("TEST"));
		given(configQuoteFacade.getConfiguration(QUOTE_ID, 0)).willReturn(configOvData);
		given(configOvFacade.getOverviewForConfiguration(CONFIG_ID, configOvData)).willReturn(configOvData);
		given(dataMapper.map(configOvData, ConfigurationOverviewWsDTO.class)).willReturn(configOvWsDTO);
	}

	@Test
	public void testReadConfigurationOveviewReturnsConfigId() throws NotFoundException
	{
		final ConfigurationOverviewData result = classUnderTest.readConfigurationOverview(QUOTE_ID, 0);
		assertNotNull(result);
		assertEquals(CONFIG_ID, result.getId());
	}

	@Test
	public void testReadConfigurationOveviewMapsData() throws NotFoundException
	{
		classUnderTest.readConfigurationOverview(QUOTE_ID, 0);
		verify(configOvFacade).getOverviewForConfiguration(CONFIG_ID, configOvData);
	}

	@Test
	public void testGetConfigurationOverviewForQuote() throws NotFoundException
	{
		final ConfigurationOverviewWsDTO resultWsDTO = classUnderTest.getConfigurationOverviewForQuotes(QUOTE_ID, 0);
		assertNotNull(resultWsDTO);
		assertEquals(CONFIG_ID, resultWsDTO.getId());
		verify(messageHandler, times(1)).processConfigurationOverview(any(ConfigurationOverviewWsDTO.class));
	}

	@Test(expected = NotFoundException.class)
	public void testGetConfigurationOverviewForQuoteNotFound() throws NotFoundException
	{
		classUnderTest.getConfigurationOverviewForQuotes(QUOTE_ID, 3);
	}


}
