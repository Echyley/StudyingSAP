/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.sapcommonbol.businesspartner.businessobject.impl;

import de.hybris.platform.sap.core.common.exceptions.ApplicationBaseRuntimeException;
import de.hybris.platform.sap.sapcommonbol.businesspartner.businessobject.interf.County;


/**
 * BO representation of a county (a geographical entity used for tax jurisdiction code determination)
 * 
 */
public class CountyImpl implements County
{

	private String countyText;
	private String taxJurCode;

	@Override
	public String getCountyText()
	{
		return countyText;
	}
	
	@Override
	public void setCountyText(final String countyText)
	{
		this.countyText = countyText;
	}
	
	@Override
	public String getTaxJurCode()
	{
		return taxJurCode;
	}
	@Override
	public void setTaxJurCode(final String taxJurCode)
	{
		this.taxJurCode = taxJurCode;
	}

	@Override
	@SuppressWarnings("squid:S2975")
	public County clone()
	{
		County clone;
		try
		{
			clone = (County) super.clone();
		}
		catch (final CloneNotSupportedException e)
		{
			throw new ApplicationBaseRuntimeException(
					"Failed to clone Object, check whether Cloneable Interface is still implemented", e);
		}
		return clone;
	}

}
