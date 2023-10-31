/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.webhookservices.model


import de.hybris.bootstrap.annotations.UnitTest
import de.hybris.platform.apiregistryservices.model.ConsumedDestinationModel
import de.hybris.platform.apiregistryservices.model.DestinationTargetModel
import de.hybris.platform.apiregistryservices.model.events.EventConfigurationModel
import de.hybris.platform.order.events.SubmitOrderEvent
import de.hybris.platform.servicelayer.event.events.TestingEvent
import de.hybris.platform.servicelayer.interceptor.InterceptorContext
import de.hybris.platform.testframework.JUnitPlatformSpecification
import de.hybris.platform.webhookservices.event.ItemSavedEvent
import de.hybris.platform.webhookservices.exceptions.WebhookConfigEventNotSupportedException
import de.hybris.platform.webhookservices.exceptions.WebhookConfigNoEventConfigException
import de.hybris.platform.webhookservices.exceptions.WebhookConfigNoEventTypeException
import de.hybris.platform.webhookservices.exceptions.WebhookConfigNotRegisteredEventException
import de.hybris.platform.webhookservices.exceptions.WebhookConfigNotValidLocationException
import org.junit.Test
import spock.lang.Specification
import spock.lang.Unroll

@UnitTest
class WebhookConfigurationValidateInterceptorUnitTest extends JUnitPlatformSpecification {
    def webhookConfigValidateInterceptor = new WebhookConfigurationValidateInterceptor()

    @Test
    def "exception is thrown when the WebhookConfigurationModel's eventType is not set"() {
        given:
        def configuration = Stub(WebhookConfigurationModel) {}

        when:
        webhookConfigValidateInterceptor.onValidate configuration, Stub(InterceptorContext)

        then:
        def e = thrown WebhookConfigNoEventTypeException
        e.message.contains "WebhookConfigurationModel is misconfigured: event type is not set."
        e.interceptor == webhookConfigValidateInterceptor
    }

    @Test
    @Unroll
    def "exception is thrown when #condition"() {
        given:
        def eventType = ItemSavedEvent
        def configuration = Stub(WebhookConfigurationModel) {
            getDestination() >> consumedDestination
            getEventType() >> eventType.canonicalName
        }

        when:
        webhookConfigValidateInterceptor.onValidate configuration, Stub(InterceptorContext)

        then:
        def e = thrown WebhookConfigNoEventConfigException
        e.message.contains "WebhookConfigurationModel is misconfigured: No Event Configurations linked with the destination target in use."
        e.interceptor == webhookConfigValidateInterceptor

        where:
        condition                                                 | consumedDestination
        "ConsumedDestination is null"                             | null
        "No EventConfiguration associated with DestinationTarget" | destinationFor([])
    }

    @Test
    def "exception is thrown when the WebhookConfigurationModel's eventType is not supported"() {
        given:
        def unsupportedEventClass = SubmitOrderEvent
        def configuration = Stub(WebhookConfigurationModel) {
            getDestination() >> destinationFor([SubmitOrderEvent, TestingEvent])
            getEventType() >> unsupportedEventClass.canonicalName
        }

        when:
        webhookConfigValidateInterceptor.onValidate configuration, Stub(InterceptorContext)

        then:
        def e = thrown WebhookConfigEventNotSupportedException
        e.message.contains "$unsupportedEventClass.canonicalName event type is not supported. Supported types"
        e.interceptor == webhookConfigValidateInterceptor
    }

    @Test
    def "exception is thrown when DestinationTarget does not contain the event type"() {
        given:
        def eventType = ItemSavedEvent
        def configuration = Stub(WebhookConfigurationModel) {
            getDestination() >> destinationFor([SubmitOrderEvent, TestingEvent])
            getEventType() >> eventType.canonicalName
        }

        when:
        webhookConfigValidateInterceptor.onValidate configuration, Stub(InterceptorContext)

        then:
        def e = thrown WebhookConfigNotRegisteredEventException
        e.message.contains "WebhookConfigurationModel is misconfigured: $eventType.canonicalName is not registered with the destination target"
        e.interceptor == webhookConfigValidateInterceptor
    }

    @Test
    def "validation passes when WebhookConfigurationModel is valid"() {
        given: 'a configuration with supported event type'
        def configuration = Stub(WebhookConfigurationModel) {
            getEventType() >> ItemSavedEvent.canonicalName
        }
        and: 'the event type is registered with the destination target'
        configuration.getDestination() >> destinationFor([ItemSavedEvent])

        when:
        webhookConfigValidateInterceptor.onValidate configuration, Stub(InterceptorContext)

        then:
        noExceptionThrown()
    }

    @Test
    @Unroll
    def "validation passes when filter location is #location"() {
        given: 'a configuration with supported event type'
        def configuration = Stub(WebhookConfigurationModel) {
            getDestination() >> destinationFor([ItemSavedEvent])
            getEventType() >> ItemSavedEvent.canonicalName
            getFilterLocation() >> location
        }

        when:
        webhookConfigValidateInterceptor.onValidate configuration, Stub(InterceptorContext)

        then:
        noExceptionThrown()

        where:
        location << ['', null, 'model://myScript']
    }

    @Test
    @Unroll
    def "exception is thrown when webhook configuration has a malformed filter location #location"() {
        given:
        def config = Stub(WebhookConfigurationModel) {
            getDestination() >> destinationFor([ItemSavedEvent])
            getEventType() >> ItemSavedEvent.canonicalName
            getFilterLocation() >> location
        }

        when:
        webhookConfigValidateInterceptor.onValidate config, Stub(InterceptorContext)

        then:
        def e = thrown WebhookConfigNotValidLocationException
        e.message.contains "WebhookConfigurationModel is misconfigured: Filter location '$location' provided does not meet the pattern model://<script_code>"
        e.interceptor == webhookConfigValidateInterceptor

        where:
        location << ['file://someFile', 'model', 'model://', 'model:/', 'model/', 'model:// ']
    }


    ConsumedDestinationModel destinationFor(List<Class> events) {
        Stub(ConsumedDestinationModel) {
            getDestinationTarget() >> Stub(DestinationTargetModel) {
                getEventConfigurations() >> events.collect({ toEventConfig(it) })
            }
        }
    }

    EventConfigurationModel toEventConfig(Class eventClass) {
        Stub(EventConfigurationModel) {
            getEventClass() >> eventClass.canonicalName
        }
    }
}
