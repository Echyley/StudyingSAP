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
import de.hybris.platform.order.strategies.calculation.FindDiscountValuesHook;
import de.hybris.platform.subscriptionservices.model.BillingFrequencyModel;
import de.hybris.platform.subscriptionservices.model.BillingTimeModel;
import de.hybris.platform.subscriptionservices.model.RecurringChargeEntryModel;
import de.hybris.platform.subscriptionservices.model.SubscriptionPricePlanModel;
import de.hybris.platform.subscriptionservices.price.SubscriptionCommercePriceService;
import de.hybris.platform.subscriptionservices.subscription.SubscriptionProductService;
import de.hybris.platform.util.DiscountValue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.log4j.Logger;


public class SubscriptionFindDiscountValuesHook implements FindDiscountValuesHook
{
	private static final Logger LOG = Logger.getLogger(SubscriptionFindDiscountValuesHook.class);

	private SubscriptionCommercePriceService commercePriceService;
	private SubscriptionProductService subscriptionProductService;

	@Override
	public List<DiscountValue> findDiscountValues(final AbstractOrderEntryModel entry)
	{
		validateParameterNotNullStandardMessage("entry", entry);

		final AbstractOrderModel order = entry.getOrder();

		final SubscriptionPricePlanModel pricePlan = getCommercePriceService().getSubscriptionPricePlanForEntry(entry);
		if (pricePlan != null)
		{
			final BillingTimeModel billingTime = order.getBillingTime();
			if (billingTime instanceof BillingFrequencyModel)
			{
				return getDiscountValuesWhenBillingFrequency(order, pricePlan);
			}
		}

		return Collections.emptyList();
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

		final BillingTimeModel billingTime = order.getBillingTime();
		final SubscriptionPricePlanModel pricePlan = getCommercePriceService().getSubscriptionPricePlanForEntry(entry);

		if (pricePlan == null && isOrderBillingTimeNotEqualProductBillingFrequency(product, order))
		{
			return false;
		}

		return pricePlan == null || billingTime instanceof BillingFrequencyModel;
	}

	protected List<DiscountValue> getDiscountValuesWhenBillingFrequency(final AbstractOrderModel order,
			final SubscriptionPricePlanModel pricePlan)
	{
		final RecurringChargeEntryModel firstRecurringPrice = getCommercePriceService().getFirstRecurringPriceFromPlan(pricePlan);
		final RecurringChargeEntryModel lastRecurringPrice = getCommercePriceService().getLastRecurringPriceFromPlan(pricePlan);

		final List<DiscountValue> discountValues = new ArrayList<>();
		LOG.debug("Discounting recurring price: first cycle: "
				+ (firstRecurringPrice != null ? firstRecurringPrice.getPrice() : null) + " last cycle:"
				+ (lastRecurringPrice != null ? lastRecurringPrice.getPrice() : null));
		if (firstRecurringPrice != null && lastRecurringPrice != null)
		{
			discountValues.add(new DiscountValue(firstRecurringPrice.getId(), lastRecurringPrice.getPrice()
					- firstRecurringPrice.getPrice(), true, order.getCurrency().getIsocode()));
		}

		return discountValues;
	}

	private boolean isOrderBillingTimeNotEqualProductBillingFrequency(ProductModel product, AbstractOrderModel order)
	{
		if (order.getBillingTime() == null)
		{
			return false;
		}

		BillingFrequencyModel productBillingFrequency = product.getSubscriptionTerm().getBillingPlan().getBillingFrequency();
		return !order.getBillingTime().equals(productBillingFrequency);
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
