/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.sapcentralorderfacades.constants;

/**
 * DefaultCentralOrderEntryCsvColumns
 */
public final class DefaultCentralOrderEntryCsvColumns
{

	@SuppressWarnings("javadoc")
	public static final String ORDER_ID = "orderId";

	@SuppressWarnings("javadoc")
	public static final String ORDER_ENTRY_ID = "orderEntryId";

	@SuppressWarnings("javadoc")
	public static final String SITE_ID = "siteId";

	@SuppressWarnings("javadoc")
	public static final String SCHEDULE_LINES = "scheduleLines";

	@SuppressWarnings("javadoc")
	public static final String VENDOR_ITEM_CATEGORY = "vendorItemCategory";

	public static final String CONSIGNMENT_SUBPROCESS_NAME = "consignment-process";
	public static final String CONSIGNMENT_ACTION_EVENT_NAME = "ConsignmentActionEvent";
	public static final String CONSIGNMENT_PICKUP = "ConsignmentPickup";

	private DefaultCentralOrderEntryCsvColumns()
	{
		// private constructor to avoid instantiation
	}
}
