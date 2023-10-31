/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.core.jco.exceptions;

import de.hybris.platform.sap.core.common.exceptions.CoreBaseException;


/**
 * Thrown if something goes wrong in the backend layer.
 * 
 */
public class BackendException extends CoreBaseException
{

	private static final long serialVersionUID = -8529406933930670212L;

	/**
	 * Constructor.
	 * 
	 * @param msg
	 *           Message for the Exception
	 */
	public BackendException(final String msg)
	{
		super(msg);
	}


	/**
	 * Constructor.
	 * 
	 * @param msg
	 *           Message for the Exception
	 * @param ex
	 *           root cause
	 */
	public BackendException(final String msg, final Throwable ex)
	{
		super(msg, ex);
	}


}
