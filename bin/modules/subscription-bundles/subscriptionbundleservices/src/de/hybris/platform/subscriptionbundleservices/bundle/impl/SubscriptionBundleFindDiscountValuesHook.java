/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.subscriptionbundleservices.bundle.impl;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNullStandardMessage;

import de.hybris.platform.configurablebundleservices.bundle.BundleRuleService;
import de.hybris.platform.configurablebundleservices.bundle.BundleTemplateService;
import de.hybris.platform.configurablebundleservices.model.ChangeProductPriceBundleRuleModel;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.order.EntryGroup;
import de.hybris.platform.subscriptionservices.model.BillingEventModel;
import de.hybris.platform.subscriptionservices.model.BillingFrequencyModel;
import de.hybris.platform.subscriptionservices.model.BillingTimeModel;
import de.hybris.platform.subscriptionservices.model.OneTimeChargeEntryModel;
import de.hybris.platform.subscriptionservices.model.RecurringChargeEntryModel;
import de.hybris.platform.subscriptionservices.model.SubscriptionPricePlanModel;
import de.hybris.platform.subscriptionservices.price.SubscriptionCommercePriceService;
import de.hybris.platform.subscriptionservices.subscription.SubscriptionProductService;
import de.hybris.platform.subscriptionservices.subscription.impl.SubscriptionFindDiscountValuesHook;
import de.hybris.platform.util.DiscountValue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.Nonnull;

import org.apache.commons.collections.CollectionUtils;


public class SubscriptionBundleFindDiscountValuesHook extends SubscriptionFindDiscountValuesHook
{

	private static final String PARAM_ENTRY = "entry";

	private SubscriptionCommercePriceService commercePriceService;
	private SubscriptionProductService subscriptionProductService;
	private BundleRuleService bundleRuleService;
	private BundleTemplateService bundleTemplateService;

	@Override
	public List<DiscountValue> findDiscountValues(final AbstractOrderEntryModel entry)
	{
		validateParameterNotNullStandardMessage(PARAM_ENTRY, entry);

		final AbstractOrderEntryModel masterEntry = entry.getMasterEntry() == null ? entry : entry.getMasterEntry();
		final EntryGroup entryGroup = getBundleTemplateService().getBundleEntryGroup(masterEntry);

		if (entryGroup == null)
		{
			return super.findDiscountValues(entry);
		}

		if (entry.getBasePrice() == null || entry.getBasePrice() <= 0.0D)
		{
			return Collections.emptyList();
		}

		return getDiscountValues(entry, masterEntry);
	}

	@Override
	public boolean isApplicable(final AbstractOrderEntryModel entry)
	{
		final AbstractOrderEntryModel masterEntry = entry.getMasterEntry() == null ? entry : entry.getMasterEntry();
		final EntryGroup entryGroup = getBundleTemplateService().getBundleEntryGroup(masterEntry);

		return entryGroup != null || super.isApplicable(entry);
	}

	@Nonnull
	public List<DiscountValue> getDiscountValues(final @Nonnull AbstractOrderEntryModel entry,
			@Nonnull final AbstractOrderEntryModel masterEntry)
	{
		final List<DiscountValue> discountValues = new ArrayList<>();
		final ProductModel product = entry.getProduct();
		final AbstractOrderModel order = entry.getOrder();
		final CurrencyModel currency = order.getCurrency();

		final ChangeProductPriceBundleRuleModel priceRule = getBundleRuleService().getChangePriceBundleRuleForOrderEntry(
				masterEntry);

		if (getSubscriptionProductService().isSubscription(product))
		{
			final SubscriptionPricePlanModel pricePlan = getCommercePriceService().getSubscriptionPricePlanForEntry(entry);

			if (pricePlan == null)
			{
				return Collections.emptyList();
			}

			if (priceRule == null || priceRule.getBillingEvent() == null)
			{
				reduceRecurringPrice(product, priceRule, discountValues, entry, pricePlan);
			}
			else
			{
				reduceOneTimePrice(pricePlan, priceRule, discountValues, currency, entry);
			}
		}
		else
		{
			// standard products: As standard products are exclusively in the master cart (billing frequency = pay now),
			// the price rule is applied to the pay now price
			if (priceRule != null)
			{
				discountValues.add(createDiscountValue(priceRule.getPrice().doubleValue(), entry.getBasePrice().doubleValue(),
						priceRule.getId(), currency));
			}
		}

		return discountValues;
	}

