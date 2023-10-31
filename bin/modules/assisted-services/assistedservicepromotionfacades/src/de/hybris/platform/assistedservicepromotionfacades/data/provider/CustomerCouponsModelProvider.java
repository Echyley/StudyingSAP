/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.assistedservicepromotionfacades.data.provider;

import de.hybris.platform.assistedservicefacades.customer360.FragmentModelProvider;
import de.hybris.platform.customercouponfacades.CustomerCouponFacade;
import de.hybris.platform.customercouponfacades.customercoupon.data.CustomerCouponData;

import java.util.List;
import java.util.Map;

public class CustomerCouponsModelProvider implements FragmentModelProvider<List<CustomerCouponData>>
{
	private CustomerCouponFacade customerCouponFacade;

	private static final String ASSIGNABLE = "assignable";
	private static final String SEARCH_QUERY = "searchQuery";

	/*
	 * (non-Javadoc)
	 *
	 * @see de.hybris.platform.assistedservicefacades.customer360.FragmentModelProvider#getModel(java.util.Map)
	 */
	@Override
	public List<CustomerCouponData> getModel(final Map<String, String> parameters)
	{
		List<CustomerCouponData> customerCoupons = null;
		if (Boolean.parseBoolean(parameters.get(ASSIGNABLE)))
		{
			customerCoupons = getCustomerCouponFacade().getAssignableCustomerCoupons(parameters.get(SEARCH_QUERY));
		}
		else
		{
			customerCoupons = getCustomerCouponFacade().getAssignedCustomerCoupons(parameters.get(SEARCH_QUERY));
		}

		return customerCoupons;
	}

	protected CustomerCouponFacade getCustomerCouponFacade()
	{
		return customerCouponFacade;
	}

	public void setCustomerCouponFacade(final CustomerCouponFacade customerCouponFacade)
	{
		this.customerCouponFacade = customerCouponFacade;
	}

}
