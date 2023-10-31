/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.webhookservices.service

import de.hybris.bootstrap.annotations.UnitTest
import de.hybris.platform.apiregistryservices.model.ConsumedDestinationModel
import de.hybris.platform.core.model.ItemModel
import de.hybris.platform.integrationservices.model.IntegrationObjectModel
import de.hybris.platform.integrationservices.util.TestApplicationContext
import de.hybris.platform.outboundservices.client.IntegrationRestTemplateFactory
import de.hybris.platform.outboundservices.enums.OutboundSource
import de.hybris.platform.outboundservices.event.impl.DefaultEventType
import de.hybris.platform.outboundservices.facade.OutboundServiceFacade
import de.hybris.platform.outboundservices.facade.SyncParameters
import de.hybris.platform.outboundservices.facade.impl.RemoteSystemClient
import de.hybris.platform.testframework.JUnitPlatformSpecification
import de.hybris.platform.tx.AfterSaveEvent
import de.hybris.platform.webhookservices.event.BaseWebhookEvent
import de.hybris.platform.webhookservices.event.ItemCreatedEvent
import de.hybris.platform.webhookservices.event.ItemDeletedEvent
import de.hybris.platform.webhookservices.event.ItemSavedEvent
import de.hybris.platform.webhookservices.event.ItemUpdatedEvent
import de.hybris.platform.webhookservices.exceptions.WebhookConfigurationValidationException
import de.hybris.platform.webhookservices.model.WebhookConfigurationModel
import de.hybris.platform.webhookservices.service.impl.DefaultCloudEventHeadersService
import de.hybris.platform.webhookservices.service.impl.DefaultWebhookValidationService
import org.junit.Rule
import org.junit.Test
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.client.HttpStatusCodeException
import rx.Observable
import rx.observers.TestSubscriber
import spock.lang.Shared

@UnitTest
class DefaultWebhookValidationServiceUnitTest extends JUnitPlatformSpecification {

    private static final def INTEGRATION_KEY_VALUE = "TEST_KEY"
    private static final def INTEGRATION_KEY = "integrationKey"
    private static final def MSG = "msg"
    private static final def MSG_VALUE = "hello"
    private static final def PAYLOAD = "{\"$MSG\" : \"$MSG_VALUE\", \"$INTEGRATION_KEY\" : \"$INTEGRATION_KEY_VALUE\"}"
    private static final NULL_PAYLOAD = null
    private static final EMPTY_PAYLOAD = ''
    private static final def CLOUD_EVENT_ID = "ce-id"
    private static final def CLOUD_EVENT_ID_VALUE = "1234-1234-efgh-5678"
    private static final def EVENT_TYPE = ItemCreatedEvent.class.canonicalName
    private static final def IO_MODEL_CODE = "ioCode"
    private static final def IO_MODEL = new IntegrationObjectModel(code: IO_MODEL_CODE)
    private static final def CREATED_EVENT_TYPE = "Created"
    private static final DESTINATION = new ConsumedDestinationModel(url: 'http://my.consumed.destination/some/path')
    private static final BAD_DESTINATION = new ConsumedDestinationModel(url: 'http://bad.consumed.destination/some/path')
    private static final CONFIGURATION = new WebhookConfigurationModel(
            destination: DESTINATION, integrationObject: IO_MODEL, eventType: EVENT_TYPE)
    private static final BAD_CONFIGURATION = new WebhookConfigurationModel(
            destination: BAD_DESTINATION, integrationObject: IO_MODEL, eventType: EVENT_TYPE)
    private static final NO_CONSUMED_DESTINATION_CONFIGURATION = new WebhookConfigurationModel(destination: null)
    private static final NO_IO_DESTINATION_CONFIGURATION = new WebhookConfigurationModel(
            destination: DESTINATION, eventType: EVENT_TYPE)
    private static final RESPONSE = new ResponseEntity(HttpStatus.ACCEPTED)
    private static final BAD_RESPONSE = new ResponseEntity(HttpStatus.BAD_REQUEST)
    private static final OBSERVABLE_RESPONSE = Observable.just(RESPONSE)
    private static final BAD_OBSERVABLE_RESPONSE = Observable.just(BAD_RESPONSE)

