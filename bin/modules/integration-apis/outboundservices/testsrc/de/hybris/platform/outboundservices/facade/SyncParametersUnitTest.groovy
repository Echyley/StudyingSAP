/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.outboundservices.facade

import de.hybris.bootstrap.annotations.UnitTest
import de.hybris.platform.apiregistryservices.model.ConsumedDestinationModel
import de.hybris.platform.core.model.ItemModel
import de.hybris.platform.integrationservices.model.IntegrationObjectClassModel
import de.hybris.platform.integrationservices.model.IntegrationObjectItemModel
import de.hybris.platform.integrationservices.model.IntegrationObjectModel
import de.hybris.platform.integrationservices.service.IntegrationObjectService
import de.hybris.platform.integrationservices.util.TestApplicationContext
import de.hybris.platform.outboundservices.enums.OutboundSource
import de.hybris.platform.outboundservices.event.impl.DefaultEventType
import de.hybris.platform.outboundservices.service.DestinationSearchService
import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException
import de.hybris.platform.testframework.JUnitPlatformSpecification
import org.junit.Rule
import org.junit.Test

@UnitTest
class SyncParametersUnitTest extends JUnitPlatformSpecification {
    private static final def CREATED_EVENT_TYPE = DefaultEventType.CREATED
    private static final def IO_CODE = 'TestObject'
    private static final def DESTINATION_ID = 'TestConsumedDest'
    private static final def INTEGRATION_KEY = 'key'
    private static final def CHANGE_ID = UUID.randomUUID().toString()
    private static final def INTEGRATION_MODEL = new IntegrationObjectModel(code: IO_CODE)
    private static final def ITEM = new ItemModel()
    private static final def PAYLOAD_OBJECT = new Object()
    private static final def DESTINATION_MODEL = new ConsumedDestinationModel(id: DESTINATION_ID)
    private static final def UUID_PATTERN = "[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}"

    def ioService = Stub IntegrationObjectService
    def destinationSearchService = Stub DestinationSearchService

    @Rule
    TestApplicationContext applicationContext

    def setup() {
        resetStaticServices()
        applicationContext.addBean('integrationObjectService', ioService)
        applicationContext.addBean('destinationSearchService', destinationSearchService)
    }

    def cleanupSpec() {
        resetStaticServices()
    }

    private void resetStaticServices() {
        SyncParametersBuilder.ioService = null
        SyncParametersBuilder.destinationSearchService = null
    }

    @Test
    def "IllegalArgumentException is thrown when none of IntegrationObjectCode and IntegrationObjectModel are provided"() {
        when:
        defaultBuilder()
                .withIntegrationObjectCode(null)
                .withIntegrationObject(null)
                .build()

        then:
        def e = thrown IllegalArgumentException
        e.message == 'Either IntegrationObjectModel, or integrationObjectCode must be provided.'
    }

    @Test
    def "IllegalArgumentException is thrown when none of ConsumedDestinationModel and DestinationId are provided"() {
        when:
        defaultBuilder()
                .withDestinationId(null)
                .withDestination(null)
                .build()

        then:
        def e = thrown IllegalArgumentException
        e.message == 'Either ConsumedDestinationModel, or destinationId must be provided.'
    }

    @Test
    def "IllegalArgumentException is thrown when none of payloadObject and integration key are provided"() {
        when:
        defaultBuilder()
                .withPayloadObject(null)
                .withIntegrationKey(null)
                .build()

        then:
        def e = thrown IllegalArgumentException
        e.message == 'Either payloadObject, or integrationKey must be provided.'
    }

    @Test
    def "no exception is thrown when request is built with null #attr"() {
        when:
        defaultBuilder()."${method}"(null).build()

        then:
        noExceptionThrown()

        where:
        method          | attr
        'withEventType' | 'eventType'
        'withChangeId'  | 'changeId'
    }

    @Test
    def "default to UNKNOWN source when a source is not provided or null"() {
        expect:
        defaultBuilder().withSource(null).build().source == OutboundSource.UNKNOWN
    }

    @Test
    def "default to UNKNOWN EventType when an event type is not provided or null"() {
        expect:
        defaultBuilder().withEventType(null).build().eventType == DefaultEventType.UNKNOWN
    }

    @Test
    def "changeID is assigned with a UUID by default"() {
        expect:
        defaultBuilder().build().getChangeId().matches(UUID_PATTERN)
    }

    @Test
    def "SyncParameters is created with random changeId by the builder if changeID is not provided or null"() {
        expect:
        defaultBuilder().withChangeId(null).build().getChangeId() != defaultBuilder().withChangeId(null).build().getChangeId()
    }

