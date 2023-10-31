/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.acceleratorservices.web.payment.validation;

import org.springframework.stereotype.Component;

@Component("hopMockUrlValidator")
public class HopMockUrlValidator extends AbstractMockUrlValidator
{
	protected static final String PAYMENT_RESPONSE_END_RELATIVE_URL = "/hop/response";

	protected String getPaymentResponseEndRelativeUrl()
	{
		return PAYMENT_RESPONSE_END_RELATIVE_URL;
	}

}
