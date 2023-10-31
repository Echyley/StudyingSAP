/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.assistedservicefacades.customer360.providers;

import de.hybris.platform.assistedservicefacades.customer360.FragmentModelProvider;
import de.hybris.platform.assistedservicefacades.customer360.FavoriteColorsData;
import de.hybris.platform.servicelayer.user.UserService;

import java.util.Map;


/**
 * Model provider implementation for Customer favorite colors fragment.
 */
public class CustomerFavoriteColorsProvider implements FragmentModelProvider<FavoriteColorsData>
{

	private UserService userService;

	@Override
	public FavoriteColorsData getModel(final Map<String, String> parameters)
	{
		final FavoriteColorsData customerFavoriteColors = new FavoriteColorsData();
		customerFavoriteColors.setName(getUserService().getCurrentUser().getName().split(" ")[0]);
		return customerFavoriteColors;
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
