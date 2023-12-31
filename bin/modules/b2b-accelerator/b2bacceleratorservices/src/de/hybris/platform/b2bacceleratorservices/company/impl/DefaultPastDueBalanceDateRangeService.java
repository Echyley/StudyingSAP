/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.b2bacceleratorservices.company.impl;

import de.hybris.platform.b2bacceleratorservices.document.NumberOfDayRange;
import de.hybris.platform.util.Config;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import de.hybris.platform.b2bacceleratorservices.company.PastDueBalanceDateRangeService;

/**
 * Provides services for past due balance date range.
 *
 */
public class DefaultPastDueBalanceDateRangeService implements PastDueBalanceDateRangeService
{

	private static final String END_SUFFIX = ".end";
	private static final String START_SUFFIX = ".start";
	private static final String ACCOUNTSUMMARY_DATERANGE = "accountsummary.daterange.";

	@Override
	public List<NumberOfDayRange> getNumberOfDayRange()
	{
		final List<NumberOfDayRange> result = new ArrayList<>();
		boolean dateRangeExist = true;
		int index = 1;
		while (dateRangeExist)
		{
			final Integer dateRangeStartValue = getRangeValue(index, true);
			final Integer dateRangeEndValue = getRangeValue(index, false);

			if (dateRangeStartValue == null)
			{
				dateRangeExist = false;
			}
			else
			{
				result.add(new NumberOfDayRange(dateRangeStartValue, dateRangeEndValue));
			}
			index++;
		}

		return result;
	}

	protected Integer getRangeValue(final int index, final boolean start)
	{
		final String dateRangeValue = Config.getParameter(ACCOUNTSUMMARY_DATERANGE + index + (start ? START_SUFFIX : END_SUFFIX));
		if (StringUtils.isBlank(dateRangeValue))
		{
			return null;
		}
		return Integer.valueOf(dateRangeValue);
	}
}
