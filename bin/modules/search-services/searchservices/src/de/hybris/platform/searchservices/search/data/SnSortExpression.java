/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.searchservices.search.data;

import de.hybris.platform.searchservices.util.JsonUtils;


public class SnSortExpression
{
	private String expression;

	private SnSortOrder order;

	public String getExpression()
	{
		return expression;
	}

	public void setExpression(final String expression)
	{
		this.expression = expression;
	}

	public SnSortOrder getOrder()
	{
		return order;
	}

	public void setOrder(final SnSortOrder order)
	{
		this.order = order;
	}

	/**
	 * @deprecated Replaced by {@link #getOrder()}.
	 */
	@Deprecated(since = "2211", forRemoval = true)
	public Boolean getAscending()
	{
		return order == null ? null : Boolean.valueOf(this.order == SnSortOrder.ASCENDING);
	}

	/**
	 * @deprecated Replaced by {@link #setOrder(SnSortOrder)}.
	 */
	@Deprecated(since = "2211", forRemoval = true)
	public void setAscending(final Boolean ascending)
	{
		this.order = ascending == null || ascending ? SnSortOrder.ASCENDING : SnSortOrder.DESCENDING;
	}

	@Override
	public String toString()
	{
		return JsonUtils.toJson(this);
	}
}
