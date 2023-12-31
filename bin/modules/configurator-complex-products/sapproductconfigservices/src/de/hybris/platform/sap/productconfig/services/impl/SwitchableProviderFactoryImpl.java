/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.services.impl;

import de.hybris.platform.core.Registry;
import de.hybris.platform.sap.productconfig.runtime.interf.AnalyticsProvider;
import de.hybris.platform.sap.productconfig.runtime.interf.ConfigurationProvider;
import de.hybris.platform.sap.productconfig.runtime.interf.PricingConfigurationParameter;
import de.hybris.platform.sap.productconfig.runtime.interf.PricingProvider;
import de.hybris.platform.sap.productconfig.runtime.interf.ProductCsticAndValueParameterProvider;
import de.hybris.platform.sap.productconfig.runtime.interf.ProviderFactory;
import de.hybris.platform.sap.productconfig.runtime.interf.SwitchableProviderFactory;
import de.hybris.platform.servicelayer.internal.service.ServicelayerUtils;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.util.Config;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.context.ApplicationContext;

import com.google.common.collect.ImmutableList;


/**
 * Default implementation for {@link SwitchableProviderFactory}
 */
public class SwitchableProviderFactoryImpl implements SwitchableProviderFactory
{

	private static final String MSG_SPECIFY_PROVIDER_FACTORY = "%s, activating last registered provider factory instead. Please specify a registered provider factory via property '%s', to avoid to depent on extension order.";

	private static final String PROP_KEY_PROVIDER_FACTORY_BEAN_NAME = "sapproductconfig.providerFactoryBeanName";

	protected static final String SESSION_ATTR_ACTIVE_PROVIDER_NAME = "sapProductConfigActiveProviderName";

	private static final Logger LOG = Logger.getLogger(SwitchableProviderFactoryImpl.class);

	private static final String JUNIT = "junit";
	private List<String> registeredProviderFactories;
	private Map<String, ProviderFactory> providerFactoryInstances = new ConcurrentHashMap<>();
	private String activeProviderFactoryBeanName;
	private ApplicationContext applicationContext;

	private SessionService sessionService;

	protected ProviderFactory getActiveProviderFactory()
	{
		String providerFactoryBeanNameInSession = getSessionService().getAttribute(SESSION_ATTR_ACTIVE_PROVIDER_NAME);
		if (providerFactoryBeanNameInSession != null && !registeredProviderFactories.contains(providerFactoryBeanNameInSession))
		{
			LOG.warn(String.format("discarding unknown provider factory '%s' in session and loading default instead",
					providerFactoryBeanNameInSession));
			providerFactoryBeanNameInSession = null;
		}
		if (null == providerFactoryBeanNameInSession)
		{
			if (getActiveProviderFactoryBeanName() == null)
			{
				setDefaultProviderFactoryActive();
			}
			providerFactoryBeanNameInSession = getActiveProviderFactoryBeanName();
			getSessionService().setAttribute(SESSION_ATTR_ACTIVE_PROVIDER_NAME, providerFactoryBeanNameInSession);
		}

		return getProviderFactoryInstance(providerFactoryBeanNameInSession);
	}

	protected void setDefaultProviderFactoryActive()
	{

		LOG.info(String.format("Currently registered provider factories: %s", getRegisteredProviderFactories().toString()));

		String defaultProviderFactoyBeanName = getFactoryBeanNameConfiguredViaProperty();
		if (null == defaultProviderFactoyBeanName)
		{
			final String reason = String.format("Property '%s' not set", PROP_KEY_PROVIDER_FACTORY_BEAN_NAME);
			LOG.warn(String.format(MSG_SPECIFY_PROVIDER_FACTORY, reason, PROP_KEY_PROVIDER_FACTORY_BEAN_NAME));
			defaultProviderFactoyBeanName = findDefaultProviderFactory();
		}
		else if (!getRegisteredProviderFactories().contains(defaultProviderFactoyBeanName))
		{
			final String reason = String.format("Configured provider factory '%s' is not registered", defaultProviderFactoyBeanName);
			LOG.warn(String.format(MSG_SPECIFY_PROVIDER_FACTORY, reason, PROP_KEY_PROVIDER_FACTORY_BEAN_NAME));
			defaultProviderFactoyBeanName = findDefaultProviderFactory();
		}

		switchProviderFactory(defaultProviderFactoyBeanName);
	}

	protected String getFactoryBeanNameConfiguredViaProperty()
	{
		// guard for unit test, do not start tennat
		final String defaultProviderFactoyBeanName = Registry.hasCurrentTenant()
				? Config.getParameter(PROP_KEY_PROVIDER_FACTORY_BEAN_NAME)
				: null;
		return defaultProviderFactoyBeanName;
	}

