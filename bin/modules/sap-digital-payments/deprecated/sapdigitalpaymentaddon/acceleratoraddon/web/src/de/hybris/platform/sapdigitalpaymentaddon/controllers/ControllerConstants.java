/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sapdigitalpaymentaddon.controllers;

/**
 *
 * Controller constant class for sapdigitalpaymentaddon extension
 */
public final class ControllerConstants
{
	public static final String ADDON_PREFIX = "addon:/sapdigitalpaymentaddon/";

	public static final String ADD_PAYMENT_METHOD_PAGE = "pages/checkout/multi/addPaymentMethodPage";
	public static final String ADD_EDIT_BILLING_ADDRESS_PAGE = ADDON_PREFIX + "pages/checkout/multi/addEditBillingAddressPage";
	public static final String ADD_EDIT_CARD_DETAILS_PAGE = ADDON_PREFIX + "pages/checkout/multi/addEditCardDetailsPage";
	public static final String DIGITAL_PAYMENT_GENERAL_ERROR_PAGE = ADDON_PREFIX
			+ "pages/checkout/multi/digitalPaymentGeneralErrorPage";
	public static final String ACCOUNT_SAP_DIGITAL_PAYMENT_INFOPAGE = ADDON_PREFIX
			+ "pages/account/accountSapDigitalPaymentInfoPage";
	public static final String ERROR_NOT_FOUND_PAGE = "pages/error/errorNotFoundPage";

	private ControllerConstants()
	{
		super();
	}
}



