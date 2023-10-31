/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.webhookbackoffice.handlers

import com.hybris.cockpitng.core.events.CockpitEventQueue
import com.hybris.cockpitng.util.notifications.NotificationService
import de.hybris.bootstrap.annotations.UnitTest
import de.hybris.platform.apiregistryservices.enums.DestinationChannel
import de.hybris.platform.apiregistryservices.model.DestinationTargetModel
import de.hybris.platform.servicelayer.model.ModelService
import de.hybris.platform.testframework.JUnitPlatformSpecification
import org.junit.Test

@UnitTest
class WebhookProcessDestinationTargetWizardHandlerUnitTest extends JUnitPlatformSpecification {
    def modelService = Stub ModelService
    def cockpitEventQueue = Stub CockpitEventQueue
    def notificationService = Stub NotificationService
    def handler = new WebhookProcessDestinationTargetWizardHandler(modelService, cockpitEventQueue, notificationService)

    @Test
    def "isValidDestinationTarget returns #expected when DestinationTarget has #channel as DestinationChannel"() {
        given:
        def destinationTarget = Stub(DestinationTargetModel) {
            getDestinationChannel() >> channel
        }

        expect:
        handler.isValidDestinationTarget(destinationTarget) == expected

        where:
        channel                            | expected
        DestinationChannel.WEBHOOKSERVICES | true
        DestinationChannel.DEFAULT         | false
        null                               | false
    }
}
