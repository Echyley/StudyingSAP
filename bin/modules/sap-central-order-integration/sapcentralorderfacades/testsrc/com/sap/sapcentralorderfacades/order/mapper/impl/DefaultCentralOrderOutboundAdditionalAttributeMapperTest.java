/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.sapcentralorderfacades.order.mapper.impl;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.sap.core.configuration.model.SAPConfigurationModel;
import de.hybris.platform.sap.sapcpiadapter.model.SAPCpiOutboundConfigModel;
import de.hybris.platform.sap.sapcpiadapter.model.SAPCpiOutboundOrderModel;
import de.hybris.platform.store.BaseStoreModel;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import com.sap.sapcentralorderservices.services.config.CoConfigurationService;


/**
 *
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultCentralOrderOutboundAdditionalAttributeMapperTest
{

	/**
	 *
	 */
	private static final String SOURCE_SYSTEM_ID = "SYSTEM1";


	private final OrderModel orderModel = mock(OrderModel.class);

	@Mock
	private final BaseStoreModel baseStoreModel = mock(BaseStoreModel.class);

	@Mock
	private final SAPConfigurationModel sapConfigurationModel = mock(SAPConfigurationModel.class);

	@Mock
	private final CoConfigurationService configurationService = mock(CoConfigurationService.class);


	private SAPCpiOutboundOrderModel sapCpiOutboundOrderModel = mock(SAPCpiOutboundOrderModel.class);


	private SAPCpiOutboundConfigModel sapCpiOutboundConfigModel;

	@InjectMocks
	private final DefaultCentralOrderOutboundAdditionalAttributeMapper defaultCentralOrderOutboundAdditionalAttributeMapper = new DefaultCentralOrderOutboundAdditionalAttributeMapper();

	@Before
	public void setUp()
	{

		defaultCentralOrderOutboundAdditionalAttributeMapper.setConfigurationService(configurationService);

		sapCpiOutboundOrderModel = new SAPCpiOutboundOrderModel();
		sapCpiOutboundConfigModel = new SAPCpiOutboundConfigModel();
		sapCpiOutboundOrderModel.setSapCpiConfig(sapCpiOutboundConfigModel);

		//Mocks
		Mockito.lenient().when(orderModel.getStore()).thenReturn(baseStoreModel);
		Mockito.lenient().when(baseStoreModel.getSAPConfiguration()).thenReturn(sapConfigurationModel);
		Mockito.lenient().when(sapConfigurationModel.getSapco_active()).thenReturn(true);
		Mockito.lenient().when(sapConfigurationModel.getSapco_sourceSystemId()).thenReturn(SOURCE_SYSTEM_ID);

		Mockito.lenient().when(configurationService.isCoActiveFromBaseStore(orderModel)).thenReturn(true);
	}

	@Test
	public void addAdditionalAttributesTest()
	{
		//before
		assertEquals(sapCpiOutboundConfigModel.getCentralOrderSourceSystemId(), null);
		//test
		defaultCentralOrderOutboundAdditionalAttributeMapper.mapAdditionalAttributes(orderModel, sapCpiOutboundOrderModel);
		//after
		assertEquals(sapCpiOutboundOrderModel.getSapCpiConfig().getCentralOrderSourceSystemId(), SOURCE_SYSTEM_ID);
	}
}
