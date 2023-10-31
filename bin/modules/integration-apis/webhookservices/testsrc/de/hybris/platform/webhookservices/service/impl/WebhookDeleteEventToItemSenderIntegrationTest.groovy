/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.webhookservices.service.impl

import de.hybris.bootstrap.annotations.IntegrationTest
import de.hybris.platform.catalog.model.CatalogModel
import de.hybris.platform.core.PK
import de.hybris.platform.core.model.ItemModel
import de.hybris.platform.core.model.c2l.CurrencyModel
import de.hybris.platform.core.model.order.OrderModel
import de.hybris.platform.core.model.product.ProductModel
import de.hybris.platform.core.model.product.UnitModel
import de.hybris.platform.core.model.user.CustomerModel
import de.hybris.platform.integrationservices.IntegrationObjectModelBuilder
import de.hybris.platform.integrationservices.scripting.ScriptModelBuilder
import de.hybris.platform.integrationservices.service.IntegrationObjectConversionService
import de.hybris.platform.integrationservices.util.IntegrationTestUtil
import de.hybris.platform.integrationservices.util.Log
import de.hybris.platform.integrationservices.util.impex.ModuleEssentialData
import de.hybris.platform.outboundservices.ConsumedDestinationBuilder
import de.hybris.platform.outboundservices.enums.OutboundSource
import de.hybris.platform.outboundservices.event.EventType
import de.hybris.platform.outboundservices.event.impl.DefaultEventType
import de.hybris.platform.outboundservices.facade.OutboundServiceFacade
import de.hybris.platform.outboundservices.facade.SyncParameters
import de.hybris.platform.outboundservices.util.TestOutboundFacade
import de.hybris.platform.servicelayer.ServicelayerSpockSpecification
import de.hybris.platform.tx.Transaction
import de.hybris.platform.webhookservices.cache.WebhookCacheService
import de.hybris.platform.webhookservices.event.ItemCreatedEvent
import de.hybris.platform.webhookservices.event.ItemDeletedEvent
import de.hybris.platform.webhookservices.event.ItemSavedEvent
import de.hybris.platform.webhookservices.event.ItemUpdatedEvent
import de.hybris.platform.webhookservices.model.WebhookPayloadModel
import de.hybris.platform.webhookservices.util.WebhookServicesEssentialData
import org.apache.commons.lang3.builder.EqualsBuilder
import org.junit.ClassRule
import org.junit.Rule
import org.junit.Test
import spock.lang.AutoCleanup
import spock.lang.Shared

import javax.annotation.Resource
import java.time.Duration

import static de.hybris.platform.integrationservices.IntegrationObjectItemAttributeModelBuilder.integrationObjectItemAttribute
import static de.hybris.platform.integrationservices.IntegrationObjectItemModelBuilder.integrationObjectItem
import static de.hybris.platform.integrationservices.IntegrationObjectModelBuilder.integrationObject
import static de.hybris.platform.integrationservices.util.EventualCondition.eventualCondition
import static de.hybris.platform.outboundservices.ConsumedDestinationBuilder.consumedDestinationBuilder
import static de.hybris.platform.webhookservices.WebhookConfigurationBuilder.webhookConfiguration

@IntegrationTest
class WebhookDeleteEventToItemSenderIntegrationTest extends ServicelayerSpockSpecification {

    private static final def LOG = Log.getLogger(WebhookDeleteEventToItemSenderIntegrationTest)
    private static final def TEST_NAME = 'WebhookDeleteEventToItemSender'

