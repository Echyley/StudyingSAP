/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.orderexchange.constants;

/**
 * Constants for Data Hub Inbound
 */
public class DataHubInboundConstants
{
	private DataHubInboundConstants() {
		throw new IllegalStateException("Utility class");
	}

	
	public static final String CODE = "code";

	
	public static final String IGNORE = "<ignore>";

	
	public static final String DATA_HUB_ORDER_IMPORT = "dataHubOrderImport";

	
	public static final String CONSIGNMENT_CREATION_EVENTNAME_PREFIX = "ConsignmentCreationEvent_";
	
	
	public static final String GOODS_ISSUE_EVENTNAME_PREFIX = "GoodsIssueEvent_";

	
	public static final String ERP_ORDER_CONFIRMATION_EVENT = "ERPOrderConfirmationEvent_";

	
	public static final String IDOC_DATE_FORMAT = "yyyyMMdd";

	
	public static final String DATE_NOT_SET = "00000000";

	
	public static final String POUND_SIGN = "#";
	
	
	public static final String UNDERSCORE = "_";
}
