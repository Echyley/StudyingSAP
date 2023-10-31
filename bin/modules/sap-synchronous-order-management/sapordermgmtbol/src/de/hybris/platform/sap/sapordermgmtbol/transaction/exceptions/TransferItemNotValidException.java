/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.sapordermgmtbol.transaction.exceptions;

import de.hybris.platform.sap.core.common.exceptions.ApplicationBaseException;


/**
 * Exception class for handling invalid exception.
 *
 */
@SuppressWarnings("squid:S2166")
public class TransferItemNotValidException extends ApplicationBaseException
{

	/**
	  *
	  */
	private static final long serialVersionUID = 5726866258031091847L;

	/**
	 * Constructor. <br>
	 *
	 * @param string
	 *           explaining the problem
	 */
	public TransferItemNotValidException(final String string)
	{
		super(string);
	}

	/**
	 * Constructor. <br>
	 *
	 * @param msg
	 *           string describing the problem
	 * @param rootCause
	 *           to keep the stack trace
	 */
	public TransferItemNotValidException(final String msg, final Throwable rootCause)
	{
		super(msg, rootCause);
	}

}
