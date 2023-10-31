/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved
 */
package de.hybris.platform.platformbackoffice.renderers.util;

import java.text.DateFormat;
import java.util.Locale;

import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.zkoss.text.DateFormats;


public class DateFormatMapperTest
{
	private final Locale locale = Locale.getDefault();

	@Test
	public void shouldReturnNullFormat()
	{
		final String actualFormat = new DateFormatMapper().map(null, locale);

		Assertions.assertThat(actualFormat).isNull();
	}

	@Test
	public void shouldReturnShortFormat()
	{ //given

		//when
		final String actualFormat = new DateFormatMapper().map("short", locale);

		//then
		final String expectedFormat = getDateTimeFormatByStyle(DateFormat.SHORT);
		Assertions.assertThat(actualFormat).isEqualTo(expectedFormat);
	}

	@Test
	public void shouldReturnMediumFormat()
	{
		//given
		//when
		final String actualFormat = new DateFormatMapper().map("medium", locale);

		//then
		final String expectedFormat = getDateTimeFormatByStyle(DateFormat.MEDIUM);
		Assertions.assertThat(actualFormat).isEqualTo(expectedFormat);
	}

	@Test
	public void shouldReturnLongFormat()
	{
		//given
		//when
		final String actualFormat = new DateFormatMapper().map("long", locale);

		//then
		final String expectedFormat = getDateTimeFormatByStyle(DateFormat.LONG);
		Assertions.assertThat(actualFormat).isEqualTo(expectedFormat);
	}

	@Test
	public void shouldReturnDefaultFormat()
	{
		//given
		//when
		final String actualFormat = new DateFormatMapper().map("none", locale);

		//then
		final String expectedFormat = getDateTimeFormatByStyle(DateFormat.MEDIUM);
		Assertions.assertThat(actualFormat).isEqualTo(expectedFormat);
	}

	private String getDateTimeFormatByStyle(final int style)
	{
		return DateFormats.getDateTimeFormat(style, style, locale, null);
	}

	@Test
	public void shouldReturnPatternFormat()
	{
		//given
		final String patternFormat = "hh:mm";
		//when
		final String actualFormat = new DateFormatMapper().map(patternFormat, locale);

		//then
		Assertions.assertThat(actualFormat).isEqualTo(patternFormat);
	}
}
