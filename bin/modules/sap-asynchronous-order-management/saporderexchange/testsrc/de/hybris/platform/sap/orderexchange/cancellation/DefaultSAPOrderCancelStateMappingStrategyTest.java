/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.orderexchange.cancellation;


import static org.mockito.Mockito.mock;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.basecommerce.enums.OrderCancelState;
import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.ordersplitting.model.ConsignmentModel;

import java.util.HashSet;
import java.util.Set;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


@SuppressWarnings("javadoc")
@UnitTest
public class DefaultSAPOrderCancelStateMappingStrategyTest
{
	private final DefaultSAPOrderCancelStateMappingStrategy classUnderTest = new DefaultSAPOrderCancelStateMappingStrategy();
	private final OrderModel orderModel = new OrderModel();

	@Before
	public void setUp()
	{
		orderModel.setCode("0815");
	}

	@Test
	public void testOrderStateCancelled() throws Exception
	{
		orderModel.setStatus(OrderStatus.CANCELLED);
		Assert.assertEquals(OrderCancelState.CANCELIMPOSSIBLE, classUnderTest.getOrderCancelState(orderModel));
	}

	@Test
	public void testOrderStateCompleted() throws Exception
	{
		orderModel.setStatus(OrderStatus.COMPLETED);
		Assert.assertEquals(OrderCancelState.CANCELIMPOSSIBLE, classUnderTest.getOrderCancelState(orderModel));
	}

	@Test
	public void testOrderStateCancelling() throws Exception
	{
		orderModel.setStatus(OrderStatus.CANCELLING);
		Assert.assertEquals(OrderCancelState.SENTTOWAREHOUSE, classUnderTest.getOrderCancelState(orderModel));
	}

	@Test
	public void testOrderStatePending() throws Exception
	{
		orderModel.setStatus(OrderStatus.CHECKED_VALID);
		Assert.assertEquals(OrderCancelState.SENTTOWAREHOUSE, classUnderTest.getOrderCancelState(orderModel));
	}

	@Test
	public void testOrderWithConsignment()
	{
		final ConsignmentModel consignment = mock(ConsignmentModel.class);
		final Set<ConsignmentModel> consignmentSet = new HashSet<ConsignmentModel>();

		consignmentSet.add(consignment);
		orderModel.setStatus(OrderStatus.CHECKED_VALID);
		orderModel.setConsignments(consignmentSet);

		Assert.assertEquals(OrderCancelState.CANCELIMPOSSIBLE, classUnderTest.getOrderCancelState(orderModel));
	}
}
