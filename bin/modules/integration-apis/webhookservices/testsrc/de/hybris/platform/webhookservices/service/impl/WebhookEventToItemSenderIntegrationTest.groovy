/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.webhookservices.service.impl

import de.hybris.bootstrap.annotations.IntegrationTest
import de.hybris.platform.apiregistryservices.dto.EventSourceData
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
import de.hybris.platform.servicelayer.config.ConfigurationService
import de.hybris.platform.servicelayer.event.events.AbstractEvent
import de.hybris.platform.tx.AfterSaveEvent
import de.hybris.platform.webhookservices.WebhookConfigurationBuilder
import de.hybris.platform.webhookservices.event.ItemCreatedEvent
import de.hybris.platform.webhookservices.event.ItemSavedEvent
import de.hybris.platform.webhookservices.event.ItemUpdatedEvent
import de.hybris.platform.webhookservices.model.WebhookConfigurationModel
import de.hybris.platform.webhookservices.util.ToStringUtil
import de.hybris.platform.webhookservices.util.WebhookServicesEssentialData
import org.junit.ClassRule
import org.junit.Rule
import org.junit.Test
import spock.lang.AutoCleanup
import spock.lang.Issue
import spock.lang.PendingFeature
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
@Issue('https://jira.hybris.com/browse/STOUT-4552')
class WebhookEventToItemSenderIntegrationTest extends ServicelayerSpockSpecification {

    private static final def LOG = Log.getLogger WebhookEventToItemSenderIntegrationTest
    private static final def TEST_NAME = 'WebhookEventToItemSender'
    private static final def CUSTOMER_IO = "${TEST_NAME}_CustomerIO"
    private static final def CUSTOMER = TEST_NAME.toLowerCase()
    private static final def DESTINATION = "${TEST_NAME}_Destination"
    private static final def OTHER_DESTINATION = "${TEST_NAME}_Other_Destination"
    private static final def ORDER_CODE = "${TEST_NAME}_Order"
    private static final def PRODUCT = 'hammer'
    private static final def UNIT = "${TEST_NAME}-piece"
    private static final def CURRENCY = 'USD'
    private static final def REASONABLE_TIME = Duration.ofSeconds(6)
    private static final def WAIT_AFTER_CHANGES_SENT = Duration.ofSeconds(3)
    private static final def TIME_TO_WAIT = Duration.ofSeconds(15)
    private static final def ORDER_IO = "${TEST_NAME}_OrderIO"
    private static final def ORDER_WEBHOOK_DESTINATION = "${TEST_NAME}_Orders"
    private static final def CONSOLIDATION_KEY = 'webhookservices.event.consolidation.enabled'

    private OutboundServiceFacade originalOutboundServiceFacade
    private String defaultConsolidationValue

    @Resource(name = "webhookEventToItemSender")
    private WebhookEventToItemSender webhookEventSender

    @Shared
    @ClassRule
    ModuleEssentialData essentialData = WebhookServicesEssentialData.webhookServicesEssentialData()

    @Shared
    @ClassRule
    IntegrationObjectModelBuilder io = integrationObject().withCode(CUSTOMER_IO)
            .withItem(integrationObjectItem().withCode('Customer').root()
                    .withAttribute(integrationObjectItemAttribute('id').withQualifier('uid').unique())
                    .withAttribute(integrationObjectItemAttribute().withName('orders').withReturnItem('Order')))
            .withItem(integrationObjectItem().withCode('Order')
                    .withAttribute(integrationObjectItemAttribute().withName('code').unique())
                    .withAttribute(integrationObjectItemAttribute().withName('user').withReturnItem('Customer'))
                    .withAttribute(integrationObjectItemAttribute().withName('entries').withReturnItem('OrderEntry'))
                    .withAttribute(integrationObjectItemAttribute().withName('currency').withReturnItem('Currency')))
            .withItem(integrationObjectItem().withCode('OrderEntry')
                    .withAttribute(integrationObjectItemAttribute().withName('order').withReturnItem('Order').unique())
                    .withAttribute(integrationObjectItemAttribute().withName('entryNumber').unique()))
            .withItem(integrationObjectItem().withCode('Currency')
                    .withAttribute(integrationObjectItemAttribute('code').withQualifier('isocode').unique()))

