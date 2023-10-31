/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.facades;

import de.hybris.platform.commercefacades.order.data.ConfigurationInfoData;
import de.hybris.platform.core.model.product.ProductModel;

import java.util.List;


/**
 * Provides configuration info for variant products
 */
public interface VariantConfigurationInfoProvider
{
	/**
	 * Retrieves configuration infos for a variant product
	 * 
	 * @param product
	 *           product model
	 * @return list of configuration infos
	 */
	List<ConfigurationInfoData> retrieveVariantConfigurationInfo(final ProductModel product);
}
