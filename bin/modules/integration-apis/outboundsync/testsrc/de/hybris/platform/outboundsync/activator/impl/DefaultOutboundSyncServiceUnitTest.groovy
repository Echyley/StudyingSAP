/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.outboundsync.activator.impl

import de.hybris.bootstrap.annotations.UnitTest
import de.hybris.platform.apiregistryservices.model.ConsumedDestinationModel
import de.hybris.platform.core.PK
import de.hybris.platform.core.model.ItemModel
import de.hybris.platform.cronjob.enums.CronJobStatus
import de.hybris.platform.integrationservices.exception.MissingKeyReferencedAttributeValueException
import de.hybris.platform.integrationservices.model.IntegrationObjectDescriptor
import de.hybris.platform.integrationservices.model.IntegrationObjectModel
import de.hybris.platform.integrationservices.service.IntegrationObjectService
import de.hybris.platform.integrationservices.service.ItemModelSearchService
import de.hybris.platform.integrationservices.util.TestApplicationContext
import de.hybris.platform.outboundservices.enums.OutboundSource
import de.hybris.platform.outboundservices.facade.OutboundServiceFacade
import de.hybris.platform.outboundservices.facade.SyncParameters
import de.hybris.platform.outboundservices.facade.SyncParametersBuilder
import de.hybris.platform.outboundservices.service.DestinationSearchService
import de.hybris.platform.outboundservices.service.OutboundMultiPartResponseParser
import de.hybris.platform.outboundsync.activator.OutboundItemConsumer
import de.hybris.platform.outboundsync.dto.OutboundChangeType
import de.hybris.platform.outboundsync.dto.OutboundItem
import de.hybris.platform.outboundsync.dto.OutboundItemChange
import de.hybris.platform.outboundsync.dto.OutboundItemDTO
import de.hybris.platform.outboundsync.dto.OutboundItemDTOGroup
import de.hybris.platform.outboundsync.events.AbortedOutboundSyncEvent
import de.hybris.platform.outboundsync.events.CompletedOutboundSyncEvent
import de.hybris.platform.outboundsync.events.SystemErrorOutboundSyncEvent
import de.hybris.platform.outboundsync.job.OutboundItemFactory
import de.hybris.platform.outboundsync.job.impl.DefaultOutboundSyncJobRegister
import de.hybris.platform.outboundsync.job.impl.OutboundSyncJob
import de.hybris.platform.outboundsync.job.impl.OutboundSyncJobRegister
import de.hybris.platform.outboundsync.model.OutboundChannelConfigurationModel
import de.hybris.platform.outboundsync.model.OutboundSyncCronJobModel
import de.hybris.platform.outboundsync.model.OutboundSyncRetryModel
import de.hybris.platform.outboundsync.retry.RetryUpdateException
import de.hybris.platform.outboundsync.retry.SyncRetryService
import de.hybris.platform.servicelayer.event.EventService
import de.hybris.platform.testframework.JUnitPlatformSpecification
import org.junit.Rule
import org.junit.Test
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import rx.Observable

import static de.hybris.platform.outboundsync.job.impl.OutboundSyncStateBuilder.initialOutboundSyncState

@UnitTest
class DefaultOutboundSyncServiceUnitTest extends JUnitPlatformSpecification {
    private static final def DEFAULT_CHANGED_ITEM = new ItemModel()
    private static final def DEFAULT_ROOT_ITEM_PK = 123
    private static final def CRON_JOB_PK = PK.fromLong(456)
    private static final def TEST_IO_CODE = "TestIntegrationObjectIO"
    private static final def TEST_IO = new IntegrationObjectModel(code: TEST_IO_CODE)
    private static final def TEST_DESTINATION_ID = "TestDestination"
    private static final def TEST_DESTINATION = new ConsumedDestinationModel(id: TEST_DESTINATION_ID)
    private static final def ALL_SUCCESS_BATCH_RESPONSE = new ResponseEntity("multipleItemsSuccessBatchResponse", HttpStatus.CREATED)
    private static final def MIXED_SUCCESS_BATCH_RESPONSE = new ResponseEntity("mixedResponse", HttpStatus.CREATED)
    private static final def CHANGE_ID_1 = '1111'
    private static final def CHANGE_ID_2 = '2222'

