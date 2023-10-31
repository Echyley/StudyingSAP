/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.outboundsync.activator.impl

import de.hybris.bootstrap.annotations.IntegrationTest
import de.hybris.deltadetection.ChangeDetectionService
import de.hybris.deltadetection.ChangesCollector
import de.hybris.deltadetection.StreamConfiguration
import de.hybris.platform.catalog.model.CatalogModel
import de.hybris.platform.catalog.model.CatalogVersionModel
import de.hybris.platform.category.model.CategoryModel
import de.hybris.platform.core.Registry
import de.hybris.platform.core.model.type.ComposedTypeModel
import de.hybris.platform.cronjob.enums.CronJobResult
import de.hybris.platform.cronjob.enums.CronJobStatus
import de.hybris.platform.integrationservices.IntegrationObjectModelBuilder
import de.hybris.platform.integrationservices.util.EventualCondition
import de.hybris.platform.integrationservices.util.IntegrationTestUtil
import de.hybris.platform.integrationservices.util.ItemTracker
import de.hybris.platform.outboundservices.facade.OutboundServiceFacade
import de.hybris.platform.outboundservices.service.DeleteRequestSender
import de.hybris.platform.outboundservices.util.TestDeleteRequestSender
import de.hybris.platform.outboundservices.util.TestOutboundFacade
import de.hybris.platform.outboundsync.OutboundChannelConfigurationBuilder
import de.hybris.platform.outboundsync.job.impl.OutboundSyncCronJobPerformable
import de.hybris.platform.outboundsync.util.OutboundSyncEssentialData
import de.hybris.platform.outboundsync.util.TestOutboundSyncJob
import de.hybris.platform.servicelayer.ServicelayerSpockSpecification
import org.junit.ClassRule
import org.junit.Rule
import org.junit.Test
import org.springframework.http.ResponseEntity
import spock.lang.AutoCleanup
import spock.lang.Issue
import spock.lang.Shared

import javax.annotation.Resource

import static de.hybris.platform.integrationservices.IntegrationObjectItemAttributeModelBuilder.integrationObjectItemAttribute
import static de.hybris.platform.integrationservices.IntegrationObjectItemModelBuilder.integrationObjectItem
import static de.hybris.platform.integrationservices.IntegrationObjectModelBuilder.integrationObject
import static de.hybris.platform.outboundservices.ConsumedDestinationBuilder.consumedDestinationBuilder
import static de.hybris.platform.outboundsync.OutboundChannelConfigurationBuilder.outboundChannelConfigurationBuilder
import static de.hybris.platform.outboundsync.OutboundSyncStreamConfigurationBuilder.outboundSyncStreamConfigurationBuilder

@IntegrationTest
@Issue('https://jira.hybris.com/browse/STOUT-3466')
class DeleteOutboundSyncJobStateIntegrationTest extends ServicelayerSpockSpecification {
    private static final def TEST_NAME = "DeleteOutboundSyncJobState"
    private static final def IO = "${TEST_NAME}_IO"
    private static final def CATEGORY_CODE = "${TEST_NAME}_Category"
    private static final def CATEGORY_CODE_1 = "${CATEGORY_CODE}1"
    private static final def CATALOG = "${TEST_NAME}_Catalog"
    private static final def CATALOG_VERSION = 'DeleteOutboundSyncJobState'
    private static final def CHANNEL_CODE = "${TEST_NAME}_OutboundChannelConfiguration"
    private static final def DESTINATION_ID = "${TEST_NAME}_Destination"

    @Resource(name = 'defaultOutboundSyncService')
    private DefaultOutboundSyncService outboundSyncService
    private OutboundServiceFacade originalOutboundServiceFacade
    @Resource(name = 'defaultDeleteOutboundSyncService')
    private DefaultDeleteOutboundSyncService deleteOutboundSyncService
    private DeleteRequestSender originalDeleteRequestSender
    @Resource
    private ChangeDetectionService changeDetectionService

    @Shared
    @ClassRule
    OutboundSyncEssentialData essentialData = OutboundSyncEssentialData.outboundSyncEssentialData()

    @Rule
    ItemTracker itemTracker = ItemTracker.track CategoryModel
    @Rule
    TestOutboundFacade testOutboundFacade = new TestOutboundFacade()
    @Rule
    TestDeleteRequestSender testDeleteRequestSender = new TestDeleteRequestSender()
    @Rule
    OutboundChannelConfigurationBuilder outboundChannelBuilder = outboundChannelConfigurationBuilder()
            .withCode(CHANNEL_CODE)
            .withDeleteSynchronization()
            .withConsumedDestination(consumedDestinationBuilder().withId(DESTINATION_ID))
            .withIntegrationObjectCode IO
    @Rule
    IntegrationObjectModelBuilder categoryIO = integrationObject()
            .withCode(IO)
            .withItem(integrationObjectItem().withCode('Category').root()
                    .withAttribute(integrationObjectItemAttribute().withName('code')))
    @AutoCleanup(value = 'cleanup')
    def streamBuilder = outboundSyncStreamConfigurationBuilder()
            .withId(TEST_NAME + "Stream")
            .withOutboundChannelCode(CHANNEL_CODE)
            .withItemType('Category')

