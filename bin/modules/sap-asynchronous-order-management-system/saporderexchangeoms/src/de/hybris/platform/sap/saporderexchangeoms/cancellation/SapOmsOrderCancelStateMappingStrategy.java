/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.saporderexchangeoms.cancellation;

import de.hybris.platform.basecommerce.enums.ConsignmentStatus;
import de.hybris.platform.basecommerce.enums.OrderCancelState;
import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.ordercancel.impl.DefaultOrderCancelStateMappingStrategy;


public class SapOmsOrderCancelStateMappingStrategy extends DefaultOrderCancelStateMappingStrategy
{

	@Override
	public OrderCancelState getOrderCancelState(final OrderModel order)
	{
		final boolean isConsignmentCancellable = order.getConsignments().stream()
				.noneMatch(consignment -> //
						consignment.getStatus().equals(ConsignmentStatus.PICKPACK) ||
								consignment.getStatus().equals(ConsignmentStatus.SHIPPED) ||
								consignment.getStatus().equals(ConsignmentStatus.CANCELLED));

		final boolean isOrderCancellable = !(OrderStatus.CANCELLED.equals(order.getStatus())
				|| OrderStatus.COMPLETED.equals(order.getStatus()));

		return isOrderCancellable && isConsignmentCancellable ? OrderCancelState.SENTTOWAREHOUSE
				: OrderCancelState.CANCELIMPOSSIBLE;

	}
}