    @Rule
    TestApplicationContext applicationContext

    def outboundServiceFacade = Mock(OutboundServiceFacade)
    def outboundItemConsumer = Mock(OutboundItemConsumer)
    def syncRetryService = Mock(SyncRetryService)
    def eventService = Mock(EventService)
    def itemModelSearchService = Mock ItemModelSearchService
    def jobRegister = Mock DefaultOutboundSyncJobRegister
    def responseParser = Mock(OutboundMultiPartResponseParser)
    def itemFactory = Stub(OutboundItemFactory) {
        createItem(_ as OutboundItemDTO) >> Stub(OutboundItem) {
            getIntegrationObject() >> Stub(IntegrationObjectDescriptor) {
                getCode() >> TEST_IO_CODE
            }
            getChannelConfiguration() >> Stub(OutboundChannelConfigurationModel) {
                getDestination() >> TEST_DESTINATION
            }
        }
    }

    def defaultOutboundSyncService = new DefaultOutboundSyncService(
            itemModelSearchService, itemFactory, jobRegister, outboundServiceFacade, responseParser)

    def setup() {
        defaultOutboundSyncService.syncRetryService = syncRetryService
        defaultOutboundSyncService.eventService = eventService
        defaultOutboundSyncService.outboundItemConsumer = outboundItemConsumer

        resetStaticServices()
        applicationContext.addBean('integrationObjectService',
                Stub(IntegrationObjectService) {
                    findIntegrationObject(TEST_IO_CODE) >> TEST_IO
                })
        applicationContext.addBean('destinationSearchService',
                Stub(DestinationSearchService) {
                    findDestination(TEST_DESTINATION_ID) >> TEST_DESTINATION
                })
    }

    def cleanupSpec() {
        resetStaticServices()
    }

    private void resetStaticServices() {
        SyncParametersBuilder.ioService = null
        SyncParametersBuilder.destinationSearchService = null
    }

    @Test
    def "cannot be instantiated with null #param"() {
        when:
        new DefaultOutboundSyncService(search, factory, register, facade, responseParser)

        then:
        def e = thrown IllegalArgumentException
        e.message == "$param cannot be null"

        where:
        param                     | search                       | factory                   | register                      | facade                      | responseParser
        'ItemModelSearchService'  | null                         | Stub(OutboundItemFactory) | Stub(OutboundSyncJobRegister) | Stub(OutboundServiceFacade) | Stub(OutboundMultiPartResponseParser)
        'OutboundItemFactory'     | Stub(ItemModelSearchService) | null                      | Stub(OutboundSyncJobRegister) | Stub(OutboundServiceFacade) | Stub(OutboundMultiPartResponseParser)
        'OutboundSyncJobRegister' | Stub(ItemModelSearchService) | Stub(OutboundItemFactory) | null                          | Stub(OutboundServiceFacade) | Stub(OutboundMultiPartResponseParser)
        'OutboundServiceFacade'   | Stub(ItemModelSearchService) | Stub(OutboundItemFactory) | Stub(OutboundSyncJobRegister) | null                        | Stub(OutboundMultiPartResponseParser)
        'MultiPartResponseParser' | Stub(ItemModelSearchService) | Stub(OutboundItemFactory) | Stub(OutboundSyncJobRegister) | Stub(OutboundServiceFacade) | null
    }

    @Test
    def "item is synced successfully when httpStatus is #status"() {
        given: 'cronjob is running'
        setupRunningCronJob()

        and: 'synchronization is successful'
        def outboundItemDTO = outboundItemDto()
        outboundServiceFacade.send(defaultSyncParams()) >> stubObservable(status)

        when:
        defaultOutboundSyncService.sync([outboundItemDTO])

        then:
        1 * outboundItemConsumer.consume(outboundItemDTO)
        1 * syncRetryService.handleSyncSuccess(_ as OutboundItemDTOGroup)

        where:
        status << [HttpStatus.CREATED, HttpStatus.OK]
    }