    @Rule
    TestOutboundFacade mockOutboundServiceFacade = new TestOutboundFacade().respondWithCreated()
    @Resource
    ConfigurationService configurationService

    @AutoCleanup('cleanup')
    def webhookConfigurationBuilder = webhookConfiguration()
            .withIntegrationObject(CUSTOMER_IO)
            .withDestination(destinationBuilder())

    @AutoCleanup('cleanup')
    def scriptBuilder = ScriptModelBuilder.groovyScript()

    def setupSpec() {
        IntegrationTestUtil.importImpEx(
                'INSERT_UPDATE Catalog ; id[unique = true]',
                "                      ; $TEST_NAME",
                'INSERT_UPDATE CatalogVersion; version[unique = true]; catalog(id)[unique = true]',
                "                            ; IntegrationTest       ; $TEST_NAME",
                'INSERT_UPDATE Product; code[unique = true]; catalogVersion(version, catalog(id))[unique = true]',
                "                     ; $PRODUCT           ; IntegrationTest:$TEST_NAME",
                'INSERT_UPDATE Currency; isocode[unique = true]; symbol',
                "                      ; $CURRENCY             ; $TEST_NAME \$",
                'INSERT_UPDATE Unit; code[unique = true]; unitType',
                "                  ; $UNIT              ; $UNIT")
    }

    def cleanupSpec() {
        IntegrationTestUtil.remove(ProductModel) { it.code == PRODUCT }
        IntegrationTestUtil.remove(CurrencyModel) { it.isocode == CURRENCY }
        IntegrationTestUtil.remove(UnitModel) { it.code == UNIT }
        IntegrationTestUtil.remove(CatalogModel) { it.id == TEST_NAME }
    }

    def setup() {
        originalOutboundServiceFacade = webhookEventSender.outboundServiceFacade
        webhookEventSender.outboundServiceFacade = mockOutboundServiceFacade

        defaultConsolidationValue = configurationService.getConfiguration().getProperty CONSOLIDATION_KEY
        configurationService.getConfiguration().setProperty CONSOLIDATION_KEY, 'true'
    }

    def cleanup() {
        webhookEventSender.outboundServiceFacade = originalOutboundServiceFacade
        IntegrationTestUtil.remove(OrderModel) { it.code == ORDER_CODE }
        IntegrationTestUtil.remove(CustomerModel) { it.uid == CUSTOMER }
        configurationService.getConfiguration().setProperty CONSOLIDATION_KEY, defaultConsolidationValue
    }

    @Test
    def 'root item is sent to webhook after it is created'() {
        given: 'WebhookConfiguration exists'
        webhookConfigurationBuilder.build()

        when: 'a Customer created'
        def customer = createCustomer()

        then:
        eventualCondition()
                .within(TIME_TO_WAIT)
                .expect { sent([created(customer)]) }
    }

    @Test
    @Issue('https://cxjira.sap.com/browse/IAPI-4769')
    def 'no item is sent when webhook is not configured'() {
        given: 'there is no a webhook configuration'
        assert IntegrationTestUtil.findAll(WebhookConfigurationModel).empty

        when:
        createCustomer()

        then:
        noChangesSentToWebhooks()
    }

    @Test
    def 'no item is sent when the PK in the event is not found'() {
        given: 'WebhookConfiguration exists'
        webhookConfigurationBuilder.build()

        when:
        webhookEventSender.send eventForNonExistingItem()

        then: 'Customer does not exist so the facade is not called'
        noChangesSentToWebhooks()
    }

