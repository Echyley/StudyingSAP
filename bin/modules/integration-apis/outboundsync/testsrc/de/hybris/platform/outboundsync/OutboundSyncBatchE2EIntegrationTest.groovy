/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.outboundsync

import de.hybris.bootstrap.annotations.IntegrationTest
import de.hybris.platform.apiregistryservices.model.ConsumedDestinationModel
import de.hybris.platform.catalog.model.CatalogVersionModel
import de.hybris.platform.core.model.c2l.CurrencyModel
import de.hybris.platform.core.model.order.OrderEntryModel
import de.hybris.platform.core.model.order.OrderModel
import de.hybris.platform.core.model.product.ProductModel
import de.hybris.platform.core.model.product.UnitModel
import de.hybris.platform.cronjob.enums.CronJobResult
import de.hybris.platform.cronjob.jalo.CronJob
import de.hybris.platform.cronjob.model.CronJobModel
import de.hybris.platform.integrationservices.util.ConfigurationRule
import de.hybris.platform.integrationservices.util.IntegrationTestUtil
import de.hybris.platform.integrationservices.util.ItemTracker
import de.hybris.platform.outboundservices.ConsumedDestinationBuilder
import de.hybris.platform.outboundservices.facade.impl.DefaultOutboundServiceFacade
import de.hybris.platform.outboundservices.util.TestRemoteSystemClient
import de.hybris.platform.outboundsync.activator.OutboundItemConsumer
import de.hybris.platform.outboundsync.activator.impl.DefaultOutboundSyncService
import de.hybris.platform.outboundsync.config.impl.DefaultOutboundSyncConfiguration
import de.hybris.platform.outboundsync.model.OutboundChannelConfigurationModel
import de.hybris.platform.outboundsync.model.OutboundSyncCronJobModel
import de.hybris.platform.outboundsync.model.OutboundSyncJobModel
import de.hybris.platform.outboundsync.model.OutboundSyncStreamConfigurationContainerModel
import de.hybris.platform.outboundsync.model.OutboundSyncStreamConfigurationModel
import de.hybris.platform.outboundsync.util.OutboundSyncEssentialData
import de.hybris.platform.servicelayer.ServicelayerSpockSpecification
import de.hybris.platform.servicelayer.cronjob.CronJobService
import de.hybris.platform.servicelayer.model.ModelService
import org.apache.commons.lang3.StringUtils
import org.junit.ClassRule
import org.junit.Rule
import org.junit.Test
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.ResponseEntity
import spock.lang.AutoCleanup
import spock.lang.Shared

import javax.annotation.Resource

import static de.hybris.platform.integrationservices.IntegrationObjectItemAttributeModelBuilder.integrationObjectItemAttribute
import static de.hybris.platform.integrationservices.IntegrationObjectItemModelBuilder.integrationObjectItem
import static de.hybris.platform.integrationservices.IntegrationObjectModelBuilder.integrationObject
import static de.hybris.platform.integrationservices.util.IntegrationTestUtil.condition
import static de.hybris.platform.outboundservices.ConsumedDestinationBuilder.consumedDestinationBuilder
import static de.hybris.platform.outboundservices.util.BatchResponseBodyBuilder.batchResponseBuilder
import static de.hybris.platform.outboundservices.util.BatchResponseBodyBuilder.singleResponseBuilder
import static de.hybris.platform.outboundsync.OutboundChannelConfigurationBuilder.outboundChannelConfigurationBuilder
import static de.hybris.platform.outboundsync.OutboundSyncStreamConfigurationBuilder.outboundSyncStreamConfigurationBuilder