    @Test
    def "syncBatch does nothing when given empty collection of OutboundItemDTOGroup"() {
        when:
        defaultOutboundSyncService.syncBatch([])

        then:
        0 * outboundServiceFacade.sendBatch(_)
        0 * itemModelSearchService.nonCachingFindByPk(_)
    }

    @Test
    def "items are synchronized successfully in a batch request when response is successful for all batch parts"() {
        given: 'cronjob is running'
        setupRunningCronJob()
        and:
        def dtoGroup1 = outboundItemDTOGroup(outboundItemDto(DEFAULT_CHANGED_ITEM), DEFAULT_ROOT_ITEM_PK, CHANGE_ID_1)
        def anotherChangedItem = new ItemModel()
        def dtoGroup2 = outboundItemDTOGroup(outboundItemDto(anotherChangedItem), 234, CHANGE_ID_2)
        def syncParameters = [syncParametersFor(dtoGroup1, DEFAULT_CHANGED_ITEM), syncParametersFor(dtoGroup2, anotherChangedItem)]
        responseParser.parseMultiPartResponse(ALL_SUCCESS_BATCH_RESPONSE) >> [successResponse(CHANGE_ID_1), successResponse(CHANGE_ID_2)]

        when:
        defaultOutboundSyncService.syncBatch([dtoGroup1, dtoGroup2])

        then:
        1 * outboundServiceFacade.sendBatch({ comparingSyncParametersList(it, syncParameters) }) >> ALL_SUCCESS_BATCH_RESPONSE
        1 * eventService.publishEvent(new CompletedOutboundSyncEvent(dtoGroup1, true))
        1 * eventService.publishEvent(new CompletedOutboundSyncEvent(dtoGroup2, true))
        1 * syncRetryService.handleSyncSuccess(dtoGroup1)
        1 * syncRetryService.handleSyncSuccess(dtoGroup2)
        2 * outboundItemConsumer.consume(_)
    }

    @Test
    def "items are consumed and others added to retry in a batch request when response has a mix of success and error parts"() {
        given: 'cronjob is running'
        setupRunningCronJob()

        and:
        def dto1 = outboundItemDto(DEFAULT_CHANGED_ITEM)
        def dtoGroup1 = outboundItemDTOGroup(dto1, DEFAULT_ROOT_ITEM_PK, CHANGE_ID_1)
        def anotherChangedItem = new ItemModel()
        def dto2 = outboundItemDto(anotherChangedItem)
        def dtoGroup2 = outboundItemDTOGroup(dto2, 234, CHANGE_ID_2)
        def syncParameters = [syncParametersFor(dtoGroup1, DEFAULT_CHANGED_ITEM), syncParametersFor(dtoGroup2, anotherChangedItem)]
        responseParser.parseMultiPartResponse(MIXED_SUCCESS_BATCH_RESPONSE) >> [successResponse(CHANGE_ID_1), errorResponse(CHANGE_ID_2)]

        when:
        defaultOutboundSyncService.syncBatch([dtoGroup1, dtoGroup2])

        then:
        1 * outboundServiceFacade.sendBatch({ comparingSyncParametersList(it, syncParameters) }) >> MIXED_SUCCESS_BATCH_RESPONSE
        1 * eventService.publishEvent(new CompletedOutboundSyncEvent(dtoGroup1, true))
        1 * eventService.publishEvent(new CompletedOutboundSyncEvent(dtoGroup2, false))
        1 * syncRetryService.handleSyncSuccess(dtoGroup1)
        1 * syncRetryService.handleSyncFailure(dtoGroup2)
        1 * outboundItemConsumer.consume(dto1)
        0 * outboundItemConsumer.consume(dto2)
    }

