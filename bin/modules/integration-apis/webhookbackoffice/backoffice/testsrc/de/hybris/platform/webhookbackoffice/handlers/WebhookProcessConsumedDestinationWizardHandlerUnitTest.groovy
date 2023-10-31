/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.webhookbackoffice.handlers

import com.hybris.cockpitng.core.events.CockpitEventQueue
import com.hybris.cockpitng.util.notifications.NotificationService
import de.hybris.bootstrap.annotations.UnitTest
import de.hybris.platform.apiregistryservices.enums.DestinationChannel
import de.hybris.platform.apiregistryservices.model.ConsumedDestinationModel
import de.hybris.platform.apiregistryservices.model.DestinationTargetModel
import de.hybris.platform.apiregistryservices.model.events.EventConfigurationModel
import de.hybris.platform.servicelayer.model.ModelService
import de.hybris.platform.testframework.JUnitPlatformSpecification
import org.junit.Test

@UnitTest
class WebhookProcessConsumedDestinationWizardHandlerUnitTest extends JUnitPlatformSpecification {
    def modelService = Stub ModelService
    def cockpitEventQueue = Stub CockpitEventQueue
    def notificationService = Stub NotificationService
    def handler = new WebhookProcessConsumedDestinationWizardHandler(modelService, cockpitEventQueue, notificationService)

    @Test
    def "isWebhookChannel returns #expected when ConsumedDestination has DestinationTarget with #channel as DestinationChannel"() {
        given:
        def destinationTarget = Stub(DestinationTargetModel) {
            getDestinationChannel() >> channel
        }

        and:
        def consumedDestination = Stub(ConsumedDestinationModel) {
            getDestinationTarget() >> destinationTarget
        }

        expect:
        handler.isWebhookChannel(consumedDestination) == expected

        where:
        channel                            | expected
        DestinationChannel.WEBHOOKSERVICES | true
        DestinationChannel.DEFAULT         | false
        null                               | false
    }

    @Test
    def "isEventNotEmpty returns #expected when ConsumedDestination has DestinationTarget with #eventConfig as list of EventConfiguration"() {
        given:
        def destinationTarget = Stub(DestinationTargetModel) {
            getEventConfigurations() >> eventConfig
        }

        and:
        def consumedDestination = Stub(ConsumedDestinationModel) {
            getDestinationTarget() >> destinationTarget
        }

        expect:
        handler.isEventNotEmpty(consumedDestination) == expected

        where:
        eventConfig                            | expected
        List.of(Stub(EventConfigurationModel)) | true
        Collections.emptyList()                | false
        null                                   | false
    }

    @Test
    def "isDTContainsSupportedEvent returns #expected when ConsumedDestination has DestinationTarget that has EventConfiguration with #eventClass as event class"() {
        given:
        def eventConfig = Stub(EventConfigurationModel) {
            getEventClass() >> eventClass
        }
        def destinationTarget = Stub(DestinationTargetModel) {
            getEventConfigurations() >> List.of(eventConfig)
        }

        and:
        def consumedDestination = Stub(ConsumedDestinationModel) {
            getDestinationTarget() >> destinationTarget
        }

        expect:
        handler.isSupportedEventExisting(consumedDestination) == expected
        WebhookProcessConsumedDestinationWizardHandler.SUPPORTED_EVENT

        where:
        eventClass                                                     | expected
        WebhookProcessConsumedDestinationWizardHandler.SUPPORTED_EVENT | true
        "de.hybris.platform.webhookservices.event.UnsupportedEvent"    | false
        null                                                           | false
    }
}
