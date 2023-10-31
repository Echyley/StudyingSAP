/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.assistedservicewebservices.filters;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercewebservicescommons.strategies.CartLoaderStrategy;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.servicelayer.user.UserService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import javax.servlet.DispatcherType;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;


/**
 * Test suite for {@link CartMatchingFilter}
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class CartMatchingFilterTest
{
	static final String DEFAULT_REGEXP = "^/[^/]+/(?:users)/[^/]+/customer360";

	private CartMatchingFilter cartMatchingFilter;
	@Mock
	private HttpServletRequest httpServletRequest;
	@Mock
	private HttpServletResponse httpServletResponse;
	@Mock
	private FilterChain filterChain;
	@Mock
	private CartLoaderStrategy cartLoaderStrategy;

	@Mock
	private UserService userService;

	@Before
	public void setUp()
	{
		cartMatchingFilter = new CartMatchingFilter();
		cartMatchingFilter.setCartLoaderStrategy(cartLoaderStrategy);
		cartMatchingFilter.setUserService(userService);
		cartMatchingFilter.setRegexp(DEFAULT_REGEXP);
		given(httpServletRequest.getDispatcherType()).willReturn(DispatcherType.REQUEST);
		CustomerModel customer = new CustomerModel();
		customer.setCustomerID("demo@customer.com");
		given(userService.getCurrentUser()).willReturn(customer);
		given(httpServletRequest.getDispatcherType()).willReturn(DispatcherType.REQUEST);
	}

	@Test
	public void testCurrentCart() throws ServletException, IOException
	{
		given(httpServletRequest.getServletPath()).willReturn("/path/users/demo@customer.com/customer360");
		cartMatchingFilter.doFilter(httpServletRequest, httpServletResponse, filterChain);
		verify(cartLoaderStrategy, times(1)).loadCart(anyString(), anyBoolean());
	}


	@Test
	public void testPathInvalid() throws ServletException, IOException
	{
		given(httpServletRequest.getServletPath()).willReturn("/path/users/demo@customer.com/address");
		cartMatchingFilter.doFilter(httpServletRequest, httpServletResponse, filterChain);
		verify(cartLoaderStrategy, times(0)).loadCart(anyString(), anyBoolean());
	}
}
