/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.retail.oaa.commerce.services.strategies.impl;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.collections.CollectionUtils;

import com.sap.retail.oaa.commerce.services.common.util.CommonUtils;
import de.hybris.platform.basecommerce.enums.StockLevelStatus;
import de.hybris.platform.commerceservices.enums.PickupInStoreMode;
import de.hybris.platform.commerceservices.stock.CommerceStockService;
import de.hybris.platform.commerceservices.strategies.PickupStrategy;
import de.hybris.platform.commerceservices.strategies.impl.DefaultPickupAvailabilityStrategy;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.storelocator.model.PointOfServiceModel;


/**
 * Default Pick up Strategy
 */
public class DefaultSapOaaPickupAvailabilityStrategy extends DefaultPickupAvailabilityStrategy
{

	private PickupStrategy pickupStrategy;
	private CommerceStockService commerceStockService;
	private CommonUtils commonUtils;
	

	protected PickupStrategy getPickupStrategy()
	{
		return pickupStrategy;
	}


	public void setPickupStrategy(final PickupStrategy pickupStrategy)
	{
		this.pickupStrategy = pickupStrategy;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.hybris.platform.commerceservices.strategies.PickupAvailabilityStrategy#isPickupAvailableForProduct(de.hybris.
	 * platform.core.model.product.ProductModel, de.hybris.platform.store.BaseStoreModel)
	 */
	@Override
	public Boolean isPickupAvailableForProduct(final ProductModel product, final BaseStoreModel baseStore)
	{
		if (getCommonUtils().isCAREnabled() || getCommonUtils().isCOSEnabled()) {
		if (!PickupInStoreMode.DISABLED.equals(getPickupStrategy().getPickupInStoreMode()))
		{
			if (baseStore !=null && CollectionUtils.isNotEmpty(baseStore.getPointsOfService()))
			{
				return isProductAvailableInAnyStore(product, baseStore);
			}
		}
		return Boolean.FALSE;
		}
		else {
			return super.isPickupAvailableForProduct(product,baseStore);
		}
	}

	/**
	 * @param product
	 * @param baseStore
	 */
	public Boolean isProductAvailableInAnyStore(final ProductModel product, final BaseStoreModel baseStore)
	{
		final Map<PointOfServiceModel, StockLevelStatus> stockLevelStatusMap = commerceStockService
				.getPosAndStockLevelStatusForProduct(product, baseStore);

		final Iterator iter = stockLevelStatusMap.entrySet().iterator();
		while (iter.hasNext())
		{
			final Map.Entry<PointOfServiceModel, StockLevelStatus> entry = (Entry<PointOfServiceModel, StockLevelStatus>) iter
					.next();
			if (entry.getValue().equals(StockLevelStatus.INSTOCK))
			{
				return Boolean.TRUE;
			}
		}

		return Boolean.FALSE;
	}

	/**
	 * @param commerceStockService
	 *           the commerceStockService to set
	 */
	public void setCommerceStockService(final CommerceStockService commerceStockService)
	{
		this.commerceStockService = commerceStockService;
	}

	/**
	 * @return the commerceStockService
	 */
	protected CommerceStockService getCommerceStockService()
	{
		return commerceStockService;
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

}
