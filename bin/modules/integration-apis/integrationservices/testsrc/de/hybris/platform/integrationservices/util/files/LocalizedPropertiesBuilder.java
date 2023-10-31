/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.integrationservices.util.files;

import java.util.Locale;
import java.util.ResourceBundle;


/**
 * A builder for creating {@link LocalizedProperties}.
 */
public class LocalizedPropertiesBuilder
{
	private static final String DEFAULT_LOCALE = "en";
	private static final String FILE_NAME_TEMPLATE = "localization.%s-locales";
	private final String extensionName;
	private String language;

	private LocalizedPropertiesBuilder(final String extName)
	{
		extensionName = extName;
	}

	/**
	 * Creates new instance of this builder.
	 *
	 * @param extName the extension for the properties file
	 * @return instance of the builder
	 */
	public static LocalizedPropertiesBuilder localizedProperties(final String extName)
	{
		return new LocalizedPropertiesBuilder(extName);
	}

	/**
	 * Creates new instance of this builder.
	 *
	 * @param language the language for the properties file
	 * @return instance of the builder
	 */
	public LocalizedPropertiesBuilder withLanguage(final String language)
	{
		this.language = language;
		return this;
	}

	public LocalizedProperties build()
	{
		final ResourceBundle rb = loadResourceBundle();
		return new ExtensionLocalizationProperties(rb);
	}

	private ResourceBundle loadResourceBundle()
	{
		return ResourceBundle.getBundle(String.format(FILE_NAME_TEMPLATE, extensionName), deriveLocale());
	}

	private Locale deriveLocale()
	{
		return language == null ? new Locale(DEFAULT_LOCALE) : new Locale(language);
	}
}
