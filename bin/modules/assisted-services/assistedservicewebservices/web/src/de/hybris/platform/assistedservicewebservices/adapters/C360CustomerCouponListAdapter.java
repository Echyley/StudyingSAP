/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.assistedservicewebservices.adapters;

import de.hybris.platform.assistedservicewebservices.dto.C360CustomerCouponList;
import de.hybris.platform.assistedservicewebservices.dto.C360CustomerCouponWsDTO;
import de.hybris.platform.assistedservicewebservices.dto.Customer360DataWsDTO;
import de.hybris.platform.customercouponfacades.customercoupon.data.CustomerCouponData;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;


public class C360CustomerCouponListAdapter extends C360FragmentDataAdapter<List<CustomerCouponData>>
{
	@Override
	public Customer360DataWsDTO adapt(List<CustomerCouponData> data)
	{
		final C360CustomerCouponList c360CustomerCouponList = new C360CustomerCouponList();
		List<C360CustomerCouponWsDTO> customerCouponWsDTOList = Collections.emptyList();
		if (data != null)
		{
			customerCouponWsDTOList = data.stream().map(customerCoupon -> getDataMapper().map(customerCoupon, C360CustomerCouponWsDTO.class))
					.collect(Collectors.toList());
		}
		c360CustomerCouponList.setCustomerCoupons(customerCouponWsDTOList);
		return c360CustomerCouponList;
	}
}
