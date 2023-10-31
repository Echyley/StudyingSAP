/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.commerceservices.multisite.listeners;

import de.hybris.platform.commerceservices.user.MultiSiteUserSessionService;
import de.hybris.platform.servicelayer.event.events.AbstractEvent;
import de.hybris.platform.servicelayer.event.impl.AbstractEventListener;


/**
 * Abstract base class for session listeners to update the session's site on session events.
 */
public abstract class AbstractBaseSiteAfterSessionEventListener<T extends AbstractEvent> extends AbstractEventListener<T>
{
	protected MultiSiteUserSessionService multiSiteUserSessionService;

	protected AbstractBaseSiteAfterSessionEventListener()
	{
	}

	protected MultiSiteUserSessionService getMultiSiteUserSessionService()
	{
		return multiSiteUserSessionService;
	}

	public void setMultiSiteUserSessionService(final MultiSiteUserSessionService multiSiteUserSessionService)
	{
		this.multiSiteUserSessionService = multiSiteUserSessionService;
	}
}
