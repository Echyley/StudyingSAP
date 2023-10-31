/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.customerticketingc4cintegration.constants;

import de.hybris.platform.util.Config;


/**
 * Global class for all Customerticketingc4cintegration constants. You can add global constants for your extension into
 * this class.
 */
public final class Customerticketingc4cintegrationConstants extends GeneratedCustomerticketingc4cintegrationConstants
{
	public static final String EXTENSIONNAME = "customerticketingc4cintegration";
	public static final String RESPONSE_COOKIE_NAME = "set-cookie";
	public static final String TICKETID_OBJECTID_MAP = "ticketId-objectId-map";

	public static final String ACCEPT = Config.getParameter("customerticketingc4cintegration.c4c-accept");
	public static final String URL = Config.getString("customerticketingc4cintegration.c4c-url", "https://dummy-c4c-url.com");

	public static final String CREATE_MEMO_ACTIVITY_URL = Config
		.getString("customerticketingc4cintegration.c4c-create-memo-activity-url", "https://dummy-c4c-url.com");
	public static final String GET_MEMO_ACTIVITY_URL = Config
		.getString("customerticketingc4cintegration.c4c-get-memo-activity-url", "https://dummy-c4c-url.com");

	public static final String TICKETING_SUFFIX = Config.getParameter("customerticketingc4cintegration.c4c-ticket-suffix");
	public static final String INDIVIDUAL_CUSTOMER_SUFFIX = Config.getParameter("customerticketingc4cintegration.c4c-individual-customer-suffix");
	public static final String CONTACT_SUFFIX = Config.getParameter("customerticketingc4cintegration.c4c-contact-suffix");
	public static final String NOTE_SUFFIX = Config.getParameter("customerticketingc4cintegration.c4c-note-suffix");
	public static final String BATCH_SUFFIX = Config.getParameter("customerticketingc4cintegration.c4c-batch-suffix");

	public static final String PAGING_COUNT_SUFFIX = Config.getParameter("customerticketingc4cintegration.c4c-count-suffix");
	public static final String PAGING_SKIP_SUFFIX = Config.getParameter("customerticketingc4cintegration.c4c-paging-skip-suffix");
	public static final String PAGING_TOP_SUFFIX = Config.getParameter("customerticketingc4cintegration.c4c-paging-top-suffix");

	public static final String EXPAND_SUFFIX = Config.getParameter("customerticketingc4cintegration.c4c-expand-suffix");
	public static final String USERNAME = Config.getString("customerticketingc4cintegration.c4c-username", "username");
	public static final String PASSWORD = Config.getString("customerticketingc4cintegration.c4c-password", "password");
	public static final String SITE_HEADER = Config.getParameter("customerticketingc4cintegration.c4c-site-header");
	public static final String UPDATE_MESSAGE_SUFFIX = Config
			.getParameter("customerticketingc4cintegration.c4c-update-message-suffix");
	public static final String CREATE_MEMO_ACTIVITY_SUFFIX = Config
		.getParameter("customerticketingc4cintegration.c4c-create-memo-activity-suffix");
	public static final String MEMO_ACTIVITY_TICKET_SUFFIX = Config
		.getParameter("customerticketingc4cintegration.c4c-memo-activity-ticket-suffix");
	public static final String GET_MEMO_ACTIVITY_SUFFIX = Config
		.getParameter("customerticketingc4cintegration.c4c-get-memo-activity-suffix");
	public static final String RELATED_BUSINESS_TRANSACTION_SUFFIX = Config
		.getParameter("customerticketingc4cintegration.c4c-related-business-transaction-suffix");

	// only to get x-token
	public static final String TOKEN_URL_SUFFIX = Config.getParameter("customerticketingc4cintegration.c4c-token-url-suffix"); // it could be a param
	public static final String TOKEN_NAMING = Config.getParameter("customerticketingc4cintegration.c4c-token-naming");
	public static final String TOKEN_EMPTY = Config.getParameter("customerticketingc4cintegration.c4c-token-empty");

	// for populating
	public static final String TYPECODE_10004 = "10004";
	public static final String TYPECODE_10007 = "10007";
	public static final String LANGUAGE = "EN";
	public static final String B2B_EXTERNAL_CUSTOMER_ID = "";
	public static final String DATA_ORIGIN_TYPECODE = "4";
	public static final String RELATED_TRANSACTION_TYPECODE = "2085";
	public static final String ROLE_CODE = "1";
	public static final String FILETR_SUFFIX = "$filter";
	public static final String SELECT_SUFFIX = "$select";
	public static final String OBJECT_ID = "ObjectID";
	public static final String ORDER_BY_SUFFIX = "$orderby=";
	public static final String ORDER_DEFAULT_VALUE = "LastChangeDateTime";
	public static final String MEMO_ACTIVITY_ORDERBY_DEFAULT_VALUE = "CreationDateTime desc";

	//for populating - memo
	public static final String INITIATOR_CODE_CUSTOMER = "2";
	public static final String CONTACT_PERSON_PARTY_TYPE_CODE = "147";
	public static final String CONTACT_PERSON_ROLE_CODE = "59";
	public static final String INDUVIDUAL_CUSTOMER_ROLE_CODE = "34";
	public static final String INDUVIDUAL_CUSTOMER_PARTY_TYPE_CODE = "159";
	public static final String TEXT_TYPE_CODE = "10002";
	public static final String MEMO_ACTIVITY_ROLE_CODE = "1";
	public static final String MEMO_ACTIVITY_TYPE_CODE = "2574";
	public static final String MEMO_ACTIVITY_ORDERBY_VALUE = "CreationDateTime";

	// for multipart
	public static final String MULTIPART = "multipart";
	public static final String MIXED = "mixed";
	public static final String MULTIPART_MIXED_MODE = "multipart/mixed";
	public static final String MULTIPART_HAS_ERROR = "has_error";
	public static final String MULTIPART_ERROR_MESSAGE = "error_message";
	public static final String MULTIPART_ERROR_CODE = "error_code";
	public static final String MULTIPART_BODY = "body";
	public static final String CONTENT_ID = "Content-ID";
	public static final String CONTENT_ID_VALUE_PREFIX = "SRQ_TXT_";
	public static final String EMPTY_SUBJECT = "Subject can't be empty";
	public static final String SUBJECT_EXCEEDS_255_CHARS = "Subject can't be longer than 255 chars";
	public static final String EMPTY_MESSAGE = "Message can't be empty";

	public static final String SUPPORT_TICKET_DISPLAY_NAME = "customerticketingc4cintegration.displayname";
	public static final String SUPPORT_TICKET_AGENT_NAME = "text.account.supporttickets.customer.support";



	private Customerticketingc4cintegrationConstants()
	{
		//empty to avoid instantiating this constant class
	}

	// implement here constants used by this extension
}
