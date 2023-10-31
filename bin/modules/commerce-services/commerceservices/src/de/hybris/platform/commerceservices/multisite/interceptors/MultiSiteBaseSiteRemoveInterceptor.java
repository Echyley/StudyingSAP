/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.commerceservices.multisite.interceptors;

import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.commerceservices.customer.CustomerService;
import de.hybris.platform.commerceservices.multisite.exceptions.BaseSiteDataIsolationEnabledRemoveInterceptorException;
import de.hybris.platform.servicelayer.interceptor.InterceptorContext;
import de.hybris.platform.servicelayer.interceptor.InterceptorException;
import de.hybris.platform.servicelayer.interceptor.RemoveInterceptor;

import java.util.Objects;


/**
 * Prevent removing the BaseSite once it is already used in somewhere
 */
public class MultiSiteBaseSiteRemoveInterceptor implements RemoveInterceptor<BaseSiteModel>
{
	private CustomerService customerService;

	@Override
	public void onRemove(final BaseSiteModel baseSiteModel, final InterceptorContext ctx) throws InterceptorException
	{
		if (isDataIsolationEnabled(baseSiteModel) && getCustomerService().getFirstCustomerByBaseSitePK(baseSiteModel.getPk())
				.isPresent())
		{
			throw new BaseSiteDataIsolationEnabledRemoveInterceptorException(
					"Could not remove current BaseSite once the Customer Data Isolation is enabled and still customer(s) is assigned to it");
		}
	}

	private boolean isDataIsolationEnabled(final BaseSiteModel baseSiteModel)
	{
		if (baseSiteModel != null)
		{
			return Objects.equals(Boolean.TRUE, baseSiteModel.getDataIsolationEnabled());
		}

		return false;
	}

	protected CustomerService getCustomerService()
	{
		return customerService;
	}

	public void setCustomerService(final CustomerService customerService)
	{
		this.customerService = customerService;
	}
}