@IntegrationTest
class OutboundSyncBatchE2EIntegrationTest extends ServicelayerSpockSpecification {
    private static final String TEST_NAME = "OutboundSyncBatchE2E"
    private static final String PRODUCT_IO = "${TEST_NAME}_OutboundProductIO"
    private static final String CATALOG = "${TEST_NAME}_Catalog"
    private static final String DESTINATION_ID = "${TEST_NAME}_ConsumedDestination"
    private static final String CHANNEL_CODE = "${TEST_NAME}_OutboundChannelConfiguration"
    private static final String CHANNEL_CODE_ORDER = "${TEST_NAME}_Order_OutboundChannelConfiguration"
    private static final String BATCH_BOUNDARY = "batch_123"
    private static final String CONTENT_TYPE = "Content-Type"
    private static final String ORDER_IO = "${TEST_NAME}_OutboundOrderIO"
    private static final String STREAM_CONTAINER_ID = "${TEST_NAME}_Order_StreamContainer"
    private static final String STREAM_ID_ORDER = "${TEST_NAME}_Order_Stream"
    private static final String STREAM_ID_ORDERENTRY = "${TEST_NAME}_OrderEntry_Stream"
    private static final String JOB_ID = "${TEST_NAME}_Order_Job"
    private static final String CRONJOB_ID = "${TEST_NAME}_Order_CronJob"
    private static final String CURRENCY = "${TEST_NAME}_USD"
    private static final String UNIT = "${TEST_NAME}_Unit"
    private static final int DEFAULT_BATCH_LIMIT = 2

    @Resource
    private CronJobService cronJobService
    @Resource
    ModelService modelService

    @Resource(name = 'outboundSyncService')
    private DefaultOutboundSyncService outboundSyncService
    @Resource(name = 'outboundServiceFacade')
    private DefaultOutboundServiceFacade outboundServiceFacade
    private DefaultOutboundServiceFacade testOutboundFacade

    @Rule
    private ConfigurationRule configurationRule = ConfigurationRule.createFor 'defaultOutboundSyncConfiguration', DefaultOutboundSyncConfiguration
    @Rule
    private TestRemoteSystemClient testRemoteClient = new TestRemoteSystemClient()
    @Rule
    private TestOutboundItemConsumer testOutboundItemConsumer = new TestOutboundItemConsumer()
    private OutboundItemConsumer outboundItemConsumer

    @Shared
    @ClassRule
    private OutboundSyncEssentialData essentialData = OutboundSyncEssentialData.outboundSyncEssentialData()

    @AutoCleanup('reset')
    private ConsumedDestinationBuilder consumedDestinationBuilder = consumedDestinationBuilder().withId(DESTINATION_ID)
    @AutoCleanup('cleanup')
    private OutboundChannelConfigurationBuilder outboundChannelBuilder = outboundChannelConfigurationBuilder()
            .withCode(CHANNEL_CODE)
            .withIntegrationObject(integrationObject().withCode(PRODUCT_IO)
                    .withItem(integrationObjectItem().withCode('Catalog')
                            .withAttribute(integrationObjectItemAttribute().withName('id')))
                    .withItem(integrationObjectItem().withCode('CatalogVersion')
                            .withAttribute(integrationObjectItemAttribute().withName('version'))
                            .withAttribute(integrationObjectItemAttribute().withName('catalog').withReturnItem('Catalog')))
                    .withItem(integrationObjectItem().withCode('Product')
                            .root()
                            .withAttribute(integrationObjectItemAttribute('code'))
                            .withAttribute(integrationObjectItemAttribute('catalogVersion').withReturnItem('CatalogVersion'))))
            .withBatch()

    @AutoCleanup('cleanup')
    private OutboundChannelConfigurationBuilder outboundChannelBuilderForOrder = setupIOAndOutboundChannelConfigForBatchRequestOfOrder()

    @AutoCleanup('cleanup')
    private OutboundSyncStreamConfigurationBuilder changeDetector = outboundSyncStreamConfigurationBuilder()
            .withOutboundChannelCode(CHANNEL_CODE)
            .withItemType('Product')

    @Rule
    ItemTracker itemTracker = ItemTracker.track ProductModel, OrderModel, OrderEntryModel, CurrencyModel, UnitModel

    private CatalogVersionModel catalogVersion
    private CronJobModel cronJobModel
    private CronJobModel cronJobModelForOrder
    private CronJob cronjob
    private CronJob cronjobForOrder
    private OutboundChannelConfigurationModel channel
    private ConsumedDestinationModel consumedDestinationModel

    def setup() {
        setBatchLimit(DEFAULT_BATCH_LIMIT)
        cronJobModel = essentialData.outboundCronJob()
        cronjob = modelService.getSource(cronJobModel)
        catalogVersion = IntegrationTestUtil.importCatalogVersion('StagedForTest', CATALOG, true)

        testOutboundFacade = new DefaultOutboundServiceFacade(testRemoteClient)
        outboundSyncService.outboundServiceFacade = testOutboundFacade

        outboundItemConsumer = outboundSyncService.outboundItemConsumer
        outboundSyncService.outboundItemConsumer = testOutboundItemConsumer

        consumedDestinationModel = consumedDestinationBuilder.build()
        channel = outboundChannelBuilder.withConsumedDestination(consumedDestinationModel).build()

        changeDetector.build()
    }

