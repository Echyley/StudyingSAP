/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.outboundsync.dto

import de.hybris.bootstrap.annotations.UnitTest
import de.hybris.platform.core.PK
import de.hybris.platform.integrationservices.model.IntegrationObjectModel
import de.hybris.platform.outboundsync.model.OutboundChannelConfigurationModel
import de.hybris.platform.testframework.JUnitPlatformSpecification
import org.junit.Test
import spock.lang.Shared

@UnitTest
class OutboundItemDTOUnitTest extends JUnitPlatformSpecification {

    private static final def ANOTHER_CRONJOB_PK = PK.fromLong 5L
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
    def 'populates integration object PK when created with an integration object model'() {
        given: 'an integration object model'
        def io = Stub(IntegrationObjectModel) {
            getPk() >> PK.fromLong(INTEGRATION_OBJ_PK)
        }

        when: 'the DTO is built using the integration object model'
        def itemDTO = OutboundItemDTOBuilder.outboundItemDTO()
                .withItem(OUTBOUND_ITEM)
                .withChannelConfigurationPK(CHANNEL_CONFIG_PK)
                .withCronJobPK(CRONJOB_PK)
                .withIntegrationObject(io)
                .build()

        then: 'the integration object PK is populated in the DTO'
        itemDTO?.integrationObjectPK == INTEGRATION_OBJ_PK
    }

    @Test
    def 'populates integration object PK, channel PK, synchronizeDelete and isBatch flag when created with an outbound channel configuration model'() {
        given: 'an outbound channel configuration model'
        def channel = Stub(OutboundChannelConfigurationModel) {
            getPk() >> PK.fromLong(CHANNEL_CONFIG_PK)
            getIntegrationObject() >> Stub(IntegrationObjectModel) {
                getPk() >> PK.fromLong(INTEGRATION_OBJ_PK)
            }
            getSynchronizeDelete() >> SYNCHRONIZE_DELETE
            getBatch() >> BATCH
        }

        when: 'the DTO is built using the outbound channel model'
        def itemDTO = OutboundItemDTOBuilder.outboundItemDTO()
                .withChannelConfiguration(channel)
                .withCronJobPK(CRONJOB_PK)
                .withItem(OUTBOUND_ITEM)
                .build()

        then: 'the channel configuration PK is populated in the DTO'
        itemDTO?.channelConfigurationPK == CHANNEL_CONFIG_PK
        and: 'integration object PK is populated'
        itemDTO?.integrationObjectPK == INTEGRATION_OBJ_PK
        and: 'synchronizeDelete is populated'
        itemDTO?.synchronizeDelete == SYNCHRONIZE_DELETE
        itemDTO?.batch == BATCH
    }

    @Test
    def "overwrites integration key, item type and root item type from the provided info: '#info'"() {
        given: 'item is created with the info'
        def item = defaultItem(info: info)

        expect: 'created item contains expected values'
        item.integrationKey == key
        item.itemType == type
        item.rootItemType == rootType

        where:
        info                             | key   | type | rootType
        new ChangeInfo(null, null, null) | null  | null | null
        new ChangeInfo('A|B', 'C', 'D')  | 'A|B' | 'C'  | 'D'
        new ChangeInfo('', '', '')       | ''    | ''   | ''
    }

    @Test
    def "two outbound items are equal"() {
        given:
        def item1 = defaultItem()
        def item2 = defaultItem()

        expect:
        item1 == item1
        item1 == item2
    }

    @Test
    def "two outbound items are not equal when #condition"() {
        given:
        def item1 = defaultItem()

        expect:
        item1 != item2

        where:
        condition                        | item2
        "item is different"              | defaultItem(item: Stub(OutboundItemChange))
        "channel pk is different"        | defaultItem(channelPk: -CHANNEL_CONFIG_PK)
        "intObj pk is different"         | defaultItem(ioPk: -INTEGRATION_OBJ_PK)
        "root pk is different"           | defaultItem(rootItemPk: -ROOT_ITEM_PK)
        "root type is different"         | defaultItem(rootItemType: ROOT_ITEM_TYPE.reverse())
        "cronJob pk is different"        | defaultItem(jobPk: ANOTHER_CRONJOB_PK)
        "integrationKey is different"    | defaultItem(integrationKey: 'anotherIntegrationKey')
        "item type is different"         | defaultItem(itemType: ITEM_TYPE.reverse())
        "synchronizeDelete is different" | defaultItem(syncDelete: !SYNCHRONIZE_DELETE)
        "isBatch is different"           | defaultItem(batch: !BATCH)
        "type is different"              | 1
        "item2 is null"                  | null
    }

    @Test
    def "two outbound items have the same hashcode"() {
        given:
        def item1 = defaultItem()
        def item2 = defaultItem()

        expect:
        item1.hashCode() == item2.hashCode()
    }

    @Test
    def "two outbound items have different hashcode when #condition is different"() {
        given:
        def item1 = defaultItem()

        expect:
        item1.hashCode() != item2.hashCode()

        where:
        condition           | item2
        "item"              | defaultItem(item: Stub(OutboundItemChange))
        "channel pk"        | defaultItem(channelPk: -1 * CHANNEL_CONFIG_PK)
        "intObj pk"         | defaultItem(ioPk: -INTEGRATION_OBJ_PK)
        "root pk"           | defaultItem(rootItemPk: -ROOT_ITEM_PK)
        "root type"         | defaultItem(rootItemType: ROOT_ITEM_TYPE.reverse())
        "cronJob pk"        | defaultItem(jobPk: ANOTHER_CRONJOB_PK)
        "integration key"   | defaultItem(integrationKey: 'anotherIntegrationKey')
        "item type"         | defaultItem(itemType: ITEM_TYPE.reverse())
        "synchronizeDelete" | defaultItem(syncDelete: !SYNCHRONIZE_DELETE)
        "isBatch"           | defaultItem(batch: !BATCH)
        "type"              | 1
    }

    @Test
    def "isDelete() is #res when change is #changeType"() {
        given:
        def change = Stub(OutboundItemChange) {
            getChangeType() >> changeType
        }

        expect:
        defaultItem(item: change).deleted == res

        where:
        changeType                  | res
        OutboundChangeType.CREATED  | false
        OutboundChangeType.DELETED  | true
        OutboundChangeType.MODIFIED | false
    }

    OutboundItemDTO defaultItem(Map customValues = [:]) {
        def itemProperties = defaultItemAttributes()
        itemProperties.putAll customValues
        item itemProperties
    }

    OutboundItemDTO item(Map attrs) {
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
