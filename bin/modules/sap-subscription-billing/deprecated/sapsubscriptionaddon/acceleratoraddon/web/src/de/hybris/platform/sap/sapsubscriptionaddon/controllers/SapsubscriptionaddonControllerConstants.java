/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.sapsubscriptionaddon.controllers;

/**
 */
public final class SapsubscriptionaddonControllerConstants
{
	
	private SapsubscriptionaddonControllerConstants() {
		
	}
	
	public static final String ADDON_PREFIX = "addon:/sapsubscriptionaddon/";

	public static final String CANCEL_SUBSCRIPTION_POPUP = ADDON_PREFIX + "fragments/account/cancelSubscriptionPopup";
	public static final String EXTEND_SUBSCRIPTION_POPUP = ADDON_PREFIX + "fragments/account/extendSubscriptionPopup";
	public static final String WITHDRAW_SUBSCRIPTION_POPUP = ADDON_PREFIX + "fragments/account/withdrawSubscriptionPopup";
	public static final String SUBSCRIPTION_VIEW_CHARGE_POPUP = ADDON_PREFIX + "fragments/account/subscriptionViewChargePopup";
}
