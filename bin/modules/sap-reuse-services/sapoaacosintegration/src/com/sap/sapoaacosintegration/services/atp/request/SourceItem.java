/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.sapoaacosintegration.services.atp.request;


import com.fasterxml.jackson.annotation.JsonGetter;


/**
 *
 */
public class SourceItem
{
	private String productId;
	private String unit;




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

	@JsonGetter("unit")
	public void setUnit(final String unit)
	{
		this.unit = unit;
	}



}
