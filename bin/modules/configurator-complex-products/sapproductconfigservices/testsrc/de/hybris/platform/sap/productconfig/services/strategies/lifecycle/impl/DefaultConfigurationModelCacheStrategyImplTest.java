/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.services.strategies.lifecycle.impl;

import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.sap.productconfig.runtime.interf.event.ProductConfigurationCacheInvalidationEvent;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ConfigModel;
import de.hybris.platform.sap.productconfig.services.SessionAccessService;
import de.hybris.platform.sap.productconfig.services.cache.ProductConfigurationCacheAccessService;
import de.hybris.platform.servicelayer.event.EventService;

import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;


/**
 * Unit test for {@link DefaultConfigurationModelCacheStrategyImpl}
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultConfigurationModelCacheStrategyImplTest
{
	private static final String CONFIG_ID = "123";

	@InjectMocks
	private DefaultConfigurationModelCacheStrategyImpl classUnderTest;

	@Mock
	private SessionAccessService mockedSessionAccessService;
	@Mock
	private ProductConfigurationCacheAccessService mockedCacheAccessService;
	@Mock
	private EventService mockedEventService;
	@Mock
	private ConfigModel mockedConfigModel;

	@Before
	public void setUp()
	{
		when(mockedCacheAccessService.getConfigurationModelEngineState(CONFIG_ID)).thenReturn(mockedConfigModel);
	}

	@Test
	public void testPurge()
	{
		classUnderTest.purge();
		verify(mockedSessionAccessService).purge();
	}

	@Test
	public void testConfigAttributeStatesWithParameter()
	{
		classUnderTest.removeConfigAttributeState(CONFIG_ID);
		verify(mockedCacheAccessService).removeConfigAttributeState(CONFIG_ID);
	}

	@Test
	public void testConfigAttributeStatesPublishesEvent()
	{
		classUnderTest.removeConfigAttributeState(CONFIG_ID);
		verify(mockedEventService)
				.publishEvent(argThat(event -> checkEvent((ProductConfigurationCacheInvalidationEvent) event)));
		verify(mockedCacheAccessService).populateCacheKeyContextAttributes(any(Map.class));
	}

	private boolean checkEvent(final ProductConfigurationCacheInvalidationEvent event)
	{
		return event.getConfigId().equals(CONFIG_ID) && event.getContextAttributes() != null;
	}

	@Test
	public void testGetConfigurationModelEngineState()
	{
		final ConfigModel result = classUnderTest.getConfigurationModelEngineState(CONFIG_ID);
		assertNotNull(result);
		verify(mockedCacheAccessService).getConfigurationModelEngineState(CONFIG_ID);
	}

	@Test
	public void testSetConfigurationModelEngineState()
	{
		classUnderTest.setConfigurationModelEngineState(CONFIG_ID, mockedConfigModel);
		verify(mockedCacheAccessService).setConfigurationModelEngineState(CONFIG_ID, mockedConfigModel);
	}
}
