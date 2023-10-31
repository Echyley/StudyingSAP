/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.swaggerintegration.constants;

import javax.ws.rs.core.MediaType;

import java.util.List;


/**
 * Global class for all Swaggerintegration constants. You can add global constants for your extension into this class.
 */
public final class SwaggerintegrationConstants
{
	public static final String EXTENSIONNAME = "swaggerintegration";

	// implement here constants used by this extension

	public static final String PLATFORM_LOGO_CODE = "swaggerintegrationPlatformLogo";

	public static final List<String> DEFAULT_PRODUCES_MEDIA_TYPES = List.of(MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON);

	public static final List<String> DEFAULT_CONSUMES_MEDIA_TYPES = List.of(MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON);

	public static final String MEDIA_TYPE_WILDCARD = MediaType.WILDCARD;

	private SwaggerintegrationConstants()
	{
		//empty to avoid instantiating this constant class
	}

}
