/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.hybris.sapcustomerb2c.strategy;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commerceservices.customer.CustomerService;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.core.model.user.UserModel;

import java.util.Optional;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

/**
 * JUnit test suite for {@link SapCustomerIdMatchingStrategy}
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class SapCustomerIdMatchingStrategyTest
{
	private static final String SAP_CUSTOMER_ID = "0000000158";
	private static final String CUSTOMER_ID = "6a2a41a3-c54c-4ce8-a2d2-0324e1c32a23";

	private SapCustomerIdMatchingStrategy strategy;
	@Mock
	private CustomerService customerService;
	@Mock
	private CustomerModel customer;

	@Before
	public void setUp()
	{
		strategy = new SapCustomerIdMatchingStrategy(customerService);
	}

	@Test
	public void getUserByPropertyForCustomerModel()
	{
		given(customer.getCustomerID()).willReturn(SAP_CUSTOMER_ID);
		given(customerService.getCustomerByCustomerId(SAP_CUSTOMER_ID)).willReturn(customer);

		final Optional<CustomerModel> userOptional = strategy.getUserByProperty(SAP_CUSTOMER_ID, CustomerModel.class);

		verify(customerService).getCustomerByCustomerId(SAP_CUSTOMER_ID);
		Assert.assertNotNull(userOptional);
		Assert.assertTrue(userOptional.isPresent());
		Assert.assertEquals(SAP_CUSTOMER_ID, userOptional.get().getCustomerID());
	}

	@Test
	public void getUserByPropertyForUserModel()
	{
		given(customer.getUid()).willReturn(SAP_CUSTOMER_ID);
		given(customerService.getCustomerByCustomerId(SAP_CUSTOMER_ID)).willReturn(customer);

		final Optional<UserModel> userOptional = strategy.getUserByProperty(SAP_CUSTOMER_ID, UserModel.class);

		verify(customerService).getCustomerByCustomerId(SAP_CUSTOMER_ID);
		Assert.assertNotNull(userOptional);
		Assert.assertTrue(userOptional.isPresent());
		Assert.assertEquals(SAP_CUSTOMER_ID, userOptional.get().getUid());
	}

	@Test
	public void getUserByPropertyForNonMatchingPattern()
	{
		Mockito.lenient().when(customer.getCustomerID()).thenReturn(CUSTOMER_ID);
		Mockito.lenient().when(customerService.getCustomerByCustomerId(CUSTOMER_ID)).thenReturn(customer);

		final Optional<CustomerModel> userOptional = strategy.getUserByProperty(CUSTOMER_ID, CustomerModel.class);

		Assert.assertNotNull(userOptional);
		Assert.assertTrue(userOptional.isEmpty());
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldThrowExceptionIfNullProperty()
	{
		strategy.getUserByProperty(null, CustomerModel.class);
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldThrowExceptionIfNullClazz()
	{
		strategy.getUserByProperty(SAP_CUSTOMER_ID, null);
	}

}
