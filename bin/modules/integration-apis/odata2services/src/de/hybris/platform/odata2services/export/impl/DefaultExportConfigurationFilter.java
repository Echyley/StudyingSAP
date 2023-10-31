/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.odata2services.export.impl;

import de.hybris.platform.odata2services.export.ExportConfigurationFilter;

import java.util.List;
import java.util.Map;

/**
 * The default implementation of the {@link ExportConfigurationFilter}.
 */
public class DefaultExportConfigurationFilter implements ExportConfigurationFilter
{
	private static final List<String> SENSITIVE_ATTRIBUTES = List.of("credentialBasic", "credentialConsumedOAuth");

	/**
	 * Recursive filter method to nullify the values of sensitive information.
	 *
	 * @param requestBody to filter
	 */
	@Override
	public void nullifySensitiveInformation(final Map<?, ?> requestBody)
	{
		requestBody.entrySet().forEach(entry -> {
			if (SENSITIVE_ATTRIBUTES.contains(entry.getKey().toString()))
			{
				entry.setValue(null);
			}
			if (entry.getValue() instanceof Map)
			{
				nullifySensitiveInformation((Map<?, ?>) entry.getValue());
			}
		});
	}

}