    def cleanup() {
        outboundSyncService.outboundServiceFacade = outboundServiceFacade
        outboundSyncService.outboundItemConsumer = outboundItemConsumer
    }

    @Test
    def "sends 2 products when 2 products have been changed"() {
        given: "2 products have been changed"
        def productCode1 = 'prod_a'
        def productCode2 = 'prod_b'
        productsAreCreated(productCode1, productCode2)

        and: "post responds with a batch response consisting of 2 batchPart responses"
        testRemoteClient.respondWithBatch((List<String> contentIds) -> {
            String batchRequestBody = batchResponseBuilder().withBoundary(BATCH_BOUNDARY)
                    .withSingleResponse(singleResponseBuilder().withStatusCode(201).withContentId(contentIds.get(0)))
                    .withSingleResponse(singleResponseBuilder().withStatusCode(201).withContentId(contentIds.get(1)))
                    .build()
            return responseEntity(batchRequestBody, BATCH_BOUNDARY)
        })

        when:
        cronJobService.performCronJob(cronJobModel, true)

        then: "the cronjob result is success and 2 OutboundItems are consumed"
        condition().eventually {
            assert testRemoteClient.invocations() == 1
            assert cronjob.result.code == CronJobResult.SUCCESS.code
            assert testOutboundItemConsumer.invocations() == 2
        }

        and: "the batch request contains created products and goes to the destination"
        def pair = testRemoteClient.getAllInvocations().get(0)
        pair.getKey() == channel.destination
        def requestBody = getRequestBody(pair.getValue())
        requestBody.body.contains(productCode1)
        requestBody.body.contains(productCode2)
    }

    @Test
    def "cronjob result is error when the request and response's contentId don't match"() {
        given: "1 products have changed"
        def productCode1 = 'prod_1'
        productsAreCreated(productCode1)

        and: "the batch response contains contentId that is not from batch request"
        String batchRequestBody = batchResponseBuilder().withBoundary(BATCH_BOUNDARY)
                .withSingleResponse(singleResponseBuilder().withStatusCode(201).withContentId("notMatchingId"))
                .build()
        testRemoteClient.respondWith(responseEntity(batchRequestBody, BATCH_BOUNDARY))

        when:
        cronJobService.performCronJob(cronJobModel, true)

        then: "the cronjob result is error and outboundItem is no consumed due to the left retry attempt > 0"
        condition().eventually {
            assert testRemoteClient.invocations() == 1
            cronjob.result.code == CronJobResult.ERROR.code
            assert testOutboundItemConsumer.invocations() == 0
        }
    }

    @Test
    def "the failed request will be sent out in a new batch request when 2 requests within the batch request of 3 changes fail"() {
        given: "reset batch limit and use real consumer"
        setBatchLimit(3)
        outboundSyncService.outboundItemConsumer = outboundItemConsumer

        and: "3 products have been changed"
        def productCode1 = 'prod_1'
        def productCode2 = 'prod_2'
        def productCode3 = 'prod_3'
        productsAreCreated(productCode1, productCode2, productCode3)

        and: "the first one is successful and the second and third one fails with incorrect contendIds"
        testRemoteClient.respondWithBatch((List<String> contentIds) -> {
            String batchRequestBody = batchResponseBuilder().withBoundary(BATCH_BOUNDARY)
                    .withSingleResponse(singleResponseBuilder().withStatusCode(201).withContentId(contentIds.get(0)))
                    .withSingleResponse(singleResponseBuilder().withStatusCode(400).withContentId("SomeIncorrectContentId"))
                    .withSingleResponse(singleResponseBuilder().withStatusCode(400).withContentId("SomeIncorrectContentId2"))
                    .build()
            return responseEntity(batchRequestBody, BATCH_BOUNDARY)
        })
        and:
        cronJobService.performCronJob(cronJobModel, true)

        when: "the second batch request succeeds with correct contendIds"
        testRemoteClient.respondWithBatch((List<String> contentIds) -> {
            String batchRequestBody = batchResponseBuilder().withBoundary(BATCH_BOUNDARY)
                    .withSingleResponse(singleResponseBuilder().withStatusCode(201).withContentId(contentIds.get(0)))
                    .withSingleResponse(singleResponseBuilder().withStatusCode(201).withContentId(contentIds.get(1)))
                    .build()
            return responseEntity(batchRequestBody, BATCH_BOUNDARY)
        })
        cronJobService.performCronJob(cronJobModel, true)

        then:
        condition().eventually {
            assert testRemoteClient.invocations() == 2
        }

        def pair = testRemoteClient.getAllInvocations().get(1)
        pair.getKey() == channel.destination
        def requestBody = getRequestBody(pair.getValue())
        StringUtils.countMatches(requestBody.body, 'Content-Id: ') == 2  // only contains 2 sub requests

        cleanup:
        outboundSyncService.outboundItemConsumer = testOutboundItemConsumer
    }