    @Test
    def 'no item is sent when a nested item that is not in the IO is changed'() {
        given: 'a Customer with Address exists before webhook is configured'
        def customer = createCustomer()
        createAddress(customer)
        waitForEventBeProcessedByListener()
        and: 'a webhook configuration is created for Customers'
        webhookConfigurationBuilder.build()

        when: 'the Address item that is not in the IO is changed'
        updateAddressCompany(customer)

        then: 'the change is not sent to the webhook'
        noChangesSentToWebhooks()
    }

    @Test
    def 'no item is sent when a nested item that has no path to the root in the IO is changed'() {
        given: 'a Customer with Order'
        def customer = createCustomer()
        createOrderWithoutEntries(customer)
        waitForEventBeProcessedByListener()
        and: 'a webhook configuration is created for Customers'
        webhookConfigurationBuilder.build()

        when: 'the Currency that is nested into the Order within the IO is changed'
        updateContextCurrencyConversionRate()

        then: 'the change is not sent to the webhook'
        noChangesSentToWebhooks()
    }

    @Test
    def 'no item is sent when the root item is deleted'() {
        given: 'a root item already exist'
        def customer = createCustomer()
        waitForEventBeProcessedByListener()
        and: 'later webhook configuration is created'
        webhookConfigurationBuilder.build()

        when: 'the item is deleted'
        deleteCustomer(customer)

        then: 'the web hook is not notified'
        noChangesSentToWebhooks()
    }

    @Test
    def 'only root item is sent when it is created with nested items'() {
        given: 'a webhook configuration is created for the customer IO'
        webhookConfigurationBuilder.build()

        when: 'a Customer is created with Order and OrderEntries'
        def customer = createCustomer()
        createOrderWithEntry(customer)

        then: 'root item change is sent'
        changesSentToWebhooksAre created(customer)
        and: 'no other changes have been sent'
        noOtherChangesSentBesides([created(customer)])
    }

    @Test
    def 'only root item update is sent when nested item(s) are created in the existing item'() {
        given: 'a Customer exists before webhook is configured'
        def customer = createCustomer()
        waitForEventBeProcessedByListener()
        and: 'a webhook configuration is created for Customers'
        webhookConfigurationBuilder.build()

        when: 'a child item is added to the root item'
        def order = createOrderWithoutEntries(customer)
        and: 'a grandchild item is added to the root item'
        createOrderEntry(order)

        then: 'root item change is sent'
        changesSentToWebhooksAre updated(customer)
        and: 'no other changes have been sent'
        noOtherChangesSentBesides([updated(customer)])
    }

    @Test
    def 'only root item update is sent when nested items updated'() {
        given: 'a root item with nested items exists before the hook is configured'
        def customer = createCustomer()
        def order = createOrderWithEntry(customer)
        waitForEventBeProcessedByListener()
        and: 'a webhook configuration is then created'
        webhookConfigurationBuilder.build()

        when: 'nested items got updated'
        updateOrderEntriesQuantities(order, 10)

        then: 'root item change is sent'
        changesSentToWebhooksAre updated(customer)
        and: 'no other changes have been sent'
        noOtherChangesSentBesides([updated(customer)])
    }

    @Test
    def 'only root item update is sent when nested item updated with attribute that is not in the IO'() {
        given: 'a root item with nested items exists before the hook is configured'
        def customer = createCustomer()
        def order = createOrderWithoutEntries(customer)
        waitForEventBeProcessedByListener()
        and: 'a webhook configuration is then created'
        webhookConfigurationBuilder.build()

        when: 'nested items got updated'
        updateOrderDescription(order, 'updates attribute that is not in the IO')

        then: 'only one update item change is sent'
        changesSentToWebhooksAre updated(customer)
        and: 'no other changes have been sent'
        noOtherChangesSentBesides([updated(customer)])
    }

