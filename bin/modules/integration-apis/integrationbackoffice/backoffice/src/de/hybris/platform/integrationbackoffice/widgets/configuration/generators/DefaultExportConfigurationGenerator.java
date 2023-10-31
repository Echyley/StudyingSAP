/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.integrationbackoffice.widgets.configuration.generators;

import de.hybris.platform.integrationbackoffice.widgets.modals.utility.ModalUtils;
import de.hybris.platform.odata2services.dto.ConfigurationBundleEntity;
import de.hybris.platform.odata2services.export.PostmanCollectionGenerator;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

/**
 * Generates a JSON payload representation of a Postman collection for a {@link ConfigurationBundleEntity}
 * and allows downloading of the payload.
 */
public class DefaultExportConfigurationGenerator implements ExportConfigurationGenerator
{
	private static final String FILENAME_PREFIX = "importConfiguration_";
	private static final String FILENAME_SUFFIX = "_postmanCollection";

	private final PostmanCollectionGenerator postmanCollectionGenerator;

	/**
	 * Instantiates a new export configuration generator.
	 *
	 * @param postmanCollectionGenerator the service that generates Postman collections as JSON payload representations
	 */
	public DefaultExportConfigurationGenerator(final PostmanCollectionGenerator postmanCollectionGenerator)
	{
		this.postmanCollectionGenerator = postmanCollectionGenerator;
	}

	/**
	 * Calls the Postman collection generator to generate a JSON payload representation of a Postman collection
	 * for a {@link ConfigurationBundleEntity}
	 *
	 * @param configurationBundleEntity a given {@link ConfigurationBundleEntity}
	 * @return a byte array from the string representation of the JSON payload
	 */
	public byte[] generateExportConfig(final ConfigurationBundleEntity configurationBundleEntity)
	{
		final String concatenatedUrlAndBody = postmanCollectionGenerator.generate(configurationBundleEntity);
		return concatenatedUrlAndBody.getBytes(StandardCharsets.UTF_8);
	}

	/**
	 * Converts a {@link ConfigurationBundleEntity} to JSON payload as byte array and calls the browser download functionality.
	 * The file name will have a uniquely generated filename based on current {@link LocalDateTime}.
	 *
	 * @param configurationBundleEntity a given {@link ConfigurationBundleEntity}
	 */
	public void downloadExportConfig(final ConfigurationBundleEntity configurationBundleEntity)
	{
		final byte[] bytes = generateExportConfig(configurationBundleEntity);
		final String filename = getDownloadFileName();

		ModalUtils.executeMediaDownload(bytes, "application/json", filename);
	}

	private String getDownloadFileName()
	{
		final String localDateTime = LocalDateTime.now().toString();
		final String timeWithoutMilli = localDateTime.substring(0, localDateTime.indexOf('.'));
		return FILENAME_PREFIX + timeWithoutMilli + FILENAME_SUFFIX;
	}
}
