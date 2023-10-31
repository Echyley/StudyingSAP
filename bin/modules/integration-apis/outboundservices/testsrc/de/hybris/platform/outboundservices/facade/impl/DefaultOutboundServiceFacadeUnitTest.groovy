/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.outboundservices.facade.impl

import de.hybris.bootstrap.annotations.UnitTest
import de.hybris.platform.apiregistryservices.model.ConsumedDestinationModel
import de.hybris.platform.core.model.ItemModel
import de.hybris.platform.integrationservices.model.IntegrationObjectClassModel
import de.hybris.platform.integrationservices.model.IntegrationObjectModel
import de.hybris.platform.integrationservices.service.IntegrationObjectService
import de.hybris.platform.integrationservices.util.TestApplicationContext
import de.hybris.platform.outboundservices.client.IntegrationRestTemplateFactory
import de.hybris.platform.outboundservices.decorator.DecoratorContext
import de.hybris.platform.outboundservices.decorator.DecoratorContextErrorException
import de.hybris.platform.outboundservices.decorator.DecoratorContextFactory
import de.hybris.platform.outboundservices.decorator.OutboundRequestDecorator
import de.hybris.platform.outboundservices.decorator.RequestDecoratorService
import de.hybris.platform.outboundservices.facade.ConsumedDestinationNotFoundModel
import de.hybris.platform.outboundservices.facade.OutboundBatchRequestPartDTO
import de.hybris.platform.outboundservices.facade.SyncParameters
import de.hybris.platform.outboundservices.facade.SyncParametersBuilder
import de.hybris.platform.outboundservices.service.DestinationSearchService
import de.hybris.platform.outboundservices.service.MultiPartRequestGenerator
import de.hybris.platform.testframework.JUnitPlatformSpecification
import org.junit.Rule
import org.junit.Test
import org.springframework.http.HttpEntity
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import rx.observers.TestSubscriber
import spock.lang.Shared

@UnitTest
class DefaultOutboundServiceFacadeUnitTest extends JUnitPlatformSpecification {
    private static final def ENDPOINT_URL = "http://my.consumed.destination/some/path"
    private static final def DESTINATION_ID = 'destination'
    private static final def ITEM_TYPE = 'MyType'
    private static final def IO_CODE = 'integrationObjectCode'
    private static final def INTEGRATION_MODEL = new IntegrationObjectModel(code: IO_CODE)
    private static final def RESPONSE = new ResponseEntity(HttpStatus.ACCEPTED)
    private static final def DESTINATION = new ConsumedDestinationModel(id: DESTINATION_ID, url: ENDPOINT_URL)
    private static final def HTTP_ENTITY = new HttpEntity([:])
    private static final def BATCH_HTTP_ENTITY = new HttpEntity('batchBody')
    private static final def BATCH_URI = '/$batch'

    @Shared
    def item = Stub(ItemModel) {
        getItemtype() >> ITEM_TYPE
    }
    @Shared
    def payloadObject = Stub Object

    @Rule
    TestApplicationContext applicationContext = new TestApplicationContext()

    def restClient = Mock(RemoteSystemClient)
    def requestDecoratorService = Mock(RequestDecoratorService)
    def batchRequestGenerator = Stub(MultiPartRequestGenerator)
    def integrationObjectService = Stub(IntegrationObjectService)
    def searchService = Stub(DestinationSearchService)
    def facade = new DefaultOutboundServiceFacade(restClient)
    def deprecatedFacade = new DefaultOutboundServiceFacade(Stub(DecoratorContextFactory), restClient)

    def setup() {
        facade.setRequestDecoratorService(requestDecoratorService)
        facade.setMultiPartRequestGenerator(batchRequestGenerator)

        deprecatedFacade.setRequestDecoratorService(requestDecoratorService)
        deprecatedFacade.setMultiPartRequestGenerator(batchRequestGenerator)

        integrationObjectService.findIntegrationObject(IO_CODE) >> INTEGRATION_MODEL
        searchService.findDestination(DESTINATION_ID) >> DESTINATION

        resetStaticServices()
        applicationContext.addBean('integrationObjectService', integrationObjectService)
        applicationContext.addBean('destinationSearchService', searchService)
    }

    def cleanupSpec() {
        resetStaticServices()
    }

    private void resetStaticServices() {
        SyncParametersBuilder.ioService = null
        SyncParametersBuilder.destinationSearchService = null
    }