    @Test
    @PendingFeature(reason = 'CXEC-5016')
    def 'create and update item changes are sent when an item was created and then child is created right away and 2 webhook configurations exist for different destinations'() {
        given: 'a webhook configuration exists for ItemCreatedEvent pointing to destination1'
        createWebhookConfiguration(webhookConfigurationBuilder, ItemCreatedEvent, DESTINATION)

        and: 'a webhook configuration exists for ItemUpdatedEvent pointing to destination2'
        createWebhookConfiguration(webhookConfigurationBuilder, ItemUpdatedEvent, OTHER_DESTINATION)

        when: 'the root item is created'
        def customer = createCustomer()
        and: 'wait for 1 second'
        waitForEventBeProcessedByListener(Duration.ofSeconds(1))
        and: 'a child of the root item is created right away'
        createOrderWithoutEntries(customer)

        then: 'create and update item changes are sent to their corresponding webhook configuration destinations'
        changesSentToWebhooksAre created(customer, DESTINATION), updated(customer, OTHER_DESTINATION)
        and: 'no other changes have been sent'
        noOtherChangesSentBesides([created(customer, DESTINATION), updated(customer, OTHER_DESTINATION)])
    }

    @Test
    def 'only one item update is sent when an item was subsequently updated multiple times'() {
        given: 'a root item already exists'
        def customer = createCustomer()
        waitForEventBeProcessedByListener()
        and: 'a webhook configuration exists'
        webhookConfigurationBuilder.build()

        when: 'the root item is updated subsequently several times'
        updateCustomerName(customer, 'first name')
        updateCustomerName(customer, 'second name')
        updateCustomerName(customer, 'last name')

        then: 'only one update item change is sent'
        changesSentToWebhooksAre updated(customer)
        and: 'no other changes have been sent'
        noOtherChangesSentBesides([updated(customer)])
    }

    @Test
    def 'sends create changes to all webhook configurations applicable'() {
        given: 'A webhook config for the customer IO exists'
        webhookConfigurationBuilder.build()
        and: 'a webhook config for the order IO is created'
        createWebhookConfigurationForChild(webhookConfigurationBuilder)

        when: 'a Customer and an Order is created'
        def customer = createCustomer()
        def order = createOrderWithoutEntries(customer)

        then: 'create item change is sent for all webhookconfigurations'
        changesSentToWebhooksAre([created(customer), created(order, ORDER_WEBHOOK_DESTINATION, ORDER_IO)], TIME_TO_WAIT)
        and: 'no other changes have been sent'
        noOtherChangesSentBesides([created(customer), created(order, ORDER_WEBHOOK_DESTINATION, ORDER_IO)])
    }

    @Test
    def 'sends update changes to all webhook configurations applicable'() {
        given: 'a Customer with Order exists'
        def customer = createCustomer()
        def order = createOrderWithoutEntries(customer)
        waitForEventBeProcessedByListener()
        and: 'there is a webhook config for the customer IO'
        webhookConfigurationBuilder.withIntegrationObject(CUSTOMER_IO).build()
        and: 'there is a webhook config for the order IO'
        createWebhookConfigurationForChild(webhookConfigurationBuilder)

        when: 'the Order is updated'
        updateOrderDescription(order, 'triggers customer and order IO notifications')

        then: 'both update item changes are sent'
        changesSentToWebhooksAre updated(customer), updated(order, ORDER_WEBHOOK_DESTINATION, ORDER_IO)
        and:
        noOtherChangesSentBesides([updated(customer), updated(order, ORDER_WEBHOOK_DESTINATION, ORDER_IO)])
    }

    @Test
    def 'sends update change for parent and create change for child when children are created for an existing parent'() {
        given: 'a Customer exists'
        def customer = createCustomer()
        waitForEventBeProcessedByListener()
        and: 'then a webhook config for the customer IO is created'
        webhookConfigurationBuilder.build()
        and: 'a webhook config for the order IO is created'
        createWebhookConfigurationForChild(webhookConfigurationBuilder)

        when: 'an Order is created'
        def order = createOrderWithoutEntries(customer)

        then: 'a notification is sent for both webhook configurations'
        changesSentToWebhooksAre([updated(customer), created(order, ORDER_WEBHOOK_DESTINATION, ORDER_IO)], TIME_TO_WAIT)
        and:
        noOtherChangesSentBesides([updated(customer), created(order, ORDER_WEBHOOK_DESTINATION, ORDER_IO)])
    }

