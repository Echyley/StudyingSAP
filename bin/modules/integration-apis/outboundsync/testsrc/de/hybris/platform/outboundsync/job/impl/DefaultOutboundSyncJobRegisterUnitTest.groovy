/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.outboundsync.job.impl

import de.hybris.bootstrap.annotations.UnitTest
import de.hybris.platform.core.PK
import de.hybris.platform.cronjob.model.CronJobModel
import de.hybris.platform.outboundsync.model.OutboundSyncCronJobModel
import de.hybris.platform.servicelayer.exceptions.ModelLoadingException
import de.hybris.platform.servicelayer.model.ModelService
import de.hybris.platform.testframework.JUnitPlatformSpecification
import org.junit.Test

import static OutboundSyncStateBuilder.initialOutboundSyncState

@UnitTest
class DefaultOutboundSyncJobRegisterUnitTest extends JUnitPlatformSpecification {
    private static final def EXISTING_JOB_PK = PK.fromLong 200
    private static final def NOT_EXISTING_JOB_PK = PK.fromLong 404
    private static final def NOT_OUTBOUND_JOB_PK = PK.fromLong 500

    def modelService = Mock(ModelService) {
        get(EXISTING_JOB_PK) >> jobModel(EXISTING_JOB_PK)
        get(NOT_EXISTING_JOB_PK) >> { throw new ModelLoadingException('') }
        get(NOT_OUTBOUND_JOB_PK) >> new CronJobModel()
    }

    def jobRegister = new DefaultOutboundSyncJobRegister(modelService)

    @Test
    def "does not create new job when its model primary key is not for an #condition"() {
        expect:
        jobRegister.getJob(pk).empty
        jobRegister.runningJobs.empty

        where:
        condition                 | pk
        'existing item model'     | NOT_EXISTING_JOB_PK
        'outbound sync job model' | NOT_OUTBOUND_JOB_PK
    }

    @Test
    def 'creates new job when it does not exist'() {
        expect:
        jobRegister.getJob(EXISTING_JOB_PK).present
    }

    @Test
    def 'adds itself as observer to the created job instance'() {
        given:
        def job = jobRegister.getJob jobModel(EXISTING_JOB_PK)

        expect:
        job.stateObservers.contains jobRegister
    }

    @Test
    def 'retrieves existing job instance by its primary key when it has been already requested'() {
        given:
        def model = jobModel()
        def job = jobRegister.getNewJob(model)

        expect:
        jobRegister.getJob(model.pk) == Optional.of(job)
    }

    @Test
    def 'retrieves existing job instance by its model when it has been already requested'() {
        given:
        def jobModel = jobModel()
        def job = jobRegister.getNewJob(jobModel)

        expect:
        jobRegister.getJob(jobModel).is job
    }

    @Test
    def 'retrieves different job instances when the job models are different'() {
        given: 'a job is registered'
        def existingJob = jobRegister.getJob(EXISTING_JOB_PK).get()
        and: 'another another job exists with a different key'
        def differentPk = PK.fromLong 201
        modelService.get(differentPk) >> jobModel(differentPk)

        expect: 'different job for the different model'
        jobRegister.getJob(differentPk).get() != existingJob
    }

    @Test
    def 'always retrieves new fresh instance of the job when getNewJob() is called'() {
        given:
        def jobModel = jobModel(EXISTING_JOB_PK)
        def origJob = jobRegister.getNewJob(jobModel)

        expect:
        !jobRegister.getNewJob(jobModel).is(origJob)
    }

    @Test
    def 'registers the job when a new job is requested'() {
        when:
        def newJob = jobRegister.getNewJob jobModel()

        then:
        jobRegister.runningJobs == [newJob]
    }

    @Test
    def "does not register a job when a job is requested by a #paramType"() {
        when:
        jobRegister.getJob param

        then:
        jobRegister.runningJobs.isEmpty()

        where:
        param           | paramType
        EXISTING_JOB_PK | 'PK'
        jobModel()      | 'job model'
    }

    @Test
    def "unregisters the job when the job state changes to #condition"() {
        given: 'job is registered'
        def jobModel = jobModel()
        jobRegister.getNewJob(jobModel)

        when:
        jobRegister.stateChanged jobModel, newState

        then: 'the job is unregistered'
        jobRegister.runningJobs.empty

        where:
        condition  | newState
        'finished' | initialOutboundSyncState().withTotalItems(1).withSuccessCount(1).build()
        'aborted'  | initialOutboundSyncState().withAborted(true).build()
        'failure'  | initialOutboundSyncState().withSystemicError(true).build()
    }

    @Test
    def 'keeps the job registered when the job changed to an unfinished state'() {
        given: 'job is registered'
        def jobModel = jobModel(EXISTING_JOB_PK)
        def origJob = jobRegister.getNewJob jobModel
        and: 'the job has started'
        def newState = initialOutboundSyncState()
                .withStartTime(new Date())
                .withTotalItems(1)
                .build()

        when:
        jobRegister.stateChanged(jobModel, newState)

        then: 'the original job is still kept'
        jobRegister.runningJobs == [origJob]
    }

    @Test
    def 'retrieves all running jobs'() {
        given: 'a job is running'
        def job1 = jobRegister.getNewJob jobModel(PK.fromLong(1))
        and: 'another job is running'
        def job2 = jobRegister.getNewJob jobModel(PK.fromLong(2))

        expect:
        jobRegister.runningJobs.size() == 2
        jobRegister.runningJobs.containsAll([job1, job2])
    }

    @Test
    def 'retrieved running jobs are immutable'() {
        given: 'a job is running'
        jobRegister.getNewJob jobModel(PK.fromLong(1))

        when: 'the running jobs collection is attempted to be modified'
        jobRegister.runningJobs.clear()

        then: 'exception is thrown'
        thrown UnsupportedOperationException
    }

    @Test
    def "isRunning is false when the job has not been requested yet for #pk PK"() {
        expect: 'job is not running if getNewJob or getJob was not called'
        !jobRegister.isRunning(pk)

        where:
        pk << [null, EXISTING_JOB_PK]
    }

    @Test
    def 'the job is running after new job was requested'() {
        given: 'the job is requested'
        jobRegister.getNewJob jobModel(EXISTING_JOB_PK)

        expect:
        jobRegister.isRunning(EXISTING_JOB_PK)
    }

    @Test
    def "the job is not running when it was requested by a #condition"() {
        given: 'the job is requested'
        jobRegister.getJob param

        expect:
        !jobRegister.isRunning(EXISTING_JOB_PK)

        where:
        param                     | condition
        EXISTING_JOB_PK           | 'PK'
        jobModel(EXISTING_JOB_PK) | 'job model'
    }

    private OutboundSyncCronJobModel jobModel(PK key = EXISTING_JOB_PK) {
        Mock(OutboundSyncCronJobModel) {
            getPk() >> key
        }
    }
}