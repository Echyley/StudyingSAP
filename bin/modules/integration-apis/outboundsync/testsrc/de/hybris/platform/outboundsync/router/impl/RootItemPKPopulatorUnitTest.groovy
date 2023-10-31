/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.outboundsync.router.impl

import de.hybris.bootstrap.annotations.UnitTest
import de.hybris.platform.core.PK
import de.hybris.platform.core.model.ItemModel
import de.hybris.platform.integrationservices.model.IntegrationObjectDescriptor
import de.hybris.platform.integrationservices.model.TypeDescriptor
import de.hybris.platform.integrationservices.search.RootItemSearchResult
import de.hybris.platform.integrationservices.search.RootItemSearchService
import de.hybris.platform.outboundsync.dto.OutboundItem
import de.hybris.platform.outboundsync.dto.OutboundItemChange
import de.hybris.platform.outboundsync.dto.OutboundItemDTO
import de.hybris.platform.outboundsync.dto.OutboundItemDTOBuilder
import de.hybris.platform.outboundsync.job.OutboundItemFactory
import de.hybris.platform.testframework.JUnitPlatformSpecification
import org.junit.Test
import spock.lang.Shared

@UnitTest
class RootItemPKPopulatorUnitTest extends JUnitPlatformSpecification {
    private static final Long CHANGED_ITEM_PK = 1L
    private static final Long ROOT_ITEM_PK = 3L
    private static final Long ROOT_ITEM_PK2 = 5L

    @Shared
    def CHANGED_ITEM = itemModel(CHANGED_ITEM_PK)

    // If you're getting the 'Update to non-static final' error when running this test in Intellij
    // remove the 'final' keyword for the time being. The test runs fine in the terminal, just not in Intellij.
    // See this issue https://github.com/spockframework/spock/issues/1011.
    private OutboundItemDTO DTO = OutboundItemDTOBuilder.outboundItemDTO()
            .withItem(Stub(OutboundItemChange) { getPK() >> CHANGED_ITEM_PK })
            .withIntegrationObjectPK(100)
            .withChannelConfigurationPK(200)
            .withCronJobPK(PK.fromLong(300))
            .build()

    def rootItemSearchService = Stub(RootItemSearchService)
    RootItemPKPopulator populator = new RootItemPKPopulator(rootItemSearchService: rootItemSearchService)
    TypeDescriptor rootItem = Stub(TypeDescriptor)
    OutboundItem outboundItem

    def setup() {
        outboundItem = Stub(OutboundItem) {
            getChangedItemModel() >> Optional.of(CHANGED_ITEM)
            getIntegrationObject() >> Stub(IntegrationObjectDescriptor) {
                getRootType() >> { Optional.ofNullable(rootItem) }
            }
        }
        populator.itemFactory = Stub(OutboundItemFactory) {
            createItem(DTO) >> outboundItem
        }
    }

    @Test
    def "item that is not the root and has a reference to the root"() {
        given: "type descriptor is present"
        def typeDescriptor = Stub(TypeDescriptor)
        outboundItem.getTypeDescriptor() >> Optional.of(typeDescriptor)

        and: "a root item is found"
        rootItemSearchService.findRoots(CHANGED_ITEM, typeDescriptor) >> RootItemSearchResult.createFrom([itemModel(ROOT_ITEM_PK)])

        when:
        def dtos = populator.populatePK(DTO)

        then:
        dtos.size() == 1
        dtos[0].rootItemPK == ROOT_ITEM_PK
    }

    @Test
    def "item that is not the root and has references to multiple roots via collection"() {
        given: "type descriptor is present"
        def typeDescriptor = Stub(TypeDescriptor)
        outboundItem.getTypeDescriptor() >> Optional.of(typeDescriptor)

        and: "2 root items are found"
        rootItemSearchService.findRoots(CHANGED_ITEM, typeDescriptor) >> RootItemSearchResult.createFrom([itemModel(ROOT_ITEM_PK),
                                                                                                   itemModel(ROOT_ITEM_PK2)])

        when:
        def dtos = populator.populatePK(DTO)

        then:
        dtos.size() == 2
        dtos.any { it.rootItemPK == ROOT_ITEM_PK }
        dtos.any { it.rootItemPK == ROOT_ITEM_PK2 }
    }

    @Test
    def "does not populate root PK for item that doesn't exist anymore"() {
        given:
        outboundItem.getChangedItemModel() >> Optional.empty()

        when:
        def dtos = populator.populatePK(DTO)

        then:
        dtos.size() == 1
        dtos[0].rootItemPK == null
    }

    @Test
    def "does not populate root PK when item that does not have a reference to the root"() {
        given: "type descriptor is present"
        def typeDescriptor = Stub(TypeDescriptor)
        outboundItem.getTypeDescriptor() >> Optional.of(typeDescriptor)

        and: "no root items are found"
        rootItemSearchService.findRoots(CHANGED_ITEM, typeDescriptor) >> RootItemSearchResult.EMPTY_RESULT

        when:
        def dtos = populator.populatePK(DTO)

        then:
        dtos.size() == 1
        dtos[0].rootItemPK == null
    }

    @Test
    def "root item PK should be own item PK when IO does not have root item type"() {
        given:
        rootItem = null

        when:
        def dtos = populator.populatePK(DTO)

        then:
        dtos.size() == 1
        dtos[0].rootItemPK == CHANGED_ITEM_PK
    }

    @Test
    def "root item PK should be own item PK when root item is null"() {
        given: "type descriptor is present"
        def typeDescriptor = Stub(TypeDescriptor)
        outboundItem.getTypeDescriptor() >> Optional.of(typeDescriptor)

        and: "a null root item is found"
        rootItemSearchService.findRoots(CHANGED_ITEM, typeDescriptor) >> RootItemSearchResult.createFrom([null])

        when:
        def dtos = populator.populatePK(DTO)

        then:
        dtos.size() == 1
        dtos[0].rootItemPK == CHANGED_ITEM_PK
    }

    @Test
    def "does not populate root PK when item is not in the IO definition"() {
        given:
        outboundItem.getTypeDescriptor() >> Optional.empty()

        when:
        def dtos = populator.populatePK(DTO)

        then:
        dtos.size() == 1
        dtos[0].rootItemPK == null
    }

    private ItemModel itemModel(Long pk) {
        Stub(ItemModel) {
            getPk() >> PK.fromLong(pk)
        }
    }
}
