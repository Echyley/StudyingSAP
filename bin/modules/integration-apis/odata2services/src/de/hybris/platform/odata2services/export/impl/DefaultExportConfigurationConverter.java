/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.odata2services.export.impl;

import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.integrationservices.model.DescriptorFactory;
import de.hybris.platform.integrationservices.model.IntegrationObjectDescriptor;
import de.hybris.platform.integrationservices.model.IntegrationObjectModel;
import de.hybris.platform.integrationservices.service.IntegrationObjectConversionService;
import de.hybris.platform.integrationservices.util.ApplicationBeans;
import de.hybris.platform.integrationservices.util.Log;
import de.hybris.platform.odata2services.dto.ConfigurationBundleEntity;
import de.hybris.platform.odata2services.dto.ExportEntity;
import de.hybris.platform.odata2services.dto.IntegrationObjectBundleEntity;
import de.hybris.platform.odata2services.export.ExportConfigurationFilter;
import de.hybris.platform.odata2services.export.ExportConfigurationSearchService;
import de.hybris.platform.odata2services.odata.schema.entity.EntitySetNameGenerator;

import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.validation.constraints.NotNull;

import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.springframework.core.convert.converter.Converter;

import com.google.common.base.Preconditions;

import groovy.json.JsonBuilder;

/**
 * The default export configuration converter.
 */
public class DefaultExportConfigurationConverter implements Converter<ConfigurationBundleEntity, Set<ExportEntity>>
{
	private static final Logger LOG = Log.getLogger(DefaultExportConfigurationConverter.class);
	private static final String BASE_URL = "{{hostUrl}}/odata2webservices/";
	private static final String OUTBOUND_CHANNEL_CONFIG = "OutboundChannelConfig";
	private static final String AUTO_GENERATE = "autoGenerate";

	private final ExportConfigurationSearchService searchService;
	private final DescriptorFactory descriptorFactory;
	private final IntegrationObjectConversionService conversionService;
	private final EntitySetNameGenerator nameGenerator;
	private ExportConfigurationFilter exportConfigurationFilter;

	/**
	 * Instantiates a new export configuration conversion service.
	 *
	 * @param searchService     to search integration object
	 * @param descriptorFactory to create integration object descriptor
	 * @param conversionService to convert the integration object to a Map<String,Object>
	 * @param nameGenerator     to generate the entity set name
	 */
	public DefaultExportConfigurationConverter(
			@NotNull final ExportConfigurationSearchService searchService,
			@NotNull final DescriptorFactory descriptorFactory,
			@NotNull final IntegrationObjectConversionService conversionService,
			@NotNull final EntitySetNameGenerator nameGenerator)
	{
		Preconditions.checkArgument(searchService != null, "searchService must not be null.");
		Preconditions.checkArgument(descriptorFactory != null, "descriptorFactory must not be null.");
		Preconditions.checkArgument(conversionService != null, "conversionService must not be null.");
		Preconditions.checkArgument(nameGenerator != null, "nameGenerator must not be null.");

		this.searchService = searchService;
		this.descriptorFactory = descriptorFactory;
		this.conversionService = conversionService;
		this.nameGenerator = nameGenerator;
	}

	/**
	 * Converts {@link ConfigurationBundleEntity} into {@link ExportEntity}.
	 *
	 * @param configurationBundleEntity the configuration bundle entity to convert
	 * @return the set export entity {@link Set<ExportEntity>}
	 */
	@Override
	public Set<ExportEntity> convert(final ConfigurationBundleEntity configurationBundleEntity)
	{
		return configurationBundleEntity.getIntegrationObjectBundles()
		                                .stream()
		                                .map(this::convertIOBundle)
		                                .filter(this::nonEmptyRequestBody)
		                                .collect(Collectors.toSet());
	}

	private ExportEntity convertIOBundle(final IntegrationObjectBundleEntity ioBundle)
	{
		final IntegrationObjectModel io = searchService
				.findExportableIntegrationObjectByCode(ioBundle.getIntegrationObjectCode());
		final var ioDescriptor = descriptorFactory.createIntegrationObjectDescriptor(io);
		final Set<ItemModel> rootItemInstances = searchService.findRootItemInstances(ioBundle);
		return constructExportEntity(io, ioDescriptor, rootItemInstances);
	}

	private boolean nonEmptyRequestBody(final ExportEntity exportEntity)
	{
		return exportEntity != null && CollectionUtils.isNotEmpty(exportEntity.getRequestBodies());
	}


	private ExportEntity constructExportEntity(final IntegrationObjectModel integrationObject,
	                                           final IntegrationObjectDescriptor ioDescriptor,
	                                           final Set<ItemModel> rootItemInstances)
	{
		final var exportEntity = new ExportEntity();
		exportEntity.setRequestUrl(constructRequestUrl(integrationObject));
		exportEntity.setRequestBodies(constructRequestBodies(ioDescriptor, rootItemInstances));
		return exportEntity;
	}

	private Set<String> constructRequestBodies(final IntegrationObjectDescriptor ioDescriptor,
	                                           final Set<ItemModel> rootItemInstances)
	{
		return rootItemInstances.stream()
		                        .map(rootItemInstance -> constructRequestBody(ioDescriptor, rootItemInstance))
		                        .collect(Collectors.toCollection(LinkedHashSet::new));
	}

	private String constructRequestBody(final IntegrationObjectDescriptor ioDescriptor, final ItemModel rootItemInstance)
	{
		final Map<String, Object> requestBody = conversionService.convert(rootItemInstance, ioDescriptor);
		getExportConfigurationFilter().nullifySensitiveInformation(requestBody);
		forceOccAutoGenerateToFalse(ioDescriptor, requestBody);
		final var jsonBody = new JsonBuilder(requestBody).toPrettyString();
		LOG.debug("The generated request requestBody for the item model with PK [{}]: {}", rootItemInstance.getPk(), jsonBody);
		return jsonBody;
	}

	private String constructRequestUrl(final IntegrationObjectModel integrationObject)
	{
		final String url = BASE_URL + integrationObject.getCode() + "/" + nameGenerator.generate(
				integrationObject.getRootItem().getCode());

		LOG.debug("The generated request URL for the integration object [{}]: {}", integrationObject.getCode(), url);
		return url;
	}

	/**
	 * Inject the filter service to filter out sensitive information from the exported configuration.
	 *
	 * @param exportConfigurationFilter the default filter service implementation.
	 */
	public void setExportConfigurationFilter(final ExportConfigurationFilter exportConfigurationFilter)
	{
		this.exportConfigurationFilter = exportConfigurationFilter;
	}

	private void forceOccAutoGenerateToFalse(final IntegrationObjectDescriptor ioDescriptor,
	                                         final Map<String, Object> requestBody)
	{
		if (OUTBOUND_CHANNEL_CONFIG.equals(ioDescriptor.getCode()))
		{
			requestBody.entrySet()
			           .stream()
			           .filter(entry -> AUTO_GENERATE.equals(entry.getKey()))
			           .findFirst()
			           .ifPresent(autoGenerate -> autoGenerate.setValue(false));
		}
	}

	private ExportConfigurationFilter getExportConfigurationFilter()
	{
		if (exportConfigurationFilter == null)
		{
			exportConfigurationFilter = ApplicationBeans.getBean("exportConfigurationFilter", ExportConfigurationFilter.class);
		}
		return exportConfigurationFilter;
	}

}
