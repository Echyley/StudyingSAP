/*
 *  Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.outboundsync;

import de.hybris.platform.impex.jalo.ImpExException;
import de.hybris.platform.integrationservices.util.IntegrationTestUtil;
import de.hybris.platform.outboundsync.model.OutboundSyncJobModel;
import de.hybris.platform.outboundsync.model.OutboundSyncStreamConfigurationContainerModel;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.junit.rules.ExternalResource;

import com.google.common.base.Preconditions;

/**
 * Builder for a {@link OutboundSyncJobModel}
 */
public class OutboundSyncJobBuilder extends ExternalResource
{
	private final Set<String> createdJobs;
	private final Set<OutboundSyncStreamConfigurationContainerBuilder> createdContainers;

	private String jobCode;
	private OutboundSyncStreamConfigurationContainerModel container;
	private OutboundSyncStreamConfigurationContainerBuilder containerBuilder;


	private OutboundSyncJobBuilder()
	{
		createdJobs = new HashSet<>();
		createdContainers = new HashSet<>();
	}

	public static OutboundSyncJobBuilder outboundSyncJob()
	{
		return new OutboundSyncJobBuilder();
	}

	public OutboundSyncJobBuilder withJobCode(final String jobCode)
	{
		this.jobCode = jobCode;
		return this;
	}

	public OutboundSyncJobBuilder withOutboundSyncStreamConfigurationContainer(
			final OutboundSyncStreamConfigurationContainerBuilder builder)
	{
		containerBuilder = builder;
		container = null;
		return this;
	}

	public OutboundSyncJobBuilder withOutboundSyncStreamConfigurationContainer(
			final OutboundSyncStreamConfigurationContainerModel container)
	{
		this.container = container;
		containerBuilder = null;
		return this;
	}

	public OutboundSyncJobModel build() throws ImpExException
	{
		Preconditions.checkArgument(jobCode != null, "job code cannot be null.");
		final OutboundSyncStreamConfigurationContainerModel derivedContainer = deriveContainer();

		IntegrationTestUtil.importImpEx(
				"INSERT_UPDATE OutboundSyncJob; code[unique = true]; streamConfigurationContainer(id)",
				"                             ; " + jobCode + "          ; " + derivedContainer.getId());

		createdJobs.add(jobCode);
		return jobByCode(jobCode).orElse(null);
	}

	private Optional<OutboundSyncJobModel> jobByCode(final String code)
	{
		return IntegrationTestUtil.findAny(OutboundSyncJobModel.class, m -> m.getCode().equals(code));
	}

	private OutboundSyncStreamConfigurationContainerModel deriveContainer() throws ImpExException
	{
		return container != null ? container : buildContainer();
	}

	private OutboundSyncStreamConfigurationContainerModel buildContainer() throws ImpExException
	{
		Preconditions.checkArgument(containerBuilder != null, "container cannot be null");
		createdContainers.add(containerBuilder);
		return containerBuilder.build();
	}

	@Override
	protected void after()
	{
		cleanup();
	}

	public void cleanup()
	{
		IntegrationTestUtil.remove(OutboundSyncJobModel.class, it -> createdJobs.contains(it.getCode()));
		createdJobs.clear();

		createdContainers.forEach(OutboundSyncStreamConfigurationContainerBuilder::cleanup);
		createdContainers.clear();
	}
}
