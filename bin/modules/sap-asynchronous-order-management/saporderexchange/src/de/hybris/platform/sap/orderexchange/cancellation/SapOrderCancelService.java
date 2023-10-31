/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.orderexchange.cancellation;

import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.ordercancel.OrderCancelException;


/**
 * This interface provides methods to cancel a hybris order and to restore an order status if cancellation failed
 *
 */
public interface SapOrderCancelService
{

	/**
	 * Cancel a hybris order,
	 *
	 * @param order
	 *           the order to be cancelled
	 * @param erpRejectionReason
	 *           rejection reason coming from ERP
	 * @throws OrderCancelException
	 * 			Exception thrown when cancelling an order fails
	 */
	void cancelOrder(OrderModel order, String erpRejectionReason) throws OrderCancelException;

	/**
	 * restore the order status after a failed cancel request
	 *
	 * @param order
	 * 			the order to be cancelled
	 * @throws OrderCancelException
	 * 			Exception thrown when cancelling an order fails
	 */
	void restoreAfterCancelFailed(OrderModel order) throws OrderCancelException;

}