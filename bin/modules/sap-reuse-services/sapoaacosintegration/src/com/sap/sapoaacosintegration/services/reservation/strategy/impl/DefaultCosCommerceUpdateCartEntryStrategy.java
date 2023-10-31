/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.sapoaacosintegration.services.reservation.strategy.impl;

import de.hybris.platform.commerceservices.order.CommerceCartModification;
import de.hybris.platform.commerceservices.order.impl.DefaultCommerceUpdateCartEntryStrategy;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.storelocator.model.PointOfServiceModel;

import org.apache.log4j.Logger;

import com.sap.retail.oaa.commerce.services.common.util.CommonUtils;
import com.sap.retail.oaa.commerce.services.order.SapOaaCartAdjustmentStrategy;
import com.sap.sapoaacosintegration.services.reservation.strategy.ReservationStrategy;


/**
 * Update Cart Entry Strategy for Omni Channel Availability. Check Allowed quantity on cart update
 */
public class DefaultCosCommerceUpdateCartEntryStrategy extends DefaultCommerceUpdateCartEntryStrategy
{
	private static final Logger LOG = Logger.getLogger(DefaultCosCommerceUpdateCartEntryStrategy.class);

	private SapOaaCartAdjustmentStrategy cosCartAdjustmentStrategy;
	private ReservationStrategy cosReservationStrategy;
	private CommonUtils commonUtils;

	@Override
	protected long getAllowedCartAdjustmentForProduct(final CartModel cartModel, final ProductModel productModel,
			final long quantityToAdd, final PointOfServiceModel pointOfServiceModel)
	{
		if (getCommonUtils().isCOSEnabled())
		{
			LOG.debug("Get allowed Cart Adjustment for Product: " + productModel.getCode() + " according SapCartAdjustmenStrategy...");

			final long cartLevel = super.checkCartLevel(productModel, cartModel, pointOfServiceModel);

			return cosCartAdjustmentStrategy
					.determineAllowedCartAdjustmentForProduct(cartModel, productModel, quantityToAdd, cartLevel, pointOfServiceModel)
					.longValue();
		}
		else
		{
			return super.getAllowedCartAdjustmentForProduct(cartModel, productModel, quantityToAdd, pointOfServiceModel);
		}
	}

	@Override
	protected CommerceCartModification modifyEntry(final CartModel cartModel, final AbstractOrderEntryModel entryToUpdate,
			final long actualAllowedQuantityChange, final long newQuantity, final Integer maxOrderQuantity)
	{
		if (getCommonUtils().isCOSEnabled() && (entryToUpdate.getQuantity().longValue() + actualAllowedQuantityChange) <= 0)
		{
			//When Entry will be removed delete reservation
			this.getCosReservationStrategy().deleteReservationItem(cartModel, entryToUpdate);
		}
		return super.modifyEntry(cartModel, entryToUpdate, actualAllowedQuantityChange, newQuantity, maxOrderQuantity);
	}

	/**
	 * @param oaaCartAdjustmentStrategy
	 *           the oaaCartAdjustmentStrategy to set
	 */
	public void setCosCartAdjustmentStrategy(final SapOaaCartAdjustmentStrategy oaaCartAdjustmentStrategy)
	{
		this.cosCartAdjustmentStrategy = oaaCartAdjustmentStrategy;
	}

	/**
	 * @return the oaaCartAdjustmentStrategy
	 */
	protected SapOaaCartAdjustmentStrategy getCosCartAdjustmentStrategy()
	{
		return cosCartAdjustmentStrategy;
	}


	public ReservationStrategy getCosReservationStrategy()
	{
		return cosReservationStrategy;
	}

	public void setCosReservationStrategy(final ReservationStrategy cosReservationStrategy)
	{
		this.cosReservationStrategy = cosReservationStrategy;
	}

	public CommonUtils getCommonUtils()
	{
		return commonUtils;
	}

	public void setCommonUtils(final CommonUtils commonUtils)
	{
		this.commonUtils = commonUtils;
	}
}
