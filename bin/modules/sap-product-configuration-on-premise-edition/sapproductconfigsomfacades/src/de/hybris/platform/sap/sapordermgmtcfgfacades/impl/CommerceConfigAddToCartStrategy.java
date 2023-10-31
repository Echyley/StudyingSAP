/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.sapordermgmtcfgfacades.impl;

import de.hybris.platform.commerceservices.order.impl.DefaultCommerceAddToCartStrategy;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.storelocator.model.PointOfServiceModel;


/**
 * DefaultAddToCartStrategy is being overriden by ProductConfigAddToCartStrategy and this class makes sure that the
 * functionality offered by both classes do not get lost.
 *
 */

public class CommerceConfigAddToCartStrategy extends DefaultCommerceAddToCartStrategy
{
	@Override
	protected long getAllowedCartAdjustmentForProduct(final CartModel cartModel, final ProductModel productModel,
			final long quantityToAdd, final PointOfServiceModel pointOfServiceModel)
	{
		if (!isSyncOrdermgmtEnabled())
		{
			return super.getAllowedCartAdjustmentForProduct(cartModel, productModel, quantityToAdd, pointOfServiceModel);
		}
		return quantityToAdd;
	}

	protected boolean isSyncOrdermgmtEnabled()
	{
		return (getBaseStoreService().getCurrentBaseStore().getSAPConfiguration() != null)
				&& (getBaseStoreService().getCurrentBaseStore().getSAPConfiguration().isSapordermgmt_enabled());
	}

}
