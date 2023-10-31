/*
 *  Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.webhookservices

import de.hybris.bootstrap.annotations.IntegrationTest
import de.hybris.platform.apiregistryservices.enums.DestinationChannel
import de.hybris.platform.apiregistryservices.model.ConsumedDestinationModel
import de.hybris.platform.apiregistryservices.model.DestinationTargetModel
import de.hybris.platform.apiregistryservices.model.events.EventConfigurationModel
import de.hybris.platform.integrationservices.IntegrationObjectModelBuilder
import de.hybris.platform.integrationservices.util.IntegrationTestUtil
import de.hybris.platform.integrationservices.util.ItemTracker
import de.hybris.platform.integrationservices.util.impex.ModuleEssentialData
import de.hybris.platform.order.events.SubmitOrderEvent
import de.hybris.platform.outboundservices.ConsumedDestinationBuilder
import de.hybris.platform.servicelayer.ServicelayerSpockSpecification
import de.hybris.platform.webhookservices.constants.WebhookservicesConstants
import de.hybris.platform.webhookservices.event.ItemSavedEvent
import de.hybris.platform.webhookservices.model.WebhookConfigurationModel
import de.hybris.platform.webhookservices.util.WebhookServicesEssentialData
import org.junit.ClassRule
import org.junit.Rule
import org.junit.Test
import spock.lang.Shared

import static de.hybris.platform.outboundservices.ConsumedDestinationBuilder.consumedDestinationBuilder
import static de.hybris.platform.outboundservices.DestinationTargetBuilder.destinationTarget
import static de.hybris.platform.webhookservices.EventConfigurationBuilder.eventConfiguration
import static de.hybris.platform.webhookservices.WebhookConfigurationBuilder.webhookConfiguration

@IntegrationTest
class WebhookConfigurationModelingIntegrationTest extends ServicelayerSpockSpecification {
    private static final String TEST_NAME = 'WebhookConfigurationModeling'
    private static final def CONSUMEDDESTINATION_ID = 'webhookDestination'
    private static final String IO = "${TEST_NAME}_IO"
    private static final String DESTINATION_ID = "${TEST_NAME}_Destination"
    private static final EventConfigurationBuilder eventConfigurationBuilder = eventConfiguration()
            .withExtensionName(WebhookservicesConstants.EXTENSIONNAME)
            .withExport(true)
    private static final ConsumedDestinationBuilder consumedDestination = consumedDestinationBuilder()

    @Shared
    @ClassRule
    IntegrationObjectModelBuilder io = IntegrationObjectModelBuilder.integrationObject().withCode(IO)
    @Rule
    ModuleEssentialData essentialData = WebhookServicesEssentialData.webhookServicesEssentialData()
    @Rule
    ItemTracker itemTracker = ItemTracker.track(WebhookConfigurationModel, ConsumedDestinationModel,
            EventConfigurationModel, DestinationTargetModel)

    @Test
    def 'event type can be defaulted for a webhook configuration'() {
        given: 'a valid destination'
        def destination = consumedDestination()

        when: 'webhook configuration is created without evenConfiguration attribute'
        IntegrationTestUtil.importImpEx(
                "INSERT WebhookConfiguration; destination[unique = true] ; integrationObject(code)[unique = true]",
                "                           ; $destination.pk            ; $IO")

        then: 'webhook configuration is created with default event configuration'
        itemTracker.isCreated(WebhookConfigurationModel) {
            it.destination.id == DESTINATION_ID && it.integrationObject.code == IO && it.eventType == ItemSavedEvent.name
        }
    }

    @Test
    def 'consumed destination is required for webhook configuration'() {
        given: 'a supported event'
        def eventType = ItemSavedEvent

        when: 'webhook configuration is created without destination attribute'
        IntegrationTestUtil.importImpEx(
                "INSERT WebhookConfiguration; eventType[unique = true]; integrationObject(code)[unique = true]",
                "                           ; $eventType.canonicalName; $IO")

        then: 'exception is thrown'
        def e = thrown AssertionError
        e.message.contains 'missing values for [destination]'
    }

    @Test
    def 'integration object is required for webhook configuration'() {
        given: 'a supported event type'
        def eventType = ItemSavedEvent
        and: 'existing destination'
        def destination = consumedDestination()

        when: 'webhook configuration is created without integrationObject attribute'
        IntegrationTestUtil.importImpEx(
                "INSERT WebhookConfiguration; eventType[unique = true]; destination[unique = true]",
                "                           ; $eventType.canonicalName; $destination.pk")

        then: 'exception is thrown'
        def e = thrown AssertionError
        e.message.contains 'missing values for [integrationObject]'
    }

    @Test
    def 'webhook configuration is created when all required attributes are present'() {
        given:
        def eventType = ItemSavedEvent
        and:
        def destination = consumedDestination()

        when:
        IntegrationTestUtil.importImpEx(
                "INSERT WebhookConfiguration; eventType[unique = true]; destination[unique = true]; integrationObject(code)[unique = true]",
                "                           ; $eventType.name         ; $destination.pk           ; $IO")

        then:
        IntegrationTestUtil.findAny(WebhookConfigurationModel) {
            it.eventType == eventType.canonicalName && it.destination.pk == destination.pk && it.integrationObject.code == IO
        }.present
    }

    @Test
    def 'other than ItemSavedEvent types cannot be used with webhooks'() {
        given: 'event type is different from ItemSavedEvent'
        def eventType = SubmitOrderEvent
        and: 'destination is valid'
        def destination = consumedDestination()

        when: 'webhook configuration is created with the event configuration'
        IntegrationTestUtil.importImpEx(
                "INSERT WebhookConfiguration; eventType[unique = true]; destination[unique = true]; integrationObject(code)[unique = true]",
                "                                  ; $eventType.canonicalName; $destination.pk           ; $IO")

        then: 'exception is thrown'
        def e = thrown AssertionError
        e.message.contains "$SubmitOrderEvent.name event type is not supported"
    }

    @Test
    def 'webhook event must be registered with the destination target'() {
        given: 'event configuration for the webhook event type is deleted'
        def eventType = ItemSavedEvent
        IntegrationTestUtil.importImpEx(
                'REMOVE EventConfiguration; eventClass[unique = true]; destinationTarget(id)[unique = true]',
                "                         ; $eventType.canonicalName ; webhookServices")
        and: 'destination is valid'
        def destination = consumedDestination()

        when: 'webhook configuration is created with the event configuration'
        IntegrationTestUtil.importImpEx(
                "INSERT WebhookConfiguration; eventType[unique = true]; destination[unique = true]; integrationObject(code)[unique = true]",
                "                           ; $eventType.canonicalName; $destination.pk           ; $IO")

        then: 'exception is thrown'
        def e = thrown AssertionError
        e.message.contains eventType.canonicalName
    }

    @Test
    def 'consumed destination used for webhook configuration must be associated with a webhook destination target'() {
        given: 'destination target is not for webhooks'
        def destinationTarget = destinationTarget()
                .withId("${TEST_NAME}_nonWebhookTarget")
                .withDestinationChannel(DestinationChannel.DEFAULT)
        and: 'event config for the supported type is added to the destination target'
        def eventType = ItemSavedEvent
        eventConfigurationBuilder
                .withDestination(destinationTarget)
                .withEventClass(eventType)
                .build()
        and: 'a destination associated with the wrong destination target'
        def destination = consumedDestination
                .withId("${TEST_NAME}_WrongDestination")
                .withDestinationTarget(destinationTarget)
                .build()

        when: 'webhook configuration is created with the event configuration'
        IntegrationTestUtil.importImpEx(
                "INSERT WebhookConfiguration; eventType[unique = true]; destination[unique = true]; integrationObject(code)[unique = true]",
                "                           ; $eventType.canonicalName; $destination.pk           ; $IO")

        then: 'exception is thrown'
        def e = thrown AssertionError
        e.message.contains "WebhookConfigurationModel is misconfigured: the WebhookConfiguration's DestinationTarget is not associated with $DestinationChannel.WEBHOOKSERVICES"
    }

    @Test
    def 'webhook configuration is created with a filter'() {
        given: 'a valid filter location'
        def filterLocation = 'model://myFilter'

        when: 'webhook configuration impex is executed'
        IntegrationTestUtil.importImpEx(
                "INSERT WebhookConfiguration; destination[unique = true] ; integrationObject(code)[unique = true]; filterLocation",
                "                           ; ${consumedDestination().pk}; $IO                                   ; $filterLocation")

        then: 'the webhook configuration is created with the filter location'
        with(itemTracker.getCreatedItems(WebhookConfigurationModel)[0]) {
            it.destination.id == DESTINATION_ID
            it.integrationObject.code == IO
            it.filterLocation == filterLocation
        }
    }

    @Test
    def 'exception is thrown when webhook configuration is created with an invalid filter url'() {
        given: 'unsupported filter location'
        def filterLocation = 'file://myIncorrectFilter'

        when:
        IntegrationTestUtil.importImpEx(
                "INSERT WebhookConfiguration; destination[unique = true] ; integrationObject(code)[unique = true]; filterLocation",
                "                           ; ${consumedDestination().pk}; $IO                                   ; $filterLocation")

        then:
        def e = thrown AssertionError
        e.message.contains filterLocation
    }

    @Test
    def "cannot delete a consumed destination when it is assigned to a webhook configuration"() {
        given:
        def consumedDestination = consumedDestinationBuilder()
                .withId(CONSUMEDDESTINATION_ID)
                .withUrl("https://path/to/webhooks")
                .withDestinationTarget('webhookServices') // created in essential data
                .build()
        webhookConfiguration()
                .withDestination(consumedDestination)
                .withIntegrationObject(IO)
                .build()

        when:
        IntegrationTestUtil.importImpEx(
                'REMOVE ConsumedDestination; id[unique=true]',
                "                          ; $CONSUMEDDESTINATION_ID"
        )

        then:
        def e = thrown AssertionError
        e.message.contains "ConsumedDestinationModel [${consumedDestination.getId()}] cannot be deleted because it is used in one or more [${WebhookConfigurationModel._TYPECODE}]"
        and:
        IntegrationTestUtil.findAny(ConsumedDestinationModel, { it.id == CONSUMEDDESTINATION_ID }).present
    }

    @Test
    def "can delete a consumed destination when it is not assigned to any webhook configurations"() {
        given:
        consumedDestinationBuilder()
                .withId(CONSUMEDDESTINATION_ID)
                .withUrl("https://path/to/webhooks")
                .withDestinationTarget('webhookServices') // created in essential data
                .build()

        when:
        IntegrationTestUtil.importImpEx(
                'REMOVE ConsumedDestination; id[unique=true]',
                "                          ; $CONSUMEDDESTINATION_ID"
        )

        then:
        IntegrationTestUtil.findAny(ConsumedDestinationModel, { it.id == CONSUMEDDESTINATION_ID }).empty
    }

    ConsumedDestinationModel consumedDestination() {
        consumedDestination
                .withId(DESTINATION_ID)
                .withDestinationTarget('webhookServices') // created in essential data
                .build()
    }
}