	protected String findDefaultProviderFactory()
	{
		final int indexOfLastRegisteredProviderFactory = getRegisteredProviderFactories().size() - 1;
		return getRegisteredProviderFactories().get(indexOfLastRegisteredProviderFactory);
	}

	@Override
	public ConfigurationProvider getConfigurationProvider()
	{
		return getActiveProviderFactory().getConfigurationProvider();
	}

	@Override
	public PricingProvider getPricingProvider()
	{

		return getActiveProviderFactory().getPricingProvider();
	}

	@Override
	public AnalyticsProvider getAnalyticsProvider()
	{
		return getActiveProviderFactory().getAnalyticsProvider();
	}

	@Override
	public ProductCsticAndValueParameterProvider getProductCsticAndValueParameterProvider()
	{
		return getActiveProviderFactory().getProductCsticAndValueParameterProvider();
	}

	@Override
	public PricingConfigurationParameter getPricingParameter()
	{
		return getActiveProviderFactory().getPricingParameter();
	}


	@Override
	public void switchProviderFactory(final String providerFactoryBeanName)
	{
		if (!isProviderFactorySwitchAllowed())
		{
			throw new IllegalArgumentException("Provider Switch is not allowed!");
		}
		if (null == getRegisteredProviderFactories() || getRegisteredProviderFactories().isEmpty())
		{
			throw new IllegalArgumentException("No provider factory was regsitered at all");
		}
		if (!getRegisteredProviderFactories().contains(providerFactoryBeanName))
		{
			throw new IllegalArgumentException("Tried to switch to unregistered provider factory: " + providerFactoryBeanName);
		}
		setActiveProviderFactoryBeanName(providerFactoryBeanName);
		reset();
		getProviderFactoryInstance(providerFactoryBeanName);

	}

	public ProviderFactory getProviderFactoryInstance(final String providerFactoryBeanName)
	{
		if (!getProviderFactoryInstances().containsKey(providerFactoryBeanName))
		{
			final ProviderFactory bean = (ProviderFactory) getApplicationContext().getBean(providerFactoryBeanName);
			LOG.info(String.format("ProviderFactoryBeanName '%s' was resolved to class '%s'", providerFactoryBeanName,
					bean.getClass().getName()));
			getProviderFactoryInstances().put(providerFactoryBeanName, bean);
		}
		return getProviderFactoryInstances().get(providerFactoryBeanName);
	}

	/**
	 * resets the current session provider, so enable provider switch in session
	 */
	protected void reset()
	{
		this.getSessionService().removeAttribute(SwitchableProviderFactoryImpl.SESSION_ATTR_ACTIVE_PROVIDER_NAME);
	}


	@Required
	public void setRegisteredProviderFactories(final List<String> registeredProviderFactories)
	{
		this.registeredProviderFactories = null;
		if (registeredProviderFactories != null)
		{
			this.registeredProviderFactories = ImmutableList.copyOf(registeredProviderFactories);
		}
	}

	@Override
	public boolean isProviderFactoryAvailable(final String providerFactoryBeanName)
	{
		return getRegisteredProviderFactories().contains(providerFactoryBeanName);
	}

	protected List<String> getRegisteredProviderFactories()
	{
		return registeredProviderFactories;
	}

	protected Map<String, ProviderFactory> getProviderFactoryInstances()
	{
		return providerFactoryInstances;
	}

	/**
	 * used by unit test
	 */
	void setProviderFactoryInstances(final Map<String, ProviderFactory> providerFactoryInstances)
	{
		this.providerFactoryInstances = providerFactoryInstances;
	}

	protected String getActiveProviderFactoryBeanName()
	{
		return activeProviderFactoryBeanName;
	}

	protected void setActiveProviderFactoryBeanName(final String activeProviderFactoryBeanName)
	{
		this.activeProviderFactoryBeanName = activeProviderFactoryBeanName;
		LOG.info(String.format("Activating ProviderFactory: '%s'", activeProviderFactoryBeanName));
	}


	protected ApplicationContext getApplicationContext()
	{
		if (applicationContext == null)
		{
			applicationContext = ServicelayerUtils.getApplicationContext();
		}
		return applicationContext;
	}


	public void setApplicationContext(final ApplicationContext applicationContext)
	{
		this.applicationContext = applicationContext;
	}


	protected boolean isProviderFactorySwitchAllowed()
	{
		if (null != getActiveProviderFactoryBeanName() && Registry.hasCurrentTenant())
		{
			return JUNIT.equals(Registry.getCurrentTenant().getTenantID());
		}
		return true;
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