    @Test
    def 'parameters correctly built when all fields are provided to the builder'() {
        given:
        def context = SyncParameters.syncParametersBuilder()
                .withPayloadObject(PAYLOAD_OBJECT)
                .withDestination(DESTINATION_MODEL)
                .withIntegrationObject(INTEGRATION_MODEL)
                .withSource(OutboundSource.OUTBOUNDSYNC)
                .withEventType(CREATED_EVENT_TYPE)
                .withIntegrationKey(INTEGRATION_KEY)
                .withChangeId(CHANGE_ID)
                .build()

        expect:
        with(context) {
            payloadObject == PAYLOAD_OBJECT
            destination == DESTINATION_MODEL
            destinationId == DESTINATION_MODEL.id
            integrationObject == INTEGRATION_MODEL
            integrationObjectCode == INTEGRATION_MODEL.code
            source == OutboundSource.OUTBOUNDSYNC
            eventType == CREATED_EVENT_TYPE
            integrationKey == INTEGRATION_KEY
            changeId == CHANGE_ID
        }
    }

    @Test
    def "destinationModel can be generated from destinationId if model is not provided or null"() {
        given: 'destinationSearchService can find the model for id'
        destinationSearchService.findDestination(DESTINATION_ID) >> DESTINATION_MODEL

        expect:
        defaultBuilder()
                .withDestination(null)
                .withDestinationId(DESTINATION_ID)
                .build()
                .destination == DESTINATION_MODEL
    }

    @Test
    def "destinationModel of type ConsumedDestinationNotFoundModel will be generated from destinationId if model cannot be found"() {
        given: 'destinationSearchService cannot find the model from id and returns ConsumedDestinationNotFoundModel'
        def nullModel = new ConsumedDestinationNotFoundModel(DESTINATION_ID)
        destinationSearchService.findDestination(DESTINATION_ID) >> nullModel

        expect:
        defaultBuilder()
                .withDestination(null)
                .withDestinationId(DESTINATION_ID)
                .build()
                .destination == nullModel
    }

    @Test
    def "integrationObjectModel can be generated from the IntegrationObjectCode"() {
        given: 'IOService can find the model from the code'
        ioService.findIntegrationObject(IO_CODE) >> INTEGRATION_MODEL

        expect:
        defaultBuilder()
                .withIntegrationObjectCode(IO_CODE)
                .withIntegrationObject(null)
                .build()
                .integrationObject == INTEGRATION_MODEL
    }

    @Test
    def "ModelNotFoundException is thrown when IntegrationObjectModel cannot be found for the given IntegrationObjectCode"() {
        given: 'IOService cannot find the model from the code'
        ioService.findIntegrationObject(IO_CODE) >> { throw new ModelNotFoundException('') }

        when:
        defaultBuilder()
                .withIntegrationObjectCode(IO_CODE)
                .withIntegrationObject(null)
                .build()

        then:
        thrown ModelNotFoundException
    }

    @Test
    def "getItem() will return #returnValue if the payload object #condition an instance of ItemModel"() {
        expect:
        defaultBuilder()
                .withPayloadObject(payload)
                .build()
                .item == expectedItem

        where:
        returnValue          | payload         | expectedItem | condition
        'the payload object' | new ItemModel() | payload      | 'is'
        'null'               | new Object()    | null         | 'is not'
    }

    @Test
    def "getIntegrationObjectCode() returns the code of the integrationObjectModel #condition"() {
        expect:
        defaultBuilder()
                .withIntegrationObjectCode(code)
                .withIntegrationObject(INTEGRATION_MODEL)
                .build()
                .integrationObjectCode == INTEGRATION_MODEL.code

        where:
        code         | condition
        null         | 'when code is not provided or null'
        'randomCode' | 'even when the provided code is different'
    }

    @Test
    def 'getDestinationId() returns the id of the destinationModel #condition'() {
        expect:
        defaultBuilder()
                .withDestinationId(id)
                .withDestination(DESTINATION_MODEL)
                .build()
                .destinationId == DESTINATION_MODEL.id

        where:
        id         | condition
        null       | 'when the id is not provided or null'
        'randomId' | 'even when the provided id is different'
    }

    @Test
    def "isBatchSupportedForIOModeling returns #returnValue when the integrationObjectModel has #modeling"() {
        given:
        def model = new IntegrationObjectModel(code: IO_CODE, classes: classes, items: items)

        when:
        def params =
                defaultBuilder()
                        .withIntegrationObject(model)
                        .build()

        then:
        params.isBatchSupported() == returnValue

        where:
        returnValue | classes                             | items                              | modeling
        false       | [Stub(IntegrationObjectClassModel)] | []                                 | 'a class model'
        true        | []                                  | [Stub(IntegrationObjectItemModel)] | 'only item model'
        true        | []                                  | []                                 | 'no model'
    }

    @Test
    def "can build a copy from another instance of parameters but with different changeId"() {
        when:
        def orgParams = defaultBuilder().build()
        def parameters = SyncParametersBuilder.from(orgParams).build()

        then:
        with(orgParams) {
            payloadObject == parameters.payloadObject
            source == parameters.source
            destination == parameters.destination
            integrationObject == parameters.integrationObject
            eventType == parameters.eventType
            integrationKey == parameters.integrationKey
            changeId != parameters.changeId
        }
    }

    @Test
    def "two SyncParameters instances are equal when all fields are equal"() {
        given:
        def params = SyncParameters.syncParametersBuilder()
                .withPayloadObject(PAYLOAD_OBJECT)
                .withDestination(DESTINATION_MODEL)
                .withIntegrationObject(INTEGRATION_MODEL)
                .withSource(OutboundSource.OUTBOUNDSYNC)
                .withEventType(CREATED_EVENT_TYPE)
                .withIntegrationKey(INTEGRATION_KEY)
                .withChangeId(CHANGE_ID)
                .build()

        expect:
        params == defaultBuilder().build()
    }

