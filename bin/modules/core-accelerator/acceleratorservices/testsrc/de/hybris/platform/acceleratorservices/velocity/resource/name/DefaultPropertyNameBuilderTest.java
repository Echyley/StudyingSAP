/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.acceleratorservices.velocity.resource.name;

import de.hybris.bootstrap.annotations.UnitTest;

import java.util.Locale;

import junit.framework.Assert;

import org.apache.commons.lang.LocaleUtils;
import org.junit.Test;


@UnitTest
public class DefaultPropertyNameBuilderTest
{
	private final DefaultPropertyNameBuilder nameBuilder = new DefaultPropertyNameBuilder();

	@Test(expected = IllegalArgumentException.class)
	public void testNameForNullLocaleAndNullResource()
	{
		nameBuilder.buildFileName(null, null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testNameForOnlyLanguageLocaleAndNullResource()
	{
		nameBuilder.buildFileName(LocaleUtils.toLocale("en"), null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testNameForOnlyLanguageLocaleAndBlankResource()
	{
		nameBuilder.buildFileName(LocaleUtils.toLocale("en"), "     ");
	}

	@Test
	public void testNameForOnlyLanguageLocaleAndResource()
	{
		Assert.assertEquals("some_res_en.properties", nameBuilder.buildFileName(LocaleUtils.toLocale("en"), "some_res"));
	}

	@Test
	public void testNameForFullLocaleAndResource()
	{
		Assert.assertEquals("some_res_en.properties", nameBuilder.buildFileName(LocaleUtils.toLocale("en_UK"), "some_res"));
	}


	//region bayern is not respected
	@Test
	public void testNameForFullLocaleRegionNotSupportedAndResource()
	{
		final Locale loc = LocaleUtils.toLocale("de_DE_by");
		//loc.getVariant();
		Assert.assertEquals("some_res_de.properties", nameBuilder.buildFileName(loc, "some_res"));
	}
}
