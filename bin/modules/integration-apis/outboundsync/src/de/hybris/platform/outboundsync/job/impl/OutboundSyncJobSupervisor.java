/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.outboundsync.job.impl;

import de.hybris.platform.core.PK;
import de.hybris.platform.cronjob.enums.CronJobResult;
import de.hybris.platform.cronjob.enums.CronJobStatus;
import de.hybris.platform.cronjob.model.CronJobModel;
import de.hybris.platform.integrationservices.util.Log;
import de.hybris.platform.servicelayer.cronjob.AbstractJobPerformable;
import de.hybris.platform.servicelayer.cronjob.PerformResult;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.validation.constraints.NotNull;

import org.slf4j.Logger;

import com.google.common.base.Preconditions;

/**
 * Keeps track of outbound sync cron jobs and stops "stuck" jobs, if it sees no activity in them.
 */
public class OutboundSyncJobSupervisor extends AbstractJobPerformable<CronJobModel>
{
	private static final Logger LOG = Log.getLogger(OutboundSyncJobSupervisor.class);
	private static final PerformResult PERFORM_RESULT = new PerformResult(CronJobResult.SUCCESS, CronJobStatus.FINISHED);

	private final OutboundSyncJobRegister jobRegister;
	private final Map<PK, OutboundSyncState> supervisedJobs;

	/**
	 * Instantiates this supervisor job.
	 *
	 * @param register register of outbound sync jobs to acquire about currently running jobs.
	 * @see OutboundSyncJobRegister#getRunningJobs()
	 */
	public OutboundSyncJobSupervisor(@NotNull final OutboundSyncJobRegister register)
	{
		Preconditions.checkArgument(register != null, "OutboundSyncJobRegister cannot be null");
		jobRegister = register;
		supervisedJobs = new HashMap<>();
	}

	@Override
	public PerformResult perform(final CronJobModel cronJobModel)
	{
		LOG.info("Checking for stuck outbound sync jobs");
		final Collection<OutboundSyncJob> runningJobs = jobRegister.getRunningJobs();
		stopStuckJobs(runningJobs);
		refreshSupervisedJobs(runningJobs);
		return PERFORM_RESULT;
	}

	private void stopStuckJobs(final Collection<OutboundSyncJob> jobs)
	{
		jobs.stream()
		    .filter(this::isStuck)
		    .forEach(this::stop);
	}

	private boolean isStuck(final OutboundSyncJob job)
	{
		final var registeredState = supervisedJobs.get(job.getId());
		return registeredState != null && registeredState.equals(job.getCurrentState());
	}

	private void stop(final OutboundSyncJob job)
	{
		LOG.info("Stopping job that is stuck Running: {}", job);
		job.stop();
	}

	private void refreshSupervisedJobs(final Collection<OutboundSyncJob> jobs)
	{
		supervisedJobs.clear();
		jobs.forEach(j -> supervisedJobs.put(j.getId(), j.getCurrentState()));
	}

	Map<PK, OutboundSyncState> getSupervisedJobs()
	{
		return Map.copyOf(supervisedJobs);
	}
}
