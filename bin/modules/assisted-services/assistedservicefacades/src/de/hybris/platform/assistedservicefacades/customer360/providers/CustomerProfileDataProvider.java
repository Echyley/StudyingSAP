/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.assistedservicefacades.customer360.providers;

import de.hybris.platform.assistedservicefacades.customer360.FragmentModelProvider;
import de.hybris.platform.assistedservicefacades.customer360.CustomerProfileData;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.user.UserService;

import java.util.Map;


public class CustomerProfileDataProvider implements FragmentModelProvider<CustomerProfileData>
{
	private UserService userService;
	private Converter<CustomerModel, CustomerProfileData> customerProfileDataConverter;

	@Override
	public CustomerProfileData getModel(final Map parameters)
	{

		final CustomerModel userModel = (CustomerModel) getUserService().getCurrentUser();

		return getCustomerProfileDataConverter().convert(userModel);
	}

	protected UserService getUserService()
	{
		return userService;
	}

	public void setUserService(final UserService userService)
	{
		this.userService = userService;
	}

	protected Converter<CustomerModel, CustomerProfileData> getCustomerProfileDataConverter()
	{
		return customerProfileDataConverter;
	}

	public void setCustomerProfileDataConverter(final Converter<CustomerModel, CustomerProfileData> customerProfileDataConverter)
	{
		this.customerProfileDataConverter = customerProfileDataConverter;
	}
}
