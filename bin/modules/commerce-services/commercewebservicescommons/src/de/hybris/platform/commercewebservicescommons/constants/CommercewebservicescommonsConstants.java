/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.commercewebservicescommons.constants;

/**
 * Global class for all Commercewebservicescommons constants. You can add global constants for your extension into this
 * class.
 */
public final class CommercewebservicescommonsConstants extends GeneratedCommercewebservicescommonsConstants
{
	public static final String EXTENSIONNAME = "commercewebservicescommons";

	public static final String ANONYMOUS_CONSENT_HEADER = "X-Anonymous-Consents";

	public static final String CAPTCHA_TOKEN_HEADER = "sap-commerce-cloud-captcha-token";

	public static final String CAPTCHA_TOKEN_HEADER_DESC = "The user's response token returned by captcha provider, for example, the g-recaptcha-response returned by google recaptcha client. When the captchaCheckEnabled = true for the store, the field is mandatory and will call the provider to validate.";

	private CommercewebservicescommonsConstants()
	{
		//empty to avoid instantiating this constant class
	}
}