    @Test
    def "each batchPart request is retried during the next cronJob run when the entire batch request fails"() {
        given: "2 products have been changed"
        outboundSyncService.outboundItemConsumer = outboundItemConsumer  // use real consumer so that successful request won't be retried.
        def productCode1 = 'prod_1'
        def productCode2 = 'prod_2'
        productsAreCreated(productCode1, productCode2)

        and: "post returns a bad response for first batch request and then succeeds during the retry"
        testRemoteClient.respondWithBatch((List<String> contentIds) -> {
            String batchRequestBody = batchResponseBuilder().withBoundary(BATCH_BOUNDARY)
                    .withSingleResponse(singleResponseBuilder().withStatusCode(400).withContentId(contentIds.get(0)))
                    .withSingleResponse(singleResponseBuilder().withStatusCode(400).withContentId(contentIds.get(1)))
                    .build()
            return responseEntity(batchRequestBody, BATCH_BOUNDARY)
        })

        and: "the first sync request is made"
        cronJobService.performCronJob(cronJobModel, true)

        when: "with correct contentId the second batch request succeeds"
        testRemoteClient.respondWithBatch((List<String> contentIds) -> {
            String batchRequestBody2 = batchResponseBuilder().withBoundary(BATCH_BOUNDARY)
                    .withSingleResponse(singleResponseBuilder().withStatusCode(201).withContentId(contentIds.get(0)))
                    .withSingleResponse(singleResponseBuilder().withStatusCode(201).withContentId(contentIds.get(1)))
                    .build()
            return responseEntity(batchRequestBody2, BATCH_BOUNDARY)
        })
        cronJobService.performCronJob(cronJobModel, true)

        then: "the job sends 1 batch and consumes both batch changes"
        condition().eventually {
            assert cronjob.result.code == CronJobResult.SUCCESS.code
        }

        and: "the second batch request contains both item changes"
        def pair = testRemoteClient.getAllInvocations().get(1)
        pair.getKey() == channel.destination
        def requestBody = getRequestBody(pair.getValue())
        requestBody.body.contains(productCode1)
        requestBody.body.contains(productCode2)

        cleanup:
        outboundSyncService.outboundItemConsumer = testOutboundItemConsumer
    }

    @Test
    def "multiple batch requests are made when number of changes is larger than max batch size"() {
        given: "batch limit is set to 1 item"
        setBatchLimit(1)

        and: "2 products have been changed"
        def productCode1 = 'prod_1'
        def productCode2 = 'prod_2'
        productsAreCreated(productCode1, productCode2)

        when: "There will be two batch requests when running cronjob and 2 batch responses will be generated accordingly"
        testRemoteClient.respondWithBatch((List<String> contentIds) -> {
            String batchRequestBody1 = batchResponseBuilder().withBoundary(BATCH_BOUNDARY)
                    .withSingleResponse(singleResponseBuilder().withStatusCode(201).withContentId(contentIds.get(0)))
                    .build()
            return responseEntity(batchRequestBody1, BATCH_BOUNDARY)
        })
        cronJobService.performCronJob(cronJobModel, true)

        then: "the cronjob runs successfully with 2 batch requests sent and both itemChanges consumed"
        condition().eventually {
            assert cronjob.result.code == CronJobResult.SUCCESS.code
            assert testOutboundItemConsumer.invocations() == 2
            assert testRemoteClient.invocations() == 2
        }

        and: "the batch request only contains the first item changes"
        def pair = testRemoteClient.getAllInvocations().get(0)
        pair.getKey() == channel.destination
        def requestBody = getRequestBody(pair.getValue())
        StringUtils.countMatches(requestBody.body, 'Content-Id: ') == 1
        def pair2 = testRemoteClient.getAllInvocations().get(1)
        pair2.getKey() == channel.destination
        def requestBody2 = getRequestBody(pair2.getValue())
        StringUtils.countMatches(requestBody2.body, 'Content-Id: ') == 1

        cleanup:
        setBatchLimit(DEFAULT_BATCH_LIMIT)
    }

