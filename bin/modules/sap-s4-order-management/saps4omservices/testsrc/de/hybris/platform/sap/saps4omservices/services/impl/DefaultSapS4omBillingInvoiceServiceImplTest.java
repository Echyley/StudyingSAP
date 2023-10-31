/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.saps4omservices.services.impl;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.util.HashSet;
import java.util.Set;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.sap.sapmodel.model.SAPOrderModel;
import de.hybris.platform.sap.saps4omservices.order.services.impl.DefaultSapS4OMOrderService;
import de.hybris.platform.sap.saps4omservices.services.SapS4OrderManagementConfigService;
import de.hybris.platform.store.services.BaseStoreService;

@UnitTest
public class DefaultSapS4omBillingInvoiceServiceImplTest {
	
	@InjectMocks
	DefaultSapS4omBillingInvoiceServiceImpl defaultSapS4omBillingInvoiceServiceImpl;
	
	@Mock
	private BaseStoreService baseStoreService;
	
	@Mock
	private DefaultSapS4OMOrderService defaultSapS4OMOrderService;
	
	@Mock
	private OrderModel model;
	
	@Mock
	private SapS4OrderManagementConfigService sapS4OrderManagementConfigService;
	
	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
		defaultSapS4omBillingInvoiceServiceImpl.setBaseStoreService(baseStoreService);
		defaultSapS4omBillingInvoiceServiceImpl.setDefaultSapS4OMOrderService(defaultSapS4OMOrderService);
		defaultSapS4omBillingInvoiceServiceImpl.setSapS4OrderManagementConfigService(sapS4OrderManagementConfigService);
	}
	
	@Test
	public void getSapOrderBySapOrderCode_WhenSyncOrderisEnabled() {
		Set<SAPOrderModel> sapOrderSet = new HashSet<>();
		SAPOrderModel sapOrder = new SAPOrderModel();
		sapOrder.setCode("12345678");
		sapOrderSet.add(sapOrder);
		when(sapS4OrderManagementConfigService.isS4SynchronousOrderEnabled()).thenReturn(true);
		when(sapS4OrderManagementConfigService.isS4SynchronousOrderHistoryEnabled()).thenReturn(true);
		when(defaultSapS4OMOrderService.getOrderForCode(anyString(), any())).thenReturn(model);
		when(model.getSapOrders()).thenReturn(sapOrderSet);
		SAPOrderModel result = defaultSapS4omBillingInvoiceServiceImpl.getSapOrderBySapOrderCode("12345678");
		Assert.assertEquals(result.getCode(), "12345678");
		
	}

}
