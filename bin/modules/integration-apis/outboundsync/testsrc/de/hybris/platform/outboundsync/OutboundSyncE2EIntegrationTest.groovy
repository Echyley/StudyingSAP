/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.outboundsync

import de.hybris.bootstrap.annotations.IntegrationTest
import de.hybris.platform.catalog.model.CatalogVersionModel
import de.hybris.platform.catalog.model.KeywordModel
import de.hybris.platform.core.model.product.ProductModel
import de.hybris.platform.core.model.product.UnitModel
import de.hybris.platform.core.model.user.CustomerModel
import de.hybris.platform.core.model.user.EmployeeModel
import de.hybris.platform.cronjob.enums.CronJobResult
import de.hybris.platform.cronjob.model.CronJobModel
import de.hybris.platform.integrationservices.util.IntegrationTestUtil
import de.hybris.platform.outboundservices.facade.OutboundServiceFacade
import de.hybris.platform.outboundservices.util.TestOutboundFacade
import de.hybris.platform.outboundsync.activator.OutboundItemConsumer
import de.hybris.platform.outboundsync.activator.impl.DefaultOutboundSyncService
import de.hybris.platform.outboundsync.model.OutboundChannelConfigurationModel
import de.hybris.platform.outboundsync.util.OutboundSyncEssentialData
import de.hybris.platform.servicelayer.ServicelayerSpockSpecification
import de.hybris.platform.servicelayer.cronjob.CronJobService
import org.junit.ClassRule
import org.junit.Rule
import org.junit.Test
import spock.lang.AutoCleanup
import spock.lang.Shared

import javax.annotation.Resource

import static de.hybris.platform.integrationservices.IntegrationObjectItemAttributeModelBuilder.integrationObjectItemAttribute
import static de.hybris.platform.integrationservices.IntegrationObjectItemModelBuilder.integrationObjectItem
import static de.hybris.platform.integrationservices.IntegrationObjectModelBuilder.integrationObject
import static de.hybris.platform.integrationservices.util.IntegrationTestUtil.condition
import static de.hybris.platform.outboundservices.ConsumedDestinationBuilder.consumedDestinationBuilder
import static de.hybris.platform.outboundsync.OutboundChannelConfigurationBuilder.outboundChannelConfigurationBuilder
import static de.hybris.platform.outboundsync.OutboundSyncStreamConfigurationBuilder.outboundSyncStreamConfigurationBuilder

@IntegrationTest
/*
 Since outbound sync is multi-threaded, using ServiceLayerTransactionalSpockSpecification
 causes the sync method not to find the item. With the sync method running in a different thread,
 it is outside the transaction of this test thread.
 */
class OutboundSyncE2EIntegrationTest extends ServicelayerSpockSpecification {
    private static final String TEST_NAME = "OutboundSyncE2E"
    private static final String PRODUCT_IO = "${TEST_NAME}_OutboundProductIO"
    private static final String CUSTOMER_IO = "${TEST_NAME}_OutboundCustomerIO"
    private static final String UNIT = "${TEST_NAME}_Unit"
    private static final String CATALOG = "${TEST_NAME}_Catalog"
    private static final String USER = "${TEST_NAME}_User"
    private static final String DESTINATION_ID = "${TEST_NAME}_ConsumedDestination"
    private static final String CHANNEL_CODE = "${TEST_NAME}_OutboundChannelConfiguration"

    @Resource
    private CronJobService cronJobService
    @Resource(name = 'outboundSyncService')
    private DefaultOutboundSyncService outboundSyncService
    private OutboundServiceFacade outboundServiceFacade
    private OutboundItemConsumer outboundItemConsumer

    @Shared
    @ClassRule
    OutboundSyncEssentialData essentialData = OutboundSyncEssentialData.outboundSyncEssentialData()

