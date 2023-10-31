/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.outboundsync;

import de.hybris.platform.cronjob.model.TriggerModel;
import de.hybris.platform.impex.jalo.ImpExException;
import de.hybris.platform.integrationservices.util.IntegrationTestUtil;
import de.hybris.platform.outboundsync.model.OutboundSyncCronJobModel;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.junit.rules.ExternalResource;

import com.google.common.base.Preconditions;

/**
 * Builder for a {@link TriggerModel}
 */
public class TriggerBuilder extends ExternalResource
{
	private static final String DEFAULT_CRONJOB_EXPRESSION = "0 0 0 * * ? 2000";

	private final Set<OutboundSyncCronJobBuilder> createdCronJobs;

	private String cronJobExpression;
	private OutboundSyncCronJobModel cronJob;
	private OutboundSyncCronJobBuilder cronJobBuilder;

	private TriggerBuilder()
	{
		createdCronJobs = new HashSet<>();
	}

	public static TriggerBuilder triggerBuilder()
	{
		return new TriggerBuilder();
	}

	public TriggerBuilder withCronJobExpression(final String cronJobExpression)
	{
		this.cronJobExpression = cronJobExpression;
		return this;
	}

	public TriggerBuilder withCronJob(final OutboundSyncCronJobModel cronJob)
	{
		this.cronJob = cronJob;
		cronJobBuilder = null;
		return this;
	}

	public TriggerBuilder withCronJob(final OutboundSyncCronJobBuilder cronJobBuilder)
	{
		this.cronJobBuilder = cronJobBuilder;
		cronJob = null;
		return this;
	}

	public TriggerModel build() throws ImpExException
	{
		final var derivedCronJobExpression = cronJobExpression != null ? cronJobExpression : DEFAULT_CRONJOB_EXPRESSION;
		final OutboundSyncCronJobModel derivedCronJob = deriveCronJob();

		IntegrationTestUtil.importImpEx(
				"INSERT_UPDATE Trigger; cronExpression[unique = true]; cronJob[unique = true]",
				"                     ; " + derivedCronJobExpression + "          ; " + derivedCronJob.getPk());
		return triggerFromExpAndCronJob(derivedCronJobExpression, derivedCronJob).orElse(null);
	}

	private Optional<TriggerModel> triggerFromExpAndCronJob(final String cronJobExpression,
	                                                        final OutboundSyncCronJobModel cronJob)
	{
		return IntegrationTestUtil.findAny(TriggerModel.class,
				m -> m.getCronExpression().equals(cronJobExpression) && m.getCronJob().equals(cronJob));
	}

	private OutboundSyncCronJobModel deriveCronJob() throws ImpExException
	{
		return cronJob != null ? cronJob : buildCronJob();
	}

	private OutboundSyncCronJobModel buildCronJob() throws ImpExException
	{
		Preconditions.checkArgument(cronJobBuilder != null, "cronJob cannot be null");
		createdCronJobs.add(cronJobBuilder);
		return cronJobBuilder.build();
	}

	@Override
	protected void after()
	{
		cleanup();
	}

	public void cleanup()
	{
		//Created Triggers will be removed automatically because Trigger has partOf relation with OutboundSyncCronJobModel.
		createdCronJobs.forEach(OutboundSyncCronJobBuilder::cleanup);
		createdCronJobs.clear();
	}
}
