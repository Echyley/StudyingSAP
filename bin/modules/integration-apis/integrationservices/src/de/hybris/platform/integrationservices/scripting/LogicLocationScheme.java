/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.integrationservices.scripting;

import java.util.stream.Stream;

/**
 * Type of schemes supported for the logic location
 */
public enum LogicLocationScheme
{
	/**
	 * Indicates program logic stored as {@link de.hybris.platform.scripting.model.ScriptModel} in the persistent storage
	 */
	MODEL,
	/**
	 * Indicates program logic present in a Spring Bean service within the application context.
	 */
	BEAN;

	/**
	 * Returns the enum for the given scheme
	 *
	 * @param scheme String representation of the enumeration to retrieve
	 * @return The enumeration that is equivalent to the given scheme
	 * @throws UnsupportedLogicLocationSchemeException when the given scheme does not match any of the enumerations
	 */
	public static LogicLocationScheme from(final String scheme) throws UnsupportedLogicLocationSchemeException
	{
		return Stream.of(values())
		             .filter(s -> s.name().equalsIgnoreCase(scheme))
		             .findFirst()
		             .orElseThrow(() -> new UnsupportedLogicLocationSchemeException(scheme));
	}
}
