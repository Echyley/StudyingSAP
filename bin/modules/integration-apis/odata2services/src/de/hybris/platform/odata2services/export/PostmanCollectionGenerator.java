/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.odata2services.export;

import de.hybris.platform.odata2services.dto.ConfigurationBundleEntity;

/**
 * Generates a Postman collection.
 */
public interface PostmanCollectionGenerator
{
	/**
	 * Generate a Postman collection from a given {@link ConfigurationBundleEntity}
	 *
	 * @param configurationBundleEntity the configuration bundle to process
	 * @return a Json representation of a Postman collection
	 */
	String generate(ConfigurationBundleEntity configurationBundleEntity);
}
