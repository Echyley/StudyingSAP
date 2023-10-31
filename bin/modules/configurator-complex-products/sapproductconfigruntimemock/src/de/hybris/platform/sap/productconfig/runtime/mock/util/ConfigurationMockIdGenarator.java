/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.runtime.mock.util;

import de.hybris.platform.sap.productconfig.runtime.mock.ConfigMock;
import de.hybris.platform.sap.productconfig.runtime.mock.data.ConfigurationId;


/**
 * Helper class to generate and parse valid {@link ConfigMock} id's.
 *
 * @see ConfigurationId
 */
public interface ConfigurationMockIdGenarator
{

	/**
	 * encode config id
	 *
	 * @param configId
	 *           structured config id
	 * @return encoded config id
	 */
	String getConfigIdFromStructured(final ConfigurationId configId);

	/**
	 * decode config id
	 *
	 * @param configId
	 *           encoded config id
	 * @return structured config id
	 */
	ConfigurationId getStructuredConfigIdFromString(final String configId);

	/**
	 * generates a unique config id
	 *
	 * @param productId
	 *           product id to encode in config id
	 * @param variantId
	 *           variant id to encode in config id
	 * @return encoded config id
	 */
	String generateConfigId(final String productId, final String variantId);

}
