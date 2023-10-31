/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.commerceservices.strategies.impl.user;

import static org.mockito.BDDMockito.given;

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
import org.mockito.junit.MockitoJUnitRunner;


/**
 * JUnit test suite for {@link AlwaysNegativeUserPropertyMatchingStrategy}
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class AlwaysNegativeUserPropertyMatchingStrategyTest
{
	private static final String CUSTOMER_ID = "6a2a41a3-c54c-4ce8-a2d2-0324e1c32a23";
	private AlwaysNegativeUserPropertyMatchingStrategy strategy;
	@Mock
	private CustomerModel customer;
	@Mock(lenient = true)
	private CustomerService customerService;

	@Before
	public void setUp()
	{
		strategy = new AlwaysNegativeUserPropertyMatchingStrategy();
	}

	@Test
	public void getCustomerByPropertyShouldReturnEmptyOptional()
	{
		given(customerService.getCustomerByCustomerId(CUSTOMER_ID)).willReturn(customer);

		final Optional<CustomerModel> userOptional = strategy.getUserByProperty(CUSTOMER_ID, CustomerModel.class);
		Assert.assertTrue(userOptional.isEmpty());
	}

	@Test
	public void getUserByPropertyShouldReturnEmptyOptional()
	{
		given(customerService.getCustomerByCustomerId(CUSTOMER_ID)).willReturn(customer);

		final Optional<UserModel> userOptional = strategy.getUserByProperty(CUSTOMER_ID, UserModel.class);
		Assert.assertTrue(userOptional.isEmpty());
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldThrowExceptionIfNullProperty()
	{
		strategy.getUserByProperty(null, UserModel.class);
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldThrowExceptionIfNullClazz()
	{
		strategy.getUserByProperty(CUSTOMER_ID, null);
	}
	
}