    @Test
    def 'sends only create changes to root webhook configuration when a parent is created with children'() {
        given: 'WebhookConfiguration exists for a parent Customer (rootItemType) for UpdatedEvent & SavedEvent(with Create type)'
        webhookConfigurationBuilder.withEvent(ItemUpdatedEvent).build()
        webhookConfigurationBuilder.withEvent(ItemSavedEvent).build()
        when: 'When the parent Customer is Created (along with children)'
        def customer = createCustomer()
        createOrderWithoutEntries(customer)

        then: 'Then a create change will be sent to the corresponding WebhookConfiguration'
        changesSentToWebhooksAre created(customer)
        and:
        noOtherChangesSentBesides([created(customer)])
    }

    @Test
    @Issue('https://cxjira.sap.com/browse/IAPI-4619')
    def 'webhook filter passes matching items through to the webhook'() {
        given: 'a filter that allows any item through'
        def scriptCode = 'allPassingFilter'
        scriptBuilder.withCode(scriptCode).withContent(passingFilterScript()).build()
        and: 'a webhook configuration with the filter'
        webhookConfigurationBuilder.withScriptFilter(scriptCode).build()

        when: 'a qualifying item is created'
        def customer = createCustomer()

        then: 'the web hook is notified for the item passing through the filter'
        changesSentToWebhooksAre([created(customer)], TIME_TO_WAIT)
        and:
        noOtherChangesSentBesides([created(customer)])
    }

    @Test
    @Issue('https://cxjira.sap.com/browse/IAPI-4619')
    def 'webhook filter blocks a non-matching item from sending it to the webhook'() {
        given: 'a filter that blocks any item from being notified'
        def scriptCode = 'blockingFilter'
        scriptBuilder.withCode(scriptCode).withContent(blockingFilterScript()).build()
        and: 'a webhook configuration with the filter'
        webhookConfigurationBuilder.withScriptFilter(scriptCode).build()

        when: 'an item is created'
        createCustomer()

        then: 'the filtered out item is not sent to the webhook'
        noChangesSentToWebhooks()
    }

    @Test
    def 'when event consolidation is #consolidationFlag and all event types are configured we expect #expectedEventTypes update events sent outbound'() {
        given:
        configurationService.getConfiguration().setProperty('webhookservices.event.consolidation.enabled', consolidationFlag)
        and:
        createDefaultWebhookConfigurationsForAllEventTypes()

        when: 'Root item is created'
        def customer = createCustomer()
        and:
        waitForEventBeProcessedByListener(Duration.ofSeconds(1))
        and: 'customer is updated'
        updateCustomerName(customer, 'updatedCustomer')

        then:
        def expectedEvents = expectedEventTypes.collect { event(customer, it as DefaultEventType) }
        changesSentToWebhooksAre(expectedEvents)
        noOtherChangesSentBesides(expectedEvents)

        where:
        consolidationFlag | expectedEventTypes
        'true'            | [DefaultEventType.CREATED, DefaultEventType.CREATED]
        'false'           | [DefaultEventType.CREATED, DefaultEventType.CREATED, DefaultEventType.UPDATED, DefaultEventType.UPDATED]
    }

    @Test
    def 'when the parent is created and then updated in next aggregator cycle, expect create and update events sent outbound even when consolidation is enabled'() {
        given:
        createWebhookConfiguration(webhookConfigurationBuilder, ItemSavedEvent)

        when:
        def customer = createCustomer()
        and:
        waitForEventBeProcessedByListener(Duration.ofSeconds(3))
        and:
        updateCustomerName(customer, 'updatedCustomer')

        then:
        changesSentToWebhooksAre([created(customer, DESTINATION), updated(customer, DESTINATION)], TIME_TO_WAIT)
        noOtherChangesSentBesides([created(customer, DESTINATION), updated(customer, DESTINATION)])
    }