    @Rule
    TestOutboundFacade testOutboundFacade = new TestOutboundFacade().respondWithCreated()
    @Rule
    TestOutboundItemConsumer testOutboundItemConsumer = new TestOutboundItemConsumer()
    @AutoCleanup('cleanup')
    def outboundChannelBuilder = outboundChannelConfigurationBuilder()
            .withCode(CHANNEL_CODE)
            .withConsumedDestination consumedDestinationBuilder().withId(DESTINATION_ID)
    @AutoCleanup('cleanup')
    def customerIO = integrationObject().withCode(CUSTOMER_IO)
    @AutoCleanup('cleanup')
    def productIO = integrationObject().withCode(PRODUCT_IO)
            .withItem(integrationObjectItem().withCode('Catalog')
                    .withAttribute(integrationObjectItemAttribute().withName('id')))
            .withItem(integrationObjectItem().withCode('CatalogVersion')
                    .withAttribute(integrationObjectItemAttribute().withName('version'))
                    .withAttribute(integrationObjectItemAttribute().withName('catalog').withReturnItem('Catalog')))
    @AutoCleanup('cleanup')
    def changeDetector = outboundSyncStreamConfigurationBuilder()
            .withOutboundChannelCode(CHANNEL_CODE)

    CatalogVersionModel catalogVersion
    CronJobModel cronJob

    def setup() {
        cronJob = essentialData.outboundCronJob()
        catalogVersion = IntegrationTestUtil.importCatalogVersion('ExistingCVersion', CATALOG, true)

        outboundServiceFacade = outboundSyncService.outboundServiceFacade
        outboundSyncService.outboundServiceFacade = testOutboundFacade

        outboundItemConsumer = outboundSyncService.outboundItemConsumer
        outboundSyncService.outboundItemConsumer = testOutboundItemConsumer
    }

    def cleanup() {
        outboundSyncService.outboundServiceFacade = outboundServiceFacade
        outboundSyncService.outboundItemConsumer = outboundItemConsumer
    }

    @Test
    def "no updates sent when no updates to Products"() {
        given: "an IntegrationObject for Product"
        productIO
                .withItem(integrationObjectItem().withCode('Product').root()
                        .withAttribute(integrationObjectItemAttribute('code'))
                        .withAttribute(integrationObjectItemAttribute('catalogVersion').withReturnItem('CatalogVersion')))
        and: "Outbound sync channel listens for Product changes"
        outboundChannelBuilder.withIntegrationObject(productIO).build()
        changeDetector.withItemType('Product').build()

        when: "no changes were made to a Product"
        cronJobService.performCronJob(cronJob, true)

        then: "the job sent no updates"
        condition().eventually {
            assert cronJob.result == CronJobResult.SUCCESS
            assert testOutboundFacade.invocations() == 0
            assert testOutboundItemConsumer.invocations() == 0
        }

        cleanup:
        IntegrationTestUtil.remove OutboundChannelConfigurationModel, { occ -> occ.integrationObject.code == PRODUCT_IO }
    }

