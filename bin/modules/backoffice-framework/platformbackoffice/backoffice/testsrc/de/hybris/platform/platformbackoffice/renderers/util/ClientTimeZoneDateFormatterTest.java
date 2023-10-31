/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved
 */
package de.hybris.platform.platformbackoffice.renderers.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.zkoss.util.Locales;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.sys.SessionsCtrl;


@RunWith(MockitoJUnitRunner.class)
public class ClientTimeZoneDateFormatterTest
{
	public static final String CUSTOM_TIME_ZONE_ID = "Pacific/Easter";
	private final ClientTimeZoneDateFormatter dateFormatter = new ClientTimeZoneDateFormatter();
	private final ZonedDateTime zonedDateTime = ZonedDateTime.now();
	private final Date dateTime = Date.from(zonedDateTime.toInstant());
	@Mock
	private Session session;
	private final Locale locale = Locales.getCurrent();

	@Before
	public void setUp() throws Exception
	{
		SessionsCtrl.setCurrent(session);
	}

	@Test
	public void shouldFormatDateWhenTimeZoneInSessionIsNull()
	{
		//given
		final String expectedFormattedDate = formatZonedDateTime(zonedDateTime, TimeZone.getDefault(),
				ClientTimeZoneDateFormatter.DEFAULT_DATE_FORMAT);
		when(session.getAttribute(ClientTimeZoneDateFormatter.ORG_ZKOSS_WEB_PREFERRED_TIME_ZONE)).thenReturn(null);

		//when
		final String formattedDate = dateFormatter.format(dateTime, null, locale);

		//then
		assertThat(formattedDate).isEqualTo(expectedFormattedDate);
		verify(session).getAttribute(ClientTimeZoneDateFormatter.ORG_ZKOSS_WEB_PREFERRED_TIME_ZONE);
	}

	@Test
	public void shouldFormatDateWithSessionTimeZone()
	{
		//given
		final TimeZone customTimeZone = TimeZone.getTimeZone(CUSTOM_TIME_ZONE_ID);
		final String expectedFormattedDate = formatZonedDateTime(zonedDateTime, customTimeZone,
				ClientTimeZoneDateFormatter.DEFAULT_DATE_FORMAT);
		when(session.getAttribute(ClientTimeZoneDateFormatter.ORG_ZKOSS_WEB_PREFERRED_TIME_ZONE)).thenReturn(customTimeZone);

		//when
		final String formattedDate = dateFormatter.format(dateTime, null, locale);

		//then
		assertThat(formattedDate).isEqualTo(expectedFormattedDate);
		verify(session).getAttribute(ClientTimeZoneDateFormatter.ORG_ZKOSS_WEB_PREFERRED_TIME_ZONE);
	}

	@Test
	public void shouldFormatDateWithDefaultFormat()
	{
		//given
		//		final TimeZone customTimeZone = TimeZone.getTimeZone(CUSTOM_TIME_ZONE_ID);
		final String expectedFormattedDate = formatZonedDateTime(zonedDateTime, TimeZone.getDefault(),
				ClientTimeZoneDateFormatter.DEFAULT_DATE_FORMAT);

		when(session.getAttribute(ClientTimeZoneDateFormatter.ORG_ZKOSS_WEB_PREFERRED_TIME_ZONE)).thenReturn(null);

		//when
		final String formattedDate = dateFormatter.format(dateTime, null, locale);

		//then
		assertThat(formattedDate).isEqualTo(expectedFormattedDate);
		verify(session).getAttribute(ClientTimeZoneDateFormatter.ORG_ZKOSS_WEB_PREFERRED_TIME_ZONE);
	}

	@Test
	public void shouldFormatDateWithCommonFormat()
	{
		//given
		final String commonFormat = "yyyy-mm-dd HH:mm";
		final String expectedFormattedDate = formatZonedDateTime(zonedDateTime, TimeZone.getDefault(), commonFormat);
		dateFormatter.setFormat(commonFormat);
		when(session.getAttribute(ClientTimeZoneDateFormatter.ORG_ZKOSS_WEB_PREFERRED_TIME_ZONE)).thenReturn(null);

		//when
		final String formattedDate = dateFormatter.format(dateTime, null, locale);

		//then
		assertThat(formattedDate).isEqualTo(expectedFormattedDate);
		verify(session).getAttribute(ClientTimeZoneDateFormatter.ORG_ZKOSS_WEB_PREFERRED_TIME_ZONE);
	}

	@Test
	public void shouldFormatDateWithCustomFormat()
	{
		//given
		final String customFormat = "MM/yy/dd";
		final String expectedFormattedDate = formatZonedDateTime(zonedDateTime, TimeZone.getDefault(), customFormat);

		when(session.getAttribute(ClientTimeZoneDateFormatter.ORG_ZKOSS_WEB_PREFERRED_TIME_ZONE)).thenReturn(null);

		//when
		final String formattedDate = dateFormatter.format(dateTime, customFormat, locale);

		//then
		assertThat(formattedDate).isEqualTo(expectedFormattedDate);
		verify(session).getAttribute(ClientTimeZoneDateFormatter.ORG_ZKOSS_WEB_PREFERRED_TIME_ZONE);
	}

	private String formatZonedDateTime(final ZonedDateTime zonedDateTime, final TimeZone timeZone, final String pattern)
	{
		return zonedDateTime.withZoneSameInstant(timeZone.toZoneId()).format(DateTimeFormatter.ofPattern(pattern, locale));
	}
}
