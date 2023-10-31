/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.webhookservices.event

import de.hybris.bootstrap.annotations.UnitTest
import de.hybris.platform.integrationservices.util.lifecycle.TenantLifecycle
import de.hybris.platform.testframework.JUnitPlatformSpecification
import de.hybris.platform.tx.AfterSaveEvent
import org.junit.Test

@UnitTest
class WebhookServicesAfterSaveEventListenerUnitTest extends JUnitPlatformSpecification {

    def rootEventSender = Mock RootEventSender
    def tenantLifecycle = Stub TenantLifecycle
    def webhookEventFactory = Stub WebhookEventFactory


    def eventListener = new WebhookServicesAfterSaveEventListener(rootEventSender, tenantLifecycle, webhookEventFactory)

    @Test
    def "Instantiation fails because #msg"() {
        when:
        new WebhookServicesAfterSaveEventListener(eventSender as RootEventSender, lifecycle, eventFactory)

        then:
        def e = thrown IllegalArgumentException
        e.message == msg

        where:
        lifecycle             | eventFactory              | eventSender           | msg
        Stub(TenantLifecycle) | null                      | Stub(RootEventSender) | 'webhookEventFactory cannot be null'
        null                  | Stub(WebhookEventFactory) | Stub(RootEventSender) | 'tenantLifecycle cannot be null'
        Stub(TenantLifecycle) | Stub(WebhookEventFactory) | null                  | 'rootEventSender cannot be null'
    }

    @Test
    def "No WebhookEvent is sent when #msg"() {
        given:
        tenantLifecycle.isOperational() >> tenantIsOperational

        and: "webhook factory creates given list of WebhookEvents"
        webhookEventFactory.create(_ as AfterSaveEvent) >> list

        when:
        eventListener.afterSave([Stub(AfterSaveEvent)])

        then:
        0 * rootEventSender.send(_ as WebhookEvent)

        where:
        msg                                                | tenantIsOperational | list
        "platform is starting up or shutting down"         | false               | [Stub(WebhookEvent)]
        "webhookEventFactory.create returns an empty list" | true                | []
    }

    @Test
    def "sends all WebhookEvents returned from webhookEventFactory.create"() {
        given:
        tenantLifecycle.isOperational() >> true
        def afterSaveEvent = Stub(AfterSaveEvent)
        def webhookEvent1 = Stub(WebhookEvent)
        def webhookEvent2 = Stub(WebhookEvent)

        and:
        webhookEventFactory.create(afterSaveEvent) >> [webhookEvent1, webhookEvent2]

        when:
        eventListener.afterSave([afterSaveEvent])

        then:
        1 * rootEventSender.send(webhookEvent1)
        1 * rootEventSender.send(webhookEvent2)
    }
}
