/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.configurablebundlefacades.order.converters.populator;

import de.hybris.platform.commercefacades.order.converters.populator.AbstractOrderPopulator;
import de.hybris.platform.commercefacades.order.data.AbstractOrderData;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.promotions.result.PromotionOrderResults;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNullStandardMessage;


/**
 * Modify the cart converter to show the first invalid bundle component in the cart (if it exists).
 *
 * @since 6.4
 */
public class BundleCommerceCartPopulator<S extends CartModel, T extends CartData> extends AbstractOrderPopulator<S, T>
{
	/**
	 * Modify populate method to set the first incomplete bundle component in the cart {@link CartModel}
	 */
	@Override
	public void populate(final S source, final T target)
	{
		validateParameterNotNullStandardMessage("source", source);
		validateParameterNotNullStandardMessage("target", target);

		if (target.getEntries() == null || target.getEntries().isEmpty()) {
			addEntries(source, target);
		}
		addPromotions(source, target);
	}

	@Override
	protected void addPromotions(final AbstractOrderModel source, final AbstractOrderData target)
	{
		addPromotions(source, getPromotionsService().getPromotionResults(source), target);
	}

	@Override
	protected void addPromotions(final AbstractOrderModel source, final PromotionOrderResults promoOrderResults,
			final AbstractOrderData target)
	{
		if (promoOrderResults != null)
		{
			final CartData cartData = (CartData) target;
			cartData.setPotentialOrderPromotions(getPromotions(promoOrderResults.getPotentialOrderPromotions()));
			cartData.setPotentialProductPromotions(getPromotions(promoOrderResults.getPotentialProductPromotions()));
		}
	}
}
