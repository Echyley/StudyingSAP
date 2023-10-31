/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.core.bol.businessobject;

import de.hybris.platform.sap.core.common.message.Message;
import de.hybris.platform.sap.core.common.message.MessageList;


/**
 * System failure of underlying backend system.
 */
public class SystemFailureException extends CommunicationException
{

	private static final long serialVersionUID = -792263602801264272L;

	/**
	 * Constructor.
	 * 
	 * @param msg
	 *           Message for the Exception
	 * @param msgList
	 *           List of the messages added to the exception
	 */
	public SystemFailureException(final String msg, final MessageList msgList)
	{
		super(msg, msgList);
	}

	/**
	 * Constructor.
	 * 
	 * @param msg
	 *           Message for the Exception
	 * @param message
	 *           message added to the exception
	 */
	public SystemFailureException(final String msg, final Message message)
	{
		super(msg, message);
	}

	/**
	 * Constructor.
	 * 
	 * @param msg
	 *           Message for the Exception
	 */
	public SystemFailureException(final String msg)
	{
		super(msg);
	}

	/**
	 * Constructor.
	 */
	public SystemFailureException()
	{
		super();
	}

	/**
	 * Standard constructor for SystemFailureException using a simple message text. <br>
	 * 
	 * @param msg
	 *           message text
	 * @param message
	 *           message object.
	 * @param rootCause
	 *           exception which causes the exception
	 */
	public SystemFailureException(final String msg, final Message message, final Throwable rootCause)
	{
		super(msg, message, rootCause);
	}

}