    @Test
    def 'only single parent update event is emitted to update and saved webhook configuration when parent update event is followed by another parent update event within same aggregator cycle'() {
        given: 'customer exists'
        def customer = createCustomer()
        waitForEventBeProcessedByListener()
        and:
        createDefaultWebhookConfigurationsForAllEventTypes()

        when:
        updateCustomerName(customer, 'firstUpdate')
        and:
        updateCustomerName(customer, 'secondUpdate')
        and:
        waitForEventBeProcessedByListener(Duration.ofSeconds(1))

        then:
        def expectedEvents = [updated(customer, DESTINATION), updated(customer, DESTINATION)]
        changesSentToWebhooksAre(expectedEvents, TIME_TO_WAIT)
        noOtherChangesSentBesides(expectedEvents)
    }

    @Test
    def 'Two unmodified update events are emitted for each  update and saved webhook configuration when update event is followed by another update event outside configured time frame'() {
        given: 'customer exists'
        def customer = createCustomer()
        waitForEventBeProcessedByListener()
        and:
        createDefaultWebhookConfigurationsForAllEventTypes()

        when:
        updateCustomerName(customer, 'firstUpdate')
        and:
        waitForEventBeProcessedByListener(Duration.ofSeconds(3))
        and:
        updateCustomerName(customer, 'secondUpdate')

        then:
        def expectedEvents = [updated(customer, DESTINATION), updated(customer, DESTINATION), updated(customer, DESTINATION), updated(customer, DESTINATION)]
        changesSentToWebhooksAre(expectedEvents)
        noOtherChangesSentBesides(expectedEvents)
    }

    @Test
    def 'parent events are squashed down and child original child events  are emitted to webhook config when  parent update event is  followed by child update event within configured time'() {
        given: 'customer and an order for that customer exist'
        def customer = createCustomer()
        def order = createOrderWithoutEntries(customer)
        waitForEventBeProcessedByListener()

        and: 'a configuration for customer exists'
        createWebhookConfiguration(webhookConfigurationBuilder, ItemSavedEvent, DESTINATION)
        and: 'a configuration for order exists'
        createWebhookConfigurationForChild(webhookConfigurationBuilder)

        when:
        updateCustomerName(customer, 'first update')
        and:
        updateOrderDescription(order, 'triggers customer and order IO notifications')
        waitForEventBeProcessedByListener()

        then:
        def expectedEvents = [updated(customer), updated(order, ORDER_WEBHOOK_DESTINATION, ORDER_IO)]
        and:
        changesSentToWebhooksAre(expectedEvents, TIME_TO_WAIT)
        noOtherChangesSentBesides(expectedEvents)
    }

    @Test
    def 'parent events are squashed down and child original child events  are emitted to webhook config when parent update event is  followed by child create event within configured time'() {
        given: 'customer and an order for that customer exist'
        def customer = createCustomer()
        waitForEventBeProcessedByListener()
        and: 'a configuration for customer exists'
        createWebhookConfiguration(webhookConfigurationBuilder, ItemSavedEvent, DESTINATION)
        and: 'a configuration for order exists'
        createWebhookConfigurationForChild(webhookConfigurationBuilder)

        when:
        updateCustomerName(customer, 'first update')
        and:
        def order = createOrderWithoutEntries(customer)
        and:
        waitForEventBeProcessedByListener()

        then:
        def expectedEvents = [updated(customer), created(order, ORDER_WEBHOOK_DESTINATION, ORDER_IO)]
        and:
        changesSentToWebhooksAre(expectedEvents, TIME_TO_WAIT)
        noOtherChangesSentBesides(expectedEvents)
    }

