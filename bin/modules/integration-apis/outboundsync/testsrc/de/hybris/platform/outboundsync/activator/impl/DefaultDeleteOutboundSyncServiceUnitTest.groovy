/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.outboundsync.activator.impl

import de.hybris.bootstrap.annotations.UnitTest
import de.hybris.platform.apiregistryservices.model.ConsumedDestinationModel
import de.hybris.platform.core.PK
import de.hybris.platform.integrationservices.model.IntegrationObjectModel
import de.hybris.platform.integrationservices.service.ItemModelSearchService
import de.hybris.platform.outboundservices.enums.OutboundSource
import de.hybris.platform.outboundservices.event.impl.DefaultEventType
import de.hybris.platform.outboundservices.facade.SyncParameters
import de.hybris.platform.outboundservices.service.DeleteRequestSender
import de.hybris.platform.outboundsync.activator.OutboundItemConsumer
import de.hybris.platform.outboundsync.dto.ChangeInfo
import de.hybris.platform.outboundsync.dto.OutboundChangeType
import de.hybris.platform.outboundsync.dto.OutboundItem
import de.hybris.platform.outboundsync.dto.OutboundItemChange
import de.hybris.platform.outboundsync.dto.OutboundItemDTO
import de.hybris.platform.outboundsync.dto.OutboundItemDTOBuilder
import de.hybris.platform.outboundsync.events.CompletedOutboundSyncEvent
import de.hybris.platform.outboundsync.job.OutboundItemFactory
import de.hybris.platform.outboundsync.job.impl.OutboundSyncJob
import de.hybris.platform.outboundsync.job.impl.OutboundSyncJobRegister
import de.hybris.platform.outboundsync.model.OutboundChannelConfigurationModel
import de.hybris.platform.outboundsync.model.OutboundSyncCronJobModel
import de.hybris.platform.servicelayer.event.EventService
import de.hybris.platform.testframework.JUnitPlatformSpecification
import org.junit.Test
import spock.lang.Issue
import spock.lang.Shared

import static de.hybris.platform.outboundsync.job.impl.OutboundSyncStateBuilder.initialOutboundSyncState

@Issue('https://cxjira.sap.com/browse/IAPI-3466')
@UnitTest
class DefaultDeleteOutboundSyncServiceUnitTest extends JUnitPlatformSpecification {

    private static final def DEL_INTEGRATION_KEY = 'deleted|item|key'
    private static final def ITEM_TYPE = 'Product'
    private static final def OUTBOUND_CHANNEL_CONFIG_PK = PK.fromLong 1L
    private static final def CRONJOB_PK = PK.fromLong 3L

    @Shared
    def outboundChannelConfig = Stub(OutboundChannelConfigurationModel) {
        getPk() >> OUTBOUND_CHANNEL_CONFIG_PK
    }

    def deleteRequestSender = Mock DeleteRequestSender
    def outboundItemConsumer = Mock OutboundItemConsumer
    def eventService = Mock EventService
    def itemModelSearchService = Stub ItemModelSearchService
    def jobRegister = Stub OutboundSyncJobRegister
    def itemFactory = Stub(OutboundItemFactory) {
        createItem({ it.type == ITEM_TYPE }) >> Stub(OutboundItem)
    }

    def deleteSyncService = new DefaultDeleteOutboundSyncService(
            itemModelSearchService, itemFactory, jobRegister, deleteRequestSender)

    def setup() {
        deleteSyncService.eventService = eventService
        deleteSyncService.outboundItemConsumer = outboundItemConsumer
    }

