/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.orderexchange.constants;

/**
 * Constants for Order Entry CSV Columns
 */
public class OrderEntryCsvColumns
{
	private OrderEntryCsvColumns() {
		throw new IllegalStateException("Utility class");
	}

	//           order entry
	
	public static final String QUANTITY = "quantity";

	
	public static final String NAMED_DELIVERY_DATE = "namedDeliveryDate";

	
	public static final String ENTRY_UNIT_CODE = "entryUnitCode";

	
	public static final String PRODUCT_NAME = "productName";

	
	public static final String EXTERNAL_PRODUCT_CONFIGURATION = "externalConfiguration";


	// general columns
	
	public static final String ENTRY_NUMBER = "entryNumber";

	
	public static final String REJECTION_REASON = "rejectionReason";

	
	public static final String PRODUCT_CODE = "productCode";

	
	public static final String WAREHOUSE = "warehouse";

	
	public static final String EXPECTED_SHIPPING_DATE = "shippingDate";
	
	
	public static final String ITEM_CATEGORY = "itemCategory";

}
