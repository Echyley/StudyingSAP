/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.saps4omfacades.populator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import java.util.Locale;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.order.data.OrderData;
import de.hybris.platform.core.enums.DeliveryStatus;
import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.sap.saps4omservices.services.SapS4OrderManagementConfigService;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultSapS4OMOrderStatusPopulatorTest {
	
	@Spy
	@InjectMocks
	private DefaultSapS4OMOrderStatusPopulator orderStatusPopulator; 
	
	@Mock
	private SapS4OrderManagementConfigService sapS4OrderManagementConfigService;
	
	public static final String PROCESSING = "processing";
	
	@Before
	public void setUp()
	{
		orderStatusPopulator.setSapS4OrderManagementConfigService(sapS4OrderManagementConfigService);
	}
	
	@Test
	public void shouldPopulateProcessing() {
		final OrderModel orderSource = spy(OrderModel.class);
		final OrderData orderData = new OrderData();
		when(sapS4OrderManagementConfigService.isS4SynchronousOrderHistoryEnabled()).thenReturn(true);
		when(orderSource.getDeliveryStatus()).thenReturn(DeliveryStatus.PARTSHIPPED);
		
		orderStatusPopulator.populate(orderSource,orderData);
		assertEquals(PROCESSING,orderData.getStatusDisplay());
	}
	
	@Test
	public void shouldPopulateOrderStatus() { 
		final OrderModel orderSource = spy(OrderModel.class);
		final OrderData orderData = new OrderData();
		when(sapS4OrderManagementConfigService.isS4SynchronousOrderHistoryEnabled()).thenReturn(true);
		when(orderSource.getDeliveryStatus()).thenReturn(DeliveryStatus.SHIPPED);
		when(orderSource.getStatus()).thenReturn(OrderStatus.COMPLETED);
			
		orderStatusPopulator.populate(orderSource,orderData);
		assertEquals(OrderStatus.COMPLETED.toString().toLowerCase(Locale.ENGLISH),orderData.getStatusDisplay());
	}
	
	@Test
	public void shouldNotPopulateOrderStatus() {
		final OrderModel orderSource = spy(OrderModel.class);
		final OrderData orderData = new OrderData();
		when(sapS4OrderManagementConfigService.isS4SynchronousOrderEnabled()).thenReturn(true);
		when(orderSource.getDeliveryStatus()).thenReturn(DeliveryStatus.SHIPPED);
		when(orderSource.getStatus()).thenReturn(null);
			
		orderStatusPopulator.populate(orderSource,orderData);
		assertNull(orderData.getStatusDisplay());
	}
	
}
