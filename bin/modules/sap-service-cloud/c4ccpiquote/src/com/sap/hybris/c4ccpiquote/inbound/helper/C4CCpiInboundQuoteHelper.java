/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.hybris.c4ccpiquote.inbound.helper;

import de.hybris.platform.core.model.order.QuoteModel;


/**
 *
 */
public interface C4CCpiInboundQuoteHelper
{

	QuoteModel processSalesInboundQuote(QuoteModel inbounbQuote);

}
