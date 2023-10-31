/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.saps4omservices.services.impl;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import java.util.Collections;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.security.PrincipalModel;
import de.hybris.platform.ordercancel.CancelDecision;
import de.hybris.platform.ordercancel.dao.OrderCancelDao;
import de.hybris.platform.sap.saps4omservices.services.SapS4OrderManagementConfigService;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultSapS4OrderCancelServiceTest {

	@Spy
	@InjectMocks
	DefaultSapS4OrderCancelService defaultSapS4OrderCancelService; 
	
	@Mock
	SapS4OrderManagementConfigService sapS4OrderManagementConfigService;
	
	@Mock
	OrderCancelDao orderCancelDao;
	
	@Before
	public void init() {
		
		defaultSapS4OrderCancelService.setSapS4OrderManagementConfigService(sapS4OrderManagementConfigService);
		defaultSapS4OrderCancelService.setOrderCancelDao(orderCancelDao);
		defaultSapS4OrderCancelService.setCancelDenialStrategies(Collections.emptyList());
		
	}
	
	@Test
	public void testIsCancelPossibleWhenBothDisabled() {
		
		OrderModel order = spy(OrderModel.class);
		PrincipalModel requestor = spy(PrincipalModel.class);
		boolean partialCancel = true;
		boolean partialEntryCancel = true;
		
		when(sapS4OrderManagementConfigService.isS4SynchronousOrderEnabled()).thenReturn(false);
		when(sapS4OrderManagementConfigService.isS4SynchronousOrderHistoryEnabled()).thenReturn(false);
		
		when(orderCancelDao.getOrderCancelConfiguration()).thenReturn(null);
		
		CancelDecision result = defaultSapS4OrderCancelService.isCancelPossible(order, requestor, partialCancel, partialEntryCancel);
		
		assertTrue(result.isAllowed());
	}	
	
	@Test
	public void testIsCancelPossibleWhenSyncOrderEnabled() {
		
		OrderModel order = spy(OrderModel.class);
		PrincipalModel requestor = spy(PrincipalModel.class);
		boolean partialCancel = true;
		boolean partialEntryCancel = true;
		
		when(sapS4OrderManagementConfigService.isS4SynchronousOrderEnabled()).thenReturn(true);
		
		CancelDecision result = defaultSapS4OrderCancelService.isCancelPossible(order, requestor, partialCancel, partialEntryCancel);
		
		assertFalse(result.isAllowed());
	}
	
	@Test
	public void testIsCancelPossibleWhenSyncOrderHistoryEnabled() {
		
		OrderModel order = spy(OrderModel.class);
		PrincipalModel requestor = spy(PrincipalModel.class);
		boolean partialCancel = true;
		boolean partialEntryCancel = true;
		
		when(sapS4OrderManagementConfigService.isS4SynchronousOrderHistoryEnabled()).thenReturn(true);
		
		CancelDecision result = defaultSapS4OrderCancelService.isCancelPossible(order, requestor, partialCancel, partialEntryCancel);
		
		assertFalse(result.isAllowed());
	}
	
}
