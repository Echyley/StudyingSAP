/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.acceleratorservices.velocity.locale;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import org.apache.commons.lang.LocaleUtils;
import org.apache.commons.lang.StringUtils;

import com.google.common.base.Preconditions;
import com.google.common.base.Splitter;


/**
 * Sample locales provider
 *
 */
public class DefaultLocaleResolver implements LocaleResolver
{
	private final List<Locale> allLocales;

	public DefaultLocaleResolver()
	{
		allLocales = Arrays.asList(Locale.US, Locale.GERMANY);
	}

	public DefaultLocaleResolver(final String supportedCodes)
	{
		allLocales = new ArrayList<>();
		Preconditions.checkArgument(StringUtils.isNotBlank(supportedCodes), "Missing supported locales is codes ");
		for (final String singleCode : Splitter.on(",").split(supportedCodes))
		{
			allLocales.add(LocaleUtils.toLocale(singleCode));
		}
	}

	@Override
	public List<Locale> getAllLocales()
	{
		return allLocales;
	}

	@Override
	public String getLanguageCode(final Locale loc)
	{
		Preconditions.checkNotNull(loc);
		Preconditions.checkNotNull(loc.getLanguage());
		return loc.getLanguage().toLowerCase();
	}
}