    TestOutboundSyncJob contextJob = new TestOutboundSyncJob()

    def setupSpec() {
        IntegrationTestUtil.importImpEx(
                'INSERT_UPDATE Catalog; id[unique=true]',
                "                     ; $CATALOG",
                'INSERT_UPDATE CatalogVersion; version[unique=true]; catalog(id)[unique=true]',
                "                            ; $CATALOG_VERSION    ; $CATALOG")
    }

    def cleanupSpec() {
        IntegrationTestUtil.removeSafely CatalogVersionModel, { it.version == CATALOG_VERSION }
        IntegrationTestUtil.removeSafely CatalogModel, { it.id == CATALOG }
    }

    def setup() {
        originalOutboundServiceFacade = outboundSyncService.outboundServiceFacade
        outboundSyncService.outboundServiceFacade = testOutboundFacade

        originalDeleteRequestSender = deleteOutboundSyncService.deleteRequestSender
        deleteOutboundSyncService.deleteRequestSender = testDeleteRequestSender
    }

    def cleanup() {
        outboundSyncService.outboundServiceFacade = originalOutboundServiceFacade
        deleteOutboundSyncService.deleteRequestSender = originalDeleteRequestSender
    }

    @Test
    def 'delete outboundsync job is immediately FINISHED when there are no changes'() {
        when: 'the job is executed without changes present'
        def futureResult = contextJob.perform()

        then: 'its status is changed to FINISHED with SUCCESS result'
        with(futureResult) {
            status == CronJobStatus.FINISHED
            result == CronJobResult.SUCCESS
        }
    }

    @Test
    def 'delete outboundsync job finishes successfully when all changes are sent'() {
        given: 'initial change detected for category creation'
        categoriesChanged CATEGORY_CODE
        streamBuilder.build()

        and: 'delete the same category'
        deleteCategories CATEGORY_CODE

        when:
        def futureResult = contextJob.performInSeparateThread()

        then: 'its status is changed to FINISHED with SUCCESS result'
        with(futureResult) {
            status == CronJobStatus.FINISHED
            result == CronJobResult.SUCCESS
        }
        and:
        allChangesConsumed()
    }

    @Test
    def 'delete outboundsync job fails when at least one change is not sent'() {
        given: 'initial change detected for category creation'
        categoriesChanged CATEGORY_CODE, CATEGORY_CODE_1
        streamBuilder.build()

        and: 'delete the same categories'
        deleteCategories CATEGORY_CODE, CATEGORY_CODE_1

        and: 'responses from the sync destination for the changes are failure and then success'
        testDeleteRequestSender.respondWithError()

        when:
        def futureResult = contextJob.performInSeparateThread()

        then: 'its status is changed to FINISHED with ERROR'
        with(futureResult) {
            status == CronJobStatus.FINISHED
            result == CronJobResult.ERROR
        }
        and:
        allChangesConsumed()
    }

    @Test
    def 'delete outboundsync job reports ERROR when something unexpected happens'() {
        given: 'initial change detected for category creation'
        categoriesChanged CATEGORY_CODE
        streamBuilder.build()

        and: 'the delete sync service is misconfigured'
        deleteOutboundSyncService.deleteRequestSender = null

        and: 'delete the same category'
        deleteCategories CATEGORY_CODE

        when:
        def futureResult = contextJob.performInSeparateThread()

        then: 'its status is changed to FINISHED with ERROR result'
        with(futureResult) {
            status == CronJobStatus.FINISHED
            result == CronJobResult.ERROR
        }
        and:
        allChangesConsumed()

        cleanup: 'restore the deleteRequestSender'
        deleteOutboundSyncService.deleteRequestSender = testDeleteRequestSender
    }

