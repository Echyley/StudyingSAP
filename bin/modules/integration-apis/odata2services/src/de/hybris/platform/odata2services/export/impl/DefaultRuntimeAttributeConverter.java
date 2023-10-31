/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.odata2services.export.impl;

import de.hybris.platform.core.model.type.AttributeDescriptorModel;
import de.hybris.platform.integrationservices.model.DescriptorFactory;
import de.hybris.platform.integrationservices.model.IntegrationObjectModel;
import de.hybris.platform.integrationservices.service.IntegrationObjectConversionService;
import de.hybris.platform.integrationservices.service.IntegrationObjectService;
import de.hybris.platform.integrationservices.util.Log;
import de.hybris.platform.odata2services.dto.ConfigurationBundleEntity;
import de.hybris.platform.odata2services.dto.ExportEntity;
import de.hybris.platform.odata2services.export.ExportConfigurationSearchService;
import de.hybris.platform.odata2services.odata.schema.entity.EntitySetNameGenerator;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.validation.constraints.NotNull;

import org.slf4j.Logger;
import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.NonNull;

import com.google.common.base.Preconditions;

import groovy.json.JsonBuilder;

/**
 * The default export configuration runtime attributes converter.
 */
public class DefaultRuntimeAttributeConverter implements Converter<ConfigurationBundleEntity, Set<ExportEntity>>
{
	private static final Logger LOG = Log.getLogger(DefaultRuntimeAttributeConverter.class);
	private static final String BASE_URL = "{{hostUrl}}/odata2webservices/";
	private static final String RUNTIME_ATTRIBUTE_SERVICE = "RuntimeAttributeService";

	private final DescriptorFactory descriptorFactory;
	private final EntitySetNameGenerator nameGenerator;
	private final IntegrationObjectService integrationObjectService;
	private final ExportConfigurationSearchService searchService;
	private final IntegrationObjectConversionService conversionService;

	/**
	 * Instantiates a new runtime attribute converter.
	 *
	 * @param descriptorFactory        to create an integration object descriptor
	 * @param nameGenerator            to generate the entity set name
	 * @param integrationObjectService to find an integration object
	 * @param searchService            to find runtime attribute descriptors
	 * @param conversionService        to convert an integration object
	 */
	public DefaultRuntimeAttributeConverter(@NotNull final DescriptorFactory descriptorFactory,
	                                        @NotNull final EntitySetNameGenerator nameGenerator,
	                                        @NotNull final IntegrationObjectService integrationObjectService,
	                                        @NotNull final ExportConfigurationSearchService searchService,
	                                        @NotNull final IntegrationObjectConversionService conversionService)
	{
		Preconditions.checkArgument(descriptorFactory != null, "descriptorFactory must not be null");
		Preconditions.checkArgument(nameGenerator != null, "nameGenerator must not be null");
		Preconditions.checkArgument(integrationObjectService != null, "integrationObjectService must not be null");
		Preconditions.checkArgument(searchService != null, "searchService must not be null");
		Preconditions.checkArgument(conversionService != null, "conversionService must not be null");

		this.descriptorFactory = descriptorFactory;
		this.nameGenerator = nameGenerator;
		this.integrationObjectService = integrationObjectService;
		this.searchService = searchService;
		this.conversionService = conversionService;
	}

	/**
	 * Converts {@link ConfigurationBundleEntity} into {@link ExportEntity}.
	 *
	 * @param configBundle passes a collection of integration object bundles
	 * @return the set export entity {@link Set<ExportEntity>}
	 */
	@Override
	public Set<ExportEntity> convert(@NonNull final ConfigurationBundleEntity configBundle)
	{
		final Set<String> requestBodies = constructRequestBodies(configBundle);
		if(!requestBodies.isEmpty()){
			final ExportEntity exportEntity = new ExportEntity();
			exportEntity.setRequestUrl(constructRequestUrl());
			exportEntity.setRequestBodies(requestBodies);
			return Set.of(exportEntity);
		}
		return Collections.emptySet();
	}

	private Set<String> constructRequestBodies(final ConfigurationBundleEntity configBundle)
	{
		final Set<AttributeDescriptorModel> descriptors = searchService.findRuntimeAttributeDescriptors(configBundle);
		return descriptors.stream().map(this::constructRequestBody).collect(Collectors.toSet());
	}

	private String constructRequestBody(final AttributeDescriptorModel attrDescriptor)
	{
		final IntegrationObjectModel io = integrationObjectService.findIntegrationObject(RUNTIME_ATTRIBUTE_SERVICE);
		final Map<String, Object> requestBody = conversionService.convert(attrDescriptor, descriptorFactory.createIntegrationObjectDescriptor(io));
		final var requestBodyString = new JsonBuilder(requestBody).toPrettyString();

		LOG.debug("The generated request body for the integration object [{}]: {}", io.getCode(), requestBodyString);
		return requestBodyString;
	}

	private String constructRequestUrl()
	{
		final IntegrationObjectModel io = integrationObjectService.findIntegrationObject(RUNTIME_ATTRIBUTE_SERVICE);
		final String url = BASE_URL + RUNTIME_ATTRIBUTE_SERVICE + "/" + nameGenerator.generate(io.getRootItem().getCode());

		LOG.debug("The generated request URL for the integration object [{}]: {}", RUNTIME_ATTRIBUTE_SERVICE, url);
		return url;
	}

}
