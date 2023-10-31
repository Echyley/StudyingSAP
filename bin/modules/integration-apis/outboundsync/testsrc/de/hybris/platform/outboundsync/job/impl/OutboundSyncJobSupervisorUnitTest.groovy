/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.outboundsync.job.impl

import de.hybris.bootstrap.annotations.UnitTest
import de.hybris.platform.core.PK
import de.hybris.platform.cronjob.enums.CronJobResult
import de.hybris.platform.cronjob.enums.CronJobStatus
import de.hybris.platform.cronjob.model.CronJobModel
import de.hybris.platform.testframework.JUnitPlatformSpecification
import org.junit.Test

@UnitTest
class OutboundSyncJobSupervisorUnitTest extends JUnitPlatformSpecification {
    private static final def SUPERVISOR_JOB = new CronJobModel()
    private static final def JOB_1_PK = PK.fromLong 1
    private static final def JOB_2_PK = PK.fromLong 2

    def jobRegistry = Stub OutboundSyncJobRegister
    def jobSupervisor = new OutboundSyncJobSupervisor(jobRegistry)

    @Test
    def 'cannot be instantiated without the job registry'() {
        when:
        new OutboundSyncJobSupervisor(null)

        then:
        def e = thrown IllegalArgumentException
        e.message == 'OutboundSyncJobRegister cannot be null'
    }

    @Test
    def 'does nothing when no jobs were executed and not running now'() {
        given:
        jobRegistry.getRunningJobs() >> []

        when:
        def result = jobSupervisor.perform SUPERVISOR_JOB

        then:
        result.result == CronJobResult.SUCCESS
        result.status == CronJobStatus.FINISHED
        jobSupervisor.supervisedJobs.isEmpty()
    }

    @Test
    def 'tracks state of the newly started job'() {
        given: 'register reports a running job'
        def job = job JOB_1_PK
        jobRegistry.getRunningJobs() >> [job]

        when:
        def result = jobSupervisor.perform SUPERVISOR_JOB

        then:
        result.result == CronJobResult.SUCCESS
        result.status == CronJobStatus.FINISHED
        jobSupervisor.supervisedJobs[JOB_1_PK].is job.currentState
        0 * job.stop()
    }

    @Test
    def 'keeps tracking previously detected job if its state changes'() {
        given: 'there are two jobs running on the first call and only one on the second'
        def firstRunJob = job JOB_1_PK, initialState()
        def secondRunJob = job JOB_1_PK, initialState().withSuccessCount(1) // state has changed for the job
        jobRegistry.getRunningJobs() >> [firstRunJob] >> [secondRunJob]
        and: 'the supervisor has detected the job on the first run'
        jobSupervisor.perform SUPERVISOR_JOB

        when: 'the supervisor detected the job in different state on the second run'
        def result = jobSupervisor.perform SUPERVISOR_JOB

        then:
        result.result == CronJobResult.SUCCESS
        result.status == CronJobStatus.FINISHED
        jobSupervisor.supervisedJobs.containsKey JOB_1_PK
        0 * firstRunJob.stop()
        0 * secondRunJob.stop()
    }

    @Test
    def 'stops tracking previously detected job that is not running anymore'() {
        given: 'there are two jobs running on the first call and only one on the second'
        def job1 = job JOB_1_PK
        def job2 = job JOB_2_PK, initialState().withSuccessCount(1) // state has changed for the job
        jobRegistry.getRunningJobs() >> [job1, job(JOB_2_PK)] >> [job2]
        and: 'the job supervisor has detected two jobs running'
        jobSupervisor.perform SUPERVISOR_JOB

        when: 'only second job is detected on the second run'
        def result = jobSupervisor.perform SUPERVISOR_JOB

        then:
        result.result == CronJobResult.SUCCESS
        result.status == CronJobStatus.FINISHED
        jobSupervisor.supervisedJobs.size() == 1
        jobSupervisor.supervisedJobs.containsKey JOB_2_PK
        0 * job1.stop()
        0 * job2.stop()
    }

    @Test
    def 'stops the job if it has not changed since previous supervisor run'() {
        given: 'there is a job stuck in the same state between the supervisor executions'
        def stuckJob = job JOB_1_PK
        jobRegistry.getRunningJobs() >> [stuckJob] >>  [stuckJob]
        and: 'the job supervisor has detected the running job'
        jobSupervisor.perform SUPERVISOR_JOB

        when: 'same state is returned on the second run'
        def result = jobSupervisor.perform SUPERVISOR_JOB

        then:
        result.result == CronJobResult.SUCCESS
        result.status == CronJobStatus.FINISHED
        jobSupervisor.supervisedJobs.containsKey JOB_1_PK
        1 * stuckJob.stop()
    }

    @Test
    def 'keeps tracking even a FINISHED job while it is registered in the register'() {
        given: 'there a job running on the first call and finished on the second'
        def initialJob = job JOB_1_PK, initialState()
        def finishedJob = job JOB_1_PK, initialState().withTotalItems(1).withSuccessCount(1)
        jobRegistry.getRunningJobs() >> [initialJob] >> [finishedJob]
        and: 'the supervisor has detected the job on the first run'
        jobSupervisor.perform SUPERVISOR_JOB

        when: 'the supervisor detected the job in the finished state on the second run'
        def result = jobSupervisor.perform SUPERVISOR_JOB

        then:
        result.result == CronJobResult.SUCCESS
        result.status == CronJobStatus.FINISHED
        jobSupervisor.supervisedJobs.containsKey JOB_1_PK
        0 * initialJob.stop()
        0 * finishedJob.stop()
    }

    private OutboundSyncJob job(PK id, OutboundSyncStateBuilder state = initialState()) {
        Mock(OutboundSyncJob) {
            getId() >> id
            getCurrentState() >> state.build()
        }
    }

    private static OutboundSyncStateBuilder initialState() {
        OutboundSyncStateBuilder.initialOutboundSyncState()
    }
}