    @Test
    def "no updates sent when root item cannot be derived from the changed item model"() {
        given: 'IO for Product has a nested entity for Unit that does not refer batck to Product'
        productIO
                .withItem(integrationObjectItem().withCode('Unit')
                        .withAttribute(integrationObjectItemAttribute('code')))
                .withItem(integrationObjectItem().withCode('Product').root()
                        .withAttribute(integrationObjectItemAttribute('code'))
                        .withAttribute(integrationObjectItemAttribute('unit').withReturnItem('Unit'))
                        .withAttribute(integrationObjectItemAttribute('catalogVersion').withReturnItem('CatalogVersion')))
        and: 'product with unit is created'
        def productCode = "prod1"
        IntegrationTestUtil.importImpEx(
                'INSERT_UPDATE Unit ; code[unique = true]; unitType',
                "                   ; $UNIT              ; package",
                'INSERT_UPDATE Product ; code[unique = true] ; unit(code) ; catalogVersion',
                "                      ; $productCode        ; $UNIT      ; $catalogVersion.pk")
        and: 'Outbound sync channel listens for Unit and Product changes after they created'
        outboundChannelBuilder.withIntegrationObject(productIO).build()
        changeDetector.withItemType('Product').build()
        changeDetector.withItemType('Unit').build()
        and: 'the unit is changed'
        IntegrationTestUtil.importImpEx(
                'UPDATE Unit ; code[unique = true] ; name[lang = en]',
                "            ; $UNIT               ; Box")

        when:
        cronJobService.performCronJob(cronJob, true)

        then:
        condition().eventually {
            assert testOutboundFacade.invocations() == 0
            assert testOutboundItemConsumer.invocations() == 0
        }

        cleanup:
        IntegrationTestUtil.remove OutboundChannelConfigurationModel, { occ -> occ.integrationObject.code == PRODUCT_IO }
        IntegrationTestUtil.remove ProductModel, { it.code == productCode }
        IntegrationTestUtil.remove UnitModel, { it.code == UNIT }
    }

    @Test
    def "no updates sent when changed child item is not present in the IntegrationObject model"() {
        given: "integration object for Customer is defined without Address"
        customerIO
                .withItem(integrationObjectItem().withCode('Customer').root()
                        .withAttribute(integrationObjectItemAttribute().withName('uid')))
        and: "a Customer is created with Address"
        IntegrationTestUtil.importImpEx(
                'INSERT_UPDATE Customer; uid[unique = true]; defaultPaymentAddress( &addrID )',
                "                      ; user123           ; theAddress",
                'INSERT_UPDATE Address; &addrID   ; email[unique = true] ; owner(Customer.uid); company',
                "                     ; theAddress; user123@some.net     ; user123              ; hybris")
        and: "Outbound sync channel listens for Customer and Address changes after they were created"
        outboundChannelBuilder.withIntegrationObject(customerIO).build()
        changeDetector.withItemType('Customer').build()
        changeDetector.withItemType('Address').build()
        and: "the Address has changed"
        IntegrationTestUtil.importImpEx(
                'UPDATE Address; email[unique = true] ; company',
                "                     ; user123@some.net     ; SAP")

        when: "the sync job runs"
        cronJobService.performCronJob(cronJob, true)

        then: "no changes sent"
        condition().eventually {
            // No changes sent to the destination(s)
            assert testOutboundFacade.invocations() == 0
            assert testOutboundItemConsumer.invocations() == 0
        }

        cleanup:
        IntegrationTestUtil.remove OutboundChannelConfigurationModel, { occ -> occ.integrationObject.code == CUSTOMER_IO }
        IntegrationTestUtil.removeSafely CustomerModel, { it.uid == 'user123' }
    }

    @Test
    def "no updates sent when root item derived from the changed child is not present in the IntegrationObject model"() {
        given: "integration object for Customer and Address"
        customerIO
                .withItem(integrationObjectItem().withCode('Address')
                        .withAttribute(integrationObjectItemAttribute().withName('email').unique()))
                .withItem(integrationObjectItem().withCode('Customer').root()
                        .withAttribute(integrationObjectItemAttribute().withName('uid'))
                        .withAttribute(integrationObjectItemAttribute('paymentAddress').withQualifier('defaultPaymentAddress')
                                .withReturnItem('Address')))
        and: "Outbound sync channel listens for Customer and Address changes"
        outboundChannelBuilder.withIntegrationObject(customerIO).build()
        changeDetector.withItemType('Customer').build()
        changeDetector.withItemType('Address').build()
        and: "an Employee (not a Customer) is created with Address"
        IntegrationTestUtil.importImpEx(
                'INSERT_UPDATE Employee; uid[unique = true]; defaultPaymentAddress( &addrID )',
                "                      ; $USER             ; theAddress",
                'INSERT_UPDATE Address; &addrID   ; owner(Employee.uid); email[unique = true]',
                "                     ; theAddress; $USER              ; a.b@some.net")

        when:
        cronJobService.performCronJob(cronJob, true)

        then: "the job does not send updates because Employee is not present in the IntegrationObject model"
        condition().eventually {
            // No changes sent to the destination(s)
            assert testOutboundFacade.invocations() == 0
            assert testOutboundItemConsumer.invocations() == 0
        }

        cleanup:
        IntegrationTestUtil.remove OutboundChannelConfigurationModel, { occ -> occ.integrationObject.code == CUSTOMER_IO }
        IntegrationTestUtil.removeSafely EmployeeModel, { it.uid == USER }
    }

