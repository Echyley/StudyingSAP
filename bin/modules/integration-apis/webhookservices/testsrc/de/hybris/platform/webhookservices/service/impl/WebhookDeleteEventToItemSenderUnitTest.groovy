package de.hybris.platform.webhookservices.service.impl

import de.hybris.platform.apiregistryservices.dto.EventSourceData
import de.hybris.platform.apiregistryservices.model.ConsumedDestinationModel
import de.hybris.platform.apiregistryservices.model.DestinationTargetModel
import de.hybris.platform.apiregistryservices.model.events.EventConfigurationModel
import de.hybris.platform.core.PK
import de.hybris.platform.integrationservices.model.IntegrationObjectModel
import de.hybris.platform.integrationservices.service.ItemModelSearchService
import de.hybris.platform.order.events.SubmitOrderEvent
import de.hybris.platform.outboundservices.enums.OutboundSource
import de.hybris.platform.outboundservices.event.EventType
import de.hybris.platform.outboundservices.event.impl.DefaultEventType
import de.hybris.platform.outboundservices.facade.OutboundServiceFacade
import de.hybris.platform.outboundservices.facade.SyncParameters
import de.hybris.platform.testframework.JUnitPlatformSpecification
import de.hybris.platform.webhookservices.cache.WebhookCacheService
import de.hybris.platform.webhookservices.dto.WebhookItemConversion
import de.hybris.platform.webhookservices.dto.WebhookItemPayload
import de.hybris.platform.webhookservices.event.ItemCreatedEvent
import de.hybris.platform.webhookservices.event.ItemDeletedEvent
import de.hybris.platform.webhookservices.event.ItemSavedEvent
import de.hybris.platform.webhookservices.event.ItemUpdatedEvent
import de.hybris.platform.webhookservices.model.WebhookConfigurationModel
import de.hybris.platform.webhookservices.model.WebhookPayloadModel
import de.hybris.platform.webhookservices.service.WebhookConfigurationService
import org.junit.Test
import rx.Observable

class WebhookDeleteEventToItemSenderUnitTest extends JUnitPlatformSpecification {

    private static final def ITEM_PK = PK.fromLong(1234)
    private static final def EVENT_CONFIG_PK = PK.fromLong(5678)
    private static final def FILTER_SCRIPT_URI = 'script://someScriptThatDoesNotMatter'
    private static final def DEST_TARGET_ID = 'matchingDestinationTargetId'
    private static final def DELETED_ITEM = new WebhookPayloadModel()

    def outboundServiceFacade = Mock(OutboundServiceFacade)
    def itemModelSearchService = Mock(ItemModelSearchService)
    def webhookConfigurationService = Mock(WebhookConfigurationService)
    def webhookCacheService = Mock(WebhookCacheService)

    def sender = new WebhookDeleteEventToItemSender(webhookCacheService,
            webhookConfigurationService,
            itemModelSearchService,
            outboundServiceFacade)

    @Test
    def "instantiation fails because #param cannot be null"() {
        when:
        new WebhookDeleteEventToItemSender(webhookCache, webhookConfiguration, itemModelSearch, outboundFacade)

        then:
        def e = thrown IllegalArgumentException
        e.message == "$param cannot be null"

        where:
        outboundFacade              | webhookConfiguration              | itemModelSearch              | webhookCache              | param
        null                        | Stub(WebhookConfigurationService) | Stub(ItemModelSearchService) | Stub(WebhookCacheService) | 'OutboundServiceFacade'
        Stub(OutboundServiceFacade) | null                              | Stub(ItemModelSearchService) | Stub(WebhookCacheService) | 'WebhookConfigurationService'
        Stub(OutboundServiceFacade) | Stub(WebhookConfigurationService) | null                         | Stub(WebhookCacheService) | 'ItemModelSearchService'
        Stub(OutboundServiceFacade) | Stub(WebhookConfigurationService) | Stub(ItemModelSearchService) | null                      | 'WebhookCacheService'
    }

