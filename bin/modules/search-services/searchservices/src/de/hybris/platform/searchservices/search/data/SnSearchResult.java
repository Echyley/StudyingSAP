/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.searchservices.search.data;

import de.hybris.platform.searchservices.util.JsonUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Represents a search result.
 */
public class SnSearchResult
{
	/**
	 * @deprecated No longer required.
	 */
	@Deprecated(since = "2211", forRemoval = true)
	private Integer offset;

	/**
	 * @deprecated No longer required.
	 */
	@Deprecated(since = "2211", forRemoval = true)
	private Integer limit;

	private Integer count;
	private List<SnSearchHit> searchHits;
	private List<AbstractSnFacetResponse> facets;
	private SnNamedSort sort;
	private List<SnNamedSort> availableSorts;

	private final Map<String, Object> debug = new HashMap<>();

	/**
	 * @deprecated No longer required.
	 */
	@Deprecated(since = "2211", forRemoval = true)
	public Integer getOffset()
	{
		return offset;
	}

	/**
	 * @deprecated No longer required.
	 */
	@Deprecated(since = "2211", forRemoval = true)
	public void setOffset(final Integer offset)
	{
		this.offset = offset;
	}

	/**
	 * @deprecated No longer required.
	 */
	@Deprecated(since = "2211", forRemoval = true)
	public Integer getLimit()
	{
		return limit;
	}

	/**
	 * @deprecated No longer required.
	 */
	@Deprecated(since = "2211", forRemoval = true)
	public void setLimit(final Integer limit)
	{
		this.limit = limit;
	}

	public Integer getSize()
	{
		return searchHits == null ? 0 : searchHits.size();
	}

	/**
	 * @deprecated No longer required.
	 */
	@Deprecated(since = "2211", forRemoval = true)
	public void setSize(final Integer size)
	{
		// Ignored
	}

	public Integer getCount()
	{
		return count;
	}

	public void setCount(final Integer count)
	{
		this.count = count;
	}

	/**
	 * @deprecated Replaced by {@link #getCount()}.
	 */
	@Deprecated(since = "2211", forRemoval = true)
	public Integer getTotalSize()
	{
		return getCount();
	}

	/**
	 * @deprecated Replaced by {@link #setCount(Integer)}.
	 */
	@Deprecated(since = "2211", forRemoval = true)
	public void setTotalSize(final Integer totalSize)
	{
		setCount(totalSize);
	}

	public List<SnSearchHit> getSearchHits()
	{
		if (searchHits == null)
		{
			searchHits = new ArrayList<>();
		}

		return searchHits;
	}

	public void setSearchHits(final List<SnSearchHit> searchHits)
	{
		this.searchHits = searchHits;
	}

	public List<AbstractSnFacetResponse> getFacets()
	{
		if (facets == null)
		{
			facets = new ArrayList<>();
		}

		return facets;
	}

	public void setFacets(final List<AbstractSnFacetResponse> facets)
	{
		this.facets = facets;
	}

	public SnNamedSort getSort()
	{
		return sort;
	}

	public void setSort(final SnNamedSort sort)
	{
		this.sort = sort;
	}

	public List<SnNamedSort> getAvailableSorts()
	{
		if (availableSorts == null)
		{
			availableSorts = new ArrayList<>();
		}

		return availableSorts;
	}

	public void setAvailableSorts(final List<SnNamedSort> availableSorts)
	{
		this.availableSorts = availableSorts;
	}

	public Map<String, Object> getDebug()
	{
		return debug;
	}

	@Override
	public String toString()
	{
		return JsonUtils.toJson(this);
	}
}
