/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.facades.filters;

import de.hybris.platform.sap.productconfig.facades.overview.FilterEnum;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class FilterTestData
{

	public static List<FilterEnum> setVisibleFilter()
	{
		final List<FilterEnum> filter = new ArrayList<FilterEnum>();
		filter.add(FilterEnum.VISIBLE);
		return filter;
	}

	public static List<FilterEnum> setUserAssignedFilter()
	{
		final List<FilterEnum> filter = new ArrayList<FilterEnum>();
		filter.add(FilterEnum.USER_INPUT);
		return filter;
	}

	public static List<FilterEnum> setPiceRelevantFilter()
	{
		final List<FilterEnum> filter = new ArrayList<FilterEnum>();
		filter.add(FilterEnum.PRICE_RELEVANT);
		return filter;
	}

	public static List<FilterEnum> setAllFilters()
	{
		return new ArrayList<FilterEnum>(Arrays.asList(FilterEnum.values()));
	}

	public static List<FilterEnum> setNoFilters()
	{
		return new ArrayList<FilterEnum>();
	}


}