    @Test
    def 'item is not sent when event is #event'() {
        given: 'an event that is not a delete event'
        def eventSourceData = Stub(EventSourceData) { getEvent() >> event }

        when:
        sender.send(eventSourceData)

        then:
        0 * outboundServiceFacade._

        where:
        event << [Stub(ItemUpdatedEvent), Stub(ItemCreatedEvent), Stub(ItemSavedEvent), Stub(SubmitOrderEvent)]
    }

    @Test
    def 'item is not sent when no itemPayloads are found'() {
        given: 'a delete item event'
        def eventSourceData = deleteEventSourceData()
        and: 'no cached item payload is found'
        webhookCacheService.findItemPayloads(ITEM_PK) >> Optional.empty()

        when:
        sender.send(eventSourceData)

        then: 'no event is sent out'
        0 * outboundServiceFacade._
    }

    @Test
    def 'item is not sent when no webhookConfiguration is found'() {
        given: 'a delete item event'
        def eventSourceData = deleteEventSourceData()
        and: 'no webhookConfiguration is found'
        webhookCacheService.findItemPayloads(ITEM_PK) >> Optional.of(webhookItemPayloads())
        webhookConfigurationService.findWebhookConfigurationByPk(EVENT_CONFIG_PK) >> Optional.empty()

        when:
        sender.send(eventSourceData)

        then: 'no item sent out'
        0 * outboundServiceFacade._
    }

    @Test
    def 'item is sent when event is an ItemDeletedEvent'() {
        given: 'a delete item event'
        def eventSourceData = deleteEventSourceData()
        and: 'event payloads, webhookConfiguration and eventConfiguration are found'
        def webhookConfig = webhookConfig()
        webhookCacheService.findItemPayloads(ITEM_PK) >> Optional.of(webhookItemPayloads())
        webhookConfigurationService.findWebhookConfigurationByPk(EVENT_CONFIG_PK) >> Optional.of(webhookConfig)
        itemModelSearchService.nonCachingFindByPk(EVENT_CONFIG_PK) >> Optional.of(eventSourceData.getEventConfig())

        when:
        sender.send(eventSourceData)

        then:
        1 * outboundServiceFacade.send(asSyncParams(webhookConfig, DefaultEventType.DELETED)) >> Observable.empty()
    }

    private WebhookItemPayload webhookItemPayloads() {
        Stub(WebhookItemPayload) {
            getItemConversions() >> Set.of(Stub(WebhookItemConversion) {
                getWebhookConfigurationPk() >> EVENT_CONFIG_PK
                getWebhookPayload() >> DELETED_ITEM

            })
        }
    }

    private EventSourceData deleteEventSourceData(EventType eventType = DefaultEventType.DELETED, String destinationTargetId = DEST_TARGET_ID) {
        Stub(EventSourceData) {
            getEvent() >> Stub(ItemDeletedEvent) {
                getPk() >> ITEM_PK
                getEventType() >> eventType
            }
            getEventConfig() >> Stub(EventConfigurationModel) {
                getDestinationTarget() >> Stub(DestinationTargetModel) {
                    getId() >> destinationTargetId
                }
                getPk() >> EVENT_CONFIG_PK
            }
        }
    }

    private WebhookConfigurationModel webhookConfig() {
        Stub(WebhookConfigurationModel) {
            getIntegrationObject() >> Stub(IntegrationObjectModel)
            getFilterLocation() >> FILTER_SCRIPT_URI
            getDestination() >> Stub(ConsumedDestinationModel) {
                getDestinationTarget() >> Stub(DestinationTargetModel) {
                    getId() >> DEST_TARGET_ID
                }
            }
        }
    }

    private static SyncParameters asSyncParams(WebhookConfigurationModel cfg, EventType eventType) {
        SyncParameters.syncParametersBuilder()
                .withPayloadObject(DELETED_ITEM)
                .withSource(OutboundSource.WEBHOOKSERVICES)
                .withIntegrationObject(cfg.integrationObject)
                .withDestination(cfg.destination)
                .withEventType(eventType)
                .build()
    }

}
