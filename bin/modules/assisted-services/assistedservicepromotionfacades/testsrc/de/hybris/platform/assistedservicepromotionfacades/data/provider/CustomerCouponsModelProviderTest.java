/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.assistedservicepromotionfacades.data.provider;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.customercouponfacades.customercoupon.data.CustomerCouponData;
import de.hybris.platform.customercouponfacades.impl.DefaultCustomerCouponFacade;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class CustomerCouponsModelProviderTest
{
	@InjectMocks
	CustomerCouponsModelProvider customerCouponsModelProviderUnderTest;

	@Mock
	private DefaultCustomerCouponFacade customerCouponFacade;

	@Test
	public void testGetModelForAssignableCustomerCoupons()
	{
		// Setup
		List<CustomerCouponData> assignableCustomerCoupons = new ArrayList<>();
		final CustomerCouponData customerCouponData = new CustomerCouponData();
		customerCouponData.setCouponId("dragonboat");
		assignableCustomerCoupons.add(customerCouponData);
		given(customerCouponFacade.getAssignableCustomerCoupons(any())).willReturn(assignableCustomerCoupons);
		final Map<String, String> parameters = Map.ofEntries(Map.entry("assignable", "true"));

		// Run the test
		final List<CustomerCouponData> result = customerCouponsModelProviderUnderTest.getModel(parameters);

		// Verify the results
		Assert.assertEquals(assignableCustomerCoupons, result);
		Assert.assertNotNull("Assignable customer coupon is not null", result);
		Assert.assertEquals("dragonboat", result.get(0).getCouponId());
	}

	@Test
	public void testGetCustomerForAssignedCustomerCoupons()
	{
		// Setup
		List<CustomerCouponData> assignedCustomerCoupons = new ArrayList<>();
		final CustomerCouponData customerCouponData = new CustomerCouponData();
		customerCouponData.setCouponId("midautumn");
		assignedCustomerCoupons.add(customerCouponData);
		given(customerCouponFacade.getAssignedCustomerCoupons(any())).willReturn(assignedCustomerCoupons);
		final Map<String, String> parameters = Map.ofEntries(Map.entry("assignable", "false"));

		// Run the test
		final List<CustomerCouponData> result = customerCouponsModelProviderUnderTest.getModel(parameters);

		// Verify the results
		Assert.assertNotNull("Assigned customer coupon is not null", result);
		Assert.assertEquals(assignedCustomerCoupons, result);
		Assert.assertEquals("midautumn", result.get(0).getCouponId());
	}
}
