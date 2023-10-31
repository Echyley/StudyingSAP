/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.b2b.punchout.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;


/**
 * Date utility for parsing and formatting {@link Date} objects in accordance with the cXML standard.
 */
public class CXmlDateUtil
{
	private static final String DATE_PATTERN = "yyyy-MM-dd'T'HH:mm:ssXXX";
	final ThreadLocal<SimpleDateFormat> dateFormat = ThreadLocal.withInitial(() -> new SimpleDateFormat(DATE_PATTERN));

	/**
	 * Parses a string into a date following the cXML specification.
	 *
	 * @param dateString the date String
	 * @return a parsed {@link Date} object
	 * @throws ParseException when parsing failure occurs
	 */
	public Date parseString(final String dateString) throws ParseException
	{
		return dateFormat.get().parse(dateString);
	}

	/**
	 * Formats a {@link Date} instance into a {@link String}.
	 *
	 * @param date the date to use
	 * @return the String representation
	 */
	public String formatDate(final Date date)
	{
		return dateFormat.get().format(date);
	}

	/**
	 * Formats a {@link ZonedDateTime} instance into a {@link String}.
	 *
	 * @param date the date to use
	 * @return the String representation
	 */
	public String formatDate(final ZonedDateTime date)
	{
		return date.format(DateTimeFormatter.ofPattern(DATE_PATTERN));
	}
}
