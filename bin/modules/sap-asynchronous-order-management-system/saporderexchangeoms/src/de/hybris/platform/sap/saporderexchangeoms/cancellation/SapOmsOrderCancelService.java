/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.saporderexchangeoms.cancellation;

import de.hybris.platform.basecommerce.enums.CancelReason;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.ordercancel.OrderCancelException;
import de.hybris.platform.ordercancel.OrderCancelResponse;
import de.hybris.platform.ordercancel.OrderCancelResponse.ResponseStatus;
import de.hybris.platform.sap.orderexchange.cancellation.DefaultSapOrderCancelService;

import org.apache.log4j.Logger;

/**
 * Extension of DefaultSapOrderCancelService to provide OMS integration functionality
 */
public class SapOmsOrderCancelService extends DefaultSapOrderCancelService
{
	private final static Logger LOG = Logger.getLogger(SapOmsOrderCancelService.class);

	@Override
	public void cancelOrder(final OrderModel order, final String erpRejectionReason) throws OrderCancelException
	{
		final OrderCancelResponse cancelResponse = new OrderCancelResponse(order, ResponseStatus.full, "");
		cancelResponse.setCancelReason(CancelReason.valueOf(erpRejectionReason));
		createOrderCancelEntryIfNecessary(order, cancelResponse);
		getOrderCancelCallbackService().onOrderCancelResponse(cancelResponse);
	}

	@Override
	protected void createOrderCancelEntryIfNecessary(final OrderModel order, final OrderCancelResponse cancelResponse)
			throws OrderCancelException
	{

		if (getOrderCancelService().getCancelRecordForOrder(order) == null)
		{
			LOG.error(String
					.format(
							"The order %s cancellation from ERP is not supported in the multiple backed scenario!",
							order.getCode()));
		}

	}


}
