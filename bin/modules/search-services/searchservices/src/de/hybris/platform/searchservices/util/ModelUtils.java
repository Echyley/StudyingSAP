/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.searchservices.util;

import de.hybris.platform.servicelayer.model.AbstractItemModel;

import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;


/**
 * Model utilities.
 */
public final class ModelUtils
{
	private ModelUtils()
	{
		// utility class
	}

	/**
	 * Converts an object to a JSON string.
	 *
	 * @param source
	 *           - the source object
	 *
	 * @return a JSON string
	 */
	public static final <T extends AbstractItemModel> Map<Locale, String> extractLocalizedValue(final T model,
			final String property, final Iterable<Locale> locales)
	{
		final Map<Locale, String> target = new LinkedHashMap<>();

		for (final Locale locale : locales)
		{
			target.put(locale, model.getProperty(property, locale));
		}

		return target;
	}
}
