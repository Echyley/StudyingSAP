/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.outboundsync.dto

import de.hybris.bootstrap.annotations.UnitTest
import de.hybris.platform.core.PK
import de.hybris.platform.testframework.JUnitPlatformSpecification
import org.junit.Test
import spock.lang.Shared

@UnitTest
class OutboundItemDTOBuilderUnitTest extends JUnitPlatformSpecification {

    private static final def CHANNEL_CONFIG_PK = 2L
    private static final def INTEGRATION_OBJ_PK = 1L
    private static final def ROOT_ITEM_PK = 3L
    private static final def ROOT_ITEM_TYPE = 'Parent'
    private static final def CRONJOB_PK = PK.fromLong 4L
    private static final def INTEGRATION_KEY = 'catalog|code'
    private static final def ITEM_TYPE = 'Child'
    private static final def SYNCHRONIZE_DELETE = true
    private static final def BATCH = true

    @Shared
    def OUTBOUND_ITEM = Stub(OutboundItemChange)

    @Test
    def "create outbound item DTO with null #param throws exception"() {
        when:
        OutboundItemDTOBuilder.outboundItemDTO()
                .withItem(item)
                .withChannelConfigurationPK(channelConfig)
                .withCronJobPK(cronjobPk)
                .build()

        then:
        def e = thrown IllegalArgumentException
        e.message == "$param cannot be null"

        where:
        param                    | item          | channelConfig     | cronjobPk
        'item'                   | null          | CHANNEL_CONFIG_PK | CRONJOB_PK
        'channelConfigurationPk' | OUTBOUND_ITEM | null              | CRONJOB_PK
        'cronJobPk'              | OUTBOUND_ITEM | CHANNEL_CONFIG_PK | null
    }

    @Test
    def "builder creates outbound item successfully when all parameters are valid"() {
        given:
        def values = defaultItemAttributes()
        def itemDTO = getOutboundItemDTO defaultItemAttributes()

        expect: "created outbound itemDTO state is as expected"
        with(itemDTO) {
            getItem() == values.item
            getChannelConfigurationPK() == values.channelPk
            getIntegrationObjectPK() == values.ioPk
            getRootItemPK() == values.rootItemPk
            getCronJobPK() == values.jobPk
            getRootItemType() == values.rootItemType
            getItemType() == values.itemType
            getIntegrationKey() == values.integrationKey
            isSynchronizeDelete() == values.syncDelete
            isBatch() == values.batch
        }
    }

    @Test
    def "outbound item is successfully created  from another not null outbound item"() {
        given:
        def originalItem = defaultItem()

        when:
        def createdItem = OutboundItemDTOBuilder.from(originalItem).build()

        then:
        with(createdItem) {
            getItem() == OUTBOUND_ITEM
            getChannelConfigurationPK() == originalItem.channelConfigurationPK
            getIntegrationObjectPK() == originalItem.integrationObjectPK
            getRootItemPK() == originalItem.rootItemPK
            getRootItemType() == originalItem.rootItemType
            getCronJobPK() == originalItem.cronJobPK
            getIntegrationKey() == originalItem.integrationKey
            getItemType() == originalItem.itemType
            isSynchronizeDelete() == originalItem.synchronizeDelete
            isBatch() == originalItem.batch
        }
    }

    @Test
    def "create an outbound item throws exception when passed outbound item is null"() {
        when:
        OutboundItemDTOBuilder.from(null).build()

        then:
        def e = thrown IllegalArgumentException
        e.message.contains 'OutboundItemDTO cannot be null'
    }

    @Test
    def "create an outbound item when every parameter except outboundItem, cronjobPk, synchronizeDelete and channelConfigPk is null"() {
        def values = [
                item          : OUTBOUND_ITEM,
                jobPk         : CRONJOB_PK,
                channelPk     : CHANNEL_CONFIG_PK,
                syncDelete    : SYNCHRONIZE_DELETE,
                batch         : BATCH,
                integrationKey: null,
                rootItemPk    : null,
                rootItemType  : null,
                ioPk          : null,
                itemType      : null,
        ]

        def itemDTO = getOutboundItemDTO values

        expect: "created outbound itemDTO state is as expected"
        with(itemDTO) {
            getItem() == values.item
            getChannelConfigurationPK() == values.channelPk
            getIntegrationObjectPK() == values.ioPk
            getRootItemPK() == values.rootItemPk
            getCronJobPK() == values.jobPk
            getRootItemType() == values.rootItemType
            getItemType() == values.itemType
            getIntegrationKey() == values.integrationKey
            isSynchronizeDelete() == values.syncDelete
            isBatch() == values.batch
        }
    }

    @Test
    def 'create different item DTO instances for subsequent build invocations'() {
        given:
        def builder = getDefaultBuilder(defaultItemAttributes())

        expect:
        !builder.build().is(builder.build())
    }

