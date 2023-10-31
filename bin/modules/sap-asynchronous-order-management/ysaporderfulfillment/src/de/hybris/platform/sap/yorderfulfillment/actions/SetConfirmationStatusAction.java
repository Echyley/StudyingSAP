/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.yorderfulfillment.actions;




import de.hybris.platform.core.enums.DeliveryStatus;
import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.orderprocessing.model.OrderProcessModel;
import de.hybris.platform.processengine.action.AbstractSimpleDecisionAction;



/**
 * Class for setting the Hybris order status after receiving an order confirmation from the SAP ERP backend .
 * 
 */
public class SetConfirmationStatusAction extends AbstractSimpleDecisionAction<OrderProcessModel>
{
	@Override
	public Transition executeAction(final OrderProcessModel process)
	{
		final OrderModel order = process.getOrder();
		order.setDeliveryStatus(DeliveryStatus.NOTSHIPPED);
		setOrderStatus(order, OrderStatus.CREATED);
		return Transition.OK;
	}
}
