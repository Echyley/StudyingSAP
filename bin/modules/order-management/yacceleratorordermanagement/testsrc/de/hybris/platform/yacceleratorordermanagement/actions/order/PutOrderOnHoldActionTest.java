/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.yacceleratorordermanagement.actions.order;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.orderprocessing.model.OrderProcessModel;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.warehousing.onhold.service.OrderOnHoldService;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class PutOrderOnHoldActionTest
{
	@InjectMocks
	private PutOrderOnHoldAction putOrderOnHoldAction;
	@Mock
	private OrderOnHoldService orderOnHoldService;
	@Mock
	private ModelService modelService;

	private OrderProcessModel orderProcessModel;
	private OrderModel orderModel;

	@Before
	public void setup()
	{
		orderModel = new OrderModel();
		orderProcessModel = new OrderProcessModel();
		orderProcessModel.setOrder(orderModel);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testExecuteActionWhenOrderProcessIsNull() throws Exception
	{
		putOrderOnHoldAction.executeAction(null);
	}

	@Test
	public void testExecuteActionSuccess() throws Exception
	{
		putOrderOnHoldAction.executeAction(orderProcessModel);

		assertEquals(OrderStatus.ON_HOLD.toString(), orderModel.getStatus().toString());
	}
}
