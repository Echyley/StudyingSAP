/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.facades.impl;

import de.hybris.platform.sap.productconfig.facades.SessionAccessFacade;
import de.hybris.platform.sap.productconfig.services.SessionAccessService;
import de.hybris.platform.sap.productconfig.services.strategies.lifecycle.intf.ConfigurationModelCacheStrategy;

import org.springframework.beans.factory.annotation.Required;


/**
 * Default implementation of {@link SessionAccessFacade}
 */
public class SessionAccessFacadeImpl implements SessionAccessFacade
{

	private SessionAccessService sessionAccessService;
	private ConfigurationModelCacheStrategy configModelCacheStrategy;

	@Override
	public <T> T getUiStatusForCartEntry(final String cartEntryKey)
	{
		return sessionAccessService.getUiStatusForCartEntry(cartEntryKey);
	}

	@Override
	public void setUiStatusForCartEntry(final String cartEntryKey, final Object uiStatus)
	{
		sessionAccessService.setUiStatusForCartEntry(cartEntryKey, uiStatus);

	}

	@Override
	public void setUiStatusForProduct(final String productKey, final Object uiStatus)
	{
		sessionAccessService.setUiStatusForProduct(productKey, uiStatus);

	}

	@Override
	public <T> T getUiStatusForProduct(final String productKey)
	{
		return sessionAccessService.getUiStatusForProduct(productKey);
	}

	@Override
	public void removeUiStatusForCartEntry(final String cartEntryKey)
	{
		sessionAccessService.removeUiStatusForCartEntry(cartEntryKey);
	}

	@Override
	public void removeUiStatusForProduct(final String productKey)
	{
		sessionAccessService.removeUiStatusForProduct(productKey);
	}


	/**
	 * @param sessionAccessService
	 *           injects the underlying session access service
	 */
	@Required
	public void setSessionAccessService(final SessionAccessService sessionAccessService)
	{
		this.sessionAccessService = sessionAccessService;
	}


	protected SessionAccessService getSessionAccessService()
	{
		return this.sessionAccessService;
	}

	protected ConfigurationModelCacheStrategy getConfigModelCacheStrategy()
	{
		return configModelCacheStrategy;
	}

	@Required
	public void setConfigModelCacheStrategy(final ConfigurationModelCacheStrategy configModelCacheStrategy)
	{
		this.configModelCacheStrategy = configModelCacheStrategy;
	}
}