	/**
	 * The price logic for subscription product: discount are applied to one time charge.
	 *
	 * @param pricePlan
	 *           the subscription price plan
	 * @param priceRule
	 *           the change product price bundle rule
	 * @param discountValues
	 *           the discount values list
	 * @param currency
	 *           the currency
	 * @param entry
	 *           the order entry
	 */
	protected void reduceOneTimePrice(@Nonnull final SubscriptionPricePlanModel pricePlan,
			@Nonnull final ChangeProductPriceBundleRuleModel priceRule,
			@Nonnull final List<DiscountValue> discountValues,
			@Nonnull final CurrencyModel currency,
			@Nonnull final AbstractOrderEntryModel entry)
	{
		validateParameterNotNullStandardMessage("pricePlan", pricePlan);
		validateParameterNotNullStandardMessage("priceRule", priceRule);
		validateParameterNotNullStandardMessage(PARAM_ENTRY, entry);
		validateParameterNotNullStandardMessage("currency", currency);
		validateParameterNotNullStandardMessage("discountValues", discountValues);

		final BillingTimeModel billingTimeOrder = entry.getOrder().getBillingTime();
		if (billingTimeOrder instanceof BillingEventModel
				&& priceRule.getBillingEvent().equals(entry.getOrder().getBillingTime()))
		{
			final OneTimeChargeEntryModel chargeEntry = getCommercePriceService().getOneTimeChargeEntryPlan(
					pricePlan, (BillingEventModel) billingTimeOrder);

			if (chargeEntry == null)
			{
				return;
			}

			final double pricePlanPrice = chargeEntry.getPrice().doubleValue();
			final double priceRulePrice = priceRule.getPrice().doubleValue();
			double discountPrice = pricePlanPrice;
			String id = chargeEntry.getId();

			if (pricePlanPrice >= priceRulePrice)
			{
				id = priceRule.getId();
				discountPrice = priceRulePrice;
			}

			discountValues.add(createDiscountValue(discountPrice, entry.getBasePrice().doubleValue(), id, currency));
		}
	}

	/**
	 * hard coded price logic for subscription products: discounts are only applied to recurring prices
	 *
	 * @param subscriptionProduct
	 *           the subscription product
	 * @param priceRule
	 *           the change product price bundle rule
	 * @param discountValues
	 *           the discount values list
	 * @param entry
	 *           the order entry
	 * @param pricePlan
	 *           the subscription price plan
	 */
	protected void reduceRecurringPrice(final ProductModel subscriptionProduct,
			final ChangeProductPriceBundleRuleModel priceRule, final List<DiscountValue> discountValues,
			final AbstractOrderEntryModel entry, final SubscriptionPricePlanModel pricePlan)
	{
		validateParameterNotNullStandardMessage("pricePlan", pricePlan);
		validateParameterNotNullStandardMessage("subscriptionProduct", subscriptionProduct);
		validateParameterNotNullStandardMessage("discountValues", discountValues);
		validateParameterNotNullStandardMessage(PARAM_ENTRY, entry);

		final BillingTimeModel billingTimeProduct = subscriptionProduct.getSubscriptionTerm().getBillingPlan()
				.getBillingFrequency();
		final BillingTimeModel billingTimeOrder = entry.getOrder().getBillingTime();
		// hard coded price logic for subscription products: discounts are only applied to recurring prices
		if (billingTimeProduct.equals(billingTimeOrder) && (billingTimeOrder instanceof BillingFrequencyModel)
				&& CollectionUtils.isNotEmpty(pricePlan.getRecurringChargeEntries()))
		{
			final double priceRulePrice = priceRule == null ? 0.0D : priceRule.getPrice().doubleValue();
			final RecurringChargeEntryModel chargeEntry = getCommercePriceService().getFirstRecurringPriceFromPlan(pricePlan);
			final double pricePlanPrice = chargeEntry.getPrice();

			// use best price as discount
			double discountPrice = pricePlanPrice;
			String id = chargeEntry.getId();
			if (priceRule != null && pricePlanPrice >= priceRulePrice)
			{
				id = priceRule.getId();
				discountPrice = priceRulePrice;
			}

			discountValues.add(createDiscountValue(discountPrice, entry.getBasePrice().doubleValue(), id, entry.getOrder()
					.getCurrency()));
		}
	}

	/**
	 * Creates a DiscountValue with an absolute reduction based on the given <code>basePrice</code> and the given
	 * <code>discountPrice</code>
	 *
	 * @return {@link DiscountValue}
	 */
	protected DiscountValue createDiscountValue(final double discountPrice, final double basePrice, final String id,
			final CurrencyModel currency)
	{
		return new DiscountValue(id, basePrice - discountPrice, true, currency.getIsocode());
	}

	@Override
	public SubscriptionCommercePriceService getCommercePriceService()
	{
		return commercePriceService;
	}

	@Override
	public void setCommercePriceService(SubscriptionCommercePriceService commercePriceService)
	{
		this.commercePriceService = commercePriceService;
	}

	@Override
	public SubscriptionProductService getSubscriptionProductService()
	{
		return subscriptionProductService;
	}

	@Override
	public void setSubscriptionProductService(
			SubscriptionProductService subscriptionProductService)
	{
		this.subscriptionProductService = subscriptionProductService;
	}

	public BundleRuleService getBundleRuleService()
	{
		return bundleRuleService;
	}

	public void setBundleRuleService(BundleRuleService bundleRuleService)
	{
		this.bundleRuleService = bundleRuleService;
	}

	public BundleTemplateService getBundleTemplateService()
	{
		return bundleTemplateService;
	}

	public void setBundleTemplateService(BundleTemplateService bundleTemplateService)
	{
		this.bundleTemplateService = bundleTemplateService;
	}
}
