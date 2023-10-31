/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.core.jco.runtime;

import com.sap.conn.jco.ext.JCoSessionReference;


/**
 * SAPJCoSessionReference is a simple reference to a SAPHyprisSession ID.
 */
public class SAPJCoSessionReference implements JCoSessionReference
{

	private final String id;


	/**
	 * Constructor.
	 * 
	 * @param id
	 *           session reference id
	 */
	public SAPJCoSessionReference(final String id)
	{
		super();
		this.id = id;
	}

	@Override
	public void contextFinished()
	{
		// Not required in SAP hybris integration project

	}

	@Override
	public void contextStarted()
	{
		// Not required in SAP hybris integration project

	}

	@Override
	public String getID()
	{
		return id;
	}
}
