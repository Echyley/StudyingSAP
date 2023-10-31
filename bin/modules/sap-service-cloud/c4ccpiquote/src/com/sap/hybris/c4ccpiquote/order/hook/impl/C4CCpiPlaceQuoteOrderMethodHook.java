/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.hybris.c4ccpiquote.order.hook.impl;

import static com.sap.hybris.c4ccpiquote.constants.C4ccpiquoteConstants.QUOTE_SALES_ORDER_NOTIFICATION_DESTINATION_ID;

import org.apache.log4j.Logger;

import com.sap.hybris.c4ccpiquote.events.SapC4CCpiQuoteBuyerOrderPlacedEvent;
import com.sap.hybris.c4ccpiquote.service.C4CConsumedDestinationService;

import de.hybris.platform.commerceservices.order.hook.impl.CommercePlaceQuoteOrderMethodHook;
import de.hybris.platform.commerceservices.service.data.CommerceCheckoutParameter;
import de.hybris.platform.commerceservices.service.data.CommerceOrderResult;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.order.QuoteModel;
import de.hybris.platform.servicelayer.util.ServicesUtil;


public class C4CCpiPlaceQuoteOrderMethodHook extends CommercePlaceQuoteOrderMethodHook
{

	private static final Logger LOG = Logger.getLogger(C4CCpiPlaceQuoteOrderMethodHook.class);
	private C4CConsumedDestinationService c4CConsumedDestinationService;
	
	@Override
	public void afterPlaceOrder(final CommerceCheckoutParameter commerceCheckoutParameter,
			final CommerceOrderResult commerceOrderResult)
	{
		if (!getC4CConsumedDestinationService().checkIfDestinationExists(QUOTE_SALES_ORDER_NOTIFICATION_DESTINATION_ID)){
            super.afterPlaceOrder(commerceCheckoutParameter,commerceOrderResult);
            return;
        }
        
		final OrderModel order = commerceOrderResult.getOrder();
		ServicesUtil.validateParameterNotNullStandardMessage("order", order);

		// Set quote state for quote order
		final QuoteModel quoteModel = order.getQuoteReference();
		if (quoteModel != null)
		{
			if (LOG.isDebugEnabled())
			{
				LOG.debug(String.format("Quote Order has been placed. Quote Code : [%s] , Order Code : [%s]", quoteModel.getCode(),
						order.getCode()));
			}
			final SapC4CCpiQuoteBuyerOrderPlacedEvent quoteBuyerOrderPlacedEvent = new SapC4CCpiQuoteBuyerOrderPlacedEvent(order,
					quoteModel);
			getEventService().publishEvent(quoteBuyerOrderPlacedEvent);
		}
	}

	public C4CConsumedDestinationService getC4CConsumedDestinationService() {
		return c4CConsumedDestinationService;
	}

	public void setC4CConsumedDestinationService(C4CConsumedDestinationService c4cConsumedDestinationService) {
		c4CConsumedDestinationService = c4cConsumedDestinationService;
	}

}
