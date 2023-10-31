/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.searchservices.suggest.data;

import de.hybris.platform.searchservices.util.JsonUtils;

import java.util.List;


/**
 * Represents a suggest result.
 */
public class SnSuggestResult
{
	private List<SnSuggestHit> suggestHits;

	public Integer getSize()
	{
		return suggestHits == null ? 0 : suggestHits.size();
	}

	/**
	 * @deprecated No longer required.
	 */
	@Deprecated(since = "2211", forRemoval = true)
	public void setSize(final Integer size)
	{
		// Ignored
	}

	public List<SnSuggestHit> getSuggestHits()
	{
		return suggestHits;
	}

	public void setSuggestHits(final List<SnSuggestHit> suggestHits)
	{
		this.suggestHits = suggestHits;
	}

	@Override
	public String toString()
	{
		return JsonUtils.toJson(this);
	}
}
