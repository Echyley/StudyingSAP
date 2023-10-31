/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.core.test.property;

import java.util.Properties;

/**
 * Single-linked list of Properties definitions.
 * 
 */
abstract class LinkedProperties extends Properties
{
	private static final long serialVersionUID = 1L;
	private LinkedProperties parent = null;

	/**
	 * Constructor.
	 * 
	 * @param parent
	 *           parent linked properties
	 */
	public LinkedProperties(final LinkedProperties parent)
	{
		super(parent);
		this.parent = parent;
	}

	/**
	 * Returns the parent linked properties.
	 * 
	 * @return parent parent linked properties
	 */
	public LinkedProperties getParent()
	{
		return parent;
	}

	/**
	 * Returns information about the source.
	 * 
	 * @return information
	 */
	public abstract String getInfo();
}
