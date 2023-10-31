/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.outboundsync;

import de.hybris.platform.impex.jalo.ImpExException;
import de.hybris.platform.integrationservices.util.IntegrationTestUtil;
import de.hybris.platform.integrationservices.util.Log;
import de.hybris.platform.outboundsync.model.OutboundSyncStreamConfigurationContainerModel;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.junit.rules.ExternalResource;

import com.google.common.base.Preconditions;

/**
 * Builder for an {@link OutboundSyncStreamConfigurationContainerModel}
 */
public class OutboundSyncStreamConfigurationContainerBuilder extends ExternalResource
{
	private final Log LOGGER = Log.getLogger(OutboundSyncStreamConfigurationContainerBuilder.class);

	private final Set<String> createdContainers;
	private final Set<OutboundSyncStreamConfigurationBuilder> createdStreams;

	private String containerId;

	private OutboundSyncStreamConfigurationContainerBuilder()
	{
		createdContainers = new HashSet<>();
		createdStreams = new HashSet<>();
	}

	public static OutboundSyncStreamConfigurationContainerBuilder outboundSyncStreamConfigurationContainer()
	{
		return new OutboundSyncStreamConfigurationContainerBuilder();
	}

	public OutboundSyncStreamConfigurationContainerBuilder withId(final String id)
	{
		containerId = id;
		return this;
	}

	public OutboundSyncStreamConfigurationContainerBuilder withStream(final OutboundSyncStreamConfigurationBuilder streamBuilder)
	{
		createdStreams.add(streamBuilder);
		return this;
	}

	public OutboundSyncStreamConfigurationContainerModel build() throws ImpExException
	{
		Preconditions.checkArgument(containerId != null, "container can not be null");
		IntegrationTestUtil.importImpEx(
				"INSERT_UPDATE OutboundSyncStreamConfigurationContainer; id[unique = true]",
				"                                                      ; " + containerId);
		createdContainers.add(containerId);

		return getContainerById(containerId)
				.map(this::containerWithStreams)
				.orElse(null);
	}

	private OutboundSyncStreamConfigurationContainerModel containerWithStreams(
			final OutboundSyncStreamConfigurationContainerModel container)
	{
		createdStreams.forEach(stream -> buildStream(stream, container));
		return container;
	}

	private void buildStream(final OutboundSyncStreamConfigurationBuilder streamBuilder,
	                         final OutboundSyncStreamConfigurationContainerModel container)
	{
		try
		{
			streamBuilder.withOutboundSyncStreamConfigurationContainer(container).build();
		}
		catch (final ImpExException e)
		{
			LOGGER.debug(e.getMessage());
		}
	}

	private Optional<OutboundSyncStreamConfigurationContainerModel> getContainerById(final String id)
	{
		return IntegrationTestUtil.findAny(OutboundSyncStreamConfigurationContainerModel.class, m -> m.getId().equals(id));
	}

	@Override
	protected void after()
	{
		cleanup();
	}

	/**
	 * Deletes all containers created by this builder.
	 */
	public void cleanup()
	{
		// Created Streams will be removed automatically because OutboundSyncStreamConfigurationModel has partOf
		// relation with OutboundSyncStreamConfigurationContainerModel.
		IntegrationTestUtil.remove(OutboundSyncStreamConfigurationContainerModel.class,
				m -> createdContainers.contains(m.getId()));
		createdContainers.clear();
		createdStreams.clear();
	}
}
