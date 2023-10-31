/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.inboundservices.persistence.hook.impl;

import de.hybris.platform.inboundservices.persistence.PersistenceContext;
import de.hybris.platform.inboundservices.persistence.hook.PersistenceHookProvider;
import de.hybris.platform.inboundservices.persistence.hook.PostPersistHook;
import de.hybris.platform.inboundservices.persistence.hook.PrePersistHook;
import de.hybris.platform.integrationservices.scripting.CannotCreateLogicLocationException;
import de.hybris.platform.integrationservices.scripting.LogicLocation;
import de.hybris.platform.integrationservices.scripting.LogicLocationScheme;
import de.hybris.platform.integrationservices.util.Log;

import java.util.Optional;

import javax.annotation.Nonnull;
import javax.validation.constraints.NotNull;

import org.slf4j.Logger;
import org.springframework.beans.factory.BeanNotOfRequiredTypeException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import com.google.common.base.Preconditions;

/**
 * Provides persistence hooks, which are defined as Spring Beans in the application context. Spring Bean hooks should be prefixed
 * with "bean://" in the request headers specifying the hook name; or they may use no prefix. For example, if a custom deployment
 * registers a persistence hook named "myTaxCalculator" in the spring.xml. Then the bean should be referred as
 * "bean://myTaxCalculator" or simply "myTaxCalculator" in the request headers.
 */
public class SpringBeanPersistenceHookProvider implements PersistenceHookProvider, ApplicationContextAware
{
	private static final Logger LOG = Log.getLogger(SpringBeanPersistenceHookProvider.class);
	private static final LogicLocationScheme SUPPORTED_SCHEMA = LogicLocationScheme.BEAN;

	private ApplicationContext applicationContext;

	@Override
	public Optional<PrePersistHook> getPrePersistHook(@NotNull final PersistenceContext context)
	{
		Preconditions.checkArgument(context != null, "PersistenceContext must not be null");
		return getPersistHook(context.getPrePersistHook(), PrePersistHook.class);
	}

	@Override
	public Optional<PostPersistHook> getPostPersistHook(@NotNull final PersistenceContext context)
	{
		Preconditions.checkArgument(context != null, "PersistenceContext must not be null");
		return getPersistHook(context.getPostPersistHook(), PostPersistHook.class);
	}

	private <T> Optional<T> getPersistHook(final String hookName, final Class<T> hookType)
	{
		return extractBeanId(hookName)
				.flatMap(id -> getHookBean(id, hookType));
	}

	private Optional<String> extractBeanId(final String hookName)
	{
		try
		{
			final LogicLocation loc = LogicLocation.fromUri(hookName, SUPPORTED_SCHEMA);
			final int beanNamePos = (loc.getScheme().name() + "://").length();
			return Optional.of(loc.getLocation().substring(beanNamePos));
		}
		catch (final CannotCreateLogicLocationException e)
		{
			return Optional.empty();
		}
	}

	private <T> Optional<T> getHookBean(final String beanName, final Class<T> hookType)
	{
		try
		{
			final T hook = applicationContext.getBean(beanName, hookType);
			return Optional.of(hook);
		}
		catch (final NoSuchBeanDefinitionException e)
		{
			LOG.debug("No hook with name {} found", beanName, e);
		}
		catch (final BeanNotOfRequiredTypeException e)
		{
			LOG.debug("Could not find a hook with name {} that implements the hook {} interface", beanName, hookType.getSimpleName(), e);
		}
		catch (final RuntimeException e)
		{
			LOG.debug("Exception while retrieving hook {}", beanName, e);
		}
		return Optional.empty();
	}

	@Override
	public void setApplicationContext(@Nonnull final ApplicationContext context)
	{
		applicationContext = context;
	}
}
