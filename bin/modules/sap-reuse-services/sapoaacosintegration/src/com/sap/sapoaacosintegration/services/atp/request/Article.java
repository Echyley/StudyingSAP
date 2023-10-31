/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.sapoaacosintegration.services.atp.request;

import java.util.Collections;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSetter;

public class Article
{
	private List<ArticleItem> items;
	private String strategyId;

	/**
	 * @return the strategyId
	 */
	@JsonGetter("strategyId")
	public String getStrategyId()
	{
		return strategyId;
	}

	/**
	 * @param strategyId
	 *           the strategyId to set
	 */
	@JsonSetter("strategyId")
	public void setStrategyId(final String strategyId)
	{
		this.strategyId = strategyId;
	}

	/**
	 * @return the articleId
	 */
	@JsonGetter("items")
	public List<ArticleItem> getItems()
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
		if (items != null && items.get(0) != null)
		{
			final ArticleItem item = getItems().get(0);
			item.setUnit(productId);
		}
		else
		{
			final ArticleItem item = new ArticleItem();
			item.setProductId(productId);
			items = Collections.singletonList(item);
		}

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
			final ArticleItem item = getItems().get(0);
			item.setUnit(unit);
		}
		else
		{

			final ArticleItem item = new ArticleItem();
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

}
