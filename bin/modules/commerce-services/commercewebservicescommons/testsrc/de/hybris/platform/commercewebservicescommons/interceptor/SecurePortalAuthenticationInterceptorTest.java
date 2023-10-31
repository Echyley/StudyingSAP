/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.commercewebservicescommons.interceptor;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.commerceservices.enums.CustomerType;
import de.hybris.platform.commercewebservicescommons.annotation.SecurePortalUnauthenticatedAccess;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.site.BaseSiteService;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import org.springframework.web.method.HandlerMethod;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class SecurePortalAuthenticationInterceptorTest
{

	@Mock
	private BaseSiteService baseSiteService;
	@Mock
	private UserService userService;
	@Mock
	private HandlerMethod handlerMethod;
	@Mock
	private HttpServletRequest httpServletRequest;
	@Mock
	private HttpServletResponse httpServletResponse;

	private SecurePortalAuthenticationInterceptor securePortalAuthenticationInterceptor;

	@Before
	public void setUp()
	{
		securePortalAuthenticationInterceptor = new SecurePortalAuthenticationInterceptor(baseSiteService, userService);
	}

	@Test
	public void testAuthenticatedUserAccessSuccessWhenBaseSiteRequireAuthentication() throws Exception
	{
		final BaseSiteModel currentBaseSite = mock(BaseSiteModel.class);
		given(currentBaseSite.isRequiresAuthentication()).willReturn(true);
		given(baseSiteService.getCurrentBaseSite()).willReturn(currentBaseSite);
		CustomerModel user = mock(CustomerModel.class);
		user.setType(CustomerType.REGISTERED);
		given(userService.getCurrentUser()).willReturn(user);
		given(userService.isAnonymousUser(user)).willReturn(false);

		assertTrue(securePortalAuthenticationInterceptor.preHandle(httpServletRequest, httpServletResponse, handlerMethod));
	}


	@Test
	public void testUnAuthenticatedUserAccessFailedWhenBaseSiteRequireAuthentication() throws Exception
	{
		final BaseSiteModel currentBaseSite = mock(BaseSiteModel.class);
		given(currentBaseSite.isRequiresAuthentication()).willReturn(true);
		given(baseSiteService.getCurrentBaseSite()).willReturn(currentBaseSite);

		UserModel user = mock(UserModel.class);
		given(userService.getCurrentUser()).willReturn(user);
		given(userService.isAnonymousUser(user)).willReturn(true);

		assertFalse(securePortalAuthenticationInterceptor.preHandle(httpServletRequest, httpServletResponse, handlerMethod));
	}


	@Test
	public void testUnAuthenticatedUserAccessUnsecurePortalSuccessWhenBaseSiteRequireAuthentication() throws Exception
	{
		final BaseSiteModel currentBaseSite = mock(BaseSiteModel.class);
		given(currentBaseSite.isRequiresAuthentication()).willReturn(true);
		given(baseSiteService.getCurrentBaseSite()).willReturn(currentBaseSite);

		final SecurePortalUnauthenticatedAccess annotation = mock(SecurePortalUnauthenticatedAccess.class);
		given(handlerMethod.getMethodAnnotation(any())).willReturn(annotation);

		assertTrue(securePortalAuthenticationInterceptor.preHandle(httpServletRequest, httpServletResponse, handlerMethod));
	}

	@Test
	public void testAccessSecurePortalSuccessWhenBaseSiteNotRequireAuthentication() throws Exception
	{
		final BaseSiteModel currentBaseSite = mock(BaseSiteModel.class);
		given(currentBaseSite.isRequiresAuthentication()).willReturn(false);
		given(baseSiteService.getCurrentBaseSite()).willReturn(currentBaseSite);

		assertTrue(securePortalAuthenticationInterceptor.preHandle(httpServletRequest, httpServletResponse, handlerMethod));
	}


	@Test
	public void testUnAuthenticatedUserAccessSecurePortalFailedWhenBaseSiteRequireAuthentication() throws Exception
	{
		final BaseSiteModel currentBaseSite = mock(BaseSiteModel.class);
		given(currentBaseSite.isRequiresAuthentication()).willReturn(true);
		given(baseSiteService.getCurrentBaseSite()).willReturn(currentBaseSite);

		given(handlerMethod.getMethodAnnotation(any())).willReturn(null);

		assertFalse(securePortalAuthenticationInterceptor.preHandle(httpServletRequest, httpServletResponse, handlerMethod));
	}


	@Test
	public void testSiteIsRequireAuthentication()
	{
		final BaseSiteModel currentBaseSite = mock(BaseSiteModel.class);
		given(currentBaseSite.isRequiresAuthentication()).willReturn(true);
		given(baseSiteService.getCurrentBaseSite()).willReturn(currentBaseSite);

		assertTrue(securePortalAuthenticationInterceptor.isSiteRequireAuthentication());
	}

	@Test
	public void testSiteNotRequireAuthentication()
	{
		final BaseSiteModel currentBaseSite = mock(BaseSiteModel.class);
		given(currentBaseSite.isRequiresAuthentication()).willReturn(false);
		given(baseSiteService.getCurrentBaseSite()).willReturn(currentBaseSite);

		assertFalse(securePortalAuthenticationInterceptor.isSiteRequireAuthentication());
	}

	@Test
	public void testCurrentBaseSiteIsNull()
	{
		given(baseSiteService.getCurrentBaseSite()).willReturn(null);

		assertFalse(securePortalAuthenticationInterceptor.isSiteRequireAuthentication());
	}

	@Test
	public void testPortalAllowUnauthenticatedAccess()
	{
		final SecurePortalUnauthenticatedAccess annotation = mock(SecurePortalUnauthenticatedAccess.class);
		given(handlerMethod.getMethodAnnotation(any())).willReturn(annotation);

		assertTrue(securePortalAuthenticationInterceptor.isAllowUnauthenticatedAccess(handlerMethod));
	}

	@Test
	public void testPortalNotAllowUnauthenticatedAccess()
	{
		given(handlerMethod.getMethodAnnotation(any())).willReturn(null);

		assertFalse(securePortalAuthenticationInterceptor.isAllowUnauthenticatedAccess(handlerMethod));
	}

	@Test
	public void testIsAuthenticatedUser()
	{
		CustomerModel user = mock(CustomerModel.class);
		user.setType(CustomerType.REGISTERED);
		given(userService.getCurrentUser()).willReturn(user);
		given(userService.isAnonymousUser(user)).willReturn(false);

		assertFalse(securePortalAuthenticationInterceptor.isUnAuthenticatedUser());
	}

	@Test
	public void testIsUnAuthenticatedUserWhenCurrentUserIsAnonymous()
	{
		UserModel user = mock(UserModel.class);
		given(userService.getCurrentUser()).willReturn(user);
		given(userService.isAnonymousUser(user)).willReturn(true);

		assertTrue(securePortalAuthenticationInterceptor.isUnAuthenticatedUser());
	}

	@Test
	public void testIsUnAuthenticatedUserWhenCurrentUserIsGuest()
	{
		CustomerModel user = mock(CustomerModel.class);
		given(user.getType()).willReturn(CustomerType.GUEST);
		given(userService.getCurrentUser()).willReturn(user);
		given(userService.isAnonymousUser(user)).willReturn(false);

		assertTrue(securePortalAuthenticationInterceptor.isUnAuthenticatedUser());
	}

	@Test
	public void testIsGuestUser()
	{
		CustomerModel user = mock(CustomerModel.class);
		given(user.getType()).willReturn(CustomerType.GUEST);

		assertTrue(securePortalAuthenticationInterceptor.isGuestUser(user));
	}

	@Test
	public void testIsNotGuestUser()
	{
		CustomerModel user = mock(CustomerModel.class);
		given(user.getType()).willReturn(CustomerType.REGISTERED);

		assertFalse(securePortalAuthenticationInterceptor.isGuestUser(user));
	}

}
