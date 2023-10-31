/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.commerceservices.multisite.listeners;

import de.hybris.platform.jalo.SessionContext;
import de.hybris.platform.servicelayer.event.events.AfterSessionUserChangeEvent;


/**
 * This listener sets up the site of the currently logged-in user in the {@link SessionContext}.
 */
public class BaseSiteAfterSessionUserChangeListener extends AbstractBaseSiteAfterSessionEventListener<AfterSessionUserChangeEvent>
{
	@Override
	protected void onEvent(final AfterSessionUserChangeEvent event)
	{
		getMultiSiteUserSessionService().setBaseSitesAttributeInSession();
	}

}
