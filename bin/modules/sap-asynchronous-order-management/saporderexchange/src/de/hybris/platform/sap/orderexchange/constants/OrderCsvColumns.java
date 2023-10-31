/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.orderexchange.constants;


/**
 * Constants for Order CSV Columns
 */
public class OrderCsvColumns
{
	private OrderCsvColumns() {
		throw new IllegalStateException("Utility class");
	}

	//	order
	
	public static final String DATE = "date";

	
	public static final String DELIVERY_MODE = "deliveryMode";

	
	public static final String PAYMENT_MODE = "paymentMode";

	
	public static final String ORDER_CURRENCY_ISO_CODE = "orderCurrencyIsoCode";
	
	
	public static final String LOGICAL_SYSTEM = "logicalSystem";
	
	
	public static final String SALES_ORGANIZATION = "salesOrganization";
	
	
	public static final String DISTRIBUTION_CHANNEL = "distributionChannel";
	
	
	public static final String DIVISION = "division";


	//	general columns
	
	public static final String ORDER_ID = "orderId";

	
	public static final String PURCHASE_ORDER_NUMBER = "purchaseOrderNumber";

	
	public static final String BASE_STORE = "baseStore";

	
	public static final String CHANNEL = "channel";
	
	
}
