/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.commerceservices.order.strategies.impl;

import static org.junit.Assert.assertEquals;
import static org.mockito.BDDMockito.given;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.user.UserService;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;


/**
 * Unit test for DefaultQuoteUserIdentificationStrategy
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultQuoteUserIdentificationStrategyTest
{
	private DefaultQuoteUserIdentificationStrategy quoteUserIdentificationStrategy;
	@Mock
	private UserService userService;
	private UserModel user;


	@Before
	public void setUp()
	{
		quoteUserIdentificationStrategy = new DefaultQuoteUserIdentificationStrategy();
		quoteUserIdentificationStrategy.setUserService(userService);
		user = new UserModel();
	}

	@Test
	public void shouldGetCurrentQuoteUser()
	{
		given(userService.getCurrentUser()).willReturn(user);
		assertEquals("Quote user is wrong", user, quoteUserIdentificationStrategy.getCurrentQuoteUser());
	}
}
