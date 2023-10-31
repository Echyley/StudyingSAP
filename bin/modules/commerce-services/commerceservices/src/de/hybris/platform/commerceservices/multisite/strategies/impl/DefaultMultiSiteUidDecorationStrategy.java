/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.commerceservices.multisite.strategies.impl;

import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.commerceservices.multisite.MultiSiteUidDecorationService;
import de.hybris.platform.commerceservices.multisite.strategies.MultiSiteUidDecorationStrategy;
import de.hybris.platform.servicelayer.user.UserConstants;
import de.hybris.platform.site.BaseSiteService;

import java.util.Objects;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



/**
 * The default implementation for interface MultiSiteUidDecorationStrategy
 */
public class DefaultMultiSiteUidDecorationStrategy implements MultiSiteUidDecorationStrategy
{
	private static final Logger LOGGER = LoggerFactory.getLogger(DefaultMultiSiteUidDecorationStrategy.class);

	private MultiSiteUidDecorationService multiSiteUidDecorationService;
	private BaseSiteService baseSiteService;

	@Override
	public Optional<String> decorateUserId(final String userId)
	{
		if (isNotSpecialUser(userId) && isCurrentSiteDataIsolationEnabled() && !getMultiSiteUidDecorationService().isDecorated(userId,
				getBaseSiteService().getCurrentBaseSite().getUid()))
		{
			final String decoratedUid = getMultiSiteUidDecorationService().decorate(userId,
					getBaseSiteService().getCurrentBaseSite().getUid());

			if (LOGGER.isDebugEnabled() && !Objects.equals(userId, decoratedUid))
			{
				LOGGER.debug("Convert user's userId from {} to: {}", decoratedUid, userId);
			}

			return Optional.of(decoratedUid);
		}

		return Optional.empty();
	}

	private boolean isNotSpecialUser(final String customerUid)
	{
		return !Objects.equals(UserConstants.ANONYMOUS_CUSTOMER_UID, customerUid) && !Objects.equals(
				UserConstants.ADMIN_EMPLOYEE_UID, customerUid);
	}

	private boolean isCurrentSiteDataIsolationEnabled()
	{
		final BaseSiteModel currentBaseSite = getBaseSiteService().getCurrentBaseSite();

		return currentBaseSite != null && Objects.equals(Boolean.TRUE, currentBaseSite.getDataIsolationEnabled());
	}

	protected MultiSiteUidDecorationService getMultiSiteUidDecorationService()
	{
		return multiSiteUidDecorationService;
	}

	public void setMultiSiteUidDecorationService(final MultiSiteUidDecorationService multiSiteUidDecorationService)
	{
		this.multiSiteUidDecorationService = multiSiteUidDecorationService;
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
