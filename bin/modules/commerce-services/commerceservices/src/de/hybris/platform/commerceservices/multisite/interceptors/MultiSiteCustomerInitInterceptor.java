/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.commerceservices.multisite.interceptors;

import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.servicelayer.interceptor.InitDefaultsInterceptor;
import de.hybris.platform.servicelayer.interceptor.InterceptorContext;
import de.hybris.platform.servicelayer.interceptor.InterceptorException;
import de.hybris.platform.site.BaseSiteService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Objects;


/**
 * Initialize the Customer.site once it is created
 */
public class MultiSiteCustomerInitInterceptor implements InitDefaultsInterceptor<CustomerModel>
{
	private static final Logger LOGGER = LoggerFactory.getLogger(MultiSiteCustomerInitInterceptor.class);

	private BaseSiteService baseSiteService;

	public void onInitDefaults(final CustomerModel customerModel, final InterceptorContext ctx) throws InterceptorException
	{
		if (ctx.isNew(customerModel) && customerModel.getSite() == null)
		{
			initBaseSite(customerModel);
		}
	}

	protected void initBaseSite(final CustomerModel customerModel)
	{
		final BaseSiteModel currentBaseSite = getBaseSiteService().getCurrentBaseSite();

		if (currentBaseSite != null && Objects.equal(Boolean.TRUE, currentBaseSite.getDataIsolationEnabled()))
		{
			customerModel.setSite(currentBaseSite);
			LOGGER.info("The data isolation option is enabled for storefront BaseSite: '{}', assign storefront customer to it.",
					currentBaseSite.getUid());
		}
	}

	protected BaseSiteService getBaseSiteService()
	{
		return baseSiteService;
	}

	public void setBaseSiteService(final BaseSiteService baseSiteService)
	{
		this.baseSiteService = baseSiteService;
	}
}
