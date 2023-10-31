/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.hybris.c4ccpiquote.inbound.helper.impl;

import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.sap.orderexchange.datahub.inbound.impl.DefaultDataHubInboundOrderHelper;


/**
 *
 */
public class DefaultC4CCpiInboundQuoteOrderedHelper extends DefaultDataHubInboundOrderHelper
{

	@Override
	public void processOrderConfirmationFromHub(final String orderNumber)
	{
		super.processOrderConfirmationFromHub(orderNumber);

		final OrderModel order = readOrder(orderNumber);
		if (order != null && order.getQuoteReference() != null && order.getQuoteReference().getCode() != null)
		{
			final String eventName = "ERPOrderConfirmationEventForC4CQuote_"
					+ order.getQuoteReference().getCode();
			getBusinessProcessService().triggerEvent(eventName);
		}
	}


}
