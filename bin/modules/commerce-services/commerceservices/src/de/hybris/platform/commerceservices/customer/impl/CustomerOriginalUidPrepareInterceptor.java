/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.commerceservices.customer.impl;

import de.hybris.bootstrap.util.LocaleHelper;
import de.hybris.platform.commerceservices.multisite.MultiSiteUidDecorationService;
import de.hybris.platform.commerceservices.multisite.exceptions.CustomerSiteInconsistentInterceptorException;
import de.hybris.platform.core.Registry;
import de.hybris.platform.core.model.security.PrincipalModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.servicelayer.interceptor.InterceptorContext;
import de.hybris.platform.servicelayer.interceptor.InterceptorException;
import de.hybris.platform.servicelayer.interceptor.PrepareInterceptor;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Synchronizes uid, originalUid and undecoratedUid of {@link CustomerModel}
 */
public class CustomerOriginalUidPrepareInterceptor implements PrepareInterceptor
{

	private static final Logger LOGGER = LoggerFactory.getLogger(CustomerOriginalUidPrepareInterceptor.class);

	private MultiSiteUidDecorationService multiSiteUidDecorationService;

	@Override
	public void onPrepare(final Object model, final InterceptorContext ctx) throws InterceptorException
	{
		if (model instanceof CustomerModel)
		{
			final CustomerModel customer = (CustomerModel) model;

			preCheck(ctx, customer);

			if (ctx.isNew(customer) || ctx.isModified(customer, CustomerModel.ORIGINALUID) || ctx.isModified(customer,
					PrincipalModel.UID) || ctx.isModified(customer, CustomerModel.UNDECORATEDUID))
			{
				alignUidAndOriginalUid(customer);

				// undecoratedUid is lowercase of originalUid
				alignUndecoratedUidWithOriginalUid(customer);
			}
		}
	}

	protected void preCheck(final InterceptorContext ctx, final CustomerModel customer) throws InterceptorException
	{
		preCheckSiteConsistency(ctx, customer);
	}

	private void preCheckSiteConsistency(final InterceptorContext ctx, final CustomerModel customer) throws InterceptorException
	{
		if (!ctx.isNew(customer) && ctx.isModified(customer, PrincipalModel.UID) && !ctx.isModified(customer,
				CustomerModel.ORIGINALUID) && customer.getSite() != null)
		{
			checkSiteIsConsistent(customer);
		}
	}

	protected void checkSiteIsConsistent(final CustomerModel customer) throws InterceptorException
	{
		if (!getMultiSiteUidDecorationService().isDecorated(customer.getUid(), customer.getSite().getUid()))
		{
			throw new CustomerSiteInconsistentInterceptorException("The updated ID is not consistent with the attached site.");
		}
	}

	/**
	 * @deprecated since 2211, use {@link CustomerOriginalUidPrepareInterceptor#alignUidAndOriginalUid(CustomerModel)}
	 */
	@Deprecated(since = "2211", forRemoval = true)
	protected void adjustUid(final CustomerModel customer)
	{
		final String original = customer.getOriginalUid();
		final String uid = customer.getUid();
		if (StringUtils.isNotEmpty(uid))
		{
			final String lowerCaseUid = StringUtils.lowerCase(uid);

			if (!uid.equals(lowerCaseUid))
			{
				customer.setUid(lowerCaseUid);
				customer.setOriginalUid(uid);
			}
			else if (!uid.equalsIgnoreCase(original))
			{
				customer.setOriginalUid(uid);
			}
		}
		else if (StringUtils.isNotEmpty(original))
		{
			customer.setUid(original.toLowerCase());
		}
	}

	protected void alignUidAndOriginalUid(final CustomerModel customer)
	{
		if (StringUtils.isNotEmpty(customer.getUid()))
		{
			alignOriginalUidFromUid(customer);
		}
		else if (StringUtils.isNotEmpty(customer.getOriginalUid()))
		{
			alignUidFromOriginUid(customer);
		}

		if (LOGGER.isDebugEnabled())
		{
			LOGGER.debug("Current customer UID is adjusted to: {}, and original uid is: {}", customer.getUid(),
					customer.getOriginalUid());
		}
	}

	protected void alignUndecoratedUidWithOriginalUid(final CustomerModel customer)
	{
		final String lowercaseOriginalUID = StringUtils.lowerCase(customer.getOriginalUid(), LocaleHelper.getPersistenceLocale());

		if (!StringUtils.equals(customer.getUndecoratedUid(), lowercaseOriginalUID))
		{
			customer.setUndecoratedUid(lowercaseOriginalUID);
		}
	}

