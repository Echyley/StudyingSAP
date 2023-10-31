/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.services.ssc.strategies.lifecycle.impl;

import de.hybris.platform.sap.productconfig.services.SessionAccessService;
import de.hybris.platform.sap.productconfig.services.strategies.lifecycle.impl.SessionServiceAware;
import de.hybris.platform.sap.productconfig.services.strategies.lifecycle.intf.ConfigurationProductLinkStrategy;


/**
 * Default implementation of the {@link ConfigurationProductLinkStrategy}. It uses the hybris session to store any data
 * and hence delegates to the {@link SessionAccessService}.
 */
public class DefaultProductLinkStrategyImpl extends SessionServiceAware implements ConfigurationProductLinkStrategy
{

	@Override
	public String getConfigIdForProduct(final String productCode)
	{
		return getSessionAccessService().getConfigIdForProduct(productCode);
	}

	@Override
	public void setConfigIdForProduct(final String productCode, final String configId)
	{
		getSessionAccessService().setConfigIdForProduct(productCode, configId);
	}

	@Override
	public void removeConfigIdForProduct(final String productCode)
	{
		getSessionAccessService().removeConfigIdForProduct(productCode);
	}

	@Override
	public String retrieveProductCode(final String configId)
	{
		return getSessionAccessService().getProductForConfigId(configId);
	}


}
