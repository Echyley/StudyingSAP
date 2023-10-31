/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.commerceservices.multisite.listeners;

import de.hybris.platform.jalo.SessionContext;
import de.hybris.platform.servicelayer.event.events.BeforeSessionCloseEvent;


/**
 * This listener removes the site of the currently logged-in user in the {@link SessionContext}.
 */
public class BaseSiteBeforeSessionCloseListener extends AbstractBaseSiteAfterSessionEventListener<BeforeSessionCloseEvent>
{
	@Override
	protected void onEvent(final BeforeSessionCloseEvent event)
	{
		getMultiSiteUserSessionService().removeBaseSitesAttributeInSession();
	}
}
