/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.facades;

import java.util.Collections;
import java.util.List;



/**
 * Facade for integrating Configuration Variants into CPQ
 */
public interface ConfigurationVariantFacade
{

	/**
	 * Searches variants that are similar to the currently configured product identified by the given config id, and
	 * decorates the result with some additional data, such as price and image data.
	 *
	 * @param configId
	 *           configuration id of current configuration session
	 * @param productCode
	 *           product code of the currently configured product
	 * @return A List of variants that are similar to current configured product, along with some additional data.
	 */
	List<ConfigurationVariantData> searchForSimilarVariants(String configId, String productCode);
	
	/**
	 * Searches variants that are similar to the currently configured product identified by the given config id, and
	 * decorates the result with some additional data, such as price and image data.
	 *
	 * @param configId
	 *           configuration id of current configuration session 
	 * @return A List of variants that are similar to currently configured product, along with some additional data.
	 */
	default List<ConfigurationVariantData> searchForSimilarVariants(String configId) 
	{
		return Collections.emptyList();
	}	
}