    @Test
    def "items handled as error in a batch request when response does not contain matching ChangeId"() {
        given: 'cronjob is running'
        setupRunningCronJob()

        and:
        def dto1 = outboundItemDto(DEFAULT_CHANGED_ITEM)
        def dtoGroup1 = outboundItemDTOGroup(dto1, DEFAULT_ROOT_ITEM_PK, CHANGE_ID_1)
        def anotherChangedItem = new ItemModel()
        def dto2 = outboundItemDto(anotherChangedItem)
        def dtoGroup2 = outboundItemDTOGroup(dto2, 234, CHANGE_ID_2)
        def syncParameters = [syncParametersFor(dtoGroup1, DEFAULT_CHANGED_ITEM), syncParametersFor(dtoGroup2, anotherChangedItem)]
        responseParser.parseMultiPartResponse(MIXED_SUCCESS_BATCH_RESPONSE) >> [successResponse(null), errorResponse(null)]

        when:
        defaultOutboundSyncService.syncBatch([dtoGroup1, dtoGroup2])

        then:
        1 * outboundServiceFacade.sendBatch({ comparingSyncParametersList(it, syncParameters) }) >> MIXED_SUCCESS_BATCH_RESPONSE
        1 * eventService.publishEvent(new CompletedOutboundSyncEvent(dtoGroup1, false))
        1 * eventService.publishEvent(new CompletedOutboundSyncEvent(dtoGroup2, false))
        1 * syncRetryService.handleSyncFailure(dtoGroup1)
        1 * syncRetryService.handleSyncFailure(dtoGroup2)
        0 * outboundItemConsumer.consume(_)
    }

    @Test
    def "item is synced successfully when optional dependencies are not injected"() {
        given: 'optional dependencies are not injected'
        defaultOutboundSyncService.eventService = null
        defaultOutboundSyncService.outboundItemConsumer = null
        and: 'cronjob is running'
        setupRunningCronJob()

        when:
        defaultOutboundSyncService.sync([outboundItemDto()])

        then:
        1 * outboundServiceFacade.send(defaultSyncParams()) >> stubObservable(HttpStatus.CREATED)
        1 * syncRetryService.handleSyncSuccess(_ as OutboundItemDTOGroup)
    }

    @Test
    def "item is not synced successfully when the outbound facade resulted in error"() {
        given: 'cronjob is running'
        setupRunningCronJob()

        and: 'synchronization failed'
        def outboundItemDTO = outboundItemDto()
        outboundServiceFacade.send(defaultSyncParams()) >> stubObservableError()

        when:
        defaultOutboundSyncService.sync([outboundItemDTO])

        then:
        0 * outboundItemConsumer.consume(_)
        1 * eventService.publishEvent(new CompletedOutboundSyncEvent(CRON_JOB_PK, false, 1))
        and:
        1 * syncRetryService.handleSyncFailure({ it.outboundItemDTOs == [outboundItemDTO] })
    }

    @Test
    def 'change is not consumed when outbound sync facade crashes while sending the item'() {
        given: 'cronjob is running'
        setupRunningCronJob()

        and: 'an item for the change'
        def outboundItemDTO = outboundItemDto()

        and: 'the item send crashes'
        outboundServiceFacade.send(defaultSyncParams()) >> { throw new RuntimeException() }

        and: 'more synchronization attempts are possible in future'
        syncRetryService.handleSyncFailure(_ as OutboundItemDTOGroup) >> false

        when:
        defaultOutboundSyncService.sync([outboundItemDTO])

        then:
        0 * outboundItemConsumer.consume(_)
        1 * eventService.publishEvent(new CompletedOutboundSyncEvent(CRON_JOB_PK, false, 1))
    }

    @Test
    def "facade returns error and it is the last retry"() {
        given: 'cronjob is running'
        setupRunningCronJob()

        and: 'synchronization failed'
        def outboundItemDTO = outboundItemDto()
        outboundServiceFacade.send(defaultSyncParams()) >> stubObservableError()

        and: 'it was last synchronization attempt possible'
        syncRetryService.handleSyncFailure(_ as OutboundItemDTOGroup) >> true

        when:
        defaultOutboundSyncService.sync([outboundItemDTO])

        then:
        1 * outboundItemConsumer.consume(outboundItemDTO)
        1 * eventService.publishEvent(new CompletedOutboundSyncEvent(CRON_JOB_PK, false, 1))
    }