    @Test
    def "delete service cannot instantiate when #param is null"() {
        when:
        new DefaultDeleteOutboundSyncService(searchSvc, factory, register, deleteReqSender)

        then:
        def e = thrown IllegalArgumentException
        e.message.contains "$param cannot be null"

        where:
        param                     | deleteReqSender           | searchSvc                    | factory                   | register
        'DeleteRequestSender'     | null                      | Stub(ItemModelSearchService) | Stub(OutboundItemFactory) | Stub(OutboundSyncJobRegister)
        'ItemModelSearchService'  | Stub(DeleteRequestSender) | null                         | Stub(OutboundItemFactory) | Stub(OutboundSyncJobRegister)
        'OutboundItemFactory'     | Stub(DeleteRequestSender) | Stub(ItemModelSearchService) | null                      | Stub(OutboundSyncJobRegister)
        'OutboundSyncJobRegister' | Stub(DeleteRequestSender) | Stub(ItemModelSearchService) | Stub(OutboundItemFactory) | null
    }

    @Test
    def 'delete item is not sent and is consumed when cronjob model is not found'() {
        given:
        def item = item()

        and: 'cron job is not found'
        itemModelSearchService.nonCachingFindByPk(CRONJOB_PK) >> Optional.empty()

        when:
        deleteSyncService.sync item

        then:
        0 * deleteRequestSender.send(_ as SyncParameters)
        1 * outboundItemConsumer.consume(item)
    }

    @Test
    def 'delete item is consumed after delete request is sent'() {
        given:
        def deletedItem = item()

        and:
        def jobModel = foundCronJob()
        jobRegister.getJob(jobModel) >> outboundSyncJob()

        and:
        foundOutboundChannelConfig()

        when:
        deleteSyncService.sync deletedItem

        then:
        1 * deleteRequestSender.send(_ as SyncParameters)
        1 * outboundItemConsumer.consume(deletedItem)
    }

    @Test
    def 'delete request is sent with fully populated sync parameters'() {
        given: 'deleted item'
        def deletedItem = item()

        and:
        def jobModel = foundCronJob()
        jobRegister.getJob(jobModel) >> outboundSyncJob()

        and: 'found outbound channel configuration'
        def integrationObjCode = "ioCode"
        def integrationObj = Stub(IntegrationObjectModel) {
            getCode() >> integrationObjCode
        }
        def destId = "destinationId"
        def dest = Stub(ConsumedDestinationModel) {
            getId() >> destId
        }
        def outboundChannelConfig = Stub(OutboundChannelConfigurationModel) {
            getPk() >> OUTBOUND_CHANNEL_CONFIG_PK
            getIntegrationObject() >> integrationObj
            getDestination() >> dest
            getItemtype() >> ITEM_TYPE
        }
        foundOutboundChannelConfig(outboundChannelConfig)

        when:
        deleteSyncService.sync deletedItem

        then:
        1 * deleteRequestSender.send(_ as SyncParameters) >> { List args ->
            with(args[0] as SyncParameters) {
                item == null
                source == OutboundSource.OUTBOUNDSYNC
                eventType == DefaultEventType.UNKNOWN
                integrationKey == DEL_INTEGRATION_KEY
                integrationObjectCode == integrationObjCode
                destination == dest
                destinationId == destId
                integrationObject == integrationObj
            }
        }
    }

    @Test
    def 'delete item is successfully sent when optional dependencies are not injected'() {
        given:
        deleteSyncService.outboundItemConsumer = null
        deleteSyncService.eventService = null

        and:
        def jobModel = foundCronJob()
        jobRegister.getJob(jobModel) >> outboundSyncJob()

        and:
        foundOutboundChannelConfig()

        when:
        deleteSyncService.sync item()

        then:
        1 * deleteRequestSender.send(_ as SyncParameters)
    }

    @Test
    def 'delete item is not sent and consumed when outbound channel config is not found'() {
        given: 'deleted item'
        def deletedItem = item()

        and:
        def jobModel = foundCronJob()
        jobRegister.getJob(jobModel) >> outboundSyncJob()

        and: 'outbound channel config is not found'
        itemModelSearchService.nonCachingFindByPk(OUTBOUND_CHANNEL_CONFIG_PK) >> Optional.empty()

        when:
        deleteSyncService.sync deletedItem

        then:
        0 * deleteRequestSender._
        1 * outboundItemConsumer.consume(deletedItem)
    }

