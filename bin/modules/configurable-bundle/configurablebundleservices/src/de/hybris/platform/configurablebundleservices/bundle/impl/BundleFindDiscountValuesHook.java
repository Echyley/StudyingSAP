/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.configurablebundleservices.bundle.impl;

import de.hybris.platform.configurablebundleservices.bundle.BundleRuleService;
import de.hybris.platform.configurablebundleservices.bundle.BundleTemplateService;
import de.hybris.platform.configurablebundleservices.model.ChangeProductPriceBundleRuleModel;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.order.EntryGroup;
import de.hybris.platform.order.strategies.calculation.FindDiscountValuesHook;
import de.hybris.platform.servicelayer.util.ServicesUtil;
import de.hybris.platform.util.DiscountValue;

import java.util.ArrayList;
import java.util.List;


public class BundleFindDiscountValuesHook implements FindDiscountValuesHook
{
	private BundleRuleService bundleRuleService;
	private BundleTemplateService bundleTemplateService;

	@Override
	public List<DiscountValue> findDiscountValues(final AbstractOrderEntryModel entry)
	{
		ServicesUtil.validateParameterNotNullStandardMessage("entry", entry);

		final List<DiscountValue> discountValues = new ArrayList<>();

		final CurrencyModel currency = entry.getOrder().getCurrency();

		final ChangeProductPriceBundleRuleModel priceRule = getBundleRuleService().getChangePriceBundleRuleForOrderEntry(entry);
		if (priceRule != null)
		{
			discountValues.add(createDiscountValue(priceRule.getPrice().doubleValue(), entry.getBasePrice().doubleValue(),
					priceRule.getId(), currency));
		}
		return discountValues;
	}

	@Override
	public boolean isApplicable(final AbstractOrderEntryModel entry)
	{
		final EntryGroup component = getBundleTemplateService().getBundleEntryGroup(entry);

		return component != null;
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

	public void setBundleTemplateService(BundleTemplateService bundleTemplateService)
	{
		this.bundleTemplateService = bundleTemplateService;
	}

	public BundleTemplateService getBundleTemplateService()
	{
		return bundleTemplateService;
	}

	public void setBundleRuleService(BundleRuleService bundleRuleService)
	{
		this.bundleRuleService = bundleRuleService;
	}

	public BundleRuleService getBundleRuleService()
	{
		return bundleRuleService;
	}
}
