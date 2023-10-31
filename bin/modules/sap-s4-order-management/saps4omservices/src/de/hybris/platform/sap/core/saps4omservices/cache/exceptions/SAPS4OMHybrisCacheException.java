/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.core.saps4omservices.cache.exceptions;

import de.hybris.platform.sap.core.common.exceptions.CoreBaseException;
import de.hybris.platform.sap.core.common.message.Message;
import de.hybris.platform.sap.core.common.message.MessageList;


/**
 * The <code>SAPS4OMHybrisCacheException</code> is thrown if something goes wrong during cache manipulation.
 * 
 */
public class SAPS4OMHybrisCacheException extends CoreBaseException
{


	private static final long serialVersionUID = -3572283983746173989L;

	/**
	 * Standard constructor method.
	 */
	public SAPS4OMHybrisCacheException()
	{
		super();
	}

	/**
	 * Standard constructor for SAPS4OMHybrisCacheException using a simple message text. <br>
	 * 
	 * @param msg
	 *           message.
	 */
	public SAPS4OMHybrisCacheException(final String msg)
	{
		super(msg);
	}

	/**
	 * Standard constructor for SAPS4OMHybrisCacheException using a simple message text. <br>
	 * 
	 * @param msg
	 *           message.
	 * @param rootCause
	 *           exception that causes the exception
	 */
	public SAPS4OMHybrisCacheException(final String msg, final Throwable rootCause)
	{
		super(msg, rootCause);
	}

	/**
	 * Constructor.
	 * 
	 * @param msg
	 *           Message for the Exception class
	 * @param msgList
	 *           List of the messages added to the exception class
	 */
	public SAPS4OMHybrisCacheException(final String msg, final MessageList msgList)
	{
		super(msg);
		this.messageList = msgList;
	}

	/**
	 * Constructor.
	 * 
	 * @param msg
	 *           Message for the Exception class
	 * @param message
	 *           message added to the exception class
	 */
	public SAPS4OMHybrisCacheException(final String msg, final Message message)
	{
		super(msg);
		this.messageList.add(message);
	}
}
