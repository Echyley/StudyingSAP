/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.sapcentralorderservices.constants;

@SuppressWarnings(
{ "deprecation", "squid:CallToDeprecatedMethod" })
public class SapcentralorderservicesConstants extends GeneratedSapcentralorderservicesConstants
{
	public static final String EXTENSIONNAME = "sapcentralorderservices";

	public static final String SLASH = "/";
	public static final String CENTRALORDERSERVICEDEST = "centralOrderServiceDest";
	public static final String CENTRALORDERSERVICEDESTTARGET = "centralOrderDestTarget";
	public static final String DEFAULT_SORT_TYPE = "precedingDocumentNumber";
	public static final String CUSTOMER_ID = "customerCuid";
	public static final String SORT = "sort";
	public static final String SORT_DIRETION = "ASC";
	public static final String PAGE = "page";
	public static final String SIZE = "size";
	public static final int DATE_SIZE = 10;
	public static final String ORDER_LIST_COUNT_HEADER = "C4hordermgmt-Count";
	public static final String DEFAULT_DATE_FORMAT = "dd/MM/yyyy";
	public static final String DATE_FORMAT = "yyyy-MM-dd";
	public static final String EMPTY_STRING = "";
	public static final String DOCUMENT_NUMBER = "precedingDocumentNumber";
	public static final String SOURCE_SYSTEM_ID = "precedingDocumentSystemId";
	public static final String ORDER_NOT_FOUND_FOR_USER_AND_BASE_STORE = "Order with id %s not found for current user in Central Order";
	public static final String ORDER_NOT_FOUND_FOR_USER = "No Order found for current user in Central Order";


	public static final String CHECKOUTCART_NOT_FOUND_MESSAGE = "Valid Checkout Cart not found.";
	public static final String OAA_CHECKOUT_ERROR = "Error when calling sourcing web service.";

	public static final String DELIVERYMODE_NOT_AVAILABLE_MESSAGE = "You must provide a delivery option in order to go to the payment step";
	public static final String DELIVERYADDRESS_NOT_AVAILABLE_MESSAGE = "You must provide a delivery address in order to go to the payment step.";
	public static final String PAYMENTDETAILS_NOT_AVAILABLE_MESSAGE = "You must provide a payment details in order to go to the next step.";

	public static final String CO_SOURCE_SYSTEM_ID = "sapco_sourceSystemId";
	public static final String CO_ACTIVE = "sapco_active";

	public static final String PAYMENT_TYPE_CARD = "Payment Card";
	public static final String PAYMENT_TYPE_INVOICE = "Invoice";

	public static final String ADDRESS_TYPE_BILLTO = "BILL_TO";
	public static final String ADDRESS_TYPE_SHIPTO = "SHIP_TO";
	public static final String CATEGORY_ONETIME = "onetime";



	public static final String CONSIGNMENT_SUBMISSION_CONFIRMATION_EVENT = "_ConsignmentSubmissionConfirmationEvent";
	public static final String CONSIGNMENT_ACTION_EVENT = "_ConsignmentActionEvent";
	public static final String PACK_CONSIGNMENT = "packConsignment";
	public static final String CANCEL_CONSIGNMENT = "cancelConsignment";

	public static final String CONFIRM_SHIP_CONSIGNMENT = "confirmShipConsignment";

	public static final String OUTBOUND_ORDER_OBJECT = "OutboundOMMOrderOMSOrder";
	public static final String OUTBOUND_CENTRAL_ORDER_DESTINATION = "scpiCentralOrderDestination";
	public static final String COMMA = ",";



	private SapcentralorderservicesConstants()
	{
		//empty
	}


}