    @Shared
    def itemModel = Stub(ItemModel) {
        getItemtype() >> 'MyType'
    }

    @Rule
    TestApplicationContext applicationContext = new TestApplicationContext()

    def outboundServiceFacade = Mock(OutboundServiceFacade)
    def remoteSystemClient = Mock RemoteSystemClient
    def cloudEventHeadersService = Stub(DefaultCloudEventHeadersService) {
        generateCloudEventHeaders(IO_MODEL_CODE, INTEGRATION_KEY_VALUE, DefaultEventType.CREATED, null) >> new HttpHeaders([(CLOUD_EVENT_ID): CLOUD_EVENT_ID_VALUE])
    }
    def service = new DefaultWebhookValidationService(remoteSystemClient, outboundServiceFacade, cloudEventHeadersService)

    @Test
    def "uses context RemoteSystemClient when instantiated with IntegrationRestTemplateFactory, RestTemplateFactory and OutboundServiceFacade"() {
        given: 'application context contains a remote system client'
        def contextRemoteSystemClient = Stub(RemoteSystemClient)
        applicationContext.addBean 'remoteSystemClient', contextRemoteSystemClient

        when: 'the service is instantiated without the remote system client'
        def service = new DefaultWebhookValidationService(
                Stub(IntegrationRestTemplateFactory), Stub(OutboundServiceFacade), Stub(CloudEventHeadersService))

        then: 'the context remote system client is used by the instantiated service'
        service.remoteSystemClient.is contextRemoteSystemClient
    }

    @Test
    def "uses context RemoteSystemClient and default CloudEvenHeadersService when instantiated with IntegrationRestTemplateFactory and OutboundServiceFacade"() {
        given: 'application context contains a remote system client'
        def contextRemoteSystemClient = Stub(RemoteSystemClient)
        applicationContext.addBean 'remoteSystemClient', contextRemoteSystemClient

        when: 'the service is instantiated without the remote system client and the client header service'
        def service = new DefaultWebhookValidationService(
                Stub(IntegrationRestTemplateFactory), Stub(OutboundServiceFacade))

        then: 'the context remote system client is used by the instantiated service'
        service.remoteSystemClient.is contextRemoteSystemClient
        and: 'a default CloudEvenHeadersService is created'
        service.cloudEventHeadersService
    }

    @Test
    def "cannot be created with null #param"() {
        when:
        new DefaultWebhookValidationService(client as RemoteSystemClient, facade, cloudService)

        then:
        def e = thrown IllegalArgumentException
        e.message == "$param cannot be null"

        where:
        param                      | client                   | facade                      | cloudService
        'RemoteSystemClient'       | null                     | Stub(OutboundServiceFacade) | Stub(CloudEventHeadersService)
        'OutboundServiceFacade'    | Stub(RemoteSystemClient) | null                        | Stub(CloudEventHeadersService)
        'CloudEventHeadersService' | Stub(RemoteSystemClient) | Stub(OutboundServiceFacade) | null
    }

    @Test
    def "WebhookConfigurationService does not throw any Exception when webhook configuration is ok"() {
        when:
        service.pingWebhookDestination(CONFIGURATION, PAYLOAD)

        then:
        noExceptionThrown()
    }

    @Test
    def "pingWebhookDestination throws exception when webhook configuration is #config_status and payload is #payload_status"() {
        when:
        service.pingWebhookDestination(config, payload)

        then:
        def e = thrown IllegalArgumentException
        e.message == errorMessage

        where:
        config                                | config_status | payload       | payload_status | errorMessage
        CONFIGURATION                         | 'ok'          | NULL_PAYLOAD  | 'not ok'       | 'webhookPayload cannot be blank'
        CONFIGURATION                         | 'ok'          | EMPTY_PAYLOAD | 'not ok'       | 'webhookPayload cannot be blank'
        null                                  | 'not ok'      | PAYLOAD       | 'ok'           | 'webhookConfiguration cannot be null'
        NO_CONSUMED_DESTINATION_CONFIGURATION | 'not ok'      | PAYLOAD       | 'ok'           | 'consumedDestination cannot be null'
        NO_IO_DESTINATION_CONFIGURATION       | 'not ok'      | PAYLOAD       | 'ok'           | 'integrationObject cannot be null'
    }