    private static final def ORDER_IO = "${TEST_NAME}_OrderIO"
    private static final def ORDER_ENTRY_IO = "${TEST_NAME}_OrderEntryIO"
    private static final def CUSTOMER_UID = "${TEST_NAME}_tester".toLowerCase()
    private static final def DESTINATION = "${TEST_NAME}_Orders"
    private static final def OTHER_DESTINATION = "${TEST_NAME}_Other_Orders"
    private static final def ORDER_CODE = "${TEST_NAME}_Order"
    private static final def PRODUCT = "${TEST_NAME}_hammer"
    private static final def UNIT = "${TEST_NAME}-piece"
    private static final def CURRENCY = 'USD'
    private static final def REASONABLE_TIME = Duration.ofSeconds(6)
    private static final def WAIT_AFTER_CHANGES_SENT = Duration.ofSeconds(3)

    private OutboundServiceFacade originalOutboundServiceFacade

    @Resource(name = "integrationObjectConversionService")
    private IntegrationObjectConversionService conversionService
    @Resource(name = "webhookCacheService")
    private WebhookCacheService webhookCacheService
    @Resource(name = "webhookDeleteEventToItemSender")
    private WebhookDeleteEventToItemSender webhookDeleteEventToItemSender
    @Resource(name = "webhookEventToItemSender")
    private WebhookEventToItemSender webhookEventToItemSender

    @Shared
    @ClassRule
    ModuleEssentialData essentialData = WebhookServicesEssentialData.webhookServicesEssentialData()
    @Shared
    @ClassRule
    IntegrationObjectModelBuilder orderIOBuilder = integrationObject().withCode(ORDER_IO)
            .withItem(integrationObjectItem().withCode('Order').root()
                    .withAttribute(integrationObjectItemAttribute().withName('code').unique())
                    .withAttribute(integrationObjectItemAttribute().withName('entries').withReturnItem('OrderEntry')))
            .withItem(integrationObjectItem().withCode('OrderEntry')
                    .withAttribute(integrationObjectItemAttribute().withName('order').withReturnItem('Order').unique())
                    .withAttribute(integrationObjectItemAttribute().withName('entryNumber').unique())
                    .withAttribute(integrationObjectItemAttribute().withName('quantity')))
    @Shared
    @ClassRule
    IntegrationObjectModelBuilder orderEntryIOBuilder = integrationObject().withCode(ORDER_ENTRY_IO)
            .withItem(integrationObjectItem().withCode('OrderEntry').root()
                    .withAttribute(integrationObjectItemAttribute().withName('order').withReturnItem('Order').unique())
                    .withAttribute(integrationObjectItemAttribute().withName('entryNumber').unique())
                    .withAttribute(integrationObjectItemAttribute().withName('quantity')))
            .withItem(integrationObjectItem().withCode('Order')
                    .withAttribute(integrationObjectItemAttribute().withName('code').unique()))
    @Rule
    TestOutboundFacade mockOutboundServiceFacade = new TestOutboundFacade().respondWithCreated()
    @AutoCleanup('cleanup')
    def webhookBuilder = webhookConfiguration().withDestination(defaultDestinationBuilder())
    @AutoCleanup('cleanup')
    def scriptBuilder = ScriptModelBuilder.groovyScript()

    def setupSpec() {
        IntegrationTestUtil.importImpEx(
                'INSERT_UPDATE Catalog ; id[unique = true]',
                "                             ; $TEST_NAME",
                'INSERT_UPDATE CatalogVersion; version[unique = true]; catalog(id)[unique = true]',
                "                            ; IntegrationTest       ; $TEST_NAME",
                'INSERT_UPDATE Product; code[unique = true]; catalogVersion(version, catalog(id))[unique = true]',
                "                     ; $PRODUCT           ; IntegrationTest:$TEST_NAME",
                'INSERT_UPDATE Currency; isocode[unique = true]',
                "                      ; $CURRENCY             ",
                'INSERT_UPDATE Customer; uid[unique = true]; name',
                "                      ; $CUSTOMER_UID     ; $CUSTOMER_UID",
                'INSERT_UPDATE Unit; code[unique = true]; unitType',
                "                  ; $UNIT              ; $UNIT")
    }

