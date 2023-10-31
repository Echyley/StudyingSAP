/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.runtime.ssc.impl;

import de.hybris.platform.sap.productconfig.runtime.interf.ConfigurationProvider;
import de.hybris.platform.sap.productconfig.runtime.ssc.SSCSessionAccessService;
import de.hybris.platform.servicelayer.session.SessionService;

import org.springframework.beans.factory.annotation.Required;


public class SSCSessionAccessServiceImpl implements SSCSessionAccessService
{
	protected static final String SSC_PRODUCT_CONFIG_SESSION_ATTRIBUTE_CONTAINER = "sscPproductconfigSessionAttributeContainer";

	private SessionService sessionService;

	@Override
	public ConfigurationProvider getConfigurationProvider()
	{
		ConfigurationProvider provider = null;
		final SSCProductConfigSessionAttributeContainer attributeContainer = retrieveSessionAttributeContainer(false);
		if (attributeContainer != null)
		{
			provider = attributeContainer.getConfigurationProvider();
		}
		return provider;
	}

	@Override
	public void setConfigurationProvider(final ConfigurationProvider provider)
	{
		retrieveSessionAttributeContainer(true).setConfigurationProvider(provider);
	}

	protected SSCProductConfigSessionAttributeContainer retrieveSessionAttributeContainer(final boolean createIfNotExist)
	{
		synchronized (getSessionService().getCurrentSession())
		{
			SSCProductConfigSessionAttributeContainer attributeContainer = getSessionService()
					.getAttribute(SSC_PRODUCT_CONFIG_SESSION_ATTRIBUTE_CONTAINER);
			if (attributeContainer == null && createIfNotExist)
			{
				attributeContainer = new SSCProductConfigSessionAttributeContainer();
				getSessionService().setAttribute(SSC_PRODUCT_CONFIG_SESSION_ATTRIBUTE_CONTAINER, attributeContainer);
			}
			return attributeContainer;
		}
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
