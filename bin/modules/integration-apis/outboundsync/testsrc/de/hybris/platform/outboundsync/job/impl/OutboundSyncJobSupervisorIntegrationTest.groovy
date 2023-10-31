/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.outboundsync.job.impl

import de.hybris.bootstrap.annotations.IntegrationTest
import de.hybris.platform.cronjob.enums.CronJobResult
import de.hybris.platform.cronjob.enums.CronJobStatus
import de.hybris.platform.cronjob.model.CronJobModel
import de.hybris.platform.integrationservices.util.IntegrationTestUtil
import de.hybris.platform.outboundsync.events.CompletedOutboundSyncEvent
import de.hybris.platform.outboundsync.events.StartedOutboundSyncEvent
import de.hybris.platform.outboundsync.model.OutboundSyncCronJobModel
import de.hybris.platform.outboundsync.util.OutboundSyncTestUtil
import de.hybris.platform.servicelayer.ServicelayerSpockSpecification
import org.junit.Rule
import org.junit.Test
import org.junit.rules.ExternalResource

import javax.annotation.Resource

@IntegrationTest
class OutboundSyncJobSupervisorIntegrationTest extends ServicelayerSpockSpecification {
    private static final def TEST_NAME = 'OutboundSyncJobSupervisor'
    private static final String SUPERVISOR_BEAN_NAME = 'outboundSyncJobSupervisor'

    @Rule
    public OutboundSyncJobSimulation outboundSyncJob = new OutboundSyncJobSimulation()
    @Rule
    public SupervisorJob supervisorJob = new SupervisorJob()

    @Resource(name = SUPERVISOR_BEAN_NAME)
    OutboundSyncJobSupervisor supervisorPerformable
    @Resource(name = 'outboundSyncJobRegister')
    DefaultOutboundSyncJobRegister jobRegister

    @Test
    def 'supervisor detects a started job and leaves it running'() {
        given: 'an outbound sync job has started'
        outboundSyncJob.run()

        when: 'the supervisor is executed'
        supervisorJob.run()

        then: 'the outbound sync job is still running'
        outboundSyncJob.state.cronJobStatus == CronJobStatus.RUNNING
        outboundSyncJob.running
    }

    @Test
    def 'supervisor aborts an outbound sync job if its state has not changed since the previous run'() {
        given: 'an outbound sync job has started'
        outboundSyncJob.run()
        and: 'the supervisor was executed and detected the outbound sync job'
        supervisorJob.run()

        when: 'the supervisor runs again before the outbound sync state changed'
        supervisorJob.run()

        then: 'the outbound sync job is aborted'
        outboundSyncJob.state.cronJobStatus == CronJobStatus.ABORTED
        !outboundSyncJob.running
    }

    @Test
    def 'supervisor leaves an outbound sync job running if its status has changed since the previous run'() {
        given: 'an outbound sync job has started'
        outboundSyncJob.run()
        and: 'the supervisor was executed and detected the outbound sync job'
        supervisorJob.run()
        and: 'the outbound sync job changed its state before the second supervisor run'
        outboundSyncJob.itemProcessed()

        when: 'the supervisor runs again before the outbound sync state changed'
        supervisorJob.run()

        then: 'the outbound sync job is aborted'
        outboundSyncJob.state.cronJobStatus == CronJobStatus.RUNNING
        outboundSyncJob.running
    }

    class OutboundSyncJobSimulation extends ExternalResource {
        private static final int NUMBER_OF_ITEMS = 2

        private OutboundSyncCronJobModel cronJob
        private OutboundSyncJob delegateJob

        @Override
        protected void before() {
            cronJob = OutboundSyncTestUtil.createOutboundSyncJob "${TEST_NAME}_Worker"
        }

        @Override
        protected void after() {
            jobRegister.clear()
            finish()
            reset()
        }

        void run() {
            delegateJob = jobRegister.getNewJob cronJob
            delegateJob.applyEvent new StartedOutboundSyncEvent(cronJob.pk, new Date(), NUMBER_OF_ITEMS)
        }

        void itemProcessed() {
            delegateJob.applyEvent new CompletedOutboundSyncEvent(cronJob.pk, true, 1)
        }

        private void finish() {
            OutboundSyncTestUtil.updateCronJobResult(cronJob, CronJobResult.SUCCESS, CronJobStatus.FINISHED)
        }

        private void reset() {
            delegateJob = null
            if (cronJob) {
                def jobModel = cronJob.job
                IntegrationTestUtil.remove cronJob.job.streamConfigurationContainer
                IntegrationTestUtil.remove cronJob
                IntegrationTestUtil.remove jobModel
                cronJob = null
            }
        }

        OutboundSyncState getState() {
            delegateJob.getCurrentState()
        }

        boolean isRunning() {
            jobRegister.isRunning cronJob.pk
        }
    }

    class SupervisorJob extends ExternalResource {
        private CronJobModel cronJob

        @Override
        protected void before() {
            cronJob = OutboundSyncTestUtil.createCronJob TEST_NAME, SUPERVISOR_BEAN_NAME
        }

        @Override
        protected void after() {
            reset()
        }

        void run() {
            supervisorPerformable.perform cronJob
        }

        private void reset() {
            if (cronJob) {
                def jobModel = cronJob.job
                IntegrationTestUtil.remove cronJob
                IntegrationTestUtil.remove jobModel
                cronJob = null
            }
        }
    }
}