    @Test
    def 'job aborted before any items are sent to destination has UNKNOWN result'() {
        given: 'initial change detected for category creation'
        categoriesChanged CATEGORY_CODE
        streamBuilder.build()

        and: 'the created category is deleted'
        deleteCategories CATEGORY_CODE

        and: "change the job performable's change detection service to abort the job before collecting the changes"
        def jobPerformable = Registry.applicationContext.getBean 'defaultOutboundSyncCronJobPerformable', OutboundSyncCronJobPerformable
        jobPerformable.changeDetectionService = Stub(ChangeDetectionService) {
            collectChangesForType(_ as ComposedTypeModel, _ as StreamConfiguration, _ as ChangesCollector) >> { List args ->
                contextJob.abort()
                changeDetectionService.collectChangesForType(args[0] as ComposedTypeModel, args[1] as StreamConfiguration, args[2] as ChangesCollector)
            }
        }

        when: 'the job is kicked off'
        def performResult = contextJob.performInSeparateThread()

        then: 'job status is changed to ABORTED with UNKNOWN result'
        with(performResult) {
            status == CronJobStatus.ABORTED
            result == CronJobResult.UNKNOWN
        }
        and: 'no items sent'
        testDeleteRequestSender.invocations() == 0

        cleanup: 'restore the change detection service'
        jobPerformable.changeDetectionService = changeDetectionService
    }

    @Test
    def "job aborted after an error occurred has ERROR result"() {
        given: 'initial change detected for category creation'
        def categories = (1..50).collect { "${CATEGORY_CODE}$it" }
        categoriesChanged categories
        streamBuilder.build()

        and: 'delete the same categories'
        deleteCategories categories

        and: 'sender responds with error and then aborts the job for one of the delete changes sent'
        testDeleteRequestSender
                .respondWithError()
                .respondWithError()
                .respondWithError()
                .respondWithError()
                .respondWithError()
                .performRunnableAndRespondWith({ sleep(10); contextJob.abort() }, ResponseEntity.badRequest() )
                .respondWithError()

        when: 'the job is kicked off'
        def jobResult = contextJob.performInSeparateThread()

        then: 'final job status is changed to ABORTED with ERROR result'
        with(jobResult) {
            status == CronJobStatus.ABORTED
            result == CronJobResult.ERROR
        }

        and: 'items sent is less than delete changes'
        testDeleteRequestSender.invocations() < categories.size()

        and: 'all changes are consumed'
        allChangesConsumed()
    }

    @Test
    def "job aborted after an item is sent successfully has SUCCESS result"() {
        given: 'initial change detected for category creation'
        def categories = (1..10).collect { "${CATEGORY_CODE}$it" }
        categoriesChanged categories
        streamBuilder.build()

        and: 'delete the same categories'
        deleteCategories categories

        and: 'sender responds with success and then aborts the job for one of the delete changes sent'
        testDeleteRequestSender
                .respondWithSuccess()
                .respondWithSuccess()
                .performRunnableAndRespondWith({ contextJob.abort() }, ResponseEntity.ok() )
                .respondWithSuccess()

        when: 'the job is kicked off'
        def futureResult = contextJob.performInSeparateThread()

        then: 'final job status is changed to ABORTED with SUCCESS result'
        with(futureResult) {
            status == CronJobStatus.ABORTED
            result == CronJobResult.SUCCESS
        }

        and: 'items sent is less than delete changes'
        testDeleteRequestSender.invocations() < categories.size()

        and: 'all changes are not consumed'
        allChangesConsumed()
    }

    @Test
    def 'job synchronizes new and deleted items'() {
        given: 'initial change detected for category creation'
        categoriesChanged CATEGORY_CODE
        streamBuilder.build()

        and: 'delete the same category'
        deleteCategories CATEGORY_CODE

        and: 'add a new category'
        categoriesChanged CATEGORY_CODE_1

        when:
        def futureResult = contextJob.performInSeparateThread()

        then: 'its status is changed to FINISHED with SUCCESS result'
        with(futureResult) {
            status == CronJobStatus.FINISHED
            result == CronJobResult.SUCCESS
        }
        and: 'create and delete changes are consumed'
        allChangesConsumed()

        and: 'the category change is sent'
        testOutboundFacade.invocations() == 1
        testDeleteRequestSender.invocations() == 1
    }

    private static void categoriesChanged(String... codes) {
        categoriesChanged(codes as List)
    }

    private static void categoriesChanged(List codes) {
        def impexLines = ['INSERT_UPDATE Category; code[unique = true]; catalogVersion(version, catalog(id))']
        impexLines.addAll codes.collect({ "                      ; $it; $CATALOG_VERSION:$CATALOG" })
        IntegrationTestUtil.importImpEx(impexLines as String[])
    }

    private static void deleteCategories(String... codes) {
        deleteCategories(codes as List)
    }

    private static void deleteCategories(List codes) {
        def impexLines = ['REMOVE Category; code[unique = true]']
        impexLines.addAll codes.collect({ "                      ; $it" })
        IntegrationTestUtil.importImpEx(impexLines as String[])
    }

    private void allChangesConsumed() {
        EventualCondition.eventualCondition().expect {
            assert streamBuilder.allChanges.isEmpty()
        }
    }
}
