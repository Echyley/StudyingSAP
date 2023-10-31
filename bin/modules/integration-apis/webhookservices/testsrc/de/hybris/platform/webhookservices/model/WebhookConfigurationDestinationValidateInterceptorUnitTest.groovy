/*
 *  Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.webhookservices.model

import de.hybris.bootstrap.annotations.UnitTest
import de.hybris.platform.apiregistryservices.enums.DestinationChannel
import de.hybris.platform.apiregistryservices.model.ConsumedDestinationModel
import de.hybris.platform.apiregistryservices.model.DestinationTargetModel
import de.hybris.platform.servicelayer.interceptor.InterceptorContext
import de.hybris.platform.testframework.JUnitPlatformSpecification
import de.hybris.platform.webhookservices.exceptions.WebhookConfigInvalidChannelException
import org.junit.Test
import spock.lang.Specification

@UnitTest
class WebhookConfigurationDestinationValidateInterceptorUnitTest extends JUnitPlatformSpecification {
    def interceptor = new WebhookConfigurationDestinationValidateInterceptor()

    @Test
    def 'exception is thrown when destination is not associated with a webhook target'() {
        given: 'consumed destination is not associated with a webhook target'
        def destinationId = 'InvalidDestination'
        def destination = Stub(ConsumedDestinationModel) {
            getDestinationTarget() >> Stub(DestinationTargetModel) {
                getDestinationChannel() >> DestinationChannel.DEFAULT
            }
            getId() >> destinationId
        }

        when: 'webhook configuration is created with that destination'
        interceptor.onValidate webHookConfig(destination), Stub(InterceptorContext)

        then: 'exception is thrown'
        def e = thrown WebhookConfigInvalidChannelException
        e.interceptor == interceptor
        e.message.contains "WebhookConfigurationModel is misconfigured: the WebhookConfiguration's DestinationTarget is not associated with $DestinationChannel.WEBHOOKSERVICES"
    }

    @Test
    def 'exception is not thrown if the destination is associated with a webhook target'() {
        given: 'consumed destination is associated with a webhook target'
        def destination = Stub(ConsumedDestinationModel) {
            getDestinationTarget() >> Stub(DestinationTargetModel) {
                getDestinationChannel() >> DestinationChannel.WEBHOOKSERVICES
            }
        }

        when: 'webhook configuration is created with that destination'
        interceptor.onValidate webHookConfig(destination), Stub(InterceptorContext)

        then: 'no exception is thrown'
        noExceptionThrown()
    }

    @Test
    def 'exception is not thrown if the destination is not provided'() {
        when: 'webhook configuration is created with null destination'
        interceptor.onValidate webHookConfig(null), Stub(InterceptorContext)

        then: 'no exception is thrown'
        noExceptionThrown()
    }

    WebhookConfigurationModel webHookConfig(ConsumedDestinationModel dest) {
        new WebhookConfigurationModel(destination: dest)
    }
}
