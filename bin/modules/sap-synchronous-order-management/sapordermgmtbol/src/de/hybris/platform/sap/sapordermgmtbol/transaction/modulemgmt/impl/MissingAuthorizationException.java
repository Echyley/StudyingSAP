/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.sapordermgmtbol.transaction.modulemgmt.impl;

/**
 * This exception is thrown when checking an authorization object was not successful. <br>
 * 
 */
public class MissingAuthorizationException extends Exception
{

	/**
	 * Standard constructor. <br>
	 * 
	 * @param message
	 *           message that describes the missing authorization
	 */
	public MissingAuthorizationException(final String message)
	{
		super(message);
	}

	/**
     * 
     */
	private static final long serialVersionUID = 1L;

}
