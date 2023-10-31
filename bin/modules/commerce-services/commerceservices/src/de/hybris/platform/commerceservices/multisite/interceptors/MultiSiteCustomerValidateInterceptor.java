/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.commerceservices.multisite.interceptors;

import de.hybris.platform.commerceservices.multisite.MultiSiteUidDecorationService;
import de.hybris.platform.commerceservices.multisite.exceptions.CustomerSiteInconsistentInterceptorException;
import de.hybris.platform.commerceservices.multisite.exceptions.CustomerSiteIsNotDataIsolatedInterceptorException;
import de.hybris.platform.core.model.security.PrincipalModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.servicelayer.interceptor.InterceptorContext;
import de.hybris.platform.servicelayer.interceptor.InterceptorException;
import de.hybris.platform.servicelayer.interceptor.ValidateInterceptor;

import com.google.common.base.Objects;


/**
 * Ensure customer is attached to isolated site
 */
public class MultiSiteCustomerValidateInterceptor implements ValidateInterceptor<CustomerModel>
{
	private MultiSiteUidDecorationService multiSiteUidDecorationService;

	@Override
	public void onValidate(final CustomerModel customer, final InterceptorContext ctx) throws InterceptorException
	{
		//Check the attached site should be data isolated when create a customer with given attached site
		if (ctx.isNew(customer) && customer.getSite() != null && !Objects.equal(Boolean.TRUE,
				customer.getSite().getDataIsolationEnabled()))
		{
			throw new CustomerSiteIsNotDataIsolatedInterceptorException("The attached site should be data isolated.");
		}

		// Check the UID and site consistency
		if (ctx.isModified(customer, PrincipalModel.UID) && customer.getSite() != null
				&& !getMultiSiteUidDecorationService().isDecorated(customer.getUid(), customer.getSite().getUid()))
		{
			throw new CustomerSiteInconsistentInterceptorException("The UID is not consistent with the attached site.");
		}
	}

	protected MultiSiteUidDecorationService getMultiSiteUidDecorationService()
	{
		return multiSiteUidDecorationService;
	}

	public void setMultiSiteUidDecorationService(final MultiSiteUidDecorationService multiSiteUidDecorationService)
	{
		this.multiSiteUidDecorationService = multiSiteUidDecorationService;
	}
}
