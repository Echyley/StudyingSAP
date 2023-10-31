/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.b2b.util;


import java.util.Calendar;

/**
 * The Class B2BYearRange.
 * 
 * 
 */
public class YearRange implements TimeRange
{
	/**
	 * @see TimeRange#getEndOfRange(Calendar)
	 */
	public Calendar getEndOfRange(final Calendar calendar)
	{
		calendar.set(calendar.get(Calendar.YEAR), Calendar.DECEMBER, calendar.getActualMaximum(Calendar.DAY_OF_MONTH), 23, 59, 59);
		return calendar;
	}

	/**
	 * @see TimeRange#getStartOfRange(Calendar)
	 */
	public Calendar getStartOfRange(final Calendar calendar)
	{
		calendar.set(calendar.get(Calendar.YEAR), Calendar.JANUARY, calendar.getActualMinimum(Calendar.DAY_OF_MONTH), 0, 0, 0);
		return calendar;
	}
}
