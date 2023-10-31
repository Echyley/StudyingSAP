/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.sapordermgmtservices.constants;


/**
 * Global class for all Sapordermgmtservices constants. You can add global constants for your extension into this class.
 */
public final class SapordermgmtservicesConstants
{
	/**
	 * Name of our extension
	 */
	@SuppressWarnings("squid:S2387")
	public static final String EXTENSIONNAME = "sapordermgmtservices";

	/**
	 * Key to indicate that a SAP back end error occurred (which is typically converted into a better readable text)
	 * which we want to display on entering checkout or addToCart
	 */
	public static final String STATUS_SAP_ERROR = "sapError";

	public static final String STATUS_SAP_INFO = "sapInfo";

	private SapordermgmtservicesConstants()
	{
		//empty to avoid instantiating this constant class
	}

	// implement here constants used by this extension
}
