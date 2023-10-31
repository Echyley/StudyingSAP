/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.sapcreditcheck.exceptions;

/**
 * 
 */
public class SapCreditCheckException extends RuntimeException 
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 6915031997744276220L;

	/**
	 * @param exp
	 */
	public SapCreditCheckException(final Exception exp)
	{
		super(exp);
	}
}
