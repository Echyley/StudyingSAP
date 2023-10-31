/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.yorderfulfillment.actions;

import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.orderprocessing.model.OrderProcessModel;
import de.hybris.platform.processengine.action.AbstractSimpleDecisionAction;


/**
 * sets Orderstatus after Business Process has been repaired by OrderCancelRepairJob.
 */
public class SetCancelAction extends AbstractSimpleDecisionAction<OrderProcessModel>
{
	@Override
	public Transition executeAction(final OrderProcessModel process)
	{
		final OrderModel orderModel = process.getOrder();
		orderModel.setStatus(OrderStatus.CANCELLED);
		modelService.save(orderModel);
		return Transition.OK;
	}
}
