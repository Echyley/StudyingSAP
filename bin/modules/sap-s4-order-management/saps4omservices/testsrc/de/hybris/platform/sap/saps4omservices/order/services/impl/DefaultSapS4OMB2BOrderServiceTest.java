/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.saps4omservices.order.services.impl;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.b2b.dao.B2BOrderDao;
import de.hybris.platform.commerceservices.customer.CustomerAccountService;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.sap.saps4omservices.services.SapS4OrderManagementConfigService;
import de.hybris.platform.sap.saps4omservices.services.impl.DefaultSapS4OMOutboundService;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.store.services.BaseStoreService;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultSapS4OMB2BOrderServiceTest {
	
	private static final String ORDER_CODE = "123";
	
	@InjectMocks
	private DefaultSapS4OMB2BOrderService defaultSapS4OMB2BOrderService;
	
	@Mock
	private SapS4OrderManagementConfigService sapS4OrderManagementConfigService;
	
	@Mock
	private DefaultSapS4OMOutboundService defaultSapS4OMOutboundService;
	
	@Mock
	private OrderModel orderModel;
	
	@Mock
	private B2BOrderDao b2BOrderDao;
	
	@Mock
	private BaseStoreModel baseStoreModel;
	
	@Mock
	private CustomerAccountService customerAccountService;
	
	@Mock
	private BaseStoreService baseStoreService;
	
	@Test
	public void testGetOrderForCodeSynchronousOrderCall() {
		when(sapS4OrderManagementConfigService.isS4SynchronousOrderEnabled()).thenReturn(true);
		when(baseStoreService.getCurrentBaseStore()).thenReturn(baseStoreModel);
		when(customerAccountService.getOrderForCode(ORDER_CODE, baseStoreModel)).thenReturn(orderModel);
		final OrderModel result = defaultSapS4OMB2BOrderService.getOrderForCode(ORDER_CODE);
		assertNotNull(result);
	}
	
	@Test
	public void testGetOrderForCodeSuperClassCall() { 
		final OrderModel orderModels = spy(OrderModel.class);
		when(sapS4OrderManagementConfigService.isS4SynchronousOrderEnabled()).thenReturn(false);	
		when(b2BOrderDao.findOrderByCode(ORDER_CODE)).thenReturn(orderModels);
		final OrderModel result = defaultSapS4OMB2BOrderService.getOrderForCode(ORDER_CODE);
		assertNotNull(result);
	}
	
}
