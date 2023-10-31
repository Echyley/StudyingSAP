/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.sapoaacosintegration.services.atp.request;

import java.util.Collections;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;


public class ArticleSources
{

	private List<SourceItem> items;
	private List<CosSource> sources;
	/**
	 * @return the items
	 */
	@JsonGetter("items")
	public List<SourceItem> getItems()
	{
		return items;
	}

	/**
	 * @param productId
	 *           the productId to set
	 */
	@JsonIgnore
	public void setArticleId(final String productId)
	{
		final SourceItem item = new SourceItem();
		item.setProductId(productId);
		items = Collections.singletonList(item);
	}

	@JsonIgnore
	public String getArticleId()
	{
		if (items != null && items.get(0) != null)
		{
			return items.get(0).getProductId();
		}
		return "";
	}


	/**
	 * @param unit
	 *           the unit to set
	 */
	@JsonIgnore
	public void setUnit(final String unit)
	{

		if (items != null && items.get(0) != null)
		{
			final SourceItem item = getItems().get(0);
			item.setUnit(unit);
		}
		else
		{

			final SourceItem item = new SourceItem();
			item.setUnit(unit);
			items = Collections.singletonList(item);
		}

	}

	@JsonIgnore
	public String getunit()
	{
		if (items != null && items.get(0) != null)
		{
			return items.get(0).getUnit();
		}
		return "";
	}

	/**
	 * @return the sources
	 */
	@JsonGetter("sources")
	public List<CosSource> getSources()
	{
		return sources;
	}

	/**
	 * @param sources
	 *           the sources to set
	 */
	public void setSources(final List<CosSource> sources)
	{
		this.sources = sources;
	}
}
