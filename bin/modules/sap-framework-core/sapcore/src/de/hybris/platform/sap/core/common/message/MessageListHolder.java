/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.core.common.message;

import java.io.Serializable;

/**
 * The MessageListHolder interface should be implemented by objects which holds a message list to handles messages.
 */
public interface MessageListHolder extends Serializable
{


	/**
	 * Constant to define the state valid.
	 */
	public final static int VALID = 0;


	/**
	 * Constant to define the state invalid.
	 */
	public final static int INVALID = 1;


	/**
	 * Add a message to the message list.
	 * 
	 * @param message
	 *           message to add
	 */
	public void addMessage(Message message);



	/**
	 * Clear all messages in the message list.
	 * 
	 */
	public void clearMessages();


	/**
	 * Returns the messages list object itself. Always the reference of the original object should be provided.
	 * 
	 * @return reference to message list
	 */
	public MessageList getMessageList();


}
