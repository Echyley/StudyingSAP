/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.acceleratorservices.velocity.core;

import de.hybris.platform.acceleratorservices.velocity.eval.PropertyEvaluatorFactory;
import de.hybris.platform.acceleratorservices.velocity.locale.LocaleResolver;
import de.hybris.platform.acceleratorservices.velocity.resource.url.ScriptUrlContext;

import java.util.Map;

import javax.annotation.Resource;

import org.apache.velocity.VelocityContext;


public class DefaultVelocityContextConfigurer implements CoreVelocityConfigurer<VelocityContext, ScriptUrlContext>
{
	//make it configurable
	private static final String LANG_VARIABLE = "lang";
	private static final String QUERY_VARIABLE = "query";

	private Map properties;
	private PropertyEvaluatorFactory propertyEvaluatorFactory;
	private LocaleResolver localeResolver;

	@Resource
	public void setProperties(final Map properties)
	{
		this.properties = properties;
	}

	@Override
	public VelocityContext configure(final ScriptUrlContext urlContext)
	{
		properties.put(QUERY_VARIABLE, propertyEvaluatorFactory.create(urlContext));
		properties.put(LANG_VARIABLE, localeResolver.getLanguageCode(urlContext.getLocale()));
		return new VelocityContext(properties);
	}

	@Override
	public VelocityContext configure()
	{
		throw new UnsupportedOperationException();
	}

	@Resource
	public void setPropertyEvaluatorFactory(final PropertyEvaluatorFactory propertyEvaluatorFactory)
	{
		this.propertyEvaluatorFactory = propertyEvaluatorFactory;
	}

	@Resource
	public void setLocaleResolver(final LocaleResolver localeResolverInstance)
	{
		this.localeResolver = localeResolverInstance;
	}
}
