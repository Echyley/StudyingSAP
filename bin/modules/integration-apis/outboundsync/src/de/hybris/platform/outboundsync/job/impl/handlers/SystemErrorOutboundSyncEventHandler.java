/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.outboundsync.job.impl.handlers;

import de.hybris.platform.outboundsync.events.SystemErrorOutboundSyncEvent;
import de.hybris.platform.outboundsync.job.impl.OutboundSyncEventHandler;
import de.hybris.platform.outboundsync.job.impl.OutboundSyncState;
import de.hybris.platform.outboundsync.job.impl.OutboundSyncStateBuilder;

import javax.validation.constraints.NotNull;

/**
 * Implementation of {@link OutboundSyncEventHandler} for events of type {@link SystemErrorOutboundSyncEvent}
 */
public final class SystemErrorOutboundSyncEventHandler implements OutboundSyncEventHandler<SystemErrorOutboundSyncEvent>
{
	private SystemErrorOutboundSyncEventHandler()
	{
		// non-instantiable
	}

	/**
	 * Instantiates this handler
	 *
	 * @return new handler instance
	 */
	public static SystemErrorOutboundSyncEventHandler createHandler()
	{
		return new SystemErrorOutboundSyncEventHandler();
	}

	@Override
	public Class<SystemErrorOutboundSyncEvent> getHandledEventClass()
	{
		return SystemErrorOutboundSyncEvent.class;
	}

	@Override
	public OutboundSyncState handle(@NotNull final SystemErrorOutboundSyncEvent event, @NotNull final OutboundSyncState currentState)
	{
		final int postSystemicErrorCount = currentState.getUnprocessedCount() + event.getChangesProcessed();
		return OutboundSyncStateBuilder.from(currentState)
		                               .withUnprocessedCount(postSystemicErrorCount)
		                               .withSystemicError(true)
		                               .build();
	}
}
