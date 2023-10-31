/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.sapcentralorderfacades.order.mapper.impl;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.sap.core.configuration.model.SAPConfigurationModel;
import de.hybris.platform.sap.sapcpiadapter.data.SapCpiConfig;
import de.hybris.platform.store.BaseStoreModel;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import com.sap.sapcentralorderservices.services.config.CoConfigurationService;


/**
 *
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultCentralOrderCpiConvertorTest
{


	private final OrderModel orderModel = mock(OrderModel.class);


	private final BaseStoreModel baseStoreModel = mock(BaseStoreModel.class);


	private final SAPConfigurationModel sapConfigurationModel = mock(SAPConfigurationModel.class);


	private final CoConfigurationService configurationService = mock(CoConfigurationService.class);


	private final DefaultCentralOrderCpiConvertor defaultCentralOrderCpiConvertor = new DefaultCentralOrderCpiConvertor();

	@Before
	public void setUp()
	{

		defaultCentralOrderCpiConvertor.setConfigurationService(configurationService);
		Mockito.lenient().when(orderModel.getStore()).thenReturn(baseStoreModel);
		Mockito.lenient().when(baseStoreModel.getSAPConfiguration()).thenReturn(sapConfigurationModel);
		Mockito.lenient().when(sapConfigurationModel.getSapco_active()).thenReturn(true);
		Mockito.lenient().when(configurationService.isCoActiveFromBaseStore(orderModel)).thenReturn(true);
	}

	@Test
	public void mapOrderConfigInfoTest()
	{
		final Object object = defaultCentralOrderCpiConvertor.mapOrderConfigInfo(orderModel);
		assertTrue(object instanceof SapCpiConfig);
	}
}
