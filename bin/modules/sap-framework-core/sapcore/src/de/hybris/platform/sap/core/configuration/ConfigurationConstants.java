/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.core.configuration;

/**
 * Constants for configuration.
 */
public class ConfigurationConstants
{

	private ConfigurationConstants() {
		throw new IllegalStateException("Utility class");
	}

	/**
	 * Default SAP backend type.
	 */
	public static final String DEFAULT_SAP_BACKEND_TYPE = "ERP";

	/**
	 * Default RFC destination name.
	 */
	public static final String DEFAULT_RFC_DESTINATION_NAME = "SAP_DEFAULT";

	/**
	 * SAP runtime configuration name.
	 */
	public static final String SAP_CONFIGURATION_NAME_ATTRIBUTE = "core_name";

}