    @Test
    def "two SyncParameters instances are equal even when changeId is different."() {
        given:
        def params = defaultBuilder().build()

        expect:
        params == defaultBuilder().withChangeId('randomValue').build()
    }

    @Test
    def "two SyncParameters instances are not equal when #condition"() {
        given:
        def params = defaultBuilder().build()

        expect:
        params != defaultBuilder()."${method}"(changedValue).build()

        where:
        method                  | changedValue                                   | condition
        'withPayloadObject'     | new Object()                                   | 'payloadObject is different'
        'withSource'            | OutboundSource.WEBHOOKSERVICES                 | 'OutboundSource is different'
        'withEventType'         | DefaultEventType.DELETED                       | 'EventType is different'
        'withIntegrationKey'    | 'anotherKey'                                   | 'IntegrationKey is different'
        'withIntegrationObject' | new IntegrationObjectModel(code: 'randomCode') | 'IntegrationObject is different'
        'withDestination'       | new ConsumedDestinationModel(id: 'anotherID')  | 'ConsumedDestinationModel is different'
    }

    @Test
    def "two SyncParameters instances have same hash code when all fields are equal"() {
        given:
        def params =
                SyncParameters.syncParametersBuilder()
                        .withPayloadObject(PAYLOAD_OBJECT)
                        .withDestination(DESTINATION_MODEL)
                        .withIntegrationObject(INTEGRATION_MODEL)
                        .withSource(OutboundSource.OUTBOUNDSYNC)
                        .withEventType(CREATED_EVENT_TYPE)
                        .withIntegrationKey(INTEGRATION_KEY)
                        .withChangeId(CHANGE_ID)
                        .build()

        expect:
        params.hashCode() == defaultBuilder().build().hashCode()
    }

    @Test
    def "two SyncParameters instances have different hash codes when #condition"() {
        given:
        def params = defaultBuilder().build()

        expect:
        params.hashCode() != defaultBuilder()."${method}"(changedValue).build().hashCode()

        where:
        method                  | changedValue                                    | condition
        'withPayloadObject'     | new Object()                                    | 'payloadObject is different'
        'withSource'            | OutboundSource.WEBHOOKSERVICES                  | 'OutboundSource is different'
        'withEventType'         | DefaultEventType.DELETED                        | 'EventType is different'
        'withIntegrationKey'    | 'anotherKey'                                    | 'IntegrationKey is different'
        'withChangeId'          | 'anotherID'                                     | 'ChangeId is different'
        'withIntegrationObject' | new IntegrationObjectModel(code: 'anotherCode') | 'IntegrationObjectModel is different'
        'withDestination'       | new ConsumedDestinationModel(id: 'anotherID')   | 'consumedDestinationModel is different'
    }

    @Test
    def "toString() contains information about all essential attributes"() {
        given:
        def params = defaultBuilder().withChangeId(CHANGE_ID).build()

        expect:
        with(params.toString()) {
            contains "'${PAYLOAD_OBJECT.toString()}'"
            contains "'$INTEGRATION_KEY'"
            contains "'$IO_CODE'"
            contains "'$DESTINATION_ID'"
            contains "'$OutboundSource.OUTBOUNDSYNC'"
            contains "'$CREATED_EVENT_TYPE.type'"
            contains "'$CHANGE_ID'"
        }

        params.getChangeId().matches(UUID_PATTERN)
    }

    @Test
    def "deprecated SyncParameters constructor for item model creates same instance as SyncParameters with item as a payloadObject"() {
        given:
        def paramsWithItem = new SyncParameters(ITEM, IO_CODE, INTEGRATION_MODEL, DESTINATION_ID, DESTINATION_MODEL, OutboundSource.OUTBOUNDSYNC)
        def paramsWithObject =
                SyncParameters.syncParametersBuilder()
                        .withPayloadObject(ITEM)
                        .withIntegrationObjectCode(IO_CODE)
                        .withIntegrationObject(INTEGRATION_MODEL)
                        .withDestinationId(DESTINATION_ID)
                        .withDestination(DESTINATION_MODEL)
                        .withSource(OutboundSource.OUTBOUNDSYNC)
                        .build()

        expect:
        paramsWithItem == paramsWithObject
    }

    private static SyncParametersBuilder defaultBuilder() {
        SyncParameters.syncParametersBuilder()
                .withIntegrationObjectCode(IO_CODE)
                .withPayloadObject(PAYLOAD_OBJECT)
                .withDestinationId(DESTINATION_ID)
                .withDestination(DESTINATION_MODEL)
                .withIntegrationObject(INTEGRATION_MODEL)
                .withSource(OutboundSource.OUTBOUNDSYNC)
                .withEventType(CREATED_EVENT_TYPE)
                .withIntegrationKey(INTEGRATION_KEY)
                .withChangeId(CHANGE_ID)
    }
}
