/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.odata2services.export;

import de.hybris.platform.odata2services.dto.ConfigurationBundleEntity;
import de.hybris.platform.odata2services.dto.ExportEntity;

import java.util.Set;

/**
 * Interface to generate export entities form a given configuration bundle entity.
 */
public interface ExportConfigurationService
{
	/**
	 * Generates list of {@link ExportEntity} from a given {@link ConfigurationBundleEntity}.
	 *
	 * @param configurationBundleEntity passes the configuration bundle with its integration object bundle
	 * @return a set of {@link ExportEntity} that encapsulates request URL and request bodies
	 */
	Set<ExportEntity> generateExportConfiguration(ConfigurationBundleEntity configurationBundleEntity);
}
