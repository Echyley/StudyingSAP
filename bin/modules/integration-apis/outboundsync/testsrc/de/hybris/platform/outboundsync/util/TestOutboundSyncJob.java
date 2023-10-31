/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.outboundsync.util;

import static java.util.concurrent.TimeUnit.SECONDS;

import de.hybris.platform.core.Registry;
import de.hybris.platform.cronjob.enums.CronJobResult;
import de.hybris.platform.cronjob.enums.CronJobStatus;
import de.hybris.platform.cronjob.model.CronJobModel;
import de.hybris.platform.integrationservices.util.EventualCondition;
import de.hybris.platform.integrationservices.util.IntegrationTestUtil;
import de.hybris.platform.integrationservices.util.Log;
import de.hybris.platform.integrationservices.util.TenantAdminUserAwareThreadFactory;
import de.hybris.platform.servicelayer.cronjob.CronJobService;
import de.hybris.platform.servicelayer.cronjob.PerformResult;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeoutException;

public class TestOutboundSyncJob
{
	private static final Log LOG = Log.getLogger(TestOutboundSyncJob.class);
	private static final CronJobService cronJobService = Registry.getApplicationContext()
	                                                             .getBean("cronJobService", CronJobService.class);
	private static final TenantAdminUserAwareThreadFactory threadFactory = Registry.getApplicationContext()
	                                                                               .getBean("outboundSyncThreadFactory", TenantAdminUserAwareThreadFactory.class);
	private static final int JOB_COMPLETION_TIMEOUT = 30;

	private final CronJobModel job;

	public TestOutboundSyncJob() {
		this(contextCronJob());
	}

	private TestOutboundSyncJob(final CronJobModel model)
	{
		job = model;
	}

	public PerformResult perform() {
		run();
		waitForJobModelUpdate();
		return new PerformResult(job.getResult(), job.getStatus());
	}

	/**
	 * After {@code cronJobService.performCronJob()} method finishes, it updates the job model with the PerformResult received
	 * from the {@link de.hybris.platform.servicelayer.cronjob.JobPerformable} in a separate thread. Without waiting for the job
	 * model being updated, the race condition results in sometimes seeing the running state after the job has finished already.
	 */
	private void waitForJobModelUpdate()
	{
		EventualCondition.eventualCondition().waitFor(() -> refresh().getStatus() != CronJobStatus.RUNNING);
	}

	public void run() {
		LOG.info("Performing {}", job);
		cronJobService.performCronJob(job, true);
	}

	public void abort() {
		LOG.info("Aborting {}", job);
		cronJobService.requestAbortCronJob(job);
	}

	public TestOutboundSyncJob refresh() {
		IntegrationTestUtil.refresh(job);
		LOG.info("Fresh state: {}", toString());
		return this;
	}

	public CronJobStatus getStatus() {
		return job.getStatus();
	}

	public CronJobResult getResult() {
		return job.getResult();
	}

	public CronJobModel getJob() {
		return job;
	}

	public PerformResult performInSeparateThread() throws ExecutionException, InterruptedException, TimeoutException
	{
		return CompletableFuture.supplyAsync(this::perform, Executors.newFixedThreadPool(1, threadFactory))
				.get(JOB_COMPLETION_TIMEOUT, SECONDS);
	}

	public String toString() {
		return job.toString() + ": " + job.getStatus() + " " + job.getResult();
	}

	private static CronJobModel contextCronJob() {
		return OutboundSyncTestUtil.outboundCronJob();
	}
}