    def cleanupSpec() {
        IntegrationTestUtil.remove(ProductModel) { it.code == PRODUCT }
        IntegrationTestUtil.remove(CurrencyModel) { it.isocode == CURRENCY }
        IntegrationTestUtil.remove(UnitModel) { it.code == UNIT }
        IntegrationTestUtil.remove(CatalogModel) { it.id == TEST_NAME }
        IntegrationTestUtil.remove(CustomerModel) { it.uid == CUSTOMER_UID }
    }

    def setup() {
        originalOutboundServiceFacade = webhookEventToItemSender.outboundServiceFacade
        webhookEventToItemSender.outboundServiceFacade = mockOutboundServiceFacade
        webhookDeleteEventToItemSender.outboundServiceFacade = mockOutboundServiceFacade
    }

    def cleanup() {
        webhookEventToItemSender.outboundServiceFacade = originalOutboundServiceFacade
        webhookDeleteEventToItemSender.outboundServiceFacade = originalOutboundServiceFacade
        IntegrationTestUtil.remove(OrderModel) { it.code == ORDER_CODE }
    }

    @Test
    def 'delete event is sent to webhook when the parent item is deleted'() {
        given: 'a parent item is created'
        def order = createOrder()
        and: 'a webhook configuration is created'
        def scriptCode = 'passingFilterScript'
        scriptBuilder.withCode(scriptCode)
                .withContent(passingFilterScript())
                .build()
        and: 'a non-blocked matching webhook configuration'
        webhookBuilder.withIntegrationObject(ORDER_IO)
                .withEvent(ItemDeletedEvent)
                .withScriptFilter(scriptCode)
                .build()
        and: 'convert the parent item before deleting it'
        def payload = new WebhookPayloadModel()
        payload.setData(conversionService.convert(order, ORDER_IO))
        waitForProcessing()

        when:
        deleteOrder(order)

        then: 'a delete event is sent to webhook'
        assertEventsSentToWebhooks([invocation(payload, DefaultEventType.DELETED, ORDER_IO)])
        and: 'the caches are cleaned'
        assertCleanCache(order.pk)
    }

    @Test
    def 'update event is sent to webhook after a child item is deleted for the parent item with configured event #updateParentEvent'() {
        given: 'parent and child item are created'
        def order = createOrder()
        def orderEntryPk = order.entries.get(0).pk
        waitForProcessing()
        and: 'a webhook configuration is created'
        webhookBuilder.withIntegrationObject(ORDER_IO)
                .withEvent(updateParentEvent)
                .build()
        waitForProcessing()

        when: 'a child item is deleted'
        deleteOrderEntry()

        then: 'update event is sent to webhook'
        assertEventsSentToWebhooks([invocation(order, DefaultEventType.UPDATED, ORDER_IO)])
        and: 'the caches are cleaned'
        assertCleanCache(orderEntryPk)

        where:
        updateParentEvent << [ItemUpdatedEvent, ItemSavedEvent]
    }

    @Test
    def 'update and delete events are sent to webhook after a child item is deleted'() {
        given: 'parent and child items are created'
        def order = createOrder()
        def orderEntryPk = order.entries.get(0).pk
        waitForProcessing()
        and: 'webhook configurations are created'
        webhookBuilder.withIntegrationObject(ORDER_IO).withEvent(ItemUpdatedEvent).build()
        webhookBuilder.withIntegrationObject(ORDER_ENTRY_IO).withEvent(ItemDeletedEvent).build()
        waitForProcessing()
        and: 'convert the child item before deleting it'
        def payload = new WebhookPayloadModel()
        payload.setData(conversionService.convert(order.entries.get(0), ORDER_ENTRY_IO))

        when: 'a child item is deleted'
        deleteOrderEntry()

        then: 'update and delete events are sent to webhooks'
        assertEventsSentToWebhooks([invocation(payload, DefaultEventType.DELETED, ORDER_ENTRY_IO),
                                    invocation(order, DefaultEventType.UPDATED, ORDER_IO)])
        and: 'the caches are cleaned'
        assertCleanCache(orderEntryPk)
    }

