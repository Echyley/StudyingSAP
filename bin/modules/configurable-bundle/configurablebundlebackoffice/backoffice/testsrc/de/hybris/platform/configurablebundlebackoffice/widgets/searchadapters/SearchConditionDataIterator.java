/*
 * [y] hybris Platform
 *
 * Copyright (c) 2017 SAP SE or an SAP affiliate company.  All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.configurablebundlebackoffice.widgets.searchadapters;

import static com.google.common.collect.Lists.newArrayList;

import java.util.Iterator;
import java.util.List;

import com.hybris.backoffice.widgets.advancedsearch.impl.AdvancedSearchData;
import com.hybris.backoffice.widgets.advancedsearch.impl.SearchConditionData;
import com.hybris.backoffice.widgets.advancedsearch.impl.SearchConditionDataList;


/**
 * Simplifies iteration over conditions in AdvancedSearchData for testing purposes
 */
public class SearchConditionDataIterator
{
	protected static Iterator<SearchConditionData> createSearchConditionDataIterator(final List<SearchConditionData> data)
	{
		final List<SearchConditionData> conditions = newArrayList();
		for (final SearchConditionData item : data)
		{
			if (SearchConditionDataList.class.isInstance(item))
			{
				final SearchConditionDataList actual = (SearchConditionDataList) item;
				conditions.addAll(newArrayList(createSearchConditionDataIterator(actual.getConditions())));
			}
			else
			{
				conditions.add(item);
			}
		}
		return conditions.iterator();
	}

	/**
	 * Flattens all conditional lists conditions in the searchData objects into iterable collection
	 *
	 * @param searchData
	 * @return iterable search conditions
	 */
	public static Iterator<SearchConditionData> createSearchConditionDataIterator(final AdvancedSearchData searchData)
	{
		final List<SearchConditionData> condition = searchData.getConditions("_orphanedSearchConditions");
		return createSearchConditionDataIterator(condition);
	}
}
