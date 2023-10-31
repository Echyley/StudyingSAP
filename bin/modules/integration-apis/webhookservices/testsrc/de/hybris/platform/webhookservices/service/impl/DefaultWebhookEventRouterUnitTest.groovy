/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.webhookservices.service.impl

import de.hybris.bootstrap.annotations.UnitTest
import de.hybris.platform.apiregistryservices.dto.EventSourceData
import de.hybris.platform.testframework.JUnitPlatformSpecification
import de.hybris.platform.webhookservices.event.ItemCreatedEvent
import de.hybris.platform.webhookservices.event.ItemDeletedEvent
import de.hybris.platform.webhookservices.event.ItemSavedEvent
import de.hybris.platform.webhookservices.event.ItemUpdatedEvent
import org.junit.Test
import org.springframework.messaging.support.GenericMessage

@UnitTest
class DefaultWebhookEventRouterUnitTest extends JUnitPlatformSpecification {

	private static final String DELETE_EVENT_CHANNEL = "deleteEventChannel"
	private static final String PERSISTENCE_EVENT_CHANNEL = "persistenceEventChannel"

	def router = new DefaultWebhookEventRouter()

	@Test
	def 'routes #event.name to #channel'() {
		given:
		def eventSourceData = Stub(EventSourceData) { getEvent() >> Stub(event) }
		def message = new GenericMessage<>(eventSourceData)

		expect:
		router.route(message) == channel

		where:
		channel                   | event
		PERSISTENCE_EVENT_CHANNEL | ItemCreatedEvent
		PERSISTENCE_EVENT_CHANNEL | ItemUpdatedEvent
		PERSISTENCE_EVENT_CHANNEL | ItemSavedEvent
		DELETE_EVENT_CHANNEL      | ItemDeletedEvent
	}
	
}

