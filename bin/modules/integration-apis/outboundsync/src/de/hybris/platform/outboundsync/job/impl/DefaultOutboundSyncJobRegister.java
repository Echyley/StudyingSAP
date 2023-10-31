/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.outboundsync.job.impl;

import de.hybris.platform.core.PK;
import de.hybris.platform.integrationservices.util.Log;
import de.hybris.platform.outboundsync.config.impl.OutboundSyncConfiguration;
import de.hybris.platform.outboundsync.model.OutboundSyncCronJobModel;
import de.hybris.platform.servicelayer.exceptions.ModelLoadingException;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import javax.validation.constraints.NotNull;

import org.slf4j.Logger;

/**
 * Implementation of the {@link OutboundSyncJobRegister} that is used by default when no custom implementation is injected.
 */
public class DefaultOutboundSyncJobRegister implements OutboundSyncJobRegister, OutboundSyncStateObserver
{
	private static final Logger LOG = Log.getLogger(DefaultOutboundSyncJobRegister.class);

	private final Map<PK, OutboundSyncJobStateAggregator> allRunningJobs;
	private final ModelService modelService;

	public DefaultOutboundSyncJobRegister(@NotNull final ModelService service)
	{
		modelService = service;
		allRunningJobs = new ConcurrentHashMap<>();
	}

	@Override
	public @NotNull OutboundSyncJob getNewJob(@NotNull final OutboundSyncCronJobModel jobModel)
	{
		unregister(jobModel);
		final var job = createJobStateAggregator(jobModel);
		register(job);
		return job;
	}

	@Override
	public Optional<OutboundSyncJob> getJob(@NotNull final PK jobPk)
	{
		final OutboundSyncJob aggregator = allRunningJobs.get(jobPk);
		if (aggregator == null)
		{
			return findJobModel(jobPk).map(this::getJob);
		}
		return Optional.of(aggregator);
	}

	@Override
	public @NotNull OutboundSyncJob getJob(@NotNull final OutboundSyncCronJobModel model)
	{
		final OutboundSyncJob job = allRunningJobs.get(model.getPk());
		return job == null ? createJobStateAggregator(model) : job;
	}

	@Override
	public Collection<OutboundSyncJob> getRunningJobs()
	{
		return List.copyOf(allRunningJobs.values());
	}

	@Override
	public boolean isRunning(final PK id)
	{
		return id != null && allRunningJobs.containsKey(id);
	}

	private Optional<OutboundSyncCronJobModel> findJobModel(final @NotNull PK jobPk)
	{
		LOG.debug("Searching for a job model corresponding to {} PK", jobPk);
		try
		{
			final Object item = modelService.get(jobPk);
			return Optional.ofNullable(item)
			               .filter(OutboundSyncCronJobModel.class::isInstance)
			               .map(OutboundSyncCronJobModel.class::cast);
		}
		catch (final ModelLoadingException e)
		{
			return Optional.empty();
		}
	}

	/**
	 * Creates new instance of the {code {@link OutboundSyncJobStateAggregator}}. This method should only create the aggregator but
	 * not register it yet.
	 *
	 * @param job a model for which an aggregator should be created.
	 * @return new instance of the aggregator.
	 */
	protected OutboundSyncJobStateAggregator createJobStateAggregator(final @NotNull OutboundSyncCronJobModel job)
	{
		final var aggregator = OutboundSyncJobStateAggregator.create(job);
		aggregator.addStateObserver(this);
		return aggregator;
	}

	@Override
	public void stateChanged(@NotNull final OutboundSyncCronJobModel model, @NotNull final OutboundSyncState state)
	{
		if (state.isFinal())
		{
			unregister(model);
		}
	}

	private void register(final OutboundSyncJobStateAggregator job)
	{
		LOG.debug("Registering new running job {}", job);
		allRunningJobs.put(job.getId(), job);
	}

	private void unregister(final OutboundSyncCronJobModel model)
	{
		LOG.debug("Unregistering job {}", model);
		allRunningJobs.remove(model.getPk());
	}

	/**
	 * Clears this registry so, that information about any running job is lost.
	 * This should be used only in tests and never in production code.
	 */
	void clear()
	{
		LOG.warn("Clearing the state of this registry");
		allRunningJobs.clear();
	}

	/**
	 * Injects implementation of the flexible search service
	 *
	 * @param flexibleSearchService service implementation to use
	 * @deprecated not used anymore in this register implementation
	 */
	@Deprecated(since = "2205", forRemoval = true)
	public void setFlexibleSearchService(final FlexibleSearchService flexibleSearchService)
	{
		// nothing to do - left for backwards compatibility
	}

	/**
	 * Injects implementation of the outbound sync configuration
	 *
	 * @param outboundSyncConfiguration configuration implementation to use
	 * @deprecated not used anymore in this register implementation
	 */
	@Deprecated(since = "2205", forRemoval = true)
	public void setOutboundSyncConfiguration(final OutboundSyncConfiguration outboundSyncConfiguration)
	{
		// nothing to do - left for backwards compatibility
	}
}
