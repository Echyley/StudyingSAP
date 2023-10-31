/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.sapinvoicebol.invoice.exception;

/**
 * 
 */
public class SapInvoiceException extends RuntimeException 
{
	/**
	 * @param exp
	 */
	public SapInvoiceException(final Exception exp)
	{
		super(exp);
	}
}
