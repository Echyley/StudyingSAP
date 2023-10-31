/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.dataimportcommons;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.annotation.Nonnull;

import org.junit.rules.ExternalResource;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanNotOfRequiredTypeException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.context.NoSuchMessageException;
import org.springframework.core.ResolvableType;
import org.springframework.core.env.Environment;
import org.springframework.core.io.Resource;

/**
 * A JUnit test rule for stubbing Spring beans in the {@link ApplicationContext}. After the test(s) execution the rule
 * automatically clears all previously stubbed beans in the application context.
 */
public final class TestApplicationContext extends ExternalResource implements ApplicationContext
{
	private final Map<String, Object> beanRegistry = new HashMap<>();

	/**
	 * Adds a bean definition into the application context of a test case.
	 *
	 * @param id   an id the bean should have in the context
	 * @param bean a Spring bean object
	 */
	public void addBean(final String id, final Object bean)
	{
		beanRegistry.put(id, bean);
	}

	@Override
	protected void before() throws Throwable
	{
		ApplicationBeans.setApplicationContext(this);
	}

	@Override
	protected void after()
	{
		ApplicationBeans.setApplicationContext(null);
		beanRegistry.clear();
	}

	@Override
	public String getId()
	{
		return getClass().getName();
	}

	@Override
	@Nonnull
	public String getApplicationName()
	{
		return "ECP Test";
	}

	@Override
	@Nonnull
	public String getDisplayName()
	{
		return getClass().getSimpleName();
	}

	@Override
	public long getStartupDate()
	{
		return 0;
	}

	@Override
	public ApplicationContext getParent()
	{
		return null;
	}

	@Override
	@Nonnull
	public AutowireCapableBeanFactory getAutowireCapableBeanFactory() throws IllegalStateException
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public BeanFactory getParentBeanFactory()
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean containsLocalBean(@Nonnull final String s)
	{
		return containsBean(s);
	}

	@Override
	public boolean containsBeanDefinition(@Nonnull final String s)
	{
		return containsBean(s);
	}

	@Override
	public int getBeanDefinitionCount()
	{
		return beanRegistry.size();
	}

	@Override
	@Nonnull
	public String[] getBeanDefinitionNames()
	{
		return beanRegistry.keySet().toArray(new String[0]);
	}

	@Override
	@Nonnull
	public <T> ObjectProvider<T> getBeanProvider(@Nonnull final Class<T> aClass, final boolean b)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	@Nonnull
	public <T> ObjectProvider<T> getBeanProvider(@Nonnull final ResolvableType resolvableType, final boolean b)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	@Nonnull
	public String[] getBeanNamesForType(@Nonnull final ResolvableType resolvableType)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	@Nonnull
	public String[] getBeanNamesForType(@Nonnull final ResolvableType resolvableType, final boolean b, final boolean b1)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	@Nonnull
	public String[] getBeanNamesForType(final Class<?> aClass)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	@Nonnull
	public String[] getBeanNamesForType(final Class<?> aClass, final boolean b, final boolean b1)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	@Nonnull
	public <T> Map<String, T> getBeansOfType(final Class<T> aClass) throws BeansException
	{
		throw new UnsupportedOperationException();
	}

	@Override
	@Nonnull
	public <T> Map<String, T> getBeansOfType(final Class<T> aClass, final boolean b, final boolean b1) throws BeansException
	{
		throw new UnsupportedOperationException();
	}

	@Override
	@Nonnull
	public String[] getBeanNamesForAnnotation(@Nonnull final Class<? extends Annotation> aClass)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	@Nonnull
	public Map<String, Object> getBeansWithAnnotation(@Nonnull final Class<? extends Annotation> aClass) throws BeansException
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public <A extends Annotation> A findAnnotationOnBean(@Nonnull final String s, @Nonnull final Class<A> aClass)
			throws NoSuchBeanDefinitionException
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public <A extends Annotation> A findAnnotationOnBean(@Nonnull final String s, @Nonnull final Class<A> aClass, final boolean b)
			throws NoSuchBeanDefinitionException
	{
		throw new UnsupportedOperationException();
	}

