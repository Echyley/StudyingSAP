/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.datahubbackoffice.service.datahub.impl;

import de.hybris.platform.core.model.user.UserGroupModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.datahubbackoffice.service.datahub.UserContext;
import de.hybris.platform.dataimportcommons.ApplicationBeans;
import de.hybris.platform.servicelayer.user.UserService;

import java.io.Serial;
import java.io.Serializable;

/**
 * Stateful service for retrieving information about the user in the context of a web request.
 */
public class UserContextImpl implements UserContext, Serializable
{
	@Serial
	private static final long serialVersionUID = 1734800045352907935L;

	private transient UserService userService;
	private UserModel currentUser;
	private UserGroupModel datahubAdminGroup;

	@Override
	public UserModel getCurrentUser()
	{
		if (currentUser == null)
		{
			currentUser = getUserService().getCurrentUser();
		}
		return currentUser;
	}

	@Override
	public boolean isUserDataHubAdmin()
	{
		return isMemberOf(getDataHubAdminGroup());
	}

	@Override
	public boolean isMemberOf(final String userGroup)
	{
		return isMemberOf(getUserGroup(userGroup));
	}

	private boolean isMemberOf(final UserGroupModel group)
	{
		return userService.isMemberOfGroup(getCurrentUser(), group);
	}

	private UserGroupModel getDataHubAdminGroup()
	{
		if (datahubAdminGroup == null)
		{
			datahubAdminGroup = getUserGroup("datahubadmingroup");
		}
		return datahubAdminGroup;
	}

	private UserGroupModel getUserGroup(final String groupId)
	{
		return userService.getUserGroupForUID(groupId);
	}

	public void setUserService(final UserService userService)
	{
		this.userService = userService;
	}

	private UserService getUserService()
	{
		if (userService == null)
		{
			userService = ApplicationBeans.getBean("userService", UserService.class);
		}
		return userService;
	}
}
