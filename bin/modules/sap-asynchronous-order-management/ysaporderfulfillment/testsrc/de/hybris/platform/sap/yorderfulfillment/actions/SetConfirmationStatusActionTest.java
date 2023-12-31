/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.yorderfulfillment.actions;


import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.enums.DeliveryStatus;
import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.orderprocessing.model.OrderProcessModel;
import de.hybris.platform.processengine.action.AbstractSimpleDecisionAction.Transition;
import de.hybris.platform.servicelayer.model.MockModelService;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;


@SuppressWarnings("javadoc")
@UnitTest
public class SetConfirmationStatusActionTest
{
	private SetConfirmationStatusAction classUnderTest;

	class OrderModelForTest extends OrderModel
	{
		@Override
		public de.hybris.platform.core.PK getPk()
		{
			return de.hybris.platform.core.PK.fromLong(9999l);
		}
	}

	@Before
	public void setUp()
	{
		classUnderTest = new SetConfirmationStatusAction();
		classUnderTest.setModelService(new MockModelService());
	}

	@Test
	public void testExecuteAction() throws Exception
	{
		final OrderProcessModel process = new OrderProcessModel();
		final OrderModel order = new OrderModelForTest();
		process.setOrder(order);
		final Transition transition = classUnderTest.executeAction(process);
		Assert.assertEquals(Transition.OK, transition);
		final OrderStatus status = order.getStatus();
		Assert.assertEquals(OrderStatus.CREATED, status);
		Assert.assertEquals(DeliveryStatus.NOTSHIPPED, order.getDeliveryStatus());
	}
}