    @Test
    def "sends update only for Product deltas when Product and its child has changed"() {
        given: "IntegrationObject is defined for Product root item"
        productIO
                .withItem(integrationObjectItem().withCode('Product').root()
                        .withAttribute(integrationObjectItemAttribute('code'))
                        .withAttribute(integrationObjectItemAttribute('catalogVersion').withReturnItem('CatalogVersion')))
        and: 'Outbound sync channel listens for Product and CatalogVersion changes'
        def channel = outboundChannelBuilder.withIntegrationObject(productIO).build()
        changeDetector.withItemType('Product').build()
        changeDetector.withItemType('CatalogVersion').build()
        and: "Product and the CatalogVersion have changed"
        def newCatalogVersion = IntegrationTestUtil.importCatalogVersion('NewCVersion', CATALOG, true)
        def productCode = 'prod1'
        IntegrationTestUtil.importImpEx(
                "INSERT_UPDATE Product ; code[unique = true] ; catalogVersion",
                "                      ; $productCode       ; $newCatalogVersion.pk")

        when:
        cronJobService.performCronJob(cronJob, true)

        then:
        condition().eventually {
            assert testOutboundFacade.invocations() == 1
            assert testOutboundFacade.itemsFromInvocationsTo(channel.destination, PRODUCT_IO)
                    .collect({ it.itemtype }) == ['Product']
            assert testOutboundItemConsumer.invocations() == 1
        }

        cleanup:
        IntegrationTestUtil.remove OutboundChannelConfigurationModel, { occ -> occ.integrationObject.code == PRODUCT_IO }
        IntegrationTestUtil.remove ProductModel, { it.code == productCode }
    }

    @Test
    def "sends update only for root item(s) when their child item(s) changed"() {
        given: "integration object for Product is defined with Keyword child"
        productIO
                .withItem(integrationObjectItem().withCode('Keyword')
                        .withAttribute(integrationObjectItemAttribute().withName('keyword').unique())
                        .withAttribute(integrationObjectItemAttribute('products').withReturnItem('Product')))
                .withItem(integrationObjectItem().withCode('Product').root()
                        .withAttribute(integrationObjectItemAttribute('code'))
                        .withAttribute(integrationObjectItemAttribute('catalogVersion').withReturnItem('CatalogVersion'))
                        .withAttribute(integrationObjectItemAttribute('keywords').withReturnItem('Keyword')))
                .build()
        and: "two products with the same keyword created"
        def productCode1 = "prod1"
        def productCode2 = "prod2"
        def key = "${TEST_NAME}_Keyword"
        IntegrationTestUtil.importImpEx(
                "INSERT_UPDATE Product ; code[unique = true] ; catalogVersion",
                "                      ; $productCode1       ; $catalogVersion.pk",
                "                      ; $productCode2       ; $catalogVersion.pk",
                "INSERT_UPDATE Keyword ; keyword[unique = true] ; catalogVersion      ; language(isocode); products(code)",
                "                      ; $key                   ; $catalogVersion.pk  ; en               ; $productCode1")
        and: "Outbound channel listens for Product and Keyword changes after they were created"
        def channel = outboundChannelBuilder.withIntegrationObjectCode(PRODUCT_IO).build()
        changeDetector.withItemType('Product').build()
        changeDetector.withItemType('Keyword').build()
        and: "the Keyword has changed by adding second Product"
        IntegrationTestUtil.importImpEx(
                "UPDATE Keyword ; keyword[unique = true] ; products(code)",
                "               ; $key                   ; $productCode2")

        when:
        cronJobService.performCronJob(cronJob, true)

        then: "update sent for both Products referred from the changed Keyword"
        condition().eventually {
            // both products were notified
            assert testOutboundFacade.itemsFromInvocationsTo(channel.destination, PRODUCT_IO)
                    .collect({ it.code }).containsAll([productCode1, productCode2])
            assert testOutboundItemConsumer.invocations() == 3 // prod1, prod2 + keyword (keyword)
        }

        cleanup:
        IntegrationTestUtil.remove OutboundChannelConfigurationModel, { occ -> occ.integrationObject.code == PRODUCT_IO }
        IntegrationTestUtil.remove ProductModel, { it.code == productCode1 || it.code == productCode2 }
        IntegrationTestUtil.remove KeywordModel, { it.keyword == key }
    }

