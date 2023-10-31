package de.hybris.platform.webhookservices.service.impl

import de.hybris.bootstrap.annotations.UnitTest
import de.hybris.platform.servicelayer.event.EventSender
import de.hybris.platform.servicelayer.event.events.AbstractEvent
import de.hybris.platform.testframework.JUnitPlatformSpecification
import de.hybris.platform.webhookservices.event.WebhookEvent
import org.junit.Test

@UnitTest
class DefaultWebhookSyncItemRouterUnitTest extends JUnitPlatformSpecification {

    def eventSender = Mock EventSender

    def router = new DefaultWebhookSyncItemRouter(eventSender)

    @Test
    def 'throw IllegalArgumentException when eventSender is null'() {
        when:
        new DefaultWebhookSyncItemRouter(null)

        then:
        def e = thrown IllegalArgumentException
        e.message.contains("eventSender cannot be null")
    }

    @Test
    def 'ignore #result when routing collection #condition'() {
        when:
        router.route(events)

        then:
        calls * eventSender.sendEvent(events ? events[0] : null)

        where:
        events                   | calls | condition                                                | result
        null                     | 0     | "is null"                                                | "the collection"
        [null]                   | 0     | "has null events"                                        | "null events"
        [webhookEvent()]         | 0     | "has webhook events which are not abstract events"       | "non abstract events"
        [abstractWebhookEvent()] | 1     | "has webhook events only which are abstract events also" | "nothing"
    }

    private WebhookEvent webhookEvent() {
        Stub(WebhookEvent)
    }

    private TestAbstractWebhookEvent abstractWebhookEvent() {
        Stub(TestAbstractWebhookEvent)
    }

    private abstract class TestAbstractWebhookEvent extends AbstractEvent implements WebhookEvent {
        //Test implementation of WebhookEvent which is also an AbstractEvent
    }
}
