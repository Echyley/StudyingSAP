/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.runtime.mock.util.impl;

import de.hybris.platform.sap.productconfig.runtime.mock.data.ConfigurationId;
import de.hybris.platform.sap.productconfig.runtime.mock.util.ConfigurationMockIdGenarator;

import java.util.UUID;


public class DefaultConfigurationMockIdGenarator implements ConfigurationMockIdGenarator
{
	private static final String SEPARATOR = "@";
	private static final int PARTS_UUID = 0;
	private static final int PARTS_PRODUCT = 1;
	private static final int PARTS_NUMBER_MINIMUM = 2;
	private static final int PARTS_NUMBER_MAXIMUM = 3;


	@Override
	public  String generateConfigId(final String productId, final String variantId)
	{
		final ConfigurationId configId = new ConfigurationId();
		configId.setUid(UUID.randomUUID().toString());
		configId.setProductId(productId);
		configId.setVariantId(variantId);
		return getConfigIdFromStructured(configId);
	}

	@Override
	public ConfigurationId getStructuredConfigIdFromString(final String configId)
	{
		final ConfigurationId structuredConfigId = new ConfigurationId();
		final String[] parts = configId.split(SEPARATOR);
		if (parts.length < PARTS_NUMBER_MINIMUM || parts.length > PARTS_NUMBER_MAXIMUM)
		{
			throw new IllegalArgumentException(String.format("No valid MOCK configId: '%s'", configId));
		}
		structuredConfigId.setUid(parts[PARTS_UUID]);
		structuredConfigId.setProductId(parts[PARTS_PRODUCT]);
		if (parts.length > PARTS_NUMBER_MINIMUM)
		{
			structuredConfigId.setVariantId(parts[PARTS_NUMBER_MINIMUM]);
		}
		return structuredConfigId;
	}

	@Override
	public String getConfigIdFromStructured(final ConfigurationId configId)
	{
		return new StringBuilder(configId.getUid()).append(SEPARATOR).append(configId.getProductId()).append(SEPARATOR)
				.append(configId.getVariantId()).toString();
	}
}
