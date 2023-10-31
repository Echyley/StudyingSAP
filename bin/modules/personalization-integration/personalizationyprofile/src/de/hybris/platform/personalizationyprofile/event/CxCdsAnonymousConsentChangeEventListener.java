/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.personalizationyprofile.event;

import static com.hybris.yprofile.constants.ProfileservicesConstants.CONSENT_GIVEN;
import static com.hybris.yprofile.constants.ProfileservicesConstants.PROFILE_CONSENT;

import de.hybris.platform.commerceservices.event.AnonymousConsentChangeEvent;
import de.hybris.platform.personalizationyprofile.strategy.impl.DefaultCxProfileIdentifierStrategy;
import de.hybris.platform.servicelayer.user.UserService;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.context.ApplicationListener;


public class CxCdsAnonymousConsentChangeEventListener implements ApplicationListener<AnonymousConsentChangeEvent>
{
	private DefaultCxProfileIdentifierStrategy defaultCxProfileIdentifierStrategy;
	private UserService userService;

	@Override
	public void onApplicationEvent(final AnonymousConsentChangeEvent event)
	{
		if (event != null && isProfileConsentWithdrawn(event))
		{
			resetProfileIdentifier();
		}
	}

	protected boolean isProfileConsentWithdrawn(final AnonymousConsentChangeEvent event)
	{
		return event.getConsentTemplateCode().equals(PROFILE_CONSENT) && !CONSENT_GIVEN.equals(event.getCurrentConsentState());
	}

	protected void resetProfileIdentifier()
	{
		defaultCxProfileIdentifierStrategy.resetProfileIdentifier(userService.getAnonymousUser());
	}

	protected DefaultCxProfileIdentifierStrategy getDefaultCxProfileIdentifierStrategy()
	{
		return defaultCxProfileIdentifierStrategy;
	}

	@Required
	public void setDefaultCxProfileIdentifierStrategy(final DefaultCxProfileIdentifierStrategy defaultCxProfileIdentifierStrategy)
	{
		this.defaultCxProfileIdentifierStrategy = defaultCxProfileIdentifierStrategy;
	}

	protected UserService getUserService()
	{
		return userService;
	}

	@Required
	public void setUserService(final UserService userService)
	{
		this.userService = userService;
	}
}
