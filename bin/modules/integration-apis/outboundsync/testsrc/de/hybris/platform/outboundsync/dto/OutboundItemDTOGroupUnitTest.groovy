/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.outboundsync.dto

import de.hybris.bootstrap.annotations.UnitTest
import de.hybris.platform.apiregistryservices.model.ConsumedDestinationModel
import de.hybris.platform.core.PK
import de.hybris.platform.integrationservices.model.IntegrationObjectDescriptor
import de.hybris.platform.outboundsync.job.OutboundItemFactory
import de.hybris.platform.outboundsync.model.OutboundChannelConfigurationModel
import de.hybris.platform.testframework.JUnitPlatformSpecification
import org.junit.Test

@UnitTest
class OutboundItemDTOGroupUnitTest extends JUnitPlatformSpecification {

    private static final Long ROOT_ITEM_PK = 123
    private static final PK CRON_JOB_PK = PK.fromLong(456)
    private static final String INTEGRATION_OBJ_CODE = "testIntegrationObject"
    private static final String DESTINATION_ID = "testDestination"
    private static final String DEFAULT_CHANGE_ID = '1199'

    @Test
    def "create an OutboundItemDTOGroup"() {
        given:
        def itemDto = outboundItemDto()

        when:
        def dtoGroup = OutboundItemDTOGroup.from([itemDto], itemFactory())

        then:
        with(dtoGroup) {
            rootItemPk == ROOT_ITEM_PK
            cronJobPk == CRON_JOB_PK
            outboundItemDTOs.containsAll([itemDto])
            integrationObjectCode == INTEGRATION_OBJ_CODE
            destinationId == DESTINATION_ID
            channelConfiguration.destination.id == DESTINATION_ID
        }
    }

    @Test
    def "create an OutboundItemDTOGroup with changeId"() {
        given:
        def itemDto = outboundItemDto()

        when:
        def dtoGroup = OutboundItemDTOGroup.from([itemDto], itemFactory(), DEFAULT_CHANGE_ID)

        then:
        with(dtoGroup) {
            rootItemPk == ROOT_ITEM_PK
            cronJobPk == CRON_JOB_PK
            outboundItemDTOs.containsAll([itemDto])
            integrationObjectCode == INTEGRATION_OBJ_CODE
            destinationId == DESTINATION_ID
            channelConfiguration.destination.id == DESTINATION_ID
            changeId == DEFAULT_CHANGE_ID
        }
    }

    @Test
    def "create OutboundItemDTOGroup throws exception when #condition"() {
        when:
        OutboundItemDTOGroup.from(outboundItemDtos, factory)

        then:
        thrown(IllegalArgumentException)

        where:
        condition                 | outboundItemDtos    | factory
        "DTO collection is null"  | null                | itemFactory()
        "DTO collection is empty" | []                  | itemFactory()
        "ModelService is null"    | [outboundItemDto()] | null
    }

    @Test
    def "two OutboundItemDTOGroups are equal"() {
        given:
        def outboundItemDtos1 = [outboundItemDto()]
        def group1 = OutboundItemDTOGroup.from(outboundItemDtos1, itemFactory())

        and:
        def outboundItemDtos2 = [outboundItemDto()]
        def group2 = OutboundItemDTOGroup.from(outboundItemDtos2, itemFactory())

        expect:
        group1 == group2
    }

    @Test
    def "two OutboundItemDTOGroups are not equal when the outboundItemDTOs are not the same"() {
        given:
        def outboundItemDtos1 = [outboundItemDtoWithRootItemPk(123)]
        def group1 = OutboundItemDTOGroup.from(outboundItemDtos1, itemFactory())

        and:
        def outboundItemDtos2 = [outboundItemDtoWithRootItemPk(456)]
        def group2 = OutboundItemDTOGroup.from(outboundItemDtos2, itemFactory())

        expect:
        group1 != group2
    }

    @Test
    def "two OutboundItemDTOGroups have the same hashcode"() {
        given:
        def outboundItemDtos1 = [outboundItemDto()]
        def group1 = OutboundItemDTOGroup.from(outboundItemDtos1, itemFactory())

        and:
        def outboundItemDtos2 = [outboundItemDto()]
        def group2 = OutboundItemDTOGroup.from(outboundItemDtos2, itemFactory())

        expect:
        group1.hashCode() == group2.hashCode()
    }

    @Test
    def "two OutboundItemDTOGroups have different hashcode when the itemDTOs are not the same"() {
        given:
        def outboundItemDtos1 = [outboundItemDtoWithRootItemPk(123)]
        def group1 = OutboundItemDTOGroup.from(outboundItemDtos1, itemFactory())

        and:
        def outboundItemDtos2 = [outboundItemDtoWithRootItemPk(456)]
        def group2 = OutboundItemDTOGroup.from(outboundItemDtos2, itemFactory())

        expect:
        group1.hashCode() != group2.hashCode()
    }

    @Test
    def 'item count is the number of item DTOs in this group'() {
        given:
        def itemDTOs = [outboundItemDto(), outboundItemDtoWithRootItemPk(1)]
        def group = OutboundItemDTOGroup.from itemDTOs, itemFactory()

        expect:
        group.itemsCount == itemDTOs.size()
    }

    @Test
    def 'references to item DTOs are not leaked through the getter'() {
        given:
        def itemDTOsRef = [outboundItemDto()]
        def group = OutboundItemDTOGroup.from itemDTOsRef, itemFactory()

        when:
        group.outboundItemDTOs.clear()

        then:
        thrown UnsupportedOperationException
    }

    @Test
    def 'references to item DTOs are not leaked through the factory method'() {
        given:
        def itemDTOsRef = [outboundItemDto()]
        def group = OutboundItemDTOGroup.from itemDTOsRef, itemFactory()

        when:
        itemDTOsRef.clear()

        then:
        !group.outboundItemDTOs.empty
    }

    def outboundItemDto() {
        outboundItemDtoWithRootItemPk(ROOT_ITEM_PK)
    }

    def outboundItemDtoWithRootItemPk(Long rootItemPk) {
        def itemChange = Stub(OutboundItemChange) {
            equals(_) >> true
            hashCode() >> 123
        }
        OutboundItemDTOBuilder.outboundItemDTO()
                .withItem(itemChange)
                .withIntegrationObjectPK(123)
                .withChannelConfigurationPK(456)
                .withRootItemPK(rootItemPk)
                .withCronJobPK(CRON_JOB_PK)
                .build()
    }

    IntegrationObjectDescriptor integrationObject() {
        Stub(IntegrationObjectDescriptor) {
            getCode() >> INTEGRATION_OBJ_CODE
        }
    }

    OutboundChannelConfigurationModel channelConfiguration() {
        Stub(OutboundChannelConfigurationModel) {
            getDestination() >> Stub(ConsumedDestinationModel) {
                getId() >> DESTINATION_ID
            }
        }
    }

    OutboundItemFactory itemFactory() {
        itemFactory(integrationObject(), channelConfiguration())
    }

    OutboundItemFactory itemFactory(IntegrationObjectDescriptor object, OutboundChannelConfigurationModel channelCfg) {
        Stub(OutboundItemFactory) {
            createItem(_ as OutboundItemDTO) >> Stub(OutboundItem) {
                getIntegrationObject() >> object
                getChannelConfiguration() >> channelCfg
            }
        }
    }
}
