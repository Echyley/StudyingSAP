/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.services.strategies.lifecycle.impl;

import de.hybris.platform.sap.productconfig.services.SessionAccessService;

import org.springframework.beans.factory.annotation.Required;


/**
 * Base class for {@link SessionAccessService} aware lifecycle starategies
 */
public abstract class SessionServiceAware
{

	private SessionAccessService sessionAccessService;

	protected SessionAccessService getSessionAccessService()
	{
		return sessionAccessService;
	}

	@Required
	public void setSessionAccessService(final SessionAccessService sessionAccessService)
	{
		this.sessionAccessService = sessionAccessService;
	}

}