    @Test
    void "cannot be created with null RemoteSystemClient"() {
        when:
        new DefaultOutboundServiceFacade(null)

        then:
        def e = thrown IllegalArgumentException
        e.message == "RemoteSystemClient cannot be null"
    }

    @Test
    void "send throws IllegalArgumentException when #arg is #condition"() {
        when:
        facade.send(itemModel, code, destinationId)

        then:
        def ex = thrown IllegalArgumentException
        ex.message.contains arg

        where:
        arg             | itemModel | code    | destinationId  | condition
        'ioCode'        | item      | null    | DESTINATION_ID | 'null'
        'ioCode'        | item      | ''      | DESTINATION_ID | 'empty'
        'destinationId' | item      | IO_CODE | null           | 'null'
        'destinationId' | item      | IO_CODE | ''             | 'empty'
    }

    @Test
    void "send throws NullPointerException when ItemModel is null"() {
        when:
        facade.send(null, IO_CODE, DESTINATION_ID)

        then:
        def ex = thrown NullPointerException
        ex.message.contains "ItemModel cannot be null"
    }

    @Test
    void "send throws a NullPointerException when the SyncParameters is null"() {
        when:
        facade.send(null)

        then:
        def e = thrown NullPointerException
        e.message == "SyncParameters should not be null."
    }

    @Test
    def 'facade completes with a correct response when no errors occur'() {
        given: 'decorator service returns a successful response'
        def probe = new TestSubscriber()
        requestDecoratorService.createHttpEntity(_ as SyncParameters) >> HTTP_ENTITY

        when: 'facade is called'
        facade.send(*params).subscribe probe

        then: 'a post request is made to the remote system'
        1 * restClient.post(DESTINATION, _ as HttpEntity, Map.class) >> RESPONSE

        and: 'the item is sent and the response is received'
        probe.assertValue RESPONSE
        probe.assertCompleted()
        probe.assertNoErrors()

        where:
        params << [[objectParameters()], [item, IO_CODE, DESTINATION_ID]]
    }

    @Test
    void "facade does not consumes any exception when RequestDecoratorService throws it because the SyncParams has non-existing destination"() {
        given: 'decorator service throws the exception'
        def probe = new TestSubscriber()
        def errorMessage = 'non-existing destination'
        def contextWithErrors = Stub(DecoratorContext) {
            getErrors() >> [errorMessage]
        }
        requestDecoratorService.createHttpEntity(_ as SyncParameters) >>
                { throw new DecoratorContextErrorException(contextWithErrors) }

        when: 'facade is called'
        facade.send(objectParameters(destination: Stub(ConsumedDestinationNotFoundModel))).subscribe probe

        then: 'facade does not consumes the exception'
        def e = thrown DecoratorContextErrorException
        e.message.contains errorMessage
    }

    @Test
    void "facade does not consumes any exception when RequestDecoratorService throws it because the three params has non-existing destination"() {
        given: 'services cannot find the model for unknown destId'
        def nonExistingDestinationId = 'someId'
        searchService.findDestination(nonExistingDestinationId) >>
                new ConsumedDestinationNotFoundModel(nonExistingDestinationId)

        and: 'decorator service throws the exception'
        def probe = new TestSubscriber()
        def error = 'non-existing destination'
        def contextWithErrors = Stub(DecoratorContext) {
            getErrors() >> [error]
        }
        requestDecoratorService.createHttpEntity(_ as SyncParameters) >> { throw new DecoratorContextErrorException(contextWithErrors) }

        when: 'facade is called'
        facade.send(item, IO_CODE, nonExistingDestinationId).subscribe probe

        then: 'facade does not consumes the exception'
        def e = thrown DecoratorContextErrorException
        e.message.contains error
    }

    @Test
    void "facade will consume all the exceptions when RequestDecoratorService crashes for #params"() {
        given: 'decorator service throws the exception'
        def probe = new TestSubscriber()
        requestDecoratorService.createHttpEntity(_ as SyncParameters) >> { throw new RuntimeException('') }

        when: 'facade is called'
        facade.send(*params).subscribe probe

        then: 'facade consumes the exception'
        noExceptionThrown()

        and: 'the subscriber will get the exception'
        probe.assertError(RuntimeException)

        where:
        params << [[objectParameters()], [item, IO_CODE, DESTINATION_ID]]
    }

