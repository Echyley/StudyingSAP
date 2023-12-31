/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.saprevenuecloudorder.constants;

import java.time.format.DateTimeFormatter;


public class SaprevenuecloudorderConstants extends GeneratedSaprevenuecloudorderConstants
{
	public static final String EXTENSIONNAME = "saprevenuecloudorder";
	public static final String SAP_SUBSCRIPTION_CONFIRMATION_EVENT = "SapSubscriptionConfirmationEvent-";
	public static final String SAP_SUBSCRIPTION_SUBPROCESS = "sap-subscription-process";
	public static final String SUBSCRIPTION_ORDER_PATH_URL = "subscription.order.path.url";
	public static final String SUBSCRIPTIONITEM = "subscriptionItem";
	public static final String DATE_TIME_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
	public static final String CANCEL_SUBSCRIPTION_DATE_FORMAT = "yyyy-MM-dd";
	public static final String SUBSCRIPTION_BILL_DATE_FORMAT = "yyyy-MM-dd";
	public static final DateTimeFormatter ISO8601_FORMATTER = DateTimeFormatter.ofPattern(DATE_TIME_PATTERN);
	public static final String UTC = "UTC";
	public static final String DEFAULT_TIMEZONE = "saprevenuecloud.subscription.default.timezone";
	public static final String DEFAULT_PAYMENT_TYPE = "saprevenuecloud.subscription.default.paymenttype";
	public static final String CARD_PAYMENT_TYPE = "Payment Card";
	public static final String YYYY_MM_DD = "yyyy-MM-dd";
	public static final String CANCELLATION_REASON = "saprevenuecloud.subscription.cancellation.reason";
	public static final String SUBSCRIPTION_ORDER_IFLOW_KEY =  "saprevenuecloudorder.cloudplatformintegration.iflowkey";
	public static final String CALENDAR_MONTHLY ="calendar_monthly";
	public static final String CALENDAR_QUARTERLY = "calendar_quarterly";
	public static final String CALENDAR_HALF_YEARLY ="calendar_half_yearly";
	public static final String CALENDAR_YEARLY ="calendar_yearly";
	public static final String ANNIVERSARY_MONTHLY ="anniversary_monthly";
	public static final String ANNIVERSARY_QUARTERLY ="anniversary_quarterly";
	public static final String ANNIVERSARY_HALF_YEARLY ="anniversary_half_yearly";
	public static final String ANNIVERSARY_YEARLY ="anniversary_yearly";
    public static final String INVOICE_PAYMENT_TYPE = "Invoice";

	public static final String RESPONSE_SUCCESS ="success";
	public static final String RESPONSE_FAILED ="failed";


	//Payment authorization target system constants
	public static final String PAYMENT_AUTH_SPLIT_TARGET_S4HANA = "S4HANA";
	public static final String PAYMENT_AUTH_SPLIT_TARGET_REVENUE_CLOUD = "REVENUE_CLOUD";
	

	private SaprevenuecloudorderConstants()
	{
		//empty
	}


}
