/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.searchservices.search.data;

import de.hybris.platform.searchservices.util.JsonUtils;

import java.util.ArrayList;
import java.util.List;


/**
 * Represents a search query.
 */
public class SnSearchQuery
{
	private List<String> queryContexts;
	private String query;
	private Integer top;
	private Integer skip;
	private List<SnFilter> filters;
	private List<AbstractSnFacetRequest> facets;
	private List<AbstractSnFacetFilter> facetFilters;
	private List<AbstractSnRankRule> rankRules;
	private SnSort sort;
	private List<SnSort> availableSorts;
	private SnGroupRequest group;

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

	public Integer getSkip()
	{
		return skip;
	}

	public void setSkip(final Integer skip)
	{
		this.skip = skip;
	}

	/**
	 * @deprecated Replaced by {@link #getSkip()}.
	 */
	@Deprecated(since = "2211", forRemoval = true)
	public Integer getOffset()
	{
		return getSkip();
	}

	/**
	 * @deprecated Replaced by {@link #setSkip(Integer)}.
	 */
	@Deprecated(since = "2211", forRemoval = true)
	public void setOffset(final Integer offset)
	{
		setSkip(offset);
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

	public List<SnFilter> getFilters()
	{
		if (filters == null)
		{
			filters = new ArrayList<>();
		}

		return filters;
	}

	public void setFilters(final List<SnFilter> filters)
	{
		this.filters = filters;
	}

	public List<AbstractSnFacetRequest> getFacets()
	{
		if (facets == null)
		{
			facets = new ArrayList<>();
		}

		return facets;
	}

	public void setFacets(final List<AbstractSnFacetRequest> facets)
	{
		this.facets = facets;
	}

	public List<AbstractSnFacetFilter> getFacetFilters()
	{
		if (facetFilters == null)
		{
			facetFilters = new ArrayList<>();
		}

		return facetFilters;
	}

	public void setFacetFilters(final List<AbstractSnFacetFilter> facetFilters)
	{
		this.facetFilters = facetFilters;
	}

	public List<AbstractSnRankRule> getRankRules()
	{
		if (rankRules == null)
		{
			rankRules = new ArrayList<>();
		}

		return rankRules;
	}

	public void setRankRules(final List<AbstractSnRankRule> rankRules)
	{
		this.rankRules = rankRules;
	}

	public SnSort getSort()
	{
		return sort;
	}

	public void setSort(final SnSort sort)
	{
		this.sort = sort;
	}

	public List<SnSort> getAvailableSorts()
	{
		if (availableSorts == null)
		{
			availableSorts = new ArrayList<>();
		}

		return availableSorts;
	}

	public void setAvailableSorts(final List<SnSort> availableSorts)
	{
		this.availableSorts = availableSorts;
	}

	public SnGroupRequest getGroup()
	{
		return group;
	}

	public void setGroup(final SnGroupRequest group)
	{
		this.group = group;
	}

	@Override
	public String toString()
	{
		return JsonUtils.toJson(this);
	}
}
