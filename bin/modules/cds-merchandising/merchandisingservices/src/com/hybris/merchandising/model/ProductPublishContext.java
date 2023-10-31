/**
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.hybris.merchandising.model;

/**
 * Simple POJO for sending additional information in publish product request
 */
public class ProductPublishContext
{
	//Total number of products
	private Long total;

	public ProductPublishContext()
	{
	}

	public ProductPublishContext(final Long total)
	{
		this.total = total;
	}

	public Long getTotal()
	{
		return total;
	}

	public void setTotal(final Long total)
	{
		this.total = total;
	}
}
