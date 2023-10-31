/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.outboundsync

import de.hybris.platform.core.Registry
import de.hybris.platform.outboundsync.dto.OutboundItemDTO
import de.hybris.platform.outboundsync.events.CompletedOutboundSyncEvent
import de.hybris.platform.outboundsync.job.ItemChangeSender
import de.hybris.platform.servicelayer.event.EventService
import org.junit.rules.ExternalResource

import java.util.concurrent.ConcurrentLinkedQueue

class TestItemChangeSender extends ExternalResource implements ItemChangeSender {

	Queue<OutboundItemDTO> items
	EventService eventService

	@Override
	void send(final OutboundItemDTO change) {
		items.add(change)
		eventService.publishEvent(CompletedOutboundSyncEvent.successfulSync(change))
	}

	int getQueueSize() {
		items.size()
	}

	OutboundItemDTO getNextItem() {
		items.poll()
	}

	@Override
	protected void before()
	{
		reset()
		eventService = getEventService()
	}

	@Override
	protected void after() {
		reset()
	}

	void reset() {
		items = new ConcurrentLinkedQueue<>()
	}

	static EventService getEventService() {
		Registry.applicationContext.getBean("eventService", EventService)
	}
}