    @Test
    def "sends updates for subtypes of the integration object root item type"() {
        given: "integration object for User root type"
        customerIO
                .withItem(integrationObjectItem().withCode('User').root()
                        .withAttribute(integrationObjectItemAttribute().withName('uid')))
        and: "Outbound sync channel listens for User changes"
        def channel = outboundChannelBuilder.withIntegrationObject(customerIO).build()
        changeDetector.withItemType('Customer').build()
        and: 'a subclass of User (Customer) is created'
        def customer = "${TEST_NAME}_Customer2"
        IntegrationTestUtil.importImpEx(
                'INSERT_UPDATE Customer; uid[unique = true]',
                "                      ; $customer         ")

        when: 'the outbound sync is performed'
        cronJobService.performCronJob(cronJob, true)

        then: 'the item that is a subtype of the root item type is sent out'
        condition().eventually {
            assert testOutboundFacade.invocations() == 1
            assert testOutboundFacade.itemsFromInvocationsTo(channel.destination, CUSTOMER_IO)
                    .collect({ it.itemtype }) == ['Customer']
            assert testOutboundItemConsumer.invocations() == 1
        }

        cleanup:
        IntegrationTestUtil.remove OutboundChannelConfigurationModel, { occ -> occ.integrationObject.code == CUSTOMER_IO }
        IntegrationTestUtil.removeSafely CustomerModel, { it.uid == customer }
    }

    @Test
    def "no updates sent for subtypes of the integration object root item type when the subtype is excluded"() {
        given: "integration object for User root type"
        customerIO
                .withItem(integrationObjectItem().withCode('User').root()
                        .withAttribute(integrationObjectItemAttribute().withName('uid')))
        and: "Outbound sync channel listens for User changes"
        outboundChannelBuilder.withIntegrationObject(customerIO).build()
        and: "Stream configuration excludes Customer sub-type"
        changeDetector.withItemType('User').withExcludedTypes(['Customer'] as Set).build()
        and: 'a subclass of User (Customer) is created'
        def customer = "${TEST_NAME}_Customer2"
        IntegrationTestUtil.importImpEx(
                'INSERT_UPDATE Customer; uid[unique = true]',
                "                      ; $customer         ")

        when: 'the outbound sync is performed'
        cronJobService.performCronJob(cronJob, true)

        then: "the job sent no updates"
        condition().eventually {
            assert cronJob.result == CronJobResult.SUCCESS
            assert testOutboundFacade.invocations() == 0
            assert testOutboundItemConsumer.invocations() == 0
        }

        cleanup:
        IntegrationTestUtil.remove OutboundChannelConfigurationModel, { occ -> occ.integrationObject.code == CUSTOMER_IO }
        IntegrationTestUtil.removeSafely CustomerModel, { it.uid == customer }
    }
}
