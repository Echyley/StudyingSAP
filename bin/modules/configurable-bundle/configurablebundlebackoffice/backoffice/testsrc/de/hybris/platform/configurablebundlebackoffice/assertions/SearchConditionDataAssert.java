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
package de.hybris.platform.configurablebundlebackoffice.assertions;

import java.util.Objects;

import org.assertj.core.api.AbstractAssert;

import com.hybris.backoffice.widgets.advancedsearch.impl.SearchConditionData;
import com.hybris.cockpitng.search.data.ValueComparisonOperator;


public class SearchConditionDataAssert extends AbstractAssert<SearchConditionDataAssert, SearchConditionData>
{

	public SearchConditionDataAssert(final SearchConditionData actual)
	{
		super(actual, SearchConditionDataAssert.class);
	}

	public static SearchConditionDataAssert assertThat(final SearchConditionData actual)
	{
		return new SearchConditionDataAssert(actual);
	}

	public SearchConditionDataAssert hasOperator(final ValueComparisonOperator operator)
	{
		isNotNull();
		if (!Objects.equals(actual.getOperator(), operator))
		{
			failWithMessage("Expected operator to be <%s> but was <%s>", operator, actual.getOperator());
		}
		return this;
	}

	public SearchConditionDataAssert hasField(final String fieldName)
	{
		isNotNull();
		if (null == actual.getFieldType())
		{
			failWithMessage("Expected field name to be not null");
		}
		else if (!Objects.equals(actual.getFieldType().getName(), fieldName))
		{
			failWithMessage("Expected field name to be <%s> but was <%s>", fieldName, actual.getFieldType().getName());
		}
		return this;
	}

	public SearchConditionDataAssert hasValue(final Object value)
	{
		isNotNull();

		if (!Objects.equals(actual.getValue(), value))
		{
			failWithMessage("Expected value to be <%s> but was <%s>", value, actual.getValue());
		}
		return this;
	}

	public SearchConditionDataAssert hasEmptyValue()
	{
		isNotNull();

		if (Objects.nonNull(actual.getValue()))
		{
			failWithMessage("Expected value to be null but was <%s>", actual.getValue());
		}
		return this;
	}
}
