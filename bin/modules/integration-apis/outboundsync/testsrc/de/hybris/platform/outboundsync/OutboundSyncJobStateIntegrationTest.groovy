/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.outboundsync

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
import de.hybris.platform.integrationservices.exception.IntegrationAttributeException
import de.hybris.platform.integrationservices.exception.IntegrationAttributeProcessingException
import de.hybris.platform.integrationservices.util.IntegrationTestUtil
import de.hybris.platform.integrationservices.util.ItemTracker
import de.hybris.platform.outboundservices.facade.OutboundServiceFacade
import de.hybris.platform.outboundservices.util.TestOutboundFacade
import de.hybris.platform.outboundsync.activator.impl.DefaultOutboundSyncService
import de.hybris.platform.outboundsync.job.impl.OutboundSyncCronJobPerformable
import de.hybris.platform.outboundsync.util.OutboundSyncEssentialData
import de.hybris.platform.outboundsync.util.TestOutboundSyncJob
import de.hybris.platform.servicelayer.ServicelayerSpockSpecification
import de.hybris.platform.servicelayer.cronjob.CronJobService
import org.junit.ClassRule
import org.junit.Rule
import org.junit.Test
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
@Issue('https://jira.hybris.com/browse/STOUT-3404')
class OutboundSyncJobStateIntegrationTest extends ServicelayerSpockSpecification {
    private static final String TEST_NAME = "OutboundSyncJobState"
    private static final String IO = "${TEST_NAME}_IO"
    private static final String CATALOG = "${TEST_NAME}_Catalog"
    private static final String CATEGORY_CODE1 = "${TEST_NAME}_Category_1"
    private static final String CATEGORY_CODE2 = "${TEST_NAME}_Category_2"
    private static final String CATEGORY_CODE3 = "${TEST_NAME}_Category_3"
    private static final String CATALOG_VERSION = 'OutboundSyncJobState'
    private static final String CHANNEL_CODE = "${TEST_NAME}_OutboundChannelConfiguration"
    private static final String STREAM_ID = "${TEST_NAME}_StreamConfiguration"

    @Resource
    private CronJobService cronJobService
    @Resource(name = 'outboundSyncService')
    private DefaultOutboundSyncService outboundSyncService
    private OutboundServiceFacade originalOutboundServiceFacade

