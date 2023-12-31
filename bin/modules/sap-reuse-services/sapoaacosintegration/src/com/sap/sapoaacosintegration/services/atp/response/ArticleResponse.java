/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.sapoaacosintegration.services.atp.response;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonSetter;


/**
 *
 */
public class ArticleResponse
{
	private String sourceId;
	private String productId;
	private Double quantity;
	private String unit;
	private String availableFrom;

	/**
	 * @return the availableFrom
	 */
	@JsonGetter("availableFrom")
	public String getAvailableFrom()
	{
		return availableFrom;
	}

	/**
	 * @param availableFrom
	 *           the availableFrom to set
	 */
	@JsonSetter("availableFrom")
	public void setAvailableFrom(final String availableFrom)
	{
		this.availableFrom = availableFrom;
	}


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
	 * @return the sourceId
	 */
	public String getSourceId()
	{
		return sourceId;
	}

	/**
	 * @param sourceId
	 *           the sourceId to set
	 */
	public void setSourceId(final String sourceId)
	{
		this.sourceId = sourceId;
	}

}
