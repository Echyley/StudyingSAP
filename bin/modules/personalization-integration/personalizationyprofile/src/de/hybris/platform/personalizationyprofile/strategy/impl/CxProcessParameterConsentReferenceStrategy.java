/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.personalizationyprofile.strategy.impl;

import static com.hybris.yprofile.constants.ProfileservicesConstants.PROFILE_CONSENT_GIVEN;
import static de.hybris.platform.personalizationyprofile.constants.PersonalizationyprofileConstants.CONSENT_REFERENCE_SESSION_ATTR_KEY;

import de.hybris.platform.personalizationservices.model.process.CxPersonalizationProcessModel;
import de.hybris.platform.personalizationservices.process.strategies.impl.AbstractCxProcessParameterStrategy;
import de.hybris.platform.servicelayer.session.SessionService;

import org.springframework.beans.factory.annotation.Required;

import com.hybris.yprofile.consent.services.ConsentService;


public class CxProcessParameterConsentReferenceStrategy extends AbstractCxProcessParameterStrategy
{
	private SessionService sessionService;
	private ConsentService consentService;

	@Override
	public void load(final CxPersonalizationProcessModel process)
	{
		consumeProcessParameter(process, CONSENT_REFERENCE_SESSION_ATTR_KEY, this::setProfileId);
		consumeProcessParameter(process, PROFILE_CONSENT_GIVEN, this::setConsentGiven);
	}

	@Override
	public void store(final CxPersonalizationProcessModel process)
	{
		final String profileId = getProfileId();
		if (profileId != null)
		{
			getProcessParameterHelper().setProcessParameter(process, CONSENT_REFERENCE_SESSION_ATTR_KEY, profileId);
			getProcessParameterHelper().setProcessParameter(process, PROFILE_CONSENT_GIVEN,
					sessionService.getAttribute(PROFILE_CONSENT_GIVEN));
		}

	}

	protected void setProfileId(final String profileId)
	{
		consentService.saveConsentReferenceInSession(profileId);
	}

	public String getProfileId()
	{
		return consentService.getConsentReferenceFromSession();
	}

	protected void setConsentGiven(final Boolean value)
	{
		if (value != null)
		{
			sessionService.setAttribute(PROFILE_CONSENT_GIVEN, value);
		}
	}

	protected ConsentService getConsentService()
	{
		return consentService;
	}

	@Required
	public void setConsentService(final ConsentService consentService)
	{
		this.consentService = consentService;
	}

	protected SessionService getSessionService()
	{
		return sessionService;
	}

	@Required
	public void setSessionService(final SessionService sessionService)
	{
		this.sessionService = sessionService;
	}

}
