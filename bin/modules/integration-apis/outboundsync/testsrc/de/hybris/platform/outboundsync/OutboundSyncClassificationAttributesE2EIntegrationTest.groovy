/*
 *  Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.outboundsync

import de.hybris.bootstrap.annotations.IntegrationTest
import de.hybris.platform.core.model.product.ProductModel
import de.hybris.platform.core.model.user.EmployeeModel
import de.hybris.platform.integrationservices.IntegrationObjectModelBuilder
import de.hybris.platform.integrationservices.util.ClassificationBuilder
import de.hybris.platform.integrationservices.util.IntegrationTestUtil
import de.hybris.platform.outboundservices.ConsumedDestinationBuilder
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
import spock.lang.Issue
import spock.lang.Shared

import javax.annotation.Resource

import static de.hybris.platform.integrationservices.IntegrationObjectItemAttributeModelBuilder.integrationObjectItemAttribute
import static de.hybris.platform.integrationservices.IntegrationObjectItemClassificationAttributeBuilder.classificationAttribute
import static de.hybris.platform.integrationservices.IntegrationObjectItemModelBuilder.integrationObjectItem
import static de.hybris.platform.integrationservices.util.IntegrationTestUtil.condition
import static de.hybris.platform.outboundservices.ConsumedDestinationBuilder.consumedDestinationBuilder

@IntegrationTest
@Issue('https://jira.hybris.com/browse/STOUT-2920')
class OutboundSyncClassificationAttributesE2EIntegrationTest extends ServicelayerSpockSpecification {
    private static final String TEST_NAME = "OutboundSyncClassificationAttributesE2E"
    private static final String SYSTEM = "${TEST_NAME}_Electronics"
    private static final String VERSION = 'Test'
    private static final String SYSTEM_VERSION = "$SYSTEM:$VERSION"
    private static final String IO = "${TEST_NAME}_OutboundProductIO"
    private static final String CLASS = "${TEST_NAME}_ClassficationClass"
    private static final String PRODUCT_CODE = "productCode"
    private static final String UID = "${TEST_NAME}_Employee"
    private static final String CHANNEL_CODE = "${TEST_NAME}_OutboundChannelConfiguration"

    @Resource
    private CronJobService cronJobService
    @Resource(name = 'outboundSyncService')
    private DefaultOutboundSyncService outboundSyncService
    private OutboundServiceFacade outboundServiceFacade
    private OutboundItemConsumer outboundItemConsumer

    @Shared
    @ClassRule
    IntegrationObjectModelBuilder integrationObject = IntegrationObjectModelBuilder.integrationObject().withCode(IO)
            .withItem(integrationObjectItem().withCode('Catalog')
                    .withAttribute(integrationObjectItemAttribute().withName('id')))
            .withItem(integrationObjectItem().withCode('CatalogVersion')
                    .withAttribute(integrationObjectItemAttribute().withName('version'))
                    .withAttribute(integrationObjectItemAttribute().withName('catalog').withReturnItem('Catalog')))
            .withItem(integrationObjectItem().withCode('Employee')
                    .withAttribute(integrationObjectItemAttribute().withName('uid'))
                    .withAttribute(integrationObjectItemAttribute().withName('name')))
            .withItem(integrationObjectItem().withCode('Product').root()
                    .withAttribute(integrationObjectItemAttribute('code'))
                    .withAttribute(integrationObjectItemAttribute('catalogVersion').withReturnItem('CatalogVersion'))
                    .withAttribute(classificationAttribute('tester').withClassificationSystem(SYSTEM, VERSION)
                            .withClassAssignment(CLASS, 'tester').withReturnItem('Employee')))
    @Shared
    @ClassRule
    ClassificationBuilder classificationBuilder = ClassificationBuilder.classification()
            .withSystem(SYSTEM)
            .withVersion(VERSION)
            .withClassificationClass(CLASS)
            .withAttribute(ClassificationBuilder.attribute().withName('tester').references('Employee'))
    @Shared
    @ClassRule
    OutboundSyncEssentialData essentialData = OutboundSyncEssentialData.outboundSyncEssentialData()

    @Rule
    TestOutboundFacade testOutboundFacade = new TestOutboundFacade().respondWithCreated()
    @Rule
    TestOutboundItemConsumer testOutboundItemConsumer = new TestOutboundItemConsumer()
    @Rule
    ConsumedDestinationBuilder destinationBuilder = consumedDestinationBuilder().withId('outbound-classification-e2e')
    @Rule
    OutboundSyncStreamConfigurationBuilder streamBuilder = OutboundSyncStreamConfigurationBuilder
            .outboundSyncStreamConfigurationBuilder()
            .withOutboundChannelCode(CHANNEL_CODE)
            .withItemType('Product')
    @Rule
    OutboundChannelConfigurationBuilder channelBuilder = new OutboundChannelConfigurationBuilder()
            .withConsumedDestination(destinationBuilder)
            .withIntegrationObjectCode(IO)
            .withCode(CHANNEL_CODE)

    def tester = createEmployee()

    def setup() {
        IntegrationTestUtil.importImpEx(
                'INSERT_UPDATE Product; code[unique = true]; catalogVersion(catalog(id), version)[unique = true]',
                "                     ; $PRODUCT_CODE      ; $SYSTEM_VERSION")

        outboundServiceFacade = outboundSyncService.outboundServiceFacade
        outboundSyncService.outboundServiceFacade = testOutboundFacade
        outboundItemConsumer = outboundSyncService.outboundItemConsumer
        outboundSyncService.outboundItemConsumer = testOutboundItemConsumer
    }

    def cleanup() {
        outboundSyncService.outboundServiceFacade = outboundServiceFacade
        outboundSyncService.outboundItemConsumer = outboundItemConsumer
        IntegrationTestUtil.removeAll ProductModel
        IntegrationTestUtil.remove tester
        IntegrationTestUtil.removeAll OutboundChannelConfigurationModel
    }

    @Test
    def 'update not sent when root item cannot be derived from the changed item model'() {
        given: 'product is associated with the classification class'
        IntegrationTestUtil.importImpEx(
                '$catalogVersionHeader=catalogVersion(catalog(id), version)',
                'INSERT_UPDATE ClassificationClass; code[unique = true]; $catalogVersionHeader[unique = true]; products($catalogVersionHeader, code)',
                "                                 ; $CLASS             ; $SYSTEM_VERSION                     ; $SYSTEM_VERSION:$PRODUCT_CODE")
        and: 'product has classification attribute assigned'
        IntegrationTestUtil.importImpEx(
                '$catalogVersionHeader = catalogVersion(catalog(id), version)',
                '$systemVersionHeader = systemVersion(catalog(id), version)',
                '$classificationClassHeader = classificationClass(catalogVersion(catalog(id), version), code)',
                '$classificationAttributeHeader = classificationAttribute($systemVersionHeader, code)',
                '$assignmentHeader=classificationAttributeAssignment($classificationClassHeader, $classificationAttributeHeader)',
                '$valueHeader=value[translator = de.hybris.platform.catalog.jalo.classification.impex.ProductFeatureValueTranslator]',
                'INSERT_UPDATE ProductFeature; product($catalogVersionHeader, code)[unique = true]; $assignmentHeader[unique = true]             ; qualifier                     ; $valueHeader               ; valuePosition[unique = true]',
                "                            ; $SYSTEM_VERSION:$PRODUCT_CODE                      ; $SYSTEM_VERSION:$CLASS:$SYSTEM_VERSION:tester; $SYSTEM/$VERSION/${CLASS}.tester; \"reference, ${tester.pk}\"; 0")
        and: 'Outbound sync channel listens for Employee and Product changes after they created'
        streamBuilder.withItemType('Product').build()
        streamBuilder.withItemType('Employee').build()
        and: 'the Employee is changed'
        IntegrationTestUtil.importImpEx(
                'UPDATE Employee ; uid[unique = true]; name',
                "                       ; $UID              ; Robbert Tester")

        when:
        cronJobService.performCronJob contextCronJob(), true

        then: 'update not sent'
        condition().eventually {
            assert testOutboundFacade.invocations() == 0
            assert testOutboundItemConsumer.invocations() == 0
        }
    }

    @Test
    def 'sends update when product is associated with new classification attribute'() {
        given: 'outbound channel listens for Product created'
        def dest = destination()
        and: 'product is associated with the classification class'
        IntegrationTestUtil.importImpEx(
                '$catalogVersionHeader=catalogVersion(catalog(id), version)',
                'INSERT_UPDATE ClassificationClass; code[unique = true]; $catalogVersionHeader[unique = true]; products($catalogVersionHeader, code)',
                "                                 ; $CLASS             ; $SYSTEM_VERSION                     ; $SYSTEM_VERSION:$PRODUCT_CODE")

        when:
        cronJobService.performCronJob contextCronJob(), true

        then: "update is sent"
        condition().eventually {
            assert testOutboundFacade.invocations() == 1
            assert testOutboundFacade.itemsFromInvocationsTo(dest, IO)
                    .collect({ it.itemtype }) == ['Product']
            assert testOutboundItemConsumer.invocations() == 1
        }
    }

    @Test
    def 'sends update when product is disassociated from the classification class'() {
        given: 'product is associated with the classification class'
        IntegrationTestUtil.importImpEx(
                '$catalogVersionHeader=catalogVersion(catalog(id), version)',
                'INSERT_UPDATE ClassificationClass; code[unique = true]; $catalogVersionHeader[unique = true]; products($catalogVersionHeader, code)',
                "                                 ; $CLASS             ; $SYSTEM_VERSION                     ; $SYSTEM_VERSION:$PRODUCT_CODE")
        and: 'product has classification attribute assigned'
        IntegrationTestUtil.importImpEx(
                '$catalogVersionHeader = catalogVersion(catalog(id), version)',
                '$systemVersionHeader = systemVersion(catalog(id), version)',
                '$classificationClassHeader = classificationClass(catalogVersion(catalog(id), version), code)',
                '$classificationAttributeHeader = classificationAttribute($systemVersionHeader, code)',
                '$assignmentHeader=classificationAttributeAssignment($classificationClassHeader, $classificationAttributeHeader)',
                '$valueHeader=value[translator = de.hybris.platform.catalog.jalo.classification.impex.ProductFeatureValueTranslator]',
                'INSERT_UPDATE ProductFeature; product($catalogVersionHeader, code)[unique = true]; $assignmentHeader[unique = true]             ; qualifier                     ; $valueHeader               ; valuePosition[unique = true]',
                "                            ; $SYSTEM_VERSION:$PRODUCT_CODE                      ; $SYSTEM_VERSION:$CLASS:$SYSTEM_VERSION:tester; $SYSTEM/$VERSION/${CLASS}.tester; \"reference, ${tester.pk}\"; 0")
        and: 'outbound channel listens for Product created'
        def dest = destination()
        and: 'product is disassociated from the classification class'
        IntegrationTestUtil.importImpEx(
                '$catalogVersionHeader=catalogVersion(catalog(id), version)',
                'UPDATE ClassificationClass; code[unique = true]; $catalogVersionHeader[unique = true]; products($catalogVersionHeader, code)[mode = remove]',
                "                          ; $CLASS             ; $SYSTEM_VERSION                     ; $SYSTEM_VERSION:$PRODUCT_CODE")

        when:
        cronJobService.performCronJob contextCronJob(), true

        then: "update is sent"
        condition().eventually {
            assert testOutboundFacade.invocations() == 1
            assert testOutboundFacade.itemsFromInvocationsTo(dest, IO)
                    .collect({ it.itemtype }) == ['Product']
            assert testOutboundItemConsumer.invocations() == 1
        }
    }

    @Test
    def "sends update when product classification attribute is assigned"() {
        given: 'product is associated with the classification class'
        IntegrationTestUtil.importImpEx(
                '$catalogVersionHeader=catalogVersion(catalog(id), version)',
                'INSERT_UPDATE ClassificationClass; code[unique = true]; $catalogVersionHeader[unique = true]; products($catalogVersionHeader, code)',
                "                                 ; $CLASS             ; $SYSTEM_VERSION                     ; $SYSTEM_VERSION:$PRODUCT_CODE")
        and: 'Outbound sync channel listens for Product changes'
        def destination = destination()
        and: 'product has classification attribute assigned'
        IntegrationTestUtil.importImpEx(
                '$catalogVersionHeader = catalogVersion(catalog(id), version)',
                '$systemVersionHeader = systemVersion(catalog(id), version)',
                '$classificationClassHeader = classificationClass(catalogVersion(catalog(id), version), code)',
                '$classificationAttributeHeader = classificationAttribute($systemVersionHeader, code)',
                '$assignmentHeader=classificationAttributeAssignment($classificationClassHeader, $classificationAttributeHeader)',
                '$valueHeader=value[translator = de.hybris.platform.catalog.jalo.classification.impex.ProductFeatureValueTranslator]',
                'INSERT_UPDATE ProductFeature; product($catalogVersionHeader, code)[unique = true]; $assignmentHeader[unique = true]             ; qualifier                     ; $valueHeader               ; valuePosition[unique = true]',
                "                            ; $SYSTEM_VERSION:$PRODUCT_CODE                      ; $SYSTEM_VERSION:$CLASS:$SYSTEM_VERSION:tester; $SYSTEM/$VERSION/${CLASS}.tester; \"reference, ${tester.pk}\"; 0")

        when:
        cronJobService.performCronJob contextCronJob(), true

        then: 'update is sent'
        condition().eventually {
            assert testOutboundFacade.invocations() == 1
            assert testOutboundFacade.itemsFromInvocationsTo(destination, IO)
                    .collect({ it.itemtype }) == ['Product']
            assert testOutboundItemConsumer.invocations() == 1
        }
    }

    @Test
    def 'sends update when classification attributes is unassigned from the product'() {
        given: 'product is associated with the classification class'
        IntegrationTestUtil.importImpEx(
                '$catalogVersionHeader=catalogVersion(catalog(id), version)',
                'INSERT_UPDATE ClassificationClass; code[unique = true]; $catalogVersionHeader[unique = true]; products($catalogVersionHeader, code)',
                "                                 ; $CLASS             ; $SYSTEM_VERSION                     ; $SYSTEM_VERSION:$PRODUCT_CODE")
        and: 'product has classification attribute assigned'
        IntegrationTestUtil.importImpEx(
                '$catalogVersionHeader = catalogVersion(catalog(id), version)',
                '$systemVersionHeader = systemVersion(catalog(id), version)',
                '$classificationClassHeader = classificationClass($catalogVersionHeader, code)',
                '$classificationAttributeHeader = classificationAttribute($systemVersionHeader, code)',
                '$assignmentHeader=classificationAttributeAssignment($classificationClassHeader, $classificationAttributeHeader)',
                '$valueHeader=value[translator = de.hybris.platform.catalog.jalo.classification.impex.ProductFeatureValueTranslator]',
                'INSERT_UPDATE ProductFeature; product($catalogVersionHeader, code)[unique = true]; $assignmentHeader[unique = true]             ; qualifier                     ; $valueHeader               ; valuePosition[unique = true]',
                "                            ; $SYSTEM_VERSION:$PRODUCT_CODE                      ; $SYSTEM_VERSION:$CLASS:$SYSTEM_VERSION:tester; $SYSTEM/$VERSION/${CLASS}.tester; \"reference, ${tester.pk}\"; 0")
        and: 'outbound sync channel listens for Product changes'
        def destination = destination()
        and: 'the classification attribute is unassigned from the product'
        IntegrationTestUtil.importImpEx(
                '$catalogVersionHeader = catalogVersion(catalog(id), version)',
                '$systemVersionHeader = systemVersion(catalog(id), version)',
                '$classificationClassHeader = classificationClass($catalogVersionHeader, code)',
                '$classificationAttributeHeader = classificationAttribute($systemVersionHeader, code)',
                '$assignmentHeader=classificationAttributeAssignment($classificationClassHeader, $classificationAttributeHeader)',
                'REMOVE ProductFeature; product($catalogVersionHeader, code)[unique = true]; $assignmentHeader[unique = true]             ',
                "                     ; $SYSTEM_VERSION:$PRODUCT_CODE                      ; $SYSTEM_VERSION:$CLASS:$SYSTEM_VERSION:tester")

        when:
        cronJobService.performCronJob contextCronJob(), true

        then: 'update is sent'
        condition().eventually {
            assert testOutboundFacade.invocations() == 1
            assert testOutboundFacade.itemsFromInvocationsTo(destination, IO)
                    .collect({ it.itemtype }) == ['Product']
            assert testOutboundItemConsumer.invocations() == 1
        }
    }

    def createEmployee() {
        IntegrationTestUtil.importImpEx(
                'INSERT_UPDATE Employee; uid[unique = true]; name',
                "                             ; $UID              ; Test Employee Name")
        IntegrationTestUtil.findAny(EmployeeModel, { it.uid == UID }).orElse(null)
    }

    def destination() {
        destinationBuilder.build()
    }

    def contextCronJob() {
        OutboundSyncEssentialData.outboundCronJob()
    }
}