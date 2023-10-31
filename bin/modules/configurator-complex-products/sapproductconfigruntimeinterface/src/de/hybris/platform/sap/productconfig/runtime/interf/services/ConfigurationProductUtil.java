/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.runtime.interf.services;

import de.hybris.platform.core.model.product.ProductModel;


/**
 * Utility service for non-configurable aspects of products
 */
public interface ConfigurationProductUtil
{

	/**
	 * Retrieves a product for the currently active catalog version
	 *
	 * @param productCode
	 *           product code
	 * @return product model
	 */
	ProductModel getProductForCurrentCatalog(String productCode);
}
