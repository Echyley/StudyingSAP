/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.core.test.property;

/**
 * Class for holding properties added during runtime.
 */
class DynamicProperties extends LinkedProperties
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Standard constructor.
	 * 
	 * @param parent
	 *           parent linked properties
	 */
	public DynamicProperties(final LinkedProperties parent)
	{
		super(parent);
	}

	@Override
	public String getInfo()
	{
		return "Dynamic properties";
	}

}
