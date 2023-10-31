/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.core.jco.exceptions;

/**
 * Thrown if something goes wrong in the backend system.
 */
public class AbapBackendException extends BackendException
{

	/**
     * 
     */
	private static final long serialVersionUID = 5526290736509791245L;

	/**
	 * Constructor.
	 * 
	 * @param msg
	 *           Message for the Exception
	 */
	public AbapBackendException(final String msg)
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
	public AbapBackendException(final String msg, final Exception ex)
	{
		super(msg, ex);
	}

}
