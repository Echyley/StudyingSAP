/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.outboundsync.job.impl

import de.hybris.bootstrap.annotations.UnitTest
import de.hybris.deltadetection.ChangeDetectionService
import de.hybris.deltadetection.StreamConfiguration
import de.hybris.platform.core.PK
import de.hybris.platform.core.model.type.ComposedTypeModel
import de.hybris.platform.cronjob.enums.CronJobResult
import de.hybris.platform.cronjob.enums.CronJobStatus
import de.hybris.platform.outboundsync.events.StartedOutboundSyncEvent
import de.hybris.platform.outboundsync.job.ChangesCollectorFactory
import de.hybris.platform.outboundsync.job.CountingChangesCollector
import de.hybris.platform.outboundsync.model.OutboundSyncCronJobModel
import de.hybris.platform.outboundsync.model.OutboundSyncJobModel
import de.hybris.platform.outboundsync.model.OutboundSyncStreamConfigurationContainerModel
import de.hybris.platform.outboundsync.model.OutboundSyncStreamConfigurationModel
import de.hybris.platform.servicelayer.event.EventService
import de.hybris.platform.servicelayer.model.ModelService
import de.hybris.platform.testframework.JUnitPlatformSpecification
import org.junit.Test

import static OutboundSyncStateBuilder.initialOutboundSyncState

@UnitTest
class OutboundSyncCronJobPerformableUnitTest extends JUnitPlatformSpecification {

    def static CRON_JOB_PK = PK.fromLong(123L)
    def static JOB_STATE = initialOutboundSyncState().build()

    def changeDetectionService = Mock ChangeDetectionService
    def changesCollectorFactory = Stub ChangesCollectorFactory
    def eventService = Mock EventService
    def jobRegister = Stub OutboundSyncJobRegister
    def modelService = Mock ModelService

    def cronJobPerformable = new OutboundSyncCronJobPerformable(
            changeDetectionService: changeDetectionService,
            changesCollectorFactory: changesCollectorFactory,
            eventService: eventService,
            jobRegister: jobRegister,
            modelService: modelService)

    @Test
    def "perform results in error when #field is null"() {
        when:
        def result = cronJobPerformable.perform cronJob

        then:
        0 * eventService._
        and:
        CronJobResult.FAILURE == result.getResult()
        CronJobStatus.FINISHED == result.getStatus()

        where:
        field                            | cronJob
        'job model'                      | cronJobWith(null)
        'stream configuration container' | cronJobWith(job(null))
    }

    @Test
    def "perform results in error when an exception is thrown while processing the changes"() {
        given:
        changeDetectionService.collectChangesForType(_ as ComposedTypeModel, _ as StreamConfiguration, _ as CountingChangesCollector) >> {
            throw new RuntimeException()
        }

        when:
        def result = cronJobPerformable.perform defaultCronJob([productStream: "Product"])

        then:
        0 * eventService._
        and:
        CronJobResult.FAILURE == result.getResult()
        CronJobStatus.FINISHED == result.getStatus()
    }

    @Test
    def "perform results in error when configurations are null"() {
        given:
        def job = defaultCronJob(null as List)

        when:
        def result = cronJobPerformable.perform job

        then:
        0 * eventService._
        and:
        CronJobResult.FAILURE == result.getResult()
        CronJobStatus.FINISHED == result.getStatus()
    }

    @Test
    def "perform results in error when exception is thrown while change collector is created"() {
        given:
        def job = defaultCronJob([productStream: "Product"])
        and:
        changesCollectorFactory.createCountingCollector(job, _ as OutboundSyncStreamConfigurationModel) >> {
            throw new RuntimeException()
        }

        when:
        def result = cronJobPerformable.perform job

        then:
        0 * eventService._
        and:
        CronJobResult.FAILURE == result.getResult()
        CronJobStatus.FINISHED == result.getStatus()
    }

