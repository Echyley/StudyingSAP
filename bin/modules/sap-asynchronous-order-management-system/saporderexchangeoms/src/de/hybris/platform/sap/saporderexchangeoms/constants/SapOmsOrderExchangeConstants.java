/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.saporderexchangeoms.constants;

/**
 * Global class for all SAP OMS data hub inbound constants
 */
public class SapOmsOrderExchangeConstants
{

	private SapOmsOrderExchangeConstants() {
		throw new IllegalStateException("Utility class");
	}

	
	public static final String CONSIGNMENT_ACTION_EVENT_NAME = "SapConsignmentActionEvent";

	
	public static final String CONSIGNMENT_PROCESS_CANCELLED = "consignmentProcessCancelled";

	
	public static final String CONSIGNMENT_DELIVERY_CONFIRMED = "consignmentDeliveryConfirmed";
	
	
	public static final String INTERNAl_VENDOR = "ERP";

	
	public static final String VENDOR_ITEM_CATEGORY = "TAB";

	
	public static final String ERROR_END_MESSAGE = "Sending to ERP went wrong.";

	
	public static final String SAP_CONS = "_sap_cons_";

	
	public static final String CONSIGNMENT_SUBPROCESS_NAME = "sap-oms-consignment-process";

	
	public static final String MISSING_LOG_SYS = "MISSING_LOGICAL_SYSTEM";

	
	public static final String MISSING_SALES_ORG = "MISSING_SALES_ORG";

	
	public static final int DEFAULT_MAX_RETRIES = 10;

	
	public static final int DEFAULT_RETRY_DELAY = 60 * 1000; // 60,000 milliseconds = 1 minute

}
