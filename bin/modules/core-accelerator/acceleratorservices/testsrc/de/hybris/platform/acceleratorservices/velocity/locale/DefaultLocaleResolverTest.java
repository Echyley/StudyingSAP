/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.acceleratorservices.velocity.locale;

import de.hybris.bootstrap.annotations.UnitTest;

import java.util.Arrays;
import java.util.Locale;

import junit.framework.Assert;

import org.junit.Test;


@UnitTest
public class DefaultLocaleResolverTest
{
	private LocaleResolver resolver = null;

	@Test(expected = IllegalArgumentException.class)
	public void testLocalizedWithNull()
	{
		resolver = new DefaultLocaleResolver(null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testLocalizedWithBlank()
	{
		resolver = new DefaultLocaleResolver(" ");
	}

	@Test(expected = IllegalArgumentException.class)
	public void testLocalizedWithIncorrectCodes()
	{
		resolver = new DefaultLocaleResolver(", ");
	}

	@Test
	public void testLocalizedWithCorrectCodes()
	{
		resolver = new DefaultLocaleResolver("de,en,zh");
		Assert.assertEquals(Arrays.asList(Locale.GERMAN, Locale.ENGLISH, Locale.CHINESE), resolver.getAllLocales());
	}

	@Test(expected = NullPointerException.class)
	public void testLocalizedGetNullCode()
	{
		resolver = new DefaultLocaleResolver("de,en,zh");
		resolver.getLanguageCode(null);
	}

	@Test
	public void testLocalizedGetNotNullCode()
	{
		resolver = new DefaultLocaleResolver("de,en,zh");
		Assert.assertEquals(Locale.GERMAN.getLanguage().toLowerCase(), resolver.getLanguageCode(Locale.GERMAN));
	}

}
