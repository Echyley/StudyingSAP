/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.odata2services.export.impl;

import static de.hybris.platform.odata2services.export.impl.PostmanCollectionBuilder.postmanCollectionBuilder;

import de.hybris.platform.integrationservices.util.Log;
import de.hybris.platform.odata2services.dto.ConfigurationBundleEntity;
import de.hybris.platform.odata2services.dto.ExportEntity;
import de.hybris.platform.odata2services.export.ExportConfigurationService;
import de.hybris.platform.odata2services.export.PostmanCollectionFormatter;
import de.hybris.platform.odata2services.export.PostmanCollectionGenerator;

import java.util.Set;
import java.util.stream.Collectors;

import javax.validation.constraints.NotNull;

import org.slf4j.Logger;

import com.google.common.base.Preconditions;

/**
 * The default implementation for the interface {@link PostmanCollectionGenerator}.
 */
public class DefaultPostmanCollectionGenerator implements PostmanCollectionGenerator
{

	private static final Logger LOG = Log.getLogger(DefaultPostmanCollectionGenerator.class);
	private static final String LS = System.lineSeparator();

	private final ExportConfigurationService exportConfigurationService;
	private final PostmanCollectionFormatter postmanCollectionFormatter;

	/**
	 * Instantiates a new Postman collection generator.
	 *
	 * @param exportConfigurationService export configuration service
	 * @param postmanCollectionFormatter Postman collection
	 */
	public DefaultPostmanCollectionGenerator(
			@NotNull final ExportConfigurationService exportConfigurationService,
			@NotNull final PostmanCollectionFormatter postmanCollectionFormatter)
	{
		Preconditions.checkArgument(exportConfigurationService != null, "exportConfigurationService must not be null.");
		Preconditions.checkArgument(postmanCollectionFormatter != null, "postmanCollectionFormatter must not be null.");
		this.exportConfigurationService = exportConfigurationService;
		this.postmanCollectionFormatter = postmanCollectionFormatter;
	}

	@Override
	public String generate(final ConfigurationBundleEntity configurationBundleEntity)
	{
		final Set<ExportEntity> exportEntities = exportConfigurationService.generateExportConfiguration(
				configurationBundleEntity);
		final PostmanCollection postmanCollection = postmanCollectionBuilder().withExportEntities(exportEntities)
		                                                                      .build();
		logConfigurationBundle(configurationBundleEntity);

		return postmanCollectionFormatter.format(postmanCollection);

	}

	private void logConfigurationBundle(final ConfigurationBundleEntity configurationBundleEntity)
	{
		final String ioBundles = configurationBundleEntity.getIntegrationObjectBundles()
		                                                  .stream()
		                                                  .map(ioBundle -> ioBundle.getIntegrationObjectCode() + "=" + ioBundle.getRootItemInstancePks())
		                                                  .collect(Collectors.joining("," + LS, LS + "{", "}"));

		LOG.info("A Postman collection has been generated for the following integration object bundles: {}", ioBundles);
	}

}

