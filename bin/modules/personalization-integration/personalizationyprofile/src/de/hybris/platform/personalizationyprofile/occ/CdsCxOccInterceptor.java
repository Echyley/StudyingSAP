/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.personalizationyprofile.occ;

import static com.hybris.yprofile.constants.ProfileservicesConstants.PROFILE_CONSENT_GIVEN;

import de.hybris.platform.personalizationservices.occ.impl.AbstractCxOccInterceptor;
import de.hybris.platform.personalizationservices.voters.Vote;
import de.hybris.platform.servicelayer.session.SessionService;

import java.util.Optional;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Required;

import com.hybris.yprofile.common.Utils;
import com.hybris.yprofile.consent.services.ConsentService;


/**
 * Interceptor for personalization process called in Commerce Web Services (OCC) which set attributes needed by CDS
 *
 * @deprecated Since 2005 This functionality is already covered by
 *             com.hybris.yprofile.consent.filters.OCCConsentLayerFilter
 */
@Deprecated(since = "2005", forRemoval = true)
public class CdsCxOccInterceptor extends AbstractCxOccInterceptor
{
	public static final int PRECEDENCE = 20;

	private boolean enabled = false;
	private SessionService sessionService;
	private ConsentService consentService;

	public CdsCxOccInterceptor()
	{
		super(PRECEDENCE);
	}

	@Override
	public void beforeVoteExecution(final HttpServletRequest request, final Vote vote)
	{
		if (isEnabled())
		{
			sessionService.setAttribute(PROFILE_CONSENT_GIVEN, getConsentGivenValue(request));
			consentService.saveConsentReferenceInSessionAndConsentModel(request);
		}
	}

	protected boolean getConsentGivenValue(final HttpServletRequest request)
	{
		final Optional<Cookie> cookie = Utils.getCookie(request, PROFILE_CONSENT_GIVEN);
		final boolean consentGiven = cookie.map(Cookie::getValue)//
				.map(Boolean::valueOf)//
				.orElse(false);

		return consentGiven;
	}

	public boolean isEnabled()
	{
		return enabled;
	}

	public void setEnabled(final boolean enabled)
	{
		this.enabled = enabled;
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

	protected ConsentService getConsentService()
	{
		return consentService;
	}

	@Required
	public void setConsentService(final ConsentService consentService)
	{
		this.consentService = consentService;
	}
}
