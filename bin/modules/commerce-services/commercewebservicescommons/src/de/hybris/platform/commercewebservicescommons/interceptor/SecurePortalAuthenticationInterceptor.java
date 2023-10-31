/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.commercewebservicescommons.interceptor;

import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.commercewebservicescommons.annotation.SecurePortalUnauthenticatedAccess;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.site.BaseSiteService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import static de.hybris.platform.commerceservices.enums.CustomerType.GUEST;


/**
 * Default interceptor to run before controllers' execution to check if the current user is authenticated
 * to access some secure portals when the requiresAuthentication enable on current BaseSite.
 */
public class SecurePortalAuthenticationInterceptor implements HandlerInterceptor
{
	private UserService userService;
	private BaseSiteService baseSiteService;

	public SecurePortalAuthenticationInterceptor(final BaseSiteService baseSiteService, final UserService userService)
	{
		this.baseSiteService = baseSiteService;
		this.userService = userService;
	}

	/**
	 * Adds the base site and user to the session. When the current base site enable the requiresAuthentication flag
	 * the interceptor will check if the secure portal has @SecurePortalUnauthenticatedAccess annotation, if not, then
	 * for unauthenticated user we return an error in the HTTP response using the status code 404.
	 */
	@Override
	public boolean preHandle(final HttpServletRequest request, final HttpServletResponse response, final Object handler)
			throws Exception
	{
		if (!isSiteRequireAuthentication())
		{
			return true;
		}
		if (isAllowUnauthenticatedAccess(handler))
		{
			return true;
		}
		if (!isUnAuthenticatedUser())
		{
			return true;
		}

		response.sendError(HttpStatus.NOT_FOUND.value());
		return false;
	}

	protected boolean isSiteRequireAuthentication()
	{
		final BaseSiteModel site = baseSiteService.getCurrentBaseSite();
		return site != null && site.isRequiresAuthentication();
	}

	protected boolean isUnAuthenticatedUser()
	{
		UserModel currentUser = userService.getCurrentUser();
		return currentUser == null || isAnonymousUser(currentUser) || isGuestUser(currentUser);
	}

	protected boolean isAllowUnauthenticatedAccess(final Object handler)
	{
		final HandlerMethod handlerMethod = (HandlerMethod) handler;
		final SecurePortalUnauthenticatedAccess annotation = handlerMethod.getMethodAnnotation(SecurePortalUnauthenticatedAccess.class);
		return annotation != null;
	}

	protected boolean isAnonymousUser(UserModel user)
	{
		return userService.isAnonymousUser(user);
	}

	protected boolean isGuestUser(UserModel user)
	{
		if (user instanceof CustomerModel)
		{
			return GUEST.equals(((CustomerModel) user).getType());
		}
		return false;
	}

}
