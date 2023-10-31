/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.gigya.gigyaloginaddon.controllers;

public final class ControllerConstants {
	
	public static final String ADDON_PREFIX = "addon:/gigyaloginaddon/";

	public static final String GLT_COOKIE = "glt_";

	public static final String GLT_EXP_COOKIE = "gltexp_";

	public static final String LOGION_TOKEN = "-LoginToken";
	
	public static final String SAME_SITE_ATTRIBUTE_LAX = "; SameSite=Lax";
	
	public static final String GLT_EXP_COOKIE_SAMESITE_ATTR_VAL="gigyaloginaddon.gltexp.cookie.samesite.value";
	
	
	public static final String VIEWS_PAGES_ACCOUNT_ACCOUNTLOGINPAGE = ADDON_PREFIX + "pages/account/accountLoginPage";
	
	public static final String CHECKOUT_CHECKOUTLOGINPAGE = ADDON_PREFIX + "pages/checkout/checkoutLoginPage";
	
	public static final String FRAGMENTS_CHECKOUT_TERMSANDCONDITIONSPOPUP = "fragments/checkout/termsAndConditionsPopup";
	
	private ControllerConstants() {
		throw new IllegalStateException("Constants class, Cannot be instantiated");
	}
}