    @Test
    def "changes are not consumed when RetryUpdateException is thrown on last retry"() {
        given: 'cronjob is running'
        setupRunningCronJob()

        and: 'synchronization failed'
        def outboundItemDTO = outboundItemDto()
        outboundServiceFacade.send(defaultSyncParams()) >> stubObservableError()

        and: 'an exception is thrown while handling the failure'
        syncRetryService.handleSyncFailure(_ as OutboundItemDTOGroup) >> { throw new RetryUpdateException(Stub(OutboundSyncRetryModel)) }

        when:
        defaultOutboundSyncService.sync([outboundItemDTO])

        then:
        0 * outboundItemConsumer.consume(_)
        1 * eventService.publishEvent(new CompletedOutboundSyncEvent(CRON_JOB_PK, false, 1))
    }

    @Test
    def "change not consumed on success case when a RetryUpdateException occurs"() {
        given: 'cronjob is running'
        setupRunningCronJob()

        and: 'synchronization is successful'
        def outboundItemDTO = outboundItemDto()
        outboundServiceFacade.send(defaultSyncParams()) >> stubObservable(HttpStatus.CREATED)

        and: 'an exception is thrown while handling the success'
        syncRetryService.handleSyncSuccess(_ as OutboundItemDTOGroup) >> {
            throw new RetryUpdateException(new OutboundSyncRetryModel())
        }

        when:
        defaultOutboundSyncService.sync([outboundItemDTO])

        then:
        0 * outboundItemConsumer.consume(_)
        1 * eventService.publishEvent(new CompletedOutboundSyncEvent(CRON_JOB_PK, false, 1))
    }

    @Test
    def "CREATED item change is received, item is not found"() {
        given:
        def outboundItemDTO = Stub(OutboundItemDTO) {
            getItem() >> Stub(OutboundItemChange) {
                getChangeType() >> OutboundChangeType.CREATED
            }
            getRootItemPK() >> DEFAULT_ROOT_ITEM_PK
            getCronJobPK() >> CRON_JOB_PK
        }
        and: 'cronjob is running'
        setupRunningCronJob()

        and:
        itemModelSearchService.nonCachingFindByPk(PK.fromLong(DEFAULT_ROOT_ITEM_PK)) >> Optional.empty()

        when:
        defaultOutboundSyncService.sync([outboundItemDTO])

        then:
        0 * outboundServiceFacade.send(_)
        0 * outboundItemConsumer.consume(_)
    }

    @Test
    def "no item is synced when item cannot be found from its PK"() {
        given:
        def outboundItemDTO = Stub(OutboundItemDTO) {
            getRootItemPK() >> DEFAULT_ROOT_ITEM_PK
            getCronJobPK() >> CRON_JOB_PK
        }
        and: 'cronjob is running'
        setupRunningCronJob()

        and:
        itemModelSearchService.nonCachingFindByPk(PK.fromLong(DEFAULT_ROOT_ITEM_PK)) >> Optional.empty()

        when:
        defaultOutboundSyncService.sync([outboundItemDTO])

        then:
        0 * outboundServiceFacade.send(_)
        0 * outboundItemConsumer.consume(_)
    }

    @Test
    def 'abort event is published when cronjob is aborting and item is not synchronized'() {
        given: 'aborting cronjob'
        setupAbortedCronJob()

        and: 'a changed item'
        def outboundItemDto = outboundItemDto DEFAULT_CHANGED_ITEM

        when:
        defaultOutboundSyncService.sync([outboundItemDto])

        then: 'abort event is published'
        1 * eventService.publishEvent(new AbortedOutboundSyncEvent(CRON_JOB_PK, 1))

        and: 'item is not synchronized'
        0 * outboundServiceFacade.send(_)
    }

    @Test
    def 'abort event is published when cronjob is aborting and item for multipart request is not synchronized'() {
        given: 'aborting cronjob'
        setupAbortedCronJob()

        and: 'a changed item for multipart request'
        def dtoGroup = outboundItemDTOGroup(outboundItemDto())

        when:
        defaultOutboundSyncService.syncBatch([dtoGroup])

        then: 'abort event is published'
        1 * eventService.publishEvent(new AbortedOutboundSyncEvent(CRON_JOB_PK, 0))

        and: 'item is not synchronized'
        0 * outboundServiceFacade.sendBatch(_)
    }

