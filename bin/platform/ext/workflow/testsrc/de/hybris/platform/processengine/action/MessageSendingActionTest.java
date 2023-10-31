/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.processengine.action;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.processengine.model.BusinessProcessModel;
import de.hybris.platform.servicelayer.event.events.MessageSendingException;
import de.hybris.platform.task.RetryLaterException;

import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.messaging.MessageChannel;

@RunWith(MockitoJUnitRunner.class)
@UnitTest
public class MessageSendingActionTest
{
	@InjectMocks
	private final MessageSendingAction messageSendingAction = new MessageSendingAction();

	@Mock
	MessageChannel channel;

	@Test
	public void testExecute() throws RetryLaterException, Exception
	{
		final BusinessProcessModel process = new BusinessProcessModel();
		Mockito.when(channel.send(Mockito.anyObject())).thenReturn(Boolean.TRUE);
		messageSendingAction.execute(process);
	}

	@Test
	public void testExecuteError() throws RetryLaterException, Exception
	{
		final BusinessProcessModel process = new BusinessProcessModel();
		Mockito.when(channel.send(Mockito.anyObject())).thenReturn(Boolean.FALSE);
		Assertions.assertThatThrownBy(() -> messageSendingAction.execute(process)).isInstanceOf(MessageSendingException.class);
	}
}
