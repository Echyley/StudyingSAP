/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.subscriptionservices.interceptor.impl;

import de.hybris.platform.servicelayer.interceptor.InterceptorContext;
import de.hybris.platform.servicelayer.interceptor.InterceptorException;
import de.hybris.platform.subscriptionservices.model.SubscriptionPricePlanModel;
import de.hybris.platform.subscriptionservices.model.UsageChargeModel;
import de.hybris.platform.subscriptionservices.model.UsageUnitModel;

import javax.annotation.Nonnull;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

import static org.apache.commons.collections4.CollectionUtils.isNotEmpty;


/**
 * Interceptor to validate SubscriptionPricePlanModel.
 * <ul>
 * <li>the {@link SubscriptionPricePlanModel}'s parent objects are marked as modified
 * </ul>
 */
public class SubscriptionPricePlanValidateInterceptor extends AbstractParentChildValidateInterceptor
{

	@Override
	public void doValidate(@Nonnull final Object model, @Nonnull final InterceptorContext ctx) throws InterceptorException
	{
		if (model instanceof SubscriptionPricePlanModel)
		{
			final SubscriptionPricePlanModel pricePlan = (SubscriptionPricePlanModel) model;
			final Collection<UsageChargeModel> models = pricePlan.getUsageCharges();
			if (isNotEmpty(models))
			{
				Set<UsageUnitModel> unitSet = models.stream().map(UsageChargeModel::getUsageUnit).collect(Collectors.toSet());
				// fast check if we have duplicated unit
				if (unitSet.size() < models.size())
				{
					findDuplicatedAndThrowException(pricePlan);
				}
			}
		}
	}

	private void findDuplicatedAndThrowException(SubscriptionPricePlanModel pricePlan) throws InterceptorException
	{
		// pay some time for error log, it's ok.
		for (final UsageChargeModel m : pricePlan.getUsageCharges())
		{
			for (final UsageChargeModel n : pricePlan.getUsageCharges())
			{
				if (!m.equals(n) && m.getUsageUnit() != null && m.getUsageUnit().equals(n.getUsageUnit()))
				{
					throw new InterceptorException("A usage charge with unit " + m.getUsageUnit().getName()
							+ "is already assigned to the price plan, please modify the existing one instead of creating a second one");
				}
			}
		}
	}
}