    @Test
    def 'create item DTO throws exception when OutboundItemChange is null'() {
        given:
        def builder = getDefaultBuilder(defaultItemAttributes())

        when: 'OutboundItemChange argument is null'
        builder.withRootItem(null)

        then: 'exception is thrown'
        def e = thrown IllegalArgumentException
        e.message == 'OutboundItemChange cannot be null'
    }

    @Test
    def 'withRootItem successfully updates the rootItemPk with the Pk of OutboundItemChange passed as argument'() {
        given:
        def pk = 9L
        def outboundItem = Stub(OutboundItemChange) {
            getPK() >> pk
        }

        and: 'creating list of attributes for creating custom '
        def attributeMap = defaultItemAttributes()
        attributeMap.item = outboundItem
        def itemDto = defaultItem(attributeMap)
        def builder = OutboundItemDTOBuilder.from(itemDto)

        when: 'populating with item Pk'
        def outboundItemDTO = builder.withRootItem(itemDto.getItem()).build()

        then: 'pk value is as expected'
        outboundItemDTO.getRootItemPK() == pk
    }

    @Test
    def 'changes in builder state before next build call do not mutate previously created items'() {
        given:
        def originalParam = defaultItemAttributes()
        def builder = getDefaultBuilder(originalParam)
        def originalItem = builder.build()
        def values = [
                item          : Stub(OutboundItemChange),
                jobPk         : PK.fromLong(5L),
                channelPk     : 10L,
                syncDelete    : false,
                batch         : false,
                integrationKey: null,
                rootItemPk    : null,
                rootItemType  : null,
                ioPk          : null,
                itemType      : null,
        ]

        when: 'all builder properties are changed'
        builder.withItem(values.item as OutboundItemChange)
                .withChannelConfigurationPK(values.channelPk as Long)
                .withIntegrationObjectPK(values.ioPk as Long)
                .withRootItemPK(values.rootItemPk as Long)
                .withCronJobPK(values.jobPk as PK)
                .withSynchronizeDelete(values.syncDelete as boolean)
                .withBatch(values.batch as boolean)
                .withInfo(values.info as ChangeInfo ?: changeInfo(values))
                .build()

        then: 'the original item still contains the original values'
        with(originalItem) {
            getItem() == originalParam.item
            getChannelConfigurationPK() == originalParam.channelPk
            getIntegrationObjectPK() == originalParam.ioPk
            getRootItemPK() == originalParam.rootItemPk
            getCronJobPK() == originalParam.jobPk
            getRootItemType() == originalParam.rootItemType
            getItemType() == originalParam.itemType
            getIntegrationKey() == originalParam.integrationKey
            isSynchronizeDelete() == originalParam.syncDelete
            isBatch() == originalParam.batch
        }
    }

    OutboundItemDTO defaultItem(Map customValues = [:]) {
        def itemProperties = defaultItemAttributes()
        itemProperties.putAll customValues
        getOutboundItemDTO itemProperties
    }

    OutboundItemDTO getOutboundItemDTO(Map attrs) {
        OutboundItemDTOBuilder.outboundItemDTO()
                .withItem(attrs.item as OutboundItemChange)
                .withChannelConfigurationPK(attrs.channelPk as Long)
                .withIntegrationObjectPK(attrs.ioPk as Long)
                .withRootItemPK(attrs.rootItemPk as Long)
                .withCronJobPK(attrs.jobPk as PK)
                .withSynchronizeDelete(attrs.syncDelete as boolean)
                .withBatch(attrs.batch as boolean)
                .withInfo(attrs.info as ChangeInfo ?: changeInfo(attrs))
                .build()
    }

    OutboundItemDTOBuilder getDefaultBuilder(Map attrs) {
        OutboundItemDTOBuilder.outboundItemDTO()
                .withItem(attrs.item as OutboundItemChange)
                .withChannelConfigurationPK(attrs.channelPk as Long)
                .withIntegrationObjectPK(attrs.ioPk as Long)
                .withRootItemPK(attrs.rootItemPk as Long)
                .withCronJobPK(attrs.jobPk as PK)
                .withSynchronizeDelete(attrs.syncDelete as boolean)
                .withBatch(attrs.batch as boolean)
                .withInfo(attrs.info as ChangeInfo ?: changeInfo(attrs))
    }

    Map defaultItemAttributes() {
        [
                integrationKey: INTEGRATION_KEY,
                syncDelete    : SYNCHRONIZE_DELETE,
                batch         : BATCH,
                jobPk         : CRONJOB_PK,
                rootItemPk    : ROOT_ITEM_PK,
                rootItemType  : ROOT_ITEM_TYPE,
                ioPk          : INTEGRATION_OBJ_PK,
                channelPk     : CHANNEL_CONFIG_PK,
                itemType      : ITEM_TYPE,
                item          : OUTBOUND_ITEM
        ]
    }

    ChangeInfo changeInfo(Map attrs) {
        new ChangeInfo(attrs.integrationKey as String, attrs.itemType as String, attrs.rootItemType as String)
    }
}
