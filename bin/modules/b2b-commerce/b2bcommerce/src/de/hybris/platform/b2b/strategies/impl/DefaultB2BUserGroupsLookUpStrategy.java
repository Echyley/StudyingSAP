/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.b2b.strategies.impl;

import de.hybris.platform.b2b.constants.B2BConstants;
import de.hybris.platform.b2b.strategies.B2BUserGroupsLookUpStrategy;

import java.util.List;

import de.hybris.platform.servicelayer.config.ConfigurationService;
import org.springframework.beans.factory.annotation.Required;


public class DefaultB2BUserGroupsLookUpStrategy implements B2BUserGroupsLookUpStrategy
{

	private ConfigurationService configurationService;
	private List<String> groups;

	@Override
	public List<String> getUserGroups()
	{
		List<String> userGroups = getGroups();

		if (!(getConfigurationService().getConfiguration().getBoolean(B2BConstants.UNITLEVELORDERS_ENABLED, false))) {
			userGroups.remove(B2BConstants.UNITORDERVIEWERGROUP);
		}
		return userGroups;
	}

	protected List<String> getGroups()
	{
		return groups;
	}

	@Required
	public void setGroups(final List<String> groups)
	{
		this.groups = groups;
	}

	protected ConfigurationService getConfigurationService()
	{
		return configurationService;
	}

	public void setConfigurationService(final ConfigurationService configurationService)
	{
		this.configurationService = configurationService;
	}
}