    private static createWebhookConfigurationForChild(final WebhookConfigurationBuilder builder) {
        builder.withIntegrationObject(integrationObject().withCode(ORDER_IO)
                .withItem(integrationObjectItem().withCode('Order').root()
                        .withAttribute(integrationObjectItemAttribute().withName('code').unique())))
        createWebhookConfiguration(builder, ItemSavedEvent, ORDER_WEBHOOK_DESTINATION)
    }

    private static SyncParameters event(ItemModel item, DefaultEventType eventType) {
        assert eventType == DefaultEventType.CREATED || eventType == DefaultEventType.UPDATED
        return DefaultEventType.CREATED == eventType ? created(item, DESTINATION) : updated(item, DESTINATION)
    }

    private createDefaultWebhookConfigurationsForAllEventTypes() {
        createWebhookConfiguration(webhookConfigurationBuilder, ItemSavedEvent, DESTINATION)
        createWebhookConfiguration(webhookConfigurationBuilder, ItemCreatedEvent, DESTINATION)
        createWebhookConfiguration(webhookConfigurationBuilder, ItemUpdatedEvent, DESTINATION)
    }

    private static createWebhookConfiguration(final WebhookConfigurationBuilder builder, Class<AbstractEvent> eventType, String destinationId = DESTINATION) {
        builder.withEvent(eventType).withDestination(destinationBuilder().withId(destinationId)).build()
    }

    private static ConsumedDestinationBuilder destinationBuilder(final String destinationId = DESTINATION) {
        consumedDestinationBuilder()
                .withId(destinationId)
                .withUrl('https://path/to/webhooks')
                .withDestinationTarget('webhookServices')// created in essential data
    }

    private static CustomerModel createCustomer(String uid = CUSTOMER) {
        IntegrationTestUtil.importImpEx(
                'INSERT Customer; uid[unique = true]',
                "               ; $uid")
        IntegrationTestUtil.findAny(CustomerModel, { it.uid == uid }).orElse(null)
                .tap { LOG.info 'Created {}', it }
    }

    private static void updateCustomerName(CustomerModel customer, String name) {
        IntegrationTestUtil.importImpEx(
                'UPDATE Customer; pk[unique = true]; name',
                "               ; $customer.pk     ; $name")
    }

    private static void deleteCustomer(CustomerModel customer) {
        IntegrationTestUtil.importImpEx(
                'REMOVE Customer; pk[unique = true]',
                "               ; $customer.pk")
    }

    private static void createAddress(CustomerModel customer) {
        IntegrationTestUtil.importImpEx(
                'INSERT Address; owner[unique = true]',
                "              ; $customer.pk")
    }

    private static void updateAddressCompany(CustomerModel customer) {
        IntegrationTestUtil.importImpEx(
                'UPDATE Address; owner[unique = true]; company',
                "              ; $customer.pk        ; SAP")
    }

    private static OrderModel createOrderWithoutEntries(CustomerModel customer) {
        IntegrationTestUtil.importImpEx(
                'INSERT_UPDATE Order; code[unique = true]; user        ; currency(isocode); date[dateformat=MM/dd/yyyy]',
                "                   ; $ORDER_CODE        ; $customer.pk; $CURRENCY        ; 09/02/2021")
        IntegrationTestUtil.findAny(OrderModel, { it.code == ORDER_CODE }).orElse(null)
                .tap { LOG.info 'Created {}', it }
    }

    private static OrderModel createOrderWithEntry(CustomerModel customer) {
        IntegrationTestUtil.importImpEx(
                'INSERT_UPDATE Order; code[unique = true]; &orderPk; user        ; currency(isocode); date[dateformat=MM/dd/yyyy]',
                "                   ; $ORDER_CODE        ; theOrder; $customer.pk; $CURRENCY        ; 09/02/2021",
                'INSERT_UPDATE OrderEntry; order(&orderPk)[unique = true]; entryNumber[unique = true]; product(code); unit(code); quantity',
                "                        ; theOrder                      ; 1                         ; $PRODUCT     ; $UNIT     ; 1")
        IntegrationTestUtil.findAny(OrderModel, { it.code == ORDER_CODE }).orElse(null)
                .tap { LOG.info 'Created {}', it }
    }

