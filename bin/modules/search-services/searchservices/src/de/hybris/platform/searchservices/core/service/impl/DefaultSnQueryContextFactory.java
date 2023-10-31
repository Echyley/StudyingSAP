/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.searchservices.core.service.impl;

import de.hybris.platform.searchservices.core.service.SnQueryContextFactory;
import de.hybris.platform.searchservices.core.service.SnQueryContextProvider;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;


/**
 * Default implementation of {@link SnQueryContextFactory}.
 */
public class DefaultSnQueryContextFactory implements SnQueryContextFactory, ApplicationContextAware, InitializingBean
{
	private ApplicationContext applicationContext;
	private List<SnQueryContextProvider> queryContextProviders;

	@Override
	public void afterPropertiesSet()
	{
		loadQueryContextProviders();
	}

	protected void loadQueryContextProviders()
	{
		final Map<String, DefaultSnQueryContextProviderDefinition> beans = BeanFactoryUtils
				.beansOfTypeIncludingAncestors(applicationContext, DefaultSnQueryContextProviderDefinition.class);
		queryContextProviders = beans.values().stream().sorted()
				.map(DefaultSnQueryContextProviderDefinition::getQueryContextProvider).collect(Collectors.toUnmodifiableList());
	}

	protected List<SnQueryContextProvider> getQueryContextProviders()
	{
		return queryContextProviders;
	}

	@Override
	public List<String> getQueryContexts()
	{
		final Set<String> collectedQueryContexts = new LinkedHashSet<>();

		for (final SnQueryContextProvider queryContextProvider : queryContextProviders)
		{
			final List<String> queryContexts = queryContextProvider.getQueryContexts();
			if (CollectionUtils.isNotEmpty(queryContexts))
			{
				collectedQueryContexts.addAll(queryContexts);
			}
		}

		return List.copyOf(collectedQueryContexts);
	}

	protected ApplicationContext getApplicationContext()
	{
		return applicationContext;
	}

	@Override
	public void setApplicationContext(final ApplicationContext applicationContext)
	{
		this.applicationContext = applicationContext;
	}
}
