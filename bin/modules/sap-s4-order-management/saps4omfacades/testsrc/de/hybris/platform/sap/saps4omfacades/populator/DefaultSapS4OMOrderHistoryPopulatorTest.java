/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.saps4omfacades.populator;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.spy;

import java.util.Locale;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.order.data.OrderHistoryData;
import de.hybris.platform.core.enums.DeliveryStatus;
import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.sap.saps4omservices.services.SapS4OrderManagementConfigService;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultSapS4OMOrderHistoryPopulatorTest {
	public static final String PROCESSING = "processing";
	
	@Spy
	@InjectMocks
	DefaultSapS4OMOrderHistoryPopulator sapS4OrderManagementHistoryPopulator;

	@Mock
	SapS4OrderManagementConfigService sapS4OrderManagementConfigService;

	
	@Test
	public void shouldPopulateProcessing() {
		
		final OrderModel orderSource = spy(OrderModel.class);
		given(sapS4OrderManagementConfigService.isS4SynchronousOrderHistoryEnabled()).willReturn(true);
		given(orderSource.getDeliveryStatus()).willReturn(DeliveryStatus.PARTSHIPPED);
		final OrderHistoryData orderHistoryData = new OrderHistoryData();
		sapS4OrderManagementHistoryPopulator.populate(orderSource, orderHistoryData);
		Assert.assertEquals(PROCESSING,orderHistoryData.getStatusDisplay());
		
	}
	
	@Test
	public void shouldPopulateOrderStatus() {
		
		final OrderModel orderSource = spy(OrderModel.class);
		given(sapS4OrderManagementConfigService.isS4SynchronousOrderHistoryEnabled()).willReturn(true);
		given(orderSource.getDeliveryStatus()).willReturn(DeliveryStatus.SHIPPED);
		given(orderSource.getStatus()).willReturn(OrderStatus.COMPLETED);
		final OrderHistoryData orderHistoryData = new OrderHistoryData();
		sapS4OrderManagementHistoryPopulator.populate(orderSource, orderHistoryData);
		Assert.assertEquals(OrderStatus.COMPLETED.toString().toLowerCase(Locale.ENGLISH),orderHistoryData.getStatusDisplay());
		
	}
	
	@Test
	public void shouldNotPopulateOrderStatus() {
		
		final OrderModel orderSource = spy(OrderModel.class);
		given(sapS4OrderManagementConfigService.isS4SynchronousOrderHistoryEnabled()).willReturn(true);
		given(orderSource.getDeliveryStatus()).willReturn(DeliveryStatus.SHIPPED);
		given(orderSource.getStatus()).willReturn(null);
		final OrderHistoryData orderHistoryData = new OrderHistoryData();
		sapS4OrderManagementHistoryPopulator.populate(orderSource, orderHistoryData);
		Assert.assertNull(orderHistoryData.getStatusDisplay());
		
	}

}
