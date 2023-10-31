/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.sapoaacosintegration.services.atp.response;


import java.util.List;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonSetter;


public class ArticleBySource
{
	private String productId = null;
	private String unit = null;
	private List<ArticleQuantityBySource> quantityBySource = null;


	/**
	 * @return the productId
	 */
	@JsonGetter("productId")
	public String getProductId()
	{
		return productId;
	}

	/**
	 * @param productId
	 *           the productId to set
	 */
	@JsonSetter("productId")
	public void setProductId(final String productId)
	{
		this.productId = productId;
	}

	/**
	 * @return the unit
	 */
	@JsonGetter("unit")
	public String getUnit()
	{
		return unit;
	}

	/**
	 * @param unit
	 *           the unit to set
	 */
	@JsonSetter("unit")
	public void setUnit(final String unit)
	{
		this.unit = unit;
	}

	/**
	 * @return the quantityBySource
	 */
	@JsonGetter("quantityBySource")
	public List<ArticleQuantityBySource> getQuantityBySource()
	{
		return quantityBySource;
	}

	/**
	 * @param quantityBySource
	 *           the quantityBySource to set
	 */
	@JsonSetter("quantityBySource")
	public void setQuantityBySource(final List<ArticleQuantityBySource> quantityBySource)
	{
		this.quantityBySource = quantityBySource;
	}
}

