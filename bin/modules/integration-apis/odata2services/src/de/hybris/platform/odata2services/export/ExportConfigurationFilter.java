/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.odata2services.export;

import java.util.Map;

/**
 * Filter sensitive information from exported entities.
 */
public interface ExportConfigurationFilter
{
	/**
	 * Filter sensitive information.
	 *
	 * @param requestBody to filter
	 */
	void nullifySensitiveInformation(final Map<?, ?> requestBody);
}
