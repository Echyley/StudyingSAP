/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.sapcentralorderservices.exception;

/**
 *
 * User Defined Exception: SapCentralOrderException
 */
public class SapCentralOrderException extends Exception
{
	/**
	 *
	 * @param exceptionString
	 */
	public SapCentralOrderException(final String exceptionMsg)
	{
		// Call constructor of parent Exception
		super(exceptionMsg);
   }
}
