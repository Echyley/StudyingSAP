/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.core.module;

import de.hybris.platform.sap.core.configuration.GlobalConfigurationManager;

import java.io.Serializable;
import java.util.Locale;


/**
 * Interface to access resources by module specific resource keys.
 * <p>
 * The module specific resource key needs to be in the following format: {@literal <moduleId>}.key (Example:
 * myModule.myKey)
 * </p>
 * <p>
 * The bundle determination works with help of {@link GlobalConfigurationManager} where all extension names for a sap
 * module id are accessible. The bundle needs to comply to the following naming convention
 * {@code <extensionName>-locales_<locale>}.
 * </p>
 */
public interface ModuleResourceAccess
{

	/**
	 * Translate the given key with the standard locale.
	 * 
	 * @param resourceKey
	 *           resource key
	 * @return translated resource string
	 */
	public String getString(final String resourceKey);

	/**
	 * Translate the given key with the given locale.
	 * 
	 * @param resourceKey
	 *           resource key
	 * @param locale
	 *           locale
	 * @return translated resource string
	 */
	public String getString(final String resourceKey, final Locale locale);

	/**
	 * Translate the given key with the default Locale replacing the passed arguments.
	 * 
	 * @param resourceKey
	 *           resource key
	 * @param args
	 *           arguments
	 * @return translated resource string
	 */
	public String getString(final String resourceKey, final Object[] args);

	/**
	 * Translate the given key with the given locale replacing the passed arguments.
	 * 
	 * @param resourceKey
	 *           resource key
	 * @param locale
	 *           locale
	 * @param args
	 *           arguments
	 * @return translated resource string
	 */
	public String getString(final String resourceKey, final Locale locale, final Object[] args);

}
