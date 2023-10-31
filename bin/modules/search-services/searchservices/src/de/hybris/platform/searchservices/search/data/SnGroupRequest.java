/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.searchservices.search.data;

import de.hybris.platform.searchservices.util.JsonUtils;


public class SnGroupRequest
{
	private String expression;
	private Integer top;

	public String getExpression()
	{
		return expression;
	}

	public void setExpression(final String expression)
	{
		this.expression = expression;
	}

	public Integer getTop()
	{
		return top;
	}

	public void setTop(final Integer top)
	{
		this.top = top;
	}

	/**
	 * @deprecated Replaced by {@link #getTop()}.
	 */
	@Deprecated(since = "2211", forRemoval = true)
	public Integer getLimit()
	{
		return getTop();
	}

	/**
	 * @deprecated Replaced by {@link #setTop(Integer)}.
	 */
	@Deprecated(since = "2211", forRemoval = true)
	public void setLimit(final Integer limit)
	{
		setTop(limit);
	}

	@Override
	public String toString()
	{
		return JsonUtils.toJson(this);
	}
}
