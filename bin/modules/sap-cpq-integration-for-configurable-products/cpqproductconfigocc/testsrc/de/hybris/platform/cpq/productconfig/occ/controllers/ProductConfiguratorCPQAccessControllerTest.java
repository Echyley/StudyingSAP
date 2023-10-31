/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cpq.productconfig.occ.controllers;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.cpq.productconfig.facades.AccessControlFacade;
import de.hybris.platform.cpq.productconfig.facades.data.AccessControlData;
import de.hybris.platform.cpq.productconfig.occ.ConfigurationEngineAccessWsDTO;
import de.hybris.platform.webservicescommons.mapping.DataMapper;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


/**
 * Unit test for {@link ProductConfiguratorCPQAccessController}
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class ProductConfiguratorCPQAccessControllerTest
{
	@InjectMocks
	private ProductConfiguratorCPQAccessController classUnderTest;

	@Mock
	private AccessControlFacade accessControlFacade;
	@Mock
	private DataMapper dataMapper;

	private final AccessControlData accessControlData = new AccessControlData();
	private final ConfigurationEngineAccessWsDTO configurationEngineAccessWsDTO = new ConfigurationEngineAccessWsDTO();

	@Before
	public void setup()
	{
		when(accessControlFacade.performAccessControlForClient()).thenReturn(accessControlData);
		when(dataMapper.map(accessControlData, ConfigurationEngineAccessWsDTO.class)).thenReturn(configurationEngineAccessWsDTO);
	}

	@Test
	public void testGetAccessToConfigurationEngine()
	{
		final ConfigurationEngineAccessWsDTO result = classUnderTest.getAccessToConfigurationEngine();
		assertNotNull(result);
	}
}
