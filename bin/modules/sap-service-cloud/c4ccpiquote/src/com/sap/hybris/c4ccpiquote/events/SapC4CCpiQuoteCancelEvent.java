/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.hybris.c4ccpiquote.events;

import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.commerceservices.enums.QuoteUserType;
import de.hybris.platform.commerceservices.event.AbstractQuoteSubmitEvent;
import de.hybris.platform.core.model.order.QuoteModel;
import de.hybris.platform.core.model.user.UserModel;


public class SapC4CCpiQuoteCancelEvent extends AbstractQuoteSubmitEvent<BaseSiteModel>
{
	/**
	 * Default Constructor
	 *
	 * @param quote
	 * @param userModel
	 * @param quoteUserType
	 */
	public SapC4CCpiQuoteCancelEvent(final QuoteModel quote, final UserModel userModel, final QuoteUserType quoteUserType)
	{
		super(quote, userModel, quoteUserType);
	}
}
