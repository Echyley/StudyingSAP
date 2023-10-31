/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.assistedservicewebservices.filters;

import de.hybris.platform.commercewebservicescommons.errors.exceptions.CartException;
import de.hybris.platform.commercewebservicescommons.strategies.CartLoaderStrategy;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.user.UserService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Filter that puts current user's cart into the session.
 */
public class CartMatchingFilter extends OncePerRequestFilter
{
	private static final Logger LOG = LoggerFactory.getLogger(CartMatchingFilter.class);

	private String regexp;
	private CartLoaderStrategy cartLoaderStrategy;
	private UserService userService;

	@Override
	protected void doFilterInternal(final HttpServletRequest request, final HttpServletResponse response,
			final FilterChain filterChain) throws ServletException, IOException
	{
		final UserModel currentUser = userService.getCurrentUser();
		// only customers can own carts
		if (matchesUrl(request, regexp) && currentUser != null && CustomerModel.class.isAssignableFrom(currentUser.getClass()))
		{
			try {
				cartLoaderStrategy.loadCart("current", true);
			}
			catch (CartException e)
			{
				LOG.warn("asm web service load cart to current session failed",e);
			}
		}

		filterChain.doFilter(request, response);
	}


	protected boolean matchesUrl(final HttpServletRequest request, final String regexp)
	{
		final Matcher matcher = getMatcher(request, regexp);
		return matcher.find();
	}

	protected Matcher getMatcher(final HttpServletRequest request, final String regexp)
	{
		final Pattern pattern = Pattern.compile(regexp);
		final String path = getPath(request);
		return pattern.matcher(path);
	}

	protected String getPath(final HttpServletRequest request)
	{
		return StringUtils.defaultString(request.getServletPath());
	}


	public String getRegexp()
	{
		return regexp;
	}

	public void setRegexp(String regexp)
	{
		this.regexp = regexp;
	}

	public CartLoaderStrategy getCartLoaderStrategy()
	{
		return cartLoaderStrategy;
	}

	public void setCartLoaderStrategy(final CartLoaderStrategy cartLoaderStrategy)
	{
		this.cartLoaderStrategy = cartLoaderStrategy;
	}

	public UserService getUserService()
	{
		return userService;
	}

	public void setUserService(UserService userService)
	{
		this.userService = userService;
	}
}
