/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.core.bol.logging;

/**
 * Log Category.
 */
public class LogCategory
{

	private final String categoryValue;

	/**
	 * Standard constructor.
	 * 
	 * @param categoryValue
	 *           log category
	 */
	public LogCategory(final String categoryValue)
	{
		super();
		this.categoryValue = categoryValue;
	}

	/**
	 * Returns the log category value.
	 * 
	 * @return the categoryValue
	 */
	public String getCategoryValue()
	{
		return categoryValue;
	}

}
