/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.b2b.punchout.security;

import de.hybris.platform.core.Registry;
import de.hybris.platform.jalo.JaloConnection;
import de.hybris.platform.jalo.JaloSession;
import de.hybris.platform.jalo.user.User;
import de.hybris.platform.jalo.user.UserManager;
import de.hybris.platform.spring.security.CoreAuthenticationProvider;
import de.hybris.platform.spring.security.CoreUserDetails;

import java.util.Collections;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public class PunchOutCoreAuthenticationProvider extends CoreAuthenticationProvider
{

	@Override
	public Authentication authenticate(final Authentication authentication)
	{
		if (Registry.hasCurrentTenant() && JaloConnection.getInstance().isSystemInitialized())
		{
			final String username = (authentication.getPrincipal() == null) ? "NONE_PROVIDED" : authentication.getName();

			UserDetails userDetails = null;

			try
			{
				userDetails = retrieveUser(username);
			}
			catch (final UsernameNotFoundException notFound)
			{
				throw new BadCredentialsException(
						messages.getMessage("CoreAuthenticationProvider.badCredentials", "Bad credentials"), notFound);
			}

			getPreAuthenticationChecks().check(userDetails);

			final User user = UserManager.getInstance().getUserByLogin(userDetails.getUsername());

			additionalAuthenticationChecks(userDetails, (AbstractAuthenticationToken) authentication);

			// finally, set user in session
			JaloSession.getCurrentSession().setUser(user);
			return createSuccessAuthentication(authentication, userDetails);
		}
		else
		{
			return createSuccessAuthentication(
					authentication,
					new CoreUserDetails("systemNotInitialized", "systemNotInitialized", true, false, true, true,
							Collections.emptyList(), null));
		}
	}
}
