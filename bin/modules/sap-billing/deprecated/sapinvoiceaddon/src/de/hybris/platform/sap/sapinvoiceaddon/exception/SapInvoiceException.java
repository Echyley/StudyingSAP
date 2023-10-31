/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.sapinvoiceaddon.exception;

/**
 * Thrown if something wrong in invoice details.
 *
 */
public class SapInvoiceException extends Exception
{

	private static final long serialVersionUID = -6529406933930670212L;

	/**
	 * Constructor.
	 *
	 * @param msg
	 *           Message for the Exception
	 */
	public SapInvoiceException(final String msg)
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
	public SapInvoiceException(final String msg, final Throwable ex)
	{
		super(msg, ex);
	}


}
