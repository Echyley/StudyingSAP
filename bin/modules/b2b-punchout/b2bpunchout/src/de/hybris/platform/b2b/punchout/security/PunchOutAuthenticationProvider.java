/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.b2b.punchout.security;

import de.hybris.platform.order.CartService;
import de.hybris.platform.servicelayer.user.UserService;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;


public class PunchOutAuthenticationProvider extends PunchOutCoreAuthenticationProvider
{
	private static final String NONE_PROVIDED = "NONE_PROVIDED";

	private UserService userService;
	private CartService cartService;

	@Override
	public Authentication authenticate(final Authentication authentication) throws AuthenticationException
	{
		final String username = (authentication.getPrincipal() == null) ? NONE_PROVIDED : authentication.getName();

		// check if the user of the cart matches the current user and if the
		// user is not anonymous. If otherwise, delete the session cart as it might
		// be stolen / from another user
		final String sessionCartUserId = getCartService().getSessionCart().getUser().getUid();

		if (!username.equals(sessionCartUserId) && !sessionCartUserId.equals(getUserService().getAnonymousUser().getUid()))
		{
			getCartService().setSessionCart(null);
		}

		return super.authenticate(authentication);
	}

	protected UserService getUserService()
	{
		return userService;
	}

	public void setUserService(final UserService userService)
	{
		this.userService = userService;
	}

	protected CartService getCartService()
	{
		return cartService;
	}

	public void setCartService(final CartService cartService)
	{
		this.cartService = cartService;
	}
}
