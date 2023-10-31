/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.assistedservicefacades.customer360.providers;

import de.hybris.platform.assistedservicefacades.customer360.FragmentModelProvider;
import de.hybris.platform.assistedservicefacades.customer360.CustomerOverviewData;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.user.UserService;

import java.util.Map;

public class CustomerOverviewDataProvider implements FragmentModelProvider<CustomerOverviewData>
{
	private UserService userService;
	private Converter<CustomerModel, CustomerOverviewData> customerOverviewDataConverter;

	@Override
	public CustomerOverviewData getModel(final Map parameters)
	{
		final CustomerModel currentUser = (CustomerModel) getUserService().getCurrentUser();

		return getCustomerOverviewDataConverter().convert(currentUser);
	}

	protected UserService getUserService()
	{
		return userService;
	}

	public void setUserService(final UserService userService)
	{
		this.userService = userService;
	}

	protected Converter<CustomerModel, CustomerOverviewData> getCustomerOverviewDataConverter()
	{
		return customerOverviewDataConverter;
	}

	public void setCustomerOverviewDataConverter(
			final Converter<CustomerModel, CustomerOverviewData> customerOverviewDataConverter)
	{
		this.customerOverviewDataConverter = customerOverviewDataConverter;
	}
}
