/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.core.common.message;

import org.junit.Assert;
import org.junit.Test;

import de.hybris.bootstrap.annotations.UnitTest;


/**
 * MessageList test.
 */
@UnitTest
public class MessageListTest
{

	/**
	 * Add and remove messages from a message-list.
	 */
	@Test
	public void testMessageList()
	{
		final MessageList list = new MessageList();

		final String key = "key";
		final String key2 = "key2";
		final Message msg = new Message(Message.INFO, key);
		final Message msg2 = new Message(Message.INFO, key2);
		final String property = "prop";
		msg2.setProperty(property);

		list.add(msg);
		list.add(msg2);

		Assert.assertEquals(2, list.size());
		list.remove(key);
		Assert.assertEquals(1, list.size());

		final Message get = list.get(Message.INFO, property);
		Assert.assertEquals(msg2, get);
	}
}
