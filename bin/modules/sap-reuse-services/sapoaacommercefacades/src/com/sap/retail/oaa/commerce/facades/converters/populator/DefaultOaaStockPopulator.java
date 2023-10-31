/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.retail.oaa.commerce.facades.converters.populator;

import static de.hybris.platform.basecommerce.enums.StockLevelStatus.INSTOCK;
import static de.hybris.platform.basecommerce.enums.StockLevelStatus.OUTOFSTOCK;

import com.sap.retail.oaa.commerce.services.common.util.CommonUtils;
import com.sap.retail.oaa.commerce.services.stock.SapOaaCommerceStockService;

import de.hybris.platform.basecommerce.enums.StockLevelStatus;
import de.hybris.platform.commercefacades.product.converters.populator.StockPopulator;
import de.hybris.platform.commercefacades.product.data.StockData;
import de.hybris.platform.commerceservices.stock.CommerceStockService;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.ordersplitting.model.StockLevelModel;
import de.hybris.platform.stock.strategy.StockLevelStatusStrategy;
import de.hybris.platform.store.BaseStoreModel;

/**
 * Populate the product data with stock information
 */
public class DefaultOaaStockPopulator<SOURCE extends ProductModel, TARGET extends StockData> extends StockPopulator<SOURCE, TARGET>
{
	private CommerceStockService oaaStockService;
	private CommonUtils commonUtils;
	private StockLevelStatusStrategy stockLevelStatusStrategy;

	public StockLevelStatusStrategy getStockLevelStatusStrategy() {
		return stockLevelStatusStrategy;
	}

	public void setStockLevelStatusStrategy(StockLevelStatusStrategy stockLevelStatusStrategy) {
		this.stockLevelStatusStrategy = stockLevelStatusStrategy;
	}
	
	public CommerceStockService getOaaStockService() {
		return oaaStockService;
	}

	public void setOaaStockService(CommerceStockService oaaStockService) {
		this.oaaStockService = oaaStockService;
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
	public void populate(final SOURCE productModel, final TARGET stockData)
	{
		if (getCommonUtils().isCAREnabled() || getCommonUtils().isCOSEnabled()) {
			final BaseStoreModel baseStore = getBaseStoreService().getCurrentBaseStore();
			if (!isStockSystemEnabled(baseStore)) {
				stockData.setStockLevelStatus(INSTOCK);
				stockData.setStockLevel(Long.valueOf(0));
			} else {
				if (getCommonUtils().isCOSEnabled()) {
					final Long stockValue = ((SapOaaCommerceStockService) getOaaStockService())
							.getAvailableStockLevel(null, null, productModel, null);
					final StockLevelStatus stockLevelStatus = checkStockLevelStatus(stockValue);
					stockData.setStockLevelStatus(stockLevelStatus);
					stockData.setStockLevel(((SapOaaCommerceStockService) getOaaStockService())
							.getStockLevel(stockLevelStatus, productModel, baseStore));
				} else {
					final StockLevelModel stockLevel = ((SapOaaCommerceStockService) getOaaStockService())
							.getStockLevelForRSI(productModel, baseStore);

					if (stockLevel == null) {
						stockData.setStockLevelStatus(OUTOFSTOCK);
					} else {
						stockData.setStockLevelStatus(getStockLevelStatusStrategy().checkStatus(stockLevel));
					}
					stockData.setStockLevel(((SapOaaCommerceStockService) getOaaStockService())
							.getStockLevel(stockData.getStockLevelStatus(), productModel, baseStore));
				}
			}
		} else {
			super.populate(productModel, stockData);
		}
	}

	@Override
	protected boolean isStockSystemEnabled(final BaseStoreModel baseStore)
	{
		return getOaaStockService().isStockSystemEnabled(baseStore);
	}
	
	private StockLevelStatus checkStockLevelStatus(final Long stockLevel)
	{
		if (stockLevel > 0) {
			return INSTOCK;
		} else {
			return OUTOFSTOCK;
		}
	}
}
