/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.facades.populator;

import de.hybris.platform.commercefacades.product.converters.populator.ProductVariantMatrixPopulator;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.sap.productconfig.services.ConfigurationVariantUtil;


/**
 * CPQ Variant base products must not be multidimensional in the accelerator sense (as we want to configure and order
 * them)
 * 
 * @param <SOURCE>
 *           product model
 * @param <TARGET>
 *           product DTO
 */
public class CPQProductVariantMatrixPopulator<SOURCE extends ProductModel, TARGET extends ProductData> extends
		ProductVariantMatrixPopulator<SOURCE, TARGET>
{
	private ConfigurationVariantUtil configurationVariantUtil;

	@Override
	public void populate(final ProductModel productModel, final ProductData productData)
	{
		if (getConfigurationVariantUtil().isCPQBaseProduct(productModel))
		{
			populateVariantAttributes(productData);
		}
		else
		{
			super.populate(productModel, productData);
		}
	}

	protected void populateVariantAttributes(final ProductData productData)
	{
		productData.setMultidimensional(Boolean.FALSE);
	}

	protected ConfigurationVariantUtil getConfigurationVariantUtil()
	{
		return configurationVariantUtil;
	}

	/**
	 * @param configurationVariantUtil
	 */
	public void setConfigurationVariantUtil(final ConfigurationVariantUtil configurationVariantUtil)
	{
		this.configurationVariantUtil = configurationVariantUtil;
	}
}
