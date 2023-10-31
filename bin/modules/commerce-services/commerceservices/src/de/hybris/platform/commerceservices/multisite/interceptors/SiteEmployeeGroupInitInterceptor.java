/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.commerceservices.multisite.interceptors;

import de.hybris.platform.commerceservices.model.user.SiteEmployeeGroupModel;
import de.hybris.platform.core.model.security.PrincipalGroupModel;
import de.hybris.platform.core.model.user.UserGroupModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.interceptor.InitDefaultsInterceptor;
import de.hybris.platform.servicelayer.interceptor.InterceptorContext;
import de.hybris.platform.servicelayer.interceptor.InterceptorException;
import de.hybris.platform.servicelayer.user.UserService;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static de.hybris.platform.commerceservices.constants.CommerceServicesConstants.SITE_EMPLOYEE_GROUP_GROUPS_DEFAULT;


/**
 * Initialize the parent groups based on the property
 */
public class SiteEmployeeGroupInitInterceptor implements InitDefaultsInterceptor<SiteEmployeeGroupModel>
{
	private static final Logger LOGGER = LoggerFactory.getLogger(SiteEmployeeGroupInitInterceptor.class);
	private static final String USER_GROUP_DELIMITER = ",";

	private ConfigurationService configurationService;

	private UserService userService;

	@Override
	public void onInitDefaults(final SiteEmployeeGroupModel siteEmployeeGroupModel, final InterceptorContext ctx)
			throws InterceptorException
	{
		if (ctx.isNew(siteEmployeeGroupModel))
		{
			setParentGroups(siteEmployeeGroupModel);
		}
	}

	private void setParentGroups(final SiteEmployeeGroupModel siteEmployeeGroupModel)
	{
		final String parentGroupNames = getConfigurationService().getConfiguration()
				.getString(SITE_EMPLOYEE_GROUP_GROUPS_DEFAULT, StringUtils.EMPTY);

		if (StringUtils.isNotBlank(parentGroupNames))
		{
			getParentGroupNameList(parentGroupNames).forEach(
					parentGroupName -> addToParentGroups(siteEmployeeGroupModel, parentGroupName));
		}
		else
		{
			LOGGER.warn("The new SiteEmployeeGroup will not be assigned to any group as the property {} is not configured.",
					SITE_EMPLOYEE_GROUP_GROUPS_DEFAULT);
		}
	}

	private List<String> getParentGroupNameList(final String parentGroupNames)
	{
		final List<String> names = new ArrayList<>();
		final StringTokenizer tokenizer = new StringTokenizer(parentGroupNames, USER_GROUP_DELIMITER);

		while (tokenizer.hasMoreTokens())
		{
			names.add(tokenizer.nextToken());
		}

		return names;
	}

	private void addToParentGroups(final SiteEmployeeGroupModel siteEmployeeGroupModel, final String parentGroupName)
	{
		try
		{
			final UserGroupModel parentGroup = getUserService().getUserGroupForUID(parentGroupName);
			Set<PrincipalGroupModel> groups = siteEmployeeGroupModel.getGroups();
			if (groups == null)
			{
				groups = new HashSet<>();
				siteEmployeeGroupModel.setGroups(groups);
			}
			groups.add(parentGroup);
		}
		catch (final UnknownIdentifierException e)
		{
			LOGGER.warn(
					"The new SiteEmployeeGroup will not be assigned to the group as no user group named {} can be found during creation.",
					parentGroupName);
		}
	}

	protected ConfigurationService getConfigurationService()
	{
		return configurationService;
	}

	public void setConfigurationService(final ConfigurationService configurationService)
	{
		this.configurationService = configurationService;
	}

	protected UserService getUserService()
	{
		return userService;
	}

	public void setUserService(final UserService userService)
	{
		this.userService = userService;
	}
}
