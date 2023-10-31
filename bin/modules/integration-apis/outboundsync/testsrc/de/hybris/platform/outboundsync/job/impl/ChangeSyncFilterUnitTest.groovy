/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.outboundsync.job.impl

import de.hybris.bootstrap.annotations.UnitTest
import de.hybris.platform.core.model.type.ComposedTypeModel
import de.hybris.platform.integrationservices.model.IntegrationObjectItemModel
import de.hybris.platform.integrationservices.model.IntegrationObjectModel
import de.hybris.platform.outboundsync.dto.OutboundItemDTO
import de.hybris.platform.testframework.JUnitPlatformSpecification
import org.junit.Test
import spock.lang.Specification
import spock.lang.Unroll

@UnitTest
class ChangeSyncFilterUnitTest extends JUnitPlatformSpecification {
    private static final def IO_ROOT_ITEM_TYPE = 'RootItemType'
    private static final def IO_ROOT_ITEM_SUB_TYPE = 'RootChildItemType'
    private static final def ITEM_TYPE = 'ItemType'
    private static final def ITEM_SUB_TYPE = 'ChildItemType'

    def rootIOI = integrationObjectItem(IO_ROOT_ITEM_TYPE, [composedType(IO_ROOT_ITEM_SUB_TYPE)])
    def nonRootIOI = integrationObjectItem(ITEM_TYPE, [composedType(ITEM_SUB_TYPE)])

    def integrationObject = Stub(IntegrationObjectModel) {
        getItems() >> [rootIOI, nonRootIOI]
        getRootItem() >> rootIOI
    }
    def changeSyncFilter = new ChangeSyncFilter()

    @Test
    @Unroll
    def "isApplicable is #expected when OutboundItemDTO is #msg"() {
        expect:
        changeSyncFilter.isApplicable(dto) == expected

        where:
        msg          | expected | dto
        "delete"     | false    | deleteOutboundItemDTO()
        "null"       | false    | null
        "non-delete" | true     | changeOutboundItemDTO()
    }

    @Test
    @Unroll
    def "evaluate returns Optional of dto when #condition"() {
        expect:
        changeSyncFilter.evaluate(outboundItemDTO, integrationObject).equals(Optional.of(outboundItemDTO))

        where:
        outboundItemDTO                        | condition
        itemDTOWithType(IO_ROOT_ITEM_TYPE)     | "dto itemType is the same as the IntegrationObject's root type"
        itemDTOWithType(IO_ROOT_ITEM_SUB_TYPE) | "dto itemType is a sub-type of the IntegrationObject's root type"
        itemDTOWithType(ITEM_TYPE)             | "dto itemType is the same type as a non-root item defined by the IntegrationObject"
        itemDTOWithType(ITEM_SUB_TYPE)         | "dto itemType is a sub-type of a non-root item defined by the IntegrationObject"
        itemDTOWithType(null)                  | "dto itemType is null"
        itemDTOWithType("")                    | "dto itemType is empty"
    }

    @Test
    def "evaluate returns Optional.empty when dto itemType is not the same or a subtype of any item within the IntegrationObject"() {
        expect:
        changeSyncFilter.evaluate(itemDTOWithType("RandomTypeNotInIO"), integrationObject).isEmpty()
    }

    @Test
    @Unroll
    def "evaluate returns Optional.empty when the #msg is null"() {
        expect:
        changeSyncFilter.evaluate(dto, io).isEmpty()

        where:
        msg                 | dto                        | io
        "IntegrationObject" | itemDTOWithType(ITEM_TYPE) | null
        "OutboundItemDTO"   | null                       | Stub(IntegrationObjectModel)
    }

    private OutboundItemDTO changeOutboundItemDTO() {
        Stub(OutboundItemDTO) {
            isDeleted() >> false
        }
    }

    private OutboundItemDTO deleteOutboundItemDTO() {
        Stub(OutboundItemDTO) {
            isDeleted() >> true
        }
    }

    private OutboundItemDTO itemDTOWithType(String type) {
        Stub(OutboundItemDTO) {
            getItemType() >> type
        }
    }

    IntegrationObjectItemModel integrationObjectItem(String type, Collection<ComposedTypeModel> subTypes = []) {
        Stub(IntegrationObjectItemModel) {
            getType() >> composedType(type, subTypes)
        }
    }

    ComposedTypeModel composedType(String typeCode, Collection<ComposedTypeModel> subTypes = []) {
        Stub(ComposedTypeModel) {
            getCode() >> typeCode
            getAllSubTypes() >> subTypes
        }
    }
}
