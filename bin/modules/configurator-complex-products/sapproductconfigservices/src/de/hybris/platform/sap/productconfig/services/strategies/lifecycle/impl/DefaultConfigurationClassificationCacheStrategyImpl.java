/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.services.strategies.lifecycle.impl;

import de.hybris.platform.sap.productconfig.runtime.interf.model.ConfigModel;
import de.hybris.platform.sap.productconfig.runtime.interf.services.impl.ClassificationSystemCPQAttributesContainer;
import de.hybris.platform.sap.productconfig.services.SessionAccessService;
import de.hybris.platform.sap.productconfig.services.cache.ProductConfigurationCacheAccessService;
import de.hybris.platform.sap.productconfig.services.strategies.lifecycle.intf.ConfigurationClassificationCacheStrategy;

import java.util.Map;

import org.springframework.beans.factory.annotation.Required;


/**
 * Default implementation of the {@link ConfigurationClassificationCacheStrategy}. It uses the hybris session to store
 * any data and hence delegates to the {@link SessionAccessService}.
 */
public class DefaultConfigurationClassificationCacheStrategyImpl implements ConfigurationClassificationCacheStrategy
{

	private ProductConfigurationCacheAccessService productConfigurationCacheAccessService;

	public Map<String, ClassificationSystemCPQAttributesContainer> getCachedNameMap(final ConfigModel config)
	{
		final String productCode = getProductCode(config);
		return getProductConfigurationCacheAccessService().getCachedNameMap(productCode);
	}

	protected String getProductCode(final ConfigModel config)
	{
		if (null != config.getKbKey() && null != config.getKbKey().getProductCode())
		{
			return config.getKbKey().getProductCode();
		}
		return config.getRootInstance().getName();
	}

	@Override
	public Map<String, ClassificationSystemCPQAttributesContainer> getCachedNameMap(final String productCode)
	{
		return getProductConfigurationCacheAccessService().getCachedNameMap(productCode);
	}

	protected ProductConfigurationCacheAccessService getProductConfigurationCacheAccessService()
	{
		return productConfigurationCacheAccessService;
	}

	@Required
	public void setProductConfigurationCacheAccessService(
			final ProductConfigurationCacheAccessService productConfigurationCacheAccessService)
	{
		this.productConfigurationCacheAccessService = productConfigurationCacheAccessService;
	}

}
