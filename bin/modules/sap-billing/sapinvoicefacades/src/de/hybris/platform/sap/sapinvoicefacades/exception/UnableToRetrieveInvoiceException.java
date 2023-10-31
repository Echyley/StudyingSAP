/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.sapinvoicefacades.exception;

/**
 * 
 */
public class UnableToRetrieveInvoiceException extends Exception
{
	
	private static final long serialVersionUID = -6539406933930670212L;
	
	public UnableToRetrieveInvoiceException(final String msg)
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
	public UnableToRetrieveInvoiceException(final String msg, final Throwable ex)
	{
		super(msg, ex);
	}
}