    @Test
    def 'abort event is not published again when cronjob is aborted already and item is not synchronized'() {
        given: 'aborting cronjob'
        setupFinalCronJob()

        and: 'a changed item'
        def outboundItemDto = outboundItemDto DEFAULT_CHANGED_ITEM

        when:
        defaultOutboundSyncService.sync([outboundItemDto])

        then: 'no abort event published'
        0 * eventService.publishEvent(_ as AbortedOutboundSyncEvent)

        and: 'item is not synchronized'
        0 * outboundServiceFacade.send(_)
    }

    @Test
    def 'abort event is not published again when cronjob has been final already and item is not synchronized'() {
        given: 'aborting cronjob'
        setupFinalCronJob()

        and: 'a changed item'
        def dtoGroup = outboundItemDTOGroup outboundItemDto()

        when:
        defaultOutboundSyncService.syncBatch([dtoGroup])

        then: 'no abort event published'
        0 * eventService.publishEvent(_ as AbortedOutboundSyncEvent)

        and: 'item is not synchronized'
        0 * outboundServiceFacade.send(_)
    }

    @Test
    def "completedOutboundSyncEvent is published when synchronization is successful"() {
        given: 'cronjob is running'
        setupRunningCronJob()

        and: 'synchronization is successful'
        def outboundItemDTO1 = outboundItemDto()
        def outboundItemDTO2 = outboundItemDto()

        outboundServiceFacade.send(defaultSyncParams()) >> stubObservable(HttpStatus.CREATED)

        when:
        defaultOutboundSyncService.sync([outboundItemDTO1, outboundItemDTO2])

        then:
        1 * eventService.publishEvent(new CompletedOutboundSyncEvent(CRON_JOB_PK, true, 2))
    }

    @Test
    def 'SystemErrorOutboundSyncEvent is published when there is a systemic error'() {
        given: 'cronJob is running'
        setupRunningCronJob()

        and: 'a changed item'
        def outboundItemDto = outboundItemDto DEFAULT_CHANGED_ITEM

        and: 'systemic error occurs'
        outboundServiceFacade.send(defaultSyncParams()) >> { throw Stub(MissingKeyReferencedAttributeValueException) }

        when:
        defaultOutboundSyncService.sync([outboundItemDto])

        then:
        1 * eventService.publishEvent(new SystemErrorOutboundSyncEvent(CRON_JOB_PK, 1))
    }

    @Test
    def 'SystemErrorOutboundSyncEvent is published when batch synchronization returns a systemic error'() {
        given: 'cronJob is running'
        setupRunningCronJob()

        and: 'a changed item'
        def group = outboundItemDTOGroup(outboundItemDto())

        and: 'systemic error occurs'
        outboundServiceFacade.sendBatch(_ as List<SyncParameters>) >> { throw Stub(MissingKeyReferencedAttributeValueException) }

        when:
        defaultOutboundSyncService.syncBatch([group])

        then:
        1 * eventService.publishEvent(new SystemErrorOutboundSyncEvent(CRON_JOB_PK, [group]))
        0 * outboundItemConsumer.consume(_)
    }

    @Test
    def 'no more items are synchronized when a systemic error occurs'() {
        given: 'cronJob is found'
        def jobModel = notAbortedCronJob()
        cronJobByPkFound jobModel

        and: 'cronJob with systemic error'
        jobRegister.getJob(jobModel) >> sysErrorOutboundSyncJob()

        and: 'a changed item'
        def outboundItemDto = outboundItemDto DEFAULT_CHANGED_ITEM

        when:
        defaultOutboundSyncService.sync([outboundItemDto])

        then: 'no item sent'
        0 * outboundServiceFacade.send(_ as SyncParameters)
        and: 'SystemErrorOutboundSyncEvent is fired'
        0 * eventService.publishEvent(new SystemErrorOutboundSyncEvent(CRON_JOB_PK, 1))
    }

