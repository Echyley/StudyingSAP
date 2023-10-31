/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.hybris.c4ccpiquote.outbound.service;

import com.sap.hybris.c4ccpiquote.model.C4CSalesOrderNotificationModel;
import com.sap.hybris.c4ccpiquote.model.SAPC4CCpiOutboundQuoteModel;

import de.hybris.platform.core.model.order.QuoteModel;

/**
 * Public interface used to handle conversion operations
 *
 */
public interface SapCpiOutboundC4CQuoteConversionService {

	/**
	 * Convert Quote to SCPI code
	 *
	 * @param quote parameter used to convert it into integration object
	 * @return SAPC4CCpiOutboundQuoteModel
	 *
	 *
	 */
	SAPC4CCpiOutboundQuoteModel convertQuoteToSapCpiQuote(QuoteModel quote);


	/**
	 * Convert Quote to Sales Order Notification
	 *
	 * @param order parameter used to convert it into integration object
	 * @return C4CSalesOrderNotificationModel
	 *
	 *
	 */
	C4CSalesOrderNotificationModel convertQuoteToSalesOrderNotification(QuoteModel order);
}