    @Test
    def 'rest client is not invoked when the decorator service crashes'() {
        given: 'a decorator that crashes'
        def probe = new TestSubscriber()
        def exception = new RuntimeException('IGNORE - testing exception')
        requestDecoratorService.createHttpEntity(_ as SyncParameters) >> { throw exception }

        when: 'facade is called'
        facade.send(*params).subscribe probe

        then: 'the rest template was not created'
        0 * restClient._

        where:
        params << [[objectParameters()], [item, IO_CODE, DESTINATION_ID]]
    }

    @Test
    void 'rest client is not invoked when the integration object has a rootClass'() {
        given:
        def classIO = Stub(IntegrationObjectModel) {
            getCode() >> IO_CODE
            getRootClass() >> Stub(IntegrationObjectClassModel)
        }

        integrationObjectService.findIntegrationObject(IO_CODE) >> classIO

        def probe = new TestSubscriber()
        requestDecoratorService.createHttpEntity(_ as SyncParameters) >> HTTP_ENTITY

        when: 'facade is called'
        facade.send(objectParameters([integrationModel: classIO])).subscribe probe

        then: 'the rest template was not created'
        0 * restClient._
    }

    @Test
    void 'facade completes with a correct response when decorator service is not injected'() {
        given: 'decorator service returns is not injected'
        facade.requestDecoratorService = null
        and: 'decorator service is present in the application context'
        applicationContext.addBean 'requestDecoratorService', Stub(RequestDecoratorService) {
            createHttpEntity(_ as SyncParameters) >> HTTP_ENTITY
        }

        when: 'facade is called'
        def probe = new TestSubscriber()
        facade.send(*params).subscribe probe

        then: 'a post request is made to the remote system'
        1 * restClient.post(DESTINATION, HTTP_ENTITY, Map.class) >> RESPONSE

        and: 'the item is sent and the response is received'
        probe.assertValue RESPONSE
        probe.assertCompleted()
        probe.assertNoErrors()

        where:
        params << [[objectParameters()], [item, IO_CODE, DESTINATION_ID]]
    }

    @Test
    void "deprecated facade cannot be created with null RemoteSystemClient"() {
        when:
        new DefaultOutboundServiceFacade(Stub(DecoratorContextFactory), null)

        then:
        def e = thrown IllegalArgumentException
        e.message == "RemoteSystemClient cannot be null"
    }

    @Test
    void 'deprecated facade completes with a correct response when no errors occur'() {
        given: 'decorator service returns a successful response'
        requestDecoratorService.createHttpEntity(_ as SyncParameters) >> HTTP_ENTITY

        when: 'facade is called'
        def probe = new TestSubscriber()
        deprecatedFacade.send(*params).subscribe probe

        then: 'a post request is made to the remote system'
        1 * restClient.post(DESTINATION, _ as HttpEntity, Map.class) >> RESPONSE

        and: 'the item is sent and the response is received'
        probe.assertValue RESPONSE
        probe.assertCompleted()
        probe.assertNoErrors()

        where:
        params << [[objectParameters()], [item, IO_CODE, DESTINATION_ID]]
    }

    @Test
    void 'rest client is not invoked in batch process when the decorator service crashes'() {
        given: 'a decorator that crashes'
        def exceptionMessage = 'IGNORE - testing exception'
        def exception = new RuntimeException(exceptionMessage)
        requestDecoratorService.createHttpEntity(_ as SyncParameters) >> { throw exception }

        when:
        facade.sendBatch([itemParameters(), itemParameters()])

        then:
        def e = thrown(RuntimeException)
        e.getMessage() == exceptionMessage

        and:
        0 * restClient._
    }

    @Test
    void 'sendBatch throws exception when the list has a SyncParameter with ConsumedDestinationNotFoundModel, by creating http entity'() {
        given:
        def errorMessage = 'decorator service crashed because of non-existing destination'
        requestDecoratorService.createHttpEntity(_ as SyncParameters) >> { throw new RuntimeException(errorMessage) }

        when: 'facade is called'
        facade.sendBatch([itemParameters(), itemParameters(destination: new ConsumedDestinationNotFoundModel(''))])

        then:
        def e = thrown(RuntimeException)
        e.message == errorMessage
    }