    @Test
    def 'no more items for multipart request are synchronized when a systemic error occurs'() {
        given: 'cronJob is found'
        def jobModel = notAbortedCronJob()
        cronJobByPkFound jobModel

        and: 'cronJob with systemic error'
        jobRegister.getJob(jobModel) >> sysErrorOutboundSyncJob()

        and: 'a changed item'
        def dtoGroup = outboundItemDTOGroup outboundItemDto()

        when:
        defaultOutboundSyncService.syncBatch([dtoGroup])

        then: 'no item sent'
        0 * outboundServiceFacade.send(_)
        and: 'SystemErrorOutboundSyncEvent is fired'
        0 * eventService.publishEvent(new SystemErrorOutboundSyncEvent(CRON_JOB_PK, 1))
    }

    @Test
    def 'only the item fails when a processing exception occurs'() {
        given: 'cronJob is running'
        setupRunningCronJob()

        and: 'a changed item'
        def dtos = [outboundItemDto()]

        and: 'failed processing item'
        outboundServiceFacade.send(defaultSyncParams()) >> { throw new RuntimeException() }

        when:
        defaultOutboundSyncService.sync dtos

        then: 'event fired to indicate the item failed'
        1 * eventService.publishEvent(new CompletedOutboundSyncEvent(CRON_JOB_PK, false, dtos.size()))
        and: 'no event fired to indicate a systemic error occurred'
        0 * eventService.publishEvent(_ as SystemErrorOutboundSyncEvent)
    }

    @Test
    def 'only the item fails when job register crashes'() {
        given: 'cronJob is running'
        def jobModel = notAbortedCronJob()
        cronJobByPkFound jobModel

        and: 'failed retrieving the job aggregate'
        jobRegister.getJob(jobModel) >> { throw new RuntimeException() }

        and:
        def itemDTOs = [outboundItemDto()]

        when:
        defaultOutboundSyncService.sync itemDTOs

        then: 'event fired to indicate the item failed'
        1 * eventService.publishEvent(new CompletedOutboundSyncEvent(CRON_JOB_PK, false, itemDTOs.size()))
    }

    @Test
    def 'only the item fails when job item search by PK crashes'() {
        given: 'cronJob is running'
        setupRunningCronJob()

        and: 'failed retrieving the root item'
        itemModelSearchService.nonCachingFindByPk(PK.fromLong(DEFAULT_ROOT_ITEM_PK)) >> { throw new RuntimeException() }

        and:
        def itemDTOs = [outboundItemDto()]

        when:
        defaultOutboundSyncService.sync itemDTOs

        then: 'event fired to indicate the item failed'
        1 * eventService.publishEvent(new CompletedOutboundSyncEvent(CRON_JOB_PK, false, itemDTOs.size()))
    }

    def stubObservable(HttpStatus httpStatus) {
        Observable.just Stub(ResponseEntity) {
            getStatusCode() >> httpStatus
        }
    }

    def stubObservableError() {
        stubObservable(HttpStatus.INTERNAL_SERVER_ERROR)
    }

    OutboundItemDTO outboundItemDto(itemModel = DEFAULT_CHANGED_ITEM, rootItemPk = DEFAULT_ROOT_ITEM_PK) {
        itemModelSearchService.nonCachingFindByPk(PK.fromLong(rootItemPk)) >> Optional.of(itemModel)
        Stub(OutboundItemDTO) {
            getRootItemPK() >> rootItemPk
            getCronJobPK() >> CRON_JOB_PK
        }
    }

    OutboundItemDTOGroup outboundItemDTOGroup(outboundItemDTO, rootItemPk = DEFAULT_ROOT_ITEM_PK, changeId = CHANGE_ID_1) {
        itemModelSearchService.nonCachingFindByPk(PK.fromLong(rootItemPk)) >> Optional.of(DEFAULT_CHANGED_ITEM)
        Stub(OutboundItemDTOGroup) {
            getOutboundItemDTOs() >> [outboundItemDTO]
            getCronJobPk() >> CRON_JOB_PK
            getRootItemPk() >> rootItemPk
            getIntegrationObjectCode() >> TEST_IO_CODE
            getDestinationId() >> TEST_DESTINATION_ID
            getChangeId() >> changeId
        }
    }