    @Shared
    @ClassRule
    OutboundSyncEssentialData essentialData = OutboundSyncEssentialData.outboundSyncEssentialData()
    @Rule
    TestOutboundFacade testOutboundFacade = new TestOutboundFacade()
    @Rule
    OutboundChannelConfigurationBuilder channelBuilder = outboundChannelConfigurationBuilder()
            .withCode(CHANNEL_CODE)
            .withConsumedDestination(consumedDestinationBuilder().withId('outboundsync-job-state'))
            .withIntegrationObject integrationObject().withCode(IO)
            .withItem(integrationObjectItem().withCode('Category').root()
                    .withAttribute(integrationObjectItemAttribute().withName('code')))
    @AutoCleanup('cleanup')
    OutboundSyncStreamConfigurationBuilder streamBuilder = outboundSyncStreamConfigurationBuilder()
            .withOutboundChannelCode(CHANNEL_CODE)
            .withId(STREAM_ID)
            .withItemType('Category')
    @Rule
    ItemTracker itemTracker = ItemTracker.track CategoryModel

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
        channelBuilder.build()
        streamBuilder.build()
        originalOutboundServiceFacade = outboundSyncService.outboundServiceFacade
        outboundSyncService.outboundServiceFacade = testOutboundFacade
    }

    def cleanup() {
        outboundSyncService.outboundServiceFacade = originalOutboundServiceFacade
    }

    @Test
    def 'outboundsync job is FINISHED with SUCCESS when #msg'() {
        given:
        categoriesChanged(changes)

        when: 'the job is executed without changes present'
        def jobResult = contextJob.performInSeparateThread()

        then: 'its status is changed to FINISHED'
        with(jobResult) {
            status == CronJobStatus.FINISHED
            result == CronJobResult.SUCCESS
        }

        where:
        msg                    | changes
        'there are no changes' | []
        'all changes are sent' | [CATEGORY_CODE1, CATEGORY_CODE2]
    }

    @Test
    def 'outboundsync job is FINISHED with ERROR when at least one change fails to sent'() {
        given: 'changes are present'
        categoriesChanged(CATEGORY_CODE1, CATEGORY_CODE2)
        and: 'responses from the sync destination for the changes are failure and then success'
        testOutboundFacade
                .respondWithBadRequest()
                .respondWithCreated()

        when: 'the job is executed'
        def jobResult = contextJob.performInSeparateThread()

        then: 'its status is changed to FINISHED with ERROR'
        with(jobResult) {
            status == CronJobStatus.FINISHED
            result == CronJobResult.ERROR
        }
    }

    @Test
    def 'outboundsync job reports FAILURE when something unexpected happens'() {
        setup: 'the job is misconfigured'
        def jobPerformable = Registry.applicationContext.getBean 'defaultOutboundSyncCronJobPerformable', OutboundSyncCronJobPerformable
        def eventServiceBackup = jobPerformable.eventService
        jobPerformable.eventService = null
        and: 'a change is present'
        categoriesChanged(CATEGORY_CODE1)

        when: 'the job is executed'
        def jobResult = contextJob.performInSeparateThread()

        then: 'its status is changed to FINISHED with FAILURE'
        with(jobResult) {
            status == CronJobStatus.FINISHED
            result == CronJobResult.FAILURE
        }
        and: 'the change is not consumed'
        !streamBuilder.allChanges.empty

        cleanup: 'restore the itemChangeSender'
        jobPerformable.eventService = eventServiceBackup
    }

    @Test
    @Issue('https://cxjira.sap.com/browse/IAPI-4067')
    def 'outboundsync job is FINISHED with FAILURE when a systemic attribute error occurs'() {
        given: 'changes are present'
        def categories = [CATEGORY_CODE1, CATEGORY_CODE2, CATEGORY_CODE3]
        categoriesChanged categories
        and: 'processing of the first changed category fails for a system problem but others would succeed'
        def exception = Stub IntegrationAttributeException
        testOutboundFacade
                .throwException(exception)
                .respondWithCreated()
                .respondWithCreated()

        when: 'the job is executed'
        def futureResult = contextJob.performInSeparateThread()

        then: 'its status is changed to FINISHED with FAILURE'
        with(futureResult) {
            status == CronJobStatus.FINISHED
            result == CronJobResult.FAILURE
        }

        and: 'unprocessed category changes are not consumed'
        !streamBuilder.allChanges.empty
        and: 'the job was interrupted and not all categories sent'
        testOutboundFacade.invocations() <= categories.size()
    }

    @Test
    def 'job aborted before any items are processed has UNKNOWN result'() {
        given: "change the job performable's change detection service to abort the job before collecting the changes"
        def jobPerformable = Registry.applicationContext.getBean 'defaultOutboundSyncCronJobPerformable', OutboundSyncCronJobPerformable
        def originalChangeDetector = jobPerformable.changeDetectionService
        jobPerformable.changeDetectionService = Stub(ChangeDetectionService) {
            collectChangesForType(_ as ComposedTypeModel, _ as StreamConfiguration, _ as ChangesCollector) >> { List args ->
                contextJob.abort()
                originalChangeDetector.collectChangesForType(args[0] as ComposedTypeModel, args[1] as StreamConfiguration, args[2] as ChangesCollector)
            }
        }
        and: 'changes are present'
        categoriesChanged(CATEGORY_CODE1)

        when: 'the job is kicked off'
        def futureResult = contextJob.performInSeparateThread()

        then: 'eventually job status is changed to ABORTED'
        with(futureResult) {
            status == CronJobStatus.ABORTED
            // we aborted before any items were processed (5 sec delay for aggregation)
            // and therefore the result is unknown
            result == CronJobResult.UNKNOWN
        }
        and: 'changes are not consumed'
        !streamBuilder.allChanges.empty
        and: 'no items sent'
        testOutboundFacade.allInvocations.isEmpty()

        cleanup: 'restore the change detection service'
        jobPerformable.changeDetectionService = originalChangeDetector
    }

    @Test
    @Issue('https://cxjira.sap.com/browse/IAPI-4067')
    def 'job continues to process when attribute processing error occurs'() {
        given: 'there are a few categories that changed'
        def categories = [CATEGORY_CODE1, CATEGORY_CODE2, CATEGORY_CODE3]
        categoriesChanged(categories)
        and: 'processing of the first changed category fails'
        def e = Stub IntegrationAttributeProcessingException
        testOutboundFacade
                .throwException(e)
                .respondWithCreated()

        when: 'the job is executed'
        def futureResult = contextJob.performInSeparateThread()

        then: 'job result is ERROR'
        with(futureResult) {
            status == CronJobStatus.FINISHED
            result == CronJobResult.ERROR
        }
        and: 'the error item change is not consumed'
        streamBuilder.allChanges.size() == 1
        and: 'all categories were sent to the outbound destination'
        testOutboundFacade.invocations() == categories.size()
    }

    private static void categoriesChanged(String... codes) {
        categoriesChanged(codes as List)
    }

    private static void categoriesChanged(List codes) {
        def impexLines = ['INSERT_UPDATE Category; code[unique = true]; catalogVersion(version, catalog(id))']
        impexLines.addAll codes.collect({ "                      ; $it; $CATALOG_VERSION:$CATALOG" })
        IntegrationTestUtil.importImpEx(impexLines as String[])
    }
}