    @Test
    def "perform reports job state when there are no change streams associated with the job"() {
        given: 'the job has no streams associated with it'
        def job = defaultCronJob([])
        and: 'final job state'
        jobRegister.getNewJob(job) >> Stub(OutboundSyncJob) {
            getFinalState() >> JOB_STATE
        }

        when:
        def result = cronJobPerformable.perform job

        then:
        1 * eventService.publishEvent(_) >> { List args ->
            def event = args[0] as StartedOutboundSyncEvent
            assert event.cronJobPk == CRON_JOB_PK
            assert event.itemCount == 0
            assert event.startTime
        }
        and:
        result.status == JOB_STATE.cronJobStatus
        result.result == JOB_STATE.cronJobResult
    }

    @Test
    def "perform reports job state when no changes are present in the streams"() {
        given:
        def stream1 = streamConfiguration('products', 'Product')
        def stream2 = streamConfiguration('categories', 'Category')
        and:
        def job = defaultCronJob([stream1, stream2])
        and: 'collectors corresponding to the streams'
        def collector1 = Stub CountingChangesCollector
        def collector2 = Stub CountingChangesCollector
        changesCollectorFactory.createCountingCollector(job, stream1) >> collector1
        changesCollectorFactory.createCountingCollector(job, stream2) >> collector2
        and: 'collectors detect no changes in the streams'
        changeDetectionService.collectChangesForType({ it.code == 'Product' }, { it.streamId == 'products' }, collector1) >> { List args ->
            args[2].getNumberOfChangesCollected() >> 0
        }
        changeDetectionService.collectChangesForType({ it.code == 'Category' }, { it.streamId == 'categories' }, collector2) >> { List args ->
            args[2].getNumberOfChangesCollected() >> 0
        }
        and: 'final job state'
        jobRegister.getNewJob(job) >> Stub(OutboundSyncJob) {
            getFinalState() >> JOB_STATE
        }

        when:
        def result = cronJobPerformable.perform job

        then:
        1 * eventService.publishEvent(_) >> { List args ->
            def event = args[0] as StartedOutboundSyncEvent
            assert event.cronJobPk == CRON_JOB_PK
            assert event.itemCount == 0
            assert event.startTime
        }
        and:
        result.status == JOB_STATE.cronJobStatus
        result.result == JOB_STATE.cronJobResult
    }

    @Test
    def "perform result is determined by the job state when one stream has #changes1 changes and second stream has #changes2 changes"() {
        given:
        def stream1 = streamConfiguration('products', 'Product')
        def stream2 = streamConfiguration('categories', 'Category')
        and:
        def jobModel = defaultCronJob([stream1, stream2])
        and: 'collectors corresponding to the streams'
        def collector1 = Stub CountingChangesCollector
        def collector2 = Stub CountingChangesCollector
        changesCollectorFactory.createCountingCollector(jobModel, stream1) >> collector1
        changesCollectorFactory.createCountingCollector(jobModel, stream2) >> collector2
        and: 'collectors detect no changes in the streams'
        changeDetectionService.collectChangesForType({ it.code == 'Product' }, { it.streamId == 'products' }, collector1) >> { List args ->
            args[2].getNumberOfChangesCollected() >> changes1
        }
        changeDetectionService.collectChangesForType({ it.code == 'Category' }, { it.streamId == 'categories' }, collector2) >> { List args ->
            args[2].getNumberOfChangesCollected() >> changes2
        }
        and: 'the job has finished running'
        jobRegister.getNewJob(jobModel) >> Stub(OutboundSyncJob) {
            getFinalState() >> JOB_STATE
        }

        when:
        def result = cronJobPerformable.perform jobModel

        then:
        1 * eventService.publishEvent(_) >> { List args ->
            def event = args[0] as StartedOutboundSyncEvent
            assert event.cronJobPk == CRON_JOB_PK
            assert event.itemCount == changes1 + changes2
            assert event.startTime
        }
        and:
        result.status == JOB_STATE.cronJobStatus
        result.result == JOB_STATE.cronJobResult

        where:
        changes1 | changes2
        0        | 1
        1        | 0
        1        | 1
    }

