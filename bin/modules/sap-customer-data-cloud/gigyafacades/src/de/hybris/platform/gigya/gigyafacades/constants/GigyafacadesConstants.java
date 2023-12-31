/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.gigya.gigyafacades.constants;

/**
 * Global class for all Gigyafacades constants. You can add global constants for
 * your extension into this class.
 */
public final class GigyafacadesConstants
{
	public static final String EXTENSIONNAME = "gigyafacades";

	// implement here constants used by this extension

	public static final String PLATFORM_LOGO_CODE = "gigyafacadesPlatformLogo";
	public static final String UTF8 = "UTF-8";

	public static final String GRANT_TYPE = "custom";
	public static final String UID_PARAM = "UID";
	public static final String BASE_SITE_PARAM = "baseSite";
	public static final String UIDSIGNATURE_PARAM = "UIDSignature";
	public static final String TIMESTAMP_PARAM = "signatureTimestamp";
	public static final String IDTOKEN_PARAM = "idToken";
	
	private GigyafacadesConstants()
	{
		// empty to avoid instantiating this constant class
	}
}
