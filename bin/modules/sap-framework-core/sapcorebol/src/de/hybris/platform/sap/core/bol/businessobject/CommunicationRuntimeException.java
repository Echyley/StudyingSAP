/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.core.bol.businessobject;


/**
 * Represents a general superclass for all exceptions of the back end layer caused by communication problems (network
 * failures etc.).
 */
public class CommunicationRuntimeException extends BORuntimeException
{


	private static final long serialVersionUID = -6659050592438056173L;


	/**
	 * Constructor.
	 * 
	 * @param msg
	 *           Message for the Exception
	 */
	public CommunicationRuntimeException(final String msg)
	{
		super(msg);
	}

	/**
	 * Constructor.
	 */
	public CommunicationRuntimeException()
	{
		super();
	}


	/**
	 * Standard constructor for CommunicationException using a simple message text. <br>
	 * 
	 * @param msg
	 *           message text.
	 * @param rootCause
	 *           exception which causes the exception
	 */
	public CommunicationRuntimeException(final String msg, final Throwable rootCause)
	{
		super(msg, rootCause);
	}

}
