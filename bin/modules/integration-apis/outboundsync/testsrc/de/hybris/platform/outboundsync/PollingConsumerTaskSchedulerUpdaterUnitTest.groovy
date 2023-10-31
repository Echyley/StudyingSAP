/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.outboundsync

import de.hybris.bootstrap.annotations.UnitTest
import de.hybris.platform.testframework.JUnitPlatformSpecification
import org.junit.Test
import org.springframework.context.ApplicationContext
import org.springframework.integration.channel.QueueChannel
import org.springframework.integration.endpoint.PollingConsumer
import org.springframework.messaging.MessageChannel
import org.springframework.scheduling.TaskScheduler
import spock.lang.Specification
import spock.lang.Unroll

@UnitTest
class PollingConsumerTaskSchedulerUpdaterUnitTest extends JUnitPlatformSpecification {

	@Test
	@Unroll
	def "#param cannot be null"() {
		when:
		new PollingConsumerTaskSchedulerUpdater(channelId, taskScheduler)

		then:
		def e = thrown IllegalArgumentException
		e.message == "$param cannot be null"

		where:
		param              | channelId   | taskScheduler
		'Input channel id' | null        | Stub(TaskScheduler)
		'Task scheduler'   | 'channelId' | null
	}

	@Test
	@Unroll
	def "Task scheduler is #updateCondition when a PollingConsumer has a #channelType.simpleName that #matchCondition the search input channel id #searchChannelId"() {
		given: 'task schedulers'
		def updatedTaskScheduler = Stub TaskScheduler
		and: 'PollingConsumer has the search input channel id'
		def channelId = 'channelId'
		def pollingConsumer = Mock(PollingConsumer) {
			getInputChannel() >> Stub(channelType) {
				getBeanName() >> channelId
			}
		}
		and: 'application context containing the PollingConsumers'
		def appContext = Stub(ApplicationContext) {
			getBeansOfType(PollingConsumer) >> ['serviceActivator': pollingConsumer]
		}
		and:
		def updater = new PollingConsumerTaskSchedulerUpdater(searchChannelId, updatedTaskScheduler)

		when:
		updater.applicationContext = appContext

		then:
		invocations * pollingConsumer.setTaskScheduler(updatedTaskScheduler)

		where:
		updateCondition | channelType    | matchCondition   | searchChannelId    | invocations
		'updated'       | QueueChannel   | 'matches'        | 'channelId'        | 1
		'not updated'   | QueueChannel   | 'does not match' | 'anotherChannelId' | 0
		'not updated'   | MessageChannel | 'matches'        | 'channelId'        | 0
	}
}
