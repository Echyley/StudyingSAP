/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.searchservices.suggest.data;

import de.hybris.platform.searchservices.util.JsonUtils;

import java.util.ArrayList;
import java.util.List;


/**
 * Represents a suggest query.
 */
public class SnSuggestQuery
{
	private List<String> queryContexts;
	private String query;
	private Integer top;

	public List<String> getQueryContexts()
	{
		if (queryContexts == null)
		{
			queryContexts = new ArrayList<>();
		}

		return queryContexts;
	}

	public void setQueryContexts(final List<String> queryContexts)
	{
		this.queryContexts = queryContexts;
	}

	public String getQuery()
	{
		return query;
	}

	public void setQuery(final String query)
	{
		this.query = query;
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
