/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.servicelayer.event.events;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyObject;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.servicelayer.cluster.ClusterService;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatcher;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;

@RunWith(MockitoJUnitRunner.class)
@UnitTest
public class MessageSendingEventListenerTest
{

	/**
	 * Custom event for testing purposes.
	 */
	private class MyCustomEvent extends AbstractEvent
	{ //
	}

	@InjectMocks
	private final MessageSendingEventListener eventListener = new MessageSendingEventListener();
	@Mock
	private MessageChannel messageChannel;
	@Mock
	private Message<AbstractEvent> message;
	@Mock
	private ClusterService clusterService;


	/**
	 * Test method for
	 * {@link MessageSendingEventListener#onEvent(de.hybris.platform.servicelayer.event.events.AbstractEvent)} .
	 */
	@Test
	public void shouldNotSendMessageToTheChannelWhenEventClassIsNotAssignableFromPassedEvent()
	{
		// given
		eventListener.setEventClass(MyCustomEvent.class);
		final TestingEvent event = new TestingEvent("bar");

		// when
		eventListener.onApplicationEvent(event);

		// then
		verify(clusterService, times(1)).getClusterIslandId();
		verify(clusterService, times(1)).getClusterId();
		verify(messageChannel, times(0)).send((Message<?>) anyObject());
	}

	/**
	 * Test method for
	 * {@link MessageSendingEventListener#onEvent(de.hybris.platform.servicelayer.event.events.AbstractEvent)} .
	 */
	@Test
	public void shouldThrowMessageSendingExceptionWhenSendReturnsFalse()
	{
		// given
		eventListener.setEventClass(MyCustomEvent.class);
		final MyCustomEvent event = new MyCustomEvent();
		Mockito.lenient().when(message.getPayload()).thenReturn(event);
		given(Boolean.valueOf(messageChannel.send((Message<?>) anyObject()))).willReturn(Boolean.FALSE);
		try
		{
			// when
			eventListener.onApplicationEvent(event);
		}
		catch (final MessageSendingException e)
		{
			// then
			verify(messageChannel, times(1)).send(argThat(isValidMessage(event)));
			assertThat(e.getMessage()).contains("Message of type " + event.getClass() + " could not be sent");
		}

	}

	/**
	 * Test method for
	 * {@link MessageSendingEventListener#onEvent(de.hybris.platform.servicelayer.event.events.AbstractEvent)} .
	 */
	@Test
	public void shouldSendMessageToTheChannelWhenEventClassAssignableFromPassedEvent()
	{
		// given
		eventListener.setEventClass(MyCustomEvent.class);
		final MyCustomEvent event = new MyCustomEvent();
		given(Boolean.valueOf(messageChannel.send((Message<?>) anyObject()))).willReturn(Boolean.TRUE);

		// when
		eventListener.onApplicationEvent(event);

		// then
		verify(messageChannel, times(1)).send(argThat(isValidMessage(event)));
	}

	private ArgumentMatcher<Message<MyCustomEvent>> isValidMessage(final MyCustomEvent event)
	{
		return myCustomEventMessage -> event.equals(myCustomEventMessage.getPayload());
	}
}
