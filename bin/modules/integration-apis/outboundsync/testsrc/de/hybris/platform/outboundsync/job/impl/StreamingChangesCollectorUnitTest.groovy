/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.outboundsync.job.impl

import de.hybris.bootstrap.annotations.UnitTest
import de.hybris.deltadetection.ItemChangeDTO
import de.hybris.deltadetection.enums.ChangeType
import de.hybris.platform.core.PK
import de.hybris.platform.core.model.type.ComposedTypeModel
import de.hybris.platform.cronjob.model.CronJobModel
import de.hybris.platform.integrationservices.model.IntegrationObjectItemModel
import de.hybris.platform.integrationservices.model.IntegrationObjectModel
import de.hybris.platform.integrationservices.util.JsonBuilder
import de.hybris.platform.outboundsync.dto.ChangeInfo
import de.hybris.platform.outboundsync.dto.OutboundItemDTO
import de.hybris.platform.outboundsync.dto.impl.DeltaDetectionOutboundItemChange
import de.hybris.platform.outboundsync.job.FilteringService
import de.hybris.platform.outboundsync.job.ItemChangeSender
import de.hybris.platform.outboundsync.model.OutboundChannelConfigurationModel
import de.hybris.platform.outboundsync.model.OutboundSyncStreamConfigurationModel
import de.hybris.platform.testframework.JUnitPlatformSpecification
import org.junit.Test
import spock.lang.Specification
import spock.lang.Unroll

@UnitTest
class StreamingChangesCollectorUnitTest extends JUnitPlatformSpecification {
    private static final Long IO_PK = 1L
    private static final Long OUTBOUND_CHANNEL_CONFIG_PK = 2L
    private static final PK JOB_PK = PK.fromLong(3)
    private static final Boolean SYNCHRONIZE_DELETE = true
    private static final String INTEGRATION_KEY = 'key|not-important'
    private static final String IO_ITEM_TYPE = 'ItemType'
    private static final String IO_ITEM_SUB_TYPE = 'ChildItemType'
    private static final String IO_ROOT_ITEM_TYPE = 'RootItemType'
    private static final String IO_ROOT_ITEM_SUB_TYPE = 'RootChildItemType'

    def filteringService = Stub(FilteringService) {
        applyFilters(_ as OutboundItemDTO, _ as IntegrationObjectModel) >> { arguments -> Optional.of(arguments[0]) }
    }
    def itemChangeSender = Mock ItemChangeSender
    def streamChangesCollector = new StreamingChangesCollector(filteringService, itemChangeSender, jobModel(), streamModel())

    @Test
    @Unroll
    def "cannot be created with a null #propertyName"() {
        when:
        new StreamingChangesCollector(filteringServiceVal, itemChangeSenderVal, job, streamConfig)

        then:
        thrown(IllegalArgumentException)

        where:
        propertyName          | filteringServiceVal    | job        | streamConfig  | itemChangeSenderVal
        'itemChangeSender'    | Stub(FilteringService) | jobModel() | streamModel() | null
        'streamConfiguration' | Stub(FilteringService) | jobModel() | null          | Stub(ItemChangeSender)
        'cron job'            | Stub(FilteringService) | null       | streamModel() | Stub(ItemChangeSender)
        'filteringService'    | null                   | jobModel() | streamModel() | Stub(ItemChangeSender)
    }

    @Test
    def 'reports 0 changes collected upon instantiation'() {
        expect:
        streamChangesCollector.numberOfChangesCollected == 0
    }

    @Test
    def 'reports change count for every change collected'() {
        when:
        streamChangesCollector.collect Stub(ItemChangeDTO)
        then:
        streamChangesCollector.numberOfChangesCollected == 1

        when:
        streamChangesCollector.collect Stub(ItemChangeDTO)
        then:
        streamChangesCollector.numberOfChangesCollected == 2
    }

    @Test
    def 'collect sends the expected OutboundItemDTO when the change stream has non-blank infoExpression'() {
        given:
        streamChangesCollector = new StreamingChangesCollector(filteringService, itemChangeSender, jobModel(), streamModel('{}'))
        and: 'a change with an info'
        ItemChangeDTO change = itemChange(key: INTEGRATION_KEY, type: IO_ROOT_ITEM_TYPE, rootType: IO_ROOT_ITEM_TYPE, changeType: ChangeType.DELETED)
        and: 'sent DTO is captured'
        def convertedDTO = Stub(OutboundItemDTO)
        itemChangeSender.send(_ as OutboundItemDTO) >> { List args ->
            convertedDTO = args[0] as OutboundItemDTO
        }

        when:
        def collectResp = streamChangesCollector.collect change

        then: 'integration key is populated in the DTO'
        collectResp
        with(convertedDTO) {
            integrationObjectPK == IO_PK
            channelConfigurationPK == OUTBOUND_CHANNEL_CONFIG_PK
            cronJobPK == JOB_PK
            integrationKey == INTEGRATION_KEY
            itemType == IO_ROOT_ITEM_TYPE
            rootItemType == IO_ROOT_ITEM_TYPE
            synchronizeDelete == SYNCHRONIZE_DELETE
            item instanceof DeltaDetectionOutboundItemChange
        }
    }