    @Test
    def 'successful completed event is published when delete request is sent successfully'() {
        given:
        def deletedItem = item()

        and:
        def jobModel = foundCronJob()
        jobRegister.getJob(jobModel) >> outboundSyncJob()
        foundOutboundChannelConfig()

        when:
        deleteSyncService.sync deletedItem

        then:
        1 * deleteRequestSender.send(_ as SyncParameters)
        1 * eventService.publishEvent(_ as CompletedOutboundSyncEvent) >> { List args ->
            with(args[0] as CompletedOutboundSyncEvent) {
                success
                changesCompleted == 1
                cronJobPk == deletedItem.cronJobPK
            }
        }
    }

    @Test
    def 'unsuccessful completed event is published when delete request encounters exception'() {
        given:
        def deletedItem = item()

        and:
        def jobModel = foundCronJob()
        jobRegister.getJob(jobModel) >> outboundSyncJob()
        foundOutboundChannelConfig()

        and: 'delete request sender throws exception'
        deleteRequestSender.send(_ as SyncParameters) >> { throw new RuntimeException("TEST IGNORE - delete request send failed") }

        when:
        deleteSyncService.sync deletedItem

        then:
        1 * eventService.publishEvent(_ as CompletedOutboundSyncEvent) >> { List args ->
            with(args[0] as CompletedOutboundSyncEvent) {
                !success
                changesCompleted == 1
                cronJobPk == deletedItem.cronJobPK
            }
        }
    }

    @Test
    def 'unsuccessful completed event is published when the job register crashes'() {
        given:
        def deletedItem = item()

        and:
        def jobModel = foundCronJob()
        jobRegister.getJob(jobModel) >> { throw new RuntimeException() }

        when:
        deleteSyncService.sync deletedItem

        then:
        1 * eventService.publishEvent(_ as CompletedOutboundSyncEvent) >> { List args ->
            with(args[0] as CompletedOutboundSyncEvent) {
                !success
                changesCompleted == 1
                cronJobPk == deletedItem.cronJobPK
            }
        }

    }

    private OutboundItemDTO item(String deleteItemIntegrationKey = DEL_INTEGRATION_KEY,
                                 PK outboundChannelConfigPk = OUTBOUND_CHANNEL_CONFIG_PK,
                                 PK cronjobPk = CRONJOB_PK,
                                 String itemType = ITEM_TYPE,
                                 String rootItemType = ITEM_TYPE) {
        OutboundItemDTOBuilder.outboundItemDTO()
                .withChannelConfigurationPK(outboundChannelConfigPk.getLong())
                .withCronJobPK(cronjobPk)
                .withSynchronizeDelete(true)
                .withItem(itemChange())
                .withInfo(new ChangeInfo(deleteItemIntegrationKey, itemType, rootItemType))
                .build()
    }

    private OutboundSyncCronJobModel foundCronJob(def cronJobPk = CRONJOB_PK) {
        def outboundSyncCronJob = Stub(OutboundSyncCronJobModel) {
            getPk() >> cronJobPk
        }
        itemModelSearchService.nonCachingFindByPk(CRONJOB_PK) >> Optional.of(outboundSyncCronJob)
        outboundSyncCronJob
    }

    private void foundOutboundChannelConfig(def config = outboundChannelConfig) {
        itemModelSearchService.nonCachingFindByPk(config.getPk()) >> Optional.of(config)
    }

    private OutboundItemChange itemChange() {
        Stub(OutboundItemChange) {
            getChangeType() >> OutboundChangeType.DELETED
        }
    }

    private OutboundSyncJob outboundSyncJob() {
        Stub(OutboundSyncJob) {
            getCurrentState() >> initialOutboundSyncState().build()
        }
    }
}