	private void alignUidFromOriginUid(final CustomerModel customer)
	{
		final String lowCaseOriginalUid = StringUtils.lowerCase(customer.getOriginalUid(), LocaleHelper.getPersistenceLocale());

		final String decoratedCustomerUid;
		if (customer.getSite() != null)
		{
			if (getMultiSiteUidDecorationService().isDecorated(customer.getOriginalUid(), customer.getSite().getUid()))
			{
				final String[] uidElements = getMultiSiteUidDecorationService().undecorate(customer.getOriginalUid(),
						customer.getSite().getUid());
				decoratedCustomerUid = getMultiSiteUidDecorationService().decorate(
						StringUtils.lowerCase(uidElements[0], LocaleHelper.getPersistenceLocale()), customer.getSite().getUid());
				customer.setOriginalUid(uidElements[0]);
			}
			else
			{
				decoratedCustomerUid = getMultiSiteUidDecorationService().decorate(lowCaseOriginalUid, customer.getSite().getUid());
			}
		}
		else
		{
			decoratedCustomerUid = lowCaseOriginalUid;
		}

		customer.setUid(decoratedCustomerUid);
	}

	private void alignOriginalUidFromUid(final CustomerModel customer)
	{
		final String decoratedCustomerUid;
		final String originalUid;

		if (customer.getSite() != null)
		{
			final Pair<String, String> uidAndOriginalUid = getUidAndOriginalUidBySite(customer);
			decoratedCustomerUid = uidAndOriginalUid.getLeft();
			originalUid = uidAndOriginalUid.getRight();
		}
		else
		{
			decoratedCustomerUid = StringUtils.lowerCase(customer.getUid(), LocaleHelper.getPersistenceLocale());
			originalUid = customer.getUid();
		}

		if (!StringUtils.equals(customer.getUid(), decoratedCustomerUid))
		{
			customer.setUid(decoratedCustomerUid);
			customer.setOriginalUid(originalUid);
		}
		// Updating original UID to different value ignore case, restore the original UID back from the UID
		else if (!StringUtils.equalsIgnoreCase(customer.getOriginalUid(), originalUid))
		{
			customer.setOriginalUid(originalUid);
		}
	}

	// Get the UID and Original UID based on the attribute site
	// The return pair is: left value is uid and the right value is original uid
	private Pair<String, String> getUidAndOriginalUidBySite(final CustomerModel customer)
	{
		final String decoratedCustomerUid;
		final String originalUid;

		if (!getMultiSiteUidDecorationService().isDecorated(customer.getUid(), customer.getSite().getUid()))
		{
			decoratedCustomerUid = decorateUidWithLowCaseOriginalUid(customer.getUid(), customer.getSite().getUid());
			originalUid = tryToGetCaseSensitiveID(customer);
		}
		else
		{
			final String[] uidElements = getMultiSiteUidDecorationService().undecorate(customer.getUid(),
					customer.getSite().getUid());
			decoratedCustomerUid = decorateUidWithLowCaseOriginalUid(uidElements[0], uidElements[1]);
			originalUid = uidElements[0];
		}

		return ImmutablePair.of(decoratedCustomerUid, originalUid);
	}

	private String tryToGetCaseSensitiveID(final CustomerModel customer)
	{
		final String originalUid;
		final String lowercaseUid = StringUtils.lowerCase(customer.getUid(), LocaleHelper.getPersistenceLocale());
		if (StringUtils.equalsIgnoreCase(customer.getUid(), customer.getOriginalUid()) && StringUtils.equals(lowercaseUid,
				customer.getUid()) && !StringUtils.equals(lowercaseUid, customer.getOriginalUid()))
		{
			originalUid = customer.getOriginalUid();
		}
		else
		{
			originalUid = customer.getUid();
		}
		return originalUid;
	}

	private String decorateUidWithLowCaseOriginalUid(final String customerUid, final String baseSiteUid)
	{
		return getMultiSiteUidDecorationService().decorate(StringUtils.lowerCase(customerUid, LocaleHelper.getPersistenceLocale()),
				baseSiteUid);
	}

	protected MultiSiteUidDecorationService getMultiSiteUidDecorationService()
	{
		if (multiSiteUidDecorationService == null)
		{
			multiSiteUidDecorationService = Registry.getApplicationContext()
					.getBean("multiSiteUidDecorationService", MultiSiteUidDecorationService.class);
		}
		return multiSiteUidDecorationService;
	}

	public void setMultiSiteUidDecorationService(final MultiSiteUidDecorationService multiSiteUidDecorationService)
	{
		this.multiSiteUidDecorationService = multiSiteUidDecorationService;
	}
}