    @Test
    def "perform does not process excluded types configured in the stream configuration"() {
        given:
        def stream1 = streamConfiguration('products', 'Product', [composedTypeModel('VariantProduct')] as Set)
        and:
        def jobModel = defaultCronJob([stream1])
        and: 'collectors corresponding to the streams'
        def collector1 = Stub CountingChangesCollector
        changesCollectorFactory.createCountingCollector(jobModel, stream1) >> collector1

        when:
        cronJobPerformable.perform jobModel

        then:
        1 * changeDetectionService.collectChangesForType(_ as ComposedTypeModel, _ as StreamConfiguration, collector1) >> { List args ->
            assert args[0].code == 'Product'
            assert args[1].streamId == 'products'
            assert args[1].excludedTypeCodes[0] == 'VariantProduct'
        }
    }

    @Test
    def "jobPerformable can be aborted"() {
        expect:
        cronJobPerformable.isAbortable()
    }

    @Test
    def 'abort status is reset when the job finishes'() {
        given: 'abort is requested'
        def cronJobModel = abortedCronJob()
        and: 'job finishes'
        jobRegister.getNewJob(cronJobModel) >> Stub(OutboundSyncJob) {
            getFinalState() >> JOB_STATE
        }

        when:
        cronJobPerformable.perform cronJobModel

        then: 'request aborted is reset'
        1 * cronJobModel.setRequestAbort(null)
        then: 'job model is saved'
        1 * modelService.save(cronJobModel)
    }

    private ComposedTypeModel composedTypeModel(String type) {
        Stub(ComposedTypeModel) {
            getCode() >> type
        }
    }

    private OutboundSyncCronJobModel abortedCronJob() {
        Mock(OutboundSyncCronJobModel) {
            getPk() >> CRON_JOB_PK
            getRequestAbort() >> true
            getJob() >> job(streamContainer())
        }
    }

    private OutboundSyncCronJobModel defaultCronJob(Map streamIdTypeCodeMap = [:]) {
        defaultCronJob streamConfigurations(streamIdTypeCodeMap)
    }

    private OutboundSyncCronJobModel defaultCronJob(List streams) {
        def container = streamContainer(streams)
        cronJobWith job(container)
    }

    private OutboundSyncCronJobModel cronJobWith(OutboundSyncJobModel job) {
        Stub(OutboundSyncCronJobModel) {
            getJob() >> job
            getPk() >> CRON_JOB_PK
        }
    }

    private OutboundSyncJobModel job(OutboundSyncStreamConfigurationContainerModel container) {
        Stub(OutboundSyncJobModel) {
            getStreamConfigurationContainer() >> container
        }
    }

    private OutboundSyncStreamConfigurationContainerModel streamContainer(List streams = []) {
        Stub(OutboundSyncStreamConfigurationContainerModel) {
            getConfigurations() >> streams
            getId() >> "outboundSyncDataStreams"
        }
    }

    private List<OutboundSyncStreamConfigurationModel> streamConfigurations(Map streamIdTypeCodeMap) {
        streamIdTypeCodeMap.collect {
            streamId, typeCode -> streamConfiguration(streamId, typeCode)
        }
    }

    private OutboundSyncStreamConfigurationModel streamConfiguration(def streamId, def typeCode) {
        Stub(OutboundSyncStreamConfigurationModel) {
            getStreamId() >> streamId
            getItemTypeForStream() >> Stub(ComposedTypeModel) {
                getCode() >> typeCode
            }
        }
    }

    private OutboundSyncStreamConfigurationModel streamConfiguration(def streamId, def typeCode, Set excludedTypes) {
        def configuration = streamConfiguration(streamId, typeCode)
        configuration.getExcludedTypes() >> excludedTypes
        configuration
    }
}
