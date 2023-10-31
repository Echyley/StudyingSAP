/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.integrationbackoffice.widgets.configuration.generators;

import de.hybris.platform.odata2services.dto.ConfigurationBundleEntity;

/**
 * Interface for the ExportConfiguration report generator.
 */
public interface ExportConfigurationGenerator
{
	/**
	 * Converts a {@link ConfigurationBundleEntity} to byte array.
	 *
	 * @param configurationBundleEntity configurationBundleEntity a given {@link ConfigurationBundleEntity}
	 * @return a byte to download
	 */
	byte[] generateExportConfig(ConfigurationBundleEntity configurationBundleEntity);

	/**
	 * Converts a {@link ConfigurationBundleEntity} to a downloadable format and downloads it.
	 *
	 * @param configurationBundleEntity a given {@link ConfigurationBundleEntity}
	 */
	void downloadExportConfig(ConfigurationBundleEntity configurationBundleEntity);

}
