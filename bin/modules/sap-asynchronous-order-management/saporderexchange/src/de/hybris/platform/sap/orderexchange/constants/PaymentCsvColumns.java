/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.orderexchange.constants;

/**
 * Constants for Payment CSV Columns
 */
public class PaymentCsvColumns
{
	private PaymentCsvColumns() {
		throw new IllegalStateException("Utility class");
	}

	
	public static final String REQUEST_ID = "requestId";

	
	public static final String PAYMENT_PROVIDER = "paymentProvider";

	
	public static final String SUBSCRIPTION_ID = "subscriptionId";

	
	public static final String VALID_TO_YEAR = "validToYear";

	
	public static final String VALID_TO_MONTH = "validToMonth";

	
	public static final String CC_OWNER = "ccOwner";
}
