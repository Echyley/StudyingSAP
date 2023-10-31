/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.acceleratorservices.velocity.locale;

import java.util.List;
import java.util.Locale;


/**
 * Resolves all available Locales guesses a language iso code for given locale
 */
public interface LocaleResolver
{
	/**
	 * Used for generating localized property 'middle-fix' like products_(en).properties
	 */
	List<Locale> getAllLocales();

	/**
	 * Used to bind a language iso code into impex template
	 */
	String getLanguageCode(Locale loc);

}
