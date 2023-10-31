/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.search.impl;

import de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.search.interf.SearchFilter;


/**
 * 
 */
public class SearchFilterImpl implements SearchFilter
{

	private String shippingStatus;
	private String product;
	private String soldToId;




	@Override
	public String getShippingStatus()
	{
		return shippingStatus;
	}

	@Override
	public void setShippingStatus(final String shippingStatus)
	{
		this.shippingStatus = shippingStatus;
	}


	@Override
	public String getProductID()
	{
		return product;
	}

	@Override
	public void setProductID(final String product)
	{
		this.product = product;

	}


	@Override
	public String getSoldToId()
	{
		return soldToId;
	}

	@Override
	public void setSoldToId(final String soldToId)
	{
		this.soldToId = soldToId;
	}


}
