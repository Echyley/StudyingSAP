/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.b2b.util;

import java.util.Calendar;

/**
 * The Class B2BDayRange.
 * 
 * 
 */
public class DayRange implements TimeRange
{
	/**
	 * @see TimeRange#getEndOfRange(Calendar)
	 */
	public Calendar getEndOfRange(final Calendar calendar)
	{
		calendar.set(Calendar.HOUR_OF_DAY, 11);
		calendar.set(Calendar.HOUR, 11);
		calendar.set(Calendar.MINUTE, 59);
		calendar.set(Calendar.SECOND, 59);
		calendar.set(Calendar.AM_PM, Calendar.PM);
		return calendar;
	}

	/**
	 * @see TimeRange#getStartOfRange(Calendar)
	 */
	public Calendar getStartOfRange(final Calendar calendar)
	{
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.HOUR, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.AM_PM, Calendar.AM);
		return calendar;
	}
}
