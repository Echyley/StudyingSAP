/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.b2bacceleratorservices.company;

import de.hybris.platform.b2bacceleratorservices.document.NumberOfDayRange;

import java.util.List;

/**
 * Provides services for Past Due Balance Date Range.
 *
 */
public interface PastDueBalanceDateRangeService
{
	/**
	 * Gets a list of number of days ranges.
	 * 
	 * @return date range list
	 */
	List<NumberOfDayRange> getNumberOfDayRange();
}
