package com.hybris.backoffice.events;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import de.hybris.platform.servicelayer.event.events.AbstractEvent;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class AbstractBackofficeEventListenerTest
{

	private AbstractBackofficeEventListener listener;
	@Mock
	private AbstractEvent evtMock;
	@Mock
	private ExternalEventCallback<AbstractEvent> cbMock;
	@Mock
	private ExternalEventCallback<AbstractEvent> cb2Mock;

	@Before
	public void setUp()
	{
		listener = new TestAbstractBackofficeEventListener();
	}

	@Test
	public void shouldRegisterCallbackSuccessfully()
	{
		listener.registerCallback(cbMock);
		listener.registerCallback(cb2Mock);

		assertThat(listener.isCallbackRegistered(cbMock)).isTrue();
		assertThat(listener.isCallbackRegistered(cb2Mock)).isTrue();
	}

	@Test
	public void shouldIgnoreWhenCallbackIsNull()
	{
		listener.registerCallback(null);
		assertThat(listener.isCallbackRegistered(null)).isFalse();
		listener.unregisterCallback(null);
	}

	@Test
	public void shouldBeOnlyOneIfSameCallbackRegisteredManyTimes()
	{
		listener.registerCallback(cbMock);
		listener.registerCallback(cbMock);
		listener.onEvent(evtMock);
		assertThat(listener.isCallbackRegistered(cbMock)).isTrue();
		verify(cbMock, times(1)).onEvent(evtMock);
	}

	@Test
	public void shouldUnregisterSuccessfully()
	{
		listener.registerCallback(cbMock);
		listener.unregisterCallback(cbMock);
		assertThat(listener.isCallbackRegistered(cbMock)).isFalse();

		listener.onEvent(evtMock);
		verify(cbMock, never()).onEvent(evtMock);
	}

	@Test
	public void shouldExecuteCallbackWhenEventComing()
	{
		listener.registerCallback(cbMock);
		listener.registerCallback(cb2Mock);

		listener.onEvent(evtMock);
		verify(cbMock, times(1)).onEvent(evtMock);
		verify(cb2Mock, times(1)).onEvent(evtMock);
	}

	private class TestAbstractBackofficeEventListener extends AbstractBackofficeEventListener<AbstractEvent>
	{

	}
}
