/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.runtime.cps.populator.impl;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Range;


enum CPSIntervalType
{
	UNCONSTRAINED_DOMAIN("0"), SINGLE_VALUE("1"), HALF_OPEN_RIGHT_INTERVAL("2"), CLOSED_INTERVAL("3"), HALF_OPEN_LEFT_INTERVAL(
			"4"), OPEN_INTERVAL("5"), INFINITY_TO_HIGH_OPEN_INTERVAL("6"), INFINITY_TO_HIGH_CLOSED_INTERVAL(
					"7"), LOW_TO_INFINITY_OPEN_INTERVAL("8"), LOW_TO_INFINITY_CLOSED_INTERVAL("9");

	public static final int LOWEST_INTERVAL_VALUE_TYPE = 2;
	public static final int HIGHEST_INTERVAL_VALUE_TYPE = 9;
	private String type;

	CPSIntervalType(final String type)
	{
		this.type = type;
	}

	public static CPSIntervalType fromString(final String intervalType)
	{
		for (final CPSIntervalType interval : CPSIntervalType.values())
		{
			if (interval.type.equals(intervalType))
			{
				return interval;
			}
		}

		return null;
	}

	public static boolean isInterval(final String intervalType)
	{
		boolean isInterval = true;

		if (StringUtils.isEmpty(intervalType))
		{
			isInterval = false;
		}
		else if (!intervalType.matches("\\d"))
		{
			isInterval = false;
		}
		else
		{
			final int intervalValue = Integer.parseInt(intervalType);
			if (!Range.between(LOWEST_INTERVAL_VALUE_TYPE, HIGHEST_INTERVAL_VALUE_TYPE).contains(intervalValue))
			{
				isInterval = false;
			}
		}

		return isInterval;
	}
}
