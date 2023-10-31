/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.outboundsync.router.impl

import de.hybris.bootstrap.annotations.UnitTest
import de.hybris.platform.core.PK
import de.hybris.platform.outboundsync.activator.OutboundItemConsumer
import de.hybris.platform.outboundsync.dto.OutboundItemChange
import de.hybris.platform.outboundsync.dto.OutboundItemDTO
import de.hybris.platform.outboundsync.job.ItemChangeSender
import de.hybris.platform.outboundsync.job.ItemPKPopulator
import de.hybris.platform.outboundsync.job.RootItemChangeSender
import de.hybris.platform.servicelayer.event.EventService
import de.hybris.platform.testframework.JUnitPlatformSpecification
import org.junit.Test
import spock.lang.Specification
import spock.lang.Unroll

@UnitTest
class DefaultOutboundItemDTORouterUnitTest extends JUnitPlatformSpecification {
    private static final Long ROOT_ITEM_PK = 1L
    private static final Long NON_ROOT_ITEM_PK = 2L
    private static final Long DELETED_ITEM_PK = 3L
    private static final Long REFERENCE_TO_ROOT_PK = 4L
    private static final Long MULTIPLE_REFERENCE_TO_ROOT_PK = 5L
    private static final def CONTEXT_JOB_PK = PK.fromLong(6)

    def changeConsumer = Mock OutboundItemConsumer
    def itemSender = Mock RootItemChangeSender
    def deleteSender = Mock ItemChangeSender
    def populator = Stub ItemPKPopulator
    def eventService = Mock EventService
    def router = new DefaultOutboundItemDTORouter(
            rootItemChangeSender: itemSender,
            itemDeleteSender: deleteSender,
            changeConsumer: changeConsumer,
            populator: populator,
            eventService: eventService)

    @Test
    @Unroll
    def "deleted items should be routed to the item change consumer when the destination #condition"() {
        when:
        router.route dtoForRouting

        then:
        1 * changeConsumer.consume { it.item.PK == DELETED_ITEM_PK }
        1 * eventService.publishEvent { it.cronJobPk == CONTEXT_JOB_PK }
        0 * itemSender._
        0 * deleteSender._

        where:
        condition                                                                | dtoForRouting
        'has delete synchronization disabled and integration key is present'     | deleteDtoNoSync()
        'has delete synchronization enabled and integration key is not present'  | deleteDtoWithSyncNoIntegrationKey()
        'has delete synchronization disabled and integration key is not present' | deleteDtoNoSyncNoIntegrationKey()

    }

    @Test
    def "deleted items should be routed to the delete change sender when the destination enables delete synchronization and there is an integration key present"() {
        when:
        router.route deleteDtoWithSync()

        then:
        0 * changeConsumer.consume { it.item.PK == DELETED_ITEM_PK }
        0 * eventService.publishEvent { it.cronJobPk == CONTEXT_JOB_PK }
        0 * itemSender._
        1 * deleteSender._
    }

    @Test
    def "item that is not the root and does not have an attribute reference to the root is routed to change consumer"() {
        given:
        def nonRootItem = dtoWithPk(NON_ROOT_ITEM_PK)
        and:
        populator.populatePK(nonRootItem) >> [nonRootItem]

        when:
        router.route(nonRootItem)

        then:
        1 * changeConsumer.consume { it.item.PK == NON_ROOT_ITEM_PK }
        1 * eventService.publishEvent { it.cronJobPk == CONTEXT_JOB_PK }
        0 * itemSender._
    }

    @Test
    def "root item is routed to item sender"() {
        given:
        def rootItem = dtoWithReferenceToRoot(ROOT_ITEM_PK)
        and:
        populator.populatePK(rootItem) >> [rootItem]

        when:
        router.route(rootItem)

        then:
        1 * itemSender.sendPopulatedItem { it.rootItemPK == ROOT_ITEM_PK }
        0 * eventService._
        0 * changeConsumer._
    }

    @Test
    def "item that is not the root but has a reference to the root is routed to item sender"() {
        given:
        def nonRootItem = dtoWithReferenceToRoot(REFERENCE_TO_ROOT_PK)
        and:
        populator.populatePK(nonRootItem) >> [nonRootItem]

        when:
        router.route(nonRootItem)

        then:
        1 * itemSender.sendPopulatedItem { it.item.PK == REFERENCE_TO_ROOT_PK }
        0 * eventService._
        0 * changeConsumer._
    }

    @Test
    def "item that is not the root and has a multiple references to the root is routed to item sender"() {
        given:
        def nonRootItem = dtoWithReferenceToRoot(MULTIPLE_REFERENCE_TO_ROOT_PK)
        and:
        populator.populatePK(nonRootItem) >> [nonRootItem, nonRootItem, nonRootItem]

        when:
        router.route(nonRootItem)

        then:
        3 * itemSender.sendPopulatedItem { it.item.PK == MULTIPLE_REFERENCE_TO_ROOT_PK }
        0 * eventService._
        0 * changeConsumer._
    }

    private def deleteDtoWithSync() {
        deleteDto(true)
    }

    private def deleteDtoWithSyncNoIntegrationKey() {
        deleteDto(true, '')
    }

    private def deleteDtoNoSync() {
        deleteDto(false)
    }

    private def deleteDtoNoSyncNoIntegrationKey() {
        deleteDto(false, '')
    }

    private def deleteDto(boolean isSync, String integrationKey = 'asdf|fdsa') {
        Stub(OutboundItemDTO) {
            getItem() >> Stub(OutboundItemChange) {
                getPK() >> DELETED_ITEM_PK
            }
            getCronJobPK() >> CONTEXT_JOB_PK
            isSynchronizeDelete() >> isSync
            getIntegrationKey() >> integrationKey
            isDeleted() >> true
        }
    }

    private OutboundItemDTO dtoWithPk(Long pk) {
        Stub(OutboundItemDTO) {
            getItem() >> Stub(OutboundItemChange) {
                getPK() >> pk
            }
            getRootItemPK() >> null
            getCronJobPK() >> CONTEXT_JOB_PK
        }
    }

    private OutboundItemDTO dtoWithReferenceToRoot(Long pk) {
        Stub(OutboundItemDTO) {
            getItem() >> Stub(OutboundItemChange) {
                getPK() >> pk
            }
            getRootItemPK() >> ROOT_ITEM_PK
            getCronJobPK() >> CONTEXT_JOB_PK
        }
    }
}
