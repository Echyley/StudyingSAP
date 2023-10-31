/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.occ.controllers;

import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.sap.productconfig.facades.ConfigurationFacade;
import de.hybris.platform.sap.productconfig.facades.ConfigurationOverviewFacade;
import de.hybris.platform.sap.productconfig.facades.overview.ConfigurationOverviewData;
import de.hybris.platform.sap.productconfig.occ.ConfigurationOverviewWsDTO;
import de.hybris.platform.sap.productconfig.occ.util.MessageHandler;
import de.hybris.platform.webservicescommons.mapping.DataMapper;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class ProductConfigOverviewWsControllerTest
{
	private static final String CONFIG_ID = "config id";
	private static final String PRODUCT_CODE = "product A";
	@Mock
	private ConfigurationOverviewFacade configOverviewFacade;
	@Mock
	private DataMapper dataMapper;
	@Mock
	private ConfigurationFacade configFacade;
	@Mock
	private MessageHandler messageHandler;
	@InjectMocks
	private ProductConfiguratorCCPController classUnderTest;

	private ConfigurationOverviewData overviewData;


	@Before
	public void setup()
	{
		overviewData = new ConfigurationOverviewData();
		overviewData.setId(CONFIG_ID);
		overviewData.setProductCode(PRODUCT_CODE);
		when(configOverviewFacade.getOverviewForConfiguration(CONFIG_ID, null)).thenReturn(overviewData);
		when(configFacade.getNumberOfErrors(CONFIG_ID)).thenReturn(0);
		when(dataMapper.map(overviewData, ConfigurationOverviewWsDTO.class)).thenReturn(new ConfigurationOverviewWsDTO());

		classUnderTest.setConfigOverviewFacade(configOverviewFacade);
		classUnderTest.setConfigFacade(configFacade);

	}

	@Test
	public void testGetOverview()
	{
		final ConfigurationOverviewWsDTO result = classUnderTest.getConfigurationOverview(CONFIG_ID);
		assertNotNull(result);

		verify(configOverviewFacade, times(1)).getOverviewForConfiguration(CONFIG_ID, null);
		verify(dataMapper, times(1)).map(overviewData, ConfigurationOverviewWsDTO.class);
		verify(messageHandler, times(1)).processConfigurationOverview(any(ConfigurationOverviewWsDTO.class));
	}


}
