/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.personalizationyprofile.strategy.impl;

import static de.hybris.platform.personalizationyprofile.constants.PersonalizationyprofileConstants.IDENTITY_ORIGIN_USER_ACCOUNT;
import static de.hybris.platform.personalizationyprofile.constants.PersonalizationyprofileConstants.IDENTITY_TYPE_EMAIL;

import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.personalizationyprofile.strategy.CxProfileIdentifierStrategy;
import de.hybris.platform.personalizationyprofile.yaas.ProfileReference;
import de.hybris.platform.personalizationyprofile.yaas.client.CxIdentityServiceClient;
import de.hybris.platform.servicelayer.exceptions.SystemException;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.servicelayer.user.UserService;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

import com.hybris.charon.exp.HttpException;
import com.hybris.charon.exp.NotFoundException;
import com.hybris.yprofile.consent.services.ConsentService;



public class SimpleCxProfileIdentifierStrategy implements CxProfileIdentifierStrategy
{
	protected static final String PROFILE_ID_SESSSION_ATTR_KEY = "profileIdentifier";
	private static final Logger LOG = LoggerFactory.getLogger(SimpleCxProfileIdentifierStrategy.class.getName());
	private SessionService sessionService;
	private UserService userService;
	private CxIdentityServiceClient cxIdentityServiceClient;
	private List<String> pauseConsentReferenceUseParameters;
	private ConsentService consentService;

	@Override
	public String getProfileIdentifier(final UserModel user)
	{
		if (canUseConsentReference())
		{
			return getProfileIdentifierFromConsentReference();
		}

		String profileId = getProfileIdentifierFromSession(user);
		if (profileId != null)
		{
			return profileId;
		}

		profileId = findProfileIdentifier(user);
		if (profileId != null)
		{
			storeProfileIdentifierInSession(user, profileId);
			return profileId;
		}

		return profileId;

	}

	protected String getProfileIdentifierFromSession(final UserModel user)
	{
		return sessionService.getAttribute(getSessionAttributeKey(user));
	}

	protected String getProfileIdentifierFromConsentReference()
	{
		return consentService.getConsentReferenceFromSession();
	}

	protected void storeProfileIdentifierInSession(final UserModel user, final String profileId)
	{
		if (user instanceof CustomerModel)
		{
			sessionService.setAttribute(getSessionAttributeKey(user), profileId);
		}
	}

	protected String getSessionAttributeKey(final UserModel user)
	{
		return PROFILE_ID_SESSSION_ATTR_KEY + user.getUid();
	}

	protected String findProfileIdentifier(final UserModel user)
	{
		if (user instanceof CustomerModel)
		{
			if (userService.isAnonymousUser(user))
			{
				return null;
			}

			return findProfileIdentifierForRegisteredUser(user);
		}
		return null;
	}

	private boolean canUseConsentReference()
	{
		final boolean noneMatch = pauseConsentReferenceUseParameters.stream()
				.noneMatch(parameter -> Boolean.TRUE.equals(sessionService.getAttribute(parameter)));
		return noneMatch;
	}

	protected String findProfileIdentifierForRegisteredUser(final UserModel user)
	{
		final String profileId = readProfileIdentifier(encodeUserId(user.getUid()), IDENTITY_TYPE_EMAIL,
				IDENTITY_ORIGIN_USER_ACCOUNT);

		return profileId;
	}

	protected String encodeUserId(final String userId)
	{
		return URLEncoder.encode(userId, StandardCharsets.UTF_8);
	}

	/**
	 * Read profile identifier from identity service
	 */
	private String readProfileIdentifier(final String identityKey, final String identityType, final String identityOrigin)
	{
		if (StringUtils.isEmpty(identityKey) || StringUtils.isEmpty(identityType) || StringUtils.isEmpty(identityOrigin))
		{
			return null;
		}

		try
		{
			final List<ProfileReference> profileReferences = cxIdentityServiceClient.getProfileReferences(identityKey, identityType,
					identityOrigin);
			if (!profileReferences.isEmpty())
			{
				return profileReferences.get(0).getProfileId();
			}
		}
		catch (final NotFoundException e)
		{
			LOG.debug("Profile identifier not found for identityKey {}, identityType {}, identityOrigin {}", identityKey,
					identityType, identityOrigin, e);
		}
		catch (final HttpException e)
		{
			LOG.warn("Get profile identifier request failed for identityKey {}, identityType {}, identityOrigin {}", identityKey,
					identityType, identityOrigin);
			LOG.debug("Exception", e);
		}
		catch (final SystemException e)
		{
			LOG.warn(
					"Failed to get profile identifier. Check if yaas configuration for CxIdentityServiceClient is properly defined. Error message : {} ",
					e.getMessage());
			LOG.debug("Exception", e);
		}
		catch (final Exception e)
		{
			LOG.warn("Failed to get profile identifier. Error message : {} ", e.getMessage());
			LOG.debug("Exception : ", e);
		}
		return null;
	}

	/**
	 * Cleans profile identifier stored in session
	 */
	public void resetProfileIdentifier(final UserModel user)
	{
		sessionService.removeAttribute(getSessionAttributeKey(user));
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

	protected UserService getUserService()
	{
		return userService;
	}

	@Required
	public void setUserService(final UserService userService)
	{
		this.userService = userService;
	}

	public CxIdentityServiceClient getCxIdentityServiceClient()
	{
		return cxIdentityServiceClient;
	}

	@Required
	public void setCxIdentityServiceClient(final CxIdentityServiceClient cxIdentityServiceClient)
	{
		this.cxIdentityServiceClient = cxIdentityServiceClient;
	}

	protected List<String> getPauseConsentReferenceUseParameters()
	{
		return pauseConsentReferenceUseParameters;
	}

	@Required
	public void setPauseConsentReferenceUseParameters(final List<String> pauseConsentReferenceUseParameters)
	{
		this.pauseConsentReferenceUseParameters = pauseConsentReferenceUseParameters;
	}

	@Required
	protected ConsentService getConsentService()
	{
		return consentService;
	}

	public void setConsentService(final ConsentService consentService)
	{
		this.consentService = consentService;
	}
}
