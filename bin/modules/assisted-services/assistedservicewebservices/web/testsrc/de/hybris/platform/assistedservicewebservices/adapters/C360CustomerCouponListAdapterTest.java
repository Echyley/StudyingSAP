/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.assistedservicewebservices.adapters;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.assistedservicewebservices.dto.C360CustomerCouponList;
import de.hybris.platform.assistedservicewebservices.dto.C360CustomerCouponWsDTO;
import de.hybris.platform.customercouponfacades.customercoupon.data.CustomerCouponData;
import de.hybris.platform.webservicescommons.mapping.DataMapper;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
@UnitTest
public class C360CustomerCouponListAdapterTest
{

	@Mock
	private DataMapper mockDataMapper;

	@InjectMocks
	private C360CustomerCouponListAdapter c360CustomerCouponListAdapterUnderTest;

	@Test
	public void testAdapt()
	{
		// Setup
		final CustomerCouponData customerCouponData = new CustomerCouponData();
		final List<CustomerCouponData> data = List.of(customerCouponData);

		// Configure DataMapper.map(...).
		final C360CustomerCouponWsDTO c360CustomerCouponWsDTO = new C360CustomerCouponWsDTO();
		when(mockDataMapper.map(any(CustomerCouponData.class), eq(C360CustomerCouponWsDTO.class)))
				.thenReturn(c360CustomerCouponWsDTO);

		// Run the test
		final C360CustomerCouponList result = (C360CustomerCouponList) c360CustomerCouponListAdapterUnderTest.adapt(data);

		// Verify the results
		verify(mockDataMapper).map(customerCouponData, C360CustomerCouponWsDTO.class);
		Assert.assertTrue("C360CustomerCouponList cannot be empty" ,result.getCustomerCoupons().size() > 0);
	}

	@Test
	public void testC360CustomerCouponListAdapterWithNull()
	{
		final C360CustomerCouponList result = (C360CustomerCouponList) c360CustomerCouponListAdapterUnderTest.adapt(null);
		assertThat(result).isNotNull();
		assertThat(result.getCustomerCoupons()).isEmpty();
	}
}