    @Test
    @Unroll
    def "collect sends the expected OutboundItemDTO when the change stream has '#expr' infoExpression"() {
        given: 'a collector with the stream that has non-blank info expression'
        streamChangesCollector = new StreamingChangesCollector(filteringService, itemChangeSender, jobModel(), streamModel(expr))
        and:
        def change = itemChange()
        and: 'the sent DTO is captured'
        def convertedDTO = Stub(OutboundItemDTO)
        itemChangeSender.send(_ as OutboundItemDTO) >> { List args ->
            convertedDTO = args[0] as OutboundItemDTO
        }

        when:
        def collectResp = streamChangesCollector.collect change

        then: 'info fields are not populated in the DTO'
        collectResp
        with(convertedDTO) {
            integrationObjectPK == IO_PK
            channelConfigurationPK == OUTBOUND_CHANNEL_CONFIG_PK
            cronJobPK == JOB_PK
            !integrationKey
            !itemType
            !rootItemType
            synchronizeDelete == SYNCHRONIZE_DELETE
            item instanceof DeltaDetectionOutboundItemChange
        }

        where:
        expr << [null, '', '  ']
    }

    @Test
    def "OutboundItemDTO change is not sent when the filterService returns an empty optional"() {
        given: 'filterService returns optional empty'
        def filterService = Stub(FilteringService) {
            applyFilters(_ as OutboundItemDTO) >> { OutboundItemDTO dto -> Optional.empty() }
        }
        and: 'a collector with the stream that has non-blank info expression'
        def changesCollector = new StreamingChangesCollector(filterService, itemChangeSender, jobModel(), streamModel())
        and: 'a change with an info'
        def change = itemChange 'default value - not from info expression'
        and: 'the sent DTO is captured'
        def convertedDTO = Stub(OutboundItemDTO)
        itemChangeSender.send(_ as OutboundItemDTO) >> { List args ->
            convertedDTO = args[0] as OutboundItemDTO
        }

        when:
        changesCollector.collect change

        then: 'info fields are not populated in the DTO'
        0 * itemChangeSender.send(_ as OutboundItemDTO)
    }

    @Test
    def 'default info parser can be overridden by a custom parser'() {
        given: 'the collector uses a custom parser'
        def infoInCustomFormat = "customKey**$IO_ITEM_TYPE**^^$IO_ROOT_ITEM_TYPE^^)"
        def changeInfo = new ChangeInfo('customKey', IO_ITEM_TYPE, IO_ROOT_ITEM_TYPE)
        streamChangesCollector.infoParser = Stub(ChangeInfoParser) {
            parse(infoInCustomFormat) >> Optional.of(changeInfo)
        }
        and: 'sent DTO is captured'
        def convertedDTO = Stub(OutboundItemDTO)
        itemChangeSender.send(_ as OutboundItemDTO) >> { List args ->
            convertedDTO = args[0] as OutboundItemDTO
        }

        when:
        streamChangesCollector.collect itemChange(infoInCustomFormat)

        then: 'value from the custom info parser are populated in the DTO'
        with(convertedDTO) {
            integrationKey == changeInfo.integrationKey
            itemType == changeInfo.itemType
            rootItemType == changeInfo.rootItemType
        }
    }

    @Test
    def 'null custom info parser is ignored'() {
        given: 'null custom info parser is injected'
        streamChangesCollector.infoParser = null
        and: 'sent DTO is captured'
        def convertedDTO = Stub(OutboundItemDTO)
        itemChangeSender.send(_ as OutboundItemDTO) >> { List args ->
            convertedDTO = args[0] as OutboundItemDTO
        }

        when:
        streamChangesCollector.collect itemChange(type: IO_ITEM_TYPE)

        then: 'the info is parsed using the previously set parser'
        with(convertedDTO) {
            itemType == IO_ITEM_TYPE
        }
    }

    OutboundSyncStreamConfigurationModel streamModel(String infoExpr = 'default expression - does not matter') {
        Stub(OutboundSyncStreamConfigurationModel) {
            getOutboundChannelConfiguration() >> Stub(OutboundChannelConfigurationModel) {
                getIntegrationObject() >> Stub(IntegrationObjectModel) {
                    getPk() >> PK.fromLong(IO_PK)
                    getItems() >> [integrationObjectItem(), rootIntegrationObjectItem()]
                }
                getPk() >> PK.fromLong(OUTBOUND_CHANNEL_CONFIG_PK)
                getSynchronizeDelete() >> SYNCHRONIZE_DELETE
            }
            getInfoExpression() >> infoExpr
        }
    }

    IntegrationObjectItemModel rootIntegrationObjectItem() {
        def item = integrationObjectItem(IO_ROOT_ITEM_TYPE, IO_ROOT_ITEM_SUB_TYPE)
        item.getRoot() >> true
        item
    }

    IntegrationObjectItemModel integrationObjectItem(String type = IO_ITEM_TYPE, String subtype = IO_ITEM_SUB_TYPE) {
        def childItemType = composedType(subtype)
        def itemType = composedType(type)
        itemType.getAllSubTypes() >> [childItemType]
        Stub(IntegrationObjectItemModel) {
            getCode() >> "${type}_IO_Item"
            getType() >> itemType
        }
    }

    ComposedTypeModel composedType(String typeCode) {
        Stub(ComposedTypeModel) {
            getCode() >> typeCode
        }
    }

    CronJobModel jobModel() {
        Stub(CronJobModel) {
            getPk() >> JOB_PK
        }
    }

    private ItemChangeDTO itemChange(Map<String, Object> values = [key: INTEGRATION_KEY, type: IO_ITEM_TYPE, rootType: IO_ROOT_ITEM_TYPE, changeType: ChangeType.MODIFIED]) {
        def json = JsonBuilder.json()
        if (values?.key) {
            json.withField 'key', values.key
        }
        if (values?.type) {
            json.withField 'type', values.type
        }
        if (values?.rootType) {
            json.withField 'rootType', values.rootType
        }
        if (values?.changeType) {
            itemChange json.build(), values.changeType
        } else {
            itemChange json.build()
        }
    }

    private ItemChangeDTO itemChange(String info, ChangeType changeType = ChangeType.MODIFIED) {
        Stub(ItemChangeDTO) {
            getInfo() >> info
            getChangeType() >> changeType
        }
    }
}