    @Test
    def "batch request is sent when the total of groups (not the changed items) reaches the batch limit"() {
        given: "create OutboundChannelConfig, OutboundSyncStream, SyncJob, CronJob and related instances for generating batch request"
        outboundChannelBuilderForOrder.withConsumedDestination(consumedDestinationModel).build()
        cronJobModelForOrder = setupOutboundSyncForOrderAndGetCronJob()
        cronjobForOrder = modelService.getSource(cronJobModelForOrder)

        and: 'create product to create order'
        def productCode = 'goodProduct'
        def orderCode = 'myOrder'
        def orderCode2 = 'myOrder2'
        productsAreCreated(productCode)

        and: 'create orders that contains multiple changed items'
        ordersForProductAreCreated(productCode, orderCode, orderCode2)

        and:
        setBatchLimit(1)

        when:
        testRemoteClient.respondWithBatch((List<String> contentIds) -> {
            String batchRequestBody1 = batchResponseBuilder().withBoundary(BATCH_BOUNDARY)
                    .withSingleResponse(singleResponseBuilder().withStatusCode(201).withContentId(contentIds.get(0)))
                    .build()
            return responseEntity(batchRequestBody1, BATCH_BOUNDARY)
        })
        cronJobService.performCronJob(cronJobModelForOrder, true)

        then: "2 batch requests generated with 2 groups and 6 changed items"
        condition().eventually {
            assert testRemoteClient.invocations() == 2
            assert testOutboundItemConsumer.invocations() == 6
            assert cronjobForOrder.result.code == CronJobResult.SUCCESS.code
        }

        cleanup:
        cleanupOutboundSyncForOrder()
        setBatchLimit(DEFAULT_BATCH_LIMIT)
    }

    def productsAreCreated(String... productCodes) {
        productCodes.each { productCode ->
            IntegrationTestUtil.importImpEx(
                    "INSERT_UPDATE Product ; code[unique = true] ; catalogVersion",
                    "                      ; $productCode       ; $catalogVersion.pk")
        }
    }

    def ordersForProductAreCreated(String product, String... orderCodes) {
        orderCodes.each { orderCode ->
            IntegrationTestUtil.importImpEx(
                    'INSERT_UPDATE Order; code[unique = true]; user(uid)        ; currency(isocode); date[dateformat=MM/dd/yyyy]',
                    "                   ; $orderCode         ; admin            ; $CURRENCY        ; 09/02/2021",
                    'INSERT OrderEntry; order(code)[unique = true]; entryNumber[unique = true]; product(code); unit(code); quantity',
                    "                 ; $orderCode                ; 1234                      ; $product     ; $UNIT     ; 1",
                    "                 ; $orderCode                ; 2345                      ; $product     ; $UNIT     ; 1"
            )
        }
    }

    def setBatchLimit(final int limit) {
        configurationRule.configuration().outboundBatchLimit = limit
    }

    def getRequestBody(Object o) {
        if (o instanceof HttpEntity<String>) {
            (HttpEntity<String>) o
        } else {
            HttpEntity.EMPTY as HttpEntity<String>
        }
    }

    def responseEntity(final String body, final String batchBoundary) {
        def headers = new HttpHeaders()
        headers.put(CONTENT_TYPE, ["multipart/mixed; boundary=${batchBoundary};charset=UTF-8" as String])
        return new ResponseEntity(body, headers, 202)
    }

