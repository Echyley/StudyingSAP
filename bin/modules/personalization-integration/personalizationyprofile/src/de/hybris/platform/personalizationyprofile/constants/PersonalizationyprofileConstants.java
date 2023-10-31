/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.personalizationyprofile.constants;

/**
 * Global class for all Personalizationyprofile constants. You can add global constants for your extension into this
 * class.
 */
public final class PersonalizationyprofileConstants extends GeneratedPersonalizationyprofileConstants
{
	public static final String EXTENSIONNAME = "personalizationyprofile";

	public static final String CONSUMPTION_LAYER_INTEGRATION_CONVERTER_NAME = "consumptionLayerIntegrationConverter";

	public static final String CREATE_SEGMENT_PROPERTY_NAME = EXTENSIONNAME + ".mappingService.createSegment";

	//Yaas Identity Service

	public static final String IDENTITY_TYPE_EMAIL = "email";
	public static final String IDENTITY_ORIGIN_USER_ACCOUNT = "userAccount";

	public static final String CONSENT_REFERENCE_SESSION_ATTR_KEY = "consent-reference";

	private PersonalizationyprofileConstants()
	{
		//empty to avoid instantiating this constant class
	}
}