    private OutboundSyncCronJobModel notAbortedCronJob() {
        Stub(OutboundSyncCronJobModel) {
            getPk() >> CRON_JOB_PK
        }
    }

    private void cronJobByPkFound(OutboundSyncCronJobModel outboundSyncCronJobModel) {
        itemModelSearchService.nonCachingFindByPk(CRON_JOB_PK) >> Optional.of(outboundSyncCronJobModel)
    }

    private OutboundSyncJob outboundSyncJob() {
        Stub(OutboundSyncJob) {
            getCurrentState() >> initialOutboundSyncState().build()
        }
    }

    private OutboundSyncJob abortedOutboundSyncJob() {
        Stub(OutboundSyncJob) {
            getCurrentState() >> initialOutboundSyncState()
                    .withTotalItems(1)
                    .withUnprocessedCount(1)
                    .withAborted(true)
                    .build()
        }
    }

    private OutboundSyncJob sysErrorOutboundSyncJob() {
        Stub(OutboundSyncJob) {
            getCurrentState() >> initialOutboundSyncState()
                    .withSystemicError(true)
                    .build()
        }
    }

    private void setupRunningCronJob() {
        def jobModel = notAbortedCronJob()
        cronJobByPkFound jobModel
        jobRegister.getJob(jobModel) >> outboundSyncJob()
    }

    private void setupAbortedCronJob() {
        def cronJob = Stub(OutboundSyncCronJobModel) {
            getPk() >> CRON_JOB_PK
            getRequestAbort() >> true
            getStatus() >> CronJobStatus.RUNNING
        }
        cronJobByPkFound cronJob
        jobRegister.getJob(cronJob) >> outboundSyncJob()
    }

    private void setupFinalCronJob() {
        def cronJob = Stub(OutboundSyncCronJobModel) {
            getPk() >> CRON_JOB_PK
            getRequestAbort() >> false
            getStatus() >> CronJobStatus.ABORTED
        }
        cronJobByPkFound cronJob
        jobRegister.getJob(cronJob) >> abortedOutboundSyncJob()
        itemModelSearchService.nonCachingFindByPk(PK.fromLong(DEFAULT_ROOT_ITEM_PK)) >> Optional.of(Stub(ItemModel))
    }

    private void comparingSyncParametersList(final List<SyncParameters> expectedInvocations, final List<SyncParameters> actualInvocations) {
        assert actualInvocations.size() == expectedInvocations.size()
        final var expected = new ArrayList<>(expectedInvocations).sort { it -> it.item.pk }.sort { it -> it.eventType }
        final var actual = new ArrayList<>(actualInvocations).sort { it -> it.item.pk }.sort { it -> it.eventType }
        expected.eachWithIndex {
            SyncParameters expectedInvocation, int i -> (expectedInvocation == actual.get(i))
        }
    }

    private SyncParameters defaultSyncParams() {
        SyncParameters.syncParametersBuilder()
                .withPayloadObject(DEFAULT_CHANGED_ITEM)
                .withIntegrationObjectCode(TEST_IO_CODE)
                .withDestinationId(TEST_DESTINATION_ID)
                .withSource(OutboundSource.OUTBOUNDSYNC)
                .build()
    }

    private SyncParameters syncParametersFor(OutboundItemDTOGroup itemDTOGroup, ItemModel itemModel) {
        SyncParameters.syncParametersBuilder()
                .withPayloadObject(itemModel)
                .withIntegrationObjectCode(itemDTOGroup.getIntegrationObjectCode())
                .withDestinationId(itemDTOGroup.getDestinationId())
                .withSource(OutboundSource.OUTBOUNDSYNC)
                .build()
    }

    private successResponse(String changeId) {
        new ResponseEntity('successPayload', new HttpHeaders(['Content-ID': changeId]), HttpStatus.CREATED)
    }

    private errorResponse(String changeId) {
        new ResponseEntity('errorPayload', new HttpHeaders(['Content-ID': changeId]), HttpStatus.BAD_REQUEST)
    }
}