    @Test
    def 'no event is sent to webhook when the parent item is deleted'() {
        given: 'a parent item is created'
        def order = createOrder()
        and: 'webhook configurations are created'
        def scriptCode = 'blockingFilterScript'
        scriptBuilder.withCode(scriptCode)
                .withContent(blockingFilterScript())
                .build()
        and: 'a blocked matching webhook configuration'
        webhookBuilder.withDestination(defaultDestinationBuilder(OTHER_DESTINATION))
                .withIntegrationObject(ORDER_IO)
                .withEvent(ItemDeletedEvent)
                .withScriptFilter(scriptCode)
                .build()
        and: 'a non-matching webhook configuration'
        webhookBuilder.withIntegrationObject(ORDER_IO)
                .withEvent(ItemCreatedEvent)
                .withScriptFilter(scriptCode)
                .build()
        waitForProcessing()

        when: 'the parent item is deleted'
        deleteOrder(order)

        then: 'no event is sent to webhook'
        assertNoEventSentToWebhook()
        and: 'the caches are cleaned'
        assertCleanCache(order.pk)
    }

    @Test
    def 'no event is sent to webhook when the transaction is rolled back'() {
        given: 'a parent item is created'
        def order = createOrder()
        and: 'a webhook configuration is created'
        webhookBuilder.withIntegrationObject(ORDER_IO)
                .withEvent(ItemDeletedEvent)
                .build()
        waitForProcessing()

        when: 'the transaction is rolled back'
        Transaction transaction = Transaction.current()
        transaction.begin()
        deleteOrder(order)
        transaction.rollback()

        then: 'no event is sent to webhook'
        assertNoEventSentToWebhook()
        and: 'the caches are cleaned'
        assertCleanCache(order.pk)
    }

    @Test
    def 'event is sent to webhook when the transaction is committed'() {
        given: 'a parent item is created'
        def order = createOrder()
        def orderPk = order.pk
        and: 'a webhook configuration is created'
        webhookBuilder.withIntegrationObject(ORDER_IO)
                .withEvent(ItemDeletedEvent)
                .build()
        and: 'convert the parent item before deleting it'
        def payload = new WebhookPayloadModel()
        payload.setData(conversionService.convert(order, ORDER_IO))
        waitForProcessing()

        when: 'the transaction is committed'
        Transaction transaction = Transaction.current()
        transaction.begin()
        deleteOrder(order)
        transaction.commit()

        then: 'a delete event is sent to webhook'
        assertEventsSentToWebhooks([invocation(payload, DefaultEventType.DELETED, ORDER_IO)])
        and: 'the caches are cleaned'
        assertCleanCache(orderPk)
    }

    private static OrderModel createOrder() {
        IntegrationTestUtil.importImpEx(
                'INSERT_UPDATE Order; code[unique = true]; user(uid)        ; currency(isocode); date[dateformat=MM/dd/yyyy]',
                "                          ; $ORDER_CODE        ; $CUSTOMER_UID    ; $CURRENCY        ; 09/02/2021",
                'INSERT_UPDATE OrderEntry; order(code)[unique = true]; entryNumber[unique = true]; product(code); unit(code); quantity',
                "                        ; $ORDER_CODE               ; 0                         ; $PRODUCT     ; $UNIT     ; 1")
        IntegrationTestUtil.findAny(OrderModel, { it.code == ORDER_CODE }).orElse(null)
                .tap { LOG.info 'Created {}', it }
    }

    private static void deleteOrder(OrderModel order) {
        IntegrationTestUtil.importImpEx(
                'REMOVE Order;code[unique=true]',
                "                   ; $order.code")
    }

