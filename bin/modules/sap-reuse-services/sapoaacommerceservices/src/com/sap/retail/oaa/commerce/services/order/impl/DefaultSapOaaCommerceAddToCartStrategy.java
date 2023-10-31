/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.retail.oaa.commerce.services.order.impl;

import de.hybris.platform.commerceservices.order.impl.DefaultCommerceAddToCartStrategy;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.storelocator.model.PointOfServiceModel;

import org.apache.log4j.Logger;

import com.sap.retail.oaa.commerce.services.common.util.CommonUtils;
import com.sap.retail.oaa.commerce.services.order.SapOaaCartAdjustmentStrategy;


/**
 * Add to Cart Strategy for Omni Channel Availability. Check Allowed quantity on add to cart
 */
public class DefaultSapOaaCommerceAddToCartStrategy extends DefaultCommerceAddToCartStrategy
{
	private static final Logger LOG = Logger.getLogger(DefaultSapOaaCommerceAddToCartStrategy.class);

	private SapOaaCartAdjustmentStrategy oaaCartAdjustmentStrategy;
	private CommonUtils commonUtils;

	@Override
	protected long getAllowedCartAdjustmentForProduct(final CartModel cartModel, final ProductModel productModel,
			final long quantityToAdd, final PointOfServiceModel pointOfServiceModel)
	{
		
		if (getCommonUtils().isCAREnabled() || getCommonUtils().isCOSEnabled()) {
		LOG.debug("Get allowed Cart Adjustment for Product: " + productModel.getCode()
				+ " according SapOaaCartAdjustmenStrategy...");

		final long cartLevel = super.checkCartLevel(productModel, cartModel, pointOfServiceModel);

		return oaaCartAdjustmentStrategy.determineAllowedCartAdjustmentForProduct(cartModel, productModel, quantityToAdd,
				cartLevel, pointOfServiceModel).longValue();
		}
		else {
			return super.getAllowedCartAdjustmentForProduct(cartModel, productModel, quantityToAdd, pointOfServiceModel);
		}
	}

	/**
	 * @return the oaaCartAdjustmentStrategy
	 */
	protected SapOaaCartAdjustmentStrategy getOaaCartAdjustmentStrategy()
	{
		return oaaCartAdjustmentStrategy;
	}

	/**
	 * @param oaaCartAdjustmentStrategy
	 *           the oaaCartAdjustmentStrategy to set
	 */
	public void setOaaCartAdjustmentStrategy(final SapOaaCartAdjustmentStrategy oaaCartAdjustmentStrategy)
	{
		this.oaaCartAdjustmentStrategy = oaaCartAdjustmentStrategy;
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
