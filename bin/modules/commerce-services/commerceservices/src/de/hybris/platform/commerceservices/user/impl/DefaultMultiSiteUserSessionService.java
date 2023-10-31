/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.commerceservices.user.impl;

import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.commerceservices.model.user.SiteEmployeeGroupModel;
import de.hybris.platform.commerceservices.user.MultiSiteUserSessionService;
import de.hybris.platform.core.Registry;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.session.Session;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.servicelayer.type.TypeService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.site.BaseSiteService;

import java.util.Collections;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static de.hybris.platform.commerceservices.constants.CommerceServicesConstants.NO_SITES_AVAILABLE_DUMMY;
import static de.hybris.platform.commerceservices.constants.CommerceServicesConstants.SITE_SESSION_ATTRIBUTE_NAME;
import static java.util.Collections.singleton;


/**
 * Default implementation of interface {@link MultiSiteUserSessionService}
 */
public class DefaultMultiSiteUserSessionService implements MultiSiteUserSessionService
{
	private static final Logger LOGGER = LoggerFactory.getLogger(DefaultMultiSiteUserSessionService.class);
	private final AtomicBoolean isCustomerSiteExisting = new AtomicBoolean(false);

	private SessionService sessionService;
	private UserService userService;
	private BaseSiteService baseSiteService;
	private TypeService typeService;

	public void setBaseSitesAttributeInSession()
	{
		final Set<BaseSiteModel> site = resolveSites();
		final Session currentSession = getSessionService().getCurrentSession();

		if (currentSession != null)
		{
			if (site.isEmpty())
			{
				currentSession.setAttribute(SITE_SESSION_ATTRIBUTE_NAME, singleton(NO_SITES_AVAILABLE_DUMMY));
			}
			else
			{
				currentSession.setAttribute(SITE_SESSION_ATTRIBUTE_NAME, Collections.unmodifiableCollection(site));
			}
		}
		else
		{
			LOGGER.warn("Could not fetch current session!");
		}
	}

	public void removeBaseSitesAttributeInSession()
	{
		getSessionService().removeAttribute(SITE_SESSION_ATTRIBUTE_NAME);
	}

	protected Set<BaseSiteModel> resolveSites()
	{
		final UserModel currentUser = getUserService().getCurrentUser();
		final Set<BaseSiteModel> sites;

		if (currentUser instanceof CustomerModel)
		{
			final CustomerModel currentCustomer = (CustomerModel) currentUser;
			sites = getSitesForCustomer(currentCustomer);
		}
		else
		{
			sites = getSitesForNonCustomer(currentUser);
		}

		return sites;
	}

	private Set<BaseSiteModel> getSitesForCustomer(final CustomerModel currentCustomer)
	{
		final Set<BaseSiteModel> sites;
		BaseSiteModel site = null;
		if (isCustomerSiteAccessible())
		{
			site = currentCustomer.getSite();
		}

		if (site != null)
		{
			sites = singleton(site);
		}
		else
		{
			final BaseSiteModel currentBaseSite = getBaseSiteService().getCurrentBaseSite();

			if (currentBaseSite == null)
			{
				sites = Collections.emptySet();
				if (LOGGER.isDebugEnabled() && !getUserService().isAnonymousUser(currentCustomer) && !getUserService().isAdmin(
						currentCustomer))
				{
					LOGGER.debug("No Base Site found for customer: {}", currentCustomer.getUid());
				}
			}
			else
			{
				sites = singleton(currentBaseSite);
			}
		}
		return sites;
	}

	private boolean isCustomerSiteAccessible()
	{
		if (isCustomerSiteExisting.get())
		{
			return true;
		}
		else
		{
			try
			{
				getTypeService().getAttributeDescriptor(CustomerModel._TYPECODE, CustomerModel.SITE);
				isCustomerSiteExisting.compareAndSet(false, true);
				return true;
			}
			catch (UnknownIdentifierException e)
			{
				LOGGER.warn("attribute " + CustomerModel.SITE + " is newly added for " + CustomerModel._TYPECODE
						+ " type, It will be found after the system upgrade is competed.");
				return false;
			}
		}
	}

	private Set<BaseSiteModel> getSitesForNonCustomer(final UserModel currentUser)
	{
		final Set<BaseSiteModel> sites;
		final Set<SiteEmployeeGroupModel> allSiteEmpGroups = getUserService().getAllUserGroupsForUser(currentUser,
				SiteEmployeeGroupModel.class);
		if (!allSiteEmpGroups.isEmpty())
		{
			sites = allSiteEmpGroups.stream().map(SiteEmployeeGroupModel::getVisibleSite).filter(Objects::nonNull)
					.collect(Collectors.toSet());
		}
		else
		{
			sites = Collections.emptySet();
		}
		return sites;
	}

	protected SessionService getSessionService()
	{
		return sessionService;
	}

	public void setSessionService(final SessionService sessionService)
	{
		this.sessionService = sessionService;
	}

	protected UserService getUserService()
	{
		return userService;
	}

	public void setUserService(final UserService userService)
	{
		this.userService = userService;
	}

	protected BaseSiteService getBaseSiteService()
	{
		return baseSiteService;
	}

	public void setBaseSiteService(final BaseSiteService baseSiteService)
	{
		this.baseSiteService = baseSiteService;
	}

	protected TypeService getTypeService()
	{
		if (typeService == null)
		{
			typeService = Registry.getApplicationContext()
					.getBean("typeService", TypeService.class);
		}
		return typeService;
	}

	public void setTypeService(final TypeService typeService)
	{
		this.typeService = typeService;
	}
}
