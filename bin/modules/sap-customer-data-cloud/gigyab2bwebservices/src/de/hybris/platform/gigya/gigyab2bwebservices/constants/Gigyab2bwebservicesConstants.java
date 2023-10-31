/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.gigya.gigyab2bwebservices.constants;

/**
 * Global class for all Gigyab2bwebservices constants. You can add global
 * constants for your extension into this class.
 */
public final class Gigyab2bwebservicesConstants
{
	public static final String EXTENSIONNAME = "gigyab2bwebservices";
	public static final String AUTHORIZATION_SCOPE_PROPERTY = EXTENSIONNAME + ".oauth.scope";
	public static final String LICENSE_URL_PROPERTY = EXTENSIONNAME + ".license.url";
	public static final String TERMS_OF_SERVICE_URL_PROPERTY = EXTENSIONNAME + ".terms.of.service.url";
	public static final String LICENSE_PROPERTY = EXTENSIONNAME + ".licence";
	public static final String DOCUMENTATION_DESC_PROPERTY = EXTENSIONNAME + ".documentation.desc";
	public static final String DOCUMENTATION_TITLE_PROPERTY = EXTENSIONNAME + ".documentation.title";
	public static final String API_VERSION = "1.0.0";

	public static final String AUTHORIZATION_URL = "https://localhost:9002/authorizationserver/oauth/token";

	public static final String PSWD_AUTHORIZATION_NAME = "oauth2_pswd";
	public static final String CLIENT_CREDENTIAL_AUTHORIZATION_NAME = "oauth2_client_credentials";

	public static final String SAMPLE_MAP_STRING_KEY = "StringKey";
	public static final String SAMPLE_MAP_STRING_VALUE = "StringValue";
	public static final String SAMPLE_MAP_INTEGER_KEY = "integerKey";
	public static final int SAMPLE_MAP_INTEGER_VALUE = 10001;

	public static final String SAMPLE_LIST_STRING_VALUE = "new String";
	public static final double SAMPLE_LIST_DOUBLE_VALUE = 0.123d;

	public static final String HOST = "gigyab2bwebservices.host";

	public static final String SUCCESS = "success";
	public static final String SUCCESS_MESSAGE = "Gigya Webhook Event(s) received successfully.";
	public static final String ERROR = "error";
	public static final String ERROR_MESSAGE = "There were errors in processing the Event(s).";
	public static final String GIGYA_JWT_HEADER = "x-gigya-sig-jwt";

	private Gigyab2bwebservicesConstants()
	{
		// empty to avoid instantiating this constant class
	}

	// implement here constants used by this extension
}
