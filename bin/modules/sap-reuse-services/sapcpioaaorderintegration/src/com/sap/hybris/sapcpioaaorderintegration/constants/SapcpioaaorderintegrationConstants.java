/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.hybris.sapcpioaaorderintegration.constants;

/**
 *
 */

@SuppressWarnings("PMD")
public class SapcpioaaorderintegrationConstants extends GeneratedSapcpioaaorderintegrationConstants //NOSONAR
{

	public static final String EXTENSIONNAME = "sapcpioaaorderintegration";
	public static final String ACTION_COMPLETION_EVENT_NAME = "ActionCompletionEvent";
	public static final String ORDER_ACTION_EVENT_NAME = "OrderActionEvent";
	public static final String ORDER_PROCESS_NAME = "order-process";
	public static final String CONSIGNMENT_SUBPROCESS_NAME = "consignment-process";
	public static final String CONSIGNMENT_ACTION_EVENT_NAME = "ConsignmentActionEvent";
	public static final String CONSIGNMENT_PICKUP = "ConsignmentPickup";
	public static final String PROPERTY_SAPOAA_VENDOR_ITEM_CATEGORY = "sapoaa_vendorItemCategory";

	public static final String CONSIGNMENT_SUBMISSION_CONFIRMATION_EVENT = "_ConsignmentSubmissionConfirmationEvent";
	public static final String CONSIGNMENT_ACTION_EVENT = "_ConsignmentActionEvent";
	public static final String PACK_CONSIGNMENT = "packConsignment";
	public static final String CANCEL_CONSIGNMENT = "cancelConsignment";
	public static final String CONFIRM_SHIP_CONSIGNMENT = "confirmShipConsignment";
	public static final String OAA_ORDER_IN_USE_FLAG= "true";


	private SapcpioaaorderintegrationConstants()
	{
		// private constructor to avoid instantiation
	}
}
