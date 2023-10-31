/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.webhookservices.event

import de.hybris.bootstrap.annotations.UnitTest
import de.hybris.platform.core.PK
import de.hybris.platform.outboundservices.event.impl.DefaultEventType
import de.hybris.platform.testframework.JUnitPlatformSpecification
import de.hybris.platform.tx.AfterSaveEvent
import org.junit.Test
import spock.lang.Shared

@UnitTest
class ItemDeletedEventUnitTest extends JUnitPlatformSpecification {

	private static final def PK1 = PK.fromLong(4321)
	private static final def PK2 = PK.fromLong(1234)

	@Shared
	def event = new ItemDeletedEvent(removeAfterSaveEvent(PK1))

	@Test
	def "IllegalArgumentException when instantiated with #description AfterSaveEvent"() {
		given:
		def afterSaveEvent = new AfterSaveEvent(PK1, eventType)

		when:
		new ItemDeletedEvent(afterSaveEvent)

		then:
		thrown IllegalArgumentException

		where:
		description | eventType
		'CREATE'    | AfterSaveEvent.CREATE
		'UPDATE'    | AfterSaveEvent.UPDATE
	}

	@Test
	def "converts AfterSaveEvent Remove type to Deleted EventType"() {
		given:
		def afterSaveEvent = removeAfterSaveEvent(PK1)
		and:
		def deletedEvent = new ItemDeletedEvent(afterSaveEvent)

		expect:
		deletedEvent.eventType.is DefaultEventType.DELETED
		deletedEvent.pk.is PK1
	}

	@Test
	def "hashcode() equal is #result when second event #description"() {
		expect:
		(event.hashCode() == event2.hashCode()) == result

		where:
		description                                         | event2                                          | result
		'same object'                                       | event                                           | true
		'different object with same class and pk'           | new ItemDeletedEvent(removeAfterSaveEvent(PK1)) | true
		'different object with different class and same pk' | new ItemCreatedEvent(removeAfterSaveEvent(PK1)) | false
		'different object with same class and different pk' | new ItemDeletedEvent(removeAfterSaveEvent(PK2)) | false
		'different object'                                  | new Integer(1)                                  | false
	}

	@Test
	def "equals() is #result when compared to #description"() {
		expect:
		(event == event2) == result

		where:
		description                          | event2                                          | result
		'same object'                        | event                                           | true
		'different object with same pk'      | new ItemDeletedEvent(removeAfterSaveEvent(PK1)) | true
		'different object with different pk' | new ItemDeletedEvent(removeAfterSaveEvent(PK2)) | false
		'different object'                   | new Integer(1)                                  | false
		'null'                               | null                                            | false
	}

	private static AfterSaveEvent removeAfterSaveEvent(PK pk) {
		new AfterSaveEvent(pk, AfterSaveEvent.REMOVE)
	}
}
