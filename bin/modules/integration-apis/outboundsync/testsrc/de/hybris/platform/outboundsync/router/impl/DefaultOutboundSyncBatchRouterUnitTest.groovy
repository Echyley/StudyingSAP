/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.outboundsync.router.impl

import de.hybris.bootstrap.annotations.UnitTest
import de.hybris.platform.core.PK
import de.hybris.platform.outboundsync.activator.OutboundSyncService
import de.hybris.platform.outboundsync.dto.OutboundItemDTO
import de.hybris.platform.outboundsync.dto.OutboundItemDTOGroup
import de.hybris.platform.outboundsync.job.BatchChangeSender
import de.hybris.platform.outboundsync.job.OutboundItemFactory
import de.hybris.platform.outboundsync.router.OutboundSyncBatchRouter
import de.hybris.platform.testframework.JUnitPlatformSpecification
import org.junit.Test

@UnitTest
class DefaultOutboundSyncBatchRouterUnitTest extends JUnitPlatformSpecification {
    def batchChangeSender = Mock(BatchChangeSender)
    def syncService = Mock(OutboundSyncService)
    def factory = Stub(OutboundItemFactory)
    OutboundSyncBatchRouter router = new DefaultOutboundSyncBatchRouter(batchChangeSender, syncService, factory)

    @Test
    def "defaults to OutboundSyncService sync for an empty list of itemChanges"() {
        when:
        router.route([])

        then:
        1 * syncService.sync(_)
        0 * batchChangeSender.send(_)
    }

    @Test
    def "non batch requests are routed to the outboundSyncService sync method"() {
        given: "a list of non batch itemDTOs"
        def itemChanges = [nonBatchOutboundItemDTO()]

        when:
        router.route(itemChanges)

        then:
        1 * syncService.sync(itemChanges)
        0 * batchChangeSender.send(_)
    }

    @Test
    def "batch requests are routed to the batchChangeSender"() {
        given: "a list of batch itemDTOs"
        def itemChanges = [batchOutboundItemDTO(), batchOutboundItemDTO()]

        when:
        router.route(itemChanges)

        then:
        1 * batchChangeSender.send(it -> {
            it instanceof OutboundItemDTOGroup
            it.getChangeId() != null
        })
        0 * syncService.sync(_)
    }

    @Test
    def "delete batch requests are routed to the outboundSyncService"() {
        given: "a list of batch itemDTOs"
        def itemChanges = [deleteBatchOutboundItemDTO()]

        when:
        router.route(itemChanges)

        then:
        1 * syncService.sync(itemChanges)
        0 * batchChangeSender.send(_)
    }

    def deleteBatchOutboundItemDTO() {
        Stub(OutboundItemDTO) {
            isBatch() >> true
            isSynchronizeDelete() >> true
        }
    }

    def batchOutboundItemDTO() {
        Stub(OutboundItemDTO) {
            isBatch() >> true
            //pk is necessary for static conversion from itemDTOs to the itemDTOGroup
            getCronJobPK() >> PK.fromLong(123)
        }
    }

    def nonBatchOutboundItemDTO() {
        Stub(OutboundItemDTO) {
            isBatch() >> false
        }
    }
}
