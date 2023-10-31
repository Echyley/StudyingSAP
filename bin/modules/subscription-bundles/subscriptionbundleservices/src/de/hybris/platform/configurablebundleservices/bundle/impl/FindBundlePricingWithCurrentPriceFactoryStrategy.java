/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.configurablebundleservices.bundle.impl;

import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.order.exceptions.CalculationException;
import de.hybris.platform.subscriptionbundleservices.bundle.impl.SubscriptionBundleFindDiscountValuesHook;
import de.hybris.platform.subscriptionservices.subscription.impl.FindSubscriptionPricingWithCurrentPriceFactoryStrategy;
import de.hybris.platform.util.DiscountValue;

import java.util.Collections;
import java.util.List;

import javax.annotation.Nonnull;



/**
 * This strategy should replace the FindPricingWithCurrentPriceFactoryStrategy and will change the discount calculation
 * for bundle entries based on the bundle rules. So based on the products that exist in the cart in the same bundle, the
 * price rules are evaluated and the a matching reduced price is found. Once the final price has been identified, the
 * same logic applies as for DiscountValues and they are persisted in exactly the same way in the database as well as
 * the calculation is don in exactly the same way.
 *
 * @deprecated Since 2105. Use
 * {@link SubscriptionBundleFindDiscountValuesHook} instead.
 */
@Deprecated(since = "2105", forRemoval = true)
public class FindBundlePricingWithCurrentPriceFactoryStrategy extends FindSubscriptionPricingWithCurrentPriceFactoryStrategy
{

	private transient SubscriptionBundleFindDiscountValuesHook subscriptionBundleFindDiscountValuesHook;

	/**
	 * For non Bundle products it should worBundleNavigationNodeContentMainComponentk as is, so the DiscountValues should result
	 * from the DiscountRows defined at the product.
	 * 
	 * For Bundle products it should work in a different way: All other products where the
	 * AbstractOrderEntries have the same entry group number should be added to a list for comparison. The Product of the current
	 * AbstractOrderEntry will be used to identify PriceRules from the current BundleTemplate where that product is set
	 * as a target. All conditional products of the rules have to be in the comparison list and that is how we find the
	 * matching rules. If there are still multiple matching rules, because, then the one with the lowest price in the
	 * current currency will be used as discount. That absolute discount will be converted into a relative DiscountValue
	 * for the AbstractOrderEntry only. All calculations of totals later will work as is. For subscription products the
	 * discount is applied to the 1st tier of recurring charge entries in the price plan. In case there is a price rule
	 * that can be applied to the subscription product and there is also a discount that comes from the tiered recurring
	 * charge entries, the better (= cheaper) price is used as discount.
	 * 
	 * For subscription product, if the there is price rule with the billing event, the specific one time charge price
	 * reduce logic will be applied.
	 */
	@Override
	@Nonnull
	// NO SONAR
	public List<DiscountValue> findDiscountValues(@Nonnull final AbstractOrderEntryModel entry) throws CalculationException
	{
		if (!subscriptionBundleFindDiscountValuesHook.isApplicable(entry))
		{
			return Collections.emptyList();
		}

		return subscriptionBundleFindDiscountValuesHook.findDiscountValues(entry);
	}

	@Nonnull
	protected List<DiscountValue> getDiscountValues(final @Nonnull AbstractOrderEntryModel entry,
			@Nonnull final AbstractOrderEntryModel masterEntry)
	{
		if (!subscriptionBundleFindDiscountValuesHook.isApplicable(entry))
		{
			return Collections.emptyList();
		}

		return subscriptionBundleFindDiscountValuesHook.getDiscountValues(entry, masterEntry);
	}

	public SubscriptionBundleFindDiscountValuesHook getSubscriptionBundleFindDiscountValuesHook()
	{
		return subscriptionBundleFindDiscountValuesHook;
	}

	public void setSubscriptionBundleFindDiscountValuesHook(
			SubscriptionBundleFindDiscountValuesHook subscriptionBundleFindDiscountValuesHook)
	{
		this.subscriptionBundleFindDiscountValuesHook = subscriptionBundleFindDiscountValuesHook;
	}
}
