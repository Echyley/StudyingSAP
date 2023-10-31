/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.commerceservices.multisite.listeners;

import de.hybris.platform.jalo.SessionContext;
import de.hybris.platform.servicelayer.event.events.AfterSessionCreationEvent;


/**
 * This listener sets up the sites of the currently logged-in user in the {@link SessionContext}.
 */
public class BaseSiteAfterSessionCreationListener extends AbstractBaseSiteAfterSessionEventListener<AfterSessionCreationEvent>
{
	@Override
	protected void onEvent(final AfterSessionCreationEvent event)
	{
		getMultiSiteUserSessionService().setBaseSitesAttributeInSession();
	}
}
