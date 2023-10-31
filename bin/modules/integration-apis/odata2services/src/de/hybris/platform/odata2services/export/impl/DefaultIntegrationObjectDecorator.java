/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.odata2services.export.impl;

import de.hybris.platform.integrationservices.model.DescriptorFactory;
import de.hybris.platform.integrationservices.model.IntegrationObjectModel;
import de.hybris.platform.integrationservices.service.IntegrationObjectConversionService;
import de.hybris.platform.integrationservices.service.IntegrationObjectService;
import de.hybris.platform.integrationservices.util.JsonObject;
import de.hybris.platform.integrationservices.util.Log;
import de.hybris.platform.odata2services.dto.ExportEntity;
import de.hybris.platform.odata2services.odata.schema.entity.EntitySetNameGenerator;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

import javax.validation.constraints.NotNull;

import org.slf4j.Logger;

/**
 * The integration object decorator to augment export configuration with referenced integration objects.
 */
public class DefaultIntegrationObjectDecorator extends AbstractExportConfigurationDecorator
{
	private static final Logger LOG = Log.getLogger(DefaultIntegrationObjectDecorator.class);

	private static final String OCC_IO = "OutboundChannelConfig";
	private static final String WEBHOOK_IO = "WebhookService";
	private static final String INTEGRATION_SERVICE_IO = "IntegrationService";
	private static final String ENTITY_SET = "IntegrationObject";
	private static final String JSON_PATH = "integrationObject.code";

	/**
	 * Instantiate a new instance of the integration object decorator.
	 *
	 * @param descriptorFactory        to create the integration object descriptor
	 * @param conversionService        to convert the integration object
	 * @param integrationObjectService to find the integration object
	 * @param nameGenerator            to generate the entity set name
	 */
	public DefaultIntegrationObjectDecorator(
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
		final Set<String> decoratedBodies = exportEntities.stream()
		                                                  .filter(this::canBeDecorated)
		                                                  .flatMap(exportEntity -> exportEntity.getRequestBodies().stream())
		                                                  .map(this::extractIntegrationObjectCode)
		                                                  .map(this::constructRequestBody)
		                                                  .collect(Collectors.toSet());

		return decoratedBodies.isEmpty() ? exportEntities : decorateEntities(exportEntities, decoratedBodies);
	}

	private boolean canBeDecorated(final ExportEntity exportEntity)
	{
		return exportEntity.getRequestUrl().contains(OCC_IO) || exportEntity.getRequestUrl().contains(WEBHOOK_IO);
	}

	private String extractIntegrationObjectCode(final String requestBodyJson)
	{
		return JsonObject.createFrom(requestBodyJson).getString(JSON_PATH);
	}

	private String constructRequestBody(final String ioCode)
	{
		final IntegrationObjectModel integrationObjectModel = getIntegrationObjectService().findIntegrationObject(ioCode);
		final var requestBodyJson = constructPayload(integrationObjectModel, INTEGRATION_SERVICE_IO);

		LOG.debug("The decorated request body for the integration object [{}]: {}", ioCode, requestBodyJson);
		return requestBodyJson;
	}

	private Set<ExportEntity> decorateEntities(final Set<ExportEntity> exportEntities, final Set<String> insertBodies)
	{
		final var integrationObjectEntity = new ExportEntity();
		integrationObjectEntity.setRequestUrl(constructRequestUrl(INTEGRATION_SERVICE_IO, ENTITY_SET));
		integrationObjectEntity.setRequestBodies(insertBodies);

		final Set<ExportEntity> decoratedEntities = new LinkedHashSet<>();
		decoratedEntities.add(integrationObjectEntity);
		decoratedEntities.addAll(exportEntities);
		return decoratedEntities;
	}

}
