/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.integrationservices.util

import de.hybris.bootstrap.annotations.UnitTest
import de.hybris.platform.testframework.JUnitPlatformSpecification
import org.junit.Test
import org.springframework.integration.endpoint.PollingConsumer
import org.springframework.scheduling.TaskScheduler

@UnitTest
class PollingConsumerTaskSchedulerUpdaterUnitTest extends JUnitPlatformSpecification {

    @Test
    def "Task scheduler will throw IllegalArgumentException when #param is null"() {
        when:
        new PollingConsumerTaskSchedulerUpdater(pollingConsumer, taskScheduler)

        then:
        def e = thrown IllegalArgumentException
        e.message.contains "$param cannot be null"

        where:
        param             | pollingConsumer       | taskScheduler
        "pollingConsumer" | null                  | Stub(TaskScheduler)
        "taskScheduler"   | Stub(PollingConsumer) | null
    }

    @Test
    def "Task scheduler is set when creating pollingConsumer"() {
        given: 'task schedulers'
        def updatedTaskScheduler = Stub TaskScheduler
        and: 'PollingConsumer has the search input channel id'
        def pollingConsumer = Mock PollingConsumer

        when:
        new PollingConsumerTaskSchedulerUpdater(pollingConsumer, updatedTaskScheduler)

        then:
        1 * pollingConsumer.setTaskScheduler(updatedTaskScheduler)
    }
}
