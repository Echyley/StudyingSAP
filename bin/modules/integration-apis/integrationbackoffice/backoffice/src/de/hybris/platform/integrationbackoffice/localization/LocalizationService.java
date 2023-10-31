/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.integrationbackoffice.localization;

/**
 * A services that provides localized messages, labels, and other resources to other services and backoffice views.
 * All methods imply the current session locale selected in the context of the call.
 */
public interface LocalizationService
{
	/**
	 * Retrieves value for the context locale in the context resource bundle.
	 *
	 * @param key resource bundle key to get the value for.
	 * @return value in the resource bundle for the localization key or the key, if the key is not found
	 * in the resource bundle.
	 */
	String getLocalizedString(String key);

	/**
	 * Retrieves parameterized value for the context locale in the context resource bundle.
	 *
	 * @param key    resource bundle key to get the value for.
	 * @param params parameters to be injected into the localized value according to the {@link java.text.MessageFormat}
	 *               conventions.
	 * @return value in the resource bundle for the localization key or the key, if the key is not found
	 * in the resource bundle.
	 */
	String getLocalizedString(String key, Object[] params);
}
