/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.services.impl;

import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.sap.productconfig.services.ConfigurationVariantUtil;
import de.hybris.platform.sap.productconfig.services.model.MockVariantProductModel;
import de.hybris.platform.variants.model.VariantProductModel;
import de.hybris.platform.variants.model.VariantTypeModel;

import java.util.Set;


/**
 * Default implementation of the {@link ConfigurationVariantUtil}.
 */
public class SimpleConfigurationVariantUtilImpl implements ConfigurationVariantUtil
{

	private Set<String> changeableVariantBaseProducts;

	/**
	 * @param changeableVariantBaseProducts
	 *           the changeableVariantBaseProducts to set
	 */
	public void setChangeableVariantBaseProducts(Set<String> changeableVariantBaseProducts)
	{
		this.changeableVariantBaseProducts = changeableVariantBaseProducts;
	}


	protected Set<String> getChangeableVariantBaseProducts()
	{
		return changeableVariantBaseProducts;
	}

	@Override
	public boolean isCPQBaseProduct(final ProductModel productModel)
	{
		final VariantTypeModel variantType = productModel.getVariantType();
		if (variantType != null)
		{
			return variantType.getCode().equals(MockVariantProductModel._TYPECODE);
		}
		return false;
	}

	@Override
	public boolean isCPQVariantProduct(final ProductModel productModel)
	{
		if (productModel instanceof VariantProductModel)
		{
			final ProductModel baseProduct = ((VariantProductModel) productModel).getBaseProduct();
			if (null != baseProduct)
			{
				return isCPQBaseProduct(baseProduct);
			}
		}
		return false;
	}

	@Override
	public String getBaseProductCode(final ProductModel variantProductModel)
	{
		return ((VariantProductModel) variantProductModel).getBaseProduct().getCode();
	}

	@Override
	public boolean isCPQChangeableVariantProduct(final ProductModel productModel)
	{
		return checkVariantAndChangeable(productModel, true);
	}

	@Override
	public boolean isCPQNotChangeableVariantProduct(final ProductModel productModel)
	{
		return checkVariantAndChangeable(productModel, false);
	}


	protected boolean checkVariantAndChangeable(final ProductModel productModel, boolean shouldBeChangeable)
	{
		if (!isCPQVariantProduct(productModel))
		{
			return false;
		}
		String baseProduct = getBaseProductCode(productModel);
		return (!shouldBeChangeable) ^ getChangeableVariantBaseProducts().contains(baseProduct);
	}


}