    private static void deleteOrderEntry() {
        IntegrationTestUtil.importImpEx(
                'REMOVE OrderEntry; order(code)[unique = true]; entryNumber[unique = true]',
                "                        ; $ORDER_CODE               ; 0                         ")
    }

    private static String passingFilterScript() {
        '''
        import de.hybris.platform.core.model.ItemModel
        import de.hybris.platform.webhookservices.filter.WebhookFilter
        import java.util.Optional
        
        class AllPassFilter implements WebhookFilter {
            @Override
            Optional<ItemModel> filter(ItemModel item) {
                return Optional.of(item)
            }
        }
        new AllPassFilter()
        '''
    }

    private static String blockingFilterScript() {
        '''
        import de.hybris.platform.core.model.ItemModel
        import de.hybris.platform.webhookservices.filter.WebhookFilter
        import java.util.Optional
        
        class AllOutFilter implements WebhookFilter {
            @Override
            Optional<ItemModel> filter(ItemModel item) {
                Optional.empty()
            }
        }
        new AllOutFilter()
        '''
    }

    private static ConsumedDestinationBuilder defaultDestinationBuilder(final String destinationId = DESTINATION) {
        consumedDestinationBuilder()
                .withId(destinationId)
                .withUrl('https://path/to/webhooks')
                .withDestinationTarget('webhookServices')
    }

    private static void waitForProcessing() {
        eventualCondition().within(WAIT_AFTER_CHANGES_SENT).retains { assert true }
    }

    private void assertNoEventSentToWebhook() {
        eventualCondition().within(REASONABLE_TIME).retains {
            assert mockOutboundServiceFacade.allInvocations.empty
        }
    }

    private static SyncParameters invocation(ItemModel item, EventType change, String io, String dest = DESTINATION) {
        SyncParameters.syncParametersBuilder()
                .withPayloadObject(item)
                .withDestinationId(dest)
                .withIntegrationObjectCode(io)
                .withSource(OutboundSource.WEBHOOKSERVICES)
                .withEventType(change)
                .build()
    }

    private void assertEventsSentToWebhooks(List<SyncParameters> expectedInvocations, long timeLimit = 10) {
        eventualCondition().within(REASONABLE_TIME).retains {
            eventualCondition().within(Duration.ofSeconds(timeLimit)).expect {
                assertInvocations(expectedInvocations, actualInvocations())
            }
        }
    }

    private void assertInvocations(List<SyncParameters> expectedInvocations, List<SyncParameters> actualInvocations) {
        assert actualInvocations.size() == expectedInvocations.size()
        expectedInvocations.eachWithIndex {
            SyncParameters expectedInvocation, int i -> assertInvocation(expectedInvocation, actualInvocations.get(i))
        }
    }

    private void assertInvocation(SyncParameters expectedInvocation, SyncParameters actualInvocation) {
        assert expectedInvocation == actualInvocation || areEqualInvocations(expectedInvocation, actualInvocation)
    }

    private boolean areEqualInvocations(SyncParameters expected, SyncParameters actual) {
        if (actual.getItem() instanceof WebhookPayloadModel) {
            def actualItem = actual.getItem() as WebhookPayloadModel
            def expectedItem = expected.getItem() as WebhookPayloadModel
            return new EqualsBuilder()
                    .append(actualItem.data, expectedItem.data)
                    .append(actual.integrationObjectCode, expected.integrationObjectCode)
                    .append(actual.destinationId, expected.destinationId)
                    .append(actual.source, expected.source)
                    .append(actual.eventType, expected.eventType)
                    .append(actual.integrationKey, expected.integrationKey)
                    .isEquals()
        }
        return false
    }

    private List<SyncParameters> actualInvocations() {
        return mockOutboundServiceFacade.allInvocations
                .toSorted { a, b -> a.eventType.type <=> b.eventType.type }
    }

    private void assertCleanCache(PK pk) {
        assert webhookCacheService.findItem(pk).empty
        assert webhookCacheService.findItemPayloads(pk).empty
    }

}
