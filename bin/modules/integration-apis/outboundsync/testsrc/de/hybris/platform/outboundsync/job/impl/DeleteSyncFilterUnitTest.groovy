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
class DeleteSyncFilterUnitTest extends JUnitPlatformSpecification {

    private static final def IO_ROOT_ITEM_TYPE = 'RootItemType'
    private static final def IO_ROOT_ITEM_SUB_TYPE = 'RootChildItemType'
    private static final def TYPE_NOT_IN_IO = 'typeNotInIO'

    @Test
    @Unroll
    def "isApplicable is #expected when OutboundItemDTO is #msg"() {
        given:
        def deleteSyncFilter = new DeleteSyncFilter()

        expect:
        deleteSyncFilter.isApplicable(dto) == expected

        where:
        msg          | expected | dto
        "null"       | false    | null
        "non-delete" | false    | changeOutboundItemDTO()
        "delete"     | true     | deleteOutboundItemDTO()
    }

    @Test
    @Unroll
    def "delete dto is returned when #msg"() {
        given:
        def integrationObject = integrationObject(IO_ROOT_ITEM_TYPE, IO_ROOT_ITEM_SUB_TYPE)
        def deleteSyncFilter = new DeleteSyncFilter()
        def outboundItemDTO = itemDTO(["type": type, "rootType": rootType])
        def deleteSyncFilterSpy = getSpy(deleteSyncFilter)

        when:
        def optionalValue = deleteSyncFilterSpy.evaluate(outboundItemDTO, integrationObject)

        then:
        optionalValue.equals(Optional.of(outboundItemDTO))

        where:
        msg                                                                    | type                  | rootType
        "dto type is IO's root item type, root type is not not part of IO"     | IO_ROOT_ITEM_TYPE     | TYPE_NOT_IN_IO
        "dto type and root type are IO's root item type"                       | IO_ROOT_ITEM_TYPE     | IO_ROOT_ITEM_TYPE
        "dto type is IO's root item type, root type is IO's root item subtype" | IO_ROOT_ITEM_TYPE     | IO_ROOT_ITEM_SUB_TYPE
        "dto type is IO's root item subtype, root type is not part of IO"      | IO_ROOT_ITEM_SUB_TYPE | TYPE_NOT_IN_IO
        "dto type is IO's root item subtype, root type is IO's root item type" | IO_ROOT_ITEM_SUB_TYPE | IO_ROOT_ITEM_TYPE
        "dto type and root type are IO's root item subtype"                     | IO_ROOT_ITEM_SUB_TYPE | IO_ROOT_ITEM_SUB_TYPE
    }

    @Test
    @Unroll
    def "delete dto is filtered out when #msg"() {
        given:
        def integrationObject = integrationObject(IO_ROOT_ITEM_TYPE, IO_ROOT_ITEM_SUB_TYPE)
        def deleteSyncFilter = new DeleteSyncFilter()
        def outboundItemDTO = itemDTO(["type": type, "rootType": rootType])
        def deleteSyncFilterSpy = getSpy(deleteSyncFilter)

        when:
        def optionalValue = deleteSyncFilterSpy.evaluate(outboundItemDTO, integrationObject)

        then:
        optionalValue.isEmpty()

        where:
        msg                                                                | type              | rootType
        "dto type is null, dto root type is IO's root item type"           | null              | IO_ROOT_ITEM_TYPE
        "dto type is empty, dto root type is IO's root item type"          | ""                | IO_ROOT_ITEM_TYPE
        "dto type is IO's root item type, root type is empty"              | IO_ROOT_ITEM_TYPE | ""
        "dto type is IO's root item type, root type is null"               | IO_ROOT_ITEM_TYPE | null
        "dto type is not part of IO, dto root type is IO's root item type" | TYPE_NOT_IN_IO    | IO_ROOT_ITEM_TYPE
    }

    @Test
    @Unroll
    def "delete dto is filtered out when #msg is null"() {
        given:
        def integrationObject = integrationObject(IO_ROOT_ITEM_TYPE, IO_ROOT_ITEM_SUB_TYPE)
        def deleteSyncFilter = new DeleteSyncFilter()
        def outboundItemDTO = itemDTO(["type": type, "rootType": rootType])
        def deleteSyncFilterSpy = getSpy(deleteSyncFilter)

        when:
        def optionalValue = deleteSyncFilterSpy.evaluate(outboundItemDTO, integrationObject)

        then:
        optionalValue.isEmpty()

        where:
        msg                                                                | type              | rootType
        "dto type is null, dto root type is IO's root item type"           | null              | IO_ROOT_ITEM_TYPE
        "dto type is empty, dto root type is IO's root item type"          | ""                | IO_ROOT_ITEM_TYPE
        "dto type is IO's root item type, root type is empty"              | IO_ROOT_ITEM_TYPE | ""
        "dto type is IO's root item type, root type is null"               | IO_ROOT_ITEM_TYPE | null
        "dto type is not part of IO, dto root type is IO's root item type" | TYPE_NOT_IN_IO    | IO_ROOT_ITEM_TYPE
    }

    @Test
    @Unroll
    def "evaluate returns Optional.empty when the #msg is null"() {
        given:
        def deleteSyncFilter = new DeleteSyncFilter()

        expect:
        deleteSyncFilter.evaluate(dto, io).isEmpty()

        where:
        msg                 | dto                                                                 | io
        "IntegrationObject" | itemDTO(["type": IO_ROOT_ITEM_TYPE, "rootType": IO_ROOT_ITEM_TYPE]) | null
        "OutboundItemDTO"   | null                                                                | Stub(IntegrationObjectModel)
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

    private OutboundItemDTO itemDTO(Map typeMap = [:]) {
        Stub(OutboundItemDTO) {
            getItemType() >> typeMap["type"]
            getRootItemType() >> typeMap["rootType"]
        }
    }

    private IntegrationObjectModel integrationObject(String rootItemType = "", String rootItemSubtype = "") {
        Stub(IntegrationObjectModel) {
            def ioItem = integrationObjectItem(rootItemType, [composedType(rootItemSubtype)])
            getItems() >> [ioItem]
            getRootItem() >> ioItem
        }
    }

    private IntegrationObjectItemModel integrationObjectItem(String type, Collection<ComposedTypeModel> subTypes = []) {
        Stub(IntegrationObjectItemModel) {
            getType() >> composedType(type, subTypes)
        }
    }

    private ComposedTypeModel composedType(String typeCode, Collection<ComposedTypeModel> subTypes = []) {
        Stub(ComposedTypeModel) {
            getCode() >> typeCode
            getAllSubTypes() >> subTypes
        }
    }

    private DeleteSyncFilter getSpy(DeleteSyncFilter deleteSyncFilter) {
        def deleteSyncFilterSpy = Spy(deleteSyncFilter)
        deleteSyncFilterSpy.isDeleteFeatureEnabled() >> true

        deleteSyncFilterSpy
    }
}
