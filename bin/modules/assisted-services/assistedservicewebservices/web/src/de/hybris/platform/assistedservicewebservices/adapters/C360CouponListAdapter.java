/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.assistedservicewebservices.adapters;

import de.hybris.platform.assistedservicepromotionfacades.customer360.CSACouponData;
import de.hybris.platform.assistedservicewebservices.dto.C360CouponList;
import de.hybris.platform.assistedservicewebservices.dto.C360CouponWsDTO;
import de.hybris.platform.assistedservicewebservices.dto.Customer360DataWsDTO;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class C360CouponListAdapter extends C360FragmentDataAdapter<List<CSACouponData>>
{
    @Override
    public Customer360DataWsDTO adapt(List<CSACouponData> data)
    {
        final C360CouponList c360CouponList = new C360CouponList();
        List<C360CouponWsDTO> couponWsDTOList = Collections.emptyList();
        if (data != null)
        {
            couponWsDTOList = data.stream().map(coupon -> getDataMapper().map(coupon, C360CouponWsDTO.class)).collect(Collectors.toList());
        }
        c360CouponList.setCoupons(couponWsDTOList);
        return c360CouponList;
    }
}
