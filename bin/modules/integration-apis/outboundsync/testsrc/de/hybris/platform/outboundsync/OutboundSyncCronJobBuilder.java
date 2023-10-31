/*
 *  Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.outboundsync;

import de.hybris.platform.impex.jalo.ImpExException;
import de.hybris.platform.integrationservices.util.IntegrationTestUtil;
import de.hybris.platform.integrationservices.util.Log;
import de.hybris.platform.outboundsync.model.OutboundSyncCronJobModel;
import de.hybris.platform.outboundsync.model.OutboundSyncJobModel;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.junit.rules.ExternalResource;

import com.google.common.base.Preconditions;

/**
 * Builder for an {@link OutboundSyncCronJobModel}
 */
public class OutboundSyncCronJobBuilder extends ExternalResource
{
	private final Log LOGGER = Log.getLogger(OutboundSyncCronJobBuilder.class);

	private final Set<String> createdCronJobs;
	private final Set<OutboundSyncJobBuilder> createdSyncJobs;
	private final Set<TriggerBuilder> createdTriggers;

	private String cronJobCode;
	private OutboundSyncJobModel syncJob;
	private OutboundSyncJobBuilder syncJobBuilder;

	private OutboundSyncCronJobBuilder()
	{
		createdCronJobs = new HashSet<>();
		createdSyncJobs = new HashSet<>();
		createdTriggers = new HashSet<>();
	}

	public static OutboundSyncCronJobBuilder outboundSyncCronJobBuilder()
	{
		return new OutboundSyncCronJobBuilder();
	}

	public OutboundSyncCronJobBuilder withCronJobCode(final String cronJobCode)
	{
		this.cronJobCode = cronJobCode;
		return this;
	}

	public OutboundSyncCronJobBuilder withSyncJob(final OutboundSyncJobModel job)
	{
		syncJob = job;
		syncJobBuilder = null;
		return this;
	}

	public OutboundSyncCronJobBuilder withSyncJob(final OutboundSyncJobBuilder builder)
	{
		syncJobBuilder = builder;
		syncJob = null;
		return this;
	}

	public OutboundSyncCronJobBuilder withTrigger(final TriggerBuilder trigger)
	{
		createdTriggers.add(trigger);
		return this;
	}

	public OutboundSyncCronJobModel build() throws ImpExException
	{
		Preconditions.checkArgument(cronJobCode != null, "cron job code cannot be null");
		final OutboundSyncJobModel derivedSyncJob = deriveSyncJob();

		IntegrationTestUtil.importImpEx(
				"INSERT_UPDATE OutboundSyncCronJob; code[unique = true]; job",
				"                                 ; " + cronJobCode + "      ; " + derivedSyncJob.getPk());

		createdCronJobs.add(cronJobCode);

		return cronJobByCode(cronJobCode)
				.map(this::jobWithTriggers)
				.orElse(null);
	}

	private OutboundSyncCronJobModel jobWithTriggers(final OutboundSyncCronJobModel cronJob)
	{
		createdTriggers.forEach(trigger -> buildTrigger(trigger, cronJob));
		return cronJob;
	}

	private void buildTrigger(final TriggerBuilder triggerBuilder, final OutboundSyncCronJobModel cronJob)
	{
		try
		{
			triggerBuilder.withCronJob(cronJob).build();
		}
		catch (final ImpExException e)
		{
			LOGGER.debug(e.getMessage());
		}
	}

	private Optional<OutboundSyncCronJobModel> cronJobByCode(final String code)
	{
		return IntegrationTestUtil.findAny(OutboundSyncCronJobModel.class, m -> m.getCode().equals(code));
	}

	private OutboundSyncJobModel deriveSyncJob() throws ImpExException
	{
		return syncJob != null ? syncJob : buildSyncJob();
	}

	private OutboundSyncJobModel buildSyncJob() throws ImpExException
	{
		Preconditions.checkArgument(syncJobBuilder != null, "syncJob cannot be null");
		createdSyncJobs.add(syncJobBuilder);
		return syncJobBuilder.build();
	}

	@Override
	protected void after()
	{
		cleanup();
	}

	public void cleanup()
	{
		//Created Triggers will be removed automatically because Trigger has partOf relation with OutboundSyncCronJobModel.
		IntegrationTestUtil.remove(OutboundSyncCronJobModel.class, it -> createdCronJobs.contains(it.getCode()));
		createdCronJobs.clear();

		createdSyncJobs.forEach(OutboundSyncJobBuilder::cleanup);
		createdSyncJobs.clear();

		createdTriggers.clear();
	}
}
