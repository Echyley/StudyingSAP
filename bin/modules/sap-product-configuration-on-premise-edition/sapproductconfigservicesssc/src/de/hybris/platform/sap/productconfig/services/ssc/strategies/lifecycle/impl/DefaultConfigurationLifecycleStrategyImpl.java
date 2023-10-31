/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.services.ssc.strategies.lifecycle.impl;

import de.hybris.platform.sap.productconfig.runtime.interf.ConfigurationEngineException;
import de.hybris.platform.sap.productconfig.runtime.interf.ConfigurationProvider;
import de.hybris.platform.sap.productconfig.runtime.interf.KBKey;
import de.hybris.platform.sap.productconfig.runtime.interf.ProviderFactory;
import de.hybris.platform.sap.productconfig.runtime.interf.external.Configuration;
import de.hybris.platform.sap.productconfig.runtime.interf.impl.ConfigurationRetrievalOptions;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ConfigModel;
import de.hybris.platform.sap.productconfig.runtime.interf.services.ProductConfigSessionAttributeContainer;
import de.hybris.platform.sap.productconfig.services.SessionAccessService;
import de.hybris.platform.sap.productconfig.services.cache.ProductConfigurationCacheAccessService;
import de.hybris.platform.sap.productconfig.services.strategies.lifecycle.impl.SessionServiceAware;
import de.hybris.platform.sap.productconfig.services.strategies.lifecycle.intf.ConfigurationLifecycleStrategy;
import de.hybris.platform.servicelayer.session.SessionService;

import java.util.Collection;
import java.util.HashSet;

import org.springframework.beans.factory.annotation.Required;


/**
 * Default implementation of the {@link ConfigurationLifecycleStrategy}. It uses the hybris session to store any data
 * and hence delegates to the {@link SessionAccessService}.
 */
public class DefaultConfigurationLifecycleStrategyImpl extends SessionServiceAware implements ConfigurationLifecycleStrategy
{

	private ProviderFactory providerFactory;

	private ProductConfigurationCacheAccessService productConfigurationCacheAccessService;

	private SessionService sessionService;


	@Override
	public ConfigModel createDefaultConfiguration(final KBKey kbKey)
	{
		return getConfigurationProvider().createDefaultConfiguration(kbKey);
	}

	@Override
	public boolean updateConfiguration(final ConfigModel model) throws ConfigurationEngineException
	{
		return getConfigurationProvider().updateConfiguration(model);
	}

	@Override
	public void updateUserLinkToConfiguration(final String userSessionId)
	{
		// empty - not needed
	}

	@Override
	public ConfigModel retrieveConfigurationModel(final String configId) throws ConfigurationEngineException
	{
		return getConfigurationProvider().retrieveConfigurationModel(configId);
	}

	@Override
	public ConfigModel retrieveConfigurationModel(final String configId, final ConfigurationRetrievalOptions options)
			throws ConfigurationEngineException
	{
		return getConfigurationProvider().retrieveConfigurationModel(configId, options);
	}

	@Override
	public String retrieveExternalConfiguration(final String configId) throws ConfigurationEngineException
	{
		return getConfigurationProvider().retrieveExternalConfiguration(configId);
	}

	@Override
	public ConfigModel createConfigurationFromExternalSource(final Configuration extConfig)
	{
		return getConfigurationProvider().createConfigurationFromExternalSource(extConfig);
	}

	@Override
	public ConfigModel createConfigurationFromExternalSource(final KBKey kbKey, final String extConfig)
	{
		return getConfigurationProvider().createConfigurationFromExternalSource(kbKey, extConfig);
	}

	@Override
	public void releaseSession(final String configId)
	{
		getConfigurationProvider().releaseSession(configId);
	}

	@Override
	public void releaseExpiredSessions(final String sessionId)
	{
		final ProductConfigSessionAttributeContainer container = getSessionService()
				.getAttribute(SessionAccessService.PRODUCT_CONFIG_SESSION_ATTRIBUTE_CONTAINER);

		if (container != null)
		{
			final Collection<String> configIdsLinkedToProducts = container.getProductConfigurations().values();
			final Collection<String> configIdsLinkedToCart = container.getCartEntryConfigurations().values();
			final Collection<String> configIdsLinkedToCartAsDraft = container.getCartEntryDraftConfigurations().values();
			final HashSet<String> allConfigIds = new HashSet<>(configIdsLinkedToProducts);
			allConfigIds.addAll(configIdsLinkedToCart);
			allConfigIds.addAll(configIdsLinkedToCartAsDraft);

			for (final String configId : allConfigIds)
			{
				releaseSession(configId);
				getProductConfigurationCacheAccessService().removeConfigAttributeState(configId);
			}
		}

	}

	@Override
	public ConfigModel retrieveConfigurationFromVariant(final String baseProductCode, final String variantProductCode)
	{
		return getConfigurationProvider().retrieveConfigurationFromVariant(baseProductCode, variantProductCode);
	}

	@Override
	public boolean isConfigForCurrentUser(final String configId)
	{
		// any config in a different session cannot be accessed and the process will fail during update/release
		return true;
	}

	protected ConfigurationProvider getConfigurationProvider()
	{
		return getProviderFactory().getConfigurationProvider();
	}

	protected ProviderFactory getProviderFactory()
	{
		return providerFactory;
	}

	@Required
	public void setProviderFactory(final ProviderFactory providerFactory)
	{
		this.providerFactory = providerFactory;
	}

	@Override
	public boolean isConfigKnown(final String configId)
	{
		// can only handle/access of own session by design
		return true;
	}

	protected ProductConfigurationCacheAccessService getProductConfigurationCacheAccessService()
	{
		return productConfigurationCacheAccessService;
	}

	public void setProductConfigurationCacheAccessService(
			final ProductConfigurationCacheAccessService productConfigurationCacheAccessService)
	{
		this.productConfigurationCacheAccessService = productConfigurationCacheAccessService;
	}

	protected SessionService getSessionService()
	{
		return sessionService;
	}

	public void setSessionService(final SessionService sessionService)
	{
		this.sessionService = sessionService;
	}
}