    @Test
    void 'rest client is invoked once in batch process with the consolidated request httpEntity'() {
        given: 'decorator creates a request'
        requestDecoratorService.createHttpEntity(_ as SyncParameters) >> HTTP_ENTITY
        and: 'batch converter consolidates requests successfully'
        batchRequestGenerator.generate(_ as List<OutboundBatchRequestPartDTO>) >> BATCH_HTTP_ENTITY

        when: 'facade is called'
        def response = facade.sendBatch([itemParameters(), itemParameters()])

        then: 'the rest client is invoked one time with the batch request'
        1 * restClient.post(DESTINATION, BATCH_HTTP_ENTITY, String.class, BATCH_URI) >> RESPONSE

        and: 'the item is sent and the response is received'
        response == RESPONSE
    }

    @Test
    void 'rest client is invoked once in batch process with the consolidated request httpEntity using deprecated facade'() {
        given: 'decorator creates a request'
        requestDecoratorService.createHttpEntity(_ as SyncParameters) >> HTTP_ENTITY
        and: 'batch converter consolidates requests successfully'
        batchRequestGenerator.generate(_ as List<OutboundBatchRequestPartDTO>) >> BATCH_HTTP_ENTITY

        when: 'facade is called'
        def response = deprecatedFacade.sendBatch([itemParameters(), objectParameters()])

        then: 'the rest client is invoked one time with the batch request'
        1 * restClient.post(DESTINATION, BATCH_HTTP_ENTITY, String.class, BATCH_URI) >> RESPONSE

        and: 'the item is sent and the response is received'
        response == RESPONSE
    }

    @Test
    void 'sendBatch does not send the batch to the client when passed list of SyncParameters #condition'() {
        when:
        facade.sendBatch(params)

        then:
        0 * restClient.post(DESTINATION, _ as HttpEntity, String.class, BATCH_URI)

        where:
        params                                      | condition
        null                                        | 'is null'
        []                                          | 'is empty'
        [objectParameters(isBatchSupported: false)] | 'has batch support not enabled'
    }

    @Test
    void 'sendBatch does not send the non-ItemModel payloads even if the batch is enabled for that SyncParameter'() {
        given: 'decorator creates a request'
        requestDecoratorService.createHttpEntity(_ as SyncParameters) >> HTTP_ENTITY

        when:
        facade.sendBatch([
                objectParameters(payloadObject: new ItemModel(), isBatchSupported: true),
                objectParameters(payloadObject: new Object(), isBatchSupported: true)])

        then:
        1 * restClient.post(DESTINATION, _ as HttpEntity, String.class, BATCH_URI)
    }

    @Test
    void 'sendBatch sends the batch to the client when passed list of SyncParameters has batch support enabled'() {
        given: 'decorator creates a request'
        requestDecoratorService.createHttpEntity(_ as SyncParameters) >> HTTP_ENTITY
        def syncParamsWithBatchEnabled = itemParameters(isBatchSupported: true)

        when:
        facade.sendBatch([syncParamsWithBatchEnabled])

        then:
        1 * restClient.post(DESTINATION, _ as HttpEntity, String.class, BATCH_URI)
    }

    @Test
    void 'access to the injected integrationRestTemplateFactory is possible'() {
        given:
        facade.setIntegrationRestTemplateFactory(Stub(IntegrationRestTemplateFactory))

        expect:
        facade.getIntegrationRestTemplateFactory() != null
    }

    @Test
    void 'access to the injected monitoringDecorator is possible'() {
        given:
        facade.setMonitoringDecorator(Stub(OutboundRequestDecorator))

        expect:
        facade.getMonitoringDecorator() != null
    }

    SyncParameters syncParameters(IntegrationObjectModel model,
                                  ConsumedDestinationModel dest,
                                  Object payload,
                                  boolean batchSupport) {
        Stub(SyncParameters) {
            getPayloadObject() >> payload
            getItem() >> payload
            getDestination() >> dest
            getIntegrationObject() >> model
            isBatchSupported() >> batchSupport
        }
    }

    SyncParameters itemParameters(Map<String, ?> params = Collections.emptyMap()) {
        syncParameters(
                params['integrationModel'] as IntegrationObjectModel ?: INTEGRATION_MODEL,
                params['destination'] as ConsumedDestinationModel ?: DESTINATION,
                params['payloadObject'] ?: item,
                params['isBatchSupported'] as boolean ?: true)
    }

    SyncParameters objectParameters(Map<String, ?> params = Collections.emptyMap()) {
        syncParameters(
                params['integrationModel'] as IntegrationObjectModel ?: INTEGRATION_MODEL,
                params['destination'] as ConsumedDestinationModel ?: DESTINATION,
                params['payloadObject'] ?: payloadObject,
                params['isBatchSupported'] as boolean ?: false)
    }
}
