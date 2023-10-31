/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.orderexchange.cancellation;

import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.ordercancel.OrderCancelPaymentServiceAdapter;


/**
 * This class provides a payment mock to be able to do an order cancellation
 */
public class OrderCancelPaymentServiceAdapterMock implements OrderCancelPaymentServiceAdapter
{

	@Override
	public void recalculateOrderAndModifyPayments(final OrderModel order)
	{
		// The mock does nothing, just allows to proceed in the process
	}

}
