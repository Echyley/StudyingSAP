/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.services.impl;

import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.sap.productconfig.services.ConfigurationVariantUtil;
import de.hybris.platform.sap.productconfig.services.model.MockVariantProductModel;
import de.hybris.platform.sap.sapmodel.model.ERPVariantProductModel;
import de.hybris.platform.variants.model.VariantProductModel;
import de.hybris.platform.variants.model.VariantTypeModel;

import java.util.Collections;
import java.util.Set;


/**
 * Default implementation of the {@link ConfigurationVariantUtil}.
 */
public class ConfigurationVariantUtilImpl implements ConfigurationVariantUtil
{

	private Set<String> changeableVariantBaseProducts;

	/**
	 * @param changeableVariantBaseProducts
	 *           the changeableVariantBaseProducts to set
	 */
	public void setChangeableVariantBaseProducts(final Set<String> changeableVariantBaseProducts)
	{
		this.changeableVariantBaseProducts = Collections.unmodifiableSet(changeableVariantBaseProducts);
	}


	protected Set<String> getChangeableVariantBaseProducts()
	{
		return Collections.unmodifiableSet(changeableVariantBaseProducts);
	}

	protected boolean isChangeableVariantForMockProduct(final ProductModel productModel)
	{
		final String baseProductCode = getBaseProductCode(productModel);
		return getChangeableVariantBaseProducts().contains(baseProductCode);
	}

	@Override
	public boolean isCPQBaseProduct(final ProductModel productModel)
	{
		final VariantTypeModel variantType = productModel.getVariantType();
		if (variantType != null)
		{
			return ((variantType.getCode().equals(ERPVariantProductModel._TYPECODE))
					|| (variantType.getCode().equals(MockVariantProductModel._TYPECODE)));
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
		boolean isCPQChangeableVariantProduct = false;
		if (isCPQVariantProduct(productModel))
		{
			if (productModel instanceof ERPVariantProductModel)
			{
				isCPQChangeableVariantProduct = ((ERPVariantProductModel) productModel).isChangeable();
			}
			else if (productModel instanceof MockVariantProductModel)
			{
				isCPQChangeableVariantProduct = isChangeableVariantForMockProduct(productModel);
			}
		}
		return isCPQChangeableVariantProduct;
	}

	@Override
	public boolean isCPQNotChangeableVariantProduct(final ProductModel productModel)
	{
		boolean isCPQNotChangeableVariantProduct = false;
		if (isCPQVariantProduct(productModel))
		{
			if (productModel instanceof ERPVariantProductModel)
			{
				isCPQNotChangeableVariantProduct = !((ERPVariantProductModel) productModel).isChangeable();
			}
			if (productModel instanceof MockVariantProductModel)
			{
				isCPQNotChangeableVariantProduct = !isChangeableVariantForMockProduct(productModel);
			}
		}
		return isCPQNotChangeableVariantProduct;

	}
}
