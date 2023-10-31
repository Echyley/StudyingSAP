/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.hybris.c4ccpiquote.outbound.service;

import de.hybris.platform.core.model.order.QuoteModel;

import java.util.Map;

import org.springframework.http.ResponseEntity;

import com.sap.hybris.c4ccpiquote.model.C4CSalesOrderNotificationModel;
import com.sap.hybris.c4ccpiquote.model.SAPC4CCpiOutboundQuoteModel;

import rx.Observable;


/**
 * Interface to handle Outbound from commerce to c4c
 */
public interface SapCpiOutboundC4CQuoteService
{


	/**
	 * Send Quote to SCPI
	 *
	 * @param sapC4CCpiOutboundQuoteModel used to send quote data as payload
	 * @return Observable<ResponseEntity<Map>>
	 */
	Observable<ResponseEntity<Map>> sendQuote(SAPC4CCpiOutboundQuoteModel sapC4CCpiOutboundQuoteModel);

	/**
	 * Send Order Notification to SCPI
	 *
	 * @param c4cSalesOrderNotificationModel used to send order data as payload
	 * @return Observable<ResponseEntity<Map>>
	 */
	Observable<ResponseEntity<Map>> sendOrderNotification(C4CSalesOrderNotificationModel c4cSalesOrderNotificationModel);

}
