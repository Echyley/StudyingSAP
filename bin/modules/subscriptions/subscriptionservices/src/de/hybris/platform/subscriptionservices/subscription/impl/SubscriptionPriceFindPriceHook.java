/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.subscriptionservices.subscription.impl;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNullStandardMessage;

import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.order.strategies.calculation.FindPriceHook;
import de.hybris.platform.subscriptionservices.model.BillingFrequencyModel;
import de.hybris.platform.subscriptionservices.model.OneTimeChargeEntryModel;
import de.hybris.platform.subscriptionservices.model.RecurringChargeEntryModel;
import de.hybris.platform.subscriptionservices.model.SubscriptionPricePlanModel;
import de.hybris.platform.subscriptionservices.price.SubscriptionCommercePriceService;
import de.hybris.platform.subscriptionservices.subscription.SubscriptionProductService;
import de.hybris.platform.util.PriceValue;

import org.apache.log4j.Logger;

/**
 * This hook should implement the price finding strategy for {@link ProductModel}s.
 */
public class SubscriptionPriceFindPriceHook implements FindPriceHook
{
	private static final Logger LOG = Logger.getLogger(SubscriptionPriceFindPriceHook.class);

	private SubscriptionCommercePriceService commercePriceService;
	private SubscriptionProductService subscriptionProductService;

	/**
	 * Resolves the subscription price value for the given AbstractOrderEntryModel by searching a {@link ProductModel}
	 * that is applicable for the entry's subscription product. In case the entry's product is not a {@code ProductModel}
	 * or there is no {@code ProductModel} for it, the method is not applied
	 */
	@Override
	public PriceValue findCustomBasePrice(final AbstractOrderEntryModel entry, final PriceValue defaultPrice)
	{
		if (LOG.isDebugEnabled())
		{
			LOG.debug("Obtaining base price for subscription product " + entry.getProduct().getCode());
		}
		validateParameterNotNullStandardMessage("entry", entry);

		final ProductModel product = entry.getProduct();
		final AbstractOrderModel order = entry.getOrder();

		final SubscriptionPricePlanModel pricePlan = getCommercePriceService().getSubscriptionPricePlanForEntry(entry);
		if (isBillingTimeNotEqualBillingFrequency(product, order, pricePlan))
		{
			return new PriceValue(order.getCurrency().getIsocode(), 0.0D, order.getNet());
		}

		if (order.getBillingTime() instanceof BillingFrequencyModel)
		{
			return createPriceValueForLastRecurringPrice(order, pricePlan);
		}

		if (pricePlan == null) {
			return null;
		}

		for (final OneTimeChargeEntryModel chargeEntry : pricePlan.getOneTimeChargeEntries())
		{
			if (order.getBillingTime().equals(chargeEntry.getBillingEvent()))
			{
				LOG.debug("Using onetime price " + order.getBillingTime().getCode() + ": " + chargeEntry.getPrice());

				return new PriceValue(order.getCurrency().getIsocode(), chargeEntry.getPrice(), order.getNet());
			}
		}

		return null;
	}

	@Override
	public boolean isApplicable(final AbstractOrderEntryModel entry)
	{
		final ProductModel product = entry.getProduct();
		final AbstractOrderModel order = entry.getOrder();

		if (!getSubscriptionProductService().isSubscription(product))
		{
			return false;
		}
		
		final SubscriptionPricePlanModel pricePlan = getCommercePriceService().getSubscriptionPricePlanForEntry(entry);

		if (!isBillingTimeNotEqualBillingFrequency(product, order, pricePlan) && pricePlan == null)
		{
			return false;
		}

		if (pricePlan == null) {
			return false;
		}

		if (order.getBillingTime() instanceof BillingFrequencyModel) {
			return true;
		}

		return pricePlan.getOneTimeChargeEntries()
				.stream()
				.anyMatch(chargeEntry -> order.getBillingTime().equals(chargeEntry.getBillingEvent()));
	}

	private boolean isBillingTimeNotEqualBillingFrequency(
			final ProductModel product,
			final AbstractOrderModel order,
			final SubscriptionPricePlanModel pricePlan)
	{
		return pricePlan == null && order.getBillingTime() != null
				&& !order.getBillingTime().equals(product.getSubscriptionTerm().getBillingPlan().getBillingFrequency());
	}

	protected PriceValue createPriceValueForLastRecurringPrice(final AbstractOrderModel order,
			final SubscriptionPricePlanModel pricePlan)
	{
		final RecurringChargeEntryModel lastRecurringPrice = getCommercePriceService().getLastRecurringPriceFromPlan(pricePlan);
		LOG.debug("Using recurring " + order.getBillingTime().getCode() + " price: "
				+ (lastRecurringPrice != null ? lastRecurringPrice.getPrice() : null));
		return new PriceValue(order.getCurrency().getIsocode(), lastRecurringPrice != null ? lastRecurringPrice.getPrice() : 0,
				order.getNet());
	}

	public SubscriptionCommercePriceService getCommercePriceService()
	{
		return commercePriceService;
	}

	public void setCommercePriceService(SubscriptionCommercePriceService commercePriceService)
	{
		this.commercePriceService = commercePriceService;
	}

	public SubscriptionProductService getSubscriptionProductService()
	{
		return subscriptionProductService;
	}

	public void setSubscriptionProductService(
			SubscriptionProductService subscriptionProductService)
	{
		this.subscriptionProductService = subscriptionProductService;
	}
}
