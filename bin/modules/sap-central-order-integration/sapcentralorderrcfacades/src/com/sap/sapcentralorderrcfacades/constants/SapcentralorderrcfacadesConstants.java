/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.sapcentralorderrcfacades.constants;

import java.time.format.DateTimeFormatter;


/**
 * Global class for all Sapcentralorderrcfacades constants. You can add global constants for your extension into this class.
 */
@SuppressWarnings(
{ "deprecation", "squid:CallToDeprecatedMethod" })

public final class SapcentralorderrcfacadesConstants extends GeneratedSapcentralorderrcfacadesConstants
{
	public static final String EXTENSIONNAME = "sapcentralorderrcfacades";

	public static final String DATE_TIME_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";

	public static final String UTC = "UTC";
	public static final DateTimeFormatter ISO8601_FORMATTER = DateTimeFormatter.ofPattern(DATE_TIME_PATTERN);

	public static final String SB_PLAN_PAYNOW = "paynow";

	private SapcentralorderrcfacadesConstants()
	{
		//empty to avoid instantiating this constant class
	}

	// implement here constants used by this extension
}
