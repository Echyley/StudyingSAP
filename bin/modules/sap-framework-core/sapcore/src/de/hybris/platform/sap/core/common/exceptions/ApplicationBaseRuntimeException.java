/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.core.common.exceptions;

import de.hybris.platform.sap.core.common.message.Message;


/**
 * Base runtime exception to be used by the applications running on the SPA integration framework.
 */
public class ApplicationBaseRuntimeException extends CoreBaseRuntimeException
{

	private static final long serialVersionUID = -4940541193479745949L;

	/**
	 * Standard constructor for ApplicationBaseException with the specified detail message.
	 * 
	 * @param message
	 *           the detail message.
	 */
	public ApplicationBaseRuntimeException(final String message)
	{
		super(message);
	}

	/**
	 * Standard constructor for ApplicationBaseException using a simple message text.
	 * 
	 * @param message
	 *           message text.
	 * @param rootCause
	 *           exception which causes the exception
	 */
	public ApplicationBaseRuntimeException(final String message, final Throwable rootCause)
	{
		super(message, rootCause);
	}



	/**
	 * Standard constructor for ApplicationBaseRuntimeException using a message object see
	 * {@link de.hybris.platform.sap.core.common.message.Message} for details.
	 * 
	 * @param message
	 *           message which identifies the error message.
	 */
	public ApplicationBaseRuntimeException(final Message message)
	{
		super(message);
	}


	/**
	 * 
	 * Standard constructor for ApplicationBaseRuntimeException using a message object see
	 * {@link de.hybris.platform.sap.core.common.message.Message} for details.
	 * 
	 * @param message
	 *           message object which identifies the error message.
	 * @param rootCause
	 *           exception which causes the exception
	 */
	public ApplicationBaseRuntimeException(final Message message, final Throwable rootCause)
	{
		super(message, rootCause);
	}


}