    private static void createOrderEntry(OrderModel order) {
        IntegrationTestUtil.importImpEx(
                'INSERT_UPDATE OrderEntry; order[unique = true]; product(code); unit(code); quantity',
                "                        ; $order.pk           ; $PRODUCT     ; $UNIT     ; 1")
    }

    private static void updateContextCurrencyConversionRate() {
        IntegrationTestUtil.importImpEx(
                'UPDATE Currency; isocode[unique = true]; conversion',
                "               ; $CURRENCY             ; 1.2")
    }

    private static void updateOrderDescription(OrderModel order, String desc) {
        IntegrationTestUtil.importImpEx(
                'UPDATE Order; pk[unique = true]; description',
                "            ; $order.pk        ; $desc")
    }

    private static void updateOrderEntriesQuantities(OrderModel order, int qty) {
        IntegrationTestUtil.importImpEx(
                'UPDATE OrderEntry; order[unique = true]; quantity',
                "                 ; $order.pk           ; $qty")
    }

    private static EventSourceData eventForNonExistingItem() {
        int usersTypeCode = 4
        def afterSaveEvent = new AfterSaveEvent(PK.fromLong(0), usersTypeCode)
        def webhookEvent = new ItemSavedEvent(afterSaveEvent)
        new EventSourceData(event: webhookEvent)
    }

    private static SyncParameters created(ItemModel item, String dest = DESTINATION, String io = CUSTOMER_IO) {
        change(item, DefaultEventType.CREATED, dest, io)
    }

    private static SyncParameters updated(ItemModel item, String dest = DESTINATION, String io = CUSTOMER_IO) {
        change(item, DefaultEventType.UPDATED, dest, io)
    }

    private static SyncParameters change(ItemModel item, EventType change, String dest, String io) {
        SyncParameters.syncParametersBuilder()
                .withPayloadObject(item)
                .withDestinationId(dest)
                .withIntegrationObjectCode(io)
                .withSource(OutboundSource.WEBHOOKSERVICES)
                .withEventType(change)
                .build()
    }

    private static void waitForEventBeProcessedByListener(final Duration waitTime = WAIT_AFTER_CHANGES_SENT) {
        eventualCondition().within(waitTime).retains { assert true }
    }

    private void noChangesSentToWebhooks() {
        eventualCondition().within(REASONABLE_TIME).retains {
            assert mockOutboundServiceFacade.allInvocations.empty
        }
    }

    private void changesSentToWebhooksAre(List<SyncParameters> changes, Duration duration = Duration.ofSeconds(10)) {
        eventualCondition().within(duration).expect { sent changes }
    }

    private void changesSentToWebhooksAre(SyncParameters... changes) {
        eventualCondition().expect { sent changes as List }
    }

    private void noOtherChangesSentBesides(List<SyncParameters> changes) {
        eventualCondition().within(WAIT_AFTER_CHANGES_SENT).retains { sent changes }
    }

    private void sent(List<SyncParameters> expected) {
        def actual = allInvocations()
        assert actual.containsAll(expected)
        assert actual.size() == expected.size()
    }

    private List<SyncParameters> allInvocations() {
        def invocations = new ArrayList<>(mockOutboundServiceFacade.allInvocations)
        LOG.debug 'Actual webhook invocations: {}', toString(invocations)
        // we get anonymous customer update for some reason, which messes up the test results.
        invocations.tap {
            removeIf { it.item instanceof CustomerModel && it.item.uid == 'anonymous' }
        }
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

    private static List toString(List<SyncParameters> params) {
        params.collect {
            "SyncParameters{item='${ToStringUtil.toString(it.item)}'," +
                    " io='$it.integrationObjectCode', destination='$it.destinationId', source='$it.source'," +
                    " eventType='$it.eventType'}"
        }
    }
}
