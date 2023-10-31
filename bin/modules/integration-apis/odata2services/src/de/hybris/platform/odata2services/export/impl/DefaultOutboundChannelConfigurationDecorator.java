/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.odata2services.export.impl;

import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.integrationservices.model.DescriptorFactory;
import de.hybris.platform.integrationservices.service.IntegrationObjectConversionService;
import de.hybris.platform.integrationservices.service.IntegrationObjectService;
import de.hybris.platform.integrationservices.util.Log;
import de.hybris.platform.odata2services.dto.ExportEntity;
import de.hybris.platform.odata2services.export.ExportConfigurationSearchService;
import de.hybris.platform.odata2services.odata.schema.entity.EntitySetNameGenerator;

import java.util.Collection;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.validation.constraints.NotNull;

import org.slf4j.Logger;

/**
 * The outbound channel configuration decorator to export configuration with referenced outbound sync entities.
 */
public class DefaultOutboundChannelConfigurationDecorator extends AbstractExportConfigurationDecorator
{
	private static final Logger LOG = Log.getLogger(DefaultOutboundChannelConfigurationDecorator.class);

	private static final String OCC_IO = "OutboundChannelConfig";
	private static final String CODE_PATH = "code";
	private static final String CONTAINER_ID_PATH = "container.id";
	private static final String JOB_ENTITY_SET = "OutboundSyncJob";
	private static final String CRON_JOB_ENTITY_SET = "OutboundSyncCronJob";
	private static final String STREAM_CONFIG_ENTITY_SET = "OutboundSyncStreamConfiguration";
	private static final String TRIGGER_ENTITY_SET = "Trigger";

	private ExportConfigurationSearchService searchService;

	/**
	 * Instantiate a new instance of the outbound channel configuration decorator.
	 *
	 * @param descriptorFactory        to create the integration object descriptor
	 * @param conversionService        to convert the integration object
	 * @param integrationObjectService to find the integration object
	 * @param nameGenerator            to generate the entity set name
	 */
	public DefaultOutboundChannelConfigurationDecorator(
			@NotNull final DescriptorFactory descriptorFactory,
			@NotNull final IntegrationObjectConversionService conversionService,
			@NotNull final IntegrationObjectService integrationObjectService,
			@NotNull final EntitySetNameGenerator nameGenerator)
	{
		super(descriptorFactory, conversionService, integrationObjectService, nameGenerator);
	}

	@Override
	public Set<ExportEntity> decorate(final Set<ExportEntity> exportEntities)
	{
		final Set<String> streamConfigs = decorateStreamConfigs(exportEntities);
		final Set<String> jobs = decorateJobs(exportEntities, streamConfigs);
		final Set<String> cronJobs = decorateCronJobs(exportEntities, jobs);
		return decorateTriggers(exportEntities, cronJobs);
	}

	private Set<String> decorateStreamConfigs(final Set<ExportEntity> exportEntities)
	{
		final Set<String> streamConfigs = exportEntities.stream()
		                                                .filter(this::isOutboundChannelConfiguration)
		                                                .flatMap(exportEntity -> exportEntity.getRequestBodies().stream())
		                                                .map(request -> extractProperty(request, CODE_PATH))
		                                                .map(occCode -> searchService.findSteamConfigurations(occCode))
		                                                .flatMap(Collection::stream)
		                                                .filter(Objects::nonNull)
		                                                .map(this::constructPayload).collect(Collectors.toSet());
		if (!streamConfigs.isEmpty())
		{
			decorateExportEntity(exportEntities, streamConfigs, constructRequestUrl(OCC_IO, STREAM_CONFIG_ENTITY_SET));
		}
		return streamConfigs;
	}

	private Set<String> decorateJobs(final Set<ExportEntity> exportEntities, final Set<String> streamConfigs)
	{
		final Set<String> jobs = findRelatedEntities(streamConfigs, CONTAINER_ID_PATH, searchService::findJobs);
		if (!jobs.isEmpty())
		{
			decorateExportEntity(exportEntities, jobs, constructRequestUrl(OCC_IO, JOB_ENTITY_SET));
		}
		return jobs;
	}

	private Set<String> decorateCronJobs(final Set<ExportEntity> exportEntities, final Set<String> jobs)
	{
		final Set<String> cronJobs = findRelatedEntities(jobs, CODE_PATH, searchService::findCronJobs);
		if (!cronJobs.isEmpty())
		{
			decorateExportEntity(exportEntities, cronJobs, constructRequestUrl(OCC_IO, CRON_JOB_ENTITY_SET));
		}
		return cronJobs;
	}

	private Set<ExportEntity> decorateTriggers(final Set<ExportEntity> exportEntities, final Set<String> cronJobs)
	{
		final Set<String> triggers = findRelatedEntities(cronJobs, CODE_PATH, searchService::findTriggers);
		if (!triggers.isEmpty())
		{
			decorateExportEntity(exportEntities, triggers, constructRequestUrl(OCC_IO, TRIGGER_ENTITY_SET));
		}
		return exportEntities;
	}

	private Set<String> findRelatedEntities(final Set<String> entities, final String property,
	                                        final Function<String, Set<ItemModel>> searchFunction)
	{
		return entities.stream()
		               .map(entity -> extractProperty(entity, property))
		               .map(searchFunction)
		               .flatMap(Collection::stream)
		               .filter(Objects::nonNull)
		               .map(this::constructPayload).collect(Collectors.toSet());
	}

	private Set<ExportEntity> decorateExportEntity(final Set<ExportEntity> exportEntities, final Set<String> requestBodies,
	                                               final String url)
	{
		final var exportEntity = new ExportEntity();
		exportEntity.setRequestUrl(url);
		exportEntity.setRequestBodies(requestBodies);
		exportEntities.add(exportEntity);
		return exportEntities;
	}

	private String constructPayload(final ItemModel itemModel)
	{
		final var requestBodyJson = constructPayload(itemModel, OCC_IO);
		LOG.debug("The decorated request body for the OutboundChannelConfiguration: {}", requestBodyJson);
		return requestBodyJson;
	}

	private boolean isOutboundChannelConfiguration(final ExportEntity exportEntity)
	{
		return exportEntity.getRequestUrl().contains(OCC_IO);
	}

	public void setSearchService(final ExportConfigurationSearchService searchService)
	{
		this.searchService = searchService;
	}

}
