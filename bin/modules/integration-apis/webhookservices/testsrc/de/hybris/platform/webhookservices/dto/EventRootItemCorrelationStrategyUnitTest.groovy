/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.webhookservices.dto

import de.hybris.bootstrap.annotations.UnitTest
import de.hybris.platform.core.PK
import de.hybris.platform.testframework.JUnitPlatformSpecification
import de.hybris.platform.webhookservices.event.WebhookEvent
import org.junit.Test

@UnitTest
class EventRootItemCorrelationStrategyUnitTest extends JUnitPlatformSpecification {

    @Test
    def "correlationKey is the string value of the webhookEvent's itemPK"() {
        given:
        def correlationStrategy = new EventRootItemCorrelationStrategy()

        expect:
        correlationStrategy.correlationKey(webhookEvent) == expectedValue

        where:
        webhookEvent                 | expectedValue
        webhookEvent(PK.fromLong(1)) | PK.fromLong(1).toString()
        null                         | ""
    }

    private WebhookEvent webhookEvent(final PK rootItemPK) {
        Stub(WebhookEvent) {
            getPk() >> rootItemPK
        }
    }
}
