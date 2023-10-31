/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.sapoaacosintegration.services.atp.response;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.sap.sapoaacosintegration.services.atp.request.CosSource;


public class ArticleQuantityBySource
{

	private Double quantity = null;
	private CosSource source = null;


	/**
	 * @return the quantity
	 */
	@JsonGetter("quantity")
	public Double getQuantity()
	{
		return quantity;
	}

	/**
	 * @param quantity
	 *           the quantity to set
	 */
	@JsonSetter("quantity")
	public void setQuantity(final Double quantity)
	{
		this.quantity = quantity;
	}


	/**
	 * @return the sources
	 */
	@JsonGetter("sources")
	public CosSource getSource()
	{
		return source;
	}

	/**
	 * @param source
	 *           the source to set
	 */
	@JsonSetter("source")
	public void setSource(final CosSource source)
	{
		this.source = source;
	}

}

