/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.integrationservices.util;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

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
	public String getApplicationName()
	{
		return "ECP Test";
	}

	@Override
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
	public boolean containsLocalBean(final String s)
	{
		return containsBean(s);
	}

	@Override
	public boolean containsBeanDefinition(final String s)
	{
		return containsBean(s);
	}

	@Override
	public int getBeanDefinitionCount()
	{
		return beanRegistry.size();
	}

	@Override
	public String[] getBeanDefinitionNames()
	{
		return beanRegistry.keySet().toArray(new String[0]);
	}

	@Override
	public <T> ObjectProvider<T> getBeanProvider(final Class<T> aClass, final boolean b)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public <T> ObjectProvider<T> getBeanProvider(final ResolvableType resolvableType, final boolean b)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public String[] getBeanNamesForType(final ResolvableType resolvableType)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public String[] getBeanNamesForType(final ResolvableType resolvableType, final boolean b, final boolean b1)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public String[] getBeanNamesForType(final Class<?> aClass)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public String[] getBeanNamesForType(final Class<?> aClass, final boolean b, final boolean b1)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public <T> Map<String, T> getBeansOfType(final Class<T> aClass) throws BeansException
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public <T> Map<String, T> getBeansOfType(final Class<T> aClass, final boolean b, final boolean b1) throws BeansException
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public String[] getBeanNamesForAnnotation(final Class<? extends Annotation> aClass)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public Map<String, Object> getBeansWithAnnotation(final Class<? extends Annotation> aClass) throws BeansException
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public <A extends Annotation> A findAnnotationOnBean(final String s, final Class<A> aClass)
			throws NoSuchBeanDefinitionException
	{
		throw new UnsupportedOperationException();
	}

	public <A extends Annotation> A findAnnotationOnBean(final String s, final Class<A> aClass, final boolean b)
			throws NoSuchBeanDefinitionException
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public Object getBean(final String s) throws BeansException
	{
		final var bean = beanRegistry.get(s);
		return validateBean(s, bean);
	}

	@Override
	public <T> T getBean(final String s, final Class<T> aClass) throws BeansException
	{
		final var bean = getBean(s);
		if (aClass.isInstance(bean))
		{
			return aClass.cast(bean);
		}
		throw new BeanNotOfRequiredTypeException(s, aClass, bean.getClass());
	}

	@Override
	public Object getBean(final String s, final Object... objects) throws BeansException
	{
		throw new UnsupportedOperationException();
	}

	@Override
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
	public <T> T getBean(final Class<T> aClass, final Object... objects) throws BeansException
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
	public <T> ObjectProvider<T> getBeanProvider(final Class<T> aClass)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public <T> ObjectProvider<T> getBeanProvider(final ResolvableType resolvableType)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean containsBean(final String s)
	{
		return beanRegistry.containsKey(s);
	}

	@Override
	public boolean isSingleton(final String s) throws NoSuchBeanDefinitionException
	{
		return true;
	}

	@Override
	public boolean isPrototype(final String s) throws NoSuchBeanDefinitionException
	{
		return false;
	}

	@Override
	public boolean isTypeMatch(final String s, final ResolvableType resolvableType) throws NoSuchBeanDefinitionException
	{
		final var bean = getBean(s);
		return resolvableType.isInstance(bean);
	}

	@Override
	public boolean isTypeMatch(final String s, final Class<?> aClass) throws NoSuchBeanDefinitionException
	{
		final var bean = getBean(s);
		return aClass.isInstance(bean);
	}

	@Override
	public Class<?> getType(final String s) throws NoSuchBeanDefinitionException
	{
		return getBean(s).getClass();
	}

	@Override
	public Class<?> getType(final String s, final boolean b) throws NoSuchBeanDefinitionException
	{
		return getType(s);
	}

	@Override
	public String[] getAliases(final String s)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public void publishEvent(final Object o)
	{
		// do nothing
	}

	@Override
	public String getMessage(final String s, final Object[] objects, final String s1, final Locale locale)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public String getMessage(final String s, final Object[] objects, final Locale locale) throws NoSuchMessageException
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public String getMessage(final MessageSourceResolvable messageSourceResolvable, final Locale locale)
			throws NoSuchMessageException
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public Environment getEnvironment()
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public Resource[] getResources(final String s) throws IOException
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public Resource getResource(final String s)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public ClassLoader getClassLoader()
	{
		return getClassLoader();
	}
}