    def setupOutboundSyncForOrderAndGetCronJob() {
        IntegrationTestUtil.importImpEx(
                'INSERT_UPDATE OutboundSyncStreamConfigurationContainer; id[unique = true]',
                "                                                      ; $STREAM_CONTAINER_ID",
                'INSERT_UPDATE OutboundSyncStreamConfiguration; streamId[unique = true]; container(id)                ; itemTypeForStream(code); outboundChannelConfiguration(code); whereClause',
                "                                             ; $STREAM_ID_ORDER       ; $STREAM_CONTAINER_ID         ; Order                  ; ${CHANNEL_CODE_ORDER}",
                "                                             ; $STREAM_ID_ORDERENTRY  ; $STREAM_CONTAINER_ID         ; OrderEntry             ; ${CHANNEL_CODE_ORDER}",
                "INSERT_UPDATE OutboundSyncJob; code[unique = true]; streamConfigurationContainer(id)",
                "                             ; $JOB_ID            ; $STREAM_CONTAINER_ID",
                "INSERT_UPDATE OutboundSyncCronJob; code[unique = true]; job(code)    ; sessionLanguage(isocode)",
                "                                 ; $CRONJOB_ID        ; $JOB_ID      ; en",
                'INSERT_UPDATE Currency; isocode[unique = true]; symbol',
                "                      ; $CURRENCY              ;\$",
                'INSERT_UPDATE Unit; code[unique = true]; unitType',
                "                  ; $UNIT              ; weight"
        )
        return IntegrationTestUtil.findAny(CronJobModel, { it.code == CRONJOB_ID }).orElse(null)
    }

    def cleanupOutboundSyncForOrder() {
        IntegrationTestUtil.remove OutboundSyncStreamConfigurationContainerModel, { it.id == STREAM_CONTAINER_ID }
        IntegrationTestUtil.remove OutboundSyncStreamConfigurationModel, { it.streamId == STREAM_ID_ORDER }
        IntegrationTestUtil.remove OutboundSyncStreamConfigurationModel, { it.streamId == STREAM_ID_ORDERENTRY }
        IntegrationTestUtil.remove OutboundSyncJobModel, { it.code == JOB_ID }
        IntegrationTestUtil.remove OutboundSyncCronJobModel, { it.code == CRONJOB_ID }
    }

    def setupIOAndOutboundChannelConfigForBatchRequestOfOrder() {
        return outboundChannelConfigurationBuilder()
                .withCode(CHANNEL_CODE_ORDER)
                .withIntegrationObject(integrationObject().withCode(ORDER_IO)
                        .withItem(integrationObjectItem().withCode('Catalog')
                                .withAttribute(integrationObjectItemAttribute().withName('id')))
                        .withItem(integrationObjectItem().withCode('CatalogVersion')
                                .withAttribute(integrationObjectItemAttribute().withName('version'))
                                .withAttribute(integrationObjectItemAttribute().withName('catalog').withReturnItem('Catalog')))
                        .withItem(integrationObjectItem().withCode('Product')
                                .withAttribute(integrationObjectItemAttribute('code'))
                                .withAttribute(integrationObjectItemAttribute('catalogVersion').withReturnItem('CatalogVersion')))
                        .withItem(integrationObjectItem().withCode('Unit')
                                .withAttribute(integrationObjectItemAttribute('name'))
                                .withAttribute(integrationObjectItemAttribute('code'))
                                .withAttribute(integrationObjectItemAttribute('unitType')))
                        .withItem(integrationObjectItem().withCode('User')
                                .withAttribute(integrationObjectItemAttribute('uid').unique()))
                        .withItem(integrationObjectItem().withCode('Currency')
                                .withAttribute(integrationObjectItemAttribute('isocode').unique()))
                        .withItem(integrationObjectItem().withCode('OrderEntry')
                                .withAttribute(integrationObjectItemAttribute('product').withReturnItem('Product').autoCreate())
                                .withAttribute(integrationObjectItemAttribute('order').withReturnItem('Order'))
                                .withAttribute(integrationObjectItemAttribute('entryNumber').unique())
                                .withAttribute(integrationObjectItemAttribute('quantity'))
                                .withAttribute(integrationObjectItemAttribute('unit').withReturnItem('Unit').autoCreate()))
                        .withItem(integrationObjectItem().withCode('Order')
                                .root()
                                .withAttribute(integrationObjectItemAttribute('currency').withReturnItem('Currency'))
                                .withAttribute(integrationObjectItemAttribute('code').unique())
                                .withAttribute(integrationObjectItemAttribute('date').unique())
                                .withAttribute(integrationObjectItemAttribute('entries').withReturnItem('OrderEntry').autoCreate())
                                .withAttribute(integrationObjectItemAttribute('description'))
                                .withAttribute(integrationObjectItemAttribute('user').withReturnItem('User'))))
                .withBatch()
    }
}
