/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.retail.oaa.commerce.facades.converters.populator;

import com.sap.retail.oaa.commerce.services.common.util.CommonUtils;

import de.hybris.platform.commercefacades.product.converters.populator.ProductStockPopulator;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commercefacades.product.data.StockData;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;

/**
 * Populate the product data with stock information
 */
public class DefaultOaaProductStockPopulator<SOURCE extends ProductModel, TARGET extends ProductData>
		extends ProductStockPopulator<SOURCE, TARGET> {
	private Converter<ProductModel, StockData> stockConverter;
	private CommonUtils commonUtils;

	@Override
	protected Converter<ProductModel, StockData> getStockConverter() {
		return stockConverter;
	}
	
	@Override
	public void setStockConverter(final Converter<ProductModel, StockData> stockConverter) {
		this.stockConverter = stockConverter;
	}

	/**
	 * @return the commonUtils
	 */
	public CommonUtils getCommonUtils() {
		return commonUtils;
	}

	/**
	 * @param commonUtils the commonUtils to set
	 */
	public void setCommonUtils(CommonUtils commonUtils) {
		this.commonUtils = commonUtils;
	}

	@Override
	public void populate(final SOURCE source, final TARGET target) {
		if (getCommonUtils().isCAREnabled() || getCommonUtils().isCOSEnabled()) {
			target.setStock(getStockConverter().convert(source));
		} else {
			super.populate(source, target);
		}
	}
}
