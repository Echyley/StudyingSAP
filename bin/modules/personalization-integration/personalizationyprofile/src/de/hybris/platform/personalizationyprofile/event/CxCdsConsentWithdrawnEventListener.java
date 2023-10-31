/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.personalizationyprofile.event;

import static com.hybris.yprofile.constants.ProfileservicesConstants.PROFILE_CONSENT;

import de.hybris.platform.commerceservices.event.ConsentWithdrawnEvent;
import de.hybris.platform.commerceservices.model.consent.ConsentModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.personalizationyprofile.strategy.impl.DefaultCxProfileIdentifierStrategy;

import org.springframework.context.ApplicationListener;


public class CxCdsConsentWithdrawnEventListener implements ApplicationListener<ConsentWithdrawnEvent>
{
	private DefaultCxProfileIdentifierStrategy defaultCxProfileIdentifierStrategy;

	@Override
	public void onApplicationEvent(final ConsentWithdrawnEvent event)
	{
		if (event == null || event.getConsent() == null)
		{
			return;
		}

		final ConsentModel consent = event.getConsent();
		if (isProfileConsentWithdrawn(consent))
		{
			resetProfileIdentifier(consent.getCustomer());
		}
	}

	protected boolean isProfileConsentWithdrawn(final ConsentModel consent)
	{
		return PROFILE_CONSENT.equals(consent.getConsentTemplate().getId());
	}

	protected void resetProfileIdentifier(final CustomerModel customer)
	{
		defaultCxProfileIdentifierStrategy.resetProfileIdentifier(customer);
	}

	protected DefaultCxProfileIdentifierStrategy getDefaultCxProfileIdentifierStrategy()
	{
		return defaultCxProfileIdentifierStrategy;
	}

	public void setDefaultCxProfileIdentifierStrategy(final DefaultCxProfileIdentifierStrategy defaultCxProfileIdentifierStrategy)
	{
		this.defaultCxProfileIdentifierStrategy = defaultCxProfileIdentifierStrategy;
	}
}
