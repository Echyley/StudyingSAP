/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.odata2services.export.impl;

import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.integrationservices.model.DescriptorFactory;
import de.hybris.platform.integrationservices.service.IntegrationObjectConversionService;
import de.hybris.platform.integrationservices.service.IntegrationObjectService;
import de.hybris.platform.integrationservices.util.JsonObject;
import de.hybris.platform.odata2services.export.ExportConfigurationDecorator;
import de.hybris.platform.odata2services.export.ExportConfigurationFilter;
import de.hybris.platform.odata2services.odata.schema.entity.EntitySetNameGenerator;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.validation.constraints.NotNull;

import com.google.common.base.Preconditions;

import groovy.json.JsonBuilder;

/**
 * The base implementation of the {@link ExportConfigurationDecorator} to be extended for reuse.
 */
public abstract class AbstractExportConfigurationDecorator implements ExportConfigurationDecorator
{
	private static final String BASE_URL = "{{hostUrl}}/odata2webservices/";
	private static final String PATH_DELIMITER = "/";

	private final DescriptorFactory descriptorFactory;
	private final IntegrationObjectConversionService conversionService;
	private final IntegrationObjectService integrationObjectService;
	private final EntitySetNameGenerator nameGenerator;
	private ExportConfigurationFilter exportConfigurationFilter;

	/**
	 * Constructor injects dependencies for reuse.
	 *
	 * @param descriptorFactory        to create the integration object descriptor
	 * @param conversionService        to convert the integration object
	 * @param integrationObjectService to find the integration object
	 * @param nameGenerator            to generate the entity set name
	 */
	protected AbstractExportConfigurationDecorator(@NotNull final DescriptorFactory descriptorFactory,
	                                               @NotNull final IntegrationObjectConversionService conversionService,
	                                               @NotNull final IntegrationObjectService integrationObjectService,
	                                               @NotNull final EntitySetNameGenerator nameGenerator)
	{
		Preconditions.checkArgument(descriptorFactory != null, "descriptorFactory must not be null.");
		Preconditions.checkArgument(conversionService != null, "conversionService must not be null.");
		Preconditions.checkArgument(integrationObjectService != null, "integrationObjectService must not be null.");
		Preconditions.checkArgument(nameGenerator != null, "nameGenerator must not be null.");

		this.descriptorFactory = descriptorFactory;
		this.conversionService = conversionService;
		this.integrationObjectService = integrationObjectService;
		this.nameGenerator = nameGenerator;
	}

	/**
	 * Set export configuration filter.
	 *
	 * @param exportConfigurationFilter to filter sensitive information
	 */
	public void setExportConfigurationFilter(final ExportConfigurationFilter exportConfigurationFilter)
	{
		this.exportConfigurationFilter = exportConfigurationFilter;
	}

	/**
	 * Constructs the integration object payload after filtering sensitive information if the filter is provided.
	 *
	 * @param itemModel the integration object root item model
	 * @param ioCode    the integration object code
	 * @return Json string of the integration object
	 */
	protected String constructPayload(final ItemModel itemModel, final String ioCode)
	{
		final var io = integrationObjectService.findIntegrationObject(ioCode);
		final Map<String, Object> requestBody = conversionService.convert(itemModel, descriptorFactory.createIntegrationObjectDescriptor(io));
		applyFilter(requestBody);
		return new JsonBuilder(requestBody).toPrettyString();
	}

	/**
	 * Extracts a property from an export entity request body.
	 *
	 * @param requestBody request body
	 * @param path        property path
	 * @return property value
	 */
	protected String extractProperty(final String requestBody, final String path)
	{
		return JsonObject.createFrom(requestBody).getString(path);
	}

	/**
	 * Extracts a property list from an export entity request body.
	 *
	 * @param requestBody request body
	 * @param path        property path
	 * @return list of property values
	 */
	protected List<String> extractProperties(final String requestBody, final String path)
	{
		final Object[] objects = JsonObject.createFrom(requestBody).getCollection(path).toArray();
		return Arrays.stream(objects).map(Object::toString).collect(Collectors.toList());
	}

	/**
	 * Constructs request URL.
	 *
	 * @param integrationObjectCode integration object code
	 * @param entitySet             entity set name
	 * @return string representation of the URl
	 */
	protected String constructRequestUrl(final String integrationObjectCode, final String entitySet)
	{
		return BASE_URL + integrationObjectCode + PATH_DELIMITER + nameGenerator.generate(entitySet);
	}

	/***
	 * Constructs export endpoint
	 * @param integrationObject integration object code
	 * @param entitySet entity set name
	 * @return endpoint
	 */
	protected String exportEndpoint(final String integrationObject, final String entitySet)
	{
		return integrationObject + PATH_DELIMITER + nameGenerator.generate(entitySet);
	}

	protected IntegrationObjectService getIntegrationObjectService()
	{
		return integrationObjectService;
	}

	private void applyFilter(final Map<String, Object> requestBody)
	{
		if (exportConfigurationFilter != null)
		{
			exportConfigurationFilter.nullifySensitiveInformation(requestBody);
		}
	}

}