    @Test
    def "pingWebhookDestination throws exception when response from the destination is #status"() {
        given:
        def clientException = Stub(HttpStatusCodeException) {
            getStatusCode() >> status
            getMessage() >> 'Some exception message'
        }
        remoteSystemClient.post(DESTINATION, _ as HttpEntity) >> { throw clientException }

        when:
        service.pingWebhookDestination(CONFIGURATION, PAYLOAD)

        then:
        def e = thrown WebhookConfigurationValidationException
        e.message == clientException.message
        e.cause == clientException

        where:
        status << [HttpStatus.BAD_REQUEST, HttpStatus.UNAUTHORIZED, HttpStatus.FORBIDDEN,
                   HttpStatus.NOT_FOUND, HttpStatus.METHOD_NOT_ALLOWED, HttpStatus.INTERNAL_SERVER_ERROR,
                   HttpStatus.SERVICE_UNAVAILABLE]
    }

    @Test
    def "pingWebhookDestination sends an ItemModel and returns HTTP response #response_status when webhook configuration is #config_status"() {
        given:
        outboundServiceFacade.send(_ as SyncParameters) >> observable

        when:
        def subscriber = new TestSubscriber()
        service.pingWebhookDestination(config, model).subscribe subscriber

        then:
        subscriber.assertCompleted()
        subscriber.assertNoErrors()
        subscriber.assertValue(response)

        where:
        observable              | config            | config_status | model     | response     | response_status
        OBSERVABLE_RESPONSE     | CONFIGURATION     | 'ok'          | itemModel | RESPONSE     | RESPONSE.getStatusCode()
        BAD_OBSERVABLE_RESPONSE | BAD_CONFIGURATION | 'bad'         | itemModel | BAD_RESPONSE | BAD_RESPONSE.getStatusCode()
    }

    @Test
    def 'CloudEvent headers is added when all data is offered'() {
        when:
        service.pingWebhookDestination(CONFIGURATION, PAYLOAD)

        then:
        1 * remoteSystemClient.post(DESTINATION, _ as HttpEntity) >> { List args ->
            def entity = args[1] as HttpEntity
            assert entity.headers.getFirst(CLOUD_EVENT_ID) == CLOUD_EVENT_ID_VALUE
            assert entity.headers.getFirst(HttpHeaders.CONTENT_TYPE) == MediaType.APPLICATION_JSON_VALUE
            assert entity.headers.getFirst(HttpHeaders.ACCEPT) == MediaType.APPLICATION_JSON_VALUE
        }
    }

    @Test
    def 'sendItemToWebhook method builds the payload with correct parameters'() {
        when:
        service.pingWebhookDestination(CONFIGURATION, itemModel)

        then:
        1 * outboundServiceFacade.send(_ as SyncParameters) >> { List args ->
            def param = args[0] as SyncParameters
            assert param.item == itemModel
            assert param.source == OutboundSource.WEBHOOKSERVICES
            assert param.integrationObjectCode == CONFIGURATION.getIntegrationObject().getCode()
            assert param.destination == CONFIGURATION.destination
            assert param.eventType.getType() == CREATED_EVENT_TYPE
        }
    }

    @Test
    def 'when #event occurred the header type will be #eventType'() {
        when:
        CONFIGURATION.eventType = event.canonicalName
        service.pingWebhookDestination(CONFIGURATION, itemModel)

        then:
        1 * outboundServiceFacade.send(_ as SyncParameters) >> { List args ->
            def param = args[0] as SyncParameters
            assert param.eventType.type == eventType
        }

        where:
        event               | eventType
        ItemUpdatedEvent    | "Updated"
        ItemCreatedEvent    | "Created"
        ItemSavedEvent      | "Updated"
        ItemDeletedEvent    | "Deleted"
        UnknownWebhookEvent | "Unknown"
    }

    private static class UnknownWebhookEvent extends BaseWebhookEvent {
        UnknownWebhookEvent(final AfterSaveEvent event) {
            super(event.getPk(), DefaultEventType.UNKNOWN);
        }
    }

}