	@Override
	@Nonnull
	public Object getBean(@Nonnull final String s) throws BeansException
	{
		final var bean = beanRegistry.get(s);
		return validateBean(s, bean);
	}

	@Override
	@Nonnull
	public <T> T getBean(@Nonnull final String s, final Class<T> aClass) throws BeansException
	{
		final var bean = getBean(s);
		if (aClass.isInstance(bean))
		{
			return aClass.cast(bean);
		}
		throw new BeanNotOfRequiredTypeException(s, aClass, bean.getClass());
	}

	@Override
	@Nonnull
	public Object getBean(@Nonnull final String s, @Nonnull final Object... objects) throws BeansException
	{
		throw new UnsupportedOperationException();
	}

	@Override
	@Nonnull
	public <T> T getBean(final Class<T> aClass) throws BeansException
	{
		final var bean = beanRegistry.values().stream()
		                             .filter(aClass::isInstance)
		                             .map(aClass::cast)
		                             .findAny()
		                             .orElse(null);
		return validateBean(aClass.getName(), bean);
	}

	@Override
	@Nonnull
	public <T> T getBean(@Nonnull final Class<T> aClass, @Nonnull final Object... objects) throws BeansException
	{
		throw new UnsupportedOperationException();
	}

	private <T> T validateBean(final String id, final T bean)
	{
		if (bean == null)
		{
			throw new NoSuchBeanDefinitionException("Bean \"" + id + "\" not found");
		}
		return bean;
	}

	@Override
	@Nonnull
	public <T> ObjectProvider<T> getBeanProvider(@Nonnull final Class<T> aClass)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	@Nonnull
	public <T> ObjectProvider<T> getBeanProvider(@Nonnull final ResolvableType resolvableType)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean containsBean(@Nonnull final String s)
	{
		return beanRegistry.containsKey(s);
	}

	@Override
	public boolean isSingleton(@Nonnull final String s) throws NoSuchBeanDefinitionException
	{
		return true;
	}

	@Override
	public boolean isPrototype(@Nonnull final String s) throws NoSuchBeanDefinitionException
	{
		return false;
	}

	@Override
	public boolean isTypeMatch(@Nonnull final String s, final ResolvableType resolvableType) throws NoSuchBeanDefinitionException
	{
		final var bean = getBean(s);
		return resolvableType.isInstance(bean);
	}

	@Override
	public boolean isTypeMatch(@Nonnull final String s, final Class<?> aClass) throws NoSuchBeanDefinitionException
	{
		final var bean = getBean(s);
		return aClass.isInstance(bean);
	}

	@Override
	public Class<?> getType(@Nonnull final String s) throws NoSuchBeanDefinitionException
	{
		return getBean(s).getClass();
	}

	@Override
	public Class<?> getType(@Nonnull final String s, final boolean b) throws NoSuchBeanDefinitionException
	{
		return getType(s);
	}

	@Override
	@Nonnull
	public String[] getAliases(@Nonnull final String s)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public void publishEvent(@Nonnull final Object o)
	{
		// do nothing
	}

	@Override
	public String getMessage(
			@Nonnull final String s,
			final Object[] objects,
			final String s1,
			@Nonnull final Locale locale)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	@Nonnull
	public String getMessage(@Nonnull final String s, final Object[] objects, @Nonnull final Locale locale)
			throws NoSuchMessageException
	{
		throw new UnsupportedOperationException();
	}

	@Override
	@Nonnull
	public String getMessage(@Nonnull final MessageSourceResolvable messageSourceResolvable, @Nonnull final Locale locale)
			throws NoSuchMessageException
	{
		throw new UnsupportedOperationException();
	}

	@Override
	@Nonnull
	public Environment getEnvironment()
	{
		throw new UnsupportedOperationException();
	}

	@Override
	@Nonnull
	public Resource[] getResources(@Nonnull final String s)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	@Nonnull
	public Resource getResource(@Nonnull final String s)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	@Nonnull
	public ClassLoader getClassLoader()
	{
		return getClass().getClassLoader();
	}
}